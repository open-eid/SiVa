/*
 * Copyright 2016 - 2025 Riigi Infosüsteemi Amet
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
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureProductionPlace;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.SubjectDistinguishedName;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.TSLRefresher;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.validation.service.generic.configuration.properties.GenericSignaturePolicyProperties;
import ee.openeid.validation.service.generic.configuration.GenericValidationServiceConfiguration;
import ee.openeid.validation.service.generic.validator.RevocationFreshnessValidatorFactory;
import ee.openeid.validation.service.generic.validator.container.ContainerValidatorFactory;
import ee.openeid.validation.service.generic.validator.ocsp.OCSPSourceFactory;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {PDFValidationServiceTest.TestConfiguration.class})
@ExtendWith(SpringExtension.class)
class PDFValidationServiceTest {

    private static final String TEST_FILES_LOCATION = "test-files/";
    private static final String PDF_WITH_REASON_AND_LOCATION = "pdf_with_reason_and_location.pdf";

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
    void testConfiguration() {
        assertNotNull(trustedListsCertificateSource);
        assertEquals(2, signaturePolicyService.getSignaturePolicies().size());
        assertNotNull(signaturePolicyService.getPolicy(null));
    }

    @Test
    void givenInvalidPDFFileShouldThrowServiceException() {
        assertThrows(
                ValidationServiceException.class, () -> validationService.validateDocument(new ValidationDocument())
        );
    }

    @Test
    void populatesLocationAsCountryName() {
        SimpleReport report = validateAndAssertReports(buildValidationDocument(PDF_WITH_REASON_AND_LOCATION)).getSimpleReport();
        SignatureProductionPlace signatureProductionPlace = report.getValidationConclusion().getSignatures().get(0)
                .getInfo().getSignatureProductionPlace();
        assertNotNull(signatureProductionPlace);
        assertEquals("Narva", signatureProductionPlace.getCountryName());
        assertEquals("", signatureProductionPlace.getPostalCode());
        assertEquals("", signatureProductionPlace.getStateOrProvince());
        assertEquals("", signatureProductionPlace.getCity());
    }

    @Test
    void populatesSigningReason() {
        SimpleReport report = validateAndAssertReports(buildValidationDocument(PDF_WITH_REASON_AND_LOCATION)).getSimpleReport();
        String reason = report.getValidationConclusion().getSignatures().get(0)
                .getInfo().getSigningReason();
        assertEquals("Roll??", reason);
    }

    @Test
    void populatesSignatureMethod() {
        SimpleReport report = validateAndAssertReports(buildValidationDocument(PDF_WITH_REASON_AND_LOCATION)).getSimpleReport();
        assertEquals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512",
                report.getValidationConclusion().getSignatures().get(0).getSignatureMethod());
    }

    void assertNoErrors(SignatureValidationData signatureValidationData) {
        assertEquals(0, signatureValidationData.getErrors().size());
    }

    void assertValidationDate(Date validationStartDate, Date validationDate) {
        Date now = new Date();
        assertTrue(validationDate.after(validationStartDate) || validationDate.equals(validationStartDate));
        assertTrue(validationDate.before(now) || validationDate.equals(now));
    }

    Reports validateAndAssertReports(ValidationDocument validationDocument) {
        Reports reports = validationService.validateDocument(validationDocument);
        assertAllReportsPresent(reports);
        assertReportsValidationConclusionsIdentical(reports);
        assertReportsDocumentName(validationDocument.getName(), reports);
        return reports;
    }

    private void assertReportsDocumentName(String documentName, Reports reports) {
        assertEquals(documentName, reports.getSimpleReport().getValidationConclusion().getValidatedDocument().getFilename());
        assertEquals(documentName, reports.getDiagnosticReport().getDiagnosticData().getDocumentName());
    }

    private void assertReportsValidationConclusionsIdentical(Reports reports) {
        assertEquals(reports.getSimpleReport().getValidationConclusion(), reports.getDetailedReport().getValidationConclusion());
        assertEquals(reports.getSimpleReport().getValidationConclusion(), reports.getDiagnosticReport().getValidationConclusion());
    }

    private void assertAllReportsPresent(Reports reports) {
        assertNotNull(reports.getSimpleReport());
        assertNotNull(reports.getDetailedReport());
        assertNotNull(reports.getDiagnosticReport());
    }

    void assertSubjectDNPresent(SignatureValidationData signature, String serialNumber, String commonName) {
        SubjectDistinguishedName subjectDistinguishedName = signature.getSubjectDistinguishedName();
        assertNotNull(subjectDistinguishedName);
        assertEquals(serialNumber, subjectDistinguishedName.getSerialNumber());
        assertEquals(commonName, subjectDistinguishedName.getCommonName());
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
        ProxyConfig proxyConfig(){
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
