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
