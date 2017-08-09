/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

public class StatisticsToLogsManualIT extends SiVaRestTests {

    @Before
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
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
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
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "bdoc", VALID_SIGNATURE_POLICY_1), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "ASiC_E",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 2,
      "vSigCt": 2,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "XAdES_BASELINE_LT_TM"},
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "XAdES_BASELINE_LT_TM"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Bdoc-Statistics-Log-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
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
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "SS-4_teadmataCA.4.asice", "bdoc", VALID_SIGNATURE_POLICY_1), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "ASiC_E",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 1,
      "vSigCt": 0,
      "sigRslt": [
         {"i":"INDETERMINATE", "si":"NO_CERTIFICATE_CHAIN_FOUND", "cc":"EE", "sf" : "XAdES_BASELINE_LT_TM"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Bdoc-Statistics-Log-3
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
     *
     * Title: Bdoc not supported file is inserted
     *
     * Expected Result: No message in statistics as the container is not validated
     *
     * File: xroad-simple.asice
     */
    @Test
    public void bdocWithErrorResponse() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        post(validationRequestWithValidKeys(encodedString, "xroad-simple.bdoc", null, ""))
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
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
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
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "Baltic MoU digital signing_EST_LT_LV.bdoc", "bdoc", VALID_SIGNATURE_POLICY_1), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "ASiC_E",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 3,
      "vSigCt": 3,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "XAdES_BASELINE_LT_TM"},
         {"i":"TOTAL-PASSED", "cc":"LT", "sf" : "XAdES_BASELINE_LT_TM"},
         {"i":"TOTAL-PASSED", "cc":"LV", "sf" : "XAdES_BASELINE_LT_TM"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Ddoc-Statistics-Log-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
     *
     * Title: Ddoc valid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: igasugust1.3.ddoc
     */
    @Test
    public void ddocWithValidSignatures() {
        setTestFilesDirectory("ddoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("igasugust1.3.ddoc"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "igasugust1.3.ddoc", "ddoc", VALID_SIGNATURE_POLICY_1), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "XAdES",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 3,
      "vSigCt": 3,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "DIGIDOC_XML_1.3"},
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "DIGIDOC_XML_1.3"},
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "DIGIDOC_XML_1.3"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Ddoc-Statistics-Log-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
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
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "ilma_kehtivuskinnituseta.ddoc", "ddoc", VALID_SIGNATURE_POLICY_1), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "XAdES",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 1,
      "vSigCt": 0,
      "sigRslt": [
         {"i":"TOTAL-FAILED", "cc":"EE", "sf" : "DIGIDOC_XML_1.2"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Ddoc-Statistics-Log-3
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
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
        post(validationRequestWithValidKeys(encodedString, "xroad-simple.ddoc", null, ""))
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
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
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
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "Belgia_kandeavaldus_LIV.ddoc", "ddoc", VALID_SIGNATURE_POLICY_1), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "XAdES",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 2,
      "vSigCt": 1,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "DIGIDOC_XML_1.3"},
         {"i":"TOTAL-FAILED", "cc":"BE", "sf" : "DIGIDOC_XML_1.3"},
      ]
   }
}        */
    }

    /**
     * TestCaseID: Pdf-Statistics-Log-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
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
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "pades_lt_two_valid_sig.pdf", "pdf", VALID_SIGNATURE_POLICY_1), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "PAdES",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 2,
      "vSigCt": 2,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "PAdES_BASELINE_LT"},
         {"i":"TOTAL-PASSED", "cc":"EE", "sf" : "PAdES_BASELINE_LT"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Pdf-Statistics-Log-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
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
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "hellopades-lt1-lt2-wrongDigestValue.pdf", "pdf", VALID_SIGNATURE_POLICY_1), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "PAdES",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 2,
      "vSigCt": 0,
      "sigRslt": [
         { "i" : "TOTAL-PASSED", "cc":"EE", "sf" : "PAdES_BASELINE_LTA"},
         { "i" : "TOTAL-FAILED",  "si" : "HASH_FAILURE", "cc" : "EE", "sf" : "PAdES_BASELINE_LTA"}
      ]
   }
}        */
    }


    /**
     * TestCaseID: Pdf-Statistics-Log-4
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
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
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "Regulatione-signedbyco-legislators.pdf", "pdf", VALID_SIGNATURE_POLICY_1), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "PAdES",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 2,
      "vSigCt": 0,
      "sigRslt": [
         { "i" : "TOTAL-FAILED", "cc" : "BE", "sf" : "PAdES_BASELINE_B"},
         { "i" : "TOTAL-FAILED", "cc" : "BE", "sf" : "PAdES_BASELINE_B"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Xroad-Statistics-Log-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
     *
     * Title: Xroad valid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: xroad-batchsignature.asice
     */
    @Test
    public void xroadWithValidSignatures() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-batchsignature.asice"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "xroad-batchsignature.asice", "xroad", VALID_SIGNATURE_POLICY_1), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "ASiC-E (BatchSignature)",
      "usrId" : "XAuthTest",
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 1,
      "vSigCt": 1,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"XX", "sf" : "XAdES_BASELINE_B_BES"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Xroad-Statistics-Log-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
     *
     * Title: Xroad invalid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: invalid-digest.asice
     */
    @Test
    public void xroadWithInvalidSignature() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("invalid-digest.asice"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "invalid-digest.asice", "xroad", VALID_SIGNATURE_POLICY_1), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    /*
    Expected result:
{
   "stats": {
      "type" : "ASiC_E",
      "usrId" : "XAuthTest",
      "dur": 134, <- Can vary, verify that its present
      "sigCt": 1,
      "vSigCt": 0,
      "sigRslt": [
         { "i" : "TOTAL-FAILED", "si" : "", "cc":"XX", "sf" : "XAdES_BASELINE_LT"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Xroad-Statistics-Log-3
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
     *
     * Title: Xroad not supported file is inserted
     *
     * Expected Result: No message in statistics as the container is not validated
     *
     * File: BDOC-TS.bdoc
     */
    @Test
    public void xroadWithErrorResponse() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("BDOC-TS.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "BDOC-TS.bdoc", "xroad", ""))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
