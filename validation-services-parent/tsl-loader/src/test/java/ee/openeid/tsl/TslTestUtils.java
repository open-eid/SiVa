/*
 * Copyright 2017 - 2026 Riigi Infosüsteemi Amet
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

import eu.europa.esig.dss.model.tsl.TrustProperties;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class TslTestUtils {

    private TslTestUtils() {
    }

    // Self-signed EC certificates with distinct subjects; no trust properties needed, the source treats an
    // anchor without them as trusted without a time limit.
    static Map<CertificateToken, List<TrustProperties>> anchors(int count) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp256r1"));
        Date notBefore = new Date(System.currentTimeMillis() - 60_000);
        Date notAfter = new Date(System.currentTimeMillis() + 3_600_000);
        Map<CertificateToken, List<TrustProperties>> anchors = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            KeyPair keys = generator.generateKeyPair();
            X500Name name = new X500Name("CN=anchor-" + i + ",C=EE");
            X509CertificateHolder holder = new JcaX509v3CertificateBuilder(name, BigInteger.valueOf(i + 1), notBefore,
                    notAfter, name, keys.getPublic())
                    .build(new JcaContentSignerBuilder("SHA256withECDSA").build(keys.getPrivate()));
            anchors.put(new CertificateToken(new JcaX509CertificateConverter().getCertificate(holder)), List.of());
        }
        return anchors;
    }

    static TrustedListsCertificateSource filled(Map<CertificateToken, List<TrustProperties>> anchors) {
        TrustedListsCertificateSource source = new TrustedListsCertificateSource();
        source.setTrustPropertiesByCertificates(anchors);
        return source;
    }

}
