/*
 * Copyright 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.validation.configuration;

import ee.openeid.siva.validation.validator.AIASourceFactory;
import ee.openeid.siva.validation.validator.MaxSizeHttpClientResponseHandler;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AIALoaderProperties.class)
public class AIALoaderConfiguration {

    @Bean
    public AIASourceFactory aiaSourceFactory(AIALoaderProperties properties, ProxyConfig proxyConfig) {
        return () -> {
            CommonsDataLoader dataLoader = new CommonsDataLoader();
            dataLoader.setProxyConfig(proxyConfig);
            dataLoader.setTimeoutConnection(properties.connectTimeoutMillis());
            dataLoader.setTimeoutResponse(properties.readTimeoutMillis());
            dataLoader.setTimeoutSocket(properties.readTimeoutMillis());
            dataLoader.setHttpClientResponseHandler(new MaxSizeHttpClientResponseHandler(properties.maxResponseSizeBytes()));
            dataLoader.setRetryStrategy(new DefaultHttpRequestRetryStrategy(0, TimeValue.ZERO_MILLISECONDS));

            DefaultAIASource aiaSource = new DefaultAIASource(dataLoader);
            aiaSource.setAcceptedProtocols(properties.getAcceptedProtocols());
            return aiaSource;
        };
    }
}
