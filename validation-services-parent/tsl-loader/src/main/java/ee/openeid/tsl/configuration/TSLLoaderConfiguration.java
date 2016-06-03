package ee.openeid.tsl.configuration;

import ee.openeid.tsl.CustomTSLValidationJob;
import ee.openeid.tsl.keystore.DSSKeyStoreFactoryBean;
import eu.europa.esig.dss.client.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.service.TSLRepository;
import eu.europa.esig.dss.tsl.service.TSLValidationJob;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.x509.crl.CRLSource;
import eu.europa.esig.dss.x509.ocsp.OCSPSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class TSLLoaderConfiguration {

    @Value("${keystore.type}")
    String keystoreType;

    @Value("${keystore.filename}")
    String keystoreFilename;

    @Value("${keystore.password}")
    String keystorePassword;

    @Value("${trusted.list.source.lotlUrl}")
    String lotlUrl;

    @Value("${trusted.list.source.lotlCode}")
    String lotlCode;

    @Bean
    public DSSKeyStoreFactoryBean dssKeyStore() {
        DSSKeyStoreFactoryBean dssKeyStoreFactoryBean = new DSSKeyStoreFactoryBean();
        dssKeyStoreFactoryBean.setKeyStoreType(keystoreType);
        dssKeyStoreFactoryBean.setKeyStoreFilename(keystoreFilename);
        dssKeyStoreFactoryBean.setKeyStorePassword(keystorePassword);
        return dssKeyStoreFactoryBean;
    }

    @Bean
    public CRLSource crlSource() {
        return new AlwaysFailingCRLSource();
    }

    @Bean
    public OCSPSource ocspSource() {
        return new AlwaysFailingOCSPSource();
    }


    @Bean
    public CommonCertificateVerifier certificateVerifier(TrustedListsCertificateSource trustedListSource, OCSPSource ocspSource, CRLSource crlSource) {
        return new CommonCertificateVerifier(trustedListSource, crlSource, ocspSource, new CommonsDataLoader());
    }

    @Bean
    public TSLValidationJob tslValidationJob(CommonsDataLoader dataLoader, TSLRepository tslRepository, KeyStoreCertificateSource keyStoreCertificateSource) {
        CustomTSLValidationJob tslValidationJob = new CustomTSLValidationJob();
        tslValidationJob.setDataLoader(dataLoader);
        tslValidationJob.setRepository(tslRepository);
        tslValidationJob.setLotlUrl(lotlUrl);
        tslValidationJob.setLotlCode(lotlCode);
        tslValidationJob.setDssKeyStore(keyStoreCertificateSource);
        tslValidationJob.setCheckLOTLSignature(true);
        tslValidationJob.setCheckTSLSignatures(true);
        return tslValidationJob;
    }

    @Bean
    public CommonsDataLoader dataLoader() {
        return new CommonsDataLoader();
    }

    @Bean
    public TSLRepository tslRepository(TrustedListsCertificateSource trustedListSource) {
        TSLRepository tslRepository = new TSLRepository();
        tslRepository.setTrustedListsCertificateSource(trustedListSource);
        return tslRepository;
    }

    @Bean
    public TrustedListsCertificateSource trustedListSource(KeyStoreCertificateSource keyStoreCertificateSource) {
        TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();
        trustedListsCertificateSource.setDssKeyStore(keyStoreCertificateSource);
        return trustedListsCertificateSource;
    }


}
