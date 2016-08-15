package ee.openeid.validation.service.ddoc.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "siva.ddoc")
public class DDOCValidationServiceProperties {
    private static final String DEFAULT_JDIGIDOC_CONF_FILE = "/jdigidoc.cfg";

    private String jdigidocConfigurationFile = DEFAULT_JDIGIDOC_CONF_FILE;
}
