package ee.openeid.siva.proxy;

import ee.openeid.pdf.webservice.json.PDFValidationService;
import ee.openeid.pdf.webservice.json.ValidationService;
import ee.openeid.pdf.webservice.json.configuration.PDFValidationServiceConfiguration;
import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import eu.europa.esig.dss.validation.CertificateVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PDFValidationServiceConfiguration.class)
public class PdfValidatorProxyConfiguration {
    @Bean
    public XMLToJSONConverter converter() {
        return new XMLToJSONConverter();
    }

    @Bean
    public ValidationService validationService(CertificateVerifier certificateVerifier) {
        PDFValidationService PDFValidationService = new PDFValidationService();
        PDFValidationService.setCertificateVerifier(certificateVerifier);

        return PDFValidationService;
    }
}
