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
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Ignore("5.2 version failure")
@Category(IntegrationTest.class)
public class DocumentValidationIT extends SiVaRestTests{

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
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title:Bdoc with two signatures and one unsigned document.
     *
     * Expected Result: The document should fail the validation and warnings should be displayed.
     *
     * File:3f_2s_1f_unsigned.bdoc
     */
    @Test
    public void bdocWithOneUnsignedDocumentShouldFail() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("3f_2s_1f_unsigned.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "3f_2s_1f_unsigned.bdoc","POLv3"))
                .then()
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The reference data object(s) is not intact!"))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[1].subIndication", Matchers.is("HASH_FAILURE"))
                .body("validationReport.validationConclusion.signatures[1].errors.content", Matchers.hasItems("The reference data object(s) is not intact!"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file document_3.xml with mimetype application/octet-stream but the signature file for signature S0 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file document_3.xml with mimetype application/octet-stream but the signature file for signature S1 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Container contains a file named document_3.xml which is not found in the signature file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: document_3.xml"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Signature PUDOV,VADIM,39101013724 has unsigned files: document_3.xml"));

    }
    /**
     * TestCaseID: Document-Validation-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Bdoc with two signatures and one document signed by only one signature.
     *
     * Expected Result: The document should fail the validation and warnings should be displayed.
     *
     * File:3f_2s_1partly_signed.bdoc
     */
    @Test
    public void bdocWithDocumentWithOneSignatureShouldFail() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("3f_2s_1partly_signed.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "3f_2s_1partly_signed.bdoc","POLv3"))
                .then()
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The reference data object(s) is not intact!"))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file document_3.xml with mimetype application/octet-stream but the signature file for signature S0 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: document_3.xml"));

    }

    /**
     * TestCaseID: Document-Validation-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Bdoc with two signatures and two documents signed by only one signature.
     *
     * Expected Result: The document should fail the validation and warnings should be displayed.
     *
     * File:3f_2s_2partly_signed.bdoc
     */

    @Test
    public void bdocWithNonOverlapingSignaturesShouldFail() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("3f_2s_2partly_signed.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "3f_2s_2partly_signed.bdoc","POLv3"))
                .then()
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The reference data object(s) is not intact!"))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The reference data object(s) is not intact!"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file document_3.xml with mimetype application/octet-stream but the signature file for signature S0 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file document_2.docx with mimetype application/octet-stream but the signature file for signature S1 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Signature PUDOV,VADIM,39101013724 has unsigned files: document_2.docx"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: document_3.xml"));

    }
    /**
     * TestCaseID: Document-Validation-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Bdoc with two signatures, one unsigned and two partly signed documents.
     *
     * Expected Result: The document should fail the validation and warnings should be displayed.
     *
     * File:4f_2s_all_combinations.bdoc
     */

    @Test
    public void bdocWithNonOverlapingSignaturesAndOneUnsignedDocumentShouldFail() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("4f_2s_all_combinations.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "4f_2s_all_combinations.bdoc", "POLv3"))
                .then()
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The reference data object(s) is not intact!"))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The reference data object(s) is not intact!"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file unsigned.txt with mimetype application/octet-stream but the signature file for signature S0 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file document_3.xml with mimetype application/octet-stream but the signature file for signature S0 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file document_2.docx with mimetype application/octet-stream but the signature file for signature S1 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file unsigned.txt with mimetype application/octet-stream but the signature file for signature S1 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Container contains a file named document_2.docx which is not found in the signature file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Container contains a file named unsigned.txt which is not found in the signature file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Signature PUDOV,VADIM,39101013724 has unsigned files: document_2.docx, unsigned.txt"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: document_3.xml, unsigned.txt"));

    }

    /**
     * TestCaseID: Document-Validation-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Bdoc with three unsigned documents.
     *
     * Expected Result: The document should pass the validation and warnings should be displayed.
     *
     * File:6f_2s_3unsigned.bdoc
     */

    @Test  //TODO Should be re-evaluated when https://github.com/open-eid/SiVa/issues/18 is fixed
    public void bdocWithThreeUnsignedDocumentShouldPass() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("6f_2s_3unsigned.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "6f_2s_3unsigned.bdoc", "POLv3"))
                .then()
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(2))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file unsigned.txt with mimetype application/octet-stream but the signature file for signature S0 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file unsigned2.txt with mimetype application/octet-stream but the signature file for signature S0 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file unsigned3.txt with mimetype application/octet-stream but the signature file for signature S0 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file unsigned.txt with mimetype application/octet-stream but the signature file for signature S1 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file unsigned2.txt with mimetype application/octet-stream but the signature file for signature S1 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file unsigned3.txt with mimetype application/octet-stream but the signature file for signature S1 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Container contains a file named unsigned.txt which is not found in the signature file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Container contains a file named unsigned2.txt which is not found in the signature file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Container contains a file named unsigned3.txt which is not found in the signature file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Signature PUDOV,VADIM,39101013724 has unsigned files: unsigned.txt, unsigned2.txt, unsigned3.txt"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: unsigned.txt, unsigned2.txt, unsigned3.txt"));

    }

    /**
     * TestCaseID: Document-Validation-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Bdoc with deleted document, named in manifest.
     *
     * Expected Result: The document should fail the validation and warning should be displayed.
     *
     * File:2f_2signed_1f_deleted.bdoc
     */
    @Test
    public void bdocWithDeletedDocumentNamedInManifestShouldFail() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("2f_2signed_1f_deleted.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "2f_2signed_1f_deleted.bdoc", "POLv3"))
                .then()
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIGNED_DATA_NOT_FOUND"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The reference data object(s) is not found!"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file Test document.pdf with mimetype application/octet-stream but the signature file for signature S0 does not have an entry for this file"))
        ;

    }
    /**
     * TestCaseID: Document-Validation-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Bdoc with deleted document, also removed from manifest.
     *
     * Expected Result: The document should fail the validation without warning.
     *
     * File:2f_2signed_1f_totally_removed.bdoc
     */
    @Test
    public void bdocWithRemovedDocumentDeletedFromManifestShouldFail() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("2f_2signed_1f_totally_removed.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "2f_2signed_1f_totally_removed.bdoc", "POLv3"))
                .then()
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIGNED_DATA_NOT_FOUND"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The reference data object(s) is not found!"))
                .body("validationReport.validationConclusion.validationWarnings", Matchers.isEmptyOrNullString());

    }


    /**
     * TestCaseID: Document-Validation-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Bdoc with one unsigned document, named in manifest.
     *
     * Expected Result: The document should fail the validation and warnings should be displayed.
     *
     * File:3f_2signed_1unsigned_all_in_manifest.bdoc
     */

    @Test  // TODO Should be re-evaluated when https://github.com/open-eid/SiVa/issues/18 is fixed
    public void bdocWithOneUnsignedDocumentNamedInManifestShouldPass() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("3f_2signed_1unsigned_all_in_manifest.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "3f_2signed_1unsigned_all_in_manifest.bdoc", "POLv3"))
                .then()
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Manifest file has an entry for file Test_1703.pdf with mimetype application/octet-stream but the signature file for signature S0 does not have an entry for this file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Container contains a file named Test_1703.pdf which is not found in the signature file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: Test_1703.pdf"));

    }
    /**
     * TestCaseID: Document-Validation-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Bdoc with one unsigned document, NOT named in manifest.
     *
     * Expected Result: The document should fail the validation and warnings should be displayed.
     *
     * File:3f_2signed_1unsigned_2in_manifest.bdoc
     */

    @Test // TODO  Should be re-evaluated when https://github.com/open-eid/SiVa/issues/18 is fixed
    public void bdocWithOneUnsignedDocumentNotNamedInManifestShouldPass() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("3f_2signed_1unsigned_2in_manifest.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "33f_2signed_1unsigned_2in_manifest.bdoc", "POLv3"))
                .then()
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Container contains a file named Test_1703.pdf which is not found in the signature file"))
                .body("validationReport.validationConclusion.validationWarnings.content", Matchers.hasItems("Signature SOLOVEI,JULIA,47711040261 has unsigned files: Test_1703.pdf"));

    }
    /**
     * TestCaseID: Document-Validation-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Bdoc with signed documents.
     *
     * Expected Result: The document should pass the validation without warning.
     *
     * File:2f_all_signed.bdoc
     */
    @Test
    public void bdocWithAllSignedDocumentsShouldPass() {
        setTestFilesDirectory("document_validation_test_files/bdoc/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("2f_all_signed.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "2f_all_signed.bdoc", "POLv3"))
                .then()
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validationWarnings", Matchers.isEmptyOrNullString());

    }



    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
