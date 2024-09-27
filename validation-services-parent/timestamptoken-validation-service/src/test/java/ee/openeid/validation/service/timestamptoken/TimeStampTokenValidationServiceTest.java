/*
 * Copyright 2019 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timestamptoken;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.CertificateType;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.exception.DocumentRequirementsException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.util.CertUtil;
import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.TSLRefresher;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.validation.service.generic.configuration.GenericValidationServiceConfiguration;
import ee.openeid.validation.service.timestamptoken.configuration.TimeStampTokenSignaturePolicyProperties;
import eu.europa.esig.dss.asic.cades.validation.ASiCContainerWithCAdESValidator;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import lombok.Setter;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TimeStampTokenValidationServiceTest.TestConfiguration.class})
@ExtendWith(SpringExtension.class)
class TimeStampTokenValidationServiceTest {

    private static final String TEST_FILES_LOCATION = "test-files/";

    private TimeStampTokenValidationService validationService;
    private final TimeStampTokenSignaturePolicyProperties policyProperties = new TimeStampTokenSignaturePolicyProperties();

    @Autowired
    private TrustedListsCertificateSource trustedListsCertificateSource;

    @BeforeEach
    public void setUp() {
        validationService = new TimeStampTokenValidationService();
        policyProperties.initPolicySettings();
        ConstraintLoadingSignaturePolicyService signaturePolicyService = new ConstraintLoadingSignaturePolicyService(policyProperties);
        validationService.setSignaturePolicyService(signaturePolicyService);
        validationService.setTrustedListsCertificateSource(trustedListsCertificateSource);
        ReportConfigurationProperties reportConfigurationProperties = new ReportConfigurationProperties(true);
        validationService.setReportConfigurationProperties(reportConfigurationProperties);
    }

    @Test
    @Disabled("SIVA-719")
    void validTimeStampToken() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("timestamptoken-ddoc.asics")).getSimpleReport();
        assertEquals(1, simpleReport.getValidationConclusion().getTimeStampTokens().size());
        TimeStampTokenValidationData validationData = simpleReport.getValidationConclusion().getTimeStampTokens().get(0);
        assertEquals(TimeStampTokenValidationData.Indication.TOTAL_PASSED, validationData.getIndication());
        assertEquals("SK TIMESTAMPING AUTHORITY", validationData.getSignedBy());
        assertNull(validationData.getError());
    }

    @Test
    void certificatePresent() throws Exception{
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("timestamptoken-ddoc.asics")).getSimpleReport();
        assertEquals(1, simpleReport.getValidationConclusion().getTimeStampTokens().size());
        TimeStampTokenValidationData timeStampTokenValidationData = simpleReport.getValidationConclusion().getTimeStampTokens().get(0);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        assertEquals(1, timeStampTokenValidationData.getCertificates().size());

        ee.openeid.siva.validation.document.report.Certificate certificate = timeStampTokenValidationData.getCertificates().get(0);
        assertEquals(CertificateType.CONTENT_TIMESTAMP, certificate.getType());
        assertEquals("SK TIMESTAMPING AUTHORITY", certificate.getCommonName());
        Certificate timeStampCertificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(certificate.getContent().getBytes())));
        assertEquals("SK TIMESTAMPING AUTHORITY", CertUtil.getCommonName((X509Certificate) timeStampCertificate));
    }

    @Test
    void multipleTimestamps() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("2xTST-valid-bdoc-data-file.asics")).getSimpleReport();
        List<TimeStampTokenValidationData> tokens = simpleReport.getValidationConclusion().getTimeStampTokens();
        assertEquals(2, tokens.size());
        assertEquals("DEMO SK TIMESTAMPING AUTHORITY 2023E", tokens.get(0).getSignedBy());
        assertEquals("2024-03-27T12:42:57Z", tokens.get(0).getSignedTime());
        assertEquals("DEMO SK TIMESTAMPING AUTHORITY 2023R", tokens.get(1).getSignedBy());
        assertEquals("2024-08-26T13:31:34Z", tokens.get(1).getSignedTime());
    }

    @Test
    void multipleDataFile() {
        assertThrows(
            DocumentRequirementsException.class, () -> {
                validationService.validateDocument(buildValidationDocument("timestamptoken-two-data-files.asics"));
            }
        );
    }

    @Test
    void notValidTstFile() {
        assertThrows(
            MalformedDocumentException.class, () -> {
                validationService.validateDocument(buildValidationDocument("timestamptoken-invalid.asics"));
            }
        );
    }

    @Test
    void dataFiledChanged() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("timestamptoken-datafile-changed.asics")).getSimpleReport();
        assertEquals("The time-stamp message imprint is not intact!", simpleReport.getValidationConclusion().getTimeStampTokens().get(0).getError().get(0).getContent());
    }

    @Test
    void signatureNotIntact() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("timestamptoken-signature-modified.asics")).getSimpleReport();
        assertEquals("The signature is not intact!", simpleReport.getValidationConclusion().getTimeStampTokens().get(0).getError().get(0).getContent());
    }

    @Test
    void onlySimpleReportPresentInDocumentValidationResultReports() {
        Reports reports = validationService.validateDocument(buildValidationDocument("timestamptoken-ddoc.asics"));

        assertNotNull(reports.getSimpleReport().getValidationConclusion());
        assertNotNull(reports.getDetailedReport().getValidationConclusion());
        assertNotNull(reports.getDiagnosticReport().getValidationConclusion());

        assertNull(reports.getDetailedReport().getValidationProcess());
        assertNull(reports.getDiagnosticReport().getDiagnosticData());
    }

    @Test
    void validateDocument_ValidationTimeProvided_ValidationTimeSetForValidator() {
        ValidationDocument validationDocument = buildValidationDocument("timestamptoken-ddoc.asics");
        Date validationTime = new Date();
        validationDocument.setValidationTime(validationTime);
        ASiCContainerWithCAdESValidator validatorMock = Mockito.mock(ASiCContainerWithCAdESValidator.class);

        createServiceFake(validatorMock).validateDocument(validationDocument);

        verify(validatorMock).setValidationTime(validationTime);
    }

    @Test
    void validateDocument_ValidationTimeNotProvided_ValidationTimeNotSetForValidator() {
        ValidationDocument validationDocument = buildValidationDocument("timestamptoken-ddoc.asics");
        ASiCContainerWithCAdESValidator validatorMock = Mockito.mock(ASiCContainerWithCAdESValidator.class);

        createServiceFake(validatorMock).validateDocument(validationDocument);

        verify(validatorMock, never()).setValidationTime(any(Date.class));
    }

    private TimeStampTokenValidationServiceFake createServiceFake(ASiCContainerWithCAdESValidator validatorMock) {
        TimeStampTokenValidationServiceFake validationServiceFake = new TimeStampTokenValidationServiceFake();
        when(validatorMock.validateDocument(any(InputStream.class))).thenReturn(
            new eu.europa.esig.dss.validation.reports.Reports(null, null, new XmlSimpleReport(), null)
        );
        validationServiceFake.setValidator(validatorMock);

        validationServiceFake.setSignaturePolicyService(new ConstraintLoadingSignaturePolicyService(policyProperties));
        validationServiceFake.setTrustedListsCertificateSource(trustedListsCertificateSource);
        validationServiceFake.setReportConfigurationProperties(new ReportConfigurationProperties(true));

        return validationServiceFake;
    }

    private static class TimeStampTokenValidationServiceFake extends TimeStampTokenValidationService {

        @Setter
        private ASiCContainerWithCAdESValidator validator;

        @Override
        protected ASiCContainerWithCAdESValidator createValidatorFromDocument(final ValidationDocument validationDocument) {
            return validator;
        }
    }

    private ValidationDocument buildValidationDocument(String testFile) {
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
