package ee.openeid.siva.proxy.impl.configuration;

import ee.openeid.pdf.webservice.json.PDFValidationService;
import ee.openeid.pdf.webservice.json.ValidationService;
import ee.openeid.pdf.webservice.json.configuration.PDFValidationServiceConfiguration;
import ee.openeid.pdf.webservice.json.converter.XMLConverter;
import eu.europa.esig.dss.validation.CertificateVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PDFValidationServiceConfiguration.class)
public class PdfValidatorProxyConfiguration {
    @Bean
    public XMLConverter converter() {
        return new XMLConverter();
    }

    @Bean
    public ValidationService validationService(CertificateVerifier certificateVerifier) {
        PDFValidationService PDFValidationService = new PDFValidationService();
        PDFValidationService.setCertificateVerifier(certificateVerifier);

        return PDFValidationService;
    }
}
