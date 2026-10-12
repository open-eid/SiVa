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
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Health of the trusted lists: UP once every {@link TSLLoader} has refreshed at least once and
 * came out of it with at least one trust anchor, DOWN otherwise. Registered as the {@code tsl}
 * health component: the webapp adds it to the readiness group, so a pod whose trust lists never
 * loaded (or were emptied by a refresh) does not receive traffic, and as part of the default
 * aggregate it also turns {@code /monitoring/health} and the heartbeat DOWN in that state.
 */
@Component("tslHealthIndicator")
public class TSLHealthIndicator implements HealthIndicator {

    public static final String DETAIL_CERTIFICATES = "certificates";
    public static final String DETAIL_LAST_REFRESH = "lastRefresh";

    private final Set<TSLLoader> loaders;

    public TSLHealthIndicator(Set<TSLLoader> loaders) {
        this.loaders = loaders;
    }

    @Override
    public Health health() {
        boolean up = !loaders.isEmpty();
        Map<String, Object> details = new LinkedHashMap<>();
        for (TSLLoader loader : loaders.stream().sorted(Comparator.comparing(TSLLoader::getTslName)).toList()) {
            Instant lastRefresh = loader.getLastRefresh();
            int certificates = loader.getNumberOfCertificates();
            up &= lastRefresh != null && certificates > 0;
            Map<String, Object> loaderDetails = new LinkedHashMap<>();
            loaderDetails.put(DETAIL_CERTIFICATES, certificates);
            if (lastRefresh != null) {
                loaderDetails.put(DETAIL_LAST_REFRESH, lastRefresh.toString());
            }
            details.put(loader.getTslName(), loaderDetails);
        }
        return (up ? Health.up() : Health.down()).withDetails(details).build();
    }
}
