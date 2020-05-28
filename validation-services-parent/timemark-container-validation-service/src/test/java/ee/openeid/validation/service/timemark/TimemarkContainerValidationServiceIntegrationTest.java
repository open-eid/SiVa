/*
 * Copyright 2020 Riigi Infosüsteemide Amet
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

package ee.openeid.validation.service.timemark;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource;
import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.validation.service.timemark.configuration.TestDigiDoc4jConfiguration;
import ee.openeid.validation.service.timemark.configuration.TimemarkContainerValidationServiceConfiguration;
import ee.openeid.validation.service.timemark.signature.policy.BDOCConfigurationService;
import ee.openeid.validation.service.timemark.signature.policy.BDOCSignaturePolicyService;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.tsl.Condition;
import eu.europa.esig.dss.spi.tsl.ServiceInfo;
import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.TSLCertificateSource;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ee.openeid.validation.service.timemark.BDOCTestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        TestDigiDoc4jConfiguration.class,
        TSLLoaderConfiguration.class,
        TSLLoader.class,
        TSLValidationJobFactory.class,
        TimemarkContainerValidationServiceConfiguration.class,
        TimemarkContainerValidationService.class,
        BDOCSignaturePolicyService.class,
        ConstraintLoadingSignaturePolicyService.class,
        BDOCConfigurationService.class,
        ReportConfigurationProperties.class
})
@ActiveProfiles("test")
public class TimemarkContainerValidationServiceIntegrationTest {

    private static final String QC_WITH_QSCD = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCWithQSCD";
    private static final String QC_STATEMENT = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCStatement";
    private static final String QC_FOR_ESIG = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCForESig";
    private static final String TEST_OF_KLASS3_SK_2010 = "TEST of KLASS3-SK 2010";
    private static String POL_V3 = "POLv3";
    private static String POL_V4 = "POLv4";

    private static String DOCUMENT_MALFORMED_MESSAGE = "Document malformed or not matching documentType";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Autowired
    private TimemarkContainerValidationService timemarkContainerValidationService;
    @Autowired
    private BDOCConfigurationService configurationService;
    @Autowired
    private ReportConfigurationProperties reportConfigurationProperties;


    @Test
    public void vShouldHaveSignatureWarnings() throws Exception {
        SimpleReport validationResult = timemarkContainerValidationService.validateDocument(buildValidationDocument(BDOC_TEST_FILE_UNSIGNED)).getSimpleReport();
        List<Warning> signatureValidationData = validationResult.getValidationConclusion().getSignatures().get(0).getWarnings();
        assertThat(signatureValidationData, hasSize(2));
        assertThat(signatureValidationData, containsInAnyOrder(
                hasProperty("content", is("The signature/seal is not a valid AdES!")),
                hasProperty("content", is("Signature SOLOVEI,JULIA,47711040261 has unsigned files: document_3.xml"))
        ));
    }

    @Test
    public void vShouldNotHaveValidationWarnings() throws Exception {
        SimpleReport validationResult = timemarkContainerValidationService.validateDocument(buildValidationDocument(BDOC_TEST_FILE_ALL_SIGNED)).getSimpleReport();
        List<ValidationWarning> validationWarnings = validationResult.getValidationConclusion().getValidationWarnings();
        assertThat(validationWarnings, hasSize(0));
    }

    @Test
    public void verifyCorrectPolicyIsLoadedToD4JConfiguration() throws Exception {
        System.out.println(configurationService.loadPolicyConfiguration(null).getConfiguration().getValidationPolicy());

        System.out.println(configurationService.loadPolicyConfiguration(POL_V4).getConfiguration().getValidationPolicy());
        System.out.println(configurationService.loadPolicyConfiguration(POL_V3).getConfiguration().getValidationPolicy());
        assertTrue(configurationService.loadPolicyConfiguration(null).getConfiguration().getValidationPolicy().contains("siva-bdoc-POLv4-constraint"));
        assertTrue(configurationService.loadPolicyConfiguration(POL_V4).getConfiguration().getValidationPolicy().contains("siva-bdoc-POLv4-constraint"));
        assertTrue(configurationService.loadPolicyConfiguration(POL_V3).getConfiguration().getValidationPolicy().contains("siva-bdoc-POLv3-constraint"));
    }

    @Test
    public void validatingABDOCWithMalformedBytesResultsInMalformedDocumentException() throws Exception {
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setBytes("Hello".getBytes());
        expectedException.expect(MalformedDocumentException.class);
        expectedException.expectMessage(DOCUMENT_MALFORMED_MESSAGE);
        timemarkContainerValidationService.validateDocument(validationDocument);
    }

    @Test
    public void validatingAnXRoadBatchSignatureAsicContainerWithBdocValidatorThrowsMalformedDocumentException() throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(XROAD_BATCHSIGNATURE_CONTAINER);
        expectedException.expect(MalformedDocumentException.class);
        expectedException.expectMessage(DOCUMENT_MALFORMED_MESSAGE);
        timemarkContainerValidationService.validateDocument(validationDocument);
    }

    @Test
    public void validatingAnXRoadSimpleAsicContainerWithBdocValidatorThrowsMalformedDocumentException() throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(XROAD_SIMPLE_CONTAINER);
        expectedException.expect(MalformedDocumentException.class);
        expectedException.expectMessage(DOCUMENT_MALFORMED_MESSAGE);
        timemarkContainerValidationService.validateDocument(validationDocument);
    }

    @Test
    public void bdocValidationResultShouldIncludeValidationReportPOJO() throws Exception {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(bdocValid2Signatures()).getSimpleReport();
        assertNotNull(validationResult2Signatures);
    }

    @Test
    public void vShouldIncludeRequiredFields() throws Exception {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(bdocValid2Signatures()).getSimpleReport();
        ValidationConclusion validationConclusion = validationResult2Signatures.getValidationConclusion();
        assertNotNull(validationConclusion.getPolicy());
        assertNotNull(validationConclusion.getValidationTime());
        assertEquals(VALID_BDOC_TM_2_SIGNATURES, validationConclusion.getValidatedDocument().getFilename());
        assertTrue(validationConclusion.getSignatures().size() == 2);
        assertTrue(validationConclusion.getValidSignaturesCount() == 2);
        assertTrue(validationConclusion.getSignaturesCount() == 2);
    }

    @Test
    public void signatureScopeShouldBeCorrectWhenDatafilesContainSpacesOrParenthesis() throws Exception {
        SimpleReport report = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_ID_CARD_MOB_ID)).getSimpleReport();
        report.getValidationConclusion().getSignatures().forEach(sig -> assertContainsScope(sig, "Proov (2).txt"));
        SimpleReport report2 = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_BALTIC_EST_LT)).getSimpleReport();
        report2.getValidationConclusion().getSignatures().forEach(sig -> assertContainsScope(sig, "Baltic MoU digital signing_04112015.docx"));
    }

    private void assertContainsScope(SignatureValidationData signature, String filename) {
        assertTrue(signature.getSignatureScopes()
                .stream()
                .map(SignatureScope::getName)
                .filter(name -> StringUtils.equals(filename, name))
                .count() > 0);
    }

    @Test
    public void vShouldHaveCorrectSignatureValidationDataForSignature1() throws Exception {

        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(bdocValid2Signatures()).getSimpleReport();
        SignatureValidationData sig1 = validationResult2Signatures.getValidationConclusion().getSignatures()
                .stream()
                .filter(sig -> sig.getId().equals("id-7b7180a5265f919bc0ac65bd02e4b46a"))
                .findFirst()
                .get();

        assertEquals("XAdES_BASELINE_LT_TM", sig1.getSignatureFormat());
        assertEquals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha224", sig1.getSignatureMethod());
        assertEquals("QESIG", sig1.getSignatureLevel());
        assertEquals("JÕEORG,JAAK-KRISTJAN,38001085718", sig1.getSignedBy());
        assertEquals("JÕEORG,JAAK-KRISTJAN,38001085718", sig1.getSubjectDistinguishedName().getCommonName());
        assertEquals("PNOEE-38001085718", sig1.getSubjectDistinguishedName().getSerialNumber());
        assertEquals(SignatureValidationData.Indication.TOTAL_PASSED.toString(), sig1.getIndication());
        assertTrue(StringUtils.isEmpty(sig1.getSubIndication()));
        assertTrue(sig1.getErrors().size() == 0);
        assertTrue(sig1.getWarnings().size() == 0);
        assertTrue(sig1.getSignatureScopes().size() == 1);
        SignatureScope scope = sig1.getSignatureScopes().get(0);
        assertEquals("test.txt", scope.getName());
        assertEquals("Digest of the document content", scope.getContent());
        assertEquals("FullSignatureScope", scope.getScope());
        assertEquals("2020-05-21T14:07:04Z", sig1.getClaimedSigningTime());
        assertEquals("2020-05-21T14:07:01Z", sig1.getInfo().getBestSignatureTime());
        assertEquals("Roll / Resulutsioon", sig1.getInfo().getSignerRole().get(0).getRole());
        assertEquals("Tallinn", sig1.getInfo().getSignatureProductionPlace().getCity());
        assertEquals("Harjumaa", sig1.getInfo().getSignatureProductionPlace().getStateOrProvince());
        assertEquals("Eesti", sig1.getInfo().getSignatureProductionPlace().getCountryName());
        assertEquals("", sig1.getInfo().getSignatureProductionPlace().getPostalCode());
    }

    @Test
    public void vShouldHaveCorrectSignatureValidationDataForSignature2() throws Exception {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(bdocValid2Signatures()).getSimpleReport();
        SignatureValidationData sig2 = validationResult2Signatures.getValidationConclusion().getSignatures()
                .stream()
                .filter(sig -> sig.getId().equals("id-c71904f656e45af0c9b0ce644fc9287d"))
                .findFirst()
                .get();

        assertEquals("XAdES_BASELINE_LT_TM", sig2.getSignatureFormat());
        assertEquals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha224", sig2.getSignatureMethod());
        assertEquals("QESIG", sig2.getSignatureLevel());
        assertEquals("MÄNNIK,MARI-LIIS,47101010033", sig2.getSignedBy());
        assertEquals("MÄNNIK,MARI-LIIS,47101010033", sig2.getSubjectDistinguishedName().getCommonName());
        assertEquals("47101010033", sig2.getSubjectDistinguishedName().getSerialNumber());
        assertEquals(SignatureValidationData.Indication.TOTAL_PASSED.toString(), sig2.getIndication());
        assertTrue(StringUtils.isEmpty(sig2.getSubIndication()));
        assertTrue(sig2.getErrors().size() == 0);
        assertTrue(sig2.getWarnings().size() == 0);
        assertTrue(sig2.getSignatureScopes().size() == 1);
        SignatureScope scope = sig2.getSignatureScopes().get(0);
        assertEquals("test.txt", scope.getName());
        assertEquals("Digest of the document content", scope.getContent());
        assertEquals("FullSignatureScope", scope.getScope());
        assertEquals("2020-05-28T10:59:12Z", sig2.getClaimedSigningTime());
        assertEquals("2020-05-28T10:59:14Z", sig2.getInfo().getBestSignatureTime());
        assertThat(sig2.getInfo().getSignerRole(), empty());
        assertEquals("", sig2.getInfo().getSignatureProductionPlace().getCity());
        assertEquals("", sig2.getInfo().getSignatureProductionPlace().getStateOrProvince());
        assertEquals("", sig2.getInfo().getSignatureProductionPlace().getCountryName());
        assertEquals("", sig2.getInfo().getSignatureProductionPlace().getPostalCode());
    }

    @Test
    public void reportForBdocValidationShouldIncludeCorrectAsiceSignatureForm() throws Exception {
        SimpleReport report = timemarkContainerValidationService.validateDocument(bdocValid2Signatures()).getSimpleReport();
        assertEquals("ASiC-E", report.getValidationConclusion().getSignatureForm());
    }

    @Test
    public void bestSignatureTimeInQualifiedBdocReportShouldNotBeBlank() throws Exception {
        SimpleReport report = timemarkContainerValidationService.validateDocument(bdocValidIdCardAndMobIdSignatures()).getSimpleReport();
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        String bestSignatureTime1 = validationConclusion.getSignatures().get(0).getInfo().getBestSignatureTime();
        String bestSignatureTime2 = validationConclusion.getSignatures().get(1).getInfo().getBestSignatureTime();
        assertTrue(StringUtils.isNotBlank(bestSignatureTime1));
        assertTrue(StringUtils.isNotBlank(bestSignatureTime2));
    }

    @Test
    public void populatesSignerRole() throws Exception {
        SimpleReport report = timemarkContainerValidationService.validateDocument(bdocValid2Signatures()).getSimpleReport();
        List<SignerRole> signerRole1 = report.getValidationConclusion().getSignatures().get(0).getInfo().getSignerRole();
        List<SignerRole> signerRole2 = report.getValidationConclusion().getSignatures().get(1).getInfo().getSignerRole();
        assertThat(signerRole1, hasSize(1));
        assertEquals("Roll / Resulutsioon", signerRole1.get(0).getRole());
        assertThat(signerRole2, empty());
    }

    @Test
    public void populatesSignatureProductionPlace() throws Exception {
        SimpleReport report = timemarkContainerValidationService.validateDocument(bdocValid2Signatures()).getSimpleReport();

        SignatureProductionPlace signatureProductionPlace1 = report.getValidationConclusion().getSignatures().get(0)
                .getInfo().getSignatureProductionPlace();
        SignatureProductionPlace signatureProductionPlace2 = report.getValidationConclusion().getSignatures().get(1)
                .getInfo().getSignatureProductionPlace();

        assertEquals("Tallinn", signatureProductionPlace1.getCity());
        assertEquals("Harjumaa", signatureProductionPlace1.getStateOrProvince());
        assertEquals("Eesti", signatureProductionPlace1.getCountryName());
        assertEquals("", signatureProductionPlace1.getPostalCode());

        assertEquals("", signatureProductionPlace2.getCity());
        assertEquals("", signatureProductionPlace2.getStateOrProvince());
        assertEquals("", signatureProductionPlace2.getCountryName());
        assertEquals("", signatureProductionPlace2.getPostalCode());
    }

    @Test
    @Ignore("Enable once dd4j release with fix for getSignatureMethod is available from maven")
    public void populatesSignatureMethod() throws Exception {
        SimpleReport report = timemarkContainerValidationService.validateDocument(bdocValid2Signatures()).getSimpleReport();
        assertEquals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha224",
                report.getValidationConclusion().getSignatures().get(0).getSignatureMethod());
        assertEquals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256",
                report.getValidationConclusion().getSignatures().get(1).getSignatureMethod());
    }

    @Test
    public void bdocWithCRLRevocationDataOnlyShouldFail() throws Exception {
        SimpleReport report = timemarkContainerValidationService.validateDocument(bdocCRLRevocationOnly()).getSimpleReport();
        assertTrue(report.getValidationConclusion().getValidSignaturesCount() == 0);
    }

    @Test
    public void validationReportShouldContainDefaultPolicyWhenPolicyIsNotExplicitlyGiven() throws Exception {
        Policy policy = validateWithPolicy("").getValidationConclusion().getPolicy();
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getName(), policy.getPolicyName());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void validationReportShouldContainAdesPolicyWhenAdesPolicyIsGivenToValidator() throws Exception {
        Policy policy = validateWithPolicy(POL_V3).getValidationConclusion().getPolicy();
        assertEquals(PredefinedValidationPolicySource.ADES_POLICY.getName(), policy.getPolicyName());
        assertEquals(PredefinedValidationPolicySource.ADES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(PredefinedValidationPolicySource.ADES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void validationReportShouldContainQESPolicyWhenQESPolicyIsGivenToValidator() throws Exception {
        Policy policy = validateWithPolicy(POL_V4).getValidationConclusion().getPolicy();
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getName(), policy.getPolicyName());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void whenNonExistingPolicyIsGivenThenValidatorShouldThrowException() throws Exception {
        expectedException.expect(InvalidPolicyException.class);
        validateWithPolicy("non-existing-policy").getValidationConclusion().getPolicy();
    }

    @Test
    @Ignore("fails because of DSS bug: https://esig-dss.atlassian.net/browse/DSS-915")
    public void WhenAllQualifiersAreSetInServiceInfoThenSignatureLevelShouldBeQESAndValidWithPOLv4() throws Exception {
        testWithAllQualifiersSet(POL_V4);
    }

    @Test
    @Ignore("fails because of DSS bug: https://esig-dss.atlassian.net/browse/DSS-915")
    public void WhenAllQualifiersAreSetInServiceInfoThenSignatureLevelShouldBeQESAndValidWithPOLv3() throws Exception {
        testWithAllQualifiersSet(POL_V3);
    }

    @Test
    @Ignore("fails because of DSS bug: https://esig-dss.atlassian.net/browse/DSS-915")
    public void whenQCWithQSCDQualifierIsNotSetThenSignatureLevelShouldBeAdesQCAndInvalidWithPOLv4() throws Exception {
        String policy = POL_V4;
        ServiceInfo serviceInfo = getServiceInfoForService(TEST_OF_KLASS3_SK_2010, policy);
        removeQcConditions(serviceInfo, QC_WITH_QSCD);
        assertServiceHasQualifiers(serviceInfo, QC_STATEMENT, QC_FOR_ESIG);
        SignatureValidationData signature = validateWithPolicy(policy, BDOC_TEST_OF_KLASS3_CHAIN).getValidationConclusion().getSignatures().get(0);
        assertEquals("TOTAL-FAILED", signature.getIndication());
        assertEquals("AdESQC", signature.getSignatureLevel());
    }

    @Test
    @Ignore("fails because of DSS bug: https://esig-dss.atlassian.net/browse/DSS-915")
    public void whenQCWithQSCDQualifierIsNotSetThenSignatureLevelShouldBeAdesQCAndValidWithPOLv3() throws Exception {
        String policy = POL_V3;
        ServiceInfo serviceInfo = getServiceInfoForService(TEST_OF_KLASS3_SK_2010, policy);
        removeQcConditions(serviceInfo, QC_WITH_QSCD);
        assertServiceHasQualifiers(serviceInfo, QC_STATEMENT, QC_FOR_ESIG);
        SignatureValidationData signature = validateWithPolicy(policy, BDOC_TEST_OF_KLASS3_CHAIN).getValidationConclusion().getSignatures().get(0);
        assertEquals("TOTAL-PASSED", signature.getIndication());
        assertEquals("AdESQC", signature.getSignatureLevel());
    }

    @Test
    @Ignore("Unknown reason")
    public void whenQCWithQSCDAndQCStatementQualifierIsNotSetThenSignatureLevelShouldBeAdesAndInvalidWithPOLv4() throws Exception {
        String policy = POL_V4;
        ServiceInfo serviceInfo = getServiceInfoForService(TEST_OF_KLASS3_SK_2010, policy);
        removeQcConditions(serviceInfo, QC_WITH_QSCD, QC_STATEMENT);
        assertServiceHasQualifiers(serviceInfo, QC_FOR_ESIG);
        SignatureValidationData signature = validateWithPolicy(policy, BDOC_TEST_OF_KLASS3_CHAIN).getValidationConclusion().getSignatures().get(0);
        assertEquals("TOTAL-FAILED", signature.getIndication());
        assertEquals("AdES", signature.getSignatureLevel());
    }

    @Ignore
    @Test
    public void whenQCWithQSCDAndQCStatementQualifierIsNotSetThenSignatureLevelShouldBeAdesAndValidWithPOLv3() throws Exception {
        String policy = POL_V3;
        ServiceInfo serviceInfo = getServiceInfoForService(TEST_OF_KLASS3_SK_2010, policy);
        removeQcConditions(serviceInfo, QC_WITH_QSCD, QC_STATEMENT);
        assertServiceHasQualifiers(serviceInfo, QC_FOR_ESIG);
        SignatureValidationData signature = validateWithPolicy(policy, BDOC_TEST_OF_KLASS3_CHAIN).getValidationConclusion().getSignatures().get(0);
        assertEquals("TOTAL-PASSED", signature.getIndication());
        assertEquals("ADESIG", signature.getSignatureLevel());
    }

    @Test
    public void onlySimpleReportPresentInDocumentValidationResultReports() {
        Reports reports = timemarkContainerValidationService.validateDocument(buildValidationDocument(BDOC_TEST_FILE_ALL_SIGNED));

        assertNotNull(reports.getSimpleReport().getValidationConclusion());
        assertNotNull(reports.getDetailedReport().getValidationConclusion());
        assertNotNull(reports.getDiagnosticReport().getValidationConclusion());

        assertNull(reports.getDetailedReport().getValidationProcess());
        assertNull(reports.getDiagnosticReport().getDiagnosticData());
    }

    @Test
    public void subjectDNPresentInAllReportTypesValidationConclusion() {
        Reports reports = timemarkContainerValidationService.validateDocument(buildValidationDocument(BDOC_TEST_FILE_ALL_SIGNED));

        String expectedSerialNumber = "47711040261";
        String expectedCommonName = "SOLOVEI,JULIA,47711040261";
        assertSubjectDNPresent(reports.getSimpleReport().getValidationConclusion().getSignatures().get(0), expectedSerialNumber, expectedCommonName);
        assertSubjectDNPresent(reports.getDetailedReport().getValidationConclusion().getSignatures().get(0), expectedSerialNumber, expectedCommonName);
        assertSubjectDNPresent(reports.getDiagnosticReport().getValidationConclusion().getSignatures().get(0), expectedSerialNumber, expectedCommonName);
    }

    private void assertSubjectDNPresent(SignatureValidationData signature, String serialNumber, String commonName) {
        SubjectDistinguishedName subjectDistinguishedName = signature.getSubjectDistinguishedName();
        assertNotNull(subjectDistinguishedName);
        assertEquals(serialNumber, subjectDistinguishedName.getSerialNumber());
        assertEquals(commonName, subjectDistinguishedName.getCommonName());
    }

    private void testWithAllQualifiersSet(String policy) throws Exception {
        ServiceInfo serviceInfo = getServiceInfoForService(TEST_OF_KLASS3_SK_2010, policy);
        assertServiceHasQualifiers(serviceInfo, QC_WITH_QSCD, QC_STATEMENT, QC_FOR_ESIG);
        SignatureValidationData signature = validateWithPolicy(policy, BDOC_TEST_OF_KLASS3_CHAIN).getValidationConclusion().getSignatures().get(0);
        assertEquals("TOTAL-PASSED", signature.getIndication());
        assertEquals("QES", signature.getSignatureLevel());
    }

    private ServiceInfo getServiceInfoForService(String serviceName, String policy) {
        TSLCertificateSource tslCertificateSource = configurationService.loadPolicyConfiguration(policy).getConfiguration().getTSL();
        return tslCertificateSource.getCertificatePool().getCertificateTokens()
                .stream()
                .map(certificateToken -> getServiceInfo(tslCertificateSource, certificateToken))
                .filter(si -> serviceMatchesServiceName(si, serviceName))
                .findFirst().get();
    }

    private void assertServiceHasQualifiers(ServiceInfo serviceInfo, String... qualifiers) {
        Set<String> existingQualifiers = serviceInfo.getStatus().getLatest().getQualifiersAndConditions().keySet();
        assertEquals(qualifiers.length, existingQualifiers.size());
        Arrays.stream(qualifiers).forEach(qualifier -> assertTrue(existingQualifiers.contains(qualifier)));

    }

    private ServiceInfo getServiceInfo(TSLCertificateSource tslCertificateSource, CertificateToken certificateToken) {
        return (ServiceInfo) tslCertificateSource.getTrustServices(certificateToken).toArray()[0];
    }

    private boolean serviceMatchesServiceName(ServiceInfo serviceInfo, String serviceName) {
        return StringUtils.contains(serviceInfo.getTspName(), serviceName);
    }

    private void removeQcConditions(ServiceInfo serviceInfo, String... qualifiers) {
        Map<String, List<Condition>> qualifiersAndConditions = serviceInfo.getStatus().getLatest().getQualifiersAndConditions();
        Arrays.stream(qualifiers).forEach(qualifiersAndConditions::remove);
    }

    private SimpleReport validateWithPolicy(String policyName) throws Exception {
        return validateWithPolicy(policyName, VALID_BDOC_TM_2_SIGNATURES);
    }

    private SimpleReport validateWithPolicy(String policyName, String file) throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(file);
        validationDocument.setSignaturePolicy(policyName);
        return timemarkContainerValidationService.validateDocument(validationDocument).getSimpleReport();
    }

    private ValidationDocument bdocValid2Signatures() throws Exception {
        return buildValidationDocument(VALID_BDOC_TM_2_SIGNATURES);
    }

    private ValidationDocument bdocValidIdCardAndMobIdSignatures() throws Exception {
        return buildValidationDocument(VALID_ID_CARD_MOB_ID);
    }

    private ValidationDocument bdocCRLRevocationOnly() throws Exception {
        return buildValidationDocument(ASICE_CRL_ONLY);
    }
}
