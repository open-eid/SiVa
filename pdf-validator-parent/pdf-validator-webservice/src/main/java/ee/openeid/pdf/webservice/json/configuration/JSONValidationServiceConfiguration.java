package ee.openeid.pdf.webservice.json.configuration;

import ee.openeid.pdf.webservice.json.converter.XMLConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath*:pdf-validator-webservice.xml")
public class JSONValidationServiceConfiguration {

    @Bean
    public XMLConverter converter() {
        return new XMLConverter();
    }
}
