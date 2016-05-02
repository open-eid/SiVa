package ee.openeid.siva.tsl.configuration;

import eu.europa.esig.dss.x509.CertificateToken;
import eu.europa.esig.dss.x509.crl.CRLSource;
import eu.europa.esig.dss.x509.crl.CRLToken;

public class AlwaysFailingCRLSource implements CRLSource {
    @Override
    public CRLToken findCrl(CertificateToken certificateToken) {
        return null;
    }
}
