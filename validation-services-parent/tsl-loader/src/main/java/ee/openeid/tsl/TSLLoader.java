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

package ee.openeid.tsl;

import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import ee.openeid.tsl.snapshot.TSLSnapshotManifest;
import ee.openeid.tsl.snapshot.TSLSnapshotStore;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.tsl.LOTLInfo;
import eu.europa.esig.dss.model.tsl.ParsingInfoRecord;
import eu.europa.esig.dss.model.tsl.TLInfo;
import eu.europa.esig.dss.model.tsl.TLValidationJobSummary;
import eu.europa.esig.dss.model.tsl.TrustProperties;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.CommonCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.tsl.function.EULOTLOtherTSLPointer;
import eu.europa.esig.dss.tsl.function.EUTLOtherTSLPointer;
import eu.europa.esig.dss.tsl.function.OfficialJournalSchemeInformationURI;
import eu.europa.esig.dss.tsl.function.SchemeTerritoryOtherTSLPointer;
import eu.europa.esig.dss.tsl.function.XMLOtherTSLPointer;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.tsl.sync.ExpirationAndSignatureCheckStrategy;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ssl.SslBundles;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class TSLLoader {

    /** Key of the LOTL in the per-list maps ({@link #getDownloadedAt()}, {@link #getLastCopies()}). */
    public static final String LOTL_TERRITORY = "EU";

    @Getter
    private final @NonNull String tslName;

    /**
     * What the last refresh attempt left behind, published as one immutable value so that a reader (the
     * health indicator, on a request thread) never sees the count of one refresh with the territories of
     * another: a reader that needs several values takes {@link #getState()} once. {@link State#NONE} until the
     * first refresh.
     */
    private final AtomicReference<State> state = new AtomicReference<>(State.NONE);

    /**
     * One refresh attempt's outcome, every value from the same refresh; the getters below each read one component
     * of the current state and document it.
     */
    public record State(Instant lastRefresh, int numberOfCertificates, Map<String, Integer> certificatesByTerritory,
                        Map<String, Instant> nextUpdateByTerritory, Instant lotlNextUpdate, String snapshotId,
                        Instant snapshotCreatedAt, Map<String, Instant> downloadedAt, Map<String, String> lastCopies,
                        Duration refreshPeriod) {
        /** Before the first refresh: nothing loaded, no times, no lists. */
        public static final State NONE = new State(null, 0, Map.of(), Map.of(), null, null, null, Map.of(), Map.of(),
                null);
    }

    // The registerer passes the name and the holder as the first two constructor arguments; the rest is autowired.
    private final @NonNull TrustedListsCertificateSourceHolder trustedListsCertificateSource;
    private final @NonNull TSLValidationJobFactory tslValidationJobFactory;
    private final @NonNull TSLLoaderConfigurationProperties configurationProperties;
    private final @NonNull KeyStoreCertificateSource keyStoreCertificateSource;
    private final @NonNull ProxyConfig proxyConfig;
    // Absent in a context without Spring Boot's SSL auto-configuration; needed only when a bundle is named.
    private final @NonNull ObjectProvider<SslBundles> sslBundles;
    private TSLSnapshotStore snapshotStore;
    private LastGoodCopyFileCacheDataLoader onlineLoader;
    // The snapshot id last refused (missing directory, too old); warned about once, then only at debug.
    private String refusedSnapshotId;
    // The snapshot id last rejected after loading (a mandatory territory missing); the poll leaves it alone, the
    // scheduled refresh tries it again.
    private String rejectedSnapshotId;

    @PostConstruct
    public void init() {
        List<String> trusted = configurationProperties.getTrustedTerritories();
        List<String> mandatory = configurationProperties.getMandatoryTerritories();
        if (trusted != null && !trusted.isEmpty() && mandatory != null && !trusted.containsAll(mandatory)) {
            log.warn("'{}' TSL: mandatory territories {} are not all among the trusted territories {}; "
                    + "readiness can never be reached (codes are case-sensitive)", tslName, mandatory, trusted);
        }
        if (!configurationProperties.isLoadFromCache()) {
            // Job-independent, and its data loader reads a reloaded bundle per connection: built once.
            onlineLoader = onlineLoader();
        } else if (configurationProperties.getSnapshotDirectory() != null) {
            snapshotStore = new TSLSnapshotStore(configurationProperties.getSnapshotDirectory().toPath());
        }
        logBundleReloads(configurationProperties.getSslBundle(), "trust-list download");
        logBundleReloads(configurationProperties.getLotlBundle(), "LOTL signer");
    }

    // A reloaded bundle is used by the next download (the data loader reads it per connection) and, for the
    // signers, by the next refresh (loadTSL rebuilds the LOTL source); the log line is what an operator looks for.
    private void logBundleReloads(String bundleName, String purpose) {
        if (hasText(bundleName)) {
            bundles().addBundleUpdateHandler(bundleName, bundle -> {
                int count = SslBundleCertificates.of(bundle).size();
                log.info("'{}' TSL: {} SSL bundle '{}' reloaded ({} certificates); in use from the next refresh",
                        tslName, purpose, bundleName, count);
            });
        }
    }

    // One refresh is one DSS validation job filling a fresh source, published when the job is done: a new job
    // starts with empty caches, so it parses every list again and always synchronizes into that source, which is
    // never written again (open-eid/SiVa#6 was the reused job refilling the source being read). Synchronized so
    // that the scheduled refresh and the snapshot poll do not load at the same time. Called by the refresher
    // (start-up, schedule) and by the fetcher under its store lock.
    public synchronized void loadTSL() {
        if (!configurationProperties.isLoadFromCache()) {
            TLValidationJob job = validationJob();
            onlineLoader.resetLastCopies();
            job.setOnlineDataLoader(onlineLoader);
            refresh(job, job::onlineRefresh, "over the network", null);
            return;
        }
        String snapshotId = null;
        Path directory = null;
        if (snapshotStore != null) {
            Optional<String> current = snapshotStore.currentId();
            if (current.isEmpty()) {
                Path root = snapshotStore.getRoot();
                log.warn("'{}' TSL: no trust-list snapshot published under {} yet; nothing to load", tslName, root);
                return;
            }
            snapshotId = current.get();
            directory = snapshotStore.snapshotDirectory(snapshotId);
            if (!acceptable(snapshotId, directory)) {
                return;
            }
        }
        TLValidationJob job = validationJob();
        job.setOfflineDataLoader(directory == null ? offlineLoader() : offlineLoader(directory));
        String origin = snapshotId == null ? "from cache" : "from snapshot " + snapshotId;
        refresh(job, job::offlineRefresh, origin, snapshotId);
    }

    // The LOTL source is built per job, so a reloaded signer bundle counts from the next refresh on.
    private TLValidationJob validationJob() {
        TLValidationJob job = tslValidationJobFactory.createValidationJob();
        job.setListOfTrustedListSources(europeanLOTL());
        job.setSynchronizationStrategy(new ExpirationAndSignatureCheckStrategy());
        return job;
    }

    private void refresh(TLValidationJob job, Runnable refresh, String origin, String snapshotId) {
        TrustedListsCertificateSource source = new TrustedListsCertificateSource();
        job.setTrustedListCertificateSource(source);
        log.info("Loading '{}' TSL {}", tslName, origin);
        refresh.run();
        log.info("Finished loading '{}' TSL {}", tslName, origin);
        record(job.getSummary(), source, snapshotId);
    }

    /**
     * In snapshot mode: reloads when the store's {@code current} names a snapshot other than the one loaded
     * (or none has been loaded yet). A no-op otherwise.
     */
    void refreshIfSnapshotChanged() {
        if (snapshotStore == null) {
            return;
        }
        String loaded = state.get().snapshotId();
        String published = snapshotStore.currentId().orElse(null);
        if (published != null && !published.equals(loaded) && !published.equals(rejectedSnapshotId)) {
            log.info("'{}' TSL: snapshot {} published, replacing {}", tslName, published, loaded);
            loadTSL();
        }
    }

    // A snapshot is loaded only when its directory is there (a pointer at a missing one must not take the startup
    // refresh down), when every file its manifest lists is there (a store copied file by file, through object
    // storage, can deliver `current` before the last file: the next poll finds it complete) and, with
    // snapshotMaxAge set, when its manifest says it is young enough.
    private boolean acceptable(String snapshotId, Path directory) {
        String reason = null;
        if (!Files.isDirectory(directory)) {
            reason = "its directory " + directory + " does not exist";
        } else {
            Optional<TSLSnapshotManifest> manifest = snapshotStore.manifest(snapshotId);
            List<String> missing = manifest.map(m -> snapshotStore.missingFiles(snapshotId, m)).orElse(List.of());
            Instant createdAt = manifest.map(TSLSnapshotManifest::createdAt).orElse(null);
            if (!missing.isEmpty()) {
                reason = "it is incomplete, " + missing.size() + " of the files its manifest lists are not there yet: "
                        + missing;
            } else if (configurationProperties.getSnapshotMaxAge() != null) {
                Instant oldest = Instant.now().minus(configurationProperties.getSnapshotMaxAge());
                if (createdAt == null) {
                    reason = "its manifest is missing or unreadable and snapshotMaxAge is set";
                } else if (createdAt.isBefore(oldest)) {
                    reason = "it was created " + createdAt + ", older than snapshotMaxAge "
                            + configurationProperties.getSnapshotMaxAge();
                }
            }
        }
        if (reason == null) {
            refusedSnapshotId = null;
            return true;
        }
        if (snapshotId.equals(refusedSnapshotId)) {
            log.debug("'{}' TSL: still refusing snapshot {}: {}", tslName, snapshotId, reason);
        } else {
            log.warn("'{}' TSL: refusing snapshot {}: {}; keeping the loaded state", tslName, snapshotId, reason);
            refusedSnapshotId = snapshotId;
        }
        return false;
    }

    // The source goes live first, then the state that describes it, so readiness never precedes the anchors.
    // Where the lists came from is known online (this process downloaded them: the cache files' modification
    // times and the loader's record of copies served) and in snapshot mode (the fetcher wrote it into the
    // manifest); a plain cache directory says nothing.
    private void record(TLValidationJobSummary summary, TrustedListsCertificateSource source, String snapshotId) {
        Instant snapshotCreatedAt = null;
        Map<String, Instant> downloadedAt = Map.of();
        Map<String, String> lastCopies = Map.of();
        Duration refreshPeriod = null;
        if (snapshotId != null) {
            Optional<TSLSnapshotManifest> manifest = snapshotStore.manifest(snapshotId);
            snapshotCreatedAt = manifest.map(TSLSnapshotManifest::createdAt).orElse(null);
            downloadedAt = manifest.map(TSLSnapshotManifest::downloadedAt)
                    .map(byLoader -> byLoader.get(tslName))
                    .map(Collections::unmodifiableMap)
                    .orElse(Map.of());
            refreshPeriod = manifest.map(TSLSnapshotManifest::period).orElse(null);
        } else if (!configurationProperties.isLoadFromCache()) {
            downloadedAt = downloadedAt(summary, onlineCacheDirectory());
            lastCopies = byList(summary, onlineLoader.getLastCopies());
            refreshPeriod = configurationProperties.getRefreshPeriod();
        }
        Map<String, Integer> certificatesByTerritory = snapshotCertificatesByTerritory(source);
        if (snapshotId != null && rejected(snapshotId, source.getNumberOfCertificates(), certificatesByTerritory)) {
            return;
        }
        State next = new State(Instant.now(), source.getNumberOfCertificates(), certificatesByTerritory,
                nextUpdateByTerritory(summary), lotlNextUpdate(summary), snapshotId, snapshotCreatedAt, downloadedAt,
                lastCopies, refreshPeriod);
        trustedListsCertificateSource.publish(source);
        state.set(next);
    }

    // A snapshot the fetcher accepted can still come out short here: a list that expired between fetch and load,
    // a file damaged on the way, another signer bundle. Replacing a source that satisfies the readiness condition
    // (anchors for every mandatory territory) with one that does not would take every replica down at the same
    // poll, so the loaded source is kept, with its state and its ages (the staleness and expiry alerts keep
    // counting from them), and the snapshot is rejected until the fetcher publishes the next one. A pod that is
    // not ready has nothing to keep: whatever came out goes live and the readiness gate reports it.
    private boolean rejected(String snapshotId, int numberOfCertificates, Map<String, Integer> certificatesByTerritory) {
        State loaded = state.get();
        if (!ready(loaded.numberOfCertificates(), loaded.certificatesByTerritory())) {
            return false;
        }
        if (ready(numberOfCertificates, certificatesByTerritory)) {
            rejectedSnapshotId = null;
            return false;
        }
        String reason = numberOfCertificates == 0 ? "it holds no trust anchors"
                : "it holds " + certificatesByTerritory + ", mandatory are " + configurationProperties.getMandatoryTerritories();
        if (snapshotId.equals(rejectedSnapshotId)) {
            log.debug("'{}' TSL: still rejecting snapshot {}: {}", tslName, snapshotId, reason);
        } else {
            String kept = loaded.snapshotId();
            log.warn("'{}' TSL: rejecting snapshot {}: {}; keeping the loaded source from snapshot {}", tslName,
                    snapshotId, reason, kept);
            rejectedSnapshotId = snapshotId;
        }
        return true;
    }

    // The readiness condition the health indicator applies: anchors, and some for every mandatory territory.
    private boolean ready(int numberOfCertificates, Map<String, Integer> certificatesByTerritory) {
        List<String> mandatory = configurationProperties.getMandatoryTerritories();
        return numberOfCertificates > 0
                && (mandatory == null || certificatesByTerritory.keySet().containsAll(mandatory));
    }

    private Path onlineCacheDirectory() {
        File directory = configurationProperties.getCacheDirectory();
        return directory == null ? Path.of(System.getProperty("java.io.tmpdir")) : directory.toPath();
    }

    // Modification time of each list's cache file: DSS rewrites the file on every successful download and the
    // last-copy fallback leaves it alone, so this is when the list was last really downloaded.
    private static Map<String, Instant> downloadedAt(TLValidationJobSummary summary, Path cacheDirectory) {
        Map<String, Instant> result = new TreeMap<>();
        urlsByList(summary).forEach((list, url) -> {
            Path file = cacheDirectory.resolve(DSSUtils.getNormalizedString(url));
            try {
                if (Files.isRegularFile(file)) {
                    result.put(list, Files.getLastModifiedTime(file).toInstant());
                }
            } catch (IOException e) {
                log.debug("Modification time of {} unreadable", file, e);
            }
        });
        return Collections.unmodifiableMap(result);
    }

    // Re-keys the loader's URL-keyed copies by list (territory, EU for the LOTL); a URL the summary does not
    // know keeps its URL as the key rather than being dropped.
    private static Map<String, String> byList(TLValidationJobSummary summary, Map<String, String> byUrl) {
        if (byUrl.isEmpty()) {
            return Map.of();
        }
        Map<String, String> listByUrl = new HashMap<>();
        urlsByList(summary).forEach((list, url) -> listByUrl.put(url, list));
        Map<String, String> result = new TreeMap<>();
        byUrl.forEach((url, reason) -> result.put(listByUrl.getOrDefault(url, url), reason));
        return Collections.unmodifiableMap(result);
    }

    // List key (territory, EU for the non-pivot LOTL) to URL, for every list the summary parsed a territory for.
    private static Map<String, String> urlsByList(TLValidationJobSummary summary) {
        Map<String, String> result = new TreeMap<>();
        if (summary == null) {
            return result;
        }
        List<TLInfo> trustedLists = new ArrayList<>(summary.getOtherTLInfos());
        for (LOTLInfo lotl : summary.getLOTLInfos()) {
            trustedLists.addAll(lotl.getTLInfos());
            if (!lotl.isPivot() && lotl.getUrl() != null) {
                result.putIfAbsent(LOTL_TERRITORY, lotl.getUrl());
            }
        }
        for (TLInfo trustedList : trustedLists) {
            String territory = territoryOf(trustedList);
            if (territory != null && trustedList.getUrl() != null) {
                result.putIfAbsent(territory, trustedList.getUrl());
            }
        }
        return result;
    }

    /** The current state as one value, for a reader that reports several of its components together. */
    public State getState() {
        return state.get();
    }

    /**
     * When the last refresh attempt finished, {@code null} until the first one. DSS does not throw on
     * download failures, so this says "attempted", not "succeeded"; {@link #getNumberOfCertificates()}
     * tells whether anything came out of it. Together they are what readiness is judged on.
     */
    public Instant getLastRefresh() {
        return state.get().lastRefresh();
    }

    /**
     * Id of the published snapshot the last refresh loaded; {@code null} outside snapshot mode or before the
     * first load.
     */
    public String getSnapshotId() {
        return state.get().snapshotId();
    }

    /**
     * Number of trust anchors in the source the last refresh attempt published, zero until then or when the
     * refresh left nothing (nothing can be validated). Recorded with the source, so the health view and the
     * metrics describe what validations use. With {@code loadFromCache} a pod that started with an empty
     * cache stays at zero until the cache is filled and the next scheduled refresh runs.
     */
    public int getNumberOfCertificates() {
        return state.get().numberOfCertificates();
    }

    /**
     * Trust anchors per territory (scheme territory code of the trusted list they came from) when the last
     * refresh attempt finished: what the certificate source actually holds after DSS synchronized it, so it
     * follows DSS's own acceptance of lists and lists of lists (expiry, signature, LOTL-level rejection); a list
     * whose re-download failed counts as long as its last good copy on disk, or the snapshot, is accepted. An anchor listed by
     * several trusted lists counts once per territory. Sorted by territory; empty until the first refresh.
     */
    public Map<String, Integer> getCertificatesByTerritory() {
        return state.get().certificatesByTerritory();
    }

    /**
     * {@code NextUpdate} of each territory's trusted list as parsed by the last refresh attempt, the earliest when a
     * territory has several; after it the list counts as expired. Includes lists DSS did not accept, so an expired
     * list stays visible. Sorted by territory; empty until the first refresh.
     */
    public Map<String, Instant> getNextUpdateByTerritory() {
        return state.get().nextUpdateByTerritory();
    }

    /**
     * {@code NextUpdate} of the list of trusted lists as parsed by the last refresh attempt (the earliest of several);
     * every territory depends on it. {@code null} until the first refresh or when it was never parsed.
     */
    public Instant getLotlNextUpdate() {
        return state.get().lotlNextUpdate();
    }

    /**
     * When the loaded snapshot was published ({@code manifest.json} {@code createdAt}); {@code null} outside snapshot
     * mode, before the first load or when the manifest is missing.
     */
    public Instant getSnapshotCreatedAt() {
        return state.get().snapshotCreatedAt();
    }

    /**
     * When each list was last downloaded from its publisher, by territory ({@link #LOTL_TERRITORY} for the list of
     * trusted lists): the cache file's modification time in online mode, the manifest's record in snapshot mode. A
     * list served from its last copy keeps the time of the download that produced the copy, so the age of this value
     * is how long the publisher has been out of reach. Sorted; empty until the first refresh, when loading a plain
     * cache directory, and for manifests written before the field existed.
     */
    public Map<String, Instant> getDownloadedAt() {
        return state.get().downloadedAt();
    }

    /**
     * The lists the last online refresh served from their last copy because the download failed, by territory
     * ({@link #LOTL_TERRITORY} for the list of trusted lists; the URL when the summary has no territory for it), with
     * the kind of failure (a {@link LastCopyReason} name; the failure itself is in the log). Empty when every download
     * succeeded and outside online mode.
     */
    public Map<String, String> getLastCopies() {
        return state.get().lastCopies();
    }

    /**
     * How often the lists are refreshed: {@code siva.tsl.loader.refreshPeriod} in online mode, the fetcher's period
     * from the manifest in snapshot mode; {@code null} until the first refresh or where neither is known.
     */
    public Duration getRefreshPeriod() {
        return state.get().refreshPeriod();
    }

    private static Map<String, Instant> nextUpdateByTerritory(TLValidationJobSummary summary) {
        if (summary == null) {
            return Map.of();
        }
        List<TLInfo> trustedLists = new ArrayList<>(summary.getOtherTLInfos());
        summary.getLOTLInfos().forEach(lotl -> trustedLists.addAll(lotl.getTLInfos()));
        Map<String, Instant> result = new TreeMap<>();
        for (TLInfo trustedList : trustedLists) {
            String territory = territoryOf(trustedList);
            Instant nextUpdate = nextUpdateOf(trustedList);
            if (territory != null && nextUpdate != null) {
                result.merge(territory, nextUpdate, (a, b) -> a.isBefore(b) ? a : b);
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private static Instant lotlNextUpdate(TLValidationJobSummary summary) {
        if (summary == null) {
            return null;
        }
        return summary.getLOTLInfos().stream()
                .filter(lotl -> !lotl.isPivot())
                .map(TSLLoader::nextUpdateOf)
                .filter(Objects::nonNull)
                .min(Instant::compareTo)
                .orElse(null);
    }

    private static Instant nextUpdateOf(TLInfo trustedList) {
        ParsingInfoRecord parsing = trustedList.getParsingCacheInfo();
        Date nextUpdate = parsing == null ? null : parsing.getNextUpdateDate();
        return nextUpdate == null ? null : nextUpdate.toInstant();
    }

    private static Map<String, Integer> snapshotCertificatesByTerritory(TrustedListsCertificateSource source) {
        Map<String, Set<String>> anchorsByTerritory = new TreeMap<>();
        for (CertificateToken token : source.getCertificates()) {
            for (TrustProperties trustProperties : source.getTrustServices(token)) {
                String territory = territoryOf(trustProperties.getTLInfo());
                if (territory != null) {
                    anchorsByTerritory.computeIfAbsent(territory, key -> new HashSet<>()).add(token.getDSSIdAsString());
                }
            }
        }
        Map<String, Integer> result = new TreeMap<>();
        anchorsByTerritory.forEach((territory, anchors) -> result.put(territory, anchors.size()));
        return Collections.unmodifiableMap(result);
    }

    private static String territoryOf(TLInfo trustedList) {
        if (trustedList == null) {
            return null;
        }
        ParsingInfoRecord parsing = trustedList.getParsingCacheInfo();
        return parsing == null ? null : parsing.getTerritory();
    }

    public LOTLSource europeanLOTL() {
        LOTLSource lotlSource = new LOTLSource();
        lotlSource.setUrl(configurationProperties.getUrl());

        lotlSource.setCertificateSource(lotlSigners());
        lotlSource.setSigningCertificatesAnnouncementPredicate(new OfficialJournalSchemeInformationURI(configurationProperties.getOjUrl()));
        lotlSource.setPivotSupport(configurationProperties.isLotlPivotSupportEnabled());
        lotlSource.setLotlPredicate(new EULOTLOtherTSLPointer()
                .and(new XMLOtherTSLPointer())
        );

        if (!configurationProperties.getTrustedTerritories().isEmpty()) {
            Set<String> trustedTerritories = new HashSet<>();
            CollectionUtils.addAll(trustedTerritories, configurationProperties.getTrustedTerritories());
            lotlSource.setTlPredicate(new SchemeTerritoryOtherTSLPointer(trustedTerritories).and(new EUTLOtherTSLPointer()
                    .and(new XMLOtherTSLPointer()))
            );
        }
        return lotlSource;
    }

    public LastGoodCopyFileCacheDataLoader onlineLoader() {
        LastGoodCopyFileCacheDataLoader onlineFileLoader = new LastGoodCopyFileCacheDataLoader();
        cacheDirectory(onlineFileLoader, configurationProperties.getCacheDirectory());
        if (configurationProperties.getOnlineCacheExpirationTime() != null) {
            Duration cacheExpirationTime = configurationProperties.getOnlineCacheExpirationTime();
            onlineFileLoader.setCacheExpirationTime(cacheExpirationTime.toMillis());
        }
        onlineFileLoader.setDataLoader(createCommonsDataLoader());
        return onlineFileLoader;
    }

    // The LOTL signer anchors: the configured SSL bundle's trust store, else the PKCS12 keystore bean.
    private CertificateSource lotlSigners() {
        String bundleName = configurationProperties.getLotlBundle();
        if (!hasText(bundleName)) {
            return keyStoreCertificateSource;
        }
        CommonCertificateSource signers = new CommonCertificateSource();
        List<X509Certificate> certificates = SslBundleCertificates.of(bundles().getBundle(bundleName));
        certificates.forEach(certificate -> signers.addCertificate(new CertificateToken(certificate)));
        int count = certificates.size();
        log.info("'{}' TSL: {} LOTL signer certificates from SSL bundle '{}'", tslName, count, bundleName);
        return signers;
    }

    private SslBundles bundles() {
        SslBundles bundles = sslBundles.getIfAvailable();
        if (bundles == null) {
            throw new IllegalStateException("siva.tsl.loader.sslBundle/lotlBundle name an SSL bundle, "
                    + "but Spring Boot's SSL bundles are not available in this context");
        }
        return bundles;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private CommonsDataLoader createCommonsDataLoader() {
        String bundleName = configurationProperties.getSslBundle();
        CommonsDataLoader commonsDataLoader;
        if (hasText(bundleName)) {
            commonsDataLoader = new SslBundleDataLoader(bundles(), bundleName);
            int count = SslBundleCertificates.of(bundles().getBundle(bundleName)).size();
            log.info("'{}' TSL: trust-list hosts trusted through SSL bundle '{}' ({} certificates)", tslName,
                    bundleName, count);
            commonsDataLoader.setProxyConfig(proxyConfig);
            return commonsDataLoader;
        }
        commonsDataLoader = new CommonsDataLoader();
        commonsDataLoader.setProxyConfig(proxyConfig);

        Resource truststoreResource = configurationProperties.getSslTruststorePath();
        if (truststoreResource == null) {
            truststoreResource = new ClassPathResource("tsl-ssl-truststore.p12", getClass().getClassLoader());
        }

        DSSDocument truststore;
        try (InputStream inputStream = truststoreResource.getInputStream()) {
            truststore = new InMemoryDocument(inputStream, truststoreResource.getFilename());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read TSL SSL truststore resource: " + truststoreResource, e);
        }
        
        commonsDataLoader.setSslTruststore(truststore);
        commonsDataLoader.setSslTruststoreType(configurationProperties.getSslTruststoreType());
        commonsDataLoader.setSslTruststorePassword(configurationProperties.getSslTruststorePassword().toCharArray());
        
        return commonsDataLoader;
    }

    public DSSFileLoader offlineLoader() {
        File cacheDirectory = configurationProperties.getCacheDirectory();
        return offlineLoader(cacheDirectory == null ? null : cacheDirectory.toPath());
    }

    private static DSSFileLoader offlineLoader(Path directory) {
        FileCacheDataLoader offlineFileLoader = new FileCacheDataLoader();
        cacheDirectory(offlineFileLoader, directory == null ? null : directory.toFile());
        offlineFileLoader.setCacheExpirationTime(Long.MAX_VALUE);
        offlineFileLoader.setDataLoader(new IgnoreDataLoader());
        return offlineFileLoader;
    }

    // Null keeps DSS's default, the JVM's temporary directory.
    private static void cacheDirectory(FileCacheDataLoader loader, File directory) {
        if (directory != null) {
            loader.setFileCacheDirectory(directory);
        }
    }

}
