/*
 * Copyright 2020 - 2024 Riigi Infosüsteemi Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

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
import ee.openeid.tsl.TSLRefresher;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.validation.service.generic.configuration.properties.GenericSignaturePolicyProperties;
import ee.openeid.validation.service.generic.configuration.GenericValidationServiceConfiguration;
import ee.openeid.validation.service.generic.validator.RevocationFreshnessValidatorFactory;
import ee.openeid.validation.service.generic.validator.container.ContainerValidatorFactory;
import ee.openeid.validation.service.generic.validator.ocsp.OCSPSourceFactory;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {GenericValidationServiceTest.TestConfiguration.class})
@ExtendWith(SpringExtension.class)
@Slf4j
class GenericValidationServiceTest {

    private static final String TEST_FILES_LOCATION = "test-files/";

    GenericValidationService validationService;

    private ConstraintLoadingSignaturePolicyService signaturePolicyService;
    @Autowired
    private TrustedListsCertificateSource trustedListsCertificateSource;
    @Autowired
    private GenericSignaturePolicyProperties policySettings;
    @Autowired
    private ContainerValidatorFactory containerValidatorFactory;
    @Autowired
    private RevocationFreshnessValidatorFactory revocationFreshnessValidatorFactory;
    @Autowired
    private OCSPSourceFactory ocspSourceFactory;

    @BeforeEach
    public void setUp() {
        validationService = new GenericValidationService();
        validationService.setTrustedListsCertificateSource(trustedListsCertificateSource);

        signaturePolicyService = new ConstraintLoadingSignaturePolicyService(policySettings);
        validationService.setSignaturePolicyService(signaturePolicyService);
        validationService.setReportConfigurationProperties(new ReportConfigurationProperties(true));

        validationService.setContainerValidatorFactory(containerValidatorFactory);
        validationService.setRevocationFreshnessValidatorFactory(revocationFreshnessValidatorFactory);
        validationService.setOcspSourceFactory(ocspSourceFactory);
    }

    @Test
    void certificatePresent_asice() throws Exception {
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
    void certificatePresent_pdf() throws Exception {
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

    @Test
    void createValidatorFromDocument_ValidationTimeProvided_SignedDocumentValidatorInstantiatedWithValidationTimeSet() {
        try (MockedStatic<SignedDocumentValidator> mockedStaticSignedDocumentValidator = Mockito.mockStatic(SignedDocumentValidator.class)) {
            SignedDocumentValidator validatorMock = Mockito.mock(SignedDocumentValidator.class);
            mockedStaticSignedDocumentValidator.when(() -> SignedDocumentValidator.fromDocument(any(DSSDocument.class))).thenReturn(validatorMock);

            ValidationDocument validationDocument = buildValidationDocument("bdoc21-TS.asice");
            Date validationTime = new Date();
            validationDocument.setValidationTime(validationTime);

            validationService.createValidatorFromDocument(validationDocument);

            verify(validatorMock).setValidationTime(validationTime);
        }
    }

    @Test
    void createValidatorFromDocument_ValidationTimeNotProvided_SignedDocumentValidatorInstantiatedWithoutValidationTimeSet() {
        try (MockedStatic<SignedDocumentValidator> mockedStaticSignedDocumentValidator = Mockito.mockStatic(SignedDocumentValidator.class)) {
            SignedDocumentValidator validatorMock = Mockito.mock(SignedDocumentValidator.class);
            mockedStaticSignedDocumentValidator.when(() -> SignedDocumentValidator.fromDocument(any(DSSDocument.class))).thenReturn(validatorMock);

            validationService.createValidatorFromDocument(buildValidationDocument("bdoc21-TS.asice"));

            verify(validatorMock, never()).setValidationTime(any(Date.class));
        }
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
            return new TSLLoader("testName");
        }

        @Bean
        public TSLRefresher tslRefresher() {
            return new TSLRefresher();
        }

        @Bean
        public TSLValidationJobFactory tslValidationJobFactory() {
            return new TSLValidationJobFactory();
        }

    }
}
