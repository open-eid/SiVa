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
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.openeid.siva.integrationtest.TestData.*;

@Category(IntegrationTest.class)
public class DdocValidationPassIT extends SiVaRestTests {

    private static final String TEST_FILES_DIRECTORY = "ddoc/live/timemark/";

    private String testFilesDirectory = TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Ddoc v1.0 with valid signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: SK-XML1.0.ddoc
     */
    @Test
    public void ddocValidMultipleSignaturesV1_0() {
        post(validationRequestFor("SK-XML1.0.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_10))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_SK_XML))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: SK-XML version: 1.0"))
                .body("signatures[0].warnings.size()", Matchers.is(1))
                .body("signatures[1].signatureFormat", Matchers.is(SIGNATURE_FORMAT_SK_XML))
                .body("signatures[1].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[1].warnings[0].content", Matchers.is("Old and unsupported format: SK-XML version: 1.0"))
                .body("signatures[1].warnings.size()", Matchers.is(1))
                .body("signaturesCount", Matchers.is(2))
                .body("validSignaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Ddoc v1.1 with valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: DIGIDOC-XML1.1.ddoc
     */
    @Test
    public void ddocValidSignatureV1_1() {
        post(validationRequestFor("DIGIDOC-XML1.1.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_11))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_11))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.1"))
                .body("signatures[0].warnings.size()", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Ddoc v1.2 with valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: DIGIDOC-XML1.2.ddoc
     */
    @Test
    public void ddocValidSignatureV1_2() {
        post(validationRequestFor("DIGIDOC-XML1.2.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_12))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_12))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.2"))
                .body("signatures[0].warnings.size()", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Ddoc v1.3 with valid signature with ESTEID-SK 2011 certificate chain
     *
     * Expected Result: The document should pass the validation
     *
     * File: DIGIDOC-XML1.3.ddoc
     */
    @Test
    public void ddocValidSignatureV1_3() {
        post(validationRequestFor("DIGIDOC-XML1.3.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Ddoc v1.3 with valid signature, signed data file name has special characters and ESTEID-SK certificate chain
     *
     * Expected Result: The document should pass the validation
     *
     * File: susisevad1_3.ddoc
     */
    @Test
    public void ddocSpecialCharactersInDataFileValidSignature() {
        post(validationRequestFor("susisevad1_3.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Ddoc v1.3 KLASS3-SK certificate chain with valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: KLASS3-SK _ KLASS3-SK OCSP RESPONDER uus.ddoc
     */
    @Test
    public void ddocKlass3SkCertificateChainValidSignature() {
        post(validationRequestFor("KLASS3-SK _ KLASS3-SK OCSP RESPONDER uus.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("KLASS3-SK OCSP RESPONDER"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Ddoc v1.3 KLASS3-SK 2010 certificate chain with valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: KLASS3-SK 2010 _ KLASS3-SK 2010 OCSP RESPONDER.ddoc
     */
    @Test
    public void ddocKlass3Sk2010CertificateChainValidSignature() {
        post(validationRequestFor("KLASS3-SK 2010 _ KLASS3-SK 2010 OCSP RESPONDER.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("KLASS3-SK 2010 OCSP RESPONDER"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Ddoc v1.1 ESTEID-SK 2007 certificate chain with valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: vaikesed1.1.ddoc
     */
    @Test
    public void ddocEsteidSk2007CertificateChainValidSignature() {
        post(validationRequestFor("vaikesed1.1.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_11))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_11))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.1"))
                .body("signatures[0].warnings.size()", Matchers.is(1))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("ESTEID-SK 2007 OCSP RESPONDER"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Ddoc v1.1 ESTEID-SK 2007 certificate chain with valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: IB-4270_ESTEID-SK 2015  SK OCSP RESPONDER 2011.ddoc
     */
    @Test
    public void ddocEsteidSk2015CertificateChainValidSignature() {
        post(validationRequestFor("IB-4270_ESTEID-SK 2015  SK OCSP RESPONDER 2011.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("SK OCSP RESPONDER 2011"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Ddoc v1.1 ESTEID-SK certificate chain with valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: EID-SK _ EID-SK OCSP RESPONDER.ddoc
     */
    @Test
    public void ddocEsteidSkCertificateChainValidSignature() {
        post(validationRequestFor("EID-SK _ EID-SK OCSP RESPONDER.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_11))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_11))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.1"))
                .body("signatures[0].warnings.size()", Matchers.is(1))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("EID-SK OCSP RESPONDER"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-12
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Ddoc v1.1 ESTEID-SK 2007 and OCSP 2010 certificate chain with valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: EID-SK 2007 _ EID-SK 2007 OCSP RESPONDER 2010.ddoc
     */
    @Test
    public void ddocEsteidSk2007Ocsp2010CertificateChainValidSignature() {
        post(validationRequestFor("EID-SK 2007 _ EID-SK 2007 OCSP RESPONDER 2010.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[1].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[1].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[1].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("EID-SK 2007 OCSP RESPONDER 2010"))
                .body("signaturesCount", Matchers.is(2))
                .body("validSignaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-13
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Ddoc v1.3 ESTEID-SK 2007 and OCSP 2007 certificate chain with valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: EID-SK 2007 _ EID-SK 2007 OCSP RESPONDER.ddoc
     */
    @Test
    public void ddocEsteidSk2007Ocsp2007CertificateChainValidSignature() {
        post(validationRequestFor("EID-SK 2007 _ EID-SK 2007 OCSP RESPONDER.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("EID-SK 2007 OCSP RESPONDER"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-14
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Ddoc v1.3 ESTEID-SK 2011 and OCSP 2011 certificate chain with valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: EID-SK 2011 _ SK OCSP RESPONDER 2011.ddoc
     */
    @Test
    public void ddocEsteidSk2011Ocsp2011CertificateChainValidSignature() {
        post(validationRequestFor("EID-SK 2011 _ SK OCSP RESPONDER 2011.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].warnings[0].content", Matchers.is("X509IssuerName has none or invalid namespace: null"))
                .body("signatures[0].warnings[1].content", Matchers.is("X509SerialNumber has none or invalid namespace: null"))
                .body("signatures[0].warnings.size()", Matchers.is(2))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("SK OCSP RESPONDER 2011"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-15
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc with warning should pass
     *
     * Expected Result: Document passes the validation
     *
     * File: 18912.ddoc
     */
    @Test
    public void ddocWithWarningShouldPass() {
        post(validationRequestFor("18912.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[0].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("36706020210"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[0].name", Matchers.is("readme"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2012-09-21T11:56:53Z"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2012-09-21T11:56:55Z"))
                .body("signatures[0].warnings[0].content", Matchers.is("Bad digest for DataFile: D0 alternate digest matches!"))
                .body("signatures[0].warnings.size()", Matchers.is(1))
                .body("validatedDocument.filename", Matchers.is("18912.ddoc"))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING))
                .body("validationWarnings[1].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationPass-16
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Ddoc with no signatures
     *
     * Expected Result: Document passes the validation
     *
     * File: DdocContainerNoSignature.ddoc
     */
    @Test
    public void ddocNoSignatures() {
        setTestFilesDirectory("document_format_test_files/");
        post(validationRequestFor("DdocContainerNoSignature.ddoc", VALID_SIGNATURE_POLICY_4, null))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13))
                .body("validatedDocument.filename", Matchers.is("DdocContainerNoSignature.ddoc"))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING))
                .body("validationWarnings[1].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: DdocHashcode-ValidationPass-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Validation of DDOC Hashcode v1.0
     *
     * Expected Result: Document passes the validation
     *
     * File: SK-XML1.0_hashcode.ddoc
     */
    @Test
    public void ddocV1_0HashcodeShouldPass() {
        post(validationRequestFor("SK-XML1_0_hashcode.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_10_HASHCODE))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_SK_XML))
                .body("signatures[0].signedBy", Matchers.is("ANSIP,ANDRUS,35610012722"))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[0].name", Matchers.is("Tartu ja Tallinna koostooleping.doc"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2002-10-07T12:10:19Z"))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: SK-XML version: 1.0"))
                .body("signatures[0].warnings.size()", Matchers.is(1))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2002-10-07T11:10:47Z"))
                .body("validatedDocument.filename", Matchers.is("SK-XML1_0_hashcode.ddoc"))
                .body("signaturesCount", Matchers.is(2))
                .body("validSignaturesCount", Matchers.is(2))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING))
                .body("validationWarnings[1].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"));
    }

    /**
     * TestCaseID: DdocHashcode-ValidationPass-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Validation of DDOC Hashcode v1.1
     *
     * Expected Result: Document passes the validation
     *
     * File: DIGIDOC-XML1.1_hashcode.ddoc
     */
    @Test
    public void ddocV1_1HashcodeShouldPass() {
        post(validationRequestFor("DIGIDOC-XML1.1_hashcode.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_11_HASHCODE))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORM_DDOC_11))
                .body("signatures[0].signedBy", Matchers.is("KESKEL,URMO,38002240232"))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[0].name", Matchers.is("puhkus_urmo_062006.doc"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2006-06-26T12:15:40Z"))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.1"))
                .body("signatures[0].warnings.size()", Matchers.is(1))
                .body("validatedDocument.filename", Matchers.is("DIGIDOC-XML1.1_hashcode.ddoc"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: DdocHashcode-ValidationPass-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Validation of DDOC Hashcode v1.2
     *
     * Expected Result: Document passes the validation
     *
     * File: DIGIDOC-XML1.2_hashcode.ddoc
     */

    @Test
    public void ddocV1_2HashcodeShouldPass() {
        post(validationRequestFor("DIGIDOC-XML1.2_hashcode.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_12_HASHCODE))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_12))
                .body("signatures[0].signatureLevel", Matchers.emptyOrNullString())
                .body("signatures[0].signedBy", Matchers.is("Eesti Ühispank: Ülekandejuhise kinnitus"))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].subIndication", Matchers.emptyOrNullString())
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[0].name", Matchers.is("RO219559508.pdf"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2003-10-24T10:57:19Z"))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.2"))
                .body("signatures[0].warnings.size()", Matchers.is(1))
                .body("validatedDocument.filename", Matchers.is("DIGIDOC-XML1.2_hashcode.ddoc"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: DdocHashcode-ValidationPass-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Validation of DDOC Hashcode v1.3
     *
     * Expected Result: Document passes the validation
     *
     * File: DIGIDOC-XML1.3_hashcode.ddoc
     */
    @Test
    public void ddocV1_3HashcodeShouldPass() {
        post(validationRequestFor("DIGIDOC-XML1.3_hashcode.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_DDOC_13_HASHCODE))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_DIGIDOC_XML_13))
                .body("signatures[0].signatureLevel", Matchers.emptyOrNullString())
                .body("signatures[0].signedBy", Matchers.is("LUKIN,LIISA,47710110274"))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].subIndication", Matchers.emptyOrNullString())
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[0].name", Matchers.is("Glitter-rock-4_gallery.jpg"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2012-10-03T07:46:31Z"))
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("validatedDocument.filename", Matchers.is("DIGIDOC-XML1.3_hashcode.ddoc"))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING))
                .body("validationWarnings[1].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
