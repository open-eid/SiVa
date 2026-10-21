/*
 * Copyright 2023 - 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timemark.tsl;

import ee.openeid.tsl.TrustedListsCertificateSourceHolder;
import eu.europa.esig.dss.enumerations.CertificateSourceType;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.model.identifier.EntityIdentifier;
import eu.europa.esig.dss.model.tsl.CertificateTrustTime;
import eu.europa.esig.dss.model.tsl.TLValidationJobSummary;
import eu.europa.esig.dss.model.tsl.TrustProperties;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.model.x509.X500PrincipalHelper;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CertificateRef;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.CertificateSourceEntity;
import eu.europa.esig.dss.spi.x509.SignerIdentifier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.digidoc4j.TSLCertificateSource;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * The trust source as digidoc4j sees it: a read-only view of what the loader last published, pinned for the
 * duration of one validation.
 */
@RequiredArgsConstructor
public class TimemarkTrustedListsCertificateSource extends TrustedListsCertificateSource implements TSLCertificateSource {

    // digidoc4j keeps one TSLCertificateSource in its Configuration for the life of the configuration and reads it
    // per operation, so every read here goes to a published source and nothing is held in this object; the loader
    // fills fresh sources, nothing fills this one. Configuration.copy() serializes the configuration's source;
    // BDOCConfigurationService sets this view on the copy again, so the copy's own, holder-less instance is never read.
    private final transient @NonNull TrustedListsCertificateSourceHolder holder;

    // A validation makes many reads; all of them must see the same source, or a swap in the middle lets DSS
    // look up a trusted list of one source in the summary of another. Validation stays on one thread.
    private final transient ThreadLocal<TrustedListsCertificateSource> pinned = new ThreadLocal<>();

    /**
     * Runs {@code validation} with the source published at its start, whatever the loader publishes meanwhile.
     */
    public <T> T withPinnedSource(Supplier<T> validation) {
        pinned.set(holder.current());
        try {
            return validation.get();
        } finally {
            pinned.remove();
        }
    }

    private TrustedListsCertificateSource source() {
        TrustedListsCertificateSource source = pinned.get();
        return source != null ? source : holder.current();
    }

    @Override
    public TLValidationJobSummary getSummary() {
        return source().getSummary();
    }

    @Override
    public CertificateSourceType getCertificateSourceType() {
        return source().getCertificateSourceType();
    }

    @Override
    public List<TrustProperties> getTrustServices(CertificateToken token) {
        return source().getTrustServices(token);
    }

    @Override
    public CertificateTrustTime getTrustTime(CertificateToken token) {
        return source().getTrustTime(token);
    }

    @Override
    public boolean isTrustedAtTime(CertificateToken certificateToken, Date controlTime) {
        return source().isTrustedAtTime(certificateToken, controlTime);
    }

    @Override
    public List<String> getAlternativeOCSPUrls(CertificateToken trustAnchor) {
        return source().getAlternativeOCSPUrls(trustAnchor);
    }

    @Override
    public List<String> getAlternativeCRLUrls(CertificateToken trustAnchor) {
        return source().getAlternativeCRLUrls(trustAnchor);
    }

    @Override
    public boolean isTrusted(CertificateToken certificateToken) {
        return source().isTrusted(certificateToken);
    }

    @Override
    public int getNumberOfTrustedEntityKeys() {
        return source().getNumberOfTrustedEntityKeys();
    }

    @Override
    public boolean isKnown(CertificateToken token) {
        return source().isKnown(token);
    }

    @Override
    public List<CertificateToken> getCertificates() {
        return source().getCertificates();
    }

    @Override
    public List<CertificateSourceEntity> getEntities() {
        return source().getEntities();
    }

    @Override
    public Set<CertificateToken> getByPublicKey(PublicKey publicKey) {
        return source().getByPublicKey(publicKey);
    }

    @Override
    public Set<CertificateToken> getByEntityKey(EntityIdentifier entityKey) {
        return source().getByEntityKey(entityKey);
    }

    @Override
    public Set<CertificateToken> getBySki(byte[] ski) {
        return source().getBySki(ski);
    }

    @Override
    public Set<CertificateToken> getBySubject(X500PrincipalHelper subject) {
        return source().getBySubject(subject);
    }

    @Override
    public Set<CertificateToken> getBySignerIdentifier(SignerIdentifier signerIdentifier) {
        return source().getBySignerIdentifier(signerIdentifier);
    }

    @Override
    public Set<CertificateToken> getByCertificateDigest(Digest digest) {
        return source().getByCertificateDigest(digest);
    }

    @Override
    public Set<CertificateToken> findTokensFromCertRef(CertificateRef certificateRef) {
        return source().findTokensFromCertRef(certificateRef);
    }

    @Override
    public int getNumberOfCertificates() {
        return source().getNumberOfCertificates();
    }

    @Override
    public int getNumberOfEntities() {
        return source().getNumberOfEntities();
    }

    @Override
    public boolean isAllSelfSigned() {
        return source().isAllSelfSigned();
    }

    @Override
    public boolean isCertificateSourceEqual(CertificateSource certificateSource) {
        return source().isCertificateSourceEqual(certificateSource);
    }

    @Override
    public boolean isCertificateSourceEquivalent(CertificateSource certificateSource) {
        return source().isCertificateSourceEquivalent(certificateSource);
    }

    @Override
    public CertificateToken addCertificate(CertificateToken certificate) {
        throw readOnly();
    }

    @Override
    public void addCertificate(CertificateToken certificateToken, List<TrustProperties> trustPropertiesList) {
        throw readOnly();
    }

    @Override
    public void addTSLCertificate(X509Certificate certificate) {
        throw readOnly();
    }

    @Override
    public void setTrustPropertiesByCertificates(Map<CertificateToken, List<TrustProperties>> trustPropertiesByCerts) {
        throw readOnly();
    }

    @Override
    public void setTrustTimeByCertificates(Map<CertificateToken, List<CertificateTrustTime>> trustTimeByCertificate) {
        throw readOnly();
    }

    @Override
    public void setSummary(TLValidationJobSummary summary) {
        throw readOnly();
    }

    @Override
    public void importAsTrusted(CertificateSource certificateSource) {
        throw readOnly();
    }

    @Override
    public void invalidateCache() {
        throw readOnly();
    }

    @Override
    public void refresh() {
        throw readOnly();
    }

    private static UnsupportedOperationException readOnly() {
        return new UnsupportedOperationException("The trust source is filled by its TSL loader, not through digidoc4j");
    }

}
