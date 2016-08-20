package ee.openeid.siva.xroad.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({XROADValidationServiceProperties.class})
public class XROADValidationServiceConfiguration {
}
