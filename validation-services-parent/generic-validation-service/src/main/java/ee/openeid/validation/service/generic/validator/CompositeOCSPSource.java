package ee.openeid.validation.service.generic.validator;

import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.BiPredicate;

@RequiredArgsConstructor
public class CompositeOCSPSource implements OCSPSource {
    @NonNull
    private final OCSPSource ocspSource;
    @NonNull
    private final BiPredicate<CertificateToken, CertificateToken> ocspRequestRequirementPredicate;

    @Override
    public OCSPToken getRevocationToken(CertificateToken certificateToken, CertificateToken issuerCertificateToken) {
        if (ocspRequestRequirementPredicate.test(certificateToken, issuerCertificateToken)) {
            return ocspSource.getRevocationToken(certificateToken, issuerCertificateToken);
        } else {
            return null;
        }
    }
}
