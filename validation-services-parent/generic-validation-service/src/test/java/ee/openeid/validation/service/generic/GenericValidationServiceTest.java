/*
 * Copyright 2020 - 2025 Riigi Infosüsteemi Amet
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.ArchiveTimeStamp;
import ee.openeid.siva.validation.document.report.Certificate;
import ee.openeid.siva.validation.document.report.CertificateType;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.util.CertUtil;
import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.TSLRefresher;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.validation.service.generic.configuration.GenericValidationServiceConfiguration;
import ee.openeid.validation.service.generic.configuration.properties.GenericSignaturePolicyProperties;
import ee.openeid.validation.service.generic.validator.RevocationFreshnessValidatorFactory;
import ee.openeid.validation.service.generic.validator.container.ContainerValidatorFactory;
import ee.openeid.validation.service.generic.validator.ocsp.OCSPSourceFactory;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {GenericValidationServiceTest.TestConfiguration.class})
@ExtendWith(SpringExtension.class)
@Slf4j
@ActiveProfiles("test")
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

        assertNull(reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getArchiveTimeStamps());
    }

    @Test
    void certificatePresent_pdf() throws Exception {
        Reports reports = validationService.validateDocument(buildValidationDocument("TEST_ESTEID2018_PAdES_LT_enveloped.pdf"));
        SignatureValidationData signatureValidationData = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        assertEquals(3, signatureValidationData.getCertificates().size());

        Certificate signerCertificate = signatureValidationData.getCertificatesByType(CertificateType.SIGNING).get(0);
        assertEquals("JÕEORG,JAAK-KRISTJAN,38001085718", signerCertificate.getCommonName());
        java.security.cert.Certificate signerX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(signerCertificate.getContent().getBytes())));
        assertEquals("JÕEORG,JAAK-KRISTJAN,38001085718", CertUtil.getCommonName((X509Certificate) signerX509Certificate));

        assertEquals("TEST of ESTEID2018", signerCertificate.getIssuer().getCommonName());


        Certificate timestampCertificate = signatureValidationData.getCertificatesByType(CertificateType.SIGNATURE_TIMESTAMP).get(0);
        assertEquals("DEMO SK TIMESTAMPING AUTHORITY 2023E", timestampCertificate.getCommonName());
        java.security.cert.Certificate timestampX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(timestampCertificate.getContent().getBytes())));
        assertEquals("DEMO SK TIMESTAMPING AUTHORITY 2023E", CertUtil.getCommonName((X509Certificate) timestampX509Certificate));

        Certificate revocationCertificate = signatureValidationData.getCertificatesByType(CertificateType.REVOCATION).get(0);
        assertEquals("DEMO of ESTEID-SK 2018 AIA OCSP RESPONDER 2018", revocationCertificate.getCommonName());
        java.security.cert.Certificate revocationX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(revocationCertificate.getContent().getBytes())));
        assertEquals("DEMO of ESTEID-SK 2018 AIA OCSP RESPONDER 2018", CertUtil.getCommonName((X509Certificate) revocationX509Certificate));
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

    @Test
    void validateAsiceContainer_ProfileLevelIsLTA_ArchiveTimestampsBlockIsPresent() {
        Reports reports = validationService.validateDocument(buildValidationDocument("valid_asice_lta.asice"));
        ValidationConclusion validationConclusion = reports.getSimpleReport().getValidationConclusion();

        assertThat(validationConclusion.getSignatures().get(0).getInfo().getArchiveTimeStamps(), notNullValue());
        assertThat(validationConclusion.getSignatures().get(0).getInfo().getArchiveTimeStamps(), hasSize(1));

        {
            ArchiveTimeStamp archiveTimeStamp = validationConclusion.getSignatures().get(0).getInfo().getArchiveTimeStamps().get(0);
            assertThat(archiveTimeStamp.getSignedTime(), equalTo("2024-11-13T08:21:07Z"));
            assertThat(archiveTimeStamp.getIndication(), sameInstance(Indication.PASSED));
            assertThat(archiveTimeStamp.getSubIndication(), nullValue());
            assertThat(archiveTimeStamp.getSignedBy(), equalTo("DEMO SK TIMESTAMPING AUTHORITY 2023E"));
            assertThat(archiveTimeStamp.getCountry(), equalTo("EE"));
            assertThat(archiveTimeStamp.getContent(), equalTo(loadTimestampContent("timestamp_content.txt")));
        }
    }

    @ParameterizedTest
    @MethodSource("timestampProvider")
    void validateAsiceContainer_ProfileLevelIsLTA_ArchiveTimestampsBlockIsPresentForMultipleTimestamps(String signedTime, String expectedContent, int iterator) {
        Reports reports = validationService.validateDocument(buildValidationDocument("test_lta_multiple_timestamps.asice"));
        ValidationConclusion validationConclusion = reports.getSimpleReport().getValidationConclusion();

        List<ArchiveTimeStamp> archiveTimeStamps = validationConclusion.getSignatures().get(0).getInfo().getArchiveTimeStamps();

        assertThat(archiveTimeStamps, notNullValue());
        assertThat(archiveTimeStamps, hasSize(10));

        {
            ArchiveTimeStamp archiveTimeStamp = archiveTimeStamps.get(iterator);
            assertThat(archiveTimeStamp.getSignedTime(), equalTo(signedTime));
            assertThat(archiveTimeStamp.getIndication(), sameInstance(Indication.PASSED));
            assertThat(archiveTimeStamp.getSubIndication(), nullValue());
            assertThat(archiveTimeStamp.getSignedBy(), equalTo("DEMO SK TIMESTAMPING AUTHORITY 2023E"));
            assertThat(archiveTimeStamp.getCountry(), equalTo("EE"));
            assertThat(archiveTimeStamp.getContent(), equalTo(expectedContent));
        }
    }

    private static String loadTimestampContent(String filename) {
        try {
            return Files.readString(Path.of("src/test/resources/test-files/" + filename));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<String> loadContentsFromJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<String>> jsonMap = objectMapper.readValue(
                new File("src/test/resources/test-files/multiple_timestamp_content.json"),
                new TypeReference<>() {}
        );
        return jsonMap.get("timestampContents");
    }

    private static Stream<Arguments> timestampProvider() throws Exception {
        List<String> expectedContents = loadContentsFromJson();

        List<String> signedTimes = List.of(
                "2024-11-13T08:21:07Z", "2025-02-07T20:21:21Z", "2025-02-07T20:22:39Z",
                "2025-02-07T20:23:39Z", "2025-02-07T20:24:03Z", "2025-02-07T20:26:48Z",
                "2025-02-07T20:27:16Z", "2025-02-07T20:27:38Z", "2025-02-07T20:28:02Z",
                "2025-02-07T20:28:24Z"
        );

        return IntStream.range(0, signedTimes.size())
                .mapToObj(i -> Arguments.of(signedTimes.get(i), expectedContents.get(i), i));
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
