/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

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
