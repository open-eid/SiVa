/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.proxy.configuration;

import ee.openeid.siva.proxy.http.RestProxyErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({ProxyConfigurationProperties.class})
public class ProxyServiceConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyServiceConfiguration.class);
    private ProxyConfigurationProperties properties;

    @Bean
    public RestTemplate restTemplate() {
        LOGGER.info("XRoad Validation service base URL: {}", properties.getXroadUrl());
        return new RestTemplateBuilder()
                .rootUri(properties.getXroadUrl())
                .errorHandler(responseErrorHandler())
                .build();
    }

    @Autowired
    public void setProperties(ProxyConfigurationProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ResponseErrorHandler responseErrorHandler() {
        return new RestProxyErrorHandler();
    }
}
