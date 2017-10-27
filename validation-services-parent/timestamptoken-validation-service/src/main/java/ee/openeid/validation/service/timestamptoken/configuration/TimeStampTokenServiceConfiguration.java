package ee.openeid.validation.service.timestamptoken.configuration;

import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({TimeStampTokenSignaturePolicyProperties.class})
public class TimeStampTokenServiceConfiguration {

    @Bean(name = "TimeStampTokenPolicyService")
    public SignaturePolicyService<ValidationPolicy> signaturePolicyService(TimeStampTokenSignaturePolicyProperties properties) {
        return new SignaturePolicyService<>(properties);
    }

} 
