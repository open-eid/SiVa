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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TSLHealthIndicatorTest {

    private static final Instant REFRESHED = Instant.parse("2026-10-12T20:00:00Z");

    @Mock
    private TSLLoader genericLoader;
    @Mock
    private TSLLoader timemarkLoader;

    @Test
    void health_WhenEveryLoaderRefreshedWithCertificates_Up() {
        stub(genericLoader, "generic", REFRESHED, 120);
        stub(timemarkLoader, "timemark", REFRESHED, 7);

        Health health = new TSLHealthIndicator(Set.of(genericLoader, timemarkLoader)).health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsOnlyKeys("generic", "timemark");
        assertThat(health.getDetails().get("generic")).isEqualTo(Map.of(
                TSLHealthIndicator.DETAIL_CERTIFICATES, 120,
                TSLHealthIndicator.DETAIL_LAST_REFRESH, "2026-10-12T20:00:00Z"));
    }

    @Test
    void health_WhenEveryLoaderRefreshedWithCertificates_DetailsOrderedByNameThenCertificatesAndRefresh() {
        stub(genericLoader, "generic", REFRESHED, 120);
        stub(timemarkLoader, "timemark", REFRESHED, 7);

        Health health = new TSLHealthIndicator(Set.of(timemarkLoader, genericLoader)).health();

        assertThat(health.getDetails().keySet()).containsExactly("generic", "timemark");
        @SuppressWarnings("unchecked")
        Map<String, Object> timemark = (Map<String, Object>) health.getDetails().get("timemark");
        assertThat(timemark.keySet())
                .containsExactly(TSLHealthIndicator.DETAIL_CERTIFICATES, TSLHealthIndicator.DETAIL_LAST_REFRESH);
    }

    @Test
    void health_WhenALoaderNeverRefreshed_DownWithoutLastRefreshDetail() {
        stub(genericLoader, "generic", REFRESHED, 120);
        stub(timemarkLoader, "timemark", null, 0);

        Health health = new TSLHealthIndicator(Set.of(genericLoader, timemarkLoader)).health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().get("timemark")).isEqualTo(Map.of(TSLHealthIndicator.DETAIL_CERTIFICATES, 0));
    }

    @Test
    void health_WhenALoaderHasCertificatesButNeverRefreshed_Down() {
        stub(genericLoader, "generic", REFRESHED, 120);
        stub(timemarkLoader, "timemark", null, 7);

        Health health = new TSLHealthIndicator(Set.of(genericLoader, timemarkLoader)).health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void health_WhenALoaderRefreshedWithoutCertificates_Down() {
        stub(genericLoader, "generic", REFRESHED, 0);
        stub(timemarkLoader, "timemark", REFRESHED, 7);

        Health health = new TSLHealthIndicator(Set.of(genericLoader, timemarkLoader)).health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().get("generic")).isEqualTo(Map.of(
                TSLHealthIndicator.DETAIL_CERTIFICATES, 0,
                TSLHealthIndicator.DETAIL_LAST_REFRESH, "2026-10-12T20:00:00Z"));
    }

    @Test
    void health_WhenNoLoaders_Down() {
        Health health = new TSLHealthIndicator(Set.of()).health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).isEmpty();
    }

    private static void stub(TSLLoader loader, String name, Instant lastRefresh, int certificates) {
        when(loader.getTslName()).thenReturn(name);
        when(loader.getLastRefresh()).thenReturn(lastRefresh);
        when(loader.getNumberOfCertificates()).thenReturn(certificates);
    }
}
