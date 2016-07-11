package ee.openeid.tsl.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tsl.loader")
public class TSLLoaderConfigurationProperties {
    private boolean loadFromCache = false;
    private String url = "https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl-mp.xml";
    private String code = "EU";
    private String schedulerCron = "0 0 3 * * ?";
}
