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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TSLLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TSLLoader.class);

    private TSLValidationJob tslValidationJob;

    private TrustedListsCertificateSource trustedListSource;

    private TSLLoaderConfigurationProperties configurationProperties;

    private KeyStoreCertificateSource keyStoreCertificateSource;

    @PostConstruct
    public void init() {
        initTslValidatonJob();
        loadTSL();
    }

    private void initTslValidatonJob() {
        TSLValidationJob tslValidationJob = new TSLValidationJob();
        tslValidationJob.setDataLoader(new CommonsDataLoader());
        TSLRepository tslRepository = new TSLRepository();
        tslRepository.setTrustedListsCertificateSource(trustedListSource);
        tslValidationJob.setRepository(tslRepository);
        tslValidationJob.setLotlUrl(configurationProperties.getUrl());
        tslValidationJob.setLotlCode(configurationProperties.getCode());
        tslValidationJob.setDssKeyStore(keyStoreCertificateSource);
        tslValidationJob.setCheckLOTLSignature(true);
        tslValidationJob.setCheckTSLSignatures(true);
        this.tslValidationJob = tslValidationJob;
    }

    private void loadTSL() {
        if (configurationProperties.isLoadFromCache()) {
            LOGGER.info("Loading TSL from cache");
            tslValidationJob.initRepository();
            LOGGER.info("Finished loading TSL from cache");
        } else {
            LOGGER.info("Loading TSL over the network");
            tslValidationJob.refresh();
            LOGGER.info("Finished loading TSL from cache");
        }
    }

    @Scheduled(cron = "${tsl.loader.schedulerCron}")
    public void refreshTSL() {
        loadTSL();
    }

    @Autowired
    public void setTrustedListsCertificateSource(TrustedListsCertificateSource trustedListSource) {
        this.trustedListSource = trustedListSource;
    }

    @Autowired
    public void setTslLoaderConfigurationProperties(TSLLoaderConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    @Autowired
    public void setKeyStoreCertificateSource(KeyStoreCertificateSource keyStoreCertificateSource) {
        this.keyStoreCertificateSource = keyStoreCertificateSource;
    }

}
