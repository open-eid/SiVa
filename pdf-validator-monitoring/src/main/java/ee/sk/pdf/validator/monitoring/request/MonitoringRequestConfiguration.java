package ee.sk.pdf.validator.monitoring.request;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "monitoring")
public class MonitoringRequestConfiguration {
    private String host;
    private String path;
    private String requestInterval;

    public String getHost() {
        return host;
    }

    @SuppressWarnings("unused")
    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    @SuppressWarnings("unused")
    public void setPath(String path) {
        this.path = path;
    }

    public String getRequestInterval() {
        return requestInterval;
    }

    @SuppressWarnings("unused")
    public void setRequestInterval(String requestInterval) {
        this.requestInterval = requestInterval;
    }
}
