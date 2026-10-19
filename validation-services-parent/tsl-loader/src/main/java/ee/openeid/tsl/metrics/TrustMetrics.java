/*
 * Copyright 2016 - 2026 Riigi Infosüsteemi Amet
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

import ee.openeid.tsl.SslBundleCertificates;
import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

// The mounted trust material per SSL bundle the loader configuration names (siva.tsl.loader.sslBundle,
// lotlBundle) for alerting: how many certificates it holds, when the first of them expires and how often Spring
// Boot reloaded it. Read from the bundle registry at scrape time, so a reload shows at once; nothing is
// registered when no bundle is configured (classpath truststores).
@Component
@RequiredArgsConstructor
public class TrustMetrics implements MeterBinder {

    static final String CERTIFICATES = "siva.trust.certificates";
    static final String EARLIEST_EXPIRY = "siva.trust.earliest.expiry.timestamp";
    static final String RELOADS = "siva.trust.reloads";

    private final @NonNull TSLLoaderConfigurationProperties configurationProperties;
    private final @NonNull ObjectProvider<SslBundles> sslBundles;

    @Override
    public void bindTo(MeterRegistry registry) {
        SslBundles bundles = sslBundles.getIfAvailable();
        Set<String> names = bundleNames();
        if (bundles == null || names.isEmpty()) {
            return;
        }
        for (String name : names) {
            Gauge.builder(CERTIFICATES, bundles, b -> SslBundleCertificates.of(b.getBundle(name)).size())
                    .description("Certificates the mounted trust material holds")
                    .tag("bundle", name)
                    .register(registry);
            Gauge.builder(EARLIEST_EXPIRY, bundles, b -> earliestExpiry(b, name))
                    .description("Expiry of the first certificate in the mounted trust material to run out")
                    .baseUnit("seconds")
                    .tag("bundle", name)
                    .register(registry);
            Counter reloads = Counter.builder(RELOADS)
                    .description("Reloads of the mounted trust material by Spring Boot")
                    .tag("bundle", name)
                    .register(registry);
            bundles.addBundleUpdateHandler(name, bundle -> reloads.increment());
        }
    }

    private Set<String> bundleNames() {
        Set<String> names = new TreeSet<>();
        for (String name : new String[] {configurationProperties.getSslBundle(), configurationProperties.getLotlBundle()}) {
            if (name != null && !name.isBlank()) {
                names.add(name);
            }
        }
        return names;
    }

    // Unix seconds; NaN while the bundle holds no certificate.
    private static double earliestExpiry(SslBundles bundles, String name) {
        return SslBundleCertificates.of(bundles.getBundle(name)).stream()
                .map(X509Certificate::getNotAfter)
                .mapToLong(Date::getTime)
                .min()
                .stream()
                .mapToDouble(millis -> millis / 1000.0)
                .findFirst()
                .orElse(Double.NaN);
    }
}
