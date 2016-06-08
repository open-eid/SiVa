package ee.openeid.siva.sample.configuration;

import ee.openeid.siva.sample.ci.info.BuildInfo;
import ee.openeid.siva.sample.ci.info.BuildInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({
    SivaConfigurationProperties.class,
    BuildInfoProperties.class
})
public class DemoApplicationConfiguration {

    @Autowired
    private BuildInfoProperties properties;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public BuildInfo displayBuildInfo() {
        BuildInfoService buildInfoService = new BuildInfoService();
        buildInfoService.setProperties(properties);
        return buildInfoService.loadBuildInfo();
    }
}
