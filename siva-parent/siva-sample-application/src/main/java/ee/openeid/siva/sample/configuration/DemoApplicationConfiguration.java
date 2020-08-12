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
import lombok.SneakyThrows;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties({
        SivaRESTWebServiceConfigurationProperties.class,
        BuildInfoProperties.class
})
public class DemoApplicationConfiguration extends MonitoringConfiguration {
    private final BuildInfoProperties properties;
    private final SivaRESTWebServiceConfigurationProperties proxyProperties;

    @Autowired
    public DemoApplicationConfiguration(BuildInfoProperties properties, SivaRESTWebServiceConfigurationProperties proxyProperties) {
        this.properties = properties;
        this.proxyProperties = proxyProperties;
    }

    @Bean
    public UrlHealthIndicator link1(RestTemplateBuilder restTemplateBuilder) {
        UrlHealthIndicator.ExternalLink link = new UrlHealthIndicator.ExternalLink("sivaService", proxyProperties.getServiceHost() + DEFAULT_MONITORING_ENDPOINT, DEFAULT_TIMEOUT * 2);
        UrlHealthIndicator indicator = new UrlHealthIndicator();
        indicator.setExternalLink(link);

        RestTemplate restTemplate = getBaseSslRestTemplateBuilder(restTemplateBuilder)
                .setConnectTimeout(Duration.ofMillis(link.getTimeout()))
                .setReadTimeout(Duration.ofMillis(link.getTimeout()))
                .build();

        indicator.setRestTemplate(restTemplate);
        return indicator;
    }

    @SneakyThrows
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return getBaseSslRestTemplateBuilder(restTemplateBuilder)
                .additionalMessageConverters(new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @SneakyThrows
    private RestTemplateBuilder getBaseSslRestTemplateBuilder(RestTemplateBuilder restTemplateBuilder) {
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(
                        new ClassPathResource(proxyProperties.getTrustStore()).getURL(),
                        proxyProperties.getTrustStorePassword().toCharArray())
                .build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();

        return restTemplateBuilder.requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @Bean
    public BuildInfo displayBuildInfo() throws IOException {
        final FilesystemBuildInfoFileLoader buildInfoFileLoader = new FilesystemBuildInfoFileLoader();
        buildInfoFileLoader.setProperties(properties);
        return buildInfoFileLoader.loadBuildInfo();
    }
}
