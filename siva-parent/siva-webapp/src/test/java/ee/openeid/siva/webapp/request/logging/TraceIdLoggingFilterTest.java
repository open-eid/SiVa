/*
 * Copyright 2024 - 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.webapp.request.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.MDC;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class TraceIdLoggingFilterTest {

    private static final String TRACE_ID = "4bf92f3577b34da6a3ce929d0e0e4736";
    private static final String TRACEPARENT = "00-" + TRACE_ID + "-00f067aa0ba902b7-01";
    private static final String APM_ID_PATTERN = "[0-9a-f]{32}";

    private final MdcCapturingController controller = new MdcCapturingController();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(controller).addFilters(new TraceIdLoggingFilter()).build();
    }

    @AfterEach
    void clearMdc() {
        MDC.remove("trace.id");
    }

    @Test
    void traceparentTraceIdIsHonouredPutInMdcAndEchoed() throws Exception {
        mockMvc.perform(get("/probe").header("traceparent", TRACEPARENT))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Request-ID", TRACE_ID));

        assertThat(controller.mdcTraceId, equalTo(TRACE_ID));
        assertThat(controller.requestIdAttribute, equalTo(TRACE_ID));
    }

    @Test
    void traceparentOfALaterVersionWithExtraFieldsIsHonoured() throws Exception {
        mockMvc.perform(get("/probe").header("traceparent", "01-" + TRACE_ID + "-00f067aa0ba902b7-01-future"))
                .andExpect(header().string("X-Request-ID", TRACE_ID));
    }

    @Test
    void requestIdWinsOverTraceparent() throws Exception {
        mockMvc.perform(get("/probe").header("traceparent", TRACEPARENT).header("X-Request-ID", "edge-42"))
                .andExpect(header().string("X-Request-ID", "edge-42"));

        assertThat(controller.mdcTraceId, equalTo("edge-42"));
    }

    @Test
    void edgeRequestIdIsLoggedAndEchoedWhenTheClientSentATraceparent() throws Exception {
        // The client propagates its trace, the edge in front of SiVa sets its own id and logs it: that id is the one
        // in SiVa's log lines and in the response.
        String edgeId = "7c0a1f3e9b2d4c6a8e1f0d2b3c4a5e6f";

        mockMvc.perform(get("/probe").header("traceparent", TRACEPARENT).header("X-Request-ID", edgeId))
                .andExpect(header().string("X-Request-ID", edgeId));

        assertThat(controller.mdcTraceId, equalTo(edgeId));
        assertThat(controller.requestIdAttribute, equalTo(edgeId));
        assertThat(controller.mdcTraceId, not(equalTo(TRACE_ID)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00-00000000000000000000000000000000-00f067aa0ba902b7-01",
            "00-" + TRACE_ID + "-0000000000000000-01",
            "ff-" + TRACE_ID + "-00f067aa0ba902b7-01",
            "00-" + TRACE_ID + "-00f067aa0ba902b7",
            "00-" + TRACE_ID + "-00f067aa0ba902b7-01-extra",
            "00-4BF92F3577B34DA6A3CE929D0E0E4736-00f067aa0ba902b7-01",
            "garbage"
    })
    void invalidTraceparentIsIgnoredAndAnIdIsMinted(String traceparent) throws Exception {
        MvcResult result = mockMvc.perform(get("/probe").header("traceparent", traceparent)).andReturn();

        assertThat(result.getResponse().getHeader("X-Request-ID"), matchesPattern(APM_ID_PATTERN));
        assertThat(controller.mdcTraceId, equalTo(result.getResponse().getHeader("X-Request-ID")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "edge-42",
            "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            "a",
            "req:1/2+3=",
            " padded "
    })
    void requestIdIsHonouredPutInMdcAndEchoed(String requestId) throws Exception {
        mockMvc.perform(get("/probe").header("X-Request-ID", requestId))
                .andExpect(header().string("X-Request-ID", requestId.trim()));

        assertThat(controller.mdcTraceId, equalTo(requestId.trim()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "has space",
            "semi;colon",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "tab\tin",
            "<script>"
    })
    void unusableRequestIdIsIgnoredAndAnIdIsMinted(String requestId) throws Exception {
        MvcResult result = mockMvc.perform(get("/probe").header("X-Request-ID", requestId)).andReturn();

        assertThat(result.getResponse().getHeader("X-Request-ID"), matchesPattern(APM_ID_PATTERN));
        assertThat(controller.mdcTraceId, equalTo(result.getResponse().getHeader("X-Request-ID")));
    }

    @Test
    void withoutHeadersAnIdIsMintedPutInMdcAndEchoed() throws Exception {
        MvcResult result = mockMvc.perform(get("/probe")).andReturn();

        String echoed = result.getResponse().getHeader("X-Request-ID");
        assertThat(echoed, matchesPattern(APM_ID_PATTERN));
        assertThat(controller.mdcTraceId, equalTo(echoed));
        assertThat(controller.requestIdAttribute, equalTo(echoed));
    }

    @Test
    void mintedIdsDiffer() throws Exception {
        String first = mockMvc.perform(get("/probe")).andReturn().getResponse().getHeader("X-Request-ID");
        String second = mockMvc.perform(get("/probe")).andReturn().getResponse().getHeader("X-Request-ID");

        assertThat(first, not(equalTo(second)));
    }

    @Test
    void mdcIsClearedAfterTheRequest() throws Exception {
        mockMvc.perform(get("/probe").header("traceparent", TRACEPARENT));

        assertThat(MDC.get("trace.id"), nullValue());
    }

    @Test
    void traceIdAlreadyInMdcWinsAndIsKeptAfterwards() throws Exception {
        MDC.put("trace.id", "agent-set");

        mockMvc.perform(get("/probe").header("traceparent", TRACEPARENT))
                .andExpect(header().string("X-Request-ID", "agent-set"));

        assertThat(controller.mdcTraceId, equalTo("agent-set"));
        assertThat(MDC.get("trace.id"), equalTo("agent-set"));
    }

    @RestController
    static class MdcCapturingController {

        String mdcTraceId;
        Object requestIdAttribute;

        @GetMapping("/probe")
        String probe(HttpServletRequest request) {
            mdcTraceId = MDC.get("trace.id");
            requestIdAttribute = request.getAttribute("requestId");
            return "ok";
        }
    }

}
