package ee.openeid.validation.service.bdoc.configuration;

import ee.openeid.tsl.CustomCertificatesLoader;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.digidoc4j.TSLCertificateSource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TSLUtilsTest {

    private TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();

    @Before
    public void setUp() {
        CustomCertificatesLoader customCertificatesLoader = new CustomCertificatesLoader();
        customCertificatesLoader.setTrustedListsCertificateSource(trustedListsCertificateSource);
        customCertificatesLoader.init();
    }

    @Test
    public void allCertificatesShouldBeCopiedFromTrustedListSourceToTSLSource() throws Exception {
        TSLCertificateSource tslCertificateSource = TSLUtils.createTSLFromTrustedCertSource(trustedListsCertificateSource);
        assertEquals(tslCertificateSource.getCertificates(), trustedListsCertificateSource.getCertificates());
    }

}