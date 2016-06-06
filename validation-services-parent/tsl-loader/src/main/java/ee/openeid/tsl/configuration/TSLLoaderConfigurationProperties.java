package ee.openeid.tsl.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tsl")
public class TSLLoaderConfigurationProperties {
    private boolean loadAlwaysFromCache;
    private String url;
    private String code;
    private String schedulerCron;
}
