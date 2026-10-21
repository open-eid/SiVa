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

package ee.openeid.tsl.health;

import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Health of the trusted lists: UP when every loader has refreshed at least once and holds the mandatory
 * territories ({@code siva.tsl.loader.mandatoryTerritories}), each with at least one trust anchor. Other
 * territories are reported but never decide the status, so one member state's list being unreachable for a
 * while does not take the service out of rotation. With no mandatory territory configured, a loader is UP once
 * it holds any trust anchor at all. Member of the {@code readiness} probe group.
 */
@Component("tslHealthIndicator")
public class TSLHealthIndicator implements HealthIndicator {

    public static final String DETAIL_CERTIFICATES = "certificates";
    public static final String DETAIL_LAST_REFRESH = "lastRefresh";
    public static final String DETAIL_TERRITORIES = "territories";
    public static final String DETAIL_MISSING = "missingMandatoryTerritories";
    public static final String DETAIL_SNAPSHOT = "snapshot";
    public static final String DETAIL_LAST_COPY = "lastCopy";

    private final Set<TSLLoader> loaders;
    private final TSLLoaderConfigurationProperties configurationProperties;

    public TSLHealthIndicator(Set<TSLLoader> loaders, TSLLoaderConfigurationProperties configurationProperties) {
        this.loaders = loaders;
        this.configurationProperties = configurationProperties;
    }

    @Override
    public Health health() {
        List<String> mandatory = configurationProperties.getMandatoryTerritories() == null
                ? List.of() : configurationProperties.getMandatoryTerritories();
        boolean up = !loaders.isEmpty();
        Map<String, Object> details = new LinkedHashMap<>();
        for (TSLLoader loader : loaders.stream().sorted(Comparator.comparing(TSLLoader::getTslName)).toList()) {
            Instant lastRefresh = loader.getLastRefresh();
            int certificates = loader.getNumberOfCertificates();
            Map<String, Integer> territories = loader.getCertificatesByTerritory();
            List<String> missing = mandatory.stream()
                    .filter(territory -> territories.getOrDefault(territory, 0) <= 0)
                    .toList();
            boolean loaded = mandatory.isEmpty() ? certificates > 0 : missing.isEmpty();
            up &= lastRefresh != null && loaded;

            Map<String, Object> loaderDetails = new LinkedHashMap<>();
            loaderDetails.put(DETAIL_CERTIFICATES, certificates);
            if (lastRefresh != null) {
                loaderDetails.put(DETAIL_LAST_REFRESH, lastRefresh.toString());
            }
            loaderDetails.put(DETAIL_TERRITORIES, territories);
            if (loader.getSnapshotId() != null) {
                loaderDetails.put(DETAIL_SNAPSHOT, loader.getSnapshotId());
            }
            if (!missing.isEmpty()) {
                loaderDetails.put(DETAIL_MISSING, missing);
            }
            Map<String, String> lastCopies = loader.getLastCopies();
            if (!lastCopies.isEmpty()) {
                loaderDetails.put(DETAIL_LAST_COPY, lastCopies);
            }
            details.put(loader.getTslName(), loaderDetails);
        }
        return (up ? Health.up() : Health.down()).withDetails(details).build();
    }
}
