package ee.openeid.pdf.webservice.json.configuration;

import ee.openeid.pdf.webservice.json.converter.XMLConverter;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JSONValidationServiceConfiguration {

    @Bean
    public CertificateVerifier certificateVerifier() {
        return new CommonCertificateVerifier();
    }

    @Bean
    public XMLConverter converter() {
        return new XMLConverter();
    }
}
