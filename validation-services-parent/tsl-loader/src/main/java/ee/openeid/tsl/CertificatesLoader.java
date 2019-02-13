package ee.openeid.tsl;

import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;

public interface CertificatesLoader {

    void loadExtraCertificates(TrustedListsCertificateSource tlCertSource);
}
