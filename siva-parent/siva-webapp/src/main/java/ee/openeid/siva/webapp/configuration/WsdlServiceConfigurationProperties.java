package ee.openeid.siva.webapp.configuration;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("siva.endpoint")
public class WsdlServiceConfigurationProperties {
    private String validationWebServiceUrl;
    private String hashcodeValidationWebService;
    private String dataFilesWebService;
}
