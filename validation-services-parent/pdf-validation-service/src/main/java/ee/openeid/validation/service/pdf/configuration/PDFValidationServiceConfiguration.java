package ee.openeid.validation.service.pdf.configuration;

import ee.openeid.validation.service.pdf.signature.policy.PDFSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PDFSignaturePolicySettings.class)
public class PDFValidationServiceConfiguration {

    @Bean
    public PDFSignaturePolicyService pdfSignaturePolicyService(PDFSignaturePolicySettings pdfPolicySettings) {
        return new PDFSignaturePolicyService(pdfPolicySettings);
    }

}
