package ee.openeid.siva.sample.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "siva")
public class SivaConfigurationProperties {
    private String uploadDirectory;
    private String serviceUrl;
}
