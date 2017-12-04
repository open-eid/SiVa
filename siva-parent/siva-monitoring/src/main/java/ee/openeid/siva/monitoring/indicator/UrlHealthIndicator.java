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
package ee.openeid.siva.monitoring.indicator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.client.RestTemplate;

public class UrlHealthIndicator extends AbstractHealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlHealthIndicator.class);
    public static final String RESPONSE_PARAM_NAME = "name";

    @Autowired
    private ExternalLink externalLink;

    @Autowired
    private RestTemplate restTemplate;

    public UrlHealthIndicator() { }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        try {
            builder.withDetail(RESPONSE_PARAM_NAME, externalLink.getName());
            HealthStatus response = restTemplate.getForObject(externalLink.getUrl(), UrlHealthIndicator.HealthStatus.class);
            LOGGER.debug(response.toString());
            setHealtStatus(builder, response);
        } catch (Exception e) {
            LOGGER.error("Failed to establish connection to '" + externalLink.getUrl() + "' > " + e.getMessage());
            builder.down();
        }
    }

    private void setHealtStatus(Health.Builder builder, HealthStatus health) {
        if (health.getStatus().equals(Status.UP.toString()))
            builder.up();
        else
            builder.down();
    }

    public void setExternalLink(ExternalLink externalLink) {
        this.externalLink = externalLink;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public ExternalLink getExternalLink() {
        return externalLink;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HealthStatus {
        private String status;

        public HealthStatus() { }

        public HealthStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class ExternalLink {

        private String name;

        private String url;

        private int timeout;

        public ExternalLink() {
        }

        public ExternalLink(String name, String url, int timeout) {
            this.name = name;
            this.url = url;
            this.timeout = timeout;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}


