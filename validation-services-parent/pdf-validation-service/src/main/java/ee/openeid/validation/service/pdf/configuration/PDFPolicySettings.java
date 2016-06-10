package ee.openeid.validation.service.pdf.configuration;

import ee.openeid.siva.validation.service.properties.PolicySettings;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "siva.pdf", ignoreUnknownFields = false)
public class PDFPolicySettings extends PolicySettings {
}
