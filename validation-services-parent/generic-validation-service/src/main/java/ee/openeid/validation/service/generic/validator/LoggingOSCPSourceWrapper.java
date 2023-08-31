package ee.openeid.validation.service.generic.validator;

import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.model.x509.revocation.ocsp.OCSP;
import eu.europa.esig.dss.spi.x509.revocation.OnlineRevocationSource;
import eu.europa.esig.dss.spi.x509.revocation.RevocationToken;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoggingOSCPSourceWrapper implements OCSPSource {
    @NonNull
    private final OnlineRevocationSource<OCSP> ocspOnlineRevocationSource;

    @Override
    public OCSPToken getRevocationToken(CertificateToken certificateToken, CertificateToken issuerCertificateToken) {

        OnlineRevocationSource.RevocationTokenAndUrl<OCSP> ocspTokenAndUrl;

        try {
            ocspTokenAndUrl = ocspOnlineRevocationSource.getRevocationTokenAndUrl(certificateToken, issuerCertificateToken);
        } catch (Exception e) {
            log.warn(
                    "Failed to perform OCSP request for certificate token '{}': {}",
                    certificateToken.getDSSIdAsString(),
                    e.getMessage()
            );
            return null;
        }

        if (ocspTokenAndUrl != null) {
            return processOcspToken(ocspTokenAndUrl, certificateToken);
        } else {
            return handleMissingOcspToken(certificateToken);
        }
    }

    private OCSPToken processOcspToken(OnlineRevocationSource.RevocationTokenAndUrl<OCSP> ocspTokenAndUrl, CertificateToken certificateToken) {
        RevocationToken<OCSP> revocationToken = ocspTokenAndUrl.getRevocationToken();

        log.info(
                "Performed OCSP request for certificate token '{}' from '{}', status: '{}'",
                certificateToken.getDSSIdAsString(),
                ocspTokenAndUrl.getUrlString(),
                revocationToken.getStatus()
        );

        return (OCSPToken) revocationToken;
    }

    private OCSPToken handleMissingOcspToken(CertificateToken certificateToken) {
        log.warn("No OCSP token found for certificate token '{}'", certificateToken.getDSSIdAsString());
        return null;
    }
}
