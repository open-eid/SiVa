package ee.openeid.pdf.webservice.json.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath*:pdf-validator-webservice.xml")
public class PDFValidationServiceConfiguration {

}
