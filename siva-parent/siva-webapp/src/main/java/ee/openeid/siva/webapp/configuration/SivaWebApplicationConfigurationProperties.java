package ee.openeid.siva.webapp.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("siva.webapp")
public class SivaWebApplicationConfigurationProperties {
    private boolean reportSignatureEnabled = false;
} 
