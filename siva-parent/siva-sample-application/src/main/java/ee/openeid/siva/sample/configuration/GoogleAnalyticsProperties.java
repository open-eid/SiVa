package ee.openeid.siva.sample.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "siva.google-analytics")
public class GoogleAnalyticsProperties {
    private String trackingId;
}
