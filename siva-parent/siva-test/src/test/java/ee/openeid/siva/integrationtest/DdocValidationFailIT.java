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

import ee.openeid.siva.common.Constants;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;

import static ee.openeid.siva.integrationtest.TestData.*;

@Category(IntegrationTest.class)
public class DdocValidationFailIT extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "ddoc/live/timemark/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc with single invalid signature
     *
     * Expected Result: The document should fail the validation
     *
     * File: AndmefailiAtribuudidMuudetud.ddoc
     */
    @Test
    public void ddocInvalidSignature() {
        post(validationRequestFor("AndmefailiAtribuudidMuudetud.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc with multiple invalid signatures
     *
     * Expected Result: The document should fail the validation
     *
     * File: multipleInvalidSignatures.ddoc
     */
    @Test
    public void ddocInvalidMultipleSignatures() {
        post(validationRequestFor("multipleInvalidSignatures.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_11))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_11))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors[0].content", Matchers.is("Bad digest for DataFile: D2"))
                .body("signatures[0].errors[1].content", Matchers.is("Invalid signature value!"))
                .body("signatures[0].errors.size()", Matchers.is(2))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.1"))
                .body("signatures[0].warnings.size()", Matchers.is(1))
                .body("signatures[1].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_11))
                .body("signatures[1].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[2].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_11))
                .body("signatures[2].indication", Matchers.is(TOTAL_FAILED))
                .body("signaturesCount", Matchers.is(3))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc with multiple signatures both valid and invalid
     *
     * Expected Result: The document should fail the validation
     *
     * File: multipleValidAndInvalidSignatures.ddoc
     */
    @Test
    public void ddocInvalidAndValidMultipleSignatures() {
        post(validationRequestFor("multipleValidAndInvalidSignatures.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_11))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_11))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors[0].content", Matchers.is("Bad digest for DataFile: D11"))
                .body("signatures[0].errors[1].content", Matchers.is("Invalid signature value!"))
                .body("signatures[0].errors.size()", Matchers.is(2))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.1"))
                .body("signatures[0].warnings.size()", Matchers.is(1))
                .body("signatures[1].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_11))
                .body("signatures[1].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[1].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.1"))
                .body("signatures[1].warnings.size()", Matchers.is(1))
                .body("signatures[2].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_11))
                .body("signatures[2].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(3))
                .body("validSignaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc signature value has been changed (SignatureValue does not correspond to the SignedInfo block)
     *
     * Expected Result: The document should fail the validation
     *
     * File: test-inv-sig-inf.ddoc
     */
    @Test
    public void ddocSignatureValueChanged() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("test-inv-sig-inf.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].errors[0].content", Matchers.containsString("Invalid signature value!"))
                .body("signatures[0].errors.size()", Matchers.is(1))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2012-09-19T06:28:55Z"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.notNullValue())
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.notNullValue())
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING))
                .body("validationWarnings[1].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc Data file(s) don't match the hash values in Reference elements
     *
     * Expected Result: The document should fail the validation
     *
     * File: AndmefailiAtribuudidMuudetud.ddoc
     */
    @Test
    public void ddocDataFileHashMismatch() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("AndmefailiAtribuudidMuudetud.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].errors[0].content", Matchers.containsString("Bad digest for DataFile: D0"))
                .body("signatures[0].errors.size()", Matchers.is(1))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING))
                .body("validationWarnings[1].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc Baseline-BES file, no OCSP response
     *
     * Expected Result: The document should fail the validation
     *
     * File: ilma_kehtivuskinnituseta.ddoc
     */
    @Test
    public void ddocNoOCSPResponse() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("ilma_kehtivuskinnituseta.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_12))
                .body("signatures[0].errors.content", Matchers.hasItems("Signature has no OCSP confirmation!"))
                .body("signatures[0].errors.size()", Matchers.is(1))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.2"))
                .body("signatures[0].warnings.size()", Matchers.is(1))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING))
                .body("validationWarnings[1].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc no non-repudiation key usage value in the certificate
     *
     * Expected Result: The document should fail the validation
     *
     * File: test-non-repu1.ddoc
     */
    @Test
    public void ddocNoNonRepudiationKey() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("test-non-repu1.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].errors.content", Matchers.hasItems("Signers cert does not have non-repudiation bit set!"))
                .body("signatures[0].errors.size()", Matchers.is(1))
                .body("signatures[0].warnings.content", Matchers.hasItems("X509IssuerName has none or invalid namespace: null", "X509SerialNumber has none or invalid namespace: null"))
                .body("signatures[0].warnings.size()", Matchers.is(2))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc Signer's certificate is not trusted
     *
     * Expected Result: The document should fail the validation
     *
     * File: Belgia_kandeavaldus_LIV.ddoc
     */
    @Test
    public void ddocSignersCertNotTrusted() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("Belgia_kandeavaldus_LIV.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[1].errors.content", Matchers.hasItems("Signers cert not trusted, missing CA cert!", "Signing certificate issuer information does not match"))
                .body("signatures[1].errors.size()", Matchers.is(3))
                .body("signatures[1].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[1].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("Guy Ramlot (Signature)"))
                .body("signatures[1].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIID5DCCAsygAwIBAgIQEAAAAAAA6b6vobxT/DKUOzANBgkqhk"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc OCSP certificate is not trusted
     *
     * Expected Result: The document should fail the validation
     *
     * File: Tundmatu_OCSP_responder.ddoc
     */
    @Test
    public void ddocOCSPNotTrusted() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("Tundmatu_OCSP_responder.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].errors.content", Matchers.hasItems("Signers cert not trusted, missing CA cert!", "Signing certificate issuer information does not match"))
                .body("signatures[0].errors.size()", Matchers.is(3))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("Belgium OCSP Responder"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content",  Matchers.startsWith("MIIDTTCCAjWgAwIBAgILBAAAAAABGTkSVnEwDQYJKoZIhvcNAQ"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc has unsigned data files in the container
     *
     * Expected Result: The document should fail the validation
     *
     * File: lisatud_andmefail.ddoc
     */
    @Test
    public void ddocNonSignedFile() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("DIGIDOC-XML1.3_lisatud_andmefail.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].errors.content", Matchers.hasItems("Missing Reference for file: testfail2.txt"))
                .body("signatures[0].errors.size()", Matchers.is(1))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc signed data file has been removed from the container
     *
     * Expected Result: The document should fail the validation
     *
     * File: faileemald1.ddoc
     */
    @Test
    public void ddocFileRemoved() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("faileemald1.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].errors.content", Matchers.hasItems("Missing DataFile for signature: S0 reference #D0"))
                .body("signatures[0].errors.size()", Matchers.is(1))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-12
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc wrong nonce
     *
     * Expected Result: The document should fail the validation
     *
     * File: OCSP nonce vale.ddoc
     */
    @Test
    public void ddocWrongOcspNonce() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("OCSP nonce vale.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].errors.content", Matchers.hasItems("Notarys digest doesn't match!", "OCSP response's nonce doesn't match the requests nonce!"))
                .body("signatures[0].errors.size()", Matchers.is(2))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("validSignaturesCount", Matchers.is(0));    }

    /**
     * TestCaseID: Ddoc-ValidationFail-13
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/
     *
     * Title: Ddoc with XML Entity expansion attack
     *
     * Expected Result: The document should fail the validation with error
     *
     * File: xml_expansion.ddoc
     */
    @Test
    public void ddocWithXMLEntityExpansionAttackShouldFail() {
        setTestFilesDirectory("ddoc/test/timemark/");
        post(validationRequestFor("xml_expansion.ddoc"))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-14
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/
     *
     * Title: Ddoc with XML server side request forgery attack
     *
     * Expected Result: The document should fail the validation with error
     *
     * File: xml_entity.ddoc
     */
    @Test
    public void ddocWithXMLServerSideRequestForgeryAttackShouldFail() {
        setTestFilesDirectory("ddoc/test/timemark/");
        post(validationRequestFor("xml_entity.ddoc"))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-15
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc no files in container
     *
     * Expected Result: The document should fail the validation
     *
     * File: KS-02_tyhi.ddoc
     */
    @Test
    public void ddocNoFilesInContainer() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("KS-02_tyhi.ddoc"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors.message", Matchers.hasItems("Document is not encoded in a valid base64 string", "may not be empty"))
                .body("requestErrors", Matchers.hasSize(2));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-16
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc with invalid datafile id
     *
     * Expected Result: The document should fail the validation
     *
     * File: 22915-bad-df-id.ddoc
     */
    @Test
    public void ddocBadDatafileId() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("22915-bad-df-id.ddoc", VALID_SIGNATURE_POLICY_4, null))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[0].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItems("Id attribute value has to be in form D<number> or DO"))
                .body("signatures[0].errors.size()", Matchers.is(1))
                .body("signatures[0].warnings.content", Matchers.hasItems("X509IssuerName has none or invalid namespace: null", "X509SerialNumber has none or invalid namespace: null"))
                .body("signatures[0].warnings.size()", Matchers.is(2))
                .body("signatures[0].signatureScopes[0].name", Matchers.is("build.xml"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2013-05-09T18:15:42Z"))
                .body("validatedDocument.filename", Matchers.is("22915-bad-df-id.ddoc"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING))
                .body("validationWarnings[1].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-17
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Validation of DDOC with revoked certificate status
     *
     * Expected Result: The document should fail the validation
     *
     * File: cert-revoked.ddoc
     */
    @Test
    public void ddocWithRevokedCertificateStatusFromOcspShouldFail() {
        post(validationRequestFor("cert-revoked.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[0].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItems("Certificate has been revoked!"))
                .body("signatures[0].errors.size()", Matchers.is(1))
                .body("signatures[0].warnings.content", Matchers.hasItems("X509IssuerName has none or invalid namespace: null", "X509SerialNumber has none or invalid namespace: null"))
                .body("signatures[0].warnings.size()", Matchers.is(2))
                .body("signatures[0].signatureScopes[0].name", Matchers.is("build.xml"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2013-05-17T12:15:08Z"))
                .body("validatedDocument.filename", Matchers.is("cert-revoked.ddoc"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING))
                .body("validationWarnings[1].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-18
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Validation of DDOC with unknown OCSP status
     *
     * Expected Result: The document should fail the validation
     *
     * File: cert-unknown.ddoc
     */
    @Test
    public void ddocWithUnknownCertificateStatusFromOcspShouldFail() {
        post(validationRequestFor("cert-unknown.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[0].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItems("Certificate status is unknown!"))
                .body("signatures[0].errors.size()", Matchers.is(1))
                .body("signatures[0].warnings.content", Matchers.hasItems("X509IssuerName has none or invalid namespace: null", "X509SerialNumber has none or invalid namespace: null"))
                .body("signatures[0].warnings.size()", Matchers.is(2))
                .body("signatures[0].signatureScopes[0].name", Matchers.is("build.xml"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2013-05-17T12:20:18Z"))
                .body("validatedDocument.filename", Matchers.is("cert-unknown.ddoc"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING))
                .body("validationWarnings[1].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

}
