/*
 * Copyright 2016 - 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.tsl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslStoreBundle;

import java.io.InputStream;
import java.security.KeyStore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;

class SslBundleDataLoaderTest {

    @Test
    void getSSLTrustStore_ReturnsTheBundlesCurrentTrustStore() throws Exception {
        KeyStore first = testTrustStore();
        KeyStore second = KeyStore.getInstance("PKCS12");
        second.load(null, null);
        DefaultSslBundleRegistry registry = new DefaultSslBundleRegistry("tsl", bundle(first));
        SslBundleDataLoader loader = new SslBundleDataLoader(registry, "tsl");

        assertThat(loader.getSSLTrustStore(), sameInstance(first));

        registry.updateBundle("tsl", bundle(second));
        assertThat(loader.getSSLTrustStore(), sameInstance(second));
    }

    static SslBundle bundle(KeyStore trustStore) {
        return SslBundle.of(SslStoreBundle.of(null, null, trustStore));
    }

    static KeyStore testTrustStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = SslBundleDataLoaderTest.class.getResourceAsStream("/test-lotl-truststore.p12")) {
            keyStore.load(in, "lotl-truststore-password".toCharArray());
        }
        return keyStore;
    }
}
