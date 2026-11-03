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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.endpoint.ApiVersion;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.WebServerNamespace;
import org.springframework.boot.health.actuate.endpoint.HealthDescriptor;
import org.springframework.boot.health.actuate.endpoint.HealthEndpointWebExtension;
import org.springframework.boot.health.actuate.endpoint.IndicatedHealthDescriptor;
import org.springframework.boot.health.contributor.Status;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class TrafficPortMonitoringControllerTest {

    @Mock
    private ObjectProvider<HealthEndpointWebExtension> healthEndpointProvider;
    @Mock
    private ObjectProvider<HeartbeatEndpointWebExtension> heartbeatEndpointProvider;
    @Mock
    private ObjectProvider<VersionEndpoint> versionEndpointProvider;
    @Mock
    private HealthEndpointWebExtension healthEndpoint;
    @Mock
    private HeartbeatEndpointWebExtension heartbeatEndpoint;
    @Mock
    private VersionEndpoint versionEndpoint;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new TrafficPortMonitoringController(
                healthEndpointProvider, heartbeatEndpointProvider, versionEndpointProvider)).build();
    }

    @Test
    void healthIsNotFoundWhenTheEndpointIsOff() throws Exception {
        Mockito.doReturn(null).when(healthEndpointProvider).getIfAvailable();

        mockMvc.perform(get("/monitoring/health"))
                .andExpect(status().isNotFound());
    }

    @Test
    void healthDelegatesToTheWebExtensionInTheServerNamespace() throws Exception {
        Mockito.doReturn(healthEndpoint).when(healthEndpointProvider).getIfAvailable();
        Mockito.doReturn(new WebEndpointResponse<>(descriptor(Status.DOWN), 503))
                .when(healthEndpoint).health(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(String[].class));

        mockMvc.perform(get("/monitoring/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(TrafficPortMonitoringController.ACTUATOR_V3_JSON))
                .andExpect(jsonPath("$.status").value("DOWN"));

        ArgumentCaptor<String[]> path = ArgumentCaptor.forClass(String[].class);
        Mockito.verify(healthEndpoint).health(Mockito.eq(ApiVersion.LATEST), Mockito.eq(WebServerNamespace.SERVER),
                Mockito.eq(SecurityContext.NONE), path.capture());
        assertThat(path.getValue(), is(emptyArray()));
    }

    @Test
    void healthGroupPathIsPassedAsSegments() throws Exception {
        Mockito.doReturn(healthEndpoint).when(healthEndpointProvider).getIfAvailable();
        Mockito.doReturn(new WebEndpointResponse<>(descriptor(Status.UP), 200))
                .when(healthEndpoint).health(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(String[].class));

        mockMvc.perform(get("/monitoring/health/readiness").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value("UP"));

        ArgumentCaptor<String[]> path = ArgumentCaptor.forClass(String[].class);
        Mockito.verify(healthEndpoint).health(Mockito.any(), Mockito.any(), Mockito.any(), path.capture());
        assertThat(path.getValue(), is(arrayContaining("readiness")));
    }

    @Test
    void unknownHealthPathAnswersNotFoundLikeTheActuator() throws Exception {
        Mockito.doReturn(healthEndpoint).when(healthEndpointProvider).getIfAvailable();
        Mockito.doReturn(new WebEndpointResponse<>(404))
                .when(healthEndpoint).health(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(String[].class));

        mockMvc.perform(get("/monitoring/health/nothing/here"))
                .andExpect(status().isNotFound());
    }

    @Test
    void heartbeatCarriesTheEndpointsStatusAndCode() throws Exception {
        Mockito.doReturn(heartbeatEndpoint).when(heartbeatEndpointProvider).getIfAvailable();
        Mockito.doReturn(new WebEndpointResponse<>(Status.DOWN, 503)).when(heartbeatEndpoint).heartbeat();

        mockMvc.perform(get("/monitoring/heartbeat"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().json("{\"status\":\"DOWN\"}"));
    }

    @Test
    void heartbeatIsNotFoundWhenTheEndpointIsOff() throws Exception {
        Mockito.doReturn(null).when(heartbeatEndpointProvider).getIfAvailable();

        mockMvc.perform(get("/monitoring/heartbeat"))
                .andExpect(status().isNotFound());
    }

    @Test
    void versionIsTheEndpointsBody() throws Exception {
        Mockito.doReturn(versionEndpoint).when(versionEndpointProvider).getIfAvailable();
        Mockito.doReturn(Map.of("version", "3.11.0")).when(versionEndpoint).version();

        mockMvc.perform(get("/monitoring/version"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"version\":\"3.11.0\"}"));
    }

    @Test
    void versionIsNotFoundWhenTheEndpointIsOff() throws Exception {
        Mockito.doReturn(null).when(versionEndpointProvider).getIfAvailable();

        mockMvc.perform(get("/monitoring/version"))
                .andExpect(status().isNotFound());
    }

    private static HealthDescriptor descriptor(Status status) {
        IndicatedHealthDescriptor descriptor = Mockito.mock(IndicatedHealthDescriptor.class);
        Mockito.doReturn(status).when(descriptor).getStatus();
        return descriptor;
    }

}
