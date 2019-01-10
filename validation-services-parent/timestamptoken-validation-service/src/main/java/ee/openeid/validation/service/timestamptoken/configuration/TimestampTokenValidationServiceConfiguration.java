package ee.openeid.validation.service.timestamptoken.configuration;


import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableConfigurationProperties({
       TimeStampTokenSignaturePolicyProperties.class
})
public class TimestampTokenValidationServiceConfiguration {

    @Bean(name = "timestampPolicyService")
    public SignaturePolicyService<ValidationPolicy> timestampSignaturePolicyService(TimeStampTokenSignaturePolicyProperties properties) {
        return new SignaturePolicyService<>(properties);
    }
}
