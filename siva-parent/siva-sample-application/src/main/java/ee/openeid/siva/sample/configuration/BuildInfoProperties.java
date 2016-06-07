package ee.openeid.siva.sample.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "siva.build")
public class BuildInfoProperties {
    private String infoFile;
}
