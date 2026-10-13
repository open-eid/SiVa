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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

// The directory the trust-list fetcher publishes into and the webapps read from:
//   current                   id of the snapshot to load (one line), replaced atomically
//   snapshots/<id>/           the DSS file cache of one fetch (file name = URL with non-alphanumerics as '_')
//   snapshots/<id>/manifest.json
// A snapshot directory is complete before `current` names it, so a reader never sees a half-written one;
// older snapshots stay until pruned so a reader in the middle of loading one is not pulled away.
@Slf4j
@RequiredArgsConstructor
public class TSLSnapshotStore {

    public static final String CURRENT_FILE = "current";
    public static final String SNAPSHOTS_DIRECTORY = "snapshots";
    public static final String MANIFEST_FILE = "manifest.json";

    private static final String TEMPORARY_SUFFIX = ".tmp";
    private static final Pattern SNAPSHOT_ID = Pattern.compile("[A-Za-z0-9][A-Za-z0-9_-]{0,63}");
    private static final DateTimeFormatter ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
            .withZone(ZoneOffset.UTC);

    @Getter
    private final @NonNull Path root;
    private final JsonMapper json = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    // The snapshot `current` names; empty when nothing has been published yet or the pointer is unreadable.
    // A malformed id counts as none: it must never leave the snapshots directory.
    public Optional<String> currentId() {
        Path current = root.resolve(CURRENT_FILE);
        if (!Files.isRegularFile(current)) {
            return Optional.empty();
        }
        try {
            String id = Files.readString(current, StandardCharsets.UTF_8).trim();
            if (!SNAPSHOT_ID.matcher(id).matches()) {
                log.warn("Trust-list snapshot pointer {} holds no snapshot id: '{}'", current, id);
                return Optional.empty();
            }
            return Optional.of(id);
        } catch (IOException e) {
            String reason = e.getMessage();
            log.warn("Trust-list snapshot pointer {} is unreadable: {}", current, reason);
            return Optional.empty();
        }
    }

    public Path snapshotDirectory(String id) {
        return root.resolve(SNAPSHOTS_DIRECTORY).resolve(id);
    }

    public Optional<TSLSnapshotManifest> manifest(String id) {
        Path manifest = snapshotDirectory(id).resolve(MANIFEST_FILE);
        if (!Files.isRegularFile(manifest)) {
            return Optional.empty();
        }
        try {
            return Optional.of(json.readValue(Files.readAllBytes(manifest), TSLSnapshotManifest.class));
        } catch (IOException | JacksonException e) {
            String reason = e.getMessage();
            log.warn("Trust-list snapshot manifest {} is unreadable: {}", manifest, reason);
            return Optional.empty();
        }
    }

    public static String newId(Instant createdAt) {
        return ID_FORMAT.format(createdAt);
    }

    // Copies the regular files of `source` (a DSS file cache) into a new snapshot directory, writes the
    // manifest beside them and only then points `current` at it. The id comes from the manifest.
    public void publish(Path source, TSLSnapshotManifest manifest) throws IOException {
        Path target = snapshotDirectory(manifest.id());
        Path staging = target.resolveSibling(target.getFileName() + TEMPORARY_SUFFIX);
        if (Files.exists(target)) {
            throw new IOException("Trust-list snapshot " + target + " exists already");
        }
        Files.createDirectories(staging);
        try (Stream<Path> files = Files.list(source)) {
            for (Path file : files.filter(Files::isRegularFile).toList()) {
                Files.copy(file, staging.resolve(file.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        Files.write(staging.resolve(MANIFEST_FILE), json.writeValueAsBytes(manifest));
        Files.move(staging, target);
        String id = manifest.id();
        int files = manifest.files();
        replace(root.resolve(CURRENT_FILE), id + System.lineSeparator());
        log.info("Published trust-list snapshot {} ({} files) to {}", id, files, root);
    }

    // Deletes every snapshot but the current one and the `keep` newest, plus staging directories a crashed
    // publish left behind. Returns the ids removed.
    public List<String> prune(int keep) throws IOException {
        Path snapshots = root.resolve(SNAPSHOTS_DIRECTORY);
        if (!Files.isDirectory(snapshots)) {
            return List.of();
        }
        String current = currentId().orElse("");
        List<String> removed = new ArrayList<>();
        List<Path> published;
        try (Stream<Path> entries = Files.list(snapshots)) {
            List<Path> directories = entries.filter(Files::isDirectory).sorted(Comparator.reverseOrder()).toList();
            published = new ArrayList<>();
            for (Path directory : directories) {
                if (directory.getFileName().toString().endsWith(TEMPORARY_SUFFIX)) {
                    deleteRecursively(directory);
                    removed.add(directory.getFileName().toString());
                } else {
                    published.add(directory);
                }
            }
        }
        int kept = 0;
        for (Path directory : published) {
            String id = directory.getFileName().toString();
            if (id.equals(current) || kept < keep) {
                kept++;
                continue;
            }
            deleteRecursively(directory);
            removed.add(id);
        }
        if (!removed.isEmpty()) {
            log.info("Pruned trust-list snapshots {}", removed);
        }
        return removed;
    }

    private static void replace(Path file, String content) throws IOException {
        Path temporary = file.resolveSibling(file.getFileName() + TEMPORARY_SUFFIX);
        Files.writeString(temporary, content, StandardCharsets.UTF_8);
        try {
            Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            log.debug("Atomic replace of {} unsupported, falling back to a plain replace", file);
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteRecursively(Path directory) throws IOException {
        List<Path> contents;
        try (Stream<Path> entries = Files.walk(directory)) {
            contents = entries.sorted(Comparator.reverseOrder()).toList();
        }
        for (Path path : contents) {
            Files.delete(path);
        }
    }
}
