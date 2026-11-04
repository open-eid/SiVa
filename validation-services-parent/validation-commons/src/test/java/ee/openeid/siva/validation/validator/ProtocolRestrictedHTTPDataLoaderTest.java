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

package ee.openeid.siva.validation.validator;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import eu.europa.esig.dss.spi.client.http.Protocol;
import eu.europa.esig.dss.spi.exception.DSSExternalResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProtocolRestrictedHTTPDataLoaderTest {

    private static final int MAX_SIZE = 1024;
    private static final byte[] CONTENT = "policy".getBytes(StandardCharsets.UTF_8);
    private static final byte[] SECRET = "secret file content".getBytes(StandardCharsets.UTF_8);

    private String baseUrl;
    private ProtocolRestrictedHTTPDataLoader dataLoader;

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .configureStaticDsl(true)
            .failOnUnmatchedRequests(true)
            .build();

    @BeforeEach
    void setUp() {
        baseUrl = wireMock.baseUrl();
        dataLoader = createDataLoader(List.of(Protocol.HTTP, Protocol.HTTPS));
    }

    @Test
    void get_WhenHttpUrl_ReturnsContent() {
        stubFor(get("/ok").willReturn(ok().withBody(CONTENT)));

        assertThat(dataLoader.get(baseUrl + "/ok"), is(CONTENT));
    }

    @Test
    void get_WhenNoProtocolsAccepted_RejectsHttpWithoutRequest() {
        String url = baseUrl + "/ok";
        ProtocolRestrictedHTTPDataLoader dataLoader = createDataLoader(List.of());

        DSSExternalResourceException exception = assertThrows(
                DSSExternalResourceException.class,
                () -> dataLoader.get(url)
        );

        assertThat(exception.getMessage(), is("The requested url protocol is not supported: " + url));
        verify(0, anyRequestedFor(anyUrl()));
    }

    @ParameterizedTest
    @ValueSource(ints = {301, 302, 303, 307})
    void get_WhenRedirectedToFileUrl_DoesNotReturnFileContent(int status, @TempDir Path tempDir) throws IOException {
        Path secretFile = Files.write(tempDir.resolve("secret.txt"), SECRET);
        stubFor(get("/redirect").willReturn(aResponse().withStatus(status).withHeader("Location", secretFile.toUri().toString())));

        byte[] content = dataLoader.get(baseUrl + "/redirect");

        assertThat(content, is(new byte[0]));
        verify(1, getRequestedFor(urlEqualTo("/redirect")));
    }

    @ParameterizedTest
    @ValueSource(ints = {301, 302, 303, 307})
    void get_WhenRedirectedToHttpUrl_FollowsRedirect(int status) {
        stubFor(get("/redirect").willReturn(aResponse().withStatus(status).withHeader("Location", baseUrl + "/policy")));
        stubFor(get("/policy").willReturn(ok().withBody(CONTENT)));

        assertThat(dataLoader.get(baseUrl + "/redirect"), is(CONTENT));
        verify(1, getRequestedFor(urlEqualTo("/redirect")));
        verify(1, getRequestedFor(urlEqualTo("/policy")));
    }

    @Test
    void get_WhenRedirectChainEndsAtFileUrl_DoesNotReturnFileContent(@TempDir Path tempDir) throws IOException {
        Path secretFile = Files.write(tempDir.resolve("secret.txt"), SECRET);
        stubFor(get("/start").willReturn(aResponse().withStatus(302).withHeader("Location", baseUrl + "/middle")));
        stubFor(get("/middle").willReturn(aResponse().withStatus(302).withHeader("Location", secretFile.toUri().toString())));

        assertThat(dataLoader.get(baseUrl + "/start"), is(new byte[0]));
        verify(1, getRequestedFor(urlEqualTo("/start")));
        verify(1, getRequestedFor(urlEqualTo("/middle")));
    }

    private static ProtocolRestrictedHTTPDataLoader createDataLoader(List<Protocol> acceptedProtocols) {
        ProtocolRestrictedHTTPDataLoader dataLoader = new ProtocolRestrictedHTTPDataLoader(acceptedProtocols);
        dataLoader.setConnectTimeout(1000);
        dataLoader.setReadTimeout(5000);
        dataLoader.setMaxInputSize(MAX_SIZE);
        return dataLoader;
    }
}
