package ee.openeid.siva.proxy.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("siva.proxy")
public class ProxyConfigurationProperties {
    private String xroadUrl = "http://localhost:8081";
}
