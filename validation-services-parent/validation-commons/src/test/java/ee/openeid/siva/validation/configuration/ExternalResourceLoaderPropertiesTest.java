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

package ee.openeid.siva.validation.configuration;

import ee.openeid.siva.validation.helper.AbstractValidationTest;
import eu.europa.esig.dss.spi.client.http.Protocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ExternalResourceLoaderPropertiesTest extends AbstractValidationTest {

    private static final String OUT_OF_RANGE_MESSAGE =
            "connect-timeout, read-timeout and max-response-size must be positive and at most 2147483647 (milliseconds or bytes)";
    private ExternalResourceLoaderProperties properties;

    @BeforeEach
    void setUpProperties() {
        properties = new AIALoaderProperties();
    }

    @Test
    void aiaLoaderProperties_WhenCreated_HasDefaults() {
        AIALoaderProperties properties = new AIALoaderProperties();

        assertThat(properties.getAcceptedProtocols(), equalTo(List.of(Protocol.HTTP, Protocol.HTTPS)));
        assertThat(properties.getConnectTimeout(), equalTo(Duration.ofSeconds(5)));
        assertThat(properties.getReadTimeout(), equalTo(Duration.ofSeconds(10)));
        assertThat(properties.getMaxResponseSize(), equalTo(DataSize.ofMegabytes(1)));
        validateAndExpectNoErrors(properties);
    }

    @Test
    void signaturePolicyDocumentLoaderProperties_WhenCreated_HasDefaults() {
        SignaturePolicyDocumentLoaderProperties properties = new SignaturePolicyDocumentLoaderProperties();

        assertThat(properties.getAcceptedProtocols(), equalTo(List.of(Protocol.HTTP, Protocol.HTTPS)));
        assertThat(properties.getConnectTimeout(), equalTo(Duration.ofSeconds(5)));
        assertThat(properties.getReadTimeout(), equalTo(Duration.ofSeconds(10)));
        assertThat(properties.getMaxResponseSize(), equalTo(DataSize.ofMegabytes(4)));
        validateAndExpectNoErrors(properties);
    }

    @Test
    void validate_WhenValuesAreSmallestValid_Succeeds() {
        properties.setAcceptedProtocols(List.of());
        properties.setConnectTimeout(Duration.ofMillis(1));
        properties.setReadTimeout(Duration.ofMillis(1));
        properties.setMaxResponseSize(DataSize.ofBytes(1));

        validateAndExpectNoErrors(properties);
    }

    @Test
    void validate_WhenAcceptedProtocolsIsNull_Fails() {
        properties.setAcceptedProtocols(null);

        validateAndExpectOneError(properties, "acceptedProtocols", "must not be null");
    }

    @Test
    void validate_WhenAcceptedProtocolsContainsNull_Fails() {
        properties.setAcceptedProtocols(Arrays.asList(Protocol.HTTP, null));

        validateAndExpectOneError(properties, "acceptedProtocols[1].<list element>", "must not be null");
    }

    @Test
    void validate_WhenConnectTimeoutIsNull_Fails() {
        properties.setConnectTimeout(null);

        validateAndExpectOneError(properties, "connectTimeout", "must not be null");
    }

    @Test
    void validate_WhenReadTimeoutIsNull_Fails() {
        properties.setReadTimeout(null);

        validateAndExpectOneError(properties, "readTimeout", "must not be null");
    }

    @Test
    void validate_WhenMaxResponseSizeIsNull_Fails() {
        properties.setMaxResponseSize(null);

        validateAndExpectOneError(properties, "maxResponseSize", "must not be null");
    }

    @ParameterizedTest
    @MethodSource("outOfRangeDurations")
    void validate_WhenConnectTimeoutIsOutOfRange_Fails(Duration connectTimeout) {
        properties.setConnectTimeout(connectTimeout);

        validateAndExpectOneError(properties, "inRange", OUT_OF_RANGE_MESSAGE);
    }

    @ParameterizedTest
    @MethodSource("outOfRangeDurations")
    void validate_WhenReadTimeoutIsOutOfRange_Fails(Duration readTimeout) {
        properties.setReadTimeout(readTimeout);

        validateAndExpectOneError(properties, "inRange", OUT_OF_RANGE_MESSAGE);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, Integer.MAX_VALUE + 1L})
    void validate_WhenMaxResponseSizeIsOutOfRange_Fails(long maxResponseSizeBytes) {
        properties.setMaxResponseSize(DataSize.ofBytes(maxResponseSizeBytes));

        validateAndExpectOneError(properties, "inRange", OUT_OF_RANGE_MESSAGE);
    }

    @Test
    void connectTimeoutMillis_WhenValueFitsInInt_ReturnsMilliseconds() {
        properties.setConnectTimeout(Duration.ofSeconds(7));

        assertThat(properties.connectTimeoutMillis(), equalTo(7000));
    }

    @Test
    void readTimeoutMillis_WhenValueFitsInInt_ReturnsMilliseconds() {
        properties.setReadTimeout(Duration.ofMillis(1500));

        assertThat(properties.readTimeoutMillis(), equalTo(1500));
    }

    @Test
    void maxResponseSizeBytes_WhenValueFitsInInt_ReturnsBytes() {
        properties.setMaxResponseSize(DataSize.ofKilobytes(512));

        assertThat(properties.maxResponseSizeBytes(), equalTo(524288));
    }

    static Stream<Duration> outOfRangeDurations() {
        return Stream.of(
                Duration.ZERO,
                Duration.ofSeconds(-1),
                Duration.ofNanos(1),
                Duration.ofMillis(Integer.MAX_VALUE + 1L));
    }
}
