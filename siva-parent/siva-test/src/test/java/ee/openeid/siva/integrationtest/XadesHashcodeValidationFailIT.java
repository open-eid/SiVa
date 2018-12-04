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
        postHashcodeValidation(validationRequestHashcode("Valid_XAdES_LT_TM.xml","Valid_XAdES_LT_TM.xml", null, null, "lama.jpg", "SHA512", "jmQGVaxq5Qb+hZNIQC1FPcRUd+YInHtlTg/ImAh5wQY="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIGNED_DATA_NOT_FOUND"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The reference data object(s) is not found!"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.validatedDocument.fileHashInHex", Matchers.is("0A8CE9855F20AF4E519AD3A2E89F24472C60C09726066CCE00292DEFD091F64D"));
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
               postHashcodeValidation(validationRequestHashcode("Valid_XAdES_LT_TM.xml","Valid_XAdES_LT_TM.xml", null, null, "lama.jpg", "SHA512", "wrongHashq5Qb+hZNIQC1FPcRUd+YInHtlTg/ImAh5wQY="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIGNED_DATA_NOT_FOUND"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The reference data object(s) is not found!"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.validatedDocument.fileHashInHex", Matchers.is("0A8CE9855F20AF4E519AD3A2E89F24472C60C09726066CCE00292DEFD091F64D"));
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
     * File: Valid_XAdES_LT_TM.xml
     **/
    @Test
    public void dataFileFilenameDoesNotMatchWithSignatureFile() {
        postHashcodeValidation(validationRequestHashcode("Valid_XAdES_LT_TM.xml","Valid_XAdES_LT_TM.xml", null, null, "wrongDataFileName.jpg", "SHA512", "jmQGVaxq5Qb+hZNIQC1FPcRUd+YInHtlTg/ImAh5wQY="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIGNED_DATA_NOT_FOUND"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The reference data object(s) is not found!"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.validatedDocument.fileHashInHex", Matchers.is("0A8CE9855F20AF4E519AD3A2E89F24472C60C09726066CCE00292DEFD091F64D"));
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
        postHashcodeValidation(validationRequestHashcode("Invalid_XAdES_LT_TM.xml","Invalid_XAdES_LT_TM.xml", null, null, "build.xml", "SHA256", "l40iM30GCmzmwkPp2I4ZzBKvQ5m3FD5v76xnDCDCU+E="))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("NO_CERTIFICATE_CHAIN_FOUND"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The certificate chain for revocation data is not trusted, there is no trusted anchor."))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.validatedDocument.fileHashInHex", Matchers.is("E78B39742B170A959BFD9638A639CCA5FA55A2F21938D819ED428D84500BFD96"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
