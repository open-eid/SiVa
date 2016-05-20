package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BDocValidatorProxyConfiguration {

    @Bean
    public XMLToJSONConverter converter() {
        return new XMLToJSONConverter();
    }

}
