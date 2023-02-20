package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.Certificate;
import ee.openeid.siva.validation.document.report.CertificateType;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.util.CertUtil;
import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.validation.service.generic.configuration.GenericSignaturePolicyProperties;
import ee.openeid.validation.service.generic.configuration.GenericValidationServiceConfiguration;
import ee.openeid.validation.service.generic.validator.container.ContainerValidatorFactory;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ValidationServiceTest.TestConfiguration.class})
@ExtendWith(SpringExtension.class)
@Slf4j
public class ValidationServiceTest {

    private static final String TEST_FILES_LOCATION = "test-files/";

    GenericValidationService validationService;

    private ConstraintLoadingSignaturePolicyService signaturePolicyService;
    @Autowired
    private TrustedListsCertificateSource trustedListsCertificateSource;
    @Autowired
    private GenericSignaturePolicyProperties policySettings;
    @Autowired
    private ContainerValidatorFactory containerValidatorFactory;

    @BeforeEach
    public void setUp() {
        validationService = new GenericValidationService();
        validationService.setTrustedListsCertificateSource(trustedListsCertificateSource);

        signaturePolicyService = new ConstraintLoadingSignaturePolicyService(policySettings);
        validationService.setSignaturePolicyService(signaturePolicyService);
        validationService.setReportConfigurationProperties(new ReportConfigurationProperties(true));

        validationService.setContainerValidatorFactory(containerValidatorFactory);
    }

    @Test
    public void certificatePresent_asice() throws Exception {

        Reports reports = validationService.validateDocument(buildValidationDocument("bdoc21-TS.asice"));
        SignatureValidationData signatureValidationData = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        assertEquals(3, signatureValidationData.getCertificates().size());

        Certificate signerCertificate = signatureValidationData.getCertificatesByType(CertificateType.SIGNING).get(0);
        assertEquals("SINIVEE,VEIKO,36706020210", signerCertificate.getCommonName());
        byte[] encodedSigningCertificate = signerCertificate.getContent().getBytes();
        java.security.cert.Certificate signerX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(encodedSigningCertificate)));
        assertEquals("SINIVEE,VEIKO,36706020210", CertUtil.getCommonName((X509Certificate) signerX509Certificate));

        assertEquals("ESTEID-SK 2011", signerCertificate.getIssuer().getCommonName());


        Certificate timestampCertificate = signatureValidationData.getCertificatesByType(CertificateType.SIGNATURE_TIMESTAMP).get(0);
        assertEquals("SK TIMESTAMPING AUTHORITY", timestampCertificate.getCommonName());
        byte[] encodedTimestampCertificate = timestampCertificate.getContent().getBytes();
        java.security.cert.Certificate timestampX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(encodedTimestampCertificate)));
        assertEquals("SK TIMESTAMPING AUTHORITY", CertUtil.getCommonName((X509Certificate) timestampX509Certificate));

        Certificate revocationCertificate = signatureValidationData.getCertificatesByType(CertificateType.REVOCATION).get(0);

        assertEquals("SK OCSP RESPONDER 2011", revocationCertificate.getCommonName());
        byte[] encodedRevocationCertificate = revocationCertificate.getContent().getBytes();
        java.security.cert.Certificate revocationX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(encodedRevocationCertificate)));
        assertEquals("SK OCSP RESPONDER 2011", CertUtil.getCommonName((X509Certificate) revocationX509Certificate));
    }

    @Test
    public void certificatePresent_pdf() throws Exception {
        Reports reports = validationService.validateDocument(buildValidationDocument("hellopades-pades-lt-sha256-sign.pdf"));
        SignatureValidationData signatureValidationData = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        assertEquals(3, signatureValidationData.getCertificates().size());

        Certificate signerCertificate = signatureValidationData.getCertificatesByType(CertificateType.SIGNING).get(0);
        assertEquals("SINIVEE,VEIKO,36706020210", signerCertificate.getCommonName());
        java.security.cert.Certificate signerX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(signerCertificate.getContent().getBytes())));
        assertEquals("SINIVEE,VEIKO,36706020210", CertUtil.getCommonName((X509Certificate) signerX509Certificate));

        assertEquals("ESTEID-SK 2011", signerCertificate.getIssuer().getCommonName());


        Certificate timestampCertificate = signatureValidationData.getCertificatesByType(CertificateType.SIGNATURE_TIMESTAMP).get(0);
        assertEquals("SK TIMESTAMPING AUTHORITY", timestampCertificate.getCommonName());
        java.security.cert.Certificate timestampX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(timestampCertificate.getContent().getBytes())));
        assertEquals("SK TIMESTAMPING AUTHORITY", CertUtil.getCommonName((X509Certificate) timestampX509Certificate));

        Certificate revocationCertificate = signatureValidationData.getCertificatesByType(CertificateType.REVOCATION).get(0);
        assertEquals("SK OCSP RESPONDER 2011", revocationCertificate.getCommonName());
        java.security.cert.Certificate revocationX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(revocationCertificate.getContent().getBytes())));
        assertEquals("SK OCSP RESPONDER 2011", CertUtil.getCommonName((X509Certificate) revocationX509Certificate));
    }

    ValidationDocument buildValidationDocument(String testFile) {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();
    }

    @Import({
            TSLLoaderConfiguration.class,
            GenericValidationServiceConfiguration.class
    })
    public static class TestConfiguration {

        @Bean
        public ProxyConfig proxyConfig() {
            return new ProxyConfig();
        }

        @Bean
        public TSLLoader tslLoader() {
            return new TSLLoader();
        }

        @Bean
        public TSLValidationJobFactory tslValidationJobFactory() {
            return new TSLValidationJobFactory();
        }

    }
}
