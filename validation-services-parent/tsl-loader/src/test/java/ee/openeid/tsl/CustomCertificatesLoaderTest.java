package ee.openeid.tsl;


import eu.europa.esig.dss.tsl.ServiceInfo;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.x509.CertificateToken;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        List<CertificateToken> certTokens = trustedListSource.getCertificatePool().getCertificateTokens();
        assertEquals(7, certTokens.size());
    }

    @Test
    public void testSKCAsShouldBeLoadedWithQCServiceInfoQualifiers() {
        List<CertificateToken> certTokens = trustedListSource.getCertificatePool().getCertificateTokens();
        Set<CertificateToken> nonManagementCertTokens = certTokens
                .stream()
                .filter(ct -> !(StringUtils.contains(ct.getIssuerDN().getName(), "CN=ManagementCA")))
                .collect(Collectors.toSet());

        nonManagementCertTokens.stream()
                .map(ct -> (ServiceInfo) ct.getAssociatedTSPS().toArray()[0]).filter(si -> StringUtils.equals("http://uri.etsi.org/TrstSvc/Svctype/CA/QC", si.getType()))
                .forEach(si -> {
                    Set<String> qualifiers = si.getQualifiersAndConditions().keySet();
                    assertTrue(qualifiers.contains("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCForESig"));
                    assertTrue(qualifiers.contains("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCWithQSCD"));
                    assertTrue(qualifiers.contains("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCStatement"));
                });
    }

    @Test
    public void softCertCAShouldNotBeLoadedWithQCServiceInfoQualifiers() {
        List<CertificateToken> certTokens = trustedListSource.getCertificatePool().getCertificateTokens();
        List<CertificateToken> managementCertTokens = certTokens
                .stream()
                .filter(ct -> (StringUtils.contains(ct.getIssuerDN().getName(), "CN=ManagementCA")))
                .collect(Collectors.toList());

        managementCertTokens.stream()
                .map(ct -> (ServiceInfo) ct.getAssociatedTSPS().toArray()[0])
                .forEach(si -> {
                    Set<String> qualifiers = si.getQualifiersAndConditions().keySet();
                    assertTrue(qualifiers.isEmpty());
                });
    }
}
