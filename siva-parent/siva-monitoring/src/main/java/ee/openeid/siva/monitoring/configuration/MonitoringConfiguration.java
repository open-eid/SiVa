/*
 * Copyright 2016 - 2026 Riigi Infosüsteemi Amet
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
import ee.openeid.siva.monitoring.enpoint.HeartbeatEndpointWebExtension;
import ee.openeid.siva.monitoring.enpoint.TrafficPortMonitoringController;
import ee.openeid.siva.monitoring.enpoint.VersionEndpoint;
import ee.openeid.siva.monitoring.indicator.ApplicationHealthIndicator;
import ee.openeid.siva.monitoring.observation.LoggingObservationHandler;
import ee.openeid.siva.monitoring.util.ManifestReader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.autoconfigure.endpoint.expose.EndpointExposure;
import org.springframework.boot.actuate.autoconfigure.web.server.ConditionalOnManagementPort;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.health.actuate.endpoint.HealthEndpointWebExtension;
import org.springframework.boot.health.actuate.endpoint.HttpCodeStatusMapper;
import org.springframework.context.annotation.Bean;


public abstract class MonitoringConfiguration {

    public static final String DEFAULT_MONITORING_ENDPOINT = "/monitoring/health";
    public static final int DEFAULT_TIMEOUT = 10000;

    // One log line per stage of a request (the siva.* observations), the metric side of them being Boot's own
    // meter handler; registered into the ObservationRegistry by Boot like any ObservationHandler bean.
    @Bean
    public LoggingObservationHandler loggingObservationHandler() {
        return new LoggingObservationHandler();
    }

    @Bean
    public ManifestReader manifestReader() {
        return new ManifestReader();
    }

    @Bean
    public ApplicationHealthIndicator health(ManifestReader manifestReader) {
        return new ApplicationHealthIndicator(manifestReader);
    }

    @Bean
    @ConditionalOnAvailableEndpoint(endpoint = HeartbeatEndpoint.class)
    public HeartbeatEndpoint heartbeatEndpoint(HealthEndpoint healthEndpoint) {
        return new HeartbeatEndpoint(healthEndpoint);
    }

    @Bean
    @ConditionalOnAvailableEndpoint(endpoint = HeartbeatEndpoint.class, exposure = EndpointExposure.WEB)
    public HeartbeatEndpointWebExtension heartbeatEndpointWebExtension(
            HeartbeatEndpoint heartbeatEndpoint,
            HttpCodeStatusMapper httpCodeStatusMapper) {
        return new HeartbeatEndpointWebExtension(heartbeatEndpoint, httpCodeStatusMapper);
    }

    @Bean
    @ConditionalOnAvailableEndpoint(endpoint = VersionEndpoint.class)
    public VersionEndpoint versionEndpoint(ManifestReader manifestReader) {
        return new VersionEndpoint(manifestReader);
    }

    // The documented monitoring URLs are public API: they stay on the application port when the actuator
    // moves to its own (management.server.port), served from the same endpoints.
    @Bean
    @ConditionalOnManagementPort(ManagementPortType.DIFFERENT)
    public TrafficPortMonitoringController trafficPortMonitoringController(
            ObjectProvider<HealthEndpointWebExtension> healthEndpointWebExtension,
            ObjectProvider<HeartbeatEndpointWebExtension> heartbeatEndpoint,
            ObjectProvider<VersionEndpoint> versionEndpoint) {
        return new TrafficPortMonitoringController(healthEndpointWebExtension, heartbeatEndpoint, versionEndpoint);
    }

}
