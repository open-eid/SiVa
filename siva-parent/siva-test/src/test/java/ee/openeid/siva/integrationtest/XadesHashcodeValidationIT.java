/*
 * Copyright 2018 Riigi Infosüsteemide Amet
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

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.openeid.siva.integrationtest.TestData.MOCK_XADES_DATAFILE_FILENAME;
import static ee.openeid.siva.integrationtest.TestData.MOCK_XADES_DATAFILE_FILENAME2;
import static ee.openeid.siva.integrationtest.TestData.MOCK_XADES_DATAFILE_HASH;
import static ee.openeid.siva.integrationtest.TestData.MOCK_XADES_DATAFILE_HASH2;
import static ee.openeid.siva.integrationtest.TestData.MOCK_XADES_DATAFILE_HASH_ALGO;
import static ee.openeid.siva.integrationtest.TestData.MOCK_XADES_SIGNATURE_FILE;

@Category(IntegrationTest.class)
public class XadesHashcodeValidationIT extends SiVaRestTests {
    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_format_test_files/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: Xades-Hash-Validation-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: XAdES extracted from ASICE, AdesQC level
     *
     * Expected Result: The document should pass the validation
     *
     * File: hashAsiceXades.xml
     */
    @Test
    public void validXadesWithHashcodeFromAsice() {
        postHashcodeValidation(validationRequestHashcode(MOCK_XADES_SIGNATURE_FILE, "signature0.xml", "POLv4", "Simple", MOCK_XADES_DATAFILE_FILENAME, MOCK_XADES_DATAFILE_HASH_ALGO, MOCK_XADES_DATAFILE_HASH)).then().
                body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Xades-Hash-Validation-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: XAdES extracted from BDOC
     *
     * Expected Result: The document should pass the validation
     *
     * File: hashBdocXades.xml
     */
    @Test
    public void validXadesWithHashcodeFromBdoc() {
        postHashcodeValidation(validationRequestHashcode("hashBdocXades.xml", "signature0.xml", "POLv4", "Simple", MOCK_XADES_DATAFILE_FILENAME2, MOCK_XADES_DATAFILE_HASH_ALGO, MOCK_XADES_DATAFILE_HASH2)).then().
                body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }


    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
