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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@RequiredArgsConstructor
public class TrustedListHealthIndicator implements HealthIndicator {

    public static final String RESPONSE_PARAM_TRUSTED_CERTIFICATE_COUNTS = "trustedCertificateCounts";

    @NonNull
    private final Set<TSLLoader> tslLoaders;

    @Override
    public Health health() {
        Map<String, Integer> trustedCertificateCounts = new TreeMap<>();
        for (TSLLoader tslLoader : tslLoaders) {
            trustedCertificateCounts.put(tslLoader.getTslName(), tslLoader.getTrustedCertificateCount());
        }

        boolean ready = !trustedCertificateCounts.isEmpty()
                && trustedCertificateCounts.values().stream().allMatch(count -> count > 0);
        Health.Builder health = ready ? Health.up() : Health.down();
        return health
                .withDetail(RESPONSE_PARAM_TRUSTED_CERTIFICATE_COUNTS, trustedCertificateCounts)
                .build();
    }
}
