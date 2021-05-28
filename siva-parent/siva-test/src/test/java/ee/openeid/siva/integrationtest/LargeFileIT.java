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

import static ee.openeid.siva.integrationtest.TestData.VALIDATION_CONCLUSION_PREFIX;
import static org.hamcrest.Matchers.equalTo;

@Category(IntegrationTest.class)
public class LargeFileIT extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "large_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: PDF-LargeFiles-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: 9MB PDF files (PAdES Baseline LT).
     *
     * Expected Result: Validation report is returned
     *
     * File: 9MB_PDF.pdf
     */
    @Test
    public void pdfNineMegabyteFilesWithLtSignatureAreAccepted() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("9MB_PDF.pdf"));
        post(validationRequestWithValidKeys(encodedString, "9MB_PDF.pdf", "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat",equalTo("PAdES_BASELINE_LT"))
                .body("validatedDocument.filename",equalTo("9MB_PDF.pdf"));
    }

    /**
     * TestCaseID: Bdoc-LargeFiles-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: 9MB ASIC-E file
     *
     * Expected Result: Validation report is returned
     *
     * File: 9MB_BDOC-TS.bdoc
     */
    @Test
    public void bdocTsNineMegabyteFilesValidSignatureAreAccepted() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("9MB_BDOC-TS.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "9MB_BDOC-TS.bdoc","POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat",equalTo("XAdES_BASELINE_LT"))
                .body("validatedDocument.filename",equalTo("9MB_BDOC-TS.bdoc"))
                .body("validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: Bdoc-LargeFiles-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: 9MB BDOC-TM
     *
     * Expected Result: Validation report is returned
     *
     * File: 9MB_BDOC-TM.bdoc
     */
    @Test
    public void bdocTmNineMegabyteFilesValidSignatureAreAccepted() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("9MB_BDOC-TM.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "9MB_BDOC-TM.bdoc","POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat",equalTo("XAdES_BASELINE_LT_TM"))
                .body("validatedDocument.filename",equalTo("9MB_BDOC-TM.bdoc"))
                .body("validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: Ddoc-LargeFiles-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: 9MB DDOC
     *
     * Expected Result: Validation report is returned
     *
     * File: 9MB_DDOC.ddoc
     */
    @Test
    public void ddocTenMegabyteFilesWithValidSignatureAreAccepted() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("9MB_DDOC.ddoc"));
        post(validationRequestWithValidKeys(encodedString, "9MB_DDOC.ddoc", "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat",equalTo("DIGIDOC_XML_1.3"))
                .body("validatedDocument.filename",equalTo("9MB_DDOC.ddoc"))
                .body("validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: Bdoc-ZipBomb-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Bdoc Zip container with Bomb file
     *
     * Expected Result: The document should fail the validation
     *
     * File: zip-bomb-package-zip-1gb.bdoc
     */
    @Test
    public void bdocZipBombsAreNotAccepted() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("zip-bomb-package-zip-1gb.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "zip-bomb-package-zip-1gb.bdoc","POLv3"))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: Asice-ZipBomb-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Asice Zip container with Bomb file
     *
     * Expected Result: The document should fail the validation
     *
     * File: zip-bomb-package-zip-1gb.bdoc
     */
    @Test
    public void asiceZipBombsAreNotAccepted() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("zip-bomb-package-zip-1gb.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "zip-bomb-package-zip-1gb.asice","POLv3"))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: Asice-ZipBomb-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Asice Zip container with Matryoshka Bomb file
     *
     * Expected Result: Expected Result: Validation report is returned
     *
     * File: zip-bomb-packages.asice
     */
    @Test
    public void asiceZipBombsWithMatryoshkaAreAccepted() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("zip-bomb-packages.asice"));
        post(validationRequestWithValidKeys(encodedString, "zip-bomb-packages.asice","POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat",equalTo("XAdES_BASELINE_B"))
                .body("validatedDocument.filename",equalTo("zip-bomb-packages.asice"))
                .body("validSignaturesCount", equalTo(0));
    }

    /**
     * TestCaseID: Bdoc-ZipBomb-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Bdoc Zip container with Matryoshka Bomb file
     *
     * Expected Result: Expected Result: Validation report is returned
     *
     * File: zip-bomb-packages.asice
     */
    @Test
    public void bdocZipBombsWithMatryoshkaAreAccepted() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("zip-bomb-packages.asice"));
        post(validationRequestWithValidKeys(encodedString, "zip-bomb-packages.bdoc","POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat",equalTo("XAdES_BASELINE_B_BES"))
                .body("validatedDocument.filename",equalTo("zip-bomb-packages.bdoc"))
                .body("validSignaturesCount", equalTo(0));
    }

    /**
     * TestCaseID: Asics-ZipBomb-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Asics Zip container with Bomb file
     *
     * Expected Result: The document should fail the validation
     *
     * File: zip-bomb-package-zip-1gb-asics.asics
     */
    @Test
    public void asicsZipBombsAreNotAccepted() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("zip-bomb-package-zip-1gb-asics.asics"));
        post(validationRequestWithValidKeys(encodedString, "zip-bomb-package-zip-1gb.asics","POLv3"))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: xRoad-ZipBomb-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: xRoad Zip container with Bomb file
     *
     * Expected Result: The document should fail the validation
     *
     * File: zip-bomb-package-zip-1gb.bdoc
     */

    @Test
    public void validatingSimpleXroadDocumentShouldReturnAReport() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("zip-bomb-package-zip-1gb.bdoc"));
        post(validationRequestWithDocumentTypeValidKeys(encodedString, "zip-bomb-package-zip-1gb.asice", "xroad", VALID_SIGNATURE_POLICY_3))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }


    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
