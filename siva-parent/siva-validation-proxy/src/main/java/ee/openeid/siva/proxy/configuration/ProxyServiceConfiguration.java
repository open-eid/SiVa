package ee.openeid.siva.proxy.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({ProxyConfigurationProperties.class})
public class ProxyServiceConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .build();
    }
}
