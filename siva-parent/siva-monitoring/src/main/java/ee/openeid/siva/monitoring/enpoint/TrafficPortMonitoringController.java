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

package ee.openeid.siva.monitoring.enpoint;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.endpoint.ApiVersion;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.WebServerNamespace;
import org.springframework.boot.health.actuate.endpoint.HealthDescriptor;
import org.springframework.boot.health.actuate.endpoint.HealthEndpointWebExtension;
import org.springframework.boot.health.contributor.Status;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(
        path = "${management.endpoints.web.base-path:/monitoring}",
        produces = {TrafficPortMonitoringController.ACTUATOR_V3_JSON, MediaType.APPLICATION_JSON_VALUE}
)
@RequiredArgsConstructor
public class TrafficPortMonitoringController {

    static final String ACTUATOR_V3_JSON = "application/vnd.spring-boot.actuator.v3+json";

    // Serves the documented monitoring URLs on the application port while the actuator runs on its own port.
    // Registered only when the management port differs (MonitoringConfiguration); with a shared port the actuator
    // serves /monitoring/health, /monitoring/heartbeat and /monitoring/version itself, under the same base path.
    // Every answer is the actuator's own: the health endpoint's web extension in the server namespace, so the
    // configured groups, detail visibility and status codes apply as on the management port, and the heartbeat
    // and version endpoints' values; an endpoint that is not enabled or not exposed answers 404 here too. The
    // body is the documented v3 one; a client asking for another actuator media type gets 406.
    @NonNull
    private final ObjectProvider<HealthEndpointWebExtension> healthEndpoint;
    @NonNull
    private final ObjectProvider<HeartbeatEndpointWebExtension> heartbeatEndpoint;
    @NonNull
    private final ObjectProvider<VersionEndpoint> versionEndpoint;

    @GetMapping({"/health", "/health/{*path}"})
    public ResponseEntity<HealthDescriptor> health(@PathVariable(required = false) String path) {
        HealthEndpointWebExtension endpoint = healthEndpoint.getIfAvailable();
        if (endpoint == null) {
            return ResponseEntity.notFound().build();
        }
        String[] segments = StringUtils.tokenizeToStringArray(path, "/");
        WebEndpointResponse<HealthDescriptor> response = endpoint.health(
                ApiVersion.LATEST, WebServerNamespace.SERVER, SecurityContext.NONE, segments);
        return ResponseEntity.status(response.getStatus()).body(response.getBody());
    }

    @GetMapping("/heartbeat")
    public ResponseEntity<Status> heartbeat() {
        HeartbeatEndpointWebExtension endpoint = heartbeatEndpoint.getIfAvailable();
        if (endpoint == null) {
            return ResponseEntity.notFound().build();
        }
        WebEndpointResponse<Status> response = endpoint.heartbeat();
        return ResponseEntity.status(response.getStatus()).body(response.getBody());
    }

    @GetMapping("/version")
    public ResponseEntity<Map<String, Object>> version() {
        VersionEndpoint endpoint = versionEndpoint.getIfAvailable();
        return endpoint == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(endpoint.version());
    }

}
