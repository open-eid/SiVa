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

import static ee.openeid.siva.integrationtest.TestData.HASH_ALGO_SHA256;
import static ee.openeid.siva.integrationtest.TestData.HASH_ALGO_SHA512;

@Category(IntegrationTest.class)
public class XadesHashcodeValidationFailIT extends SiVaRestTests {
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
     * TestCaseID: Xades-Hashcode-Validation-Fail-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: Data file hash algorithm do not match signature hash algorithm
     *
     * Expected Result: Validation fails
     *
     * File: Valid_XAdES_LT_TM.xml
     **/
    @Test
    public void dataFileHashAlgorithmDoesNotMatchWithSignatureDataFileHashAlgorithm() {
        postHashcodeValidation(validationRequestHashcode("Valid_XAdES_LT_TM.xml", null, null, "test.txt", HASH_ALGO_SHA512, "RnKZobNWVy8u92sDL4S2j1BUzMT5qTgt6hm90TfAGRo="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIG_CRYPTO_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2019-02-05T13:36:23Z"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("47101010033"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.commonName", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("9Rfw5pxkfQyLn7eKnSmTqDQbFKeAEDqUXoRN9Sthyo4="));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Fail-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Hashes do not match
     *
     * Expected Result: Validation fails
     *
     * File: Valid_XAdES_LT_TM.xml
     **/
    @Test
    public void dataFileHashDoesNotMatchWithSignatureFile() {
               postHashcodeValidation(validationRequestHashcode("Valid_XAdES_LT_TM.xml", null, null, "test.txt", HASH_ALGO_SHA256, "kl2ZobNWVy8u92sDL4S2j1BUzMT5qTgt6hm90TfAGRo="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2019-02-05T13:36:23Z"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("9Rfw5pxkfQyLn7eKnSmTqDQbFKeAEDqUXoRN9Sthyo4="));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Fail-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Wrong data file name is used
     *
     * Expected Result: Validation report is returned
     *
     * File: Valid_XAdES_LT_TS.xml
     **/
    @Test
    public void dataFileFilenameDoesNotMatchWithSignatureFile() {
        postHashcodeValidation(validationRequestHashcode("Valid_XAdES_LT_TS.xml", null, null, "wrongDataFileName.jpg", HASH_ALGO_SHA256, "Sj/WcgsM57hpCiR5E8OycJ4jioYwdHzz3s4e5LXditA="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIGNED_DATA_NOT_FOUND"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2019-02-05T13:27:24Z"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("47101010033"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.commonName", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("VLcbTMyISKcCDPJDQ/Z34/TbBueUeqLMFPOrD9Av+b4="));
    }

    /**
     * TestCaseID: Xades-Hashcode-Validation-Fail-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Invalid signature in XAdES
     *
     * Expected Result: Validation report is returned
     *
     * File: Valid_XAdES_LT_TM.xml
     **/
    @Test
    public void invalidSignature() {
        postHashcodeValidation(validationRequestHashcode("Invalid_XAdES_LT_TM.xml", null, null, "test.txt", HASH_ALGO_SHA256, "RnKZobNWVy8u92sDL4S2j1BUzMT5qTgt6hm90TfAGRo="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIG_CRYPTO_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2019-02-05T13:36:23Z"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("ZbbpVP+ue5TfDdbpzZ93b9LK+++26IfQNb7cjCKV7jE="));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
