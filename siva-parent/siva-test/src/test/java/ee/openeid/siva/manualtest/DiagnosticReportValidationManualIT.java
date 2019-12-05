/*
 * Copyright 2018 Riigi Infosüsteemide Amet
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
package ee.openeid.siva.manualtest;

import ee.openeid.siva.common.DateTimeMatcher;
import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.signature.configuration.SignatureServiceConfigurationProperties;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static ee.openeid.siva.integrationtest.TestData.*;
import static org.hamcrest.Matchers.*;

@Category(IntegrationTest.class)

public class DiagnosticReportValidationManualIT extends SiVaRestTests {
    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signature_cryptographic_algorithm_test_files/";
    private static final String VALIDATION_ENDPOINT = "/validate";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    private Response response;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    @Autowired
    private SignatureServiceConfigurationProperties signatureServiceConfigurationProperties;

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: Diagnostic-Report-Validation-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: ValidationConclusion element
     *
     * Expected Result: Diagnostic report includes validationConclusion element
     *
     * File: ValidLiveSignature.asice
     */
    @Test
    public void diagnosticReportAssertValidValidationConclusionAsicE() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        ZonedDateTime testStartDate = ZonedDateTime.now(ZoneId.of("GMT"));

        post(validationRequestFor("ValidLiveSignature.asice", null, REPORT_TYPE_DIAGNOSTIC ))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("policy.policyDescription", equalTo(POLICY_4_DESCRIPTION))
                .body("policy.policyName", equalTo(SIGNATURE_POLICY_2))
                .body("policy.policyUrl", equalTo(POLICY_4_URL))
                .body("signatureForm", equalTo(SIGNATURE_FORM_ASICE))
                .body("validationTime", DateTimeMatcher.isEqualOrAfter(testStartDate))
                .body("signaturesCount", equalTo(1))
                .body("validSignaturesCount", equalTo(1))
                .body("signatures", notNullValue())
                .body("signatures[0].id", equalTo("S0"))
                .body("signatures[0].signatureFormat", equalTo(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].signatureLevel", equalTo(SIGNATURE_LEVEL_QESIG))
                .body("signatures[0].signedBy", equalTo("NURM,AARE,38211015222"))
                .body("signatures[0].indication", equalTo(TOTAL_PASSED))
                .body("signatures[0].signatureScopes[0].name", equalTo("Tresting.txt"))
                .body("signatures[0].signatureScopes[0].scope", equalTo(SIGNATURE_SCOPE_FULL))
                .body("signatures[0].signatureScopes[0].content", equalTo(VALID_SIGNATURE_SCOPE_CONTENT_FULL))
                .body("signatures[0].claimedSigningTime", equalTo("2016-10-11T09:35:48Z"))
                .body("signatures[0].info.bestSignatureTime", equalTo("2016-10-11T09:36:10Z"));
    }

    /**
     * TestCaseID: Diagnostic-Report-Validation-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Trusted List element
     *
     * Expected Result: Diagnostic report includes Trust List element and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Test
    public  void diagnosticReportAssertTrustedLists(){
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DIAGNOSTIC ))
                .then().root(DIAGNOSTIC_DATA_PREFIX)
                .body("ListOfTrustedLists", notNullValue())
                .body("ListOfTrustedLists.Url", notNullValue())
                .body("ListOfTrustedLists.SequenceNumber", greaterThanOrEqualTo(46))
                .body("ListOfTrustedLists.Version", equalTo(5))
                .body("ListOfTrustedLists.LastLoading", notNullValue())
                .body("ListOfTrustedLists.IssueDate", notNullValue())
                .body("ListOfTrustedLists.NextUpdate", notNullValue())
                .body("ListOfTrustedLists.WellSigned", notNullValue());
    }

    /**
     * TestCaseID: Diagnostic-Report-Validation-3
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: List Of Trusted List element
     *
     * Expected Result: Diagnostic report includes lotl element and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Test

    public  void diagnosticReportAssertLotl(){
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DIAGNOSTIC ))
                .then().root(DIAGNOSTIC_DATA_PREFIX)
                .body("ListOfTrustedLists", notNullValue())
                .body("ListOfTrustedLists.CountryCode", equalTo("EU"))
                .body("ListOfTrustedLists.Url", equalTo("http://repo.ria/tsl/trusted-test-mp.xml"))
                .body("ListOfTrustedLists.SequenceNumber", greaterThanOrEqualTo(237))
                .body("ListOfTrustedLists.Version", equalTo(5))
                .body("ListOfTrustedLists.LastLoading", notNullValue())
                .body("ListOfTrustedLists.IssueDate", notNullValue())
                .body("ListOfTrustedLists.NextUpdate", notNullValue())
                .body("ListOfTrustedLists.WellSigned", notNullValue());
    }

    /**
     * TestCaseID: Diagnostic-Report-Validation-4
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Signatures element
     *
     * Expected Result: Diagnostic report includes tlanalysis element and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Test //TODO: This test may became flaky, we need a mechanism to cover elements changing their order
    public  void diagnosticReportAssertSignature(){
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DIAGNOSTIC))
                .then().root(DIAGNOSTIC_DATA_PREFIX)
                .body("Signature[0]", notNullValue())
                .body("Signature[0].SignatureFilename", equalTo("pades-baseline-lta-live-aj.pdf"))
                .body("Signature[0].DateTime", notNullValue())
                .body("Signature[0].SignatureFormat", equalTo("PAdES-BASELINE-LTA"))
//                .body("Signature[0].ContentType", equalTo("application/pdf"))
                .body("Signature[0].StructuralValidation.Valid", equalTo(true))
                .body("Signature[0].DigestMatcher[0].DigestMethod", equalTo(HASH_ALGO_SHA256))
                .body("Signature[0].DigestMatcher[0].DigestValue", equalTo("7UlS2NYiVo7OhneOHdb6gsTuA1HLM433vrBKSYnI46c="))
                .body("Signature[0].DigestMatcher[0].DataFound", equalTo(true))
                .body("Signature[0].DigestMatcher[0].DataIntact", equalTo(true))
                .body("Signature[0].DigestMatcher[0].type", equalTo("MESSAGE_DIGEST"))
                .body("Signature[0].BasicSignature.EncryptionAlgoUsedToSignThisToken", equalTo("RSA"))
                .body("Signature[0].BasicSignature.KeyLengthUsedToSignThisToken", equalTo("2048"))
                .body("Signature[0].BasicSignature.DigestAlgoUsedToSignThisToken", equalTo(HASH_ALGO_SHA256))
                .body("Signature[0].BasicSignature.SignatureIntact", equalTo(true))
                .body("Signature[0].BasicSignature.SignatureValid", equalTo(true))
                .body("Signature[0].SigningCertificate.AttributePresent", equalTo(true))
                .body("Signature[0].SigningCertificate.DigestValuePresent", equalTo(true))
                .body("Signature[0].SigningCertificate.DigestValueMatch", equalTo(true))
                .body("Signature[0].SigningCertificate.IssuerSerialMatch", equalTo(true))
                .body("Timestamp[0].ProductionTime", notNullValue())
                .body("Timestamp[0].Id", equalTo("T-986BB33B29274A85EF94B7EC0FB89C3427910D59C40A233FD588FBCB2A0E4A84"))
                .body("Timestamp[0].Type", equalTo("SIGNATURE_TIMESTAMP"))
                .body("Timestamp[0].DigestMatcher.DigestMethod", equalTo(HASH_ALGO_SHA256))
                .body("Timestamp[0].DigestMatcher.DigestValue", equalTo("7DhYQqR1MqVR3yDAY3HgnjvVjYmQpJPI9Afhqe8usmA="))
                .body("Timestamp[0].DigestMatcher.DataFound", equalTo(true))
                .body("Timestamp[0].DigestMatcher.DataIntact", equalTo(true))
                .body("Timestamp[0].DigestMatcher.type", equalTo("MESSAGE_IMPRINT"))
                .body("Timestamp[0].BasicSignature.EncryptionAlgoUsedToSignThisToken", equalTo("RSA"))
                .body("Timestamp[0].BasicSignature.KeyLengthUsedToSignThisToken", equalTo("2048"))
                .body("Timestamp[0].BasicSignature.DigestAlgoUsedToSignThisToken", equalTo(HASH_ALGO_SHA512))
                .body("Timestamp[0].BasicSignature.SignatureIntact", equalTo(true))
                .body("Timestamp[0].BasicSignature.SignatureValid", equalTo(true))
                .body("Timestamp[0].TimestampedObject[1].Category", equalTo("SIGNATURE"))
                .body("Timestamp[0].DigestAlgoAndValue.DigestMethod", equalTo(HASH_ALGO_SHA256))
//                .body("Timestamp[0].DigestAlgoAndValue.DigestValue", equalTo("8BTH3ySdhzTfJz2TfuXr8PgWa+B3XEeoBgjxoU6yP0w="))
                .body("Signature[0].SignatureScope[0].Description", equalTo("The document byte range: [0, 9136, 28082, 26387]"))
                .body("Signature[0].SignatureScope[0].Name", equalTo("PDF previous version #1"))
                .body("Signature[0].SignatureScope[0].Scope", equalTo("PARTIAL"));
    }

    /**
     * TestCaseID: Diagnostic-Report-Validation-5
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Used Certificates
     *
     * Expected Result: Diagnostic report includes used certificates element and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Ignore
    @Test //TODO: This test is flaky, we need a mechanism to cover elements changing their order
    public  void diagnosticReportAssertUsedCertificates(){
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DIAGNOSTIC ))
                .then().root(DIAGNOSTIC_DATA_PREFIX)
                .body("usedCertificates", notNullValue())
                .body("usedCertificates.serialNumber", notNullValue())
                .body("usedCertificates[0].subjectDistinguishedName[0].value", equalTo("cn=esteid-sk 2015,2.5.4.97=#0c0e4e545245452d3130373437303133,o=as sertifitseerimiskeskus,c=ee"))
                .body("usedCertificates[0].subjectDistinguishedName[0].format", equalTo("CANONICAL"))
                .body("usedCertificates[0].subjectDistinguishedName[1].value", equalTo("CN=ESTEID-SK 2015,2.5.4.97=#0c0e4e545245452d3130373437303133,O=AS Sertifitseerimiskeskus,C=EE"))
                .body("usedCertificates[0].subjectDistinguishedName[1].format", equalTo("RFC2253"))
                .body("usedCertificates[0].issuerDistinguishedName[0].value", equalTo("1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=ee certification centre root ca,o=as sertifitseerimiskeskus,c=ee"))
                .body("usedCertificates[0].issuerDistinguishedName[0].format", equalTo("CANONICAL"))
                .body("usedCertificates[0].issuerDistinguishedName[1].value", equalTo("1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=EE Certification Centre Root CA,O=AS Sertifitseerimiskeskus,C=EE"))
                .body("usedCertificates[0].issuerDistinguishedName[1].format", equalTo("RFC2253"));
    }

    /**
     * TestCaseID: Diagnostic-Report-Validation-6
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Wrong signature value
     *
     * Expected Result: Diagnostic report includes wrong signature value
     *
     * File: TS-02_23634_TS_wrong_SignatureValue.asice
     */
    @Test
    public  void diagnosticReportWrongSignatureValueAsice() {
        setTestFilesDirectory("bdoc/live/timestamp/");

        post(validationRequestFor("TS-02_23634_TS_wrong_SignatureValue.asice", null, REPORT_TYPE_DIAGNOSTIC))
                .then().root(DIAGNOSTIC_DATA_PREFIX)
                .body("Signature[0].BasicSignature.EncryptionAlgoUsedToSignThisToken", equalTo("RSA"))
                .body("Signature[0].BasicSignature.KeyLengthUsedToSignThisToken", equalTo("2048"))
                .body("Signature[0].BasicSignature.DigestAlgoUsedToSignThisToken", equalTo(HASH_ALGO_SHA256))
                .body("Signature[0].BasicSignature.SignatureIntact", equalTo(false))
                .body("Signature[0].BasicSignature.SignatureValid", equalTo(false));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
