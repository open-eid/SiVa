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

import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import ee.openeid.tsl.health.TSLHealthIndicator;
import ee.openeid.tsl.snapshot.TSLSnapshotManifest;
import ee.openeid.tsl.snapshot.TSLSnapshotStore;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

// One run of the trust-list fetcher: the loaders have already refreshed online into their working cache (same
// code path as a webapp start); this publishes that cache as a snapshot when the `tsl` health is UP, i.e. when
// every mandatory territory came through, and refuses otherwise so that `current` keeps naming the last good
// snapshot. A working cache that persists between runs keeps the last copy of any list whose download failed
// (LastGoodCopyFileCacheDataLoader), so one unreachable member state never costs its territory.
@Slf4j
@RequiredArgsConstructor
public class TSLSnapshotFetcher {

    public static final int EXIT_PUBLISHED = 0;
    public static final int EXIT_BELOW_THRESHOLD = 1;
    public static final int EXIT_PUBLISH_FAILED = 2;

    private final @NonNull TSLSnapshotStore store;
    private final @NonNull Path workingCache;
    private final @NonNull TSLHealthIndicator tslHealth;
    private final @NonNull TSLLoaderConfigurationProperties loaderProperties;
    private final @NonNull Clock clock;
    private final int keepSnapshots;

    // Publishes the working cache and returns the process exit code: 0 published, 1 the trust lists were below
    // the readiness threshold (nothing published), 2 the store could not be written.
    public int fetch() {
        Health health = tslHealth.health();
        Map<String, Object> details = health.getDetails();
        if (!Status.UP.equals(health.getStatus())) {
            log.error("Trust lists below the readiness threshold, snapshot not published: {}", details);
            return EXIT_BELOW_THRESHOLD;
        }
        Path root = store.getRoot();
        try {
            TSLSnapshotManifest manifest = manifest(health);
            String id = manifest.id();
            store.publish(workingCache, manifest);
            store.prune(keepSnapshots);
            log.info("Trust-list snapshot {} published: {}", id, details);
            return EXIT_PUBLISHED;
        } catch (IOException e) {
            log.error("Publishing the trust-list snapshot to {} failed", root, e);
            return EXIT_PUBLISH_FAILED;
        }
    }

    private TSLSnapshotManifest manifest(Health health) throws IOException {
        Instant createdAt = clock.instant();
        List<String> mandatory = loaderProperties.getMandatoryTerritories() == null
                ? List.of() : List.copyOf(loaderProperties.getMandatoryTerritories());
        int files;
        try (Stream<Path> entries = Files.list(workingCache)) {
            files = (int) entries.filter(Files::isRegularFile).count();
        }
        return new TSLSnapshotManifest(TSLSnapshotStore.newId(createdAt), createdAt, health.getStatus().getCode(),
                mandatory, health.getDetails(), files);
    }
}
