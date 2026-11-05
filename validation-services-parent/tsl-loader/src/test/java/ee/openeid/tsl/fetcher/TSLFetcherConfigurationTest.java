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

package ee.openeid.tsl.fetcher;

import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import ee.openeid.tsl.health.TSLHealthIndicator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TSLFetcherConfigurationTest {

    @TempDir
    private Path directory;
    @Mock
    private TSLHealthIndicator tslHealth;

    private final TSLFetcherConfiguration configuration = new TSLFetcherConfiguration();

    @Test
    void tslSnapshotFetcher_WithAWorkingCacheAndAnOutputDirectory_IsBuilt() {
        TSLSnapshotFetcher fetcher = configuration.tslSnapshotFetcher(fetcherProperties(), loaderProperties(),
                tslHealth, Set.of());

        assertThat(fetcher, is(notNullValue()));
        assertThat(configuration.tslFetcherConfigurationProperties(), is(notNullValue()));
    }

    @Test
    void tslSnapshotFetcher_WhenLoadFromCacheIsOn_Refuses() {
        TSLLoaderConfigurationProperties loaderProperties = loaderProperties();
        loaderProperties.setLoadFromCache(true);

        IllegalStateException refusal = assertThrows(IllegalStateException.class,
                () -> configuration.tslSnapshotFetcher(fetcherProperties(), loaderProperties, tslHealth, Set.of()));

        assertThat(refusal.getMessage(), containsString("loadFromCache"));
    }

    @Test
    void tslSnapshotFetcher_WithoutAWorkingCache_Refuses() {
        TSLLoaderConfigurationProperties loaderProperties = loaderProperties();
        loaderProperties.setCacheDirectory(null);

        IllegalStateException refusal = assertThrows(IllegalStateException.class,
                () -> configuration.tslSnapshotFetcher(fetcherProperties(), loaderProperties, tslHealth, Set.of()));

        assertThat(refusal.getMessage(), containsString("cacheDirectory"));
    }

    @Test
    void tslSnapshotFetcher_WithoutAnOutputDirectory_Refuses() {
        TSLFetcherConfigurationProperties fetcherProperties = fetcherProperties();
        fetcherProperties.setOutputDirectory(null);

        IllegalStateException refusal = assertThrows(IllegalStateException.class,
                () -> configuration.tslSnapshotFetcher(fetcherProperties, loaderProperties(), tslHealth, Set.of()));

        assertThat(refusal.getMessage(), containsString("outputDirectory"));
    }

    private TSLLoaderConfigurationProperties loaderProperties() {
        TSLLoaderConfigurationProperties properties = new TSLLoaderConfigurationProperties();
        properties.setCacheDirectory(directory.resolve("cache").toFile());
        return properties;
    }

    private TSLFetcherConfigurationProperties fetcherProperties() {
        TSLFetcherConfigurationProperties properties = new TSLFetcherConfigurationProperties();
        properties.setOutputDirectory(directory.resolve("store").toFile());
        return properties;
    }
}
