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

package ee.openeid.validation.service.timemark;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.ArchiveTimeStamp;
import ee.openeid.siva.validation.document.report.CertificateType;
import ee.openeid.siva.validation.document.report.Policy;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.Scope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.SubjectDistinguishedName;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.ValidationWarning;
import ee.openeid.siva.validation.document.report.Warning;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource;
import ee.openeid.siva.validation.util.CertUtil;
import ee.openeid.tsl.TSLRefresher;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.tsl.configuration.TSLLoaderDefinitionRegisterer;
import ee.openeid.validation.service.timemark.configuration.TimemarkContainerValidationServiceConfiguration;
import ee.openeid.validation.service.timemark.signature.policy.BDOCConfigurationService;
import ee.openeid.validation.service.timemark.signature.policy.BDOCSignaturePolicyService;
import ee.openeid.validation.service.timemark.tsl.TimemarkTrustedListsCertificateSource;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.tsl.ConditionForQualifiers;
import eu.europa.esig.dss.spi.tsl.TrustProperties;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.digidoc4j.TSLCertificateSource;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ee.openeid.validation.service.timemark.BDOCTestUtils.ASICE_CRL_ONLY;
import static ee.openeid.validation.service.timemark.BDOCTestUtils.ASICE_TEST_FILE_LTA_LEVEL_SIGNATURE;
import static ee.openeid.validation.service.timemark.BDOCTestUtils.BDOC_TEST_FILE_ALL_SIGNED;
import static ee.openeid.validation.service.timemark.BDOCTestUtils.BDOC_TEST_FILE_LTA_LEVEL_SIGNATURE;
import static ee.openeid.validation.service.timemark.BDOCTestUtils.BDOC_TEST_FILE_T_LEVEL_SIGNATURE;
import static ee.openeid.validation.service.timemark.BDOCTestUtils.BDOC_TEST_FILE_UNSIGNED;
import static ee.openeid.validation.service.timemark.BDOCTestUtils.BDOC_TEST_OF_KLASS3_CHAIN;
import static ee.openeid.validation.service.timemark.BDOCTestUtils.VALID_ASICE;
import static ee.openeid.validation.service.timemark.BDOCTestUtils.VALID_BALTIC_EST_LT;
import static ee.openeid.validation.service.timemark.BDOCTestUtils.VALID_BDOC_TM_2_SIGNATURES;
import static ee.openeid.validation.service.timemark.BDOCTestUtils.VALID_ID_CARD_MOB_ID;
import static ee.openeid.validation.service.timemark.BDOCTestUtils.buildValidationDocument;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        TSLLoaderConfiguration.class,
        TSLLoaderDefinitionRegisterer.class,
        TSLRefresher.class,
        TSLValidationJobFactory.class,
        TimemarkTrustedListsCertificateSource.class,
        TimemarkContainerValidationServiceConfiguration.class,
        TimemarkContainerValidationService.class,
        BDOCSignaturePolicyService.class,
        ConstraintLoadingSignaturePolicyService.class,
        BDOCConfigurationService.class,
        ReportConfigurationProperties.class,
        ProxyConfig.class
})
@ActiveProfiles("test")
class TimemarkContainerValidationServiceIntegrationTest {

    private static final String QC_WITH_QSCD = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCWithQSCD";
    private static final String QC_STATEMENT = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCStatement";
    private static final String QC_FOR_ESIG = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCForESig";
    private static final String TEST_OF_KLASS3_SK_2010 = "TEST of KLASS3-SK 2010";
    private static final String POL_V3 = "POLv3";
    private static final String POL_V4 = "POLv4";

    private static final String DOCUMENT_MALFORMED_MESSAGE = "Document malformed or not matching documentType";
    @Autowired
    private TimemarkContainerValidationService timemarkContainerValidationService;
    @Autowired
    private BDOCConfigurationService configurationService;
    @Autowired
    private ReportConfigurationProperties reportConfigurationProperties;
    @Autowired
    private TimemarkContainerValidationReportBuilderFactory timemarkContainerValidationReportBuilderFactory;


