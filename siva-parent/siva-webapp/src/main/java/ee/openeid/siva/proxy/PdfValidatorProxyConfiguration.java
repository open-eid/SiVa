package ee.openeid.siva.proxy;

import ee.openeid.pdf.webservice.json.JSONValidationService;
import ee.openeid.pdf.webservice.json.ValidationService;
import ee.openeid.pdf.webservice.json.configuration.JSONValidationServiceConfiguration;
import eu.europa.esig.dss.validation.CertificateVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JSONValidationServiceConfiguration.class)
public class PdfValidatorProxyConfiguration {

    @Bean
    public ValidationService validationService(CertificateVerifier certificateVerifier) {
        JSONValidationService jsonValidationService = new JSONValidationService();
        jsonValidationService.setCertificateVerifier(certificateVerifier);

        return jsonValidationService;
    }
}
