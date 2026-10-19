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

package ee.openeid.tsl.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "siva.tsl.loader", ignoreUnknownFields = false)
public class TSLLoaderConfigurationProperties {
    private boolean loadFromCache = false;
    // DSS file cache the trusted lists are downloaded into or, with loadFromCache, read from; null = java.io.tmpdir
    private File cacheDirectory;
    // With loadFromCache: a snapshot store published by the fetcher (`current` names the snapshot under
    // snapshots/); the loader reads that snapshot instead of cacheDirectory and follows `current` when it changes
    private File snapshotDirectory;
    // How often a loader in snapshot mode checks `current` for another snapshot; zero or null = scheduled refresh only
    private Duration snapshotPollInterval = Duration.ofMinutes(1);
    // In snapshot mode: refuse a snapshot whose manifest is older than this (or has no readable manifest) and keep
    // the loaded state, so a store that stopped being refreshed, or was pointed at an old snapshot, cannot feed a
    // fresh pod lists older than the operator allows. Null = no age limit
    private Duration snapshotMaxAge;
    private Duration onlineCacheExpirationTime = Duration.parse("PT1H");
    private String url = "https://ec.europa.eu/tools/lotl/eu-lotl.xml";
    private String ojUrl = "https://eur-lex.europa.eu/eli/C/2026/1944/oj";
    private String lotlRootSchemeInfoUri = "https://ec.europa.eu/tools/lotl/eu-lotl-legalnotice.html";
    private String schedulerCron = "0 0 3 * * ?";
    // Territories whose trusted list must hold at least one anchor for the service to count as ready (tsl health);
    // any other trusted territory may be missing, stale or failing. Empty: any loaded trusted list is enough
    private List<String> mandatoryTerritories = Arrays.asList("EE");
    private List<String> trustedTerritories =   Arrays.asList("AT", "BE", "BG", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "EL", "HU", "HR", "IE", "IS", "IT", "LT", "LU", "LV", "LI", "MT", "NO", "NL", "PL", "PT", "RO", "SE", "SI", "SK", "UK");
    private Resource sslTruststorePath;
    // Name of a Spring SSL bundle (spring.ssl.bundle.pem|jks.<name>) whose trust store replaces sslTruststorePath for
    // the trust-list downloads; read at every connection, so a bundle with reload-on-update follows its files
    private String sslBundle;
    private String sslTruststoreType = "PKCS12";
    @SuppressWarnings("squid:S2068") //default password
    private String sslTruststorePassword = "digidoc4j-password";
    private boolean lotlPivotSupportEnabled = true;
    private String lotlTruststoreType = "PKCS12";
    private Resource lotlTruststorePath;
    // Name of a Spring SSL bundle whose trust store holds the LOTL signer certificates instead of lotlTruststorePath;
    // read before every refresh
    private String lotlBundle;
    @SuppressWarnings("squid:S2068") //default password
    private String lotlTruststorePassword = "lotl-truststore-password";
}
