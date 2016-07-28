package ee.openeid.validation.service.ddoc.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "siva.ddoc")
public class DDOCValidationServiceProperties {
    private String jdigidocConfigurationFile;
}
