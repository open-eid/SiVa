package ee.openeid.tsl.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "siva.keystore")
public class TSLValidationKeystoreProperties {
    private String type = "JKS";
    private String filename = "siva-keystore.jks";
    private String password = "siva-keystore-password";
}
