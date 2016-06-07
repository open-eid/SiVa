package ee.openeid.tsl.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tsl.loader")
public class TSLLoaderConfigurationProperties {
    private boolean loadFromCache;
    private String url;
    private String code;
    private String schedulerCron;
}
