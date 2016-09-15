package ee.openeid.validation.service.pdf.configuration;

import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PDFSignaturePolicyProperties.class)
public class PDFValidationServiceConfiguration {

    @Bean(name = "PDFPolicyService")
    public ConstraintLoadingSignaturePolicyService signaturePolicyService(PDFSignaturePolicyProperties properties) {
        return new ConstraintLoadingSignaturePolicyService(properties);
    }

}