    @Test
    void validationReportShouldHaveSignatureWarnings() {
        SimpleReport validationResult = timemarkContainerValidationService.validateDocument(buildValidationDocument(BDOC_TEST_FILE_UNSIGNED)).getSimpleReport();
        List<Warning> signatureValidationData = validationResult.getValidationConclusion().getSignatures().get(0).getWarnings();
        assertThat(signatureValidationData, hasSize(1));
        assertThat(signatureValidationData, containsInAnyOrder(
                hasProperty("content", is("The signature/seal is not a valid AdES digital signature!"))
        ));
    }

    @Test
    void validationReportShouldNotHaveValidationWarnings() {
        SimpleReport validationResult = timemarkContainerValidationService.validateDocument(buildValidationDocument(BDOC_TEST_FILE_ALL_SIGNED)).getSimpleReport();
        List<ValidationWarning> validationWarnings = validationResult.getValidationConclusion().getValidationWarnings();
        assertThat(validationWarnings, hasSize(0));
    }

    @Test
    void verifyCorrectPolicyIsLoadedToD4JConfiguration() {
        System.out.println(configurationService.loadPolicyConfiguration(null).getConfiguration().getValidationPolicy());

        System.out.println(configurationService.loadPolicyConfiguration(POL_V4).getConfiguration().getValidationPolicy());
        System.out.println(configurationService.loadPolicyConfiguration(POL_V3).getConfiguration().getValidationPolicy());
        assertTrue(configurationService.loadPolicyConfiguration(null).getConfiguration().getValidationPolicy().contains("siva-bdoc-POLv4-constraint"));
        assertTrue(configurationService.loadPolicyConfiguration(POL_V4).getConfiguration().getValidationPolicy().contains("siva-bdoc-POLv4-constraint"));
        assertTrue(configurationService.loadPolicyConfiguration(POL_V3).getConfiguration().getValidationPolicy().contains("siva-bdoc-POLv3-constraint"));
    }

    @Test
    void validatingABDOCWithMalformedBytesResultsInMalformedDocumentException() {
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setBytes("Hello".getBytes());
        MalformedDocumentException caughtException = assertThrows(
                MalformedDocumentException.class, () -> timemarkContainerValidationService.validateDocument(validationDocument)
        );
        assertEquals(DOCUMENT_MALFORMED_MESSAGE, caughtException.getMessage());
    }

    @Test
    void bdocValidationResultShouldIncludeValidationReportPOJO() {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(bdocValid2Signatures()).getSimpleReport();
        assertNotNull(validationResult2Signatures);
    }

    @Test
    void validationReportShouldIncludeRequiredFields() {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(bdocValid2Signatures()).getSimpleReport();
        ValidationConclusion validationConclusion = validationResult2Signatures.getValidationConclusion();
        assertNotNull(validationConclusion.getPolicy());
        assertNotNull(validationConclusion.getValidationTime());
        assertEquals(VALID_BDOC_TM_2_SIGNATURES, validationConclusion.getValidatedDocument().getFilename());
        assertThat(validationConclusion.getSignatures(), hasSize(2));
        assertEquals(2, validationConclusion.getValidSignaturesCount());
        assertEquals(2, validationConclusion.getSignaturesCount());
    }

