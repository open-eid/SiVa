package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import ee.openeid.validation.service.ddoc.DDOCValidationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DDocValidatorProxyConfiguration {

    @Bean
    public XMLToJSONConverter converter() {
        return new XMLToJSONConverter();
    }

    @Bean
    public DDOCValidationService ddocValidationService() {
        DDOCValidationService ddocValidationService = new DDOCValidationService();
        return ddocValidationService;
    }

}
