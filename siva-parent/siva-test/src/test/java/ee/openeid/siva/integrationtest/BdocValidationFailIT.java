/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class BdocValidationFailIT extends SiVaRestTests{

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
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc with single invalid signature
     *
     * Expected Result: The document should fail the validation
     *
     * File: IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc
     */
    @Test
    public void bdocInvalidSingleSignature() {
        QualifiedReport report = postForReport("IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc");
        assertAllSignaturesAreInvalid(report);
        assertEquals("The signature is not intact!", report.getSignatures().get(0).getErrors().get(0).getContent());
        assertEquals(2, report.getSignatures().get(0).getErrors().size());
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc with multiple invalid signatures
     *
     * Expected Result: The document should fail the validation
     *
     * File: BdocMultipleSignaturesInvalid.bdoc
     */
    @Test
    public void bdocInvalidMultipleSignatures() {
        setTestFilesDirectory("bdoc/test/timemark/");
        assertAllSignaturesAreInvalid(postForReport("BdocMultipleSignaturesInvalid.bdoc"));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc with multiple signatures both valid and invalid
     *
     * Expected Result: The document should fail the validation
     *
     * File: BdocMultipleSignaturesMixedWithValidAndInvalid.bdoc
     */
    @Test
    public void bdocInvalidAndValidMultipleSignatures() {
        setTestFilesDirectory("bdoc/test/timemark/");
        assertSomeSignaturesAreValid(postForReport("BdocMultipleSignaturesMixedWithValidAndInvalid.bdoc"),2);
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc with no signatures
     *
     * Expected Result: The document should fail the validation
     *
     * File: BdocContainerNoSignature.bdoc
     */
    @Test
    public void bdocNoSignatures() {
        setTestFilesDirectory("document_format_test_files/");
        assertAllSignaturesAreInvalid(postForReport("BdocContainerNoSignature.bdoc"));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc with invalid mimetype in manifest
     *
     * Expected Result: document malformed error should be returned
     *
     * File: 23147_weak-warning-sha1-invalid-mimetype-in-manifest.bdoc
     */
    @Test
    public void bdocMalformedBdocWithInvalidMimetypeInManifest() {
        post(validationRequestFor("23147_weak-warning-sha1-invalid-mimetype-in-manifest.bdoc"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));

    }

    /**
     * TestCaseID: Bdoc-ValidationFail-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Asice with wrong slash character ('\') in data file mime-type value
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-V-33.asice
     */
    @Test
    public void bdocInvalidMimeTypeChars() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("EE_SER-AEX-B-LT-V-33.asice"));
        post(validationRequestWithValidKeys(encodedString, "EE_SER-AEX-B-LT-V-33.asice", "bdoc", ""))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Wrong signature timestamp
     *
     * Expected Result: The document should fail the validation
     *
     * File: TS-02_23634_TS_wrong_SignatureValue.asice
     */
    @Test
    public void bdocInvalidTimeStampDontMatchSigValue() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("TS-02_23634_TS_wrong_SignatureValue.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("SIG_CRYPTO_FAILURE"))
                .body("signatures[0].errors.content", Matchers.hasItems("Signature has an invalid timestamp"))
                .body("signatures[0].errors.content", Matchers.hasItems("The signature is not intact!"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Asice No non-repudiation key usage value in the certificate, verification of AdES signature level
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-I-43.asice
     */
    @Test
    public void bdocInvalidNonRepudiationKey() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-I-43.asice"))
                .then()
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("SIG_CONSTRAINTS_FAILURE"))
                .body("signatures[0].errors.content", Matchers.hasItems("The signer's certificate has not expected key-usage!"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Asice signers certificate does not have non-repudiation value in the certificates key usage field and it does not contain the QC and SSCD compliance information.
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-I-26.asice
     */
    @Test
    public void bdocInvalidNonRepudiationKeyNoComplianceInfo() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-I-26.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("SIG_CONSTRAINTS_FAILURE"))
                .body("signatures[0].errors.content", Matchers.hasItems("The signer's certificate has not expected key-usage!"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc OCSP certificate is not trusted
     *
     * Expected Result: The document should fail the validation
     *
     * File:  TM-01_bdoc21-unknown-resp.bdoc
     */
    @Test
    public void bdocNotTrustedOcspCert() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("TM-01_bdoc21-unknown-resp.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "TM-01_bdoc21-unknown-resp.bdoc", "bdoc", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("NO_CERTIFICATE_CHAIN_FOUND"))
                .body("signatures[0].errors.content", Matchers.hasItems("The certificate chain for revocation data is not trusted, there is no trusted anchor."))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Asice TSA certificate is not trusted
     *
     * Expected Result: The document should fail the validation
     *
     * File: TS-05_23634_TS_unknown_TSA.asice
     */
    @Test
    public void bdocNotTrustedTsaCert() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("TS-05_23634_TS_unknown_TSA.asice"));
        post(validationRequestWithValidKeys(encodedString, "TS-05_23634_TS_unknown_TSA.asice", "bdoc", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("Signature has an invalid timestamp"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-12
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Asice OCSP response status is revoked
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-R-25.asice
     */
    @Test
    public void bdocTsOcspStatusRevoked() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-R-25.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("The certificate is revoked!"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-13
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Asice difference between OCSP and time-stamp issuing times is more than 24 hours
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-V-20.asice
     */
    @Test
    public void bdocOcspAndTsDifferenceOver24H() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("EE_SER-AEX-B-LT-V-20.asice"));
        post(validationRequestWithValidKeys(encodedString, "EE_SER-AEX-B-LT-V-20.asice", "bdoc", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("The difference between the revocation time and the signature time stamp is too large"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-14
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Asice unsigned data files in the container
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-V-34.asice
     */
    @Test @Ignore //TODO: https://github.com/open-eid/SiVa/issues/18
    public void bdocUnsignedDataFiles() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-V-34.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems(""))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-15
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc different data file mime-type values in signatures.xml and manifest.xml files
     *
     * Expected Result: The document should fail the validation
     *
     * File: 23613_TM_wrong-manifest-mimetype.bdoc
     */
    @Test @Ignore //TODO: https://github.com/open-eid/SiVa/issues/18
    public void bdocDifferentDataFileInSignature() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("23613_TM_wrong-manifest-mimetype.bdoc"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems(""))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-16
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc SignatureValue does not correspond to the SignedInfo block
     *
     * Expected Result: The document should fail the validation
     *
     * File: REF-19_bdoc21-no-sig-asn1-pref.bdoc
     */
    @Test
    public void bdocSignatureValueDoNotCorrespondToSignedInfo() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("REF-19_bdoc21-no-sig-asn1-pref.bdoc"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("SIG_CRYPTO_FAILURE"))
                .body("signatures[0].errors.content", Matchers.hasItems("The signature is not intact!"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-17
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc Baseline-BES file
     *
     * Expected Result: The document should fail the validation
     *
     * File: signWithIdCard_d4j_1.0.4_BES.asice
     */
    @Test
    public void bdocBaselineBesSignatureLevel() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("signWithIdCard_d4j_1.0.4_BES.asice"))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B_BES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("The expected format is not found!"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-18
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc Baseline-EPES file
     *
     * Expected Result: The document should fail the validation
     *
     * File: TM-04_kehtivuskinnituset.4.asice
     */
    @Test
    public void bdocBaselineEpesSignatureLevel() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-04_kehtivuskinnituset.4.asice"))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B_EPES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("The expected format is not found!"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-19
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc signers certificate is not trusted
     *
     * Expected Result: The document should fail the validation
     *
     * File: SS-4_teadmataCA.4.asice
     */
    @Test
    public void bdocSignersCertNotTrusted() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("SS-4_teadmataCA.4.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("NO_CERTIFICATE_CHAIN_FOUND"))
                .body("signatures[0].errors[0].content", Matchers.is("The certificate chain for signature is not trusted, there is no trusted anchor."))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-20
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc OCSP response status is revoked
     *
     * Expected Result: The document should fail the validation
     *
     * File: TM-15_revoked.4.asice
     */
    @Test
    public void bdocTmOcspStatusRevoked() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-15_revoked.4.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("The certificate is revoked!"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-21
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc OCSP response status is unknown
     *
     * Expected Result: The document should fail the validation
     *
     * File: TM-16_unknown.4.asice
     */
    @Test @Ignore //TODO: https://github.com/open-eid/SiVa/issues/23
    public void bdocTmOcspStatusUnknown() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-16_unknown.4.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems(""))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-22
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc signed data file has been removed from the container
     *
     * Expected Result: The document should fail the validation
     *
     * File: KS-21_fileeemaldatud.4.asice
     */
    @Test
    public void bdocSignedFileRemoved() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("KS-21_fileeemaldatud.4.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("SIGNED_DATA_NOT_FOUND"))
                .body("signatures[0].errors[0].content", Matchers.is("The reference data object(s) is not found!"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-23
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc no files in container
     *
     * Expected Result: The document should fail the validation
     *
     * File: KS-02_tyhi.bdoc
     */
    @Test
    public void bdocNoFilesInContainer() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("KS-02_tyhi.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "KS-02_tyhi.bdoc", "bdoc", ""))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors.message", Matchers.hasItem(MAY_NOT_BE_EMPTY))
                .body("requestErrors.message", Matchers.hasItem(INVALID_BASE_64));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-24
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc wrong nonce
     *
     * Expected Result: The document should fail the validation
     *
     * File: TM-10_noncevale.4.asice
     */
    @Test
    public void bdocWrongOcspNonce() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("TM-10_noncevale.4.asice"));
        post(validationRequestWithValidKeys(encodedString, "TM-10_noncevale.4.asice", "bdoc", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItem("Nonce is invalid"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-25
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc signed data file(s) don't match the hash values in reference elements
     *
     * Expected Result: The document should fail the validation
     *
     * File: REF-14_filesisumuudetud.4.asice
     */
    @Test
    public void bdocDataFilesDontMatchHash() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("REF-14_filesisumuudetud.4.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("signatures[0].errors.content", Matchers.hasItem("The reference data object(s) is not intact!"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-26
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Asice Baseline-T signature
     *
     * Expected Result: The document should fail the validation
     *
     * File: TS-06_23634_TS_missing_OCSP.asice
     */
    @Test
    public void bdocBaselineTSignature() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("TS-06_23634_TS_missing_OCSP.asice"));
        post(validationRequestWithValidKeys(encodedString, "TS-06_23634_TS_missing_OCSP.asice", "bdoc", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT")) //TODO: Shouldnt it return XAdES_BASELINE_T instead?
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("TRY_LATER"))
                .body("signatures[0].errors.content", Matchers.hasItem("No revocation data for the certificate"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-27
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc OCSP response is not the one expected
     *
     * Expected Result: The document should fail the validation
     *
     * File: 23608-bdoc21-TM-ocsp-bad-nonce.bdoc
     */
    @Test
    public void bdocWrongSignersCertInOcspResponse() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("23608-bdoc21-TM-ocsp-bad-nonce.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "23608-bdoc21-TM-ocsp-bad-nonce.bdoc", "bdoc", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItem("Nonce is invalid"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-28
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc certificate's validity time is not in the period of OCSP producedAt time
     *
     * Expected Result: The document should fail the validation
     *
     * File: 23154_test1-old-sig-sigat-OK-prodat-NOK-1.bdoc
     */
    @Test
    public void bdocCertificateValidityOutOfOcspRange() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("23154_test1-old-sig-sigat-OK-prodat-NOK-1.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "23154_test1-old-sig-sigat-OK-prodat-NOK-1.bdoc", "bdoc", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItem("Signature has been created with expired certificate"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationFail-29
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc 	BDOC-1.0 version container
     *
     * Expected Result: The document should fail the validation
     *
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
