/*
 * Copyright 2017 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.sample.configuration;

import ee.openeid.siva.monitoring.configuration.MonitoringConfiguration;
import ee.openeid.siva.monitoring.indicator.UrlHealthIndicator;
import ee.openeid.siva.sample.ci.info.BuildInfo;
import ee.openeid.siva.sample.ci.info.FilesystemBuildInfoFileLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties({
    SivaRESTWebServiceConfigurationProperties.class,
    BuildInfoProperties.class,
    GoogleAnalyticsProperties.class
})
public class DemoApplicationConfiguration extends MonitoringConfiguration {
    private BuildInfoProperties properties;
    private SivaRESTWebServiceConfigurationProperties proxyProperties;

    public List<UrlHealthIndicator.ExternalLink> getDefaultExternalLinks() {
        return new ArrayList<UrlHealthIndicator.ExternalLink>() {{
            add(new UrlHealthIndicator.ExternalLink("sivaService", proxyProperties.getServiceHost() + DEFAULT_MONITORING_ENDPOINT, DEFAULT_TIMEOUT * 2));
        }};
    }

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        return restTemplate;
    }

    @Bean
    public BuildInfo displayBuildInfo() throws IOException {
        final FilesystemBuildInfoFileLoader buildInfoFileLoader = new FilesystemBuildInfoFileLoader();
        buildInfoFileLoader.setProperties(properties);
        return buildInfoFileLoader.loadBuildInfo();
    }

    @Autowired
    public void setProperties(final BuildInfoProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setProxyProperties(final SivaRESTWebServiceConfigurationProperties proxyProperties) {
        this.proxyProperties = proxyProperties;
    }
}
