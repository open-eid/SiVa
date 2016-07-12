package ee.openeid.validation.service.pdf.configuration;

import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicySettings;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "siva.pdf.signaturePolicy")
public class PDFSignaturePolicySettings extends SignaturePolicySettings {
    
}
