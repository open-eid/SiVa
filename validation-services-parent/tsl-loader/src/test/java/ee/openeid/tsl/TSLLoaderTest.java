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
import eu.europa.esig.dss.model.tsl.DownloadInfoRecord;
import eu.europa.esig.dss.model.tsl.LOTLInfo;
import eu.europa.esig.dss.model.tsl.ParsingInfoRecord;
import eu.europa.esig.dss.model.tsl.TLInfo;
import eu.europa.esig.dss.model.tsl.TLValidationJobSummary;
import eu.europa.esig.dss.model.tsl.ValidationInfoRecord;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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
    private TrustedListsCertificateSource trustedListSource;
    @Mock
    private KeyStoreCertificateSource keyStoreCertificateSource;

    @InjectMocks
    private TSLLoader tslLoader = new TSLLoader("testName");

    @BeforeEach
    public void setUp() throws Exception {
        when(tslValidationJobFactory.createValidationJob()).thenReturn(tslValidationJob);
    }

    @Test
    void loadTSL_WhenRefreshReturns_LastRefreshAndCertificateCountAreRecorded() {
        tslLoader.setTslLoaderConfigurationProperties(createConfigurationProperties(false));
        tslLoader.init();
        when(trustedListSource.getNumberOfCertificates()).thenReturn(42);
        assertNull(tslLoader.getLastRefresh());
        assertEquals(0, tslLoader.getNumberOfCertificates());
        Instant before = Instant.now();

        tslLoader.loadTSL();

        assertNotNull(tslLoader.getLastRefresh());
        assertFalse(tslLoader.getLastRefresh().isBefore(before));
        assertEquals(42, tslLoader.getNumberOfCertificates());
    }

    @Test
    void loadTSL_WhenSummaryHasTrustedLists_OnlyParsedUnexpiredNotInvalidOnesAreCountedPerTerritory() {
        tslLoader.setTslLoaderConfigurationProperties(createConfigurationProperties(false));
        tslLoader.init();
        Date tomorrow = new Date(System.currentTimeMillis() + 86_400_000L);
        Date yesterday = new Date(System.currentTimeMillis() - 86_400_000L);
        LOTLInfo lotl = new LOTLInfo(mock(DownloadInfoRecord.class), mock(ParsingInfoRecord.class), mock(ValidationInfoRecord.class), "lotl");
        lotl.setTlInfos(List.of(
                trustedList("EE", 7, true, Boolean.TRUE, tomorrow),
                trustedList("EE", 3, true, Boolean.TRUE, null),
                trustedList("LV", 4, true, Boolean.TRUE, tomorrow),
                trustedList("LT", 1, true, null, tomorrow),
                trustedList("BE", 9, true, Boolean.FALSE, tomorrow),
                trustedList("DE", 9, true, Boolean.TRUE, yesterday),
                trustedList("FR", 9, false, Boolean.TRUE, tomorrow)));
        TLValidationJobSummary summary = new TLValidationJobSummary(List.of(lotl), List.of(
                trustedList("LT", 2, true, Boolean.TRUE, tomorrow)));
        when(trustedListSource.getSummary()).thenReturn(summary);
        assertEquals(Map.of(), tslLoader.getCertificatesByTerritory());

        tslLoader.loadTSL();

        assertEquals(Map.of("EE", 7, "LT", 3, "LV", 4), tslLoader.getCertificatesByTerritory());
        assertEquals(List.of("EE", "LT", "LV"), List.copyOf(tslLoader.getCertificatesByTerritory().keySet()));
    }

    @Test
    void loadTSL_WhenSummaryIsAbsent_NoTerritoriesAreRecorded() {
        tslLoader.setTslLoaderConfigurationProperties(createConfigurationProperties(false));
        tslLoader.init();
        when(trustedListSource.getSummary()).thenReturn(null);

        tslLoader.loadTSL();

        assertEquals(Map.of(), tslLoader.getCertificatesByTerritory());
    }

    @Test
    void loadTSL_WhenRefreshThrows_NothingIsRecorded() {
        tslLoader.setTslLoaderConfigurationProperties(createConfigurationProperties(false));
        tslLoader.init();
        doThrow(new IllegalStateException("boom")).when(tslValidationJob).onlineRefresh();

        assertThrows(IllegalStateException.class, tslLoader::loadTSL);

        assertNull(tslLoader.getLastRefresh());
        assertEquals(0, tslLoader.getNumberOfCertificates());
        verifyNoInteractions(trustedListSource);
    }

    @Test
    void loadTSL_WhenLoadFromCacheIsTrue_OfflineDataLoaderIsUsed() {
        tslLoader.setTslLoaderConfigurationProperties(createConfigurationProperties(true));
        tslLoader.init();

        tslLoader.loadTSL();

        verify(tslValidationJobFactory).createValidationJob();
        verify(tslValidationJob).setOfflineDataLoader(any(DSSFileLoader.class));
        verify(tslValidationJob).setTrustedListCertificateSource(trustedListSource);
        verify(tslValidationJob).setListOfTrustedListSources(any());
        verify(tslValidationJob).setSynchronizationStrategy(any());
        verify(tslValidationJob).offlineRefresh();
        verify(trustedListSource).getNumberOfCertificates();
        verify(trustedListSource).getSummary();
        verifyNoMoreInteractions(tslValidationJobFactory, tslValidationJob, trustedListSource);
        verifyNoInteractions(keyStoreCertificateSource);
    }

    @Test
    void loadTSL_WhenLoadFromCacheIsFalse_OnlineDataLoaderIsUsed() {
        tslLoader.setTslLoaderConfigurationProperties(createConfigurationProperties(false));
        tslLoader.init();

        tslLoader.loadTSL();

        verify(tslValidationJobFactory).createValidationJob();
        verify(tslValidationJob).setOnlineDataLoader(any(DSSFileLoader.class));
        verify(tslValidationJob).setTrustedListCertificateSource(trustedListSource);
        verify(tslValidationJob).setListOfTrustedListSources(any());
        verify(tslValidationJob).setSynchronizationStrategy(any());
        verify(tslValidationJob).onlineRefresh();
        verify(trustedListSource).getNumberOfCertificates();
        verify(trustedListSource).getSummary();
        verifyNoMoreInteractions(tslValidationJobFactory, tslValidationJob, trustedListSource);
        verifyNoInteractions(keyStoreCertificateSource);
    }

    /** {@code valid} null: the trusted list has no validation result (yet), which the DSS strategy accepts. */
    private static TLInfo trustedList(String territory, int certificates, boolean parsed, Boolean valid, Date nextUpdate) {
        ParsingInfoRecord parsing = mock(ParsingInfoRecord.class);
        when(parsing.isResultExist()).thenReturn(parsed);
        ValidationInfoRecord validation = mock(ValidationInfoRecord.class);
        boolean unexpired = nextUpdate != null && nextUpdate.after(new Date());
        if (parsed) {
            when(parsing.getTerritory()).thenReturn(territory);
            when(parsing.getNextUpdateDate()).thenReturn(nextUpdate);
        }
        if (parsed && unexpired) {
            when(validation.isResultExist()).thenReturn(valid != null);
        }
        if (parsed && unexpired && valid != null) {
            when(validation.isValid()).thenReturn(valid);
        }
        if (parsed && unexpired && !Boolean.FALSE.equals(valid)) {
            when(parsing.getCertNumber()).thenReturn(certificates);
        }
        return new TLInfo(mock(DownloadInfoRecord.class), parsing, validation, "https://tl/" + territory + "/" + certificates);
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
