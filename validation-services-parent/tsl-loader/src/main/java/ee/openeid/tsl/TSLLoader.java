/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.tsl;

import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import eu.europa.esig.dss.client.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.service.TSLRepository;
import eu.europa.esig.dss.tsl.service.TSLValidationJob;
import eu.europa.esig.dss.x509.KeyStoreCertificateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
public class TSLLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TSLLoader.class);

    private TSLValidationJobFactory tslValidationJobFactory;
    private TSLValidationJob tslValidationJob;
    private TSLLoaderConfigurationProperties configurationProperties;
    private TrustedListsCertificateSource trustedListSource;
    private KeyStoreCertificateSource keyStoreCertificateSource;

    private static final List<String> DEFAULT_TRUESTED_TERRITORIES = Arrays.asList("AT", "BE", "BG", "CY", "CZ",/*"DE",*/"DK", "EE", "ES", "FI", "FR", "GR", "HU",/*"HR",*/"IE", "IS", "IT", "LT", "LU", "LV", "LI", "MT",/*"NO",*/"NL", "PL", "PT", "RO", "SE", "SI", "SK", "UK");

    @PostConstruct
    public void init() {
        initTslValidatonJob();
        loadTSL();
    }

    private void initTslValidatonJob() {
        tslValidationJob = tslValidationJobFactory.createValidationJob();
        tslValidationJob.setDataLoader(new CommonsDataLoader());
        TSLRepository tslRepository = new TSLRepository();
        tslRepository.setTrustedListsCertificateSource(trustedListSource);
        tslValidationJob.setRepository(tslRepository);
        tslValidationJob.setLotlUrl(configurationProperties.getUrl());
        tslValidationJob.setLotlCode(configurationProperties.getCode());
        tslValidationJob.setDssKeyStore(keyStoreCertificateSource);
        tslValidationJob.setFilterTerritories(DEFAULT_TRUESTED_TERRITORIES);
        tslValidationJob.setCheckLOTLSignature(true);
        tslValidationJob.setCheckTSLSignatures(true);
    }

    void loadTSL() {
        if (configurationProperties.isLoadFromCache()) {
            LOGGER.info("Loading TSL from cache");
            tslValidationJob.initRepository();
            LOGGER.info("Finished loading TSL from cache");
        } else {
            LOGGER.info("Loading TSL over the network");
            tslValidationJob.refresh();
            LOGGER.info("Finished loading TSL over the network");
        }
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
