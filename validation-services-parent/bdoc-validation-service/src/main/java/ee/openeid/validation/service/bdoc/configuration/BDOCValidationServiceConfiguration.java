package ee.openeid.validation.service.bdoc.configuration;

import eu.europa.esig.dss.tsl.ServiceInfo;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.digidoc4j.Configuration;
import org.digidoc4j.TSLCertificateSource;
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
        TSLCertificateSource tslCertificateSource = new TSLCertificateSourceImpl();

        trustedListSource.getCertificates()
                .stream()
                .forEach(certToken -> {
                    ServiceInfo serviceInfo = null;
                    if (certToken.getAssociatedTSPS().size() > 0) {
                        serviceInfo = (ServiceInfo) certToken.getAssociatedTSPS().toArray()[0];
                    }
                    tslCertificateSource.addCertificate(certToken, serviceInfo);
                });
        configuration.setTSL(tslCertificateSource);
        return configuration;
    }
}
