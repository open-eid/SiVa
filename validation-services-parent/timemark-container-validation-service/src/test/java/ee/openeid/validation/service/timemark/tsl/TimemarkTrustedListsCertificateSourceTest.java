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

package ee.openeid.validation.service.timemark.tsl;

import ee.openeid.tsl.TrustedListsCertificateSourceHolder;
import eu.europa.esig.dss.model.tsl.TrustPropertiesCertificateSource;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.model.x509.X500PrincipalHelper;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.TrustedCertificateSource;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.digidoc4j.TSLCertificateSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;

class TimemarkTrustedListsCertificateSourceTest {

    private final TrustedListsCertificateSourceHolder holder = new TrustedListsCertificateSourceHolder();
    private final TimemarkTrustedListsCertificateSource view = new TimemarkTrustedListsCertificateSource(holder);
    private CertificateToken anchor;

    @BeforeEach
    void setUp() throws Exception {
        anchor = selfSigned("CN=anchor,C=EE");
    }

    @Test
    void readsAnswerFromTheSourceLastPublished() {
        assertFalse(view.isTrusted(anchor));
        assertEquals(0, view.getNumberOfCertificates());
        TrustedListsCertificateSource filled = new TrustedListsCertificateSource();
        filled.setTrustPropertiesByCertificates(Map.of(anchor, List.of()));

        holder.publish(filled);

        assertTrue(view.isTrusted(anchor));
        assertTrue(view.isKnown(anchor));
        assertThat(view.getCertificates(), contains(anchor));
        assertEquals(1, view.getNumberOfCertificates());
        assertEquals(1, view.getNumberOfTrustedEntityKeys());
        assertThat(view.getBySubject(anchor.getSubject()), contains(anchor));
        assertThat(view.getByPublicKey(anchor.getPublicKey()), contains(anchor));
        assertEquals(List.of(), view.getTrustServices(anchor));

        holder.publish(new TrustedListsCertificateSource());

        assertFalse(view.isTrusted(anchor));
        assertEquals(0, view.getNumberOfCertificates());
    }

    @Test
    void withPinnedSource_WhenTheLoaderPublishesMeanwhile_TheValidationKeepsTheSourceItStartedWith() {
        TrustedListsCertificateSource first = new TrustedListsCertificateSource();
        first.setTrustPropertiesByCertificates(Map.of(anchor, List.of()));
        holder.publish(first);

        boolean trustedThroughout = view.withPinnedSource(() -> {
            boolean before = view.isTrusted(anchor);
            holder.publish(new TrustedListsCertificateSource());
            return before && view.isTrusted(anchor) && view.getNumberOfCertificates() == 1;
        });

        assertTrue(trustedThroughout);
        assertFalse(view.isTrusted(anchor));
    }

