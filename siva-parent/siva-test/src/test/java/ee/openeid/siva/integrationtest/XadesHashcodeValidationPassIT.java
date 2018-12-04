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
        postHashcodeValidation(validationRequestHashcode("Valid_XAdES_LT_TS.xml","Valid_XAdES_LT_TS.xml", null, null, "RELEASE-NOTES.txt", "SHA256", "Sj/WcgsM57hpCiR5E8OycJ4jioYwdHzz3s4e5LXditA="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2014-10-31T14:08:19Z"))
                .body("validationReport.validationConclusion.validatedDocument.fileHashInHex", Matchers.is("F9FD100BD985DF062E954A42FD292CA095F614329CFC179D01F5D318C47DC50A"));
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
        postHashcodeValidation(validationRequestHashcode("Valid_XAdES_LT_TM.xml","Valid_XAdES_LT_TM.xml", null, null, "lama.jpg", "SHA256", "jmQGVaxq5Qb+hZNIQC1FPcRUd+YInHtlTg/ImAh5wQY="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2013-11-25T13:16:59Z"))
                .body("validationReport.validationConclusion.validatedDocument.fileHashInHex", Matchers.is("0A8CE9855F20AF4E519AD3A2E89F24472C60C09726066CCE00292DEFD091F64D"));
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
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.validatedDocument.fileHashInHex", Matchers.is("A0D7AA6CFDA7F1A5E07223107B75F81E786F1A5869EED8F8BD1001845BA91FBA"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
