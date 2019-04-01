/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.SimpleReport;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;
@Category(IntegrationTest.class)
public class BdocValidationFailIT extends SiVaRestTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timemark/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-1
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc with single invalid signature
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc
     */
    @Test
    public void bdocInvalidSingleSignature() {
        SimpleReport report = postForReport("IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc");
        assertAllSignaturesAreInvalid(report);
        assertEquals("The result of the LTV validation process is not acceptable to continue the process!", report.getValidationConclusion().getSignatures().get(0).getErrors().get(0).getContent());
        assertEquals(4, report.getValidationConclusion().getSignatures().get(0).getErrors().size());
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-2
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc with multiple invalid signatures
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: BdocMultipleSignaturesInvalid.bdoc
     */
    @Test
    public void bdocInvalidMultipleSignatures() {
        setTestFilesDirectory("bdoc/test/timemark/");
        assertAllSignaturesAreInvalid(postForReport("BdocMultipleSignaturesInvalid.bdoc"));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-3
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc with multiple signatures both valid and invalid
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: BdocMultipleSignaturesMixedWithValidAndInvalid.bdoc
     */
    @Test
    public void bdocInvalidAndValidMultipleSignatures() {
        setTestFilesDirectory("bdoc/test/timemark/");
        assertSomeSignaturesAreValid(postForReport("BdocMultipleSignaturesMixedWithValidAndInvalid.bdoc"), 3);
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-4
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc with no signatures
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: BdocContainerNoSignature.bdoc
     */
    @Test
    public void bdocNoSignatures() {
        setTestFilesDirectory("document_format_test_files/");
        post(validationRequestFor("BdocContainerNoSignature.bdoc"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.validationWarnings", Matchers.isEmptyOrNullString());
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-7
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Wrong signature timestamp
     * <p>
     * Expected Result: The document should fail the validation in DD4J
     * <p>
     * File: TS-02_23634_TS_wrong_SignatureValue.asice
     */
    @Test
    public void bdocInvalidTimeStampDontMatchSigValue() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestForDD4j("TS-02_23634_TS_wrong_SignatureValue.asice",null, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIG_CRYPTO_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2015-11-13T11:15:36Z"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!", "The result of the timestamps validation process is not conclusive!", "The reference data object is not intact!", "Signature has an invalid timestamp"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-8
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice No non-repudiation key usage value in the certificate, verification of AdES signature level
     * <p>
     * Expected Result: The document should fail the validation in DD4J
     * <p>
     * File: EE_SER-AEX-B-LT-I-43.asice
     */
    @Test
    public void bdocInvalidNonRepudiationKey() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestForDD4j("EE_SER-AEX-B-LT-I-43.asice", null, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("NOT_ADES"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIG_CONSTRAINTS_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-9
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice signers certificate does not have non-repudiation value in the certificates key usage field and it does not contain the QC and SSCD compliance information.
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: EE_SER-AEX-B-LT-I-26.asice
     */
    @Test
    public void bdocInvalidNonRepudiationKeyNoComplianceInfo() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestForDD4j("EE_SER-AEX-B-LT-I-26.asice", null, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIG_CONSTRAINTS_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-10
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc OCSP certificate is not trusted
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: TM-01_bdoc21-unknown-resp.bdoc
     */
    @Test
    public void bdocNotTrustedOcspCert() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("TM-01_bdoc21-unknown-resp.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "TM-01_bdoc21-unknown-resp.bdoc", VALID_SIGNATURE_POLICY_3))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("NO_CERTIFICATE_CHAIN_FOUND"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-11
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice TSA certificate is not trusted
     * <p>
     * Expected Result: The document should fail the validation in DD4J
     * <p>
     * File: TS-05_23634_TS_unknown_TSA.asice
     */
    @Test
    public void bdocNotTrustedTsaCert() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestForDD4j("TS-05_23634_TS_unknown_TSA.asice", null, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2014-05-19T10:45:19Z"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("Signature has an invalid timestamp"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-12
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice OCSP response status is revoked
     * <p>
     * Expected Result: The document should fail the validation in DD4J
     * <p>
     * File: EE_SER-AEX-B-LT-R-25.asice
     */
    @Test
    public void bdocTsOcspStatusRevoked() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestForDD4j("EE_SER-AEX-B-LT-R-25.asice", null, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("NO_POE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The past signature validation is not conclusive!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-13
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice difference between OCSP and time-stamp issuing times is more than 24 hours
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: EE_SER-AEX-B-LT-V-20.asice
     */
    @Test
    public void bdocOcspAndTsDifferenceOver24H() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestForDD4j("EE_SER-AEX-B-LT-V-20.asice", null, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.is("The difference between the OCSP response time and the signature timestamp is too large"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-15
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc different data file mime-type values in signatures.xml and manifest.xml files
     * <p>
     * Expected Result: The document should return warning regarding the mismatch
     * <p>
     * File: 23613_TM_wrong-manifest-mimetype.bdoc
     */
    @Test
    public void bdocDifferentDataFileInSignature() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("23613_TM_wrong-manifest-mimetype.bdoc"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file <test.txt> with mimetype <application/binary> but the signature file for signature S0 indicates the mimetype is <application/octet-stream>"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-16
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc SignatureValue does not correspond to the SignedInfo block
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: REF-19_bdoc21-no-sig-asn1-pref.bdoc
     */
    @Test
    public void bdocSignatureValueDoNotCorrespondToSignedInfo() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("REF-19_bdoc21-no-sig-asn1-pref.bdoc"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIG_CRYPTO_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-17
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc Baseline-BES file
     * <p>
     * Expected Result: The document should fail the validation in DD4J
     * <p>
     * File: signWithIdCard_d4j_1.0.4_BES.asice
     */
    @Test
    public void bdocBaselineBesSignatureLevel() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestForDD4j("signWithIdCard_d4j_1.0.4_BES.asice", null, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B_BES"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.serialNumber", Matchers.notNullValue())
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.commonName", Matchers.notNullValue())
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-18
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc Baseline-EPES file
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: TM-04_kehtivuskinnituset.4.asice
     */
    @Test
    public void bdocBaselineEpesSignatureLevel() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDD4j("TM-04_kehtivuskinnituset.4.asice", null, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B_EPES"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-19
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc signers certificate is not trusted
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: SS-4_teadmataCA.4.asice
     */
    @Test
    public void bdocSignersCertNotTrusted() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDD4j("SS-4_teadmataCA.4.asice", null, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2013-10-11T08:15:47Z"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.is("The certificate path is not trusted!"))
                .body("validationReport.validationConclusion.signatures[0].errors[1].content", Matchers.is("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-20
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc OCSP response status is revoked
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: TM-15_revoked.4.asice
     */
    @Test
    public void bdocTmOcspStatusRevoked() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDD4j("TM-15_revoked.4.asice", null, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("NO_POE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The past signature validation is not conclusive!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-21
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc OCSP response status is unknown
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: TM-16_unknown.4.asice
     */
    @Test
    public void bdocTmOcspStatusUnknown() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDD4j("TM-16_unknown.4.asice", null, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-22
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc signed data file has been removed from the container
     * <p>
     * Expected Result: The document should fail the validation in DD4J
     * <p>
     * File: KS-21_fileeemaldatud.4.asice
     */
    @Test
    public void bdocSignedFileRemoved() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDD4j("KS-21_fileeemaldatud.4.asice", null, null))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-23
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc no files in container
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: KS-02_tyhi.bdoc
     */
    @Test
    public void bdocNoFilesInContainer() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("KS-02_tyhi.bdoc"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors", Matchers.hasSize(2));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-24
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc wrong nonce
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: TM-10_noncevale.4.asice
     */
    @Test
    public void bdocWrongOcspNonce() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-10_noncevale.4.bdoc"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItem("OCSP nonce is invalid"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-25
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc signed data file(s) don't match the hash values in reference elements
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: REF-14_filesisumuudetud.4.bdoc
     */
    @Test
    public void bdocDataFilesDontMatchHash() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("REF-14_filesisumuudetud.4.bdoc"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItem("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-26
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice Baseline-T signature
     * <p>
     * Expected Result: The document should fail the validation in DD4J
     * <p>
     * File: TS-06_23634_TS_missing_OCSP.asice
     */
    @Test
    public void bdocBaselineTSignature() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestForDD4j("TS-06_23634_TS_missing_OCSP.asice", null, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2014-05-19T10:48:04Z"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItem("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-27
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc OCSP response is not the one expected
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: 23608-bdoc21-TM-ocsp-bad-nonce.bdoc
     */
    @Test
    public void bdocWrongSignersCertInOcspResponse() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("23608-bdoc21-TM-ocsp-bad-nonce.bdoc"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2014-12-12T13:17:00Z"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItem("OCSP nonce is invalid"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-28
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc certificate's validity time is not in the period of OCSP producedAt time
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: 23154_test1-old-sig-sigat-OK-prodat-NOK-1.bdoc
     */
    @Test
    public void bdocCertificateValidityOutOfOcspRange() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("23154_test1-old-sig-sigat-OK-prodat-NOK-1.bdoc"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))

                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItem("Signature has been created with expired certificate"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-29
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc 	BDOC-1.0 version container
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: BDOC-1.0.bdoc
     */
    @Test
    public void bdocOldNotSupportedVersion() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("BDOC-1.0.bdoc"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
