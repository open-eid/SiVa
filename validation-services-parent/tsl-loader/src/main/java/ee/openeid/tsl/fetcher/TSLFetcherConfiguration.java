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

package ee.openeid.tsl.fetcher;

import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import ee.openeid.tsl.health.TSLHealthIndicator;
import ee.openeid.tsl.snapshot.TSLSnapshotStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.util.Set;

// The tsl-fetch profile turns the application into a one-shot trust-list fetcher: the loaders refresh online
// into siva.tsl.loader.cacheDirectory as usual while the context starts, then the runner publishes that cache
// into siva.tsl.fetcher.outputDirectory and ends the JVM with the fetcher's exit code (the webapp's
// application-tsl-fetch.yml keeps the servlet context but binds no port).
@Slf4j
@Configuration
@Profile("tsl-fetch")
public class TSLFetcherConfiguration {

    @Bean
    public TSLFetcherConfigurationProperties tslFetcherConfigurationProperties() {
        return new TSLFetcherConfigurationProperties();
    }

    @Bean
    public TSLSnapshotFetcher tslSnapshotFetcher(TSLFetcherConfigurationProperties fetcherProperties,
                                                 TSLLoaderConfigurationProperties loaderProperties,
                                                 TSLHealthIndicator tslHealthIndicator, Set<TSLLoader> loaders) {
        if (loaderProperties.isLoadFromCache()) {
            throw new IllegalStateException("The tsl-fetch profile downloads the trust lists: "
                    + "siva.tsl.loader.loadFromCache must be false");
        }
        if (loaderProperties.getCacheDirectory() == null) {
            throw new IllegalStateException("The tsl-fetch profile needs siva.tsl.loader.cacheDirectory, "
                    + "the working cache it downloads into (persistent, so a failed download keeps its last copy)");
        }
        if (fetcherProperties.getOutputDirectory() == null) {
            throw new IllegalStateException("The tsl-fetch profile needs siva.tsl.fetcher.outputDirectory, "
                    + "the snapshot store it publishes into");
        }
        TSLSnapshotStore store = new TSLSnapshotStore(fetcherProperties.getOutputDirectory().toPath());
        return new TSLSnapshotFetcher(store, loaderProperties.getCacheDirectory().toPath(), tslHealthIndicator,
                loaders, loaderProperties, fetcherProperties.getPeriod(), Clock.systemUTC(),
                fetcherProperties.getKeepSnapshots());
    }

    @Bean
    public ApplicationRunner tslSnapshotFetchRunner(TSLSnapshotFetcher fetcher,
                                                    ConfigurableApplicationContext context) {
        return args -> exit(context, fetcher.fetch());
    }

    @SuppressWarnings("PMD.DoNotTerminateVM") // one-shot process: scheduler and HTTP client threads keep the JVM alive
    private static void exit(ConfigurableApplicationContext context, int exitCode) {
        log.info("Trust-list fetcher finished with exit code {}", exitCode);
        System.exit(SpringApplication.exit(context, () -> exitCode));
    }
}
