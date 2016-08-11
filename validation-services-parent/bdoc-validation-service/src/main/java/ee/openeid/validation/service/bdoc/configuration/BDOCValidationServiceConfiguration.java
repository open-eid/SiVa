package ee.openeid.validation.service.bdoc.configuration;

import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.validation.service.bdoc.signature.policy.BDOCSignaturePolicyService;
import eu.europa.esig.dss.tsl.ServiceInfo;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.apache.commons.lang.StringUtils;
import org.digidoc4j.Configuration;
import org.digidoc4j.TSLCertificateSource;
import org.digidoc4j.impl.bdoc.tsl.TSLCertificateSourceImpl;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableConfigurationProperties({
    BDOCSignaturePolicyProperties.class,
    BDOCValidationServiceProperties.class
})
public class BDOCValidationServiceConfiguration {

    @Bean
    public SignaturePolicyService signaturePolicyService(BDOCSignaturePolicyProperties properties) {
        return new SignaturePolicyService(properties);
    }

    @Bean
    public Configuration configuration(TrustedListsCertificateSource trustedListSource, BDOCSignaturePolicyService bdocSignaturePolicyService) {
        Configuration configuration = new Configuration();
        configuration.setValidationPolicy(bdocSignaturePolicyService.getAbsolutePath(StringUtils.EMPTY));
        TSLCertificateSource tslCertificateSource = new TSLCertificateSourceImpl();

        trustedListSource.getCertificates().forEach(certToken -> {
            ServiceInfo serviceInfo = null;
            if (!certToken.getAssociatedTSPS().isEmpty()) {
                serviceInfo = (ServiceInfo) certToken.getAssociatedTSPS().toArray()[0];
            }
            tslCertificateSource.addCertificate(certToken, serviceInfo);
        });
        configuration.setTSL(tslCertificateSource);
        return configuration;
    }
}
