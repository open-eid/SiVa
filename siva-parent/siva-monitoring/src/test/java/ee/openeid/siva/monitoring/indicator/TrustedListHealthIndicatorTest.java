/*
 * Copyright 2026 Mindaugas Kiškis
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

package ee.openeid.siva.monitoring.indicator;

import ee.openeid.tsl.TSLLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrustedListHealthIndicatorTest {

    @Mock
    private TSLLoader genericTslLoader;
    @Mock
    private TSLLoader timemarkTslLoader;

    @Test
    void healthIsUpWhenEveryTrustedListContainsCertificates() {
        when(genericTslLoader.getTslName()).thenReturn("generic");
        when(genericTslLoader.getTrustedCertificateCount()).thenReturn(120);
        when(timemarkTslLoader.getTslName()).thenReturn("timemark");
        when(timemarkTslLoader.getTrustedCertificateCount()).thenReturn(118);

        Health health = new TrustedListHealthIndicator(Set.of(genericTslLoader, timemarkTslLoader)).health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(
                Map.of("generic", 120, "timemark", 118),
                health.getDetails().get(TrustedListHealthIndicator.RESPONSE_PARAM_TRUSTED_CERTIFICATE_COUNTS)
        );
    }

    @Test
    void healthIsDownWhenAnyTrustedListIsEmpty() {
        when(genericTslLoader.getTslName()).thenReturn("generic");
        when(genericTslLoader.getTrustedCertificateCount()).thenReturn(0);
        when(timemarkTslLoader.getTslName()).thenReturn("timemark");
        when(timemarkTslLoader.getTrustedCertificateCount()).thenReturn(118);

        Health health = new TrustedListHealthIndicator(Set.of(genericTslLoader, timemarkTslLoader)).health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(
                Map.of("generic", 0, "timemark", 118),
                health.getDetails().get(TrustedListHealthIndicator.RESPONSE_PARAM_TRUSTED_CERTIFICATE_COUNTS)
        );
    }

    @Test
    void healthIsDownWhenNoTrustedListLoadersExist() {
        Health health = new TrustedListHealthIndicator(Set.of()).health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(
                Map.of(),
                health.getDetails().get(TrustedListHealthIndicator.RESPONSE_PARAM_TRUSTED_CERTIFICATE_COUNTS)
        );
    }
}
