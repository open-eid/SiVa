package ee.openeid.validation.service.ddoc.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({DDOCValidationServiceProperties.class})
public class DDOCValidationServiceConfiguration {
}
