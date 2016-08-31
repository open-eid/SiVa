package ee.openeid.siva.statistics.googleanalytics.configuration;

import ee.openeid.siva.statistics.googleanalytics.configuration.properties.GoogleAnalyticsMeasurementProtocolProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GoogleAnalyticsMeasurementProtocolProperties.class})
public class GoogleAnalyticsMeasurementProtocolConfiguration {
}
