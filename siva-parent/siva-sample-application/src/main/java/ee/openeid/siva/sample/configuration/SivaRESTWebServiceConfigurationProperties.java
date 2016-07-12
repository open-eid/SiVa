package ee.openeid.siva.sample.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "siva.service")
public class SivaRESTWebServiceConfigurationProperties {
    private static final String DEFAULT_SERVICE_URL = "http://localhost:8080/validate";
    private String serviceUrl = DEFAULT_SERVICE_URL;
}
