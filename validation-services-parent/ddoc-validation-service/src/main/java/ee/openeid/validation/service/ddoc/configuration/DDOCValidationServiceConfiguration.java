package ee.openeid.validation.service.ddoc.configuration;

import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({DDOCValidationServiceProperties.class, DDOCSignaturePolicyProperties.class})
public class DDOCValidationServiceConfiguration {

    @Bean(name = "DDOCPolicyService")
    public SignaturePolicyService<ValidationPolicy> signaturePolicyService(DDOCSignaturePolicyProperties properties) {
        return new SignaturePolicyService<>(properties);
    }
}
