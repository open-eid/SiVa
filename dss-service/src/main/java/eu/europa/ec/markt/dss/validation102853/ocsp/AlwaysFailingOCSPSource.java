package eu.europa.ec.markt.dss.validation102853.ocsp;

import eu.europa.ec.markt.dss.validation102853.CertificatePool;
import eu.europa.ec.markt.dss.validation102853.CertificateToken;
import eu.europa.ec.markt.dss.validation102853.OCSPToken;

public class AlwaysFailingOCSPSource implements OCSPSource {

    @Override
    public OCSPToken getOCSPToken(CertificateToken certificateToken, CertificatePool certificatePool) {
        return null;
    }
}
