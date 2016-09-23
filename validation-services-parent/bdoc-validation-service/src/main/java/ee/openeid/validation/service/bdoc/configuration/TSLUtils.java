package ee.openeid.validation.service.bdoc.configuration;

import eu.europa.esig.dss.tsl.ServiceInfo;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.digidoc4j.TSLCertificateSource;
import org.digidoc4j.impl.bdoc.tsl.TSLCertificateSourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TSLUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TSLUtils.class);

    public static TSLCertificateSource createTSLFromTrustedCertSource(TrustedListsCertificateSource trustedListSource) {
        TSLCertificateSource tslCertificateSource = new TSLCertificateSourceImpl();
        trustedListSource.getCertificates().forEach(certToken -> {
            ServiceInfo serviceInfo = null;
            if (!certToken.getAssociatedTSPS().isEmpty()) {
                serviceInfo = (ServiceInfo) certToken.getAssociatedTSPS().toArray()[0];
            }
            tslCertificateSource.addCertificate(certToken, serviceInfo);
        });
        LOGGER.debug("{} certificates added to TSL certificate source", tslCertificateSource.getCertificates().size());
        return tslCertificateSource;
    }
}
