/*
 * Copyright 2026 Mindaugas Kiškis
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

import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TslSslTruststoreTest {

    private static final char[] TRUSTSTORE_PASSWORD = "digidoc4j-password".toCharArray();

    @Test
    void truststoreContainsGlobalSignAtlasRootCertificates() throws Exception {
        KeyStore truststore = KeyStore.getInstance("PKCS12");
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("tsl-ssl-truststore.p12")) {
            assertNotNull(inputStream);
            truststore.load(inputStream, TRUSTSTORE_PASSWORD);
        }

        assertCertificateFingerprint(
                truststore.getCertificate("globalsign root r46"),
                "4fa3126d8d3a11d1c4855a4f807cbad6cf919d3a5a88b03bea2c6372d93c40c9"
        );
        assertCertificateFingerprint(
                truststore.getCertificate("globalsign root e46"),
                "cbb9c44d84b8043e1050ea31a69f514955d7bfd2e2c6b49301019ad61d9f5058"
        );
    }

    private static void assertCertificateFingerprint(Certificate certificate, String expectedFingerprint) throws Exception {
        assertNotNull(certificate);
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(certificate.getEncoded());
        assertEquals(expectedFingerprint, HexFormat.of().formatHex(digest));
    }
}
