package ee.openeid.validation.service.bdoc.configuration;

import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicySettings;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "siva.bdoc.signaturePolicy")
public class BDOCSignaturePolicySettings extends SignaturePolicySettings {
}
