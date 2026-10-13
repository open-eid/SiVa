/*
 * Copyright 2016 - 2026 Riigi Infosüsteemi Amet
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
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.tsl.ParsingInfoRecord;
import eu.europa.esig.dss.model.tsl.TLInfo;
import eu.europa.esig.dss.model.tsl.TrustProperties;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.tsl.function.EULOTLOtherTSLPointer;
import eu.europa.esig.dss.tsl.function.EUTLOtherTSLPointer;
import eu.europa.esig.dss.tsl.function.OfficialJournalSchemeInformationURI;
import eu.europa.esig.dss.tsl.function.SchemeTerritoryOtherTSLPointer;
import eu.europa.esig.dss.tsl.function.XMLOtherTSLPointer;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.tsl.sync.ExpirationAndSignatureCheckStrategy;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.digidoc4j.utils.ResourceUtils;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class TSLLoader {

    @Getter
    private final @NonNull String tslName;

    /**
     * What the last refresh attempt left behind, published as one immutable value so that a reader (the
     * health indicator, on a request thread) never sees the count of one refresh with the territories of
     * another. {@link State#NONE} until the first refresh.
     */
    private final AtomicReference<State> state = new AtomicReference<>(State.NONE);

    private record State(Instant lastRefresh, int numberOfCertificates, Map<String, Integer> certificatesByTerritory,
                         String snapshotId) {
        private static final State NONE = new State(null, 0, Map.of(), null);
    }

    private TSLValidationJobFactory tslValidationJobFactory;
    private TLValidationJob tslValidationJob;
    private TSLLoaderConfigurationProperties configurationProperties;
    private TrustedListsCertificateSource trustedListSource;
    private KeyStoreCertificateSource keyStoreCertificateSource;
    private ProxyConfig proxyConfig;
    private TSLSnapshotStore snapshotStore;
    // The snapshot id last refused (missing directory, too old); warned about once, then only at debug.
    private String refusedSnapshotId;

    @PostConstruct
    public void init() {
        List<String> trusted = configurationProperties.getTrustedTerritories();
        List<String> mandatory = configurationProperties.getMandatoryTerritories();
        if (trusted != null && !trusted.isEmpty() && mandatory != null && !trusted.containsAll(mandatory)) {
            log.warn("'{}' TSL: mandatory territories {} are not all among the trusted territories {}; "
                    + "readiness can never be reached (codes are case-sensitive)", tslName, mandatory, trusted);
        }
        tslValidationJob = tslValidationJobFactory.createValidationJob();
        if (!configurationProperties.isLoadFromCache()) {
            tslValidationJob.setOnlineDataLoader(onlineLoader());
        } else if (configurationProperties.getSnapshotDirectory() != null) {
            // The offline loader is created per refresh, on whichever snapshot `current` names then.
            snapshotStore = new TSLSnapshotStore(configurationProperties.getSnapshotDirectory().toPath());
        } else {
            tslValidationJob.setOfflineDataLoader(offlineLoader());
        }
        tslValidationJob.setTrustedListCertificateSource(trustedListSource);
        tslValidationJob.setListOfTrustedListSources(europeanLOTL());
        tslValidationJob.setSynchronizationStrategy(new ExpirationAndSignatureCheckStrategy());
    }

    void loadTSL() {
        if (!configurationProperties.isLoadFromCache()) {
            log.info("Loading '{}' TSL over the network", tslName);
            tslValidationJob.onlineRefresh();
            log.info("Finished loading '{}' TSL over the network", tslName);
            record(null);
            return;
        }
        String snapshotId = null;
        if (snapshotStore != null) {
            Optional<String> current = snapshotStore.currentId();
            if (current.isEmpty()) {
                Path root = snapshotStore.getRoot();
                log.warn("'{}' TSL: no trust-list snapshot published under {} yet; nothing to load", tslName, root);
                return;
            }
            snapshotId = current.get();
            Path directory = snapshotStore.snapshotDirectory(snapshotId);
            if (!acceptable(snapshotId, directory)) {
                return;
            }
            tslValidationJob.setOfflineDataLoader(offlineLoader(directory));
        }
        String source = snapshotId == null ? "cache" : "snapshot " + snapshotId;
        log.info("Loading '{}' TSL from {}", tslName, source);
        tslValidationJob.offlineRefresh();
        log.info("Finished loading '{}' TSL from {}", tslName, source);
        record(snapshotId);
    }

    /**
     * In snapshot mode: reloads when the store's {@code current} names a snapshot other than the one loaded
     * (or none has been loaded yet). A no-op otherwise.
     */
    void refreshIfSnapshotChanged() {
        if (snapshotStore == null) {
            return;
        }
        String loaded = state.get().snapshotId();
        String published = snapshotStore.currentId().orElse(null);
        if (published != null && !published.equals(loaded)) {
            log.info("'{}' TSL: snapshot {} published, replacing {}", tslName, published, loaded);
            loadTSL();
        }
    }

    // A snapshot is loaded only when its directory is there (a pointer at a missing one must not take the startup
    // refresh down) and, with snapshotMaxAge set, when its manifest says it is young enough.
    private boolean acceptable(String snapshotId, Path directory) {
        String reason = null;
        if (!Files.isDirectory(directory)) {
            reason = "its directory " + directory + " does not exist";
        } else if (configurationProperties.getSnapshotMaxAge() != null) {
            Instant oldest = Instant.now().minus(configurationProperties.getSnapshotMaxAge());
            Instant createdAt = snapshotStore.manifest(snapshotId).map(TSLSnapshotManifest::createdAt).orElse(null);
            if (createdAt == null) {
                reason = "its manifest is missing or unreadable and snapshotMaxAge is set";
            } else if (createdAt.isBefore(oldest)) {
                reason = "it was created " + createdAt + ", older than snapshotMaxAge "
                        + configurationProperties.getSnapshotMaxAge();
            }
        }
        if (reason == null) {
            refusedSnapshotId = null;
            return true;
        }
        if (snapshotId.equals(refusedSnapshotId)) {
            log.debug("'{}' TSL: still refusing snapshot {}: {}", tslName, snapshotId, reason);
        } else {
            log.warn("'{}' TSL: refusing snapshot {}: {}; keeping the loaded state", tslName, snapshotId, reason);
            refusedSnapshotId = snapshotId;
        }
        return false;
    }

    private void record(String snapshotId) {
        state.set(new State(Instant.now(), trustedListSource.getNumberOfCertificates(),
                snapshotCertificatesByTerritory(trustedListSource), snapshotId));
    }

    /**
     * When the last refresh attempt finished, {@code null} until the first one. DSS does not throw on
     * download failures, so this says "attempted", not "succeeded"; {@link #getNumberOfCertificates()}
     * tells whether anything came out of it. Together they are what readiness is judged on.
     */
    public Instant getLastRefresh() {
        return state.get().lastRefresh();
    }

    /**
     * Id of the published snapshot the last refresh loaded; {@code null} outside snapshot mode or before the
     * first load.
     */
    public String getSnapshotId() {
        return state.get().snapshotId();
    }

    /**
     * Number of trust anchors this TSL held when its last refresh attempt finished, zero until then or
     * when the refresh left nothing (nothing can be validated). A snapshot on purpose: the DSS source
     * is emptied and refilled while a refresh runs, so reading it live would flicker. With
     * {@code loadFromCache} a pod that started with an empty cache stays at zero until the cache is
     * filled and the next scheduled refresh runs.
     */
    public int getNumberOfCertificates() {
        return state.get().numberOfCertificates();
    }

    /**
     * Trust anchors per territory (scheme territory code of the trusted list they came from) when the last
     * refresh attempt finished: what the certificate source actually holds after DSS synchronized it, so it
     * follows DSS's own acceptance of lists and lists of lists (expiry, signature, LOTL-level rejection) and
     * keeps a list whose re-download failed exactly as long as DSS keeps its anchors. An anchor listed by
     * several trusted lists counts once per territory. Sorted by territory; empty until the first refresh.
     */
    public Map<String, Integer> getCertificatesByTerritory() {
        return state.get().certificatesByTerritory();
    }

    private static Map<String, Integer> snapshotCertificatesByTerritory(TrustedListsCertificateSource source) {
        Map<String, Set<String>> anchorsByTerritory = new TreeMap<>();
        for (CertificateToken token : source.getCertificates()) {
            for (TrustProperties trustProperties : source.getTrustServices(token)) {
                String territory = territoryOf(trustProperties.getTLInfo());
                if (territory != null) {
                    anchorsByTerritory.computeIfAbsent(territory, key -> new HashSet<>()).add(token.getDSSIdAsString());
                }
            }
        }
        Map<String, Integer> result = new TreeMap<>();
        anchorsByTerritory.forEach((territory, anchors) -> result.put(territory, anchors.size()));
        return Collections.unmodifiableMap(result);
    }

    private static String territoryOf(TLInfo trustedList) {
        if (trustedList == null) {
            return null;
        }
        ParsingInfoRecord parsing = trustedList.getParsingCacheInfo();
        return parsing == null ? null : parsing.getTerritory();
    }

    public LOTLSource europeanLOTL() {
        LOTLSource lotlSource = new LOTLSource();
        lotlSource.setUrl(configurationProperties.getUrl());

        lotlSource.setCertificateSource(keyStoreCertificateSource);
        lotlSource.setSigningCertificatesAnnouncementPredicate(new OfficialJournalSchemeInformationURI(configurationProperties.getOjUrl()));
        lotlSource.setPivotSupport(configurationProperties.isLotlPivotSupportEnabled());
        lotlSource.setLotlPredicate(new EULOTLOtherTSLPointer()
                .and(new XMLOtherTSLPointer())
        );

        if (!configurationProperties.getTrustedTerritories().isEmpty()) {
            Set<String> trustedTerritories = new HashSet<>();
            CollectionUtils.addAll(trustedTerritories, configurationProperties.getTrustedTerritories());
            lotlSource.setTlPredicate(new SchemeTerritoryOtherTSLPointer(trustedTerritories).and(new EUTLOtherTSLPointer()
                    .and(new XMLOtherTSLPointer()))
            );
        }
        return lotlSource;
    }

    public DSSFileLoader onlineLoader() {
        FileCacheDataLoader onlineFileLoader = new LastGoodCopyFileCacheDataLoader();
        cacheDirectory(onlineFileLoader, configurationProperties.getCacheDirectory());
        if (configurationProperties.getOnlineCacheExpirationTime() != null) {
            Duration cacheExpirationTime = configurationProperties.getOnlineCacheExpirationTime();
            onlineFileLoader.setCacheExpirationTime(cacheExpirationTime.toMillis());
        }
        onlineFileLoader.setDataLoader(createCommonsDataLoader());
        return onlineFileLoader;
    }

    private CommonsDataLoader createCommonsDataLoader() {
        CommonsDataLoader commonsDataLoader = new CommonsDataLoader();
        commonsDataLoader.setProxyConfig(proxyConfig);

        Resource truststoreResource = configurationProperties.getSslTruststorePath();
        if (truststoreResource == null) {
            truststoreResource = new ClassPathResource("tsl-ssl-truststore.p12", getClass().getClassLoader());
        }

        DSSDocument truststore;
        try (InputStream inputStream = truststoreResource.getInputStream()) {
            truststore = new InMemoryDocument(inputStream, truststoreResource.getFilename());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read TSL SSL truststore resource: " + truststoreResource, e);
        }
        
        commonsDataLoader.setSslTruststore(truststore);
        commonsDataLoader.setSslTruststoreType(configurationProperties.getSslTruststoreType());
        commonsDataLoader.setSslTruststorePassword(configurationProperties.getSslTruststorePassword().toCharArray());
        
        return commonsDataLoader;
    }

    public DSSFileLoader offlineLoader() {
        File cacheDirectory = configurationProperties.getCacheDirectory();
        return offlineLoader(cacheDirectory == null ? null : cacheDirectory.toPath());
    }

    private static DSSFileLoader offlineLoader(Path directory) {
        FileCacheDataLoader offlineFileLoader = new FileCacheDataLoader();
        cacheDirectory(offlineFileLoader, directory == null ? null : directory.toFile());
        offlineFileLoader.setCacheExpirationTime(Long.MAX_VALUE);
        offlineFileLoader.setDataLoader(new IgnoreDataLoader());
        return offlineFileLoader;
    }

    // Null keeps DSS's default, the JVM's temporary directory.
    private static void cacheDirectory(FileCacheDataLoader loader, File directory) {
        if (directory != null) {
            loader.setFileCacheDirectory(directory);
        }
    }

    @Autowired
    public void setProxyConfig (ProxyConfig proxyConfig){
        this.proxyConfig = proxyConfig;
    }

    @Autowired
    public void setTslValidationJobFactory(TSLValidationJobFactory tslValidationJobFactory) {
        this.tslValidationJobFactory = tslValidationJobFactory;
    }

    @Autowired
    public void setTslLoaderConfigurationProperties(TSLLoaderConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    @Autowired
    public void setKeyStoreCertificateSource(KeyStoreCertificateSource keyStoreCertificateSource) {
        this.keyStoreCertificateSource = keyStoreCertificateSource;
    }

    @Autowired
    public void setTrustedListsCertificateSource(TrustedListsCertificateSource trustedListSource) {
        this.trustedListSource = trustedListSource;
    }

}
