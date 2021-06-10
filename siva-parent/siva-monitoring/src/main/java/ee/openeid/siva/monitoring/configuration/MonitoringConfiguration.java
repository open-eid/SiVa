/*
 * Copyright 2016 - 2021 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.monitoring.configuration;

import ee.openeid.siva.monitoring.enpoint.HeartbeatEndpoint;
import ee.openeid.siva.monitoring.indicator.ApplicationHealthIndicator;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletContext;

public abstract class MonitoringConfiguration {

    public static final String DEFAULT_MONITORING_ENDPOINT = "/monitoring/health";
    public static final int DEFAULT_TIMEOUT = 10000;

    @Bean
    public ApplicationHealthIndicator health(ServletContext context) {
        return new ApplicationHealthIndicator(context);
    }

    @Bean
    @ConditionalOnAvailableEndpoint(endpoint = HeartbeatEndpoint.class)
    public HeartbeatEndpoint heartbeatEndpoint(HealthEndpoint healthEndpoint) {
        return new HeartbeatEndpoint(healthEndpoint);
    }

}
