/*
 * Copyright 2023 - 2026 Riigi Infosüsteemi Amet
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

import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TSLValidationJobFactoryTest {

    private final CountingExecutor executor = new CountingExecutor();
    private final TSLValidationJobFactory factory = new TSLValidationJobFactory(executor);

    @TempDir
    private Path cache;

    @AfterEach
    void tearDown() {
        executor.shutdownNow();
    }

    @Test
    void createValidationJob_WhenJobsRefresh_TheirAnalysesRunOnTheFactoryExecutor() {
        refresh(factory.createValidationJob());
        refresh(factory.createValidationJob());

        assertEquals(2, executor.tasks.get());
        assertFalse(executor.isShutdown());
    }

    @Test
    void shutdown_StopsTheExecutor() {
        factory.createValidationJob();

        factory.shutdown();

        assertTrue(executor.isShutdown());
    }

    // The smallest refresh DSS runs: one list of trusted lists, nothing to download, so one analysis task.
    private void refresh(TLValidationJob job) {
        LOTLSource lotl = new LOTLSource();
        lotl.setUrl("https://lotl.test/lotl.xml");
        job.setListOfTrustedListSources(lotl);
        FileCacheDataLoader loader = new FileCacheDataLoader(new IgnoreDataLoader());
        loader.setFileCacheDirectory(cache.toFile());
        job.setOfflineDataLoader(loader);
        job.setTrustedListCertificateSource(new TrustedListsCertificateSource());
        job.offlineRefresh();
    }

    private static final class CountingExecutor extends ThreadPoolExecutor {

        private final AtomicInteger tasks = new AtomicInteger();

        private CountingExecutor() {
            super(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        }

        @Override
        public void execute(Runnable task) {
            tasks.incrementAndGet();
            super.execute(task);
        }
    }

}