    // digidoc4j validates each signature on its executor's threads; the pin must reach them.
    @Test
    void pinning_ATaskSubmittedDuringAValidationReadsTheSourcePinnedByIt() throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        ExecutorService pinning = view.pinning(pool);
        try {
            TrustedListsCertificateSource first = new TrustedListsCertificateSource();
            first.setTrustPropertiesByCertificates(Map.of(anchor, List.of()));
            holder.publish(first);

            boolean trustedOnTheWorker = view.withPinnedSource(() -> {
                holder.publish(new TrustedListsCertificateSource());
                try {
                    return pinning.submit(() -> view.isTrusted(anchor)).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new IllegalStateException(e);
                }
            });

            assertTrue(trustedOnTheWorker);
            // Outside a validation the worker reads what is published, and keeps nothing from the task before.
            assertFalse(pinning.submit(() -> view.isTrusted(anchor)).get());
        } finally {
            pinning.shutdownNow();
        }
    }

    // A container's signatures validate at once on several pool threads; a snapshot switch meanwhile must reach
    // none of them, and must leave no pin behind on any of them.
    @Test
    void tasksOnEveryPoolThreadReadTheSourcePinnedByTheValidationThatSubmittedThem() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        ExecutorService pinning = view.pinning(pool);
        try {
            TrustedListsCertificateSource first = new TrustedListsCertificateSource();
            first.setTrustPropertiesByCertificates(Map.of(anchor, List.of()));
            holder.publish(first);
            CountDownLatch occupied = new CountDownLatch(THREADS);
            CountDownLatch swapped = new CountDownLatch(1);
            Set<Thread> workers = ConcurrentHashMap.newKeySet();

            List<Future<Boolean>> reads = view.withPinnedSource(() -> {
                List<Future<Boolean>> submitted = submitReads(pinning, THREADS * 8, occupied, swapped, workers);
                await(occupied);
                publishFromAnotherThread(new TrustedListsCertificateSource());
                swapped.countDown();
                return submitted;
            });

            for (Future<Boolean> read : reads) {
                assertTrue(read.get(5, TimeUnit.SECONDS));
            }
            assertThat(workers, hasSize(THREADS));
            assertThat(workers, not(hasItem(Thread.currentThread())));
            // After the validation every worker reads what is published, so no pin stayed on any of them.
            CountDownLatch occupiedAgain = new CountDownLatch(THREADS);
            for (Future<Boolean> read : submitReads(pinning, THREADS, occupiedAgain, occupiedAgain, workers)) {
                assertFalse(read.get(5, TimeUnit.SECONDS));
            }
        } finally {
            pinning.shutdownNow();
        }
    }

    @Test
    void tasksSubmittedOutsideAValidationReadTheSourceCurrentWhenTheyRun() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        ExecutorService pinning = view.pinning(pool);
        try {
            CountDownLatch occupied = new CountDownLatch(THREADS);
            CountDownLatch swapped = new CountDownLatch(1);
            Set<Thread> workers = ConcurrentHashMap.newKeySet();
            List<Future<Boolean>> reads = submitReads(pinning, THREADS * 8, occupied, swapped, workers);
            await(occupied);
            TrustedListsCertificateSource filled = new TrustedListsCertificateSource();
            filled.setTrustPropertiesByCertificates(Map.of(anchor, List.of()));

            publishFromAnotherThread(filled);
            swapped.countDown();

            for (Future<Boolean> read : reads) {
                assertTrue(read.get(5, TimeUnit.SECONDS));
            }
            assertThat(workers, hasSize(THREADS));
        } finally {
            pinning.shutdownNow();
        }
    }

    @Test
    void pinning_ForwardsTheLifecycleToTheDelegate() throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        ExecutorService pinning = view.pinning(pool);

        assertFalse(pinning.isShutdown());
        pinning.shutdown();

        assertTrue(pool.isShutdown());
        assertTrue(pinning.awaitTermination(5, TimeUnit.SECONDS));
        assertTrue(pinning.isTerminated());
    }

    @Test
    void withPinnedSource_WhenTheValidationThrows_ThePinIsReleased() {
        holder.publish(new TrustedListsCertificateSource());
        assertThrows(IllegalStateException.class, () -> view.withPinnedSource(() -> {
            throw new IllegalStateException("validation failed");
        }));
        TrustedListsCertificateSource filled = new TrustedListsCertificateSource();
        filled.setTrustPropertiesByCertificates(Map.of(anchor, List.of()));

        holder.publish(filled);

        assertTrue(view.isTrusted(anchor));
    }

    @Test
    void writesAreRefused() {
        assertThrows(UnsupportedOperationException.class, () -> view.addCertificate(anchor));
        assertThrows(UnsupportedOperationException.class, () -> view.addCertificate(anchor, List.of()));
        assertThrows(UnsupportedOperationException.class, () -> view.addTSLCertificate(anchor.getCertificate()));
        assertThrows(UnsupportedOperationException.class, () -> view.setTrustPropertiesByCertificates(Map.of()));
        assertThrows(UnsupportedOperationException.class, () -> view.setTrustTimeByCertificates(Map.of()));
        assertThrows(UnsupportedOperationException.class, () -> view.setSummary(null));
        assertThrows(UnsupportedOperationException.class, view::invalidateCache);
        assertThrows(UnsupportedOperationException.class, view::refresh);
        assertEquals(0, view.getNumberOfCertificates());
    }

    @Test
    void everyReadIsForwardedToThePublishedSourceWithItsArguments() throws Exception {
        TrustedListsCertificateSource source = mock(TrustedListsCertificateSource.class);
        holder.publish(source);
        List<String> notForwarded = new ArrayList<>();
        for (Method read : sourceMethods().filter(method -> !WRITES.contains(method.getName())).toList()) {
            Object[] arguments = Stream.of(read.getParameterTypes()).map(this::argumentOf).toArray();
            read.invoke(view, arguments);
            boolean forwarded = mockingDetails(source).getInvocations().stream()
                    .anyMatch(invocation -> invocation.getMethod().getName().equals(read.getName())
                            && Arrays.equals(invocation.getArguments(), arguments));
            if (!forwarded) {
                notForwarded.add(read.getName());
            }
        }

        assertThat(notForwarded, is(empty()));
    }

    // Configuration.copy() serializes the configuration together with its TSL source.
    @Test
    void theViewSerializesLikeTheSourceItStandsFor() {
        assertDoesNotThrow(() -> {
            try (ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream())) {
                out.writeObject(view);
            }
        });
    }

    // digidoc4j keeps the source as TSLCertificateSource; DSS reads trust services and trust time through
    // TrustPropertiesCertificateSource (DiagnosticDataBuilder) and trust through TrustedCertificateSource
    // (SignatureValidationContext).
    @Test
    void theViewIsWhatDigidoc4jAndDssLookFor() {
        assertTrue(TSLCertificateSource.class.isAssignableFrom(TimemarkTrustedListsCertificateSource.class));
        assertTrue(TrustPropertiesCertificateSource.class.isAssignableFrom(TimemarkTrustedListsCertificateSource.class));
        assertTrue(TrustedCertificateSource.class.isAssignableFrom(TimemarkTrustedListsCertificateSource.class));
    }

    private static final int THREADS = 4;

    // Each read parks on its pool thread until released, so the first THREADS of them hold every thread of the pool
    // once occupied is open; a read is true when it saw the source that trusts the anchor from start to end.
    private List<Future<Boolean>> submitReads(ExecutorService executor, int count, CountDownLatch occupied,
                                              CountDownLatch released, Set<Thread> workers) {
        List<Future<Boolean>> reads = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            reads.add(executor.submit(() -> {
                workers.add(Thread.currentThread());
                occupied.countDown();
                await(released);
                return view.isTrusted(anchor) && view.getNumberOfCertificates() == 1;
            }));
        }
        return reads;
    }

    private void publishFromAnotherThread(TrustedListsCertificateSource source) {
        Thread loader = new Thread(() -> holder.publish(source), "loader");
        loader.start();
        try {
            loader.join(5_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
        assertFalse(loader.isAlive());
    }

    private static void await(CountDownLatch latch) {
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), "latch still closed after 5 s");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    private static final Set<String> WRITES = Set.of("addCertificate", "addTSLCertificate",
            "setTrustPropertiesByCertificates", "setTrustTimeByCertificates", "setSummary", "invalidateCache", "refresh");

    // Every abstract method of the two interfaces the view implements, each signature once.
    private static Stream<Method> sourceMethods() {
        Set<String> seen = new HashSet<>();
        return Stream.of(TSLCertificateSource.class, TrustedCertificateSource.class, TrustPropertiesCertificateSource.class)
                .flatMap(type -> Stream.of(type.getMethods()))
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .filter(method -> seen.add(method.getName() + Arrays.toString(method.getParameterTypes())));
    }

    private Object argumentOf(Class<?> type) {
        if (type == CertificateToken.class) {
            return anchor;
        } else if (type == PublicKey.class) {
            return anchor.getPublicKey();
        } else if (type == X500PrincipalHelper.class) {
            return anchor.getSubject();
        } else if (type == byte[].class) {
            return new byte[] {1, 2, 3};
        } else if (type == Date.class) {
            return new Date();
        }
        return mock(type);
    }

    private static CertificateToken selfSigned(String name) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp256r1"));
        KeyPair keys = generator.generateKeyPair();
        X500Name subject = new X500Name(name);
        Date now = new Date();
        X509CertificateHolder certificate = new JcaX509v3CertificateBuilder(subject, BigInteger.ONE,
                new Date(now.getTime() - 60_000), new Date(now.getTime() + 3_600_000), subject, keys.getPublic())
                .build(new JcaContentSignerBuilder("SHA256withECDSA").build(keys.getPrivate()));
        return new CertificateToken(new JcaX509CertificateConverter().getCertificate(certificate));
    }

}
