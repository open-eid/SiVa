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

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Category(IntegrationTest.class)
public class XadesHashcodeValidationPassIT extends SiVaRestTests {
    private static final String DEFAULT_TEST_FILES_DIRECTORY = "xades/";
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
     * TestCaseID: Xades-Hashcode-Validation-Pass-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: XAdES extracted from ASICE
     *
     * Expected Result: The document should pass the validation
     *
     * File: Valid_XAdES_LT_TS.xml
     */
    @Test
    public void validXadesWithHashcodeFromAsice() {
        postHashcodeValidation(validationRequestHashcode("Valid_XAdES_LT_TS.xml", null, null, "test.txt", "SHA256", "RnKZobNWVy8u92sDL4S2j1BUzMT5qTgt6hm90TfAGRo="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2019-02-05T13:27:24Z"))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("VLcbTMyISKcCDPJDQ/Z34/TbBueUeqLMFPOrD9Av+b4="));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Pass-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: XAdES extracted from BDOC
     *
     * Expected Result: The document should pass the validation
     *
     * File: Valid_XAdES_LT_TM.xml
     */
    @Test
    public void validXadesWithHashcodeFromBdoc() {
        postHashcodeValidation(validationRequestHashcode("Valid_XAdES_LT_TM.xml", null, null, "test.txt", "SHA256", "RnKZobNWVy8u92sDL4S2j1BUzMT5qTgt6hm90TfAGRo="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2019-02-05T13:36:23Z"))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("9Rfw5pxkfQyLn7eKnSmTqDQbFKeAEDqUXoRN9Sthyo4="));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Pass-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: XAdES extracted from BDOC
     *
     * Expected Result: The document should pass the validation
     *
     * File: Valid_XADES_LT_TS_multiple_datafiles.xml
     */
    @Test
    public void validXadesWithHashcodeWithMultipleDataFiles() throws IOException, SAXException, ParserConfigurationException {
        postHashcodeValidation(validationRequestHashcodeReadFromFile("Valid_XAdES_LT_TS_multiple_datafiles.xml", null, null))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2019-02-05T12:48:26Z"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("9un8fNcRbS462smQM6YW+Od987gc8Cm4wC+CdSjaXAc="));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
