package ee.openeid.validation.service.pdf.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PDFPolicySettings.class)
public class PDFValidationServiceConfiguration {
}
