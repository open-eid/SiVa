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

package ee.openeid.tsl.metrics;

import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

// Trust-list state per loader for alerting: anchors, list expiry and last download per configured territory,
// snapshot age, and the refresh period the instance runs on. Gauges are registered for the configured
// territories (trusted and mandatory) up front, so a territory whose list never loaded reads 0 anchors instead
// of being absent; values are read from the loader's state at scrape time. The anchors carry
// mandatory=true|false and the period is a gauge of its own, so alerts take the effective configuration from
// the metrics instead of repeating it. The list of trusted lists is reported as territory EU, kind lotl.
@Component
@RequiredArgsConstructor
public class TSLMetrics implements MeterBinder {

    static final String TRUST_ANCHORS = "siva.tsl.trust.anchors";
    static final String NEXT_UPDATE = "siva.tsl.next.update.timestamp";
    static final String LIST_DOWNLOADED = "siva.tsl.list.downloaded.timestamp";
    static final String SNAPSHOT_CREATED = "siva.tsl.snapshot.created.timestamp";
    static final String REFRESH_PERIOD = "siva.tsl.refresh.period";
    static final String LOTL_TERRITORY = TSLLoader.LOTL_TERRITORY;
    private static final String DOWNLOADED_DESCRIPTION = "When the trusted list was last downloaded from its "
            + "publisher; a copy served after a failed download keeps the earlier time";

    private final @NonNull Set<TSLLoader> loaders;
    private final @NonNull TSLLoaderConfigurationProperties configurationProperties;

    @Override
    public void bindTo(MeterRegistry registry) {
        Set<String> territories = configuredTerritories();
        List<String> mandatory = configurationProperties.getMandatoryTerritories() == null
                ? List.of() : configurationProperties.getMandatoryTerritories();
        boolean snapshotMode = configurationProperties.isLoadFromCache()
                && configurationProperties.getSnapshotDirectory() != null;
        for (TSLLoader loader : loaders) {
            String name = loader.getTslName();
            for (String territory : territories) {
                Gauge.builder(TRUST_ANCHORS, loader, l -> l.getCertificatesByTerritory().getOrDefault(territory, 0))
                        .description("Trust anchors held for the territory after DSS accepted its trusted list")
                        .tags("loader", name, "territory", territory,
                                "mandatory", String.valueOf(mandatory.contains(territory)))
                        .register(registry);
                registerTimestamp(registry, NEXT_UPDATE, loader,
                        l -> l.getNextUpdateByTerritory().get(territory),
                        "NextUpdate of the trusted list; after it the list counts as expired",
                        "loader", name, "kind", "tl", "territory", territory);
                registerTimestamp(registry, LIST_DOWNLOADED, loader, l -> l.getDownloadedAt().get(territory),
                        DOWNLOADED_DESCRIPTION, "loader", name, "kind", "tl", "territory", territory);
            }
            registerTimestamp(registry, NEXT_UPDATE, loader, TSLLoader::getLotlNextUpdate,
                    "NextUpdate of the trusted list; after it the list counts as expired",
                    "loader", name, "kind", "lotl", "territory", LOTL_TERRITORY);
            registerTimestamp(registry, LIST_DOWNLOADED, loader, l -> l.getDownloadedAt().get(LOTL_TERRITORY),
                    DOWNLOADED_DESCRIPTION, "loader", name, "kind", "lotl", "territory", LOTL_TERRITORY);
            Gauge.builder(REFRESH_PERIOD, loader, l -> seconds(l.getRefreshPeriod()))
                    .description("How often the trusted lists are refreshed, as configured (online: "
                            + "siva.tsl.loader.refreshPeriod; snapshot: the fetcher's period from the manifest)")
                    .baseUnit("seconds")
                    .tags("loader", name)
                    .register(registry);
            if (snapshotMode) {
                registerTimestamp(registry, SNAPSHOT_CREATED, loader, TSLLoader::getSnapshotCreatedAt,
                        "When the loaded trust-list snapshot was published", "loader", name);
            }
        }
    }

    private Set<String> configuredTerritories() {
        Set<String> territories = new TreeSet<>();
        addAll(territories, configurationProperties.getTrustedTerritories());
        addAll(territories, configurationProperties.getMandatoryTerritories());
        return territories;
    }

    private static void addAll(Set<String> target, List<String> territories) {
        if (territories != null) {
            target.addAll(territories);
        }
    }

    // Seconds; NaN while the period is unknown (never loaded, a plain cache directory, an older manifest).
    private static double seconds(Duration duration) {
        return duration == null ? Double.NaN : duration.toMillis() / 1000.0;
    }

    // Unix seconds; NaN while the value is unknown (never loaded, never parsed).
    private static void registerTimestamp(MeterRegistry registry, String name, TSLLoader loader,
                                          Function<TSLLoader, Instant> value,
                                          String description, String... tags) {
        ToDoubleFunction<TSLLoader> seconds = l -> {
            Instant instant = value.apply(l);
            return instant == null ? Double.NaN : instant.toEpochMilli() / 1000.0;
        };
        Gauge.builder(name, loader, seconds)
                .description(description)
                .baseUnit("seconds")
                .tags(tags)
                .register(registry);
    }
}
