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
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.openeid.siva.integrationtest.TestData.VALIDATION_CONCLUSION_PREFIX;


@Category(IntegrationTest.class)
public class DocumentValidationIT extends SiVaRestTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_validation_test_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: Document-Validation-1
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title:Bdoc with two signatures and one unsigned document.
     * <p>
     * Expected Result: The document should fail the validation and warnings should be displayed.
     * <p>
     * File:3f_2s_1f_unsigned.bdoc
     */
    @Test
    public void bdocWithOneUnsignedDocumentShouldFail() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("3f_2s_1f_unsigned.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "3f_2s_1f_unsigned.bdoc", "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signaturesCount", Matchers.is(2))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("signatures[0].errors.content", Matchers.hasItems("Manifest file has an entry for file <document_3.xml> with mimetype <application/octet-stream> but the signature file for signature S0 does not have an entry for this file"))
                .body("signatures[0].errors.content", Matchers.hasItems("Manifest file has an entry for file <document_3.xml> with mimetype <application/octet-stream> but the signature file for signature S1 does not have an entry for this file"))
                .body("signatures[0].errors.content", Matchers.hasItems("Container contains a file named <document_3.xml> which is not found in the signature file"))
                .body("signatures[0].warnings.content", Matchers.hasItems("The signature/seal is not a valid AdES digital signature!"))
                .body("signatures[0].warnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: document_3.xml"))
                .body("signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[1].subIndication", Matchers.is("HASH_FAILURE"))
                .body("signatures[1].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("signatures[1].errors.content", Matchers.hasItems("Manifest file has an entry for file <document_3.xml> with mimetype <application/octet-stream> but the signature file for signature S0 does not have an entry for this file"))
                .body("signatures[1].errors.content", Matchers.hasItems("Manifest file has an entry for file <document_3.xml> with mimetype <application/octet-stream> but the signature file for signature S1 does not have an entry for this file"))
                .body("signatures[1].errors.content", Matchers.hasItems("Container contains a file named <document_3.xml> which is not found in the signature file"))
                .body("signatures[1].warnings.content", Matchers.hasItems("The signature/seal is not a valid AdES digital signature!"))
                .body("signatures[1].warnings.content", Matchers.hasItems("Signature PUDOV,VADIM,39101013724 has unsigned files: document_3.xml"));

    }

    /**
     * TestCaseID: Document-Validation-2
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Bdoc with two signatures and one document signed by only one signature.
     * <p>
     * Expected Result: The document should fail the validation and warnings should be displayed.
     * <p>
     * File:3f_2s_1partly_signed.bdoc
     */
    @Test
    public void bdocWithDocumentWithOneSignatureShouldFail() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("3f_2s_1partly_signed.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "3f_2s_1partly_signed.bdoc", "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signaturesCount", Matchers.is(2))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("signatures[0].errors.content", Matchers.hasItems("Manifest file has an entry for file <document_3.xml> with mimetype <application/octet-stream> but the signature file for signature S0 does not have an entry for this file"))
                .body("signatures[0].warnings.content", Matchers.hasItems("The signature/seal is not a valid AdES digital signature!"))
                .body("signatures[0].warnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: document_3.xml"))
                .body("signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[1].errors.content", Matchers.hasItems("Manifest file has an entry for file <document_3.xml> with mimetype <application/octet-stream> but the signature file for signature S0 does not have an entry for this file"));
    }

    /**
     * TestCaseID: Document-Validation-3
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Bdoc with two signatures and two documents signed by only one signature.
     * <p>
     * Expected Result: The document should fail the validation and warnings should be displayed.
     * <p>
     * File:3f_2s_2partly_signed.bdoc
     */

    @Test
    public void bdocWithNonOverlapingSignaturesShouldFail() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("3f_2s_2partly_signed.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "3f_2s_2partly_signed.bdoc", "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signaturesCount", Matchers.is(2))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("signatures[0].warnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: document_3.xml"))
                .body("signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[1].warnings.content", Matchers.hasItems("Signature PUDOV,VADIM,39101013724 has unsigned files: document_2.docx"));

    }

    /**
     * TestCaseID: Document-Validation-4
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Bdoc with two signatures, one unsigned and two partly signed documents.
     * <p>
     * Expected Result: The document should fail the validation and warnings should be displayed.
     * <p>
     * File:4f_2s_all_combinations.bdoc
     */

    @Test
    public void bdocWithNonOverlapingSignaturesAndOneUnsignedDocumentShouldFail() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("4f_2s_all_combinations.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "4f_2s_all_combinations.bdoc", "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signaturesCount", Matchers.is(2))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("signatures[0].errors.content", Matchers.hasItems("Manifest file has an entry for file <unsigned.txt> with mimetype <application/octet-stream> but the signature file for signature S0 does not have an entry for this file"))
                .body("signatures[0].errors.content", Matchers.hasItems("Manifest file has an entry for file <document_3.xml> with mimetype <application/octet-stream> but the signature file for signature S0 does not have an entry for this file"))
                .body("signatures[0].errors.content", Matchers.hasItems("Manifest file has an entry for file <document_2.docx> with mimetype <application/octet-stream> but the signature file for signature S1 does not have an entry for this file"))
                .body("signatures[0].errors.content", Matchers.hasItems("Manifest file has an entry for file <unsigned.txt> with mimetype <application/octet-stream> but the signature file for signature S1 does not have an entry for this file"))
                .body("signatures[0].errors.content", Matchers.hasItems("Container contains a file named <document_2.docx> which is not found in the signature file"))
                .body("signatures[0].errors.content", Matchers.hasItems("Container contains a file named <unsigned.txt> which is not found in the signature file"))
                .body("signatures[0].warnings.content", Matchers.hasItems("The signature/seal is not a valid AdES digital signature!"))
                .body("signatures[0].warnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: document_3.xml, unsigned.txt"))
                .body("signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[1].subIndication", Matchers.is("HASH_FAILURE"));

    }

    /**
     * TestCaseID: Document-Validation-5
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Bdoc with three unsigned documents.
     * <p>
     * Expected Result: The document should pass the validation and warnings should be displayed.
     * <p>
     * File:6f_2s_3unsigned.bdoc
     */

    @Test  //TODO Should be re-evaluated when https://github.com/open-eid/SiVa/issues/18 is fixed
    public void bdocWithThreeUnsignedDocumentShouldPass() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("6f_2s_3unsigned.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "6f_2s_3unsigned.bdoc", "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signaturesCount", Matchers.is(2))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[1].errors.content", Matchers.hasItems("Manifest file has an entry for file <unsigned.txt> with mimetype <application/octet-stream> but the signature file for signature S0 does not have an entry for this file"))
                .body("signatures[1].errors.content", Matchers.hasItems("Manifest file has an entry for file <unsigned2.txt> with mimetype <application/octet-stream> but the signature file for signature S0 does not have an entry for this file"))
                .body("signatures[1].errors.content", Matchers.hasItems("Manifest file has an entry for file <unsigned3.txt> with mimetype <application/octet-stream> but the signature file for signature S0 does not have an entry for this file"))
                .body("signatures[1].errors.content", Matchers.hasItems("Manifest file has an entry for file <unsigned.txt> with mimetype <application/octet-stream> but the signature file for signature S1 does not have an entry for this file"))
                .body("signatures[1].errors.content", Matchers.hasItems("Manifest file has an entry for file <unsigned2.txt> with mimetype <application/octet-stream> but the signature file for signature S1 does not have an entry for this file"))
                .body("signatures[1].errors.content", Matchers.hasItems("Manifest file has an entry for file <unsigned3.txt> with mimetype <application/octet-stream> but the signature file for signature S1 does not have an entry for this file"))
                .body("signatures[1].errors.content", Matchers.hasItems("Container contains a file named <unsigned.txt> which is not found in the signature file"))
                .body("signatures[1].errors.content", Matchers.hasItems("Container contains a file named <unsigned2.txt> which is not found in the signature file"))
                .body("signatures[1].errors.content", Matchers.hasItems("Container contains a file named <unsigned3.txt> which is not found in the signature file"))
                .body("signatures[1].warnings.content", Matchers.hasItems("Signature PUDOV,VADIM,39101013724 has unsigned files: unsigned.txt, unsigned2.txt, unsigned3.txt"))
                .body("signatures[0].warnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: unsigned.txt, unsigned2.txt, unsigned3.txt"));

    }

    /**
     * TestCaseID: Document-Validation-6
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Bdoc with deleted document, named in manifest.
     * <p>
     * Expected Result: The document should fail the validation and warning should be displayed.
     * <p>
     * File:2f_2signed_1f_deleted.bdoc
     */
    @Test
    public void bdocWithDeletedDocumentNamedInManifestShouldFail() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("2f_2signed_1f_deleted.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "2f_2signed_1f_deleted.bdoc", "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("SIGNED_DATA_NOT_FOUND"))
                .body("signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationWarnings", Matchers.hasSize(1))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING));
        ;

    }

    /**
     * TestCaseID: Document-Validation-7
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Bdoc with deleted document, also removed from manifest.
     * <p>
     * Expected Result: The document should fail the validation without warning.
     * <p>
     * File:2f_2signed_1f_totally_removed.bdoc
     */
    @Test
    public void bdocWithRemovedDocumentDeletedFromManifestShouldFail() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("2f_2signed_1f_totally_removed.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "2f_2signed_1f_totally_removed.bdoc", "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signatures[0].signatureLevel", Matchers.is("INDETERMINATE_QESIG"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("SIGNED_DATA_NOT_FOUND"))
                .body("signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("signatures[0].errors.content", Matchers.hasItems("The signature file for signature S0 has an entry for file <Test document.pdf> with mimetype <application/octet-stream> but the manifest file does not have an entry for this file"));

    }


    /**
     * TestCaseID: Document-Validation-8
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Bdoc with one unsigned document, named in manifest.
     * <p>
     * Expected Result: The document should fail the validation and warnings should be displayed.
     * <p>
     * File:3f_2signed_1unsigned_all_in_manifest.bdoc
     */

    @Test  // TODO Should be re-evaluated when https://github.com/open-eid/SiVa/issues/18 is fixed
    public void bdocWithOneUnsignedDocumentNamedInManifestShouldPass() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("3f_2signed_1unsigned_all_in_manifest.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "3f_2signed_1unsigned_all_in_manifest.bdoc", "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].errors.content", Matchers.hasItems("Manifest file has an entry for file <Test_1703.pdf> with mimetype <application/octet-stream> but the signature file for signature S0 does not have an entry for this file"))
                .body("signatures[0].errors.content", Matchers.hasItems("Container contains a file named <Test_1703.pdf> which is not found in the signature file"))
                .body("signatures[0].warnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: Test_1703.pdf"));

    }

    /**
     * TestCaseID: Document-Validation-9
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Bdoc with one unsigned document, NOT named in manifest.
     * <p>
     * Expected Result: The document should fail the validation and warning should be displayed.
     * <p>
     * File:3f_2signed_1unsigned_2in_manifest.bdoc
     */

    @Test // TODO  Should be re-evaluated when https://github.com/open-eid/SiVa/issues/18 is fixed
    public void bdocWithOneUnsignedDocumentNotNamedInManifestShouldPass() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("3f_2signed_1unsigned_2in_manifest.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "33f_2signed_1unsigned_2in_manifest.bdoc", "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].errors.content", Matchers.hasItems("Container contains a file named <Test_1703.pdf> which is not found in the signature file"))
                .body("signatures[0].warnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: Test_1703.pdf"));

    }

    /**
     * TestCaseID: Document-Validation-10
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Bdoc with signed documents.
     * <p>
     * Expected Result: The document should pass the validation without warning.
     * <p>
     * File:2f_all_signed.bdoc
     */
    @Test
    public void bdocWithAllSignedDocumentsShouldPass() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("2f_all_signed.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "2f_all_signed.bdoc", "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationWarnings", Matchers.hasSize(1))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING));

    }


    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
