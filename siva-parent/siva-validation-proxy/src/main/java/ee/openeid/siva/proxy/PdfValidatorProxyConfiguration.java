package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("ee.openeid")
public class PdfValidatorProxyConfiguration {

    @Bean
    public XMLToJSONConverter converter() {
        return new XMLToJSONConverter();
    }

}
