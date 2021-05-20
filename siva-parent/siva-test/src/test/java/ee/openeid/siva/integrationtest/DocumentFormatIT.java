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
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.openeid.siva.integrationtest.TestData.VALIDATION_CONCLUSION_PREFIX;

@Category(IntegrationTest.class)
public class DocumentFormatIT extends SiVaRestTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_format_test_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: DocumentFormat-1
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Validation of pdf document acceptance
     *
     * Expected Result: Pdf is accepted and correct signature validation is given
     *
     * File: hellopades-pades-lt-sha256-sign.pdf
     */
    @Test
    public void PAdESDocumentShouldPass() {
        post(validationRequestFor("hellopades-pades-lt-sha256-sign.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: DocumentFormat-2
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Validation of bdoc document acceptance
     *
     * Expected Result: Bdoc is accepted and correct signature validation is given
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    @Ignore("SIVA-278")
    public void BdocDocumentShouldPass() {
        post(validationRequestFor("Valid_IDCard_MobID_signatures.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signaturesCount", Matchers.is(2))
                .body("validSignaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: DocumentFormat-3
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Validation of asice document acceptance
     *
     * Expected Result: asice is accepted and correct signature validation is given
     *
     * File: Vbdoc21-TS.asice
     */
    @Test
    public void asiceDocumentShouldPass() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("bdoc21-TS.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: DocumentFormat-4
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Validation of asics document acceptance
     *
     * Expected Result: asics is accepted and correct signature validation is given
     *
     * File: ValidDDOCinsideAsics.asics
     */
    @Test
    public void asicsDocumentShouldPass() {
        setTestFilesDirectory("asics/");
        post(validationRequestFor("ValidDDOCinsideAsics.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: DocumentFormat-5
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Validation of xades acceptance
     *
     * Expected Result: xades is accepted and correct signature validation is given
     *
     * File: signatures0.xml
     */
    @Test
    public void xadesDocumentShouldPass() {
        postHashcodeValidation(validationRequestHashcodeSimple("signatures0.xml", "POLv4", "Simple"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: DocumentFormat-6
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Validation of cades acceptance
     *
     * Expected Result: cades is accepted and correct signature validation is given
     *
     * File:
     */
    @Ignore //TODO: Test file needed
    @Test
    public void cadesDocumentShouldPass() {
        post(validationRequestFor(""))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("signatures[0].signatureFormat", Matchers.is("CAdES_BASELINE_LT"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
