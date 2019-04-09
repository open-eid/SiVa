package ee.openeid.validation.service.timemark.signature.policy;

import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.digidoc4j.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class EmptyConfigurationLoader implements ConfigurationLoader {

    @Override
    public void reloadTrustedCertificatesIfNecessary(Configuration configuration, TrustedListsCertificateSource trustedListSource) {
        //Do nothing
    }

}
