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
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TSLMetricsTest {

    private static final Instant EE_NEXT_UPDATE = Instant.parse("2026-11-20T00:00:00Z");
    private static final Instant LOTL_NEXT_UPDATE = Instant.parse("2027-03-17T12:04:06Z");
    private static final Instant SNAPSHOT_CREATED = Instant.parse("2026-10-14T06:01:08Z");

    @Mock
    private TSLLoader loader;

    private final TSLLoaderConfigurationProperties properties = new TSLLoaderConfigurationProperties();
    private final SimpleMeterRegistry registry = new SimpleMeterRegistry();

    @BeforeEach
    void setUp() {
        lenient().when(loader.getTslName()).thenReturn("generic");
        properties.setTrustedTerritories(List.of("EE", "LV"));
        properties.setMandatoryTerritories(List.of("EE"));
    }

    @Test
    void anchorsAreReportedPerConfiguredTerritory() {
        when(loader.getCertificatesByTerritory()).thenReturn(Map.of("EE", 32, "FI", 7));

        bind();

        assertThat(anchors("EE"), is(32.0));
        assertThat(registry.find(TSLMetrics.TRUST_ANCHORS).tag("territory", "FI").gauge(), nullValue());
    }

    @Test
    void configuredTerritoryWithoutAnchorsReadsZero() {
        when(loader.getCertificatesByTerritory()).thenReturn(Map.of("EE", 32));

        bind();

        assertThat(anchors("LV"), is(0.0));
    }

    @Test
    void mandatoryTerritoryIsReportedWhenTrustedTerritoriesAreEmpty() {
        properties.setTrustedTerritories(List.of());
        properties.setMandatoryTerritories(List.of("EE_T"));
        when(loader.getCertificatesByTerritory()).thenReturn(Map.of("EE_T", 32));

        bind();

        assertThat(anchors("EE_T"), is(32.0));
        assertThat(registry.find(TSLMetrics.TRUST_ANCHORS).tag("territory", "EE").gauge(), nullValue());
    }

    @Test
    void anchorsSayWhetherTheTerritoryIsMandatory() {
        bind();

        assertThat(registry.get(TSLMetrics.TRUST_ANCHORS).tag("territory", "EE").gauge().getId().getTag("mandatory"),
                is("true"));
        assertThat(registry.get(TSLMetrics.TRUST_ANCHORS).tag("territory", "LV").gauge().getId().getTag("mandatory"),
                is("false"));
    }

    @Test
    void nextUpdateIsReportedInUnixSecondsPerTerritoryAndForTheLotl() {
        when(loader.getNextUpdateByTerritory()).thenReturn(Map.of("EE", EE_NEXT_UPDATE));
        when(loader.getLotlNextUpdate()).thenReturn(LOTL_NEXT_UPDATE);

        bind();

        assertThat(nextUpdate("tl", "EE"), is((double) EE_NEXT_UPDATE.getEpochSecond()));
        assertThat(nextUpdate("lotl", TSLMetrics.LOTL_TERRITORY), is((double) LOTL_NEXT_UPDATE.getEpochSecond()));
    }

    @Test
    void nextUpdateIsNotANumberUntilTheListWasParsed() {
        when(loader.getNextUpdateByTerritory()).thenReturn(Map.of());

        bind();

        assertThat(Double.isNaN(nextUpdate("tl", "LV")), is(true));
    }

    @Test
    void snapshotCreationIsReportedInSnapshotMode() {
        properties.setLoadFromCache(true);
        properties.setSnapshotDirectory(new File("/var/siva/tsl"));
        when(loader.getSnapshotCreatedAt()).thenReturn(SNAPSHOT_CREATED);

        bind();

        assertThat(registry.get(TSLMetrics.SNAPSHOT_CREATED).tag("loader", "generic").gauge().value(),
                is((double) SNAPSHOT_CREATED.getEpochSecond()));
    }

    @Test
    void snapshotCreationIsNotReportedOutsideSnapshotMode() {
        bind();

        assertThat(registry.find(TSLMetrics.SNAPSHOT_CREATED).gauges(), empty());
    }

    @Test
    void gaugesCarryTheLoaderName() {
        bind();

        assertThat(registry.find(TSLMetrics.TRUST_ANCHORS).tag("loader", "generic").gauges().stream()
                .map(gauge -> gauge.getId().getTag("territory")).sorted().toList(), contains("EE", "LV"));
        assertThat(registry.find(TSLMetrics.NEXT_UPDATE).tag("kind", "lotl").gauge(), notNullValue());
    }

    private void bind() {
        new TSLMetrics(Set.of(loader), properties).bindTo(registry);
    }

    private double anchors(String territory) {
        return registry.get(TSLMetrics.TRUST_ANCHORS).tag("territory", territory).gauge().value();
    }

    private double nextUpdate(String kind, String territory) {
        return registry.get(TSLMetrics.NEXT_UPDATE).tag("kind", kind).tag("territory", territory).gauge().value();
    }
}
