package ee.openeid.tsl;

import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class EmptyCertificatesLoader implements CertificatesLoader {
    @Override
    public void loadExtraCertificates(TrustedListsCertificateSource tlCertSource) {
        //Do nothing
    }
}
