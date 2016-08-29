package ee.openeid.siva.statistics.googleanalytics.configuration;

import ee.openeid.siva.statistics.googleanalytics.configuration.properties.GoogleAnalyticsMeasurementProtocolProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({GoogleAnalyticsMeasurementProtocolProperties.class})
public class GoogleAnalyticsMeasurementProtocolConfiguration {
}
