package ee.openeid.siva.signature.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("siva.signatureService")
public class SignatureServiceConfigurationProperties {
    private String keystorePath;
    private String keystorePassword;
    private String signatureLevel;
} 
