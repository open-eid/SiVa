/*
 * Copyright 2019 Riigi Infosüsteemide Amet
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
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.exception.DocumentRequirementsException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.siva.validation.util.CertUtil;
import ee.openeid.validation.service.timestamptoken.configuration.TimeStampTokenSignaturePolicyProperties;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
public class TimeStampTokenValidationServiceTest {

    private static final String TEST_FILES_LOCATION = "test-files/";

    private TimeStampTokenValidationService validationService;
    private TimeStampTokenSignaturePolicyProperties policyProperties = new TimeStampTokenSignaturePolicyProperties();


    @BeforeEach
    public void setUp() {
        validationService = new TimeStampTokenValidationService();
        policyProperties.initPolicySettings();
        SignaturePolicyService<ValidationPolicy> signaturePolicyService = new SignaturePolicyService<>(policyProperties);
        validationService.setSignaturePolicyService(signaturePolicyService);
        ReportConfigurationProperties reportConfigurationProperties = new ReportConfigurationProperties(true);
        validationService.setReportConfigurationProperties(reportConfigurationProperties);
    }

    @Test
    public void validTimeStampToken() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("timestamptoken-ddoc.asics")).getSimpleReport();
        TimeStampTokenValidationData validationData = simpleReport.getValidationConclusion().getTimeStampTokens().get(0);
        assertEquals(TimeStampTokenValidationData.Indication.TOTAL_PASSED, validationData.getIndication());
        assertEquals("SK TIMESTAMPING AUTHORITY", validationData.getSignedBy());
        assertNull(validationData.getError());
    }

    @Test
    public void certificatePresent() throws Exception{
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("timestamptoken-ddoc.asics")).getSimpleReport();
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
    public void multipleDataFile() {
        assertThrows(
            DocumentRequirementsException.class, () -> {
                validationService.validateDocument(buildValidationDocument("timestamptoken-two-data-files.asics"));
            }
        );
    }

    @Test
    public void notValidTstFile() {
        assertThrows(
            MalformedDocumentException.class, () -> {
                validationService.validateDocument(buildValidationDocument("timestamptoken-invalid.asics"));
            }
        );

    }

    @Test
    public void dataFiledChanged() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("timestamptoken-datafile-changed.asics")).getSimpleReport();
        assertEquals("Signature not intact", simpleReport.getValidationConclusion().getTimeStampTokens().get(0).getError().get(0).getContent());
    }

    @Test
    public void signatureNotIntact() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("timestamptoken-signature-modified.asics")).getSimpleReport();
        assertEquals("Signature not intact", simpleReport.getValidationConclusion().getTimeStampTokens().get(0).getError().get(0).getContent());
    }

    @Test
    public void onlySimpleReportPresentInDocumentValidationResultReports() {
        Reports reports = validationService.validateDocument(buildValidationDocument("timestamptoken-ddoc.asics"));

        assertNotNull(reports.getSimpleReport().getValidationConclusion());
        assertNotNull(reports.getDetailedReport().getValidationConclusion());
        assertNotNull(reports.getDiagnosticReport().getValidationConclusion());

        assertNull(reports.getDetailedReport().getValidationProcess());
        assertNull(reports.getDiagnosticReport().getDiagnosticData());
    }

    private ValidationDocument buildValidationDocument(String testFile) {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();
    }

}
