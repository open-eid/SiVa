package ee.openeid.validation.service.bdoc.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "siva.bdoc")
public class BDOCValidationServiceProperties {
    private String digidoc4JConfigurationFile;
}
