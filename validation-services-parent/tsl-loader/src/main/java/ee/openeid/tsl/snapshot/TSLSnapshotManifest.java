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

package ee.openeid.tsl.snapshot;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

// manifest.json of a published snapshot: the fetcher's `tsl` health at publication, i.e. per loader the
// anchors, territories, missing mandatory territories and lists served from their last copy, as a webapp
// reports them after loading the same files; plus what only the fetcher knows and the webapps publish as
// metrics: how often it runs (`period`) and, per loader and territory (`EU` for the list of trusted lists),
// when each list was last downloaded from its publisher (`downloadedAt`, the cache file's modification time,
// which a copy served in place of a failed download leaves as it was); and the names of the snapshot's files
// (`fileNames`), so that a reader can tell a complete snapshot from one a copy without rename atomicity (a sync
// through object storage) is still delivering. `fileNames`, `period` and `downloadedAt` are null in manifests
// written before they existed.
public record TSLSnapshotManifest(
        String id,
        Instant createdAt,
        String status,
        List<String> mandatoryTerritories,
        Map<String, Object> tsl,
        int files,
        List<String> fileNames,
        Duration period,
        Map<String, Map<String, Instant>> downloadedAt
) {
}
