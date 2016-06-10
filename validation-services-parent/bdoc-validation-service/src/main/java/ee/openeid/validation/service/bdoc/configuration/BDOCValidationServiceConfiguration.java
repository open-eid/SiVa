package ee.openeid.validation.service.bdoc.configuration;

import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.digidoc4j.Configuration;
import org.digidoc4j.impl.bdoc.tsl.TSLCertificateSourceImpl;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableConfigurationProperties(BDOCPolicySettings.class)
public class BDOCValidationServiceConfiguration {

    @Bean
    public Configuration configuration(TrustedListsCertificateSource trustedListSource, BDOCPolicySettings policySettings) {
        Configuration configuration = new Configuration();
        configuration.setValidationPolicy(policySettings.getAbsolutePath());
        TSLCertificateSourceImpl tslCertificateSource = new TSLCertificateSourceImpl();
        trustedListSource.getCertificates()
                .stream()
                .forEach(certToken -> tslCertificateSource.addTSLCertificate(certToken.getCertificate()));
        configuration.setTSL(tslCertificateSource);
        return configuration;
    }
}
