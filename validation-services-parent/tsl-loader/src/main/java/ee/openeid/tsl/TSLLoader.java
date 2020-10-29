/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.tsl.function.EULOTLOtherTSLPointer;
import eu.europa.esig.dss.tsl.function.OfficialJournalSchemeInformationURI;
import eu.europa.esig.dss.tsl.function.SchemeTerritoryOtherTSLPointer;
import eu.europa.esig.dss.tsl.function.XMLOtherTSLPointer;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component("tslLoader")
public class TSLLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TSLLoader.class);
    private TSLValidationJobFactory tslValidationJobFactory;
    private TLValidationJob tslValidationJob;
    private TSLLoaderConfigurationProperties configurationProperties;
    private TrustedListsCertificateSource trustedListSource;
    private KeyStoreCertificateSource keyStoreCertificateSource;

    @PostConstruct
    public void init() {
        initTslValidationJob();
        loadTSL();
    }

    private void initTslValidationJob() {
        tslValidationJob = tslValidationJobFactory.createValidationJob();
        tslValidationJob.setOnlineDataLoader(onlineLoader());
        tslValidationJob.setOfflineDataLoader(offlineLoader());
        tslValidationJob.setTrustedListCertificateSource(trustedListSource);
        tslValidationJob.setListOfTrustedListSources(europeanLOTL());
    }

    void loadTSL() {
        if (configurationProperties.isLoadFromCache()) {
            LOGGER.info("Loading TSL from cache");
            tslValidationJob.offlineRefresh();
            LOGGER.info("Finished loading TSL from cache");
        } else {
            LOGGER.info("Loading TSL over the network");
            tslValidationJob.onlineRefresh();
            LOGGER.info("Finished loading TSL over the network");
        }
    }

    public LOTLSource europeanLOTL() {
        LOTLSource lotlSource = new LOTLSource();
        lotlSource.setUrl(configurationProperties.getUrl());

        lotlSource.setCertificateSource(keyStoreCertificateSource);
        lotlSource.setSigningCertificatesAnnouncementPredicate(new OfficialJournalSchemeInformationURI(configurationProperties.getOjUrl()));
        lotlSource.setPivotSupport(false);
        Set<String> trustedTerritories = new HashSet<>();
        if (!configurationProperties.getTrustedTerritories().isEmpty()) {
            CollectionUtils.addAll(trustedTerritories, configurationProperties.getTrustedTerritories());
        }
        lotlSource.setLotlPredicate(new EULOTLOtherTSLPointer()
                .and(new XMLOtherTSLPointer())
                .and(new SchemeTerritoryOtherTSLPointer(trustedTerritories))
        );
        return lotlSource;
    }

    public DSSFileLoader onlineLoader() {
        FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader();
        onlineFileLoader.setDataLoader(createCommonsDataLoader());
        return onlineFileLoader;
    }

    private CommonsDataLoader createCommonsDataLoader() {
        CommonsDataLoader commonsDataLoader = new CommonsDataLoader();
        if (configurationProperties.getSslTruststorePath() != null) {
            String documentPath = Objects.requireNonNull(getClass().getClassLoader()
                    .getResource(configurationProperties.getSslTruststorePath())).getPath();
            DSSDocument truststore = new FileDocument(documentPath);
            commonsDataLoader.setSslTruststore(truststore);
        }
        commonsDataLoader.setSslTruststoreType(configurationProperties.getSslTruststoreType());
        commonsDataLoader.setSslTruststorePassword(configurationProperties.getSslTruststorePassword());
        return commonsDataLoader;
    }

    public DSSFileLoader offlineLoader() {
        FileCacheDataLoader offlineFileLoader = new FileCacheDataLoader();
        offlineFileLoader.setCacheExpirationTime(Long.MAX_VALUE);
        offlineFileLoader.setDataLoader(new IgnoreDataLoader());
        return offlineFileLoader;
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
