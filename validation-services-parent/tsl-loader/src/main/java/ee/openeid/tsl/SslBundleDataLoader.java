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

import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import lombok.NonNull;
import org.springframework.boot.ssl.SslBundles;

import java.security.KeyStore;

// Trusts the trust-list hosts through a Spring SSL bundle (spring.ssl.bundle.*) instead of a PKCS12 document.
// DSS builds a new HTTP client for every request and asks for the trust store then, so a bundle Spring Boot
// reloaded (reload-on-update) is used from the next download on; nothing is rebuilt and no password is involved.
public class SslBundleDataLoader extends CommonsDataLoader {

    private static final long serialVersionUID = 1L;

    private final transient SslBundles sslBundles;
    private final String bundleName;

    public SslBundleDataLoader(@NonNull SslBundles sslBundles, @NonNull String bundleName) {
        this.sslBundles = sslBundles;
        this.bundleName = bundleName;
    }

    @Override
    protected KeyStore getSSLTrustStore() {
        return sslBundles.getBundle(bundleName).getStores().getTrustStore();
    }
}
