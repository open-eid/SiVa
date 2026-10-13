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
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
     * another. {@link Snapshot#NONE} until the first refresh.
     */
    private final AtomicReference<Snapshot> snapshot = new AtomicReference<>(Snapshot.NONE);

    private record Snapshot(Instant lastRefresh, int numberOfCertificates, Map<String, Integer> certificatesByTerritory) {
        private static final Snapshot NONE = new Snapshot(null, 0, Map.of());
    }

    private TSLValidationJobFactory tslValidationJobFactory;
    private TLValidationJob tslValidationJob;
    private TSLLoaderConfigurationProperties configurationProperties;
    private TrustedListsCertificateSource trustedListSource;
    private KeyStoreCertificateSource keyStoreCertificateSource;
    private ProxyConfig proxyConfig;

    @PostConstruct
    public void init() {
        List<String> trusted = configurationProperties.getTrustedTerritories();
        List<String> mandatory = configurationProperties.getMandatoryTerritories();
        if (trusted != null && !trusted.isEmpty() && mandatory != null && !trusted.containsAll(mandatory)) {
            log.warn("'{}' TSL: mandatory territories {} are not all among the trusted territories {}; "
                    + "readiness can never be reached (codes are case-sensitive)", tslName, mandatory, trusted);
        }
        tslValidationJob = tslValidationJobFactory.createValidationJob();
        if (configurationProperties.isLoadFromCache()) {
            tslValidationJob.setOfflineDataLoader(offlineLoader());
        } else {
            tslValidationJob.setOnlineDataLoader(onlineLoader());
        }
        tslValidationJob.setTrustedListCertificateSource(trustedListSource);
        tslValidationJob.setListOfTrustedListSources(europeanLOTL());
        tslValidationJob.setSynchronizationStrategy(new ExpirationAndSignatureCheckStrategy());
    }

    void loadTSL() {
        if (configurationProperties.isLoadFromCache()) {
            log.info("Loading '{}' TSL from cache", tslName);
            tslValidationJob.offlineRefresh();
            log.info("Finished loading '{}' TSL from cache", tslName);
        } else {
            log.info("Loading '{}' TSL over the network", tslName);
            tslValidationJob.onlineRefresh();
            log.info("Finished loading '{}' TSL over the network", tslName);
        }
        snapshot.set(new Snapshot(Instant.now(), trustedListSource.getNumberOfCertificates(),
                snapshotCertificatesByTerritory(trustedListSource)));
    }

    /**
     * When the last refresh attempt finished, {@code null} until the first one. DSS does not throw on
     * download failures, so this says "attempted", not "succeeded"; {@link #getNumberOfCertificates()}
     * tells whether anything came out of it. Together they are what readiness is judged on.
     */
    public Instant getLastRefresh() {
        return snapshot.get().lastRefresh();
    }

    /**
     * Number of trust anchors this TSL held when its last refresh attempt finished, zero until then or
     * when the refresh left nothing (nothing can be validated). A snapshot on purpose: the DSS source
     * is emptied and refilled while a refresh runs, so reading it live would flicker. With
     * {@code loadFromCache} a pod that started with an empty cache stays at zero until the cache is
     * filled and the next scheduled refresh runs.
     */
    public int getNumberOfCertificates() {
        return snapshot.get().numberOfCertificates();
    }

    /**
     * Trust anchors per territory (scheme territory code of the trusted list they came from) when the last
     * refresh attempt finished: what the certificate source actually holds after DSS synchronized it, so it
     * follows DSS's own acceptance of lists and lists of lists (expiry, signature, LOTL-level rejection) and
     * keeps a list whose re-download failed exactly as long as DSS keeps its anchors. An anchor listed by
     * several trusted lists counts once per territory. Sorted by territory; empty until the first refresh.
     */
    public Map<String, Integer> getCertificatesByTerritory() {
        return snapshot.get().certificatesByTerritory();
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
        FileCacheDataLoader offlineFileLoader = new FileCacheDataLoader();
        cacheDirectory(offlineFileLoader, configurationProperties.getCacheDirectory());
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
