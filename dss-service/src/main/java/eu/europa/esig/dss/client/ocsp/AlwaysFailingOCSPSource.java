package eu.europa.esig.dss.client.ocsp;

import eu.europa.esig.dss.x509.CertificateToken;
import eu.europa.esig.dss.x509.OCSPToken;
import eu.europa.esig.dss.x509.ocsp.OCSPSource;

public class AlwaysFailingOCSPSource implements OCSPSource {

    @Override
    public OCSPToken getOCSPToken(CertificateToken certificateToken, CertificateToken issuerCertificateToken) {
        return null;
    }
}
