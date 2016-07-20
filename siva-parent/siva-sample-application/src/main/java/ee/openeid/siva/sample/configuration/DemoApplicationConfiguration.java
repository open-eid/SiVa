package ee.openeid.siva.sample.configuration;

import ee.openeid.siva.sample.ci.info.BuildInfo;
import ee.openeid.siva.sample.ci.info.FilesystemBuildInfoFileLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.io.IOException;
import java.nio.charset.Charset;

@Configuration
@EnableConfigurationProperties({
    SivaRESTWebServiceConfigurationProperties.class,
    BuildInfoProperties.class,
    GoogleAnalyticsProperties.class
})
public class DemoApplicationConfiguration {
    private BuildInfoProperties properties;

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        return restTemplate;
    }

    @Bean
    public Observable<BuildInfo> displayBuildInfo() throws IOException {
        final FilesystemBuildInfoFileLoader buildInfoFileLoader = new FilesystemBuildInfoFileLoader();
        buildInfoFileLoader.setProperties(properties);
        return buildInfoFileLoader.loadBuildInfo();
    }

    @Autowired
    public void setProperties(final BuildInfoProperties properties) {
        this.properties = properties;
    }
}
