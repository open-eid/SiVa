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
import eu.europa.esig.dss.model.tsl.LOTLInfo;
import eu.europa.esig.dss.model.tsl.ParsingInfoRecord;
import eu.europa.esig.dss.model.tsl.TLInfo;
import eu.europa.esig.dss.model.tsl.TLValidationJobSummary;
import eu.europa.esig.dss.model.tsl.ValidationInfoRecord;
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

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Slf4j
@RequiredArgsConstructor
public class TSLLoader {

    @Getter
    private final @NonNull String tslName;

    /**
     * When the last refresh attempt finished, {@code null} until the first one. DSS does not throw on
     * download failures, so this says "attempted", not "succeeded"; {@link #getNumberOfCertificates()}
     * tells whether anything came out of it. Together they are what readiness is judged on.
     */
    @Getter
    private volatile Instant lastRefresh;
    private volatile int numberOfCertificates;
    private volatile Map<String, Integer> certificatesByTerritory = Map.of();

    private TSLValidationJobFactory tslValidationJobFactory;
    private TLValidationJob tslValidationJob;
    private TSLLoaderConfigurationProperties configurationProperties;
    private TrustedListsCertificateSource trustedListSource;
    private KeyStoreCertificateSource keyStoreCertificateSource;
    private ProxyConfig proxyConfig;

    @PostConstruct
    public void init() {
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
        numberOfCertificates = trustedListSource.getNumberOfCertificates();
        certificatesByTerritory = snapshotCertificatesByTerritory(trustedListSource.getSummary());
        lastRefresh = Instant.now();
    }

    /**
     * Number of trust anchors this TSL held when its last refresh attempt finished, zero until then or
     * when the refresh left nothing (nothing can be validated). A snapshot on purpose: the DSS source
     * is emptied and refilled while a refresh runs, so reading it live would flicker. With
     * {@code loadFromCache} a pod that started with an empty cache stays at zero until the cache is
     * filled and the next scheduled refresh runs.
     */
    public int getNumberOfCertificates() {
        return numberOfCertificates;
    }

    /**
     * Trust anchors per territory (scheme territory code) when the last refresh attempt finished, for every
     * trusted list this loader follows directly or through a list of trusted lists. A list counts only when it
     * parsed, has a next-update date in the future and its signature did not fail validation, the conditions
     * under which {@link ExpirationAndSignatureCheckStrategy} lets its anchors into the certificate source. A list whose
     * latest download failed keeps its previous parsed state in the DSS cache, so it stays here until that
     * state is dropped; a list that never parsed is absent. Sorted by territory; empty until the first refresh.
     */
    public Map<String, Integer> getCertificatesByTerritory() {
        return certificatesByTerritory;
    }

    private static Map<String, Integer> snapshotCertificatesByTerritory(TLValidationJobSummary summary) {
        if (summary == null) {
            return Map.of();
        }
        List<TLInfo> trustedLists = new ArrayList<>();
        if (summary.getOtherTLInfos() != null) {
            trustedLists.addAll(summary.getOtherTLInfos());
        }
        if (summary.getLOTLInfos() != null) {
            for (LOTLInfo lotl : summary.getLOTLInfos()) {
                if (lotl.getTLInfos() != null) {
                    trustedLists.addAll(lotl.getTLInfos());
                }
            }
        }
        Date now = new Date();
        Map<String, Integer> result = new TreeMap<>();
        for (TLInfo trustedList : trustedLists) {
            ParsingInfoRecord parsing = trustedList.getParsingCacheInfo();
            ValidationInfoRecord validation = trustedList.getValidationCacheInfo();
            // The same acceptance rule as ExpirationAndSignatureCheckStrategy, which decides what reaches the
            // certificate source: no next-update date or one in the past means expired; a validation result,
            // when there is one, must be valid.
            if (parsing == null || !parsing.isResultExist() || parsing.getTerritory() == null
                    || parsing.getNextUpdateDate() == null || now.after(parsing.getNextUpdateDate())
                    || (validation != null && validation.isResultExist() && !validation.isValid())) {
                continue;
            }
            result.merge(parsing.getTerritory(), parsing.getCertNumber(), Integer::sum);
        }
        return Collections.unmodifiableMap(result);
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
        FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader();
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
        offlineFileLoader.setCacheExpirationTime(Long.MAX_VALUE);
        offlineFileLoader.setDataLoader(new IgnoreDataLoader());
        return offlineFileLoader;
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
