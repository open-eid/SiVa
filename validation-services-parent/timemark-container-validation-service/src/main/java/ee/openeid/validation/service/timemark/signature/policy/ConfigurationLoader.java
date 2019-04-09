package ee.openeid.validation.service.timemark.signature.policy;

import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.digidoc4j.Configuration;

public interface ConfigurationLoader {

    void reloadTrustedCertificatesIfNecessary(Configuration configuration, TrustedListsCertificateSource trustedListSource);
}
