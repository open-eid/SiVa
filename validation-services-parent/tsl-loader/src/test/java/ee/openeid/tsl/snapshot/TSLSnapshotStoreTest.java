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

package ee.openeid.tsl.snapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TSLSnapshotStoreTest {

    private static final Instant CREATED = Instant.parse("2026-10-13T18:30:00Z");

    @TempDir
    private Path root;
    @TempDir
    private Path source;

    private TSLSnapshotStore store;

    @BeforeEach
    void setUp() throws IOException {
        store = new TSLSnapshotStore(root);
        Files.writeString(source.resolve("https___lotl_example_eu-lotl_xml"), "lotl");
        Files.writeString(source.resolve("https___tl_example_EE_xml"), "ee");
        Files.createDirectory(source.resolve("ignored-subdirectory"));
    }

    @Test
    void newId_IsTheUtcTimestampOfCreation() {
        assertThat(TSLSnapshotStore.newId(CREATED), is("20261013T183000Z"));
    }

    @Test
    void currentId_WhenNothingPublished_Empty() {
        assertThat(store.currentId(), is(Optional.empty()));
    }

    @Test
    void publish_CopiesTheFilesWritesTheManifestAndPointsCurrentAtTheSnapshot() throws IOException {
        TSLSnapshotManifest manifest = manifest("20261013T183000Z");

        store.publish(source, manifest);

        assertThat(store.currentId(), is(Optional.of("20261013T183000Z")));
        Path snapshot = store.snapshotDirectory("20261013T183000Z");
        assertThat(Files.readString(snapshot.resolve("https___lotl_example_eu-lotl_xml")), is("lotl"));
        assertThat(Files.readString(snapshot.resolve("https___tl_example_EE_xml")), is("ee"));
        assertThat(Files.exists(snapshot.resolve("ignored-subdirectory")), is(false));
        assertThat(store.manifest("20261013T183000Z"), is(Optional.of(manifest)));
        assertThat(Files.exists(root.resolve("snapshots/20261013T183000Z.tmp")), is(false));
    }

    @Test
    void publish_WhenTheSnapshotExists_Refuses() throws IOException {
        store.publish(source, manifest("20261013T183000Z"));

        assertThrows(IOException.class, () -> store.publish(source, manifest("20261013T183000Z")));
    }

    @Test
    void currentId_WhenThePointerIsNotAnId_Empty() throws IOException {
        Files.writeString(root.resolve(TSLSnapshotStore.CURRENT_FILE), "../../etc\n");

        assertThat(store.currentId(), is(Optional.empty()));
    }

    @Test
    void currentId_WhenThePointerIsOversized_Empty() throws IOException {
        Files.write(root.resolve(TSLSnapshotStore.CURRENT_FILE), new byte[2048]);

        assertThat(store.currentId(), is(Optional.empty()));
    }

    @Test
    void manifest_WhenUnreadable_Empty() throws IOException {
        Files.createDirectories(store.snapshotDirectory("broken"));
        Files.writeString(store.snapshotDirectory("broken").resolve(TSLSnapshotStore.MANIFEST_FILE), "{");

        assertThat(store.manifest("broken"), is(Optional.empty()));
        assertThat(store.manifest("absent"), is(Optional.empty()));
    }

    @Test
    void prune_KeepsTheCurrentAndTheNewestSnapshotsAndRemovesStagingLeftovers() throws IOException {
        for (String id : List.of("20261013T010000Z", "20261013T020000Z", "20261013T030000Z", "20261013T040000Z")) {
            store.publish(source, manifest(id));
        }
        Files.createDirectories(root.resolve("snapshots/20261013T050000Z.tmp"));
        Files.writeString(root.resolve(TSLSnapshotStore.CURRENT_FILE), "20261013T020000Z");

        List<String> removed = store.prune(1);

        assertThat(removed, contains("20261013T050000Z.tmp", "20261013T030000Z", "20261013T010000Z"));
        assertThat(Files.isDirectory(store.snapshotDirectory("20261013T040000Z")), is(true));
        assertThat(Files.isDirectory(store.snapshotDirectory("20261013T020000Z")), is(true));
        assertThat(Files.exists(store.snapshotDirectory("20261013T030000Z")), is(false));
        assertThat(Files.exists(root.resolve("snapshots/20261013T050000Z.tmp")), is(false));
    }

    @Test
    void prune_WhenNothingPublished_NothingToDo() throws IOException {
        assertThat(store.prune(3), is(empty()));
        assertThat(store.prune(0), is(empty()));
    }

    @Test
    void publish_WritesThePeriodAndTheDownloadTimesAsIsoText() throws IOException {
        store.publish(source, manifest("20261013T183000Z"));

        String json = Files.readString(store.snapshotDirectory("20261013T183000Z").resolve("manifest.json"));

        assertThat(json, containsString("\"period\" : \"PT6H\""));
        assertThat(json, containsString("\"EE\" : \"2026-10-13T18:00:00Z\""));
    }

    @Test
    void manifest_WrittenBeforeThePeriodAndDownloadTimesExisted_ReadsWithoutThem() throws IOException {
        Path snapshot = store.snapshotDirectory("20261013T183000Z");
        Files.createDirectories(snapshot);
        Files.writeString(snapshot.resolve("manifest.json"), """
                {"id":"20261013T183000Z","createdAt":"2026-10-13T18:30:00Z","status":"UP","mandatoryTerritories":["EE"],
                 "tsl":{},"files":2}
                """);

        Optional<TSLSnapshotManifest> manifest = store.manifest("20261013T183000Z");

        assertThat(manifest.map(TSLSnapshotManifest::createdAt), is(Optional.of(CREATED)));
        assertThat(manifest.get().period(), nullValue());
        assertThat(manifest.get().downloadedAt(), nullValue());
    }

    private static TSLSnapshotManifest manifest(String id) {
        return new TSLSnapshotManifest(id, CREATED, "UP", List.of("EE"),
                Map.of("generic", Map.of("certificates", 32, "territories", Map.of("EE", 32))), 2,
                Duration.ofHours(6), Map.of("generic", Map.of("EE", Instant.parse("2026-10-13T18:00:00Z"))));
    }
}
