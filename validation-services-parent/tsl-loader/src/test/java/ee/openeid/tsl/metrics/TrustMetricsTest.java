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

package ee.openeid.tsl.metrics;

import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslStoreBundle;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notANumber;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrustMetricsTest {

    @Mock
    private ObjectProvider<SslBundles> sslBundles;

    private final SimpleMeterRegistry registry = new SimpleMeterRegistry();
    private final TSLLoaderConfigurationProperties properties = new TSLLoaderConfigurationProperties();

    @Test
    void nothingIsRegisteredWithoutAConfiguredBundle() {
        new TrustMetrics(properties, sslBundles).bindTo(registry);

        assertThat(registry.getMeters(), empty());
    }

    @Test
    void countExpiryAndReloadsAreReadFromTheBundleRegistry() throws Exception {
        properties.setSslBundle("tsl");
        properties.setLotlBundle("tsl");
        KeyStore testStore = lotlTestTrustStore();
        DefaultSslBundleRegistry bundles = new DefaultSslBundleRegistry("tsl", bundle(testStore));
        when(sslBundles.getIfAvailable()).thenReturn(bundles);
        new TrustMetrics(properties, sslBundles).bindTo(registry);
        double testTslNotAfter = ((X509Certificate) testStore.getCertificate("test-tsl"))
                .getNotAfter().getTime() / 1000.0;

        assertThat(registry.get(TrustMetrics.CERTIFICATES).tag("bundle", "tsl").gauge().value(), equalTo(1.0));
        assertThat(registry.get(TrustMetrics.EARLIEST_EXPIRY).tag("bundle", "tsl").gauge().value(),
                closeTo(testTslNotAfter, 1.0));
        assertThat(registry.get(TrustMetrics.RELOADS).tag("bundle", "tsl").counter().count(), equalTo(0.0));

        KeyStore empty = KeyStore.getInstance("PKCS12");
        empty.load(null, null);
        bundles.updateBundle("tsl", bundle(empty));

        assertThat(registry.get(TrustMetrics.CERTIFICATES).tag("bundle", "tsl").gauge().value(), equalTo(0.0));
        assertThat(registry.get(TrustMetrics.EARLIEST_EXPIRY).tag("bundle", "tsl").gauge().value(), notANumber());
        assertThat(registry.get(TrustMetrics.RELOADS).tag("bundle", "tsl").counter().count(), equalTo(1.0));
    }

    private static SslBundle bundle(KeyStore trustStore) {
        return SslBundle.of(SslStoreBundle.of(null, null, trustStore));
    }

    private static KeyStore lotlTestTrustStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = TrustMetricsTest.class.getResourceAsStream("/test-lotl-truststore.p12")) {
            keyStore.load(in, "lotl-truststore-password".toCharArray());
        }
        return keyStore;
    }
}
