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

import ee.openeid.tsl.TSLLoader;
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
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

// One run of the trust-list fetcher: under the store's fetch lock the loaders refresh online into their working
// cache (same code path as a webapp start), then that cache is published as a snapshot when the `tsl` health is
// UP, i.e. when every mandatory territory came through, and refused otherwise so that `current` keeps naming the
// last good snapshot. A working cache that persists between runs keeps the last copy of any list whose download failed
// (LastGoodCopyFileCacheDataLoader), so one unreachable member state never costs its territory; such a run
// still publishes (the copy is valid until the list says otherwise) but ends with its own exit code, so that
// a Job watcher sees a fetch that did not fetch.
@Slf4j
@RequiredArgsConstructor
public class TSLSnapshotFetcher {

    public static final int EXIT_PUBLISHED = 0;
    public static final int EXIT_BELOW_THRESHOLD = 1;
    public static final int EXIT_PUBLISH_FAILED = 2;
    public static final int EXIT_PUBLISHED_FROM_LAST_COPY = 3;
    public static final int EXIT_STORE_LOCKED = 4;

    // A lock older than this was left by a run that died (an orchestrator's deadline kills a fetch long before);
    // a run that finds the store locked waits this long for the other to finish before giving up.
    static final Duration LOCK_STALE_AFTER = Duration.ofHours(1);
    static final Duration LOCK_POLL = Duration.ofSeconds(5);

    private final @NonNull TSLSnapshotStore store;
    private final @NonNull Path workingCache;
    private final @NonNull TSLHealthIndicator tslHealth;
    private final @NonNull Set<TSLLoader> loaders;
    private final @NonNull TSLLoaderConfigurationProperties loaderProperties;
    private final Duration period;
    private final @NonNull Clock clock;
    private final int keepSnapshots;
    private final @NonNull Duration lockWait;

    // Refreshes the loaders and publishes the working cache, and returns the process exit code: 0 published, 1 the
    // trust lists were below the readiness threshold (nothing published), 2 the store could not be written, 3
    // published, but one or more lists came from their last copy because the download failed, 4 another fetcher
    // held the store for the whole wait (nothing downloaded, nothing published).
    public int fetch() {
        Path root = store.getRoot();
        Optional<TSLSnapshotStore.FetchLock> lock;
        try {
            lock = lock();
        } catch (IOException e) {
            log.error("The trust-list store {} could not be locked", root, e);
            return EXIT_PUBLISH_FAILED;
        }
        if (lock.isEmpty()) {
            log.error("Another trust-list fetch held the store {} for {}; nothing fetched", root, lockWait);
            return EXIT_STORE_LOCKED;
        }
        TSLSnapshotStore.FetchLock held = lock.get();
        try (held) {
            loaders.forEach(TSLLoader::loadTSL);
            return publish();
        } catch (IOException e) {
            log.warn("The trust-list store lock in {} could not be released", root, e);
            return EXIT_PUBLISH_FAILED;
        }
    }

    private Optional<TSLSnapshotStore.FetchLock> lock() throws IOException {
        String owner = ManagementFactory.getRuntimeMXBean().getName();
        Instant giveUp = clock.instant().plus(lockWait);
        while (true) {
            Optional<TSLSnapshotStore.FetchLock> lock = store.tryLock(owner, LOCK_STALE_AFTER);
            if (lock.isPresent() || !clock.instant().isBefore(giveUp)) {
                return lock;
            }
            try {
                Thread.sleep(LOCK_POLL.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Optional.empty();
            }
        }
    }

    private int publish() {
        Health health = tslHealth.health();
        Map<String, Object> details = health.getDetails();
        if (!Status.UP.equals(health.getStatus())) {
            log.error("Trust lists below the readiness threshold, snapshot not published: {}", details);
            return EXIT_BELOW_THRESHOLD;
        }
        Path root = store.getRoot();
        String id;
        try {
            TSLSnapshotManifest manifest = manifest(health);
            id = manifest.id();
            store.publish(workingCache, manifest);
            store.prune(keepSnapshots);
        } catch (IOException e) {
            log.error("Publishing the trust-list snapshot to {} failed", root, e);
            return EXIT_PUBLISH_FAILED;
        }
        Map<String, Map<String, String>> lastCopies = perLoader(TSLLoader::getLastCopies);
        if (!lastCopies.isEmpty()) {
            log.warn("Trust-list snapshot {} published with lists served from their last copy, the download failed: "
                    + "{}; state: {}", id, lastCopies, details);
            return EXIT_PUBLISHED_FROM_LAST_COPY;
        }
        log.info("Trust-list snapshot {} published: {}", id, details);
        return EXIT_PUBLISHED;
    }

    private TSLSnapshotManifest manifest(Health health) throws IOException {
        Instant createdAt = clock.instant();
        List<String> mandatory = loaderProperties.getMandatoryTerritories() == null
                ? List.of() : List.copyOf(loaderProperties.getMandatoryTerritories());
        List<String> fileNames;
        try (Stream<Path> entries = Files.list(workingCache)) {
            fileNames = entries.filter(Files::isRegularFile)
                    .map(file -> file.getFileName().toString())
                    .sorted()
                    .toList();
        }
        return new TSLSnapshotManifest(TSLSnapshotStore.newId(createdAt), createdAt, health.getStatus().getCode(),
                mandatory, health.getDetails(), fileNames.size(), fileNames, period,
                perLoader(TSLLoader::getDownloadedAt));
    }

    // Loader name to the loader's per-list map, for the loaders whose map is not empty; sorted by name.
    private <V> Map<String, Map<String, V>> perLoader(Function<TSLLoader, Map<String, V>> value) {
        Map<String, Map<String, V>> result = new TreeMap<>();
        for (TSLLoader loader : loaders) {
            Map<String, V> lists = value.apply(loader);
            if (!lists.isEmpty()) {
                result.put(loader.getTslName(), lists);
            }
        }
        return Collections.unmodifiableMap(result);
    }
}
