package ee.openeid.siva.xroad.configuration;

import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({XROADValidationServiceProperties.class, XROADSignaturePolicyProperties.class})
public class XROADValidationServiceConfiguration {

    @Bean(name = "XROADPolicyService")
    public SignaturePolicyService<ValidationPolicy> signaturePolicyService(XROADSignaturePolicyProperties properties) {
        return new SignaturePolicyService<>(properties);
    }
}
