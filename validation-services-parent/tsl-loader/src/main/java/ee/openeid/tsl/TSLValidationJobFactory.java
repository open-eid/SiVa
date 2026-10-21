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

import eu.europa.esig.dss.tsl.job.TLValidationJob;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TSLValidationJobFactory {

    // Every job would otherwise start a thread pool of its own; the loaders create one job per refresh.
    private final ExecutorService executorService;

    public TSLValidationJobFactory() {
        this(Executors.newCachedThreadPool());
    }

    TSLValidationJobFactory(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public TLValidationJob createValidationJob() {
        TLValidationJob job = new TLValidationJob();
        job.setExecutorService(executorService);
        return job;
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdownNow();
    }
}
