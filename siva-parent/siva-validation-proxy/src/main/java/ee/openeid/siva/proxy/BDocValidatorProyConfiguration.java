package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import ee.openeid.siva.validation.service.bdoc.BDOCValidationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BDocValidatorProyConfiguration {

    @Bean
    public XMLToJSONConverter converter() {
        return new XMLToJSONConverter();
    }

    @Bean
    public BDOCValidationService bdocValidationService() {
        BDOCValidationService bdocValidationService = new BDOCValidationService();
        return bdocValidationService;
    }

}