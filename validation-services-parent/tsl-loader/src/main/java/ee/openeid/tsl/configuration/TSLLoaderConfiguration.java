package ee.openeid.tsl.configuration;

import ee.openeid.tsl.keystore.DSSKeyStoreFactoryBean;
import eu.europa.esig.dss.client.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.x509.crl.CRLSource;
import eu.europa.esig.dss.x509.ocsp.OCSPSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({
    TSLLoaderConfigurationProperties.class,
    TSLValidationKeystoreProperties.class
})
public class TSLLoaderConfiguration {

    private TSLValidationKeystoreProperties keystoreProperties;

    @Bean
    public DSSKeyStoreFactoryBean dssKeyStore() {
        DSSKeyStoreFactoryBean dssKeyStoreFactoryBean = new DSSKeyStoreFactoryBean();
        dssKeyStoreFactoryBean.setKeyStoreType(keystoreProperties.getType());
        dssKeyStoreFactoryBean.setKeyStoreFilename(keystoreProperties.getFilename());
        dssKeyStoreFactoryBean.setKeyStorePassword(keystoreProperties.getPassword());
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
    public TrustedListsCertificateSource trustedListSource(KeyStoreCertificateSource keyStoreCertificateSource) {
        TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();
        trustedListsCertificateSource.setDssKeyStore(keyStoreCertificateSource);
        return trustedListsCertificateSource;
    }

    @Autowired
    public void setKeystoreProperties(TSLValidationKeystoreProperties keystoreProperties) {
        this.keystoreProperties = keystoreProperties;
    }
}
