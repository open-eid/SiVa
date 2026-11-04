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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class ExternalResourceLoaderPropertiesBindingTest {

    @Test
    void context_WhenValueIsOutOfRange_FailsToStart() {
        new ApplicationContextRunner()
                .withUserConfiguration(TestConfiguration.class)
                .withPropertyValues("siva.external-resources.aia.connect-timeout=0s")
                .run(context -> assertThat(context.getStartupFailure(), notNullValue()));
    }

    @Test
    void context_WhenAiaValuesAreOverridden_UsesThemAndKeepsOtherDefaults() {
        new ApplicationContextRunner()
                .withUserConfiguration(TestConfiguration.class)
                .withPropertyValues(
                        "siva.external-resources.aia.connect-timeout=7s",
                        "siva.external-resources.aia.accepted-protocols=HTTPS",
                        "siva.external-resources.signature-policy-document.accepted-protocols=HTTP")
                .run(context -> {
                    AIALoaderProperties aia = context.getBean(AIALoaderProperties.class);
                    SignaturePolicyDocumentLoaderProperties policy = context.getBean(SignaturePolicyDocumentLoaderProperties.class);

                    assertThat(aia.getConnectTimeout(), equalTo(Duration.ofSeconds(7)));
                    assertThat(aia.getAcceptedProtocols(), equalTo(List.of(Protocol.HTTPS)));
                    assertThat(aia.getReadTimeout(), equalTo(Duration.ofSeconds(10)));
                    assertThat(aia.getMaxResponseSize(), equalTo(DataSize.ofMegabytes(1)));

                    assertThat(policy.getConnectTimeout(), equalTo(Duration.ofSeconds(5)));
                    assertThat(policy.getAcceptedProtocols(), equalTo(List.of(Protocol.HTTP)));
                    assertThat(policy.getMaxResponseSize(), equalTo(DataSize.ofMegabytes(4)));
                });
    }

    @EnableConfigurationProperties({AIALoaderProperties.class, SignaturePolicyDocumentLoaderProperties.class})
    static class TestConfiguration {
    }
}
