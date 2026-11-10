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

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.health.actuate.endpoint.HttpCodeStatusMapper;
import org.springframework.boot.health.contributor.Status;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

@ExtendWith(MockitoExtension.class)
class HeartbeatEndpointWebExtensionTest {

    @Mock
    private HeartbeatEndpoint heartbeatEndpoint;
    @Mock
    private HttpCodeStatusMapper httpCodeStatusMapper;
    @InjectMocks
    private HeartbeatEndpointWebExtension webExtension;

    @ParameterizedTest
    @MethodSource("allStatuses")
    void bodyIsTheEndpointsStatusAndCodeComesFromTheHealthEndpointsMapper(Status status) {
        Mockito.doReturn(status).when(heartbeatEndpoint).heartbeat();
        Mockito.doReturn(599).when(httpCodeStatusMapper).getStatusCode(status);

        WebEndpointResponse<Status> response = webExtension.heartbeat();

        assertThat(response.getBody(), is(sameInstance(status)));
        assertThat(response.getStatus(), is(599));
        Mockito.verify(heartbeatEndpoint).heartbeat();
        Mockito.verify(httpCodeStatusMapper).getStatusCode(status);
        Mockito.verifyNoMoreInteractions(heartbeatEndpoint, httpCodeStatusMapper);
    }

    private static Stream<Status> allStatuses() {
        return Stream.of(
                Status.DOWN,
                Status.OUT_OF_SERVICE,
                Status.UNKNOWN,
                Status.UP
        );
    }

}
