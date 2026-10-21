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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "siva.tsl.fetcher", ignoreUnknownFields = false)
public class TSLFetcherConfigurationProperties {
    // Root of the snapshot store (current, snapshots/<id>/); the webapps read it as siva.tsl.loader.snapshotDirectory
    private File outputDirectory;
    // Snapshots kept besides the current one; older ones are deleted after each successful publish
    private int keepSnapshots = 3;
    // How often the fetcher is scheduled to run, written into the manifest so that the webapps publish it
    // (siva_tsl_refresh_period_seconds) and staleness alerts can be sized from the instance. Unset: not reported.
    private Duration period;
}
