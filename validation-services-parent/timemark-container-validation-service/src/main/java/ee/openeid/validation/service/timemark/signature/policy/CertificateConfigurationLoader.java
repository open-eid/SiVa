package ee.openeid.validation.service.timemark.signature.policy;

import ee.openeid.validation.service.timemark.configuration.TSLUtils;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.digidoc4j.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class CertificateConfigurationLoader implements ConfigurationLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateConfigurationLoader.class);

    private int trustStoreSize;

    @Override
    public void reloadTrustedCertificatesIfNecessary(Configuration configuration, TrustedListsCertificateSource trustedListSource) {
        if (configuration.getTSL().getCertificates().size() != trustedListSource.getCertificates().size() && trustStoreSize != trustedListSource.getCertificates().size()) {
            trustStoreSize = trustedListSource.getCertificates().size();
            LOGGER.debug("some or all trusted certificates are not added to D4J configuration, repopulating from cert pool");
            configuration.setTSL(TSLUtils.addCertificatesFromTrustedListSource(configuration.getTSL(), trustedListSource));
        }
    }


}
