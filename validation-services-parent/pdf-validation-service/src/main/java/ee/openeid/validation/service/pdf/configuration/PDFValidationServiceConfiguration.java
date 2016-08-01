package ee.openeid.validation.service.pdf.configuration;

import ee.openeid.validation.service.pdf.signature.policy.PDFSignaturePolicyService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PDFSignaturePolicyProperties.class)
public class PDFValidationServiceConfiguration {

    @Bean
    public PDFSignaturePolicyService pdfSignaturePolicyService(PDFSignaturePolicyProperties pdfPolicySettings) {
        return new PDFSignaturePolicyService(pdfPolicySettings);
    }

}
