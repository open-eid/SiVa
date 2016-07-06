package ee.openeid.validation.service.bdoc.configuration;

import ee.openeid.validation.service.bdoc.signature.policy.BDOCSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
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
@EnableConfigurationProperties(BDOCSignaturePolicySettings.class)
public class BDOCValidationServiceConfiguration {

    @Bean
    public BDOCSignaturePolicyService bdocSignaturePolicyService(BDOCSignaturePolicySettings bdocPolicySettings) {
        return new BDOCSignaturePolicyService(bdocPolicySettings);
    }

    @Bean
    public Configuration configuration(TrustedListsCertificateSource trustedListSource, BDOCSignaturePolicyService bdocSignaturePolicyService) {
        Configuration configuration = new Configuration();
        configuration.setValidationPolicy(bdocSignaturePolicyService.getAbsolutePath(null));
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
