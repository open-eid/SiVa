package ee.openeid.siva.sample.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "siva.build")
public class BuildInfoProperties {
    private static final String DEFAULT_BUILD_FILE_URL = "ci-build-info.yml";
    private String infoFile = DEFAULT_BUILD_FILE_URL;
}
