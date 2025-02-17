/*
 * Copyright 2021 - 2025 Riigi Infosüsteemi Amet
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

import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfiguration {
    public static final String HTTPS_PROXY_PORT = "https.proxyPort";
    public static final String HTTPS_PROXY_HOST = "https.proxyHost";
    public static final String HTTP_PROXY_PORT = "http.proxyPort";
    public static final String HTTP_PROXY_HOST = "http.proxyHost";

    @Bean
    public ProxyConfig getProxyConfig() {
        ProxyConfig proxyConfig = new ProxyConfig();

        ProxyProperties httpProperties = createProxyProperties(HTTP_PROXY_HOST, HTTP_PROXY_PORT);
        ProxyProperties httpsProperties = createProxyProperties(HTTPS_PROXY_HOST, HTTPS_PROXY_PORT);
        if (httpProperties != null) {
            proxyConfig.setHttpProperties(httpProperties);
        }
        if (httpsProperties != null) {
            proxyConfig.setHttpsProperties(httpsProperties);
        }
        return proxyConfig;
    }

    private ProxyProperties createProxyProperties(String hostKey, String portKey) {
        ProxyProperties proxyProperties = new ProxyProperties();
        String proxyHost = System.getProperty(hostKey);
        String proxyPort = System.getProperty(portKey);
        if (proxyHost != null && proxyPort != null) {
            proxyProperties.setHost(proxyHost);
            proxyProperties.setPort(Integer.parseInt(proxyPort));
            return proxyProperties;
        }
        return null;
    }

}
