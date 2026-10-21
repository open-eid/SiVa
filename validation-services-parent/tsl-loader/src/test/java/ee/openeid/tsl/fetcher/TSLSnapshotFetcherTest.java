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
import ee.openeid.tsl.snapshot.TSLSnapshotManifest;
import ee.openeid.tsl.snapshot.TSLSnapshotStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TSLSnapshotFetcherTest {

    private static final Instant NOW = Instant.parse("2026-10-13T18:30:00Z");
    private static final Map<String, Object> DETAILS = Map.of("generic", Map.of("certificates", 32));

    private static final Duration PERIOD = Duration.ofHours(6);
    private static final Map<String, Instant> DOWNLOADED =
            Map.of("EE", NOW.minusSeconds(60), "EU", NOW.minusSeconds(30));

    @TempDir
    private Path workingCache;
    @Mock
    private TSLSnapshotStore store;
    @Mock
    private TSLHealthIndicator tslHealth;
    @Mock
    private TSLLoader genericLoader;
    @Mock
    private TSLLoader timemarkLoader;

    private TSLSnapshotFetcher fetcher;

    @BeforeEach
    void setUp() throws IOException {
        Files.writeString(workingCache.resolve("https___tl_example_EE_xml"), "ee");
        Files.writeString(workingCache.resolve("https___lotl_example_eu-lotl_xml"), "lotl");
        TSLLoaderConfigurationProperties loaderProperties = new TSLLoaderConfigurationProperties();
        loaderProperties.setMandatoryTerritories(List.of("EE"));
        lenient().when(genericLoader.getTslName()).thenReturn("generic");
        lenient().when(timemarkLoader.getTslName()).thenReturn("timemark");
        fetcher = new TSLSnapshotFetcher(store, workingCache, tslHealth, Set.of(genericLoader, timemarkLoader),
                loaderProperties, PERIOD, Clock.fixed(NOW, ZoneOffset.UTC), 2);
    }

    @Test
    void fetch_WhenTheTrustListsAreUp_PublishesTheCacheWithAManifestAndPrunes() throws IOException {
        when(tslHealth.health()).thenReturn(Health.up().withDetails(DETAILS).build());
        when(genericLoader.getDownloadedAt()).thenReturn(DOWNLOADED);

        int exitCode = fetcher.fetch();

        assertThat(exitCode, is(TSLSnapshotFetcher.EXIT_PUBLISHED));
        ArgumentCaptor<TSLSnapshotManifest> manifest = ArgumentCaptor.forClass(TSLSnapshotManifest.class);
        verify(store).publish(eq(workingCache), manifest.capture());
        assertThat(manifest.getValue(), is(new TSLSnapshotManifest("20261013T183000Z", NOW, "UP", List.of("EE"),
                DETAILS, 2, PERIOD, Map.of("generic", DOWNLOADED))));
        verify(store).prune(2);
    }

    @Test
    void fetch_WhenAListCameFromItsLastCopy_PublishesAndExitsWithTheLastCopyCode() throws IOException {
        when(tslHealth.health()).thenReturn(Health.up().withDetails(DETAILS).build());
        when(genericLoader.getLastCopies()).thenReturn(Map.of("EU", "Unable to process GET call"));

        int exitCode = fetcher.fetch();

        assertThat(exitCode, is(TSLSnapshotFetcher.EXIT_PUBLISHED_FROM_LAST_COPY));
        verify(store).publish(eq(workingCache), any());
        verify(store).prune(2);
    }

    @Test
    void fetch_WhenThePeriodIsUnknown_TheManifestCarriesNone() throws IOException {
        fetcher = new TSLSnapshotFetcher(store, workingCache, tslHealth, Set.of(),
                new TSLLoaderConfigurationProperties(), null, Clock.fixed(NOW, ZoneOffset.UTC), 2);
        when(tslHealth.health()).thenReturn(Health.up().build());

        fetcher.fetch();

        ArgumentCaptor<TSLSnapshotManifest> manifest = ArgumentCaptor.forClass(TSLSnapshotManifest.class);
        verify(store).publish(eq(workingCache), manifest.capture());
        assertThat(manifest.getValue().period(), nullValue());
        assertThat(manifest.getValue().downloadedAt(), is(Map.of()));
    }

    @Test
    void fetch_WhenTheTrustListsAreBelowTheThreshold_PublishesNothing() {
        when(tslHealth.health()).thenReturn(Health.down().withDetails(DETAILS).build());

        int exitCode = fetcher.fetch();

        assertThat(exitCode, is(TSLSnapshotFetcher.EXIT_BELOW_THRESHOLD));
        verifyNoMoreInteractions(store);
    }

    @Test
    void fetch_WhenTheStoreCannotBeWritten_ReportsIt() throws IOException {
        when(tslHealth.health()).thenReturn(Health.up().build());
        doThrow(new IOException("read-only file system")).when(store).publish(any(), any());

        int exitCode = fetcher.fetch();

        assertThat(exitCode, is(TSLSnapshotFetcher.EXIT_PUBLISH_FAILED));
    }
}
