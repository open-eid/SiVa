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

import eu.europa.esig.dss.spi.client.http.Protocol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.util.unit.DataSize;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalResourceLoaderPropertiesBindingTest {

    private static final String OUT_OF_RANGE_MESSAGE =
            "connect-timeout, read-timeout and max-response-size must be positive and at most 2147483647 (milliseconds or bytes)";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfiguration.class);

    @Test
    void context_WhenNothingIsConfigured_UsesDefaults() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            AIALoaderProperties aia = context.getBean(AIALoaderProperties.class);
            SignaturePolicyDocumentLoaderProperties policy = context.getBean(SignaturePolicyDocumentLoaderProperties.class);

            assertThat(aia.getAcceptedProtocols()).containsExactly(Protocol.HTTP, Protocol.HTTPS);
            assertThat(aia.getConnectTimeout()).isEqualTo(Duration.ofSeconds(5));
            assertThat(aia.getReadTimeout()).isEqualTo(Duration.ofSeconds(10));
            assertThat(aia.getMaxResponseSize()).isEqualTo(DataSize.ofMegabytes(1));
            assertThat(policy.getMaxResponseSize()).isEqualTo(DataSize.ofMegabytes(4));
        });
    }

    @Test
    void context_WhenValuesAreConfigured_BindsSupportedFormats() {
        contextRunner
                .withPropertyValues(
                        "siva.external-resources.aia.accepted-protocols=HTTPS",
                        "siva.external-resources.aia.connect-timeout=7s",
                        "siva.external-resources.aia.read-timeout=PT1.5S",
                        "siva.external-resources.aia.max-response-size=512KB",
                        "siva.external-resources.signature-policy-document.accepted-protocols=",
                        "siva.external-resources.signature-policy-document.read-timeout=1500")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    AIALoaderProperties aia = context.getBean(AIALoaderProperties.class);
                    SignaturePolicyDocumentLoaderProperties policy = context.getBean(SignaturePolicyDocumentLoaderProperties.class);

                    assertThat(aia.getAcceptedProtocols()).containsExactly(Protocol.HTTPS);
                    assertThat(aia.connectTimeoutMillis()).isEqualTo(7000);
                    assertThat(aia.readTimeoutMillis()).isEqualTo(1500);
                    assertThat(aia.maxResponseSizeBytes()).isEqualTo(524288);
                    assertThat(policy.getAcceptedProtocols()).isEmpty();
                    assertThat(policy.readTimeoutMillis()).isEqualTo(1500);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "siva.external-resources.aia.connect-timeout=1ms",
            "siva.external-resources.aia.read-timeout=2147483647ms",
            "siva.external-resources.aia.max-response-size=1B",
            "siva.external-resources.aia.max-response-size=2147483647B"
    })
    void context_WhenValueIsAtLimit_Starts(String property) {
        contextRunner
                .withPropertyValues(property)
                .run(context -> assertThat(context).hasNotFailed());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "siva.external-resources.aia.connect-timeout=0s",
            "siva.external-resources.aia.connect-timeout=1ns",
            "siva.external-resources.aia.read-timeout=2147483648ms",
            "siva.external-resources.aia.max-response-size=2GB",
            "siva.external-resources.signature-policy-document.max-response-size=0B"
    })
    void context_WhenValueIsOutOfRange_FailsToStart(String property) {
        contextRunner
                .withPropertyValues(property)
                .run(context -> assertThat(context)
                        .hasFailed()
                        .getFailure()
                        .hasRootCauseInstanceOf(BindValidationException.class)
                        .hasStackTraceContaining(OUT_OF_RANGE_MESSAGE));
    }

    @Test
    void context_WhenAcceptedProtocolsHasEmptyElement_FailsToStart() {
        contextRunner
                .withPropertyValues("siva.external-resources.aia.accepted-protocols=HTTP,")
                .run(context -> assertThat(context)
                        .hasFailed()
                        .getFailure()
                        .hasRootCauseInstanceOf(BindValidationException.class)
                        .hasStackTraceContaining("acceptedProtocols"));
    }

    @EnableConfigurationProperties({AIALoaderProperties.class, SignaturePolicyDocumentLoaderProperties.class})
    static class TestConfiguration {
    }
}
