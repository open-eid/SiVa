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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Reproduces open-eid/SiVa#6 at the DSS level: a trust-list refresh that changed a list rebuilds the
// trust source with setTrustPropertiesByCertificates, which clears the lookup maps and re-adds every
// anchor under the source's monitor, while the reads a validation takes (isKnown, getBySubject, ...)
// hold no lock. A validation in flight during the refill finds no, or not all, trust anchors.
// Fails on DSS 6.2 (sd-dss 6.2.d4j.1); enable with -Dsiva.tsl.rebuildRace=true. Passes once the
// source swaps complete maps in instead of refilling in place (DSS) or SiVa reads through a
// reference it replaces after each refresh.
class TrustedListsCertificateSourceRebuildTest {

    private static final int ANCHORS = 200;
    private static final int REBUILDS = 200;

    @Test
    @EnabledIfSystemProperty(named = "siva.tsl.rebuildRace", matches = "true")
    void anchorsStayTrustedWhileTheSourceIsRebuilt() throws Exception {
        Map<CertificateToken, List<TrustProperties>> anchors = anchors(ANCHORS);
        // The last anchor added: the one a reader misses for the whole refill, not just its start.
        CertificateToken probe = anchors.keySet().stream().reduce((first, second) -> second).orElseThrow();
        TrustedListsCertificateSource source = new TrustedListsCertificateSource();
        source.setTrustPropertiesByCertificates(anchors);
        assertTrue(source.isTrusted(probe));

        AtomicBoolean rebuilding = new AtomicBoolean(true);
        AtomicLong reads = new AtomicLong();
        AtomicLong misses = new AtomicLong();
        Thread reader = new Thread(() -> {
            while (rebuilding.get()) {
                reads.incrementAndGet();
                if (!source.isTrusted(probe)) {
                    misses.incrementAndGet();
                }
            }
        }, "validation");
        reader.start();
        long start = System.nanoTime();
        for (int i = 0; i < REBUILDS; i++) {
            source.setTrustPropertiesByCertificates(anchors);
        }
        long rebuildMicros = (System.nanoTime() - start) / 1_000 / REBUILDS;
        rebuilding.set(false);
        reader.join();

        assertTrue(source.isTrusted(probe));
        assertEquals(0, misses.get(), () -> misses + " of " + reads + " isTrusted answers were false during "
                + REBUILDS + " rebuilds of " + ANCHORS + " anchors (" + rebuildMicros + " us per rebuild)");
    }

    // Self-signed EC certificates with distinct subjects; no trust properties needed, the source treats an
    // anchor without them as trusted without a time limit.
    private static Map<CertificateToken, List<TrustProperties>> anchors(int count) throws Exception {
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
}
