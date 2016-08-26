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
public class BdocValidationFail extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timemark/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with single invalid signature
     *
     * Expected Result: The document should fail the validation
     *
     * File: IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc
     ***/
    @Test
    public void bdocInvalidSingleSignature() {
        QualifiedReport report = postForReport("IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc");
        assertAllSignaturesAreInvalid(report);
        assertEquals("The signature is not intact!", report.getSignatures().get(0).getErrors().get(0).getContent());
        assertEquals(2, report.getSignatures().get(0).getErrors().size());
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with multiple invalid signatures
     *
     * Expected Result: The document should fail the validation
     *
     * File: BdocMultipleSignaturesInvalid.bdoc
     ***/
    @Test
    public void bdocInvalidMultipleSignatures() {
        setTestFilesDirectory("bdoc/test/timemark/");
        assertAllSignaturesAreInvalid(postForReport("BdocMultipleSignaturesInvalid.bdoc"));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with multiple signatures both valid and invalid
     *
     * Expected Result: The document should fail the validation
     *
     * File: BdocMultipleSignaturesMixedWithValidAndInvalid.bdoc
     ***/
    @Test
    public void bdocInvalidAndValidMultipleSignatures() {
        setTestFilesDirectory("bdoc/test/timemark/");
        assertSomeSignaturesAreValid(postForReport("BdocMultipleSignaturesMixedWithValidAndInvalid.bdoc"),2);
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with no signatures
     *
     * Expected Result: The document should fail the validation
     *
     * File: BdocContainerNoSignature.bdoc
     ***/
    @Test
    public void bdocNoSignatures() {
        setTestFilesDirectory("document_format_test_files/");
        assertAllSignaturesAreInvalid(postForReport("BdocContainerNoSignature.bdoc"));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-5
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with invalid mimetype in manifest
     *
     * Expected Result: document malformed error should be returned
     *
     * File: 23147_weak-warning-sha1-invalid-mimetype-in-manifest.bdoc
     ***/
    @Test
    public void bdocMalformedBdocWithInvalidMimetypeInManifest() {
        post(validationRequestFor("23147_weak-warning-sha1-invalid-mimetype-in-manifest.bdoc"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));

    }

    /***
     * TestCaseID: Bdoc-ValidationFail-6
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice with wrong slash character ('\') in data file mime-type value
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-V-33.asice
     ***/
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

    /***
     * TestCaseID: Bdoc-ValidationFail-7
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice time-stamp does not correspond to the SignatureValue element
     *
     * Expected Result: The document should fail the validation
     *
     * File: TS-02_23634_TS_wrong_SignatureValue.asice
     ***/
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

    /***
     * TestCaseID: Bdoc-ValidationFail-8
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice No non-repudiation key usage value in the certificate, verification of AdES signature level
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-I-43.asice
     ***/
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

    /***
     * TestCaseID: Bdoc-ValidationFail-9
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice signers certificate does not have non-repudiation value in the certificates key usage field and it does not contain the QC and SSCD compliance information.
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-I-26.asice
     ***/
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

    /***
     * TestCaseID: Bdoc-ValidationFail-10
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc OCSP certificate is not trusted
     *
     * Expected Result: The document should fail the validation
     *
     * File:  TM-01_bdoc21-unknown-resp.bdoc
     ***/
    @Test
    public void bdocNotTrustedOcspCert() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-01_bdoc21-unknown-resp.bdoc"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("NO_CERTIFICATE_CHAIN_FOUND"))
                .body("signatures[0].errors.content", Matchers.hasItems("The certificate chain for revocation data is not trusted, there is no trusted anchor."))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-11
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice TSA certificate is not trusted
     *
     * Expected Result: The document should fail the validation
     *
     * File: TS-05_23634_TS_unknown_TSA.asice
     ***/
    @Test //TODO: Should the indication be INDERMINATE instead of TOTAL-FAILED?
    public void bdocNotTrustedTsaCert() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("TS-05_23634_TS_unknown_TSA.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("Signature has an invalid timestamp"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-12
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice OCSP response status is revoked
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-R-25.asice
     ***/
    @Test //TODO: Should the indication be INDERMINATE instead of TOTAL-FAILED? Should subindication be REVOKED_NO_POE?
    public void bdocTsOcspStatusRevoked() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-R-25.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("The certificate is revoked!"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-13
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice difference between OCSP and time-stamp issuing times is more than 24 hours
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-V-20.asice
     ***/
    @Test
    public void bdocOcspAndTsDifferenceOver24H() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-V-20.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("The difference between the revocation time and the signature time stamp is too large"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-14
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice unsigned data files in the container
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-V-34.asice
     ***/
    @Test @Ignore //TODO: Needs investigation whether it is actually okay to ignore manifest errors as currently is done
    public void bdocUnsignedDataFiles() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-V-34.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems(""))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-15
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc different data file mime-type values in signatures.xml and manifest.xml files
     *
     * Expected Result: The document should fail the validation
     *
     * File: 23613_TM_wrong-manifest-mimetype.bdoc
     ***/
    @Test @Ignore //TODO: Needs investigation whether it is actually okay to ignore manifest errors as currently is done
    public void bdocDifferentDataFileInSignature() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("23613_TM_wrong-manifest-mimetype.bdoc"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems(""))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-16
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc SignatureValue does not correspond to the SignedInfo block
     *
     * Expected Result: The document should fail the validation
     *
     * File: REF-19_bdoc21-no-sig-asn1-pref.bdoc
     ***/
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

    /***
     * TestCaseID: Bdoc-ValidationFail-17
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc Baseline-BES file
     *
     * Expected Result: The document should fail the validation
     *
     * File: signWithIdCard_d4j_1.0.4_BES.asice
     ***/
    @Test
    public void bdocBaselineBesSignatureLevel() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("signWithIdCard_d4j_1.0.4_BES.asice"))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B_BES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("TRY_LATER"))
                .body("signatures[0].errors.content", Matchers.hasItems("No revocation data for the certificate"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-18
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc Baseline-EPES file
     *
     * Expected Result: The document should fail the validation
     *
     * File: TM-04_kehtivuskinnituset.4.asice
     ***/
    @Test //TODO: Should the indication be INDERMINATE instead of TOTAL-FAILED?
    public void bdocBaselineEpesSignatureLevel() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-04_kehtivuskinnituset.4.asice"))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B_EPES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("TRY_LATER"))
                .body("signatures[0].errors.content", Matchers.hasItems("No revocation data for the certificate"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-19
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc signers certificate is not trusted
     *
     * Expected Result: The document should fail the validation
     *
     * File: SS-4_teadmataCA.4.asice
     ***/
    @Test //TODO: Should the indication be INDERMINATE instead of TOTAL-FAILED?
    public void bdocSignersCertNotTrusted() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("SS-4_teadmataCA.4.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("NO_CERTIFICATE_CHAIN_FOUND"))
                .body("signatures[0].errors[0].content", Matchers.is("The certificate chain for signature is not trusted, there is no trusted anchor."))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-20
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc OCSP response status is revoked
     *
     * Expected Result: The document should fail the validation
     *
     * File: TM-15_revoked.4.asice
     ***/
    @Test //TODO: Should the indication be INDERMINATE instead of TOTAL-FAILED? Should the subIndication be REVOKED_NO_POE instead?
    public void bdocTmOcspStatusRevoked() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-15_revoked.4.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("The certificate is revoked!"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-21
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc OCSP response status is unknown
     *
     * Expected Result: The document should fail the validation
     *
     * File: TM-16_unknown.4.asice
     ***/
    @Test @Ignore //TODO: This file returns revoked status...
    public void bdocTmOcspStatusUnknown() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-16_unknown.4.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems(""))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-22
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc signed data file has been removed from the container
     *
     * Expected Result: The document should fail the validation
     *
     * File: TM-16_unknown.4.asice
     ***/
    @Test //TODO: Should the indication be INDERMINATE instead of TOTAL-FAILED?
    public void bdocSignedFileRemoved() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("KS-21_fileeemaldatud.4.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("SIGNED_DATA_NOT_FOUND"))
                .body("signatures[0].errors[0].content", Matchers.is("The reference data object(s) is not found!"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-23
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc no files in container
     *
     * Expected Result: The document should fail the validation
     *
     * File: KS-02_tyhi.bdoc
     ***/
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

    /***
     * TestCaseID: Bdoc-ValidationFail-24
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc signed data file has been removed from the container
     *
     * Expected Result: The document should fail the validation
     *
     * File: TM-10_noncevale.4.asice
     ***/
    @Test
    public void bdocWrongOcspNonce() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-10_noncevale.4.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItem("Nonce is invalid"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-25
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc signed data file(s) don't match the hash values in reference elements
     *
     * Expected Result: The document should fail the validation
     *
     * File: REF-14_filesisumuudetud.4.asice
     ***/
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

    /***
     * TestCaseID: Bdoc-ValidationFail-26
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice Baseline-T signature
     *
     * Expected Result: The document should fail the validation
     *
     * File: TS-06_23634_TS_missing_OCSP.asice
     ***/
    @Test
    public void bdocBaselineTSignature() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("TS-06_23634_TS_missing_OCSP.asice"))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("TRY_LATER"))
                .body("signatures[0].errors.content", Matchers.hasItem("No revocation data for the certificate"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-27
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc 	OCSP response doesn't correspond to the signers certificate
     *
     * Expected Result: The document should fail the validation
     *
     * File: NS28_WrongSignerCertInOCSPResp.bdoc
     ***/
    @Test
    public void bdocWrongSignersCertInOcspResponse() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("NS28_WrongSignerCertInOCSPResp.bdoc"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("TRY_LATER"))
                .body("signatures[0].errors.content", Matchers.hasItem("No revocation data for the certificate"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-28
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc certificate's validity time is not in the period of OCSP producedAt time
     *
     * Expected Result: The document should fail the validation
     *
     * File: 23154_test1-old-sig-sigat-OK-prodat-NOK-1.bdoc
     ***/
    @Test
    public void bdocCertificateValidityOutOfOcspRange() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("23154_test1-old-sig-sigat-OK-prodat-NOK-1.bdoc"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("")) //TODO: VAL-242 Subindication should not be empty.
                .body("signatures[0].errors.content", Matchers.hasItem("Signature has been created with expired certificate"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-29
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc 	BDOC-1.0 version container
     *
     * Expected Result: The document should fail the validation
     *
     * File: BDOC-1.0.bdoc
     ***/
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
