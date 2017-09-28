package ee.openeid.siva.signature.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({SignatureServiceConfigurationProperties.class})
public class SignatureServiceConfiguration {

} 
