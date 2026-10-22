/*
 * Copyright 2017 - 2026 Riigi Infosüsteemi Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.tsl;

import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import ee.openeid.tsl.snapshot.TSLSnapshotManifest;
import ee.openeid.tsl.snapshot.TSLSnapshotStore;
import eu.europa.esig.dss.model.tsl.LOTLInfo;
import eu.europa.esig.dss.model.tsl.ParsingInfoRecord;
import eu.europa.esig.dss.model.tsl.TLInfo;
import eu.europa.esig.dss.model.tsl.TLValidationJobSummary;
import eu.europa.esig.dss.model.tsl.TrustProperties;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.exception.DSSExternalResourceException;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundles;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.security.KeyStore;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TSLLoaderTest {

    private static final String TSL_URL = "url";
    private static final String TSL_OJ_URL = "ojUrl";
    private static final String TSL_INFO_URL = "infoUrl";

    private static final List<String> DEFAULT_TRUSTED_TERRITORIES = Arrays.asList(/*AT*/ "BE", "BG", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "GR", "HU", "HR", "IE", "IS", "IT", "LT", "LU", "LV", "LI", "MT", "NO", "NL", "PL", "PT", "RO", "SE", "SI", "SK", "UK");
    @Mock
    private TSLValidationJobFactory tslValidationJobFactory;
    @Mock
    private TLValidationJob tslValidationJob;
    @Mock
    private KeyStoreCertificateSource keyStoreCertificateSource;
    @Mock
    private ObjectProvider<SslBundles> sslBundles;
    private final TrustedListsCertificateSourceHolder holder = new TrustedListsCertificateSourceHolder();

    @TempDir
    private Path snapshotRoot;
    @TempDir
    private Path cache;

    private TSLLoader tslLoader;

    @BeforeEach
    public void setUp() throws Exception {
        lenient().when(tslValidationJobFactory.createValidationJob()).thenReturn(tslValidationJob);
    }

    private TSLLoader loader(TSLLoaderConfigurationProperties properties) {
        return new TSLLoader("testName", holder, tslValidationJobFactory, properties, keyStoreCertificateSource,
                new ProxyConfig(), sslBundles);
    }

    @Test
    void loadTSL_WhenRefreshReturns_LastRefreshAndCertificateCountAreRecorded() throws Exception {
        tslLoader = loader(createConfigurationProperties(false));
        tslLoader.init();
        filledOnRefresh(TslTestUtils.anchors(3));
        assertNull(tslLoader.getLastRefresh());
        assertEquals(0, tslLoader.getNumberOfCertificates());
        assertEquals(0, holder.current().getNumberOfCertificates());
        Instant before = Instant.now();

        tslLoader.loadTSL();

        assertNotNull(tslLoader.getLastRefresh());
        assertFalse(tslLoader.getLastRefresh().isBefore(before));
        assertEquals(3, tslLoader.getNumberOfCertificates());
        assertEquals(3, holder.current().getNumberOfCertificates());
    }

    @Test
    void loadTSL_WhenRefreshedAgain_ANewSourceIsPublishedAndThePreviousOneIsLeftAsItWas() throws Exception {
        tslLoader = loader(createConfigurationProperties(true));
        tslLoader.init();
        Map<CertificateToken, List<TrustProperties>> first = TslTestUtils.anchors(2);
        Map<CertificateToken, List<TrustProperties>> second = TslTestUtils.anchors(1);
        CertificateToken firstAnchor = first.keySet().iterator().next();
        CertificateToken secondAnchor = second.keySet().iterator().next();
        filledOnRefresh(first);
        tslLoader.loadTSL();
        TrustedListsCertificateSource published = holder.current();
        assertTrue(published.isTrusted(firstAnchor));
        filledOnRefresh(second);

        tslLoader.loadTSL();

        assertNotSame(published, holder.current());
        assertTrue(holder.current().isTrusted(secondAnchor));
        assertFalse(holder.current().isTrusted(firstAnchor));
        assertEquals(2, published.getNumberOfCertificates());
        assertTrue(published.isTrusted(firstAnchor));
        assertEquals(1, tslLoader.getNumberOfCertificates());
        ArgumentCaptor<TrustedListsCertificateSource> given =
                ArgumentCaptor.forClass(TrustedListsCertificateSource.class);
        verify(tslValidationJob, times(2)).setTrustedListCertificateSource(given.capture());
        assertNotSame(given.getAllValues().get(0), given.getAllValues().get(1));
    }

    @Test
    void loadTSL_WhenSourceHoldsAnchors_TheyAreCountedOncePerTerritoryOfTheirTrustedList() throws Exception {
        tslLoader = loader(createConfigurationProperties(false));
        tslLoader.init();
        TLInfo estonian = trustedList("EE");
        TLInfo estonianMirror = trustedList("EE");
        TLInfo latvian = trustedList("LV");
        TLInfo unparsed = new TLInfo(null, null, null, "https://tl/none");
        List<CertificateToken> tokens = List.copyOf(TslTestUtils.anchors(5).keySet());
        Map<CertificateToken, List<TrustProperties>> anchors = new LinkedHashMap<>();
        anchors.put(tokens.get(0), List.of(trust(estonian), trust(estonianMirror)));
        anchors.put(tokens.get(1), List.of(trust(estonian), trust(latvian)));
        anchors.put(tokens.get(2), List.of(trust(latvian)));
        anchors.put(tokens.get(3), List.of());
        anchors.put(tokens.get(4), List.of(trust(unparsed), trust(null)));
        filledOnRefresh(anchors);
        assertEquals(Map.of(), tslLoader.getCertificatesByTerritory());

        tslLoader.loadTSL();

        assertEquals(Map.of("EE", 2, "LV", 2), tslLoader.getCertificatesByTerritory());
        assertEquals(List.of("EE", "LV"), List.copyOf(tslLoader.getCertificatesByTerritory().keySet()));
    }

    @Test
    void loadTSL_WhenSourceIsEmpty_NoTerritoriesAreRecorded() {
        tslLoader = loader(createConfigurationProperties(false));
        tslLoader.init();

        tslLoader.loadTSL();

        assertEquals(Map.of(), tslLoader.getCertificatesByTerritory());
    }

    @Test
    void loadTSL_WhenLoadFromCacheIsTrue_OfflineDataLoaderIsUsed() {
        tslLoader = loader(createConfigurationProperties(true));
        tslLoader.init();

        tslLoader.loadTSL();

        verify(tslValidationJobFactory).createValidationJob();
        verify(tslValidationJob).setOfflineDataLoader(any(DSSFileLoader.class));
        verify(tslValidationJob).setTrustedListCertificateSource(any(TrustedListsCertificateSource.class));
        verify(tslValidationJob).setListOfTrustedListSources(any());
        verify(tslValidationJob).setSynchronizationStrategy(any());
        verify(tslValidationJob).offlineRefresh();
        verify(tslValidationJob).getSummary();
        verifyNoMoreInteractions(tslValidationJobFactory, tslValidationJob);
        verifyNoInteractions(keyStoreCertificateSource);
    }

    @Test
    void loadTSL_WhenLoadFromCacheIsFalse_OnlineDataLoaderIsUsed() {
        tslLoader = loader(createConfigurationProperties(false));
        tslLoader.init();

        tslLoader.loadTSL();

        verify(tslValidationJobFactory).createValidationJob();
        verify(tslValidationJob).setOnlineDataLoader(any(DSSFileLoader.class));
        verify(tslValidationJob).setTrustedListCertificateSource(any(TrustedListsCertificateSource.class));
        verify(tslValidationJob).setListOfTrustedListSources(any());
        verify(tslValidationJob).setSynchronizationStrategy(any());
        verify(tslValidationJob).onlineRefresh();
        verify(tslValidationJob).getSummary();
        verifyNoMoreInteractions(tslValidationJobFactory, tslValidationJob);
        verifyNoInteractions(keyStoreCertificateSource);
    }

    @Test
    void loadTSL_WhenSnapshotStoreHasNoCurrentSnapshot_NothingIsLoaded() {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();

        tslLoader.loadTSL();

        verify(tslValidationJob, never()).setOfflineDataLoader(any());
        verify(tslValidationJob, never()).offlineRefresh();
        assertNull(tslLoader.getLastRefresh());
        assertNull(tslLoader.getSnapshotId());
    }

    @Test
    void loadTSL_WhenSnapshotStoreNamesASnapshot_ItIsLoadedAndRecorded() throws IOException {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        publish("20261013T183000Z");

        tslLoader.loadTSL();

        var order = inOrder(tslValidationJob);
        order.verify(tslValidationJob).setOfflineDataLoader(any(DSSFileLoader.class));
        order.verify(tslValidationJob).offlineRefresh();
        verify(tslValidationJob, never()).onlineRefresh();
        assertEquals("20261013T183000Z", tslLoader.getSnapshotId());
        assertNotNull(tslLoader.getLastRefresh());
    }

    @Test
    void refreshIfSnapshotChanged_WhenCurrentStillNamesTheLoadedSnapshot_NothingHappens() throws IOException {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        publish("20261013T183000Z");
        tslLoader.loadTSL();

        tslLoader.refreshIfSnapshotChanged();

        verify(tslValidationJob).offlineRefresh();
    }

    @Test
    void refreshIfSnapshotChanged_WhenCurrentNamesAnotherSnapshot_ItIsLoaded() throws IOException {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        publish("20261013T183000Z");
        tslLoader.loadTSL();
        publish("20261014T000000Z");

        tslLoader.refreshIfSnapshotChanged();

        verify(tslValidationJob, times(2)).offlineRefresh();
        assertEquals("20261014T000000Z", tslLoader.getSnapshotId());
    }

    @Test
    void refreshIfSnapshotChanged_WhenCurrentNamesAnotherSnapshot_TheOldSourceServesUntilTheSwap() throws Exception {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        Map<CertificateToken, List<TrustProperties>> first = TslTestUtils.anchors(1);
        Map<CertificateToken, List<TrustProperties>> second = TslTestUtils.anchors(1);
        CertificateToken firstAnchor = first.keySet().iterator().next();
        CertificateToken secondAnchor = second.keySet().iterator().next();
        filledOnRefresh(first);
        publish("20261013T183000Z");
        tslLoader.loadTSL();
        TrustedListsCertificateSource published = holder.current();
        publish("20261014T000000Z");
        // A reader during the second load, while the job fills the fresh source: the holder still serves the old.
        AtomicReference<TrustedListsCertificateSource> fresh = new AtomicReference<>();
        doAnswer(invocation -> {
            fresh.set(invocation.getArgument(0));
            return null;
        }).when(tslValidationJob).setTrustedListCertificateSource(any());
        doAnswer(invocation -> {
            assertSame(published, holder.current());
            assertTrue(holder.current().isTrusted(firstAnchor));
            assertNotSame(published, fresh.get());
            fresh.get().setTrustPropertiesByCertificates(second);
            return null;
        }).when(tslValidationJob).offlineRefresh();

        tslLoader.refreshIfSnapshotChanged();

        assertNotSame(published, holder.current());
        assertTrue(holder.current().isTrusted(secondAnchor));
        assertFalse(holder.current().isTrusted(firstAnchor));
        assertEquals("20261014T000000Z", tslLoader.getSnapshotId());
    }

    @Test
    void refreshIfSnapshotChanged_WhenTheNewSnapshotLacksAMandatoryTerritory_TheLoadedSourceIsKept() throws Exception {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        Map<CertificateToken, List<TrustProperties>> estonian = anchorsOf("EE");
        filledOnRefresh(estonian);
        publish("20261013T183000Z", Instant.parse("2026-10-13T18:30:00Z"));
        tslLoader.loadTSL();
        filledOnRefresh(anchorsOf("LV"));
        publish("20261014T000000Z", Instant.parse("2026-10-14T00:00:00Z"));

        tslLoader.refreshIfSnapshotChanged();
        tslLoader.refreshIfSnapshotChanged();

        // Loaded once, rejected, and the poll leaves it alone afterwards.
        verify(tslValidationJob, times(2)).offlineRefresh();
        assertEquals("20261013T183000Z", tslLoader.getSnapshotId());
        assertEquals(Instant.parse("2026-10-13T18:30:00Z"), tslLoader.getSnapshotCreatedAt());
        assertEquals(Map.of("EE", 1), tslLoader.getCertificatesByTerritory());
        assertTrue(holder.current().isTrusted(estonian.keySet().iterator().next()));
    }

    @Test
    void refreshIfSnapshotChanged_WhenTheNewSnapshotIsEmpty_TheLoadedSourceIsKept() throws Exception {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        filledOnRefresh(anchorsOf("EE"));
        publish("20261013T183000Z");
        tslLoader.loadTSL();
        filledOnRefresh(Map.of());
        publish("20261014T000000Z");

        tslLoader.refreshIfSnapshotChanged();

        assertEquals("20261013T183000Z", tslLoader.getSnapshotId());
        assertEquals(1, tslLoader.getNumberOfCertificates());
    }

    @Test
    void refreshIfSnapshotChanged_AfterARejectedSnapshot_TheNextGoodOneIsLoaded() throws Exception {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        filledOnRefresh(anchorsOf("EE"));
        publish("20261013T183000Z");
        tslLoader.loadTSL();
        filledOnRefresh(anchorsOf("LV"));
        publish("20261014T000000Z");
        tslLoader.refreshIfSnapshotChanged();
        filledOnRefresh(anchorsOf("EE"));
        publish("20261014T060000Z");

        tslLoader.refreshIfSnapshotChanged();

        assertEquals("20261014T060000Z", tslLoader.getSnapshotId());
    }

    @Test
    void loadTSL_TheScheduledRefreshTriesARejectedSnapshotAgain() throws Exception {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        filledOnRefresh(anchorsOf("EE"));
        publish("20261013T183000Z");
        tslLoader.loadTSL();
        filledOnRefresh(anchorsOf("LV"));
        publish("20261014T000000Z");
        tslLoader.refreshIfSnapshotChanged();
        filledOnRefresh(anchorsOf("EE"));

        tslLoader.loadTSL();

        verify(tslValidationJob, times(3)).offlineRefresh();
        assertEquals("20261014T000000Z", tslLoader.getSnapshotId());
    }

    @Test
    void loadTSL_WhenTheFirstSnapshotLacksAMandatoryTerritory_ItGoesLiveAndIsReported() throws Exception {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        filledOnRefresh(anchorsOf("LV"));
        publish("20261013T183000Z");

        tslLoader.loadTSL();

        assertEquals("20261013T183000Z", tslLoader.getSnapshotId());
        assertEquals(Map.of("LV", 1), tslLoader.getCertificatesByTerritory());
    }

    @Test
    void loadTSL_WhenNoTerritoryIsMandatory_AnySnapshotWithAnchorsIsLoaded() throws Exception {
        TSLLoaderConfigurationProperties props = createSnapshotConfigurationProperties();
        props.setMandatoryTerritories(List.of());
        tslLoader = loader(props);
        tslLoader.init();
        filledOnRefresh(anchorsOf("EE"));
        publish("20261013T183000Z");
        tslLoader.loadTSL();
        filledOnRefresh(anchorsOf("LV"));
        publish("20261014T000000Z");

        tslLoader.refreshIfSnapshotChanged();

        assertEquals("20261014T000000Z", tslLoader.getSnapshotId());
    }

    @Test
    void refreshIfSnapshotChanged_OutsideSnapshotMode_NothingHappens() {
        tslLoader = loader(createConfigurationProperties(true));
        tslLoader.init();

        tslLoader.refreshIfSnapshotChanged();

        verify(tslValidationJob, never()).offlineRefresh();
    }

    @Test
    void loadTSL_WhenCurrentNamesAMissingDirectory_NothingIsLoadedAndStartupSurvives() throws IOException {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        Files.writeString(snapshotRoot.resolve("current"), "20261013T183000Z\n");

        tslLoader.loadTSL();

        verify(tslValidationJob, never()).offlineRefresh();
        assertNull(tslLoader.getSnapshotId());
    }

    @Test
    void loadTSL_WhenSnapshotIsOlderThanMaxAge_ItIsRefusedAndTheLoadedStateKept() throws IOException {
        TSLLoaderConfigurationProperties props = createSnapshotConfigurationProperties();
        props.setSnapshotMaxAge(Duration.ofDays(2));
        tslLoader = loader(props);
        tslLoader.init();
        Instant keptCreatedAt = Instant.now().minus(Duration.ofHours(1)).truncatedTo(ChronoUnit.SECONDS);
        publish("20261013T183000Z", keptCreatedAt);
        tslLoader.loadTSL();
        publish("20261014T000000Z", Instant.now().minus(Duration.ofDays(3)));

        tslLoader.refreshIfSnapshotChanged();
        tslLoader.refreshIfSnapshotChanged();

        verify(tslValidationJob, times(1)).offlineRefresh();
        assertEquals("20261013T183000Z", tslLoader.getSnapshotId());
        // SivaTslSnapshotStale reads this time: a refused snapshot must not make the pod look fresh.
        assertEquals(keptCreatedAt, tslLoader.getSnapshotCreatedAt());
    }

    @Test
    void loadTSL_WhenSnapshotLacksAFileItsManifestLists_ItIsRefusedUntilTheFileArrives() throws IOException {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        List<String> files = List.of("https___lotl_example_eu-lotl_xml", "https___tl_example_EE_xml");
        publish("20261013T183000Z", files, files.subList(0, 1));

        tslLoader.loadTSL();
        tslLoader.refreshIfSnapshotChanged();
        verify(tslValidationJob, never()).offlineRefresh();
        assertNull(tslLoader.getSnapshotId());

        Files.writeString(snapshotRoot.resolve("snapshots/20261013T183000Z/https___tl_example_EE_xml"), "<list/>");
        tslLoader.refreshIfSnapshotChanged();

        verify(tslValidationJob).offlineRefresh();
        assertEquals("20261013T183000Z", tslLoader.getSnapshotId());
    }

    @Test
    void loadTSL_WhenMaxAgeIsSetAndTheManifestIsMissing_TheSnapshotIsRefused() throws IOException {
        TSLLoaderConfigurationProperties props = createSnapshotConfigurationProperties();
        props.setSnapshotMaxAge(Duration.ofDays(2));
        tslLoader = loader(props);
        tslLoader.init();
        publish("20261013T183000Z");

        tslLoader.loadTSL();

        verify(tslValidationJob, never()).offlineRefresh();
    }

    @Test
    void loadTSL_WhenSnapshotIsYoungerThanMaxAge_ItIsLoaded() throws IOException {
        TSLLoaderConfigurationProperties props = createSnapshotConfigurationProperties();
        props.setSnapshotMaxAge(Duration.ofDays(2));
        tslLoader = loader(props);
        tslLoader.init();
        publish("20261013T183000Z", Instant.now().minus(Duration.ofHours(1)));

        tslLoader.loadTSL();

        verify(tslValidationJob).offlineRefresh();
        assertEquals("20261013T183000Z", tslLoader.getSnapshotId());
    }

    @Test
    void loadTSL_WhenListsWereParsed_TheEarliestNextUpdatePerTerritoryAndTheLotlsAreRecorded() {
        tslLoader = loader(createConfigurationProperties(false));
        tslLoader.init();
        Instant estonianSoon = Instant.parse("2026-11-20T00:00:00Z");
        Instant later = Instant.parse("2027-04-08T00:00:00Z");
        LOTLInfo lotl = lotl(Instant.parse("2027-03-17T12:04:06Z"),
                trustedList("EE", later), trustedList("EE", estonianSoon), trustedList("LV", later),
                trustedList("FI", null), new TLInfo(null, null, null, "https://tl/unparsed"));
        TLInfo polish = trustedList("PL", later);
        when(tslValidationJob.getSummary()).thenReturn(new TLValidationJobSummary(List.of(lotl), List.of(polish)));
        assertEquals(Map.of(), tslLoader.getNextUpdateByTerritory());
        assertNull(tslLoader.getLotlNextUpdate());

        tslLoader.loadTSL();

        assertEquals(Map.of("EE", estonianSoon, "LV", later, "PL", later), tslLoader.getNextUpdateByTerritory());
        assertEquals(Instant.parse("2027-03-17T12:04:06Z"), tslLoader.getLotlNextUpdate());
    }

    @Test
    void loadTSL_WhenSnapshotIsLoaded_ItsCreationTimeIsRecorded() throws IOException {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        Instant createdAt = Instant.parse("2026-10-14T06:01:08Z");
        publish("20261014T060108Z", createdAt);

        tslLoader.loadTSL();

        assertEquals(createdAt, tslLoader.getSnapshotCreatedAt());
    }

    @Test
    void loadTSL_WhenSnapshotHasNoManifest_ItsCreationTimeIsUnknown() throws IOException {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        publish("20261014T060108Z");

        tslLoader.loadTSL();

        assertEquals("20261014T060108Z", tslLoader.getSnapshotId());
        assertNull(tslLoader.getSnapshotCreatedAt());
    }

    @Test
    void loadTSL_Online_TheDownloadTimeOfEachListIsItsCacheFilesModificationTime() throws IOException {
        TSLLoaderConfigurationProperties props = createConfigurationProperties(false);
        props.setCacheDirectory(cache.toFile());
        props.setRefreshPeriod(Duration.ofHours(6));
        tslLoader = loader(props);
        tslLoader.init();
        Instant lotlDownloaded = Instant.parse("2026-10-20T06:00:00Z");
        Instant estonianDownloaded = Instant.parse("2026-10-21T06:00:00Z");
        cached("https://lotl", lotlDownloaded);
        cached("https://tl/EE", estonianDownloaded);
        LOTLInfo lotl = lotl(Instant.parse("2027-03-17T12:04:06Z"), trustedList("EE"), trustedList("LV"));
        when(tslValidationJob.getSummary()).thenReturn(new TLValidationJobSummary(List.of(lotl), List.of()));

        tslLoader.loadTSL();

        assertEquals(Map.of("EU", lotlDownloaded, "EE", estonianDownloaded), tslLoader.getDownloadedAt());
        assertEquals(Map.of(), tslLoader.getLastCopies());
        assertEquals(Duration.ofHours(6), tslLoader.getRefreshPeriod());
    }

    @Test
    void loadTSL_Online_AListServedFromItsLastCopyIsRecordedByTerritoryWithTheReason() throws IOException {
        TSLLoaderConfigurationProperties props = createConfigurationProperties(false);
        props.setCacheDirectory(cache.toFile());
        tslLoader = loader(props);
        tslLoader.init();
        cached("https://lotl", Instant.parse("2026-10-20T06:00:00Z"));
        AtomicReference<LastGoodCopyFileCacheDataLoader> onlineLoader = new AtomicReference<>();
        doAnswer(invocation -> {
            LastGoodCopyFileCacheDataLoader loader = invocation.getArgument(0);
            DataLoader unreachable = mock(DataLoader.class);
            when(unreachable.get(anyString())).thenThrow(new DSSExternalResourceException("certificate_unknown"));
            loader.setDataLoader(unreachable);
            onlineLoader.set(loader);
            return null;
        }).when(tslValidationJob).setOnlineDataLoader(any());
        doAnswer(invocation -> onlineLoader.get().getDocument("https://lotl"))
                .when(tslValidationJob).onlineRefresh();
        LOTLInfo lotl = lotl(Instant.parse("2027-03-17T12:04:06Z"), trustedList("EE"));
        when(tslValidationJob.getSummary()).thenReturn(new TLValidationJobSummary(List.of(lotl), List.of()));

        tslLoader.loadTSL();

        assertEquals(Map.of("EU", "certificate_unknown"), tslLoader.getLastCopies());
        assertEquals(Map.of("EU", Instant.parse("2026-10-20T06:00:00Z")), tslLoader.getDownloadedAt());
    }

    @Test
    void loadTSL_Online_TheNextRefreshForgetsTheCopiesServedBefore() throws IOException {
        TSLLoaderConfigurationProperties props = createConfigurationProperties(false);
        props.setCacheDirectory(cache.toFile());
        tslLoader = loader(props);
        tslLoader.init();
        cached("https://lotl", Instant.parse("2026-10-20T06:00:00Z"));
        AtomicReference<LastGoodCopyFileCacheDataLoader> onlineLoader = new AtomicReference<>();
        doAnswer(invocation -> {
            onlineLoader.set(invocation.getArgument(0));
            return null;
        }).when(tslValidationJob).setOnlineDataLoader(any());
        DataLoader dataLoader = mock(DataLoader.class);
        when(dataLoader.get(anyString())).thenThrow(new DSSExternalResourceException("unreachable"))
                .thenReturn("<lotl/>".getBytes(StandardCharsets.UTF_8));
        doAnswer(invocation -> {
            onlineLoader.get().setDataLoader(dataLoader);
            return onlineLoader.get().getDocument("https://lotl");
        }).when(tslValidationJob).onlineRefresh();
        LOTLInfo lotl = lotl(Instant.parse("2027-03-17T12:04:06Z"));
        when(tslValidationJob.getSummary()).thenReturn(new TLValidationJobSummary(List.of(lotl), List.of()));
        tslLoader.loadTSL();
        assertEquals(Map.of("EU", "unreachable"), tslLoader.getLastCopies());

        tslLoader.loadTSL();

        assertEquals(Map.of(), tslLoader.getLastCopies());
    }

    @Test
    void loadTSL_WhenSnapshotIsLoaded_ItsPeriodAndDownloadTimesAreTakenFromTheManifest() throws IOException {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        Instant downloaded = Instant.parse("2026-10-21T06:00:12Z");
        publish("20261021T060108Z", Instant.parse("2026-10-21T06:01:08Z"), Duration.ofHours(6),
                Map.of("testName", Map.of("EU", downloaded, "EE", downloaded),
                        "other", Map.of("EE", Instant.EPOCH)));

        tslLoader.loadTSL();

        assertEquals(Map.of("EU", downloaded, "EE", downloaded), tslLoader.getDownloadedAt());
        assertEquals(Duration.ofHours(6), tslLoader.getRefreshPeriod());
        assertEquals(Map.of(), tslLoader.getLastCopies());
    }

    @Test
    void loadTSL_WhenTheManifestPredatesThePeriodAndDownloadTimes_TheyAreUnknown() throws IOException {
        tslLoader = loader(createSnapshotConfigurationProperties());
        tslLoader.init();
        publish("20261014T060108Z", Instant.parse("2026-10-14T06:01:08Z"));

        tslLoader.loadTSL();

        assertEquals(Map.of(), tslLoader.getDownloadedAt());
        assertNull(tslLoader.getRefreshPeriod());
    }

    @Test
    void init_WhenSslBundleIsSet_TheTrustListDownloadsTrustTheBundle() throws Exception {
        TSLLoaderConfigurationProperties props = createConfigurationProperties(false);
        props.setSslBundle("tsl");
        tslLoader = loader(props);
        when(sslBundles.getIfAvailable()).thenReturn(new DefaultSslBundleRegistry("tsl",
                SslBundleDataLoaderTest.bundle(SslBundleDataLoaderTest.lotlTestTrustStore())));

        tslLoader.init();

        FileCacheDataLoader onlineLoader = (FileCacheDataLoader) tslLoader.onlineLoader();
        assertTrue(onlineLoader.getDataLoader() instanceof SslBundleDataLoader);
        assertSame(keyStoreCertificateSource, tslLoader.europeanLOTL().getCertificateSource());
    }

    @Test
    void loadTSL_WhenLotlBundleIsSet_TheSignersAreReadFromTheBundleBeforeEveryRefresh() throws Exception {
        TSLLoaderConfigurationProperties props = createConfigurationProperties(false);
        props.setLotlBundle("lotl");
        tslLoader = loader(props);
        DefaultSslBundleRegistry registry = new DefaultSslBundleRegistry("lotl",
                SslBundleDataLoaderTest.bundle(SslBundleDataLoaderTest.lotlTestTrustStore()));
        when(sslBundles.getIfAvailable()).thenReturn(registry);
        tslLoader.init();
        CertificateSource signers = tslLoader.europeanLOTL().getCertificateSource();
        assertEquals(1, signers.getCertificates().size());
        assertTrue(((FileCacheDataLoader) tslLoader.onlineLoader()).getDataLoader() instanceof CommonsDataLoader);
        KeyStore empty = KeyStore.getInstance("PKCS12");
        empty.load(null, null);
        registry.updateBundle("lotl", SslBundleDataLoaderTest.bundle(empty));

        tslLoader.loadTSL();

        ArgumentCaptor<LOTLSource> lotl = ArgumentCaptor.forClass(LOTLSource.class);
        verify(tslValidationJob).setListOfTrustedListSources(lotl.capture());
        assertEquals(0, lotl.getValue().getCertificateSource().getCertificates().size());
        verifyNoInteractions(keyStoreCertificateSource);
    }

    @Test
    void init_WhenABundleIsNamedWithoutSslBundles_InitFails() {
        TSLLoaderConfigurationProperties props = createConfigurationProperties(false);
        props.setSslBundle("tsl");
        tslLoader = loader(props);

        assertThrows(IllegalStateException.class, tslLoader::init);
    }

    private void publish(String id) throws IOException {
        Files.createDirectories(snapshotRoot.resolve("snapshots").resolve(id));
        Files.writeString(snapshotRoot.resolve("current"), id + "\n");
    }

    // A list file in the online working cache as DSS names it, with the modification time of its download.
    private void cached(String url, Instant downloadedAt) throws IOException {
        Path file = cache.resolve(DSSUtils.getNormalizedString(url));
        Files.writeString(file, "<list/>");
        Files.setLastModifiedTime(file, FileTime.from(downloadedAt));
    }

    private void publish(String id, Instant createdAt) throws IOException {
        publish(id, createdAt, null, null);
    }

    private void publish(String id, Instant createdAt, Duration period,
                         Map<String, Map<String, Instant>> downloadedAt) throws IOException {
        publish(id, createdAt, null, period, downloadedAt);
    }

    // A snapshot whose manifest lists `fileNames`, of which only `present` have arrived.
    private void publish(String id, List<String> fileNames, List<String> present) throws IOException {
        publish(id, Instant.now(), fileNames, null, null);
        for (String name : present) {
            Files.writeString(snapshotRoot.resolve("snapshots").resolve(id).resolve(name), "<list/>");
        }
    }

    private void publish(String id, Instant createdAt, List<String> fileNames, Duration period,
                         Map<String, Map<String, Instant>> downloadedAt) throws IOException {
        publish(id);
        TSLSnapshotManifest manifest = new TSLSnapshotManifest(id, createdAt, "UP", List.of(), Map.of(),
                fileNames == null ? 0 : fileNames.size(), fileNames, period, downloadedAt);
        Files.write(snapshotRoot.resolve("snapshots").resolve(id).resolve(TSLSnapshotStore.MANIFEST_FILE),
                JsonMapper.shared().writeValueAsBytes(manifest));
    }

    private TSLLoaderConfigurationProperties createSnapshotConfigurationProperties() {
        TSLLoaderConfigurationProperties props = createConfigurationProperties(true);
        props.setSnapshotDirectory(snapshotRoot.toFile());
        return props;
    }

    // One anchor listed by the trusted list of `territory`.
    private static Map<CertificateToken, List<TrustProperties>> anchorsOf(String territory) throws Exception {
        CertificateToken token = TslTestUtils.anchors(1).keySet().iterator().next();
        return Map.of(token, List.of(trust(trustedList(territory))));
    }

    private static TLInfo trustedList(String territory) {
        ParsingInfoRecord parsing = mock(ParsingInfoRecord.class);
        when(parsing.getTerritory()).thenReturn(territory);
        return new TLInfo(null, parsing, null, "https://tl/" + territory);
    }

    private static TLInfo trustedList(String territory, Instant nextUpdate) {
        ParsingInfoRecord parsing = mock(ParsingInfoRecord.class);
        when(parsing.getTerritory()).thenReturn(territory);
        lenient().when(parsing.getNextUpdateDate()).thenReturn(nextUpdate == null ? null : Date.from(nextUpdate));
        return new TLInfo(null, parsing, null, "https://tl/" + territory);
    }

    private static LOTLInfo lotl(Instant nextUpdate, TLInfo... trustedLists) {
        ParsingInfoRecord parsing = mock(ParsingInfoRecord.class);
        when(parsing.getNextUpdateDate()).thenReturn(Date.from(nextUpdate));
        LOTLInfo lotl = new LOTLInfo(null, parsing, null, "https://lotl");
        lotl.setTlInfos(List.of(trustedLists));
        return lotl;
    }

    private static TrustProperties trust(TLInfo trustedList) {
        TrustProperties trustProperties = mock(TrustProperties.class);
        when(trustProperties.getTLInfo()).thenReturn(trustedList);
        return trustProperties;
    }

    // What the job does during a refresh: fills the source it was given.
    private void filledOnRefresh(Map<CertificateToken, List<TrustProperties>> anchors) {
        AtomicReference<TrustedListsCertificateSource> given = new AtomicReference<>();
        doAnswer(invocation -> {
            given.set(invocation.getArgument(0));
            return null;
        }).when(tslValidationJob).setTrustedListCertificateSource(any());
        Answer<Void> fill = invocation -> {
            given.get().setTrustPropertiesByCertificates(anchors);
            return null;
        };
        lenient().doAnswer(fill).when(tslValidationJob).onlineRefresh();
        lenient().doAnswer(fill).when(tslValidationJob).offlineRefresh();
    }

    private static TSLLoaderConfigurationProperties createConfigurationProperties(boolean loadFromCache) {
        TSLLoaderConfigurationProperties props = new TSLLoaderConfigurationProperties();
        props.setUrl(TSL_URL);
        props.setOjUrl(TSL_OJ_URL);
        props.setLotlRootSchemeInfoUri(TSL_INFO_URL);
        props.setLoadFromCache(loadFromCache);
        props.setTrustedTerritories(DEFAULT_TRUSTED_TERRITORIES);
        return props;
    }

}
