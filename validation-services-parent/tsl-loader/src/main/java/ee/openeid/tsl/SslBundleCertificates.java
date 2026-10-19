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

import org.springframework.boot.ssl.SslBundle;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// The certificates a Spring SSL bundle's trust store holds, read from the bundle as it is now (a reloaded bundle
// answers with its new content).
public final class SslBundleCertificates {

    private SslBundleCertificates() {
    }

    public static List<X509Certificate> of(SslBundle bundle) {
        KeyStore trustStore = bundle.getStores().getTrustStore();
        if (trustStore == null) {
            return List.of();
        }
        try {
            List<X509Certificate> certificates = new ArrayList<>();
            for (String alias : Collections.list(trustStore.aliases())) {
                Certificate certificate = trustStore.getCertificate(alias);
                if (certificate instanceof X509Certificate x509) {
                    certificates.add(x509);
                }
            }
            return certificates;
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Failed to read the SSL bundle's trust store", e);
        }
    }
}
