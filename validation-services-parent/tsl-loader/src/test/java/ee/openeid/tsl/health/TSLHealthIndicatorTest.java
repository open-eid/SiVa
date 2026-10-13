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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TSLHealthIndicatorTest {

    private static final Instant REFRESHED = Instant.parse("2026-10-12T20:00:00Z");
    private static final Map<String, Integer> EE_AND_LV = Map.of("EE", 32, "LV", 5);

    @Mock
    private TSLLoader genericLoader;
    @Mock
    private TSLLoader timemarkLoader;

    @Test
    void health_WhenEveryLoaderHoldsTheMandatoryTerritories_Up() {
        stub(genericLoader, "generic", REFRESHED, 37, EE_AND_LV);
        stub(timemarkLoader, "timemark", REFRESHED, 32, Map.of("EE", 32));

        Health health = indicator(List.of("EE")).health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsOnlyKeys("generic", "timemark");
        assertThat(health.getDetails().get("generic")).isEqualTo(Map.of(
                TSLHealthIndicator.DETAIL_CERTIFICATES, 37,
                TSLHealthIndicator.DETAIL_LAST_REFRESH, "2026-10-12T20:00:00Z",
                TSLHealthIndicator.DETAIL_TERRITORIES, EE_AND_LV));
    }

    @Test
    void health_WhenANonMandatoryTerritoryIsMissing_Up() {
        stub(genericLoader, "generic", REFRESHED, 32, Map.of("EE", 32));
        stub(timemarkLoader, "timemark", REFRESHED, 32, Map.of("EE", 32));

        Health health = indicator(List.of("EE")).health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void health_WhenAMandatoryTerritoryIsMissingInOneLoader_DownNamingIt() {
        stub(genericLoader, "generic", REFRESHED, 37, EE_AND_LV);
        stub(timemarkLoader, "timemark", REFRESHED, 5, Map.of("LV", 5));

        Health health = indicator(List.of("EE", "LV")).health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().get("timemark")).isEqualTo(Map.of(
                TSLHealthIndicator.DETAIL_CERTIFICATES, 5,
                TSLHealthIndicator.DETAIL_LAST_REFRESH, "2026-10-12T20:00:00Z",
                TSLHealthIndicator.DETAIL_TERRITORIES, Map.of("LV", 5),
                TSLHealthIndicator.DETAIL_MISSING, List.of("EE")));
        assertThat(health.getDetails().get("generic")).isEqualTo(Map.of(
                TSLHealthIndicator.DETAIL_CERTIFICATES, 37,
                TSLHealthIndicator.DETAIL_LAST_REFRESH, "2026-10-12T20:00:00Z",
                TSLHealthIndicator.DETAIL_TERRITORIES, EE_AND_LV));
    }

    @Test
    void health_WhenAMandatoryTerritoryHasNoCertificates_Down() {
        stub(genericLoader, "generic", REFRESHED, 5, Map.of("EE", 0, "LV", 5));
        stub(timemarkLoader, "timemark", REFRESHED, 32, Map.of("EE", 32));

        Health health = indicator(List.of("EE")).health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void health_WhenNoMandatoryTerritoriesAndEveryLoaderHasCertificates_Up() {
        stub(genericLoader, "generic", REFRESHED, 5, Map.of("LV", 5));
        stub(timemarkLoader, "timemark", REFRESHED, 32, Map.of("EE", 32));

        assertThat(indicator(List.of()).health().getStatus()).isEqualTo(Status.UP);
        assertThat(indicator(null).health().getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void health_WhenNoMandatoryTerritoriesAndALoaderHasNoCertificates_Down() {
        stub(genericLoader, "generic", REFRESHED, 0, Map.of());
        stub(timemarkLoader, "timemark", REFRESHED, 32, Map.of("EE", 32));

        assertThat(indicator(List.of()).health().getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void health_WhenALoaderNeverRefreshed_DownWithoutLastRefreshDetail() {
        stub(genericLoader, "generic", REFRESHED, 32, Map.of("EE", 32));
        stub(timemarkLoader, "timemark", null, 0, Map.of());

        Health health = indicator(List.of("EE")).health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().get("timemark")).isEqualTo(Map.of(
                TSLHealthIndicator.DETAIL_CERTIFICATES, 0,
                TSLHealthIndicator.DETAIL_TERRITORIES, Map.of(),
                TSLHealthIndicator.DETAIL_MISSING, List.of("EE")));
    }

    @Test
    void health_WhenALoaderHoldsTheTerritoriesButNeverRefreshed_Down() {
        stub(genericLoader, "generic", REFRESHED, 32, Map.of("EE", 32));
        stub(timemarkLoader, "timemark", null, 32, Map.of("EE", 32));

        assertThat(indicator(List.of("EE")).health().getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void health_Details_OrderedByLoaderNameThenCertificatesRefreshTerritories() {
        stub(genericLoader, "generic", REFRESHED, 32, Map.of("EE", 32));
        stub(timemarkLoader, "timemark", REFRESHED, 32, Map.of("EE", 32));

        Health health = new TSLHealthIndicator(Set.of(timemarkLoader, genericLoader), properties(List.of("EE"))).health();

        assertThat(health.getDetails().keySet()).containsExactly("generic", "timemark");
        @SuppressWarnings("unchecked")
        Map<String, Object> timemark = (Map<String, Object>) health.getDetails().get("timemark");
        assertThat(timemark.keySet()).containsExactly(TSLHealthIndicator.DETAIL_CERTIFICATES,
                TSLHealthIndicator.DETAIL_LAST_REFRESH, TSLHealthIndicator.DETAIL_TERRITORIES);
    }

    @Test
    void health_WhenNoLoaders_Down() {
        Health health = new TSLHealthIndicator(Set.of(), properties(List.of("EE"))).health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).isEmpty();
    }

    private TSLHealthIndicator indicator(List<String> mandatoryTerritories) {
        return new TSLHealthIndicator(Set.of(genericLoader, timemarkLoader), properties(mandatoryTerritories));
    }

    private static TSLLoaderConfigurationProperties properties(List<String> mandatoryTerritories) {
        TSLLoaderConfigurationProperties properties = new TSLLoaderConfigurationProperties();
        properties.setMandatoryTerritories(mandatoryTerritories);
        return properties;
    }

    private static void stub(TSLLoader loader, String name, Instant lastRefresh, int certificates, Map<String, Integer> territories) {
        when(loader.getTslName()).thenReturn(name);
        when(loader.getLastRefresh()).thenReturn(lastRefresh);
        when(loader.getNumberOfCertificates()).thenReturn(certificates);
        when(loader.getCertificatesByTerritory()).thenReturn(territories);
    }
}
