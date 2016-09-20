package ee.openeid.siva.proxy.configuration;

import ee.openeid.siva.proxy.http.RestProxyErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({ProxyConfigurationProperties.class})
public class ProxyServiceConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyServiceConfiguration.class);
    private ProxyConfigurationProperties properties;

    @Bean
    public RestTemplate restTemplate() {
        LOGGER.info("XRoad Validation service base URL: {}", properties.getXroadUrl());
        return new RestTemplateBuilder()
                .rootUri(properties.getXroadUrl())
                .errorHandler(responseErrorHandler())
                .build();
    }

    @Autowired
    public void setProperties(ProxyConfigurationProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ResponseErrorHandler responseErrorHandler() {
        return new RestProxyErrorHandler();
    }
}
