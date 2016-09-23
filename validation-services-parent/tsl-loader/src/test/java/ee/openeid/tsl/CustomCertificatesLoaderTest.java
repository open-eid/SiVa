package ee.openeid.tsl;


import eu.europa.esig.dss.tsl.ServiceInfo;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.x509.CertificateToken;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomCertificatesLoaderTest {

    private TrustedListsCertificateSource trustedListSource;

    @Before
    public void setUp() {
        CustomCertificatesLoader customCertificatesLoader = new CustomCertificatesLoader();
        trustedListSource = new TrustedListsCertificateSource();
        customCertificatesLoader.setTrustedListsCertificateSource(trustedListSource);
        customCertificatesLoader.init();
    }

    @Test
    public void allTestCertificatesShouldBeAddedToTSL() {
        List<CertificateToken> certTokens = getCertificateTokens();
        assertEquals(7, certTokens.size());
    }

    @Test
    public void softCertCAShouldNotBeLoadedWithQCServiceInfoQualifiers() {
        getServiceInfoStream()
                .filter(this::isManagementServiceInfo)
                .forEach(this::assertNoQualifiers);
    }

    @Test
    public void testSKCAsShouldBeLoadedWithQCServiceInfoQualifiers() {
        getServiceInfoStream()
                .filter(this::isNonManagementCA)
                .forEach(this::assertQcQualifiers);
    }

    private Stream<ServiceInfo> getServiceInfoStream() {
        return getCertificateTokens()
                .stream()
                .map(this::getServiceInfo);
    }

    private boolean isManagementServiceInfo(ServiceInfo serviceInfo) {
        return StringUtils.contains(serviceInfo.getServiceName(), "Management");
    }

    private boolean isNonManagementCA(ServiceInfo serviceInfo) {
        return StringUtils.equals("http://uri.etsi.org/TrstSvc/Svctype/CA/QC", serviceInfo.getType()) &&
                !isManagementServiceInfo(serviceInfo);
    }

    private List<CertificateToken> getCertificateTokens() {
        return trustedListSource
                .getCertificatePool()
                .getCertificateTokens();
    }

    private void assertNoQualifiers(ServiceInfo serviceInfo) {
        assertTrue(serviceInfo.getQualifiersAndConditions().keySet().isEmpty());
    }

    private void assertQcQualifiers(ServiceInfo serviceInfo) {
        Set<String> qualifiers = serviceInfo.getQualifiersAndConditions().keySet();
        assertTrue(qualifiers.contains("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCForESig"));
        assertTrue(qualifiers.contains("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCWithQSCD"));
        assertTrue(qualifiers.contains("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCStatement"));
    }

    private ServiceInfo getServiceInfo(CertificateToken certificateToken) {
        return (ServiceInfo) certificateToken.getAssociatedTSPS().toArray()[0];
    }


}
