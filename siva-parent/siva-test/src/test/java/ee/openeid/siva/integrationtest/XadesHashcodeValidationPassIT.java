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
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static ee.openeid.siva.integrationtest.TestData.HASH_ALGO_SHA224;
import static ee.openeid.siva.integrationtest.TestData.HASH_ALGO_SHA256;
import static ee.openeid.siva.integrationtest.TestData.HASH_ALGO_SHA384;
import static ee.openeid.siva.integrationtest.TestData.HASH_ALGO_SHA512;

@Category(IntegrationTest.class)
public class XadesHashcodeValidationPassIT extends SiVaRestTests {
    private static final String DEFAULT_TEST_FILES_DIRECTORY = "xades/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Pass-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: XAdES extracted from ASICE
     *
     * Expected Result: The document should pass the validation
     *
     * File: Valid_XAdES_LT_TS.xml
     */
    @Test
    public void validXadesWithHashcodeFromAsice() throws IOException, SAXException, ParserConfigurationException {
        postHashcodeValidation(validationRequestHashcodeSimple("Valid_XAdES_LT_TS.xml", null, null))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("47101010033"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.commonName", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2019-02-05T13:27:24Z"));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Pass-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: XAdES extracted from BDOC
     *
     * Expected Result: The document should pass the validation
     *
     * File: Valid_XAdES_LT_TM.xml
     */
    @Test
    public void validXadesWithHashcodeFromBdoc() {
        postHashcodeValidation(validationRequestHashcodeSimple("Valid_XAdES_LT_TM.xml", null, null))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("47101010033"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.commonName", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2019-02-05T13:36:23Z"));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Pass-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: XAdES extracted from BDOC
     *
     * Expected Result: The document should pass the validation
     *
     * File: Valid_XADES_LT_TS_multiple_datafiles.xml
     */
    @Test
    public void validXadesWithHashcodeWithMultipleDataFiles() throws IOException, SAXException, ParserConfigurationException {
        postHashcodeValidation(validationRequestHashcodeSimple("Valid_XAdES_LT_TS_multiple_datafiles.xml", null, null))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2019-02-05T12:48:26Z"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Pass-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Datafile has + in name
     *
     * Expected Result: The document should pass the validation
     *
     * File: test+document.xml
     */
    @Test
    public void validXadesWithPlusInDataFileName() {
        postHashcodeValidation(validationRequestHashcode("test+document.xml", null, null, "test+document.txt", HASH_ALGO_SHA256, "heKN3NGQ0HttzgmfKG0L243dfG7W+6kTMO5n7YbKeS4="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2019-02-05T12:43:15Z"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("test+document.txt"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Pass-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Datafile has space in name
     *
     * Expected Result: The document should pass the validation
     *
     * File: spacesInDatafile.xml
     */
    @Test
    public void validXadesWithSpaceInDataFileName() {
        postHashcodeValidation(validationRequestHashcode("spacesInDatafile.xml", null, null, "Te st in g.txt", HASH_ALGO_SHA256, "5UxI8Rm1jUZm48+Vkdutyrsyr3L/MPu/RK1V81AeKEY="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2019-02-05T13:22:04Z"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("Te st in g.txt"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Algorithms-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Datafile digest in SHA1
     *
     * Expected Result: The document should pass the validation
     *
     * File: sha224_TS.xml
     */
    @Test
    public void sha1DatafileDigestSignatureShouldPass() {
        postHashcodeValidation(validationRequestHashcode("sha1_TM.xml", null, null, "test.txt", "SHA1", "qP3CBanxnMHHUHpgxPAbE9Edf9A="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].hashAlgo", Matchers.is("SHA1"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Algorithms-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Datafile digest in SHA224
     *
     * Expected Result: The document should pass the validation
     *
     * File: sha224_TS.xml
     */
    @Test
    public void sha224DatafileDigestSignatureShouldPass() {
        postHashcodeValidation(validationRequestHashcode("sha224_TS.xml", null, null, "test1.txt", HASH_ALGO_SHA224, "C7YzVACWz0f8pxd7shHKB1BzOuIuSjBysO3xgw=="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].hashAlgo", Matchers.is(HASH_ALGO_SHA224))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Algorithms-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Datafile digest in SHA256
     *
     * Expected Result: The document should pass the validation
     *
     * File: Valid_XAdES_LT_TS.xml
     */
    @Test
    public void sha256DatafileDigestSignatureShouldPass() {
        postHashcodeValidation(validationRequestHashcode("Valid_XAdES_LT_TS.xml", null, null, "test.txt", HASH_ALGO_SHA256, "RnKZobNWVy8u92sDL4S2j1BUzMT5qTgt6hm90TfAGRo="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].hashAlgo", Matchers.is(HASH_ALGO_SHA256))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Algorithms-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Datafile digest in SHA384
     *
     * Expected Result: The document should pass the validation
     *
     * File: sha384_TS.xml
     */
    @Test
    public void sha384DatafileDigestSignatureShouldPass() {
        postHashcodeValidation(validationRequestHashcode("sha384_TS.xml", null, null, "test1.txt", HASH_ALGO_SHA384, "DU5PS1Qcd2gu8U3g+4hDYldhAoT/sxEWz6YV8cEdjAaVEFMYSNOypSL+xt4KkK9k"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].hashAlgo", Matchers.is(HASH_ALGO_SHA384))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Algorithms-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Datafile digest in SHA512
     *
     * Expected Result: The document should pass the validation
     *
     * File: sha512_TS.xml
     */
    @Test
    public void sha512DatafileDigestSignatureShouldPass() {
        postHashcodeValidation(validationRequestHashcode("sha512_TS.xml", null, null, "test1.txt", HASH_ALGO_SHA512, "pA2Dh2/WoCnnxGL9PZd+DQivXUmq8dQG1nyQY3phKZPKlm/HfZZDG8yB79hTG2F4pV9LqW+6SGsETE9d+LQsRg=="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].hashAlgo", Matchers.is(HASH_ALGO_SHA512))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
