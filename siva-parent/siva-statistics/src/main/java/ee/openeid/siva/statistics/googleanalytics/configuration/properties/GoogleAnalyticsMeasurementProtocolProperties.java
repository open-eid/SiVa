package ee.openeid.siva.statistics.googleanalytics.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "siva.statistics.google-analytics")
public class GoogleAnalyticsMeasurementProtocolProperties {

    private boolean enabled = false;
    private String url = "http://www.google-analytics.com/batch";
    private String trackingId = "UA-83206619-1";
    private String dataSourceName = "SiVa";

}
