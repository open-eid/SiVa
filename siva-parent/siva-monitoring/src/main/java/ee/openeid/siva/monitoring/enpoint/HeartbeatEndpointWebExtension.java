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
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;
import org.springframework.boot.health.actuate.endpoint.HttpCodeStatusMapper;
import org.springframework.boot.health.contributor.Status;

@RequiredArgsConstructor
@EndpointWebExtension(endpoint = HeartbeatEndpoint.class)
public class HeartbeatEndpointWebExtension {

    @NonNull
    private final HeartbeatEndpoint heartbeatEndpoint;
    @NonNull
    private final HttpCodeStatusMapper httpCodeStatusMapper;

    // Over HTTP the overall status comes with the status code the health endpoint would give it (503 for DOWN
    // and OUT_OF_SERVICE unless management.endpoint.health.status.http-mapping says otherwise), so that a load
    // balancer's health check can read the code and a client the body. Other exposures keep the bare status.
    @ReadOperation
    public WebEndpointResponse<Status> heartbeat() {
        Status status = heartbeatEndpoint.heartbeat();
        return new WebEndpointResponse<>(status, httpCodeStatusMapper.getStatusCode(status));
    }

}