    @Test
    void signatureScopeShouldBeCorrectWhenDatafilesContainSpacesOrParenthesis() {
        SimpleReport report = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_ID_CARD_MOB_ID)).getSimpleReport();
        report.getValidationConclusion().getSignatures().forEach(sig -> assertContainsScope(sig, "Proov (2).txt"));
        SimpleReport report2 = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_BALTIC_EST_LT)).getSimpleReport();
        report2.getValidationConclusion().getSignatures().forEach(sig -> assertContainsScope(sig, "Baltic MoU digital signing_04112015.docx"));
    }

    private void assertContainsScope(SignatureValidationData signature, String filename) {
        assertThat(signature.getSignatureScopes()
                .stream()
                .map(Scope::getName)
                .filter(name -> StringUtils.equals(filename, name))
                .count(), greaterThan(0L));
    }

    @Test
    void validationReportShouldHaveCorrectSignatureValidationDataForSignature1() {

        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(bdocValid2Signatures()).getSimpleReport();
        SignatureValidationData sig1 = validationResult2Signatures.getValidationConclusion().getSignatures()
                .stream()
                .filter(sig -> sig.getId().equals("id-7b7180a5265f919bc0ac65bd02e4b46a"))
                .findFirst()
                .orElseThrow();

        assertEquals("XAdES_BASELINE_LT_TM", sig1.getSignatureFormat());
        assertEquals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256", sig1.getSignatureMethod());
        assertEquals("QESIG", sig1.getSignatureLevel());
        assertEquals("JÕEORG,JAAK-KRISTJAN,38001085718", sig1.getSignedBy());
        assertEquals("JÕEORG,JAAK-KRISTJAN,38001085718", sig1.getSubjectDistinguishedName().getCommonName());
        assertEquals("PNOEE-38001085718", sig1.getSubjectDistinguishedName().getSerialNumber());
        assertEquals(SignatureValidationData.Indication.TOTAL_PASSED.toString(), sig1.getIndication());
        assertThat(sig1.getSubIndication(), emptyOrNullString());
        assertThat(sig1.getErrors(), empty());
        assertThat(sig1.getWarnings(), empty());
        assertThat(sig1.getSignatureScopes(), hasSize(1));
        Scope scope = sig1.getSignatureScopes().get(0);
        assertEquals("test.txt", scope.getName());
        assertEquals("Digest of the document content", scope.getContent());
        assertEquals("FullSignatureScope", scope.getScope());
        assertEquals("2020-05-21T14:07:04Z", sig1.getClaimedSigningTime());
        assertEquals("2020-05-21T14:07:01Z", sig1.getInfo().getBestSignatureTime());
        assertEquals("MDEwDQYJYIZIAWUDBAIBBQAEIGKrO2Grf+WLkmOnj9QQbCXAa2A3881D9PUIOk0M7Nm6", sig1.getInfo().getTimeAssertionMessageImprint());
        assertTrue(sig1.getInfo().getSignerRole().isEmpty());
        assertNull(sig1.getInfo().getSignatureProductionPlace());
    }

    @Test
    void validationReportShouldHaveCorrectSignatureValidationDataForSignature2() {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(bdocValid2Signatures()).getSimpleReport();
        SignatureValidationData sig2 = validationResult2Signatures.getValidationConclusion().getSignatures()
                .stream()
                .filter(sig -> sig.getId().equals("id-c71904f656e45af0c9b0ce644fc9287d"))
                .findFirst()
                .orElseThrow();

        assertEquals("XAdES_BASELINE_LT_TM", sig2.getSignatureFormat());
        assertEquals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256", sig2.getSignatureMethod());
        assertEquals("QESIG", sig2.getSignatureLevel());
        assertEquals("MÄNNIK,MARI-LIIS,47101010033", sig2.getSignedBy());
        assertEquals("MÄNNIK,MARI-LIIS,47101010033", sig2.getSubjectDistinguishedName().getCommonName());
        assertEquals("47101010033", sig2.getSubjectDistinguishedName().getSerialNumber());
        assertEquals(SignatureValidationData.Indication.TOTAL_PASSED.toString(), sig2.getIndication());
        assertThat(sig2.getSubIndication(), emptyOrNullString());
        assertThat(sig2.getErrors(), empty());
        assertThat(sig2.getWarnings(), empty());
        assertThat(sig2.getSignatureScopes(), hasSize(1));
        Scope scope = sig2.getSignatureScopes().get(0);
        assertEquals("test.txt", scope.getName());
        assertEquals("Digest of the document content", scope.getContent());
        assertEquals("FullSignatureScope", scope.getScope());
        assertEquals("2020-05-28T10:59:12Z", sig2.getClaimedSigningTime());
        assertEquals("2020-05-28T10:59:14Z", sig2.getInfo().getBestSignatureTime());
        assertEquals("MDEwDQYJYIZIAWUDBAIBBQAEIDDnPj4HDgSwi+tj/s30GshbBf1L8Nqnt2GMK+6VnEdt", sig2.getInfo().getTimeAssertionMessageImprint());
        assertEquals(1, sig2.getInfo().getSignerRole().size());
        assertEquals("Signing as king of signers", sig2.getInfo().getSignerRole().get(0).getClaimedRole());
        assertEquals("Tallinn", sig2.getInfo().getSignatureProductionPlace().getCity());
        assertEquals("Harju", sig2.getInfo().getSignatureProductionPlace().getStateOrProvince());
        assertEquals("Elbonia", sig2.getInfo().getSignatureProductionPlace().getCountryName());
        assertEquals("32323", sig2.getInfo().getSignatureProductionPlace().getPostalCode());
    }

    @Test
    void reportForBdocValidationShouldIncludeCorrectAsiceSignatureForm() {
        SimpleReport report = timemarkContainerValidationService.validateDocument(bdocValid2Signatures()).getSimpleReport();
        assertEquals("ASiC-E", report.getValidationConclusion().getSignatureForm());
    }

    @Test
    void bestSignatureTimeInQualifiedBdocReportShouldNotBeBlank() {
        SimpleReport report = timemarkContainerValidationService.validateDocument(bdocValidIdCardAndMobIdSignatures()).getSimpleReport();
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        String bestSignatureTime1 = validationConclusion.getSignatures().get(0).getInfo().getBestSignatureTime();
        String bestSignatureTime2 = validationConclusion.getSignatures().get(1).getInfo().getBestSignatureTime();
        assertTrue(StringUtils.isNotBlank(bestSignatureTime1));
        assertTrue(StringUtils.isNotBlank(bestSignatureTime2));
    }

    @Test
    void bdocWithCRLRevocationDataOnlyShouldFail() {
        SimpleReport report = timemarkContainerValidationService.validateDocument(bdocCRLRevocationOnly()).getSimpleReport();
        assertEquals(0, report.getValidationConclusion().getValidSignaturesCount());
    }

    @Test
    void validationReportShouldContainDefaultPolicyWhenPolicyIsNotExplicitlyGiven() {
        Policy policy = validateWithPolicy("").getValidationConclusion().getPolicy();
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getName(), policy.getPolicyName());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    void validationReportShouldContainAdesPolicyWhenAdesPolicyIsGivenToValidator() {
        Policy policy = validateWithPolicy(POL_V3).getValidationConclusion().getPolicy();
        assertEquals(PredefinedValidationPolicySource.ADES_POLICY.getName(), policy.getPolicyName());
        assertEquals(PredefinedValidationPolicySource.ADES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(PredefinedValidationPolicySource.ADES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    void validationReportShouldContainQESPolicyWhenQESPolicyIsGivenToValidator() {
        Policy policy = validateWithPolicy(POL_V4).getValidationConclusion().getPolicy();
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getName(), policy.getPolicyName());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    void whenNonExistingPolicyIsGivenThenValidatorShouldThrowException() {
        assertThrows(
                InvalidPolicyException.class, () -> validateWithPolicy("non-existing-policy")
        );
    }

    @Test
    @Disabled("fails because of DSS bug: https://esig-dss.atlassian.net/browse/DSS-915")
    void WhenAllQualifiersAreSetInServiceInfoThenSignatureLevelShouldBeQESAndValidWithPOLv4() {
        testWithAllQualifiersSet(POL_V4);
    }

    @Test
    @Disabled("fails because of DSS bug: https://esig-dss.atlassian.net/browse/DSS-915")
    void WhenAllQualifiersAreSetInServiceInfoThenSignatureLevelShouldBeQESAndValidWithPOLv3() {
        testWithAllQualifiersSet(POL_V3);
    }

    @Test
    @Disabled("fails because of DSS bug: https://esig-dss.atlassian.net/browse/DSS-915")
    void whenQCWithQSCDQualifierIsNotSetThenSignatureLevelShouldBeAdesQCAndInvalidWithPOLv4() {
        String policy = POL_V4;
        TrustProperties trustProperties = getServiceInfoForService(TEST_OF_KLASS3_SK_2010, policy);
        removeQcConditions(trustProperties, QC_WITH_QSCD);
        assertServiceHasQualifiers(trustProperties, QC_STATEMENT, QC_FOR_ESIG);
        SignatureValidationData signature = validateWithPolicy(policy, BDOC_TEST_OF_KLASS3_CHAIN).getValidationConclusion().getSignatures().get(0);
        assertEquals("TOTAL-FAILED", signature.getIndication());
        assertEquals("AdESQC", signature.getSignatureLevel());
    }

    @Test
    @Disabled("fails because of DSS bug: https://esig-dss.atlassian.net/browse/DSS-915")
    void whenQCWithQSCDQualifierIsNotSetThenSignatureLevelShouldBeAdesQCAndValidWithPOLv3() {
        String policy = POL_V3;
        TrustProperties trustProperties = getServiceInfoForService(TEST_OF_KLASS3_SK_2010, policy);
        removeQcConditions(trustProperties, QC_WITH_QSCD);
        assertServiceHasQualifiers(trustProperties, QC_STATEMENT, QC_FOR_ESIG);
        SignatureValidationData signature = validateWithPolicy(policy, BDOC_TEST_OF_KLASS3_CHAIN).getValidationConclusion().getSignatures().get(0);
        assertEquals("TOTAL-PASSED", signature.getIndication());
        assertEquals("AdESQC", signature.getSignatureLevel());
    }

    @Test
    @Disabled("fails because of DSS bug: https://esig-dss.atlassian.net/browse/DSS-915")
    void whenQCWithQSCDAndQCStatementQualifierIsNotSetThenSignatureLevelShouldBeAdesAndInvalidWithPOLv4() {
        String policy = POL_V4;
        TrustProperties trustProperties = getServiceInfoForService(TEST_OF_KLASS3_SK_2010, policy);
        removeQcConditions(trustProperties, QC_WITH_QSCD, QC_STATEMENT);
        assertServiceHasQualifiers(trustProperties, QC_FOR_ESIG);
        SignatureValidationData signature = validateWithPolicy(policy, BDOC_TEST_OF_KLASS3_CHAIN).getValidationConclusion().getSignatures().get(0);
        assertEquals("TOTAL-FAILED", signature.getIndication());
        assertEquals("AdES", signature.getSignatureLevel());
    }

    @Disabled("fails because of DSS bug: https://esig-dss.atlassian.net/browse/DSS-915")
    @Test
    void whenQCWithQSCDAndQCStatementQualifierIsNotSetThenSignatureLevelShouldBeAdesAndValidWithPOLv3() {
        String policy = POL_V3;
        TrustProperties trustProperties = getServiceInfoForService(TEST_OF_KLASS3_SK_2010, policy);
        removeQcConditions(trustProperties, QC_WITH_QSCD, QC_STATEMENT);
        assertServiceHasQualifiers(trustProperties, QC_FOR_ESIG);
        SignatureValidationData signature = validateWithPolicy(policy, BDOC_TEST_OF_KLASS3_CHAIN).getValidationConclusion().getSignatures().get(0);
        assertEquals("TOTAL-PASSED", signature.getIndication());
        assertEquals("ADESIG", signature.getSignatureLevel());
    }

    @Test
    void onlySimpleReportPresentInDocumentValidationResultReports() {
        Reports reports = timemarkContainerValidationService.validateDocument(buildValidationDocument(BDOC_TEST_FILE_ALL_SIGNED));

        assertNotNull(reports.getSimpleReport().getValidationConclusion());
        assertNotNull(reports.getDetailedReport().getValidationConclusion());
        assertNotNull(reports.getDiagnosticReport().getValidationConclusion());

        assertNull(reports.getDetailedReport().getValidationProcess());
        assertNull(reports.getDiagnosticReport().getDiagnosticData());
    }

    @Test
    void subjectDNPresentInAllReportTypesValidationConclusion() {
        Reports reports = timemarkContainerValidationService.validateDocument(buildValidationDocument(BDOC_TEST_FILE_ALL_SIGNED));

        String expectedSerialNumber = "47711040261";
        String expectedCommonName = "SOLOVEI,JULIA,47711040261";
        String givenName = "JULIA";
        String surname = "SOLOVEI";
        assertSubjectDNPresent(reports.getSimpleReport().getValidationConclusion().getSignatures().get(0), expectedSerialNumber, expectedCommonName, givenName, surname);
        assertSubjectDNPresent(reports.getDetailedReport().getValidationConclusion().getSignatures().get(0), expectedSerialNumber, expectedCommonName, givenName, surname);
        assertSubjectDNPresent(reports.getDiagnosticReport().getValidationConclusion().getSignatures().get(0), expectedSerialNumber, expectedCommonName, givenName, surname);
    }

    @Test
    void timeAssertionMessageImprintIsNotEmptyForLT() {
        Reports reports = timemarkContainerValidationService.validateDocument(buildValidationDocument("LT_without_nonce.bdoc"));

        SignatureValidationData signatureValidationData = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0);

        assertEquals("MDEwDQYJYIZIAWUDBAIBBQAEIE541TO5ZHHgKv60XxTXJX0Qg04pjs4uN8bELnDUDFp1", signatureValidationData.getInfo().getTimeAssertionMessageImprint());
    }

    @Test
    void timestampAndRevocationTimeExistsInLT(){
        Reports reports = timemarkContainerValidationService.validateDocument(buildValidationDocument("LT_without_nonce.bdoc"));
        SignatureValidationData signatureValidationData = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0);
        assertEquals("2020-06-04T11:34:54Z", signatureValidationData.getInfo().getOcspResponseCreationTime());
        assertEquals("2020-06-04T11:34:53Z", signatureValidationData.getInfo().getTimestampCreationTime());
    }

    @Test
    void timestampTimeMissingLT_TM(){
        Reports reports = timemarkContainerValidationService.validateDocument(buildValidationDocument("bdoc_tm_valid_2_signatures.bdoc"));
        SignatureValidationData signatureValidationData = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0);
        assertEquals("2020-05-21T14:07:01Z", signatureValidationData.getInfo().getOcspResponseCreationTime());
        assertNull(signatureValidationData.getInfo().getTimestampCreationTime());
        SignatureValidationData signatureValidationData2 = reports.getSimpleReport().getValidationConclusion().getSignatures().get(1);
        assertEquals("2020-05-28T10:59:14Z", signatureValidationData2.getInfo().getOcspResponseCreationTime());
        assertNull(signatureValidationData2.getInfo().getTimestampCreationTime());
    }

    @Test
    void certificatePresentLT_TM() throws Exception {
        Reports reports = timemarkContainerValidationService.validateDocument(buildValidationDocument(BDOC_TEST_FILE_ALL_SIGNED));
        SignatureValidationData signatureValidationData = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        assertEquals(2, signatureValidationData.getCertificates().size());

        ee.openeid.siva.validation.document.report.Certificate signerCertificate = signatureValidationData.getCertificatesByType(CertificateType.SIGNING).get(0);
        Certificate signerX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(signerCertificate.getContent().getBytes())));
        assertEquals("SOLOVEI,JULIA,47711040261", CertUtil.getCommonName((X509Certificate) signerX509Certificate));
        assertEquals("SOLOVEI,JULIA,47711040261", signerCertificate.getCommonName());

        ee.openeid.siva.validation.document.report.Certificate revocationCertificate = signatureValidationData.getCertificatesByType(CertificateType.REVOCATION).get(0);
        Certificate revocationX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(revocationCertificate.getContent().getBytes())));
        assertEquals("SK OCSP RESPONDER 2011", CertUtil.getCommonName((X509Certificate) revocationX509Certificate));
        assertEquals("SK OCSP RESPONDER 2011", revocationCertificate.getCommonName());

        assertNull(reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getArchiveTimeStamps());
    }

    @Test
    void certificatePresentLT() throws Exception {
        Reports reports = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_ASICE));
        SignatureValidationData signatureValidationData = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        assertEquals(3, signatureValidationData.getCertificates().size());

        ee.openeid.siva.validation.document.report.Certificate signerCertificate = signatureValidationData.getCertificatesByType(CertificateType.SIGNING).get(0);
        Certificate signerX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(signerCertificate.getContent().getBytes())));
        assertEquals("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865", CertUtil.getCommonName((X509Certificate) signerX509Certificate));
        assertEquals("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865", signerCertificate.getCommonName());

        ee.openeid.siva.validation.document.report.Certificate revocationCertificate = signatureValidationData.getCertificatesByType(CertificateType.REVOCATION).get(0);
        Certificate revocationX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(revocationCertificate.getContent().getBytes())));
        assertEquals("TEST of SK OCSP RESPONDER 2011", CertUtil.getCommonName((X509Certificate) revocationX509Certificate));
        assertEquals("TEST of SK OCSP RESPONDER 2011", revocationCertificate.getCommonName());

        ee.openeid.siva.validation.document.report.Certificate timestampCertificate = signatureValidationData.getCertificatesByType(CertificateType.SIGNATURE_TIMESTAMP).get(0);
        Certificate timestampX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(timestampCertificate.getContent().getBytes())));
        assertEquals("DEMO of SK TSA 2014", CertUtil.getCommonName((X509Certificate) timestampX509Certificate));
        assertEquals("DEMO of SK TSA 2014", timestampCertificate.getCommonName());
    }

    @Test
    void validateDocument_ProfileLevelIsT_TimestampCreationTimeIsPresent() {
        SimpleReport validationResult = timemarkContainerValidationService.validateDocument(buildValidationDocument(BDOC_TEST_FILE_T_LEVEL_SIGNATURE)).getSimpleReport();

        String tsCreationTime = validationResult.getValidationConclusion().getSignatures().get(0).getInfo().getTimestampCreationTime();
        assertThat(tsCreationTime, notNullValue());
        assertThat(tsCreationTime, equalTo("2014-05-19T10:45:19Z"));
    }

    @Test
    void validateDocument_ProfileLevelIsLTA_TimestampCreationTimeIsPresent() {
        SimpleReport validationResult = timemarkContainerValidationService.validateDocument(buildValidationDocument(ASICE_TEST_FILE_LTA_LEVEL_SIGNATURE)).getSimpleReport();

        String tsCreationTime = validationResult.getValidationConclusion().getSignatures().get(0).getInfo().getTimestampCreationTime();
        assertThat(tsCreationTime, notNullValue());
        assertThat(tsCreationTime, equalTo("2018-11-23T12:24:04Z"));
    }

    @Test
    void validateAsiceContainer_ProfileLevelIsLTA_ArchiveTimestampsBlockIsPresent() {
        Reports reports = timemarkContainerValidationService.validateDocument(buildValidationDocument(ASICE_TEST_FILE_LTA_LEVEL_SIGNATURE));
        ValidationConclusion validationConclusion = reports.getSimpleReport().getValidationConclusion();

        assertThat(validationConclusion.getSignatures().get(0).getInfo().getArchiveTimeStamps(), CoreMatchers.notNullValue());
        assertThat(validationConclusion.getSignatures().get(0).getInfo().getArchiveTimeStamps(), hasSize(1));

        {
            ArchiveTimeStamp archiveTimeStamp = validationConclusion.getSignatures().get(0).getInfo().getArchiveTimeStamps().get(0);
            assertThat(archiveTimeStamp.getSignedTime(), equalTo("2024-03-14T11:22:54Z"));
            assertThat(archiveTimeStamp.getIndication(), sameInstance(Indication.PASSED));
            assertThat(archiveTimeStamp.getSubIndication(), nullValue());
            assertThat(archiveTimeStamp.getSignedBy(), equalTo("DEMO SK TIMESTAMPING AUTHORITY 2023E"));
            assertThat(archiveTimeStamp.getCountry(), equalTo("EE"));
            assertThat(archiveTimeStamp.getContent(), equalTo(loadTimestampContent("timestamp_content.txt")));
        }
    }

    @ParameterizedTest
    @MethodSource("timestampProvider")
    void validateBdocContainer_ProfileLevelIsLTA_ArchiveTimestampsBlockIsPresentForMultipleTimestamps(String signedTime, String expectedContent, int iterator) {
        Reports reports = timemarkContainerValidationService.validateDocument(buildValidationDocument(BDOC_TEST_FILE_LTA_LEVEL_SIGNATURE));
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

    private void assertSubjectDNPresent(SignatureValidationData signature, String serialNumber, String
            commonName, String givenName, String surname) {
        SubjectDistinguishedName subjectDistinguishedName = signature.getSubjectDistinguishedName();
        assertNotNull(subjectDistinguishedName);
        assertEquals(serialNumber, subjectDistinguishedName.getSerialNumber());
        assertEquals(commonName, subjectDistinguishedName.getCommonName());
        assertEquals(givenName, subjectDistinguishedName.getGivenName());
        assertEquals(surname, subjectDistinguishedName.getSurname());
    }

    private void testWithAllQualifiersSet(String policy) {
        TrustProperties trustProperties = getServiceInfoForService(TEST_OF_KLASS3_SK_2010, policy);
        assertServiceHasQualifiers(trustProperties, QC_WITH_QSCD, QC_STATEMENT, QC_FOR_ESIG);
        SignatureValidationData signature = validateWithPolicy(policy, BDOC_TEST_OF_KLASS3_CHAIN).getValidationConclusion().getSignatures().get(0);
        assertEquals("TOTAL-PASSED", signature.getIndication());
        assertEquals("QES", signature.getSignatureLevel());
    }

    private TrustProperties getServiceInfoForService(String serviceName, String policy) {
        TSLCertificateSource tslCertificateSource = configurationService.loadPolicyConfiguration(policy).getConfiguration().getTSL();
        return tslCertificateSource.getCertificates()
                .stream()
                .map(certificateToken -> getTrustProperties(tslCertificateSource, certificateToken))
                .filter(si -> serviceMatchesServiceName(si, serviceName))
                .findFirst().orElseThrow();
    }

    private void assertServiceHasQualifiers(TrustProperties trustProperties, String... qualifiers) {
        List<ConditionForQualifiers> qualifiersAndConditions = trustProperties.getTrustService().getLatest().getConditionsForQualifiers();
        assertEquals(qualifiers.length, qualifiersAndConditions.size());

        Arrays.stream(qualifiers)
                .forEach(qualifier -> qualifiersAndConditions
                        .forEach(q -> assertTrue(q.getQualifiers().contains(qualifier))));
    }

    private TrustProperties getTrustProperties(TSLCertificateSource tslCertificateSource, CertificateToken
            certificateToken) {
        return (TrustProperties) tslCertificateSource.getTrustServices(certificateToken).toArray()[0];
    }

    private boolean serviceMatchesServiceName(TrustProperties trustProperties, String serviceName) {
        return trustProperties.getTrustService().getLatest().getNames().containsKey(serviceName);
    }

    private void removeQcConditions(TrustProperties trustProperties, String... qualifiers) {
        List<ConditionForQualifiers> qualifiersAndConditions = trustProperties.getTrustService().getLatest().getConditionsForQualifiers();
        Arrays.stream(qualifiers)
                .forEach(qualifier -> qualifiersAndConditions
                        .forEach(q -> q.getQualifiers().remove(qualifier)));


        qualifiersAndConditions.stream().filter(i -> i.getQualifiers().contains(qualifiers)).forEach(qualifiersAndConditions::remove);
    }

    private SimpleReport validateWithPolicy(String policyName) {
        return validateWithPolicy(policyName, VALID_BDOC_TM_2_SIGNATURES);
    }

    private SimpleReport validateWithPolicy(String policyName, String file) {
        ValidationDocument validationDocument = buildValidationDocument(file);
        validationDocument.setSignaturePolicy(policyName);
        return timemarkContainerValidationService.validateDocument(validationDocument).getSimpleReport();
    }

    private ValidationDocument bdocValid2Signatures() {
        return buildValidationDocument(VALID_BDOC_TM_2_SIGNATURES);
    }

    private ValidationDocument bdocValidIdCardAndMobIdSignatures() {
        return buildValidationDocument(VALID_ID_CARD_MOB_ID);
    }

    private ValidationDocument bdocCRLRevocationOnly() {
        return buildValidationDocument(ASICE_CRL_ONLY);
    }
}
