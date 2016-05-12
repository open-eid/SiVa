package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import ee.openeid.validation.service.pdf.PDFValidationService;
import eu.europa.esig.dss.validation.CertificateVerifier;
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

    @Bean
    public PDFValidationService pdfValidationService(CertificateVerifier certificateVerifier) {
        PDFValidationService PDFValidationService = new PDFValidationService();
        PDFValidationService.setCertificateVerifier(certificateVerifier);

        return PDFValidationService;
    }
}
