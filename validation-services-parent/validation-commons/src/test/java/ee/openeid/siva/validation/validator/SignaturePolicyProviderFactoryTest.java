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
import ee.openeid.siva.validation.configuration.SignaturePolicyDocumentLoaderConfiguration;
import ee.openeid.siva.validation.configuration.SignaturePolicyDocumentLoaderProperties;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.client.http.Protocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.unit.DataSize;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

class SignaturePolicyProviderFactoryTest {

    private static final int MAX_SIZE = 1024;
    private static final byte[] POLICY = "policy document".getBytes(StandardCharsets.UTF_8);

    private String baseUrl;
    private SignaturePolicyProviderFactory signaturePolicyProviderFactory;

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .configureStaticDsl(true)
            .failOnUnmatchedRequests(true)
            .build();

    @BeforeEach
    void setUp() {
        baseUrl = wireMock.baseUrl();
        signaturePolicyProviderFactory = createFactory(List.of(Protocol.HTTP, Protocol.HTTPS), Duration.ofSeconds(5));
    }

    @Test
    void create_WhenCalledTwice_ReturnsNewInstances() {
        assertThat(signaturePolicyProviderFactory.create(), not(sameInstance(signaturePolicyProviderFactory.create())));
    }

    @Test
    void getSignaturePolicyByUrl_WhenHttpUrl_ReturnsDocument() {
        stubFor(get("/policy").willReturn(ok().withBody(POLICY)));

        assertThat(DSSUtils.toByteArray(signaturePolicyProviderFactory.create().getSignaturePolicyByUrl(baseUrl + "/policy")), is(POLICY));
    }

    @Test
    void getSignaturePolicyByUrl_WhenFileUrl_ReturnsNull(@TempDir Path tempDir) throws IOException {
        String url = writePolicyFile(tempDir);

        assertThat(signaturePolicyProviderFactory.create().getSignaturePolicyByUrl(url), nullValue());
    }

    @Test
    void getSignaturePolicyByUrl_WhenFileProtocolIsAccepted_ReturnsDocument(@TempDir Path tempDir) throws IOException {
        String url = writePolicyFile(tempDir);
        SignaturePolicyProviderFactory fileFactory = createFactory(List.of(Protocol.FILE), Duration.ofSeconds(5));

        assertThat(DSSUtils.toByteArray(fileFactory.create().getSignaturePolicyByUrl(url)), is(POLICY));
    }

    @Test
    void getSignaturePolicyByUrl_WhenResponseIsExactlyMaxSize_ReturnsDocument() {
        byte[] body = new byte[MAX_SIZE];
        Arrays.fill(body, (byte) 'a');
        stubFor(get("/exact").willReturn(ok().withBody(body)));

        assertThat(DSSUtils.toByteArray(signaturePolicyProviderFactory.create().getSignaturePolicyByUrl(baseUrl + "/exact")), is(body));
    }

    @Test
    void getSignaturePolicyByUrl_WhenResponseIsOverMaxSize_ReturnsNull() {
        stubFor(get("/large").willReturn(ok().withBody(new byte[MAX_SIZE + 1])));

        assertThat(signaturePolicyProviderFactory.create().getSignaturePolicyByUrl(baseUrl + "/large"), nullValue());
    }

    @Test
    void getSignaturePolicyByUrl_WhenResponseIsSlowerThanReadTimeout_ReturnsNull() {
        stubFor(get("/slow").willReturn(ok().withBody(POLICY).withFixedDelay(2000)));
        SignaturePolicyProviderFactory slowFactory = createFactory(List.of(Protocol.HTTP, Protocol.HTTPS), Duration.ofMillis(200));

        assertThat(slowFactory.create().getSignaturePolicyByUrl(baseUrl + "/slow"), nullValue());
    }

    private static String writePolicyFile(Path directory) throws IOException {
        return Files.write(directory.resolve("policy.der"), POLICY).toUri().toString();
    }

    private static SignaturePolicyProviderFactory createFactory(List<Protocol> acceptedProtocols, Duration readTimeout) {
        SignaturePolicyDocumentLoaderProperties properties = new SignaturePolicyDocumentLoaderProperties();
        properties.setAcceptedProtocols(acceptedProtocols);
        properties.setConnectTimeout(Duration.ofSeconds(1));
        properties.setReadTimeout(readTimeout);
        properties.setMaxResponseSize(DataSize.ofBytes(MAX_SIZE));
        return new SignaturePolicyDocumentLoaderConfiguration().signaturePolicyProviderFactory(properties);
    }
}
