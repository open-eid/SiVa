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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// open-eid/SiVa#6: a trust-list refresh that changed a list used to rebuild the one trust source in place,
// with setTrustPropertiesByCertificates clearing the lookup maps and re-adding every anchor under the source's
// monitor, while the reads a validation takes (isKnown, getBySubject, ...) hold no lock; a validation in flight
// during the refill found no, or not all, trust anchors. TSLLoader now fills a fresh source per refresh and
// publishes it through TrustedListsCertificateSourceHolder, which readers resolve per use. The first test pins
// that; the second shows the DSS behaviour it steps around (fails on sd-dss 6.2.d4j.1, enabled with
// -Dsiva.tsl.rebuildRace=true) so that a return to a shared source is caught.
class TrustedListsCertificateSourceRebuildTest {

    private static final int ANCHORS = 200;
    private static final int REBUILDS = 200;

    @Test
    void anchorsStayTrustedWhileFreshSourcesArePublished() throws Exception {
        Map<CertificateToken, List<TrustProperties>> anchors = TslTestUtils.anchors(ANCHORS);
        CertificateToken probe = lastOf(anchors);
        TrustedListsCertificateSourceHolder holder = new TrustedListsCertificateSourceHolder();
        holder.publish(TslTestUtils.filled(anchors));
        assertTrue(holder.current().isTrusted(probe));

        Misses misses = countMisses(() -> holder.current().isTrusted(probe), () -> {
            for (int i = 0; i < REBUILDS; i++) {
                holder.publish(TslTestUtils.filled(anchors));
            }
        });

        assertTrue(holder.current().isTrusted(probe));
        assertEquals(0, misses.misses(), () -> misses + " during " + REBUILDS + " published sources of "
                + ANCHORS + " anchors");
    }

    @Test
    @EnabledIfSystemProperty(named = "siva.tsl.rebuildRace", matches = "true")
    void anchorsAreMissedWhileOneSourceIsRebuiltInPlace() throws Exception {
        Map<CertificateToken, List<TrustProperties>> anchors = TslTestUtils.anchors(ANCHORS);
        CertificateToken probe = lastOf(anchors);
        TrustedListsCertificateSource source = TslTestUtils.filled(anchors);
        assertTrue(source.isTrusted(probe));

        long start = System.nanoTime();
        Misses misses = countMisses(() -> source.isTrusted(probe), () -> {
            for (int i = 0; i < REBUILDS; i++) {
                source.setTrustPropertiesByCertificates(anchors);
            }
        });
        long rebuildMicros = (System.nanoTime() - start) / 1_000 / REBUILDS;

        assertTrue(source.isTrusted(probe));
        assertEquals(0, misses.misses(), () -> misses + " during " + REBUILDS + " in-place rebuilds of "
                + ANCHORS + " anchors (" + rebuildMicros + " us per rebuild)");
    }

    private record Misses(long misses, long reads) {
        @Override
        public String toString() {
            return misses + " of " + reads + " isTrusted answers were false";
        }
    }

    // Reads as fast as one thread can while the writer runs; a miss is a false answer about a trusted anchor.
    private static Misses countMisses(BooleanSupplier read, Runnable write) throws InterruptedException {
        AtomicBoolean writing = new AtomicBoolean(true);
        AtomicLong reads = new AtomicLong();
        AtomicLong misses = new AtomicLong();
        Thread reader = new Thread(() -> {
            while (writing.get()) {
                reads.incrementAndGet();
                if (!read.getAsBoolean()) {
                    misses.incrementAndGet();
                }
            }
        }, "validation");
        reader.start();
        write.run();
        writing.set(false);
        reader.join();
        return new Misses(misses.get(), reads.get());
    }

    // The last anchor added: the one a reader misses for a whole in-place refill, not just its start.
    private static CertificateToken lastOf(Map<CertificateToken, List<TrustProperties>> anchors) {
        return anchors.keySet().stream().reduce((first, second) -> second).orElseThrow();
    }

}
