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
import static org.junit.Assert.assertTrue;

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
        assertTrue(report.getSignatures().get(0).getErrors().size() == 2);
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
        post(validationRequestWithValidKeys(encodedString, "EE_SER-AEX-B-LT-V-33.asice", "bdoc"))
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
                .body("signatures[0].errors.nameId", Matchers.hasItems("GENERIC"))
                .body("signatures[0].errors.content", Matchers.hasItems("Signature has an invalid timestamp"))
                .body("signatures[0].errors.nameId", Matchers.hasItems("BBB_CV_ISI_ANS"))
                .body("signatures[0].errors.content", Matchers.hasItems("The signature is not intact!"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("SIG_CRYPTO_FAILURE"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-8
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice No non-repudiation key usage value in the certificate
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
                .body("signatures[0].errors.nameId", Matchers.hasItems("BBB_XCV_ISCGKU_ANS"))
                .body("signatures[0].errors.content", Matchers.hasItems("The signer's certificate has not expected key-usage!"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("SIG_CONSTRAINTS_FAILURE"))
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
                .body("signatures[0].errors.nameId", Matchers.hasItems("BBB_XCV_ISCGKU_ANS"))
                .body("signatures[0].errors.content", Matchers.hasItems("The signer's certificate has not expected key-usage!"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("SIG_CONSTRAINTS_FAILURE"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-10
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice OCSP certificate is not trusted
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-I-27.asice
     ***/
    @Test @Ignore //TODO: As test certs are included by defaul this file currently passes. Separate class with life profile may be needed for this test.
    public void bdocNotTrustedOcspCert() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-I-27.asice"))
                .then()
                .body("signatures[0].errors.nameId", Matchers.hasItems(""))
                .body("signatures[0].errors.content", Matchers.hasItems(""))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
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
    @Test
    public void bdocNotTrustedTsaCert() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("TS-05_23634_TS_unknown_TSA.asice"))
                .then()
                .body("signatures[0].errors.nameId", Matchers.hasItems("GENERIC"))
                .body("signatures[0].errors.content", Matchers.hasItems("Signature has an invalid timestamp"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("NO_VALID_TIMESTAMP"))
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
    @Test
    public void bdocTsOcspStatusRevoked() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-R-25.asice"))
                .then()
                .body("signatures[0].errors.nameId", Matchers.hasItems("BBB_XCV_ISCR_ANS"))
                .body("signatures[0].errors.content", Matchers.hasItems("The certificate is revoked!"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("REVOKED_NO_POE"))
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
                .body("signatures[0].errors.nameId", Matchers.hasItems("GENERIC"))
                .body("signatures[0].errors.content", Matchers.hasItems("The difference between the revocation time and the signature time stamp is too large"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
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
                .body("signatures[0].errors.nameId", Matchers.hasItems(""))
                .body("signatures[0].errors.content", Matchers.hasItems(""))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
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
                .body("signatures[0].errors.nameId", Matchers.hasItems(""))
                .body("signatures[0].errors.content", Matchers.hasItems(""))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
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
                .body("signatures[0].errors.nameId", Matchers.hasItems("BBB_CV_ISI_ANS"))
                .body("signatures[0].errors.content", Matchers.hasItems("The signature is not intact!"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("SIG_CRYPTO_FAILURE"))
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
     * File: TM-05_bdoc21-good-nonce-policy-bes.bdoc
     ***/
    @Test @Ignore //TODO: This is actually a EPES level file?
    public void bdocBaselineBesSignatureLevel() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-05_bdoc21-good-nonce-policy-bes.bdoc"))
                .then()
                .body("signatures[0].errors.nameId", Matchers.hasItems("BBB_XCV_IRDPFC_ANS"))
                .body("signatures[0].errors.content", Matchers.hasItems("No revocation data for the certificate"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("TRY_LATER"))
                .body("validSignaturesCount", Matchers.is(0));
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
    @Test
    public void bdocBaselineEpesSignatureLevel() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-04_kehtivuskinnituset.4.asice"))
                .then()
                .body("signatures[0].errors.nameId", Matchers.hasItems("BBB_XCV_IRDPFC_ANS"))
                .body("signatures[0].errors.content", Matchers.hasItems("No revocation data for the certificate"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("TRY_LATER"))
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
    @Test
    public void bdocSignersCertNotTrusted() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("SS-4_teadmataCA.4.asice"))
                .then()
                .body("signatures[0].errors.nameId", Matchers.hasItems("BBB_XCV_CCCBB_ANS"))
                .body("signatures[0].errors.content", Matchers.hasItems("The certificate chain is not trusted, there is no trusted anchor."))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("NO_CERTIFICATE_CHAIN_FOUND"))
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
    @Test
    public void bdocTmOcspStatusRevoked() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-15_revoked.4.asice"))
                .then()
                .body("signatures[0].errors.nameId", Matchers.hasItems("BBB_XCV_ISCR_ANS"))
                .body("signatures[0].errors.content", Matchers.hasItems("The certificate is revoked!"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("REVOKED_NO_POE"))
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
                .body("signatures[0].errors.nameId", Matchers.hasItems(""))
                .body("signatures[0].errors.content", Matchers.hasItems(""))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is(""))
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
    @Test
    public void bdocSignedFileRemoved() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("KS-21_fileeemaldatud.4.asice"))
                .then()
                .body("signatures[0].errors.nameId", Matchers.hasItems("BBB_CV_IRDOF_ANS"))
                .body("signatures[0].errors.content", Matchers.hasItems("The reference data object(s) not found!"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("SIGNED_DATA_NOT_FOUND"))
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
    @Test @Ignore //TODO: this file gives two errors, need a good solution to check them both in same time.
    public void bdocNoFilesInContainer() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("KS-02_tyhi.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "KS-02_tyhi.bdoc", "bdoc"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(MAY_NOT_BE_EMPTY));
    }
    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
