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

package ee.openeid.siva.manualtest;

import ee.openeid.siva.integrationtest.SiVaRestTests;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class StatisticsToLogsManualIT extends SiVaRestTests {

    @BeforeEach
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timemark/";

    /*
    * Note: All tests in this class expect manual verification of responses in log! The tests are made to prepare test data and ease the test execution.
    */

    /**
     * TestCaseID: Bdoc-Statistics-Log-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/deployment_guide/#statistics
     *
     * Title: Bdoc valid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void bdocWithValidSignatures() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", VALID_SIGNATURE_POLICY_3), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "ASiC-E",
      "usrId" : "XAuthTest",
      "dur": 68, <- Can vary, verify that its present
      "sigCt": 2,
      "vSigCt": 2,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "XAdES_BASELINE_LT_TM"},
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "XAdES_BASELINE_LT_TM"}
      ],
      "sigType" : "XAdES"
   }
}        */
    }

    /**
     * TestCaseID: Bdoc-Statistics-Log-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/deployment_guide/#statistics
     *
     * Title: Bdoc invalid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: SS-4_teadmataCA.4.asice
     */
    @Test
    public void bdocWithInvalidSignatures() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("SS-4_teadmataCA.4.asice"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "SS-4_teadmataCA.4.asice", VALID_SIGNATURE_POLICY_3), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "ASiC-E",
      "usrId" : "XAuthTest",
      "dur": 585, <- Can vary, verify that its present
      "sigCt": 1,
      "vSigCt": 0,
      "sigRslt": [
         {"i":"TOTAL-FAILED", "si":"FORMAT_FAILURE", "cc":"EE", "sf" : "XAdES_BASELINE_T"}
      ],
      "sigType" : "XAdES"
   }
}        */
    }

    /**
     * TestCaseID: Bdoc-Statistics-Log-3
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/deployment_guide/#statistics
     *
     * Title: Bdoc not supported file is inserted
     *
     * Expected Result: No message in statistics as the container is not validated
     *
     * File: xroad-simple.asice
     */
    @Test
    @Disabled("SIVA-352 - remark 8")
    public void bdocWithErrorResponse() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        post(validationRequestWithValidKeys(encodedString, "xroad-simple.bdoc", "POLv3"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: Bdoc-Statistics-Log-4
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/deployment_guide/#statistics
     *
     * Title: Bdoc with certificates from different countries.
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: Baltic MoU digital signing_EST_LT_LV.bdoc
     */
    @Test
    public void bdocWithSignaturesFromDifferentCountries() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Baltic MoU digital signing_EST_LT_LV.bdoc"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "Baltic MoU digital signing_EST_LT_LV.bdoc", VALID_SIGNATURE_POLICY_3), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "ASiC-E",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 3,
      "vSigCt": 3,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "XAdES_BASELINE_LT_TM"},
         {"i":"TOTAL-PASSED", "cc":"LT", "sf" : "XAdES_BASELINE_LT_TM"},
         {"i":"TOTAL-PASSED", "cc":"LV", "sf" : "XAdES_BASELINE_LT_TM"}
      ],
      "sigType" : "XAdES"
   }
}        */
    }

    /**
     * TestCaseID: Ddoc-Statistics-Log-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/deployment_guide/#statistics
     *
     * Title: Ddoc valid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: valid_XML1_3.ddoc
     */
    @Test
    public void ddocWithValidSignatures() {
        setTestFilesDirectory("ddoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("valid_XML1_3.ddoc"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "valid_XML1_3.ddoc", VALID_SIGNATURE_POLICY_3), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "DIGIDOC_XML",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 1,
      "vSigCt": 1,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "DIGIDOC_XML_1.3"}
      ],
      "sigType" : "XAdES"
   }
}        */
    }

    /**
     * TestCaseID: Ddoc-Statistics-Log-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/deployment_guide/#statistics
     *
     * Title: Ddoc invalid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: ilma_kehtivuskinnituseta.ddoc
     */
    @Test
    public void ddocWithInvalidSignatures() {
        setTestFilesDirectory("ddoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("ilma_kehtivuskinnituseta.ddoc"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "ilma_kehtivuskinnituseta.ddoc", VALID_SIGNATURE_POLICY_3), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "DIGIDOC_XML",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 1,
      "vSigCt": 0,
      "sigRslt": [
         {"i":"TOTAL-FAILED", "cc":"EE", "sf" : "DIGIDOC_XML_1.2"}
      ],
      "sigType" : "XAdES"
   }
}        */
    }

    /**
     * TestCaseID: Ddoc-Statistics-Log-3
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/deployment_guide/#statistics
     *
     * Title: Ddoc not supported file is inserted
     *
     * Expected Result: No message in statistics as the container is not validated
     *
     * File: xroad-simple.asice
     */
    @Test
    public void ddocWithErrorResponse() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        post(validationRequestWithValidKeys(encodedString, "xroad-simple.ddoc", "POLv3"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: Ddoc-Statistics-Log-4
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/deployment_guide/#statistics
     *
     * Title: Ddoc with certificates from different countries.
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: Belgia_kandeavaldus_LIV.ddoc
     */
    @Test
    public void ddocWithSignaturesFromDifferentCountries() {
        setTestFilesDirectory("ddoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Belgia_kandeavaldus_LIV.ddoc"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "Belgia_kandeavaldus_LIV.ddoc", VALID_SIGNATURE_POLICY_3), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "DIGIDOC_XML",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 2,
      "vSigCt": 1,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "DIGIDOC_XML_1.3"},
         {"i":"TOTAL-FAILED", "cc":"BE", "sf" : "DIGIDOC_XML_1.3"},
      ],
      "sigType" : "XAdES"
   }
}        */
    }

    /**
     * TestCaseID: Pdf-Statistics-Log-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/deployment_guide/#statistics
     *
     * Title: Pdf valid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: pades_lt_two_valid_sig.pdf
     */
    @Test
    public void pdfWithValidSignatures() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades_lt_two_valid_sig.pdf"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "pades_lt_two_valid_sig.pdf", VALID_SIGNATURE_POLICY_3), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
  "stats" : {
    "type" : "PAdES",
    "usrId" : "XAuthTest",
    "dur" : 685,
    "sigCt" : 2,
    "vSigCt" : 2,
    "sigRslt" : [
        {"i" : "TOTAL-PASSED", "cc" : "EE", "sf" : "PAdES_BASELINE_LT"},
        {"i" : "TOTAL-PASSED", "cc" : "EE", "sf" : "PAdES_BASELINE_LT"}
    ],
    "sigType" : "PAdES"
  }
}        */
    }

    /**
     * TestCaseID: Pdf-Statistics-Log-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/deployment_guide/#statistics
     *
     * Title: Pdf invalid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: hellopades-lt1-lt2-wrongDigestValue.pdf
     */
    @Test
    public void pdfWithInvalidSignatures() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt1-lt2-wrongDigestValue.pdf"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "hellopades-lt1-lt2-wrongDigestValue.pdf", VALID_SIGNATURE_POLICY_3), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
  {
  "stats" : {
    "type" : "PAdES",
    "usrId" : "XAuthTest",
    "dur" : 687,
    "sigCt" : 2,
    "vSigCt" : 0,
    "sigRslt" : [
       {"i" : "TOTAL-FAILED", "cc" : "EE", "sf" : "PAdES_BASELINE_LT"},
       {"i" : "TOTAL-FAILED", "si" : "HASH_FAILURE", "cc" : "EE", "sf" : "PAdES_BASELINE_LT"}
    ],
    "sigType" : "PAdES"
  }
}        */
    }


    /**
     * TestCaseID: Pdf-Statistics-Log-4
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/deployment_guide/#statistics
     *
     * Title: Pdf with certificates from non Estonian countries.
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: Regulatione-signedbyco-legislators.pdf
     */
    @Test
    public void pdfWithSignaturesFromDifferentCountries() {
        setTestFilesDirectory("pdf/signing_certifacte_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Regulatione-signedbyco-legislators.pdf"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "Regulatione-signedbyco-legislators.pdf", VALID_SIGNATURE_POLICY_3), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
  "stats" : {
    "type" : "PAdES",
    "usrId" : "XAuthTest",
    "dur" : 830,
    "sigCt" : 2,
    "vSigCt" : 0,
    "sigRslt" : [
      {"i" : "TOTAL-FAILED", "si" : "FORMAT_FAILURE", "cc" : "BE", "sf" : "PAdES_BASELINE_B"},
      {"i" : "TOTAL-FAILED", "si" : "FORMAT_FAILURE", "cc" : "IT", "sf" : "PAdES_BASELINE_B"}
    ],
    "sigType" : "PAdES"
  }
}        */
    }

    /**
     * TestCaseID: Asics-Statistics-Log-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/deployment_guide/#statistics
     *
     * Title: ASiCs valid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: ValidBDOCinsideAsics.asics
     */
    @Test
    public void asicsWithValidSignatures() {
        setTestFilesDirectory("asics/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("ValidBDOCinsideAsics.asics"));
        post(validationRequestWithValidKeys(encodedString, "ValidBDOCinsideAsics.asics", "POLv4"))
                .then()
                .statusCode(HttpStatus.OK.value());
       /*
       stats" : {
        "type" : "ASiC-S",
        "usrId" : "N/A",
        "dur" : 1566,
        "sigCt" : 0,
        "vSigCt" : 0,
        "sigRslt" : [],
        "sigType" : "N/A"
    }
}     */
    }

    /**
     * TestCaseID: Asics-Statistics-Log-3
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/deployment_guide/#statistics
     *
     * Title: asics invalid container is validated
     *
     * Expected Result: No message in statistics as the container is not validated
     *
     * File: TwoDataFilesAsics.asics
     */
    @Test
    public void asicWithErrorResponse() {
        setTestFilesDirectory("asics/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("TwoDataFilesAsics.asics"));
        post(validationRequestWithValidKeys(encodedString, "TwoDataFilesAsics.asics", "POLv4"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
