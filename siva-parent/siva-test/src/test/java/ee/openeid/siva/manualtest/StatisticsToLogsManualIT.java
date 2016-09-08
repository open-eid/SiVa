package ee.openeid.siva.manualtest;

import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
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
     * RequirementID:
     *
     * Title: Bdoc valid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void bdocWithValidSignatures() {
        QualifiedReport report = postForReport("Valid_IDCard_MobID_signatures.bdoc", VALID_SIGNATURE_POLICY_1);
        assertAllSignaturesAreValid((report));
    /*
    Expected result:
{
   "stats": {
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 2,
      "vSigCt": 2,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE"},
         {"i":"TOTAL-PASSED", "cc":"EE"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Bdoc-Statistics-Log-2
     *
     * TestType: Manual
     *
     * RequirementID:
     *
     * Title: Bdoc invalid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: SS-4_teadmataCA.4.asice
     */
    @Test
    public void bdocWithInvalidSignatures() {
        QualifiedReport report = postForReport("SS-4_teadmataCA.4.asice", VALID_SIGNATURE_POLICY_1);
        assertAllSignaturesAreInvalid((report));
    /*
    Expected result:
{
   "stats": {
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 1,
      "vSigCt": 0,
      "sigRslt": [
         {"i":"TOTAL-FAILED", "si":"NO_CERTIFICATE_CHAIN_FOUND", "cc":"EE"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Bdoc-Statistics-Log-3
     *
     * TestType: Manual
     *
     * RequirementID:
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
        post(validationRequestWithValidKeys(encodedString, "xroad-simple.asice", "bdoc", ""))
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
     * RequirementID:
     *
     * Title: Bdoc with certificates from different countries.
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: Baltic MoU digital signing_EST_LT_LV.bdoc
     */
    @Test
    public void bdocWithSignaturesFromDifferentCountries() {
        QualifiedReport report = postForReport("Baltic MoU digital signing_EST_LT_LV.bdoc", VALID_SIGNATURE_POLICY_1);
        assertAllSignaturesAreValid((report));
    /*
    Expected result:
{
   "stats": {
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 3,
      "vSigCt": 3,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE"},
         {"i":"TOTAL-PASSED", "cc":"LT"},
         {"i":"TOTAL-PASSED", "cc":"LV"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Ddoc-Statistics-Log-1
     *
     * TestType: Manual
     *
     * RequirementID:
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
        QualifiedReport report = postForReport("igasugust1.3.ddoc", VALID_SIGNATURE_POLICY_1);
        assertAllSignaturesAreValid((report));
    /*
    Expected result:
{
   "stats": {
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 3,
      "vSigCt": 3,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE"},
         {"i":"TOTAL-PASSED", "cc":"EE"},
         {"i":"TOTAL-PASSED", "cc":"EE"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Ddoc-Statistics-Log-2
     *
     * TestType: Manual
     *
     * RequirementID:
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
        QualifiedReport report = postForReport("ilma_kehtivuskinnituseta.ddoc", VALID_SIGNATURE_POLICY_1);
        assertAllSignaturesAreInvalid((report));
    /*
    Expected result:
{
   "stats": {
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 1,
      "vSigCt": 0,
      "sigRslt": [
         {"i":"TOTAL-FAILED", "cc":"EE"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Ddoc-Statistics-Log-3
     *
     * TestType: Manual
     *
     * RequirementID:
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
        post(validationRequestWithValidKeys(encodedString, "xroad-simple.asice", "ddoc", ""))
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
     * RequirementID:
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
        QualifiedReport report = postForReport("Belgia_kandeavaldus_LIV.ddoc", VALID_SIGNATURE_POLICY_1);
        assertSomeSignaturesAreValid(report, 1);
    /*
    Expected result:
{
   "stats": {
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 2,
      "vSigCt": 1,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE"},
         {"i":"TOTAL-FAILED", "cc":"BE"},
      ]
   }
}        */
    }

    /**
     * TestCaseID: Pdf-Statistics-Log-1
     *
     * TestType: Manual
     *
     * RequirementID:
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
        QualifiedReport report = postForReport("pades_lt_two_valid_sig.pdf", VALID_SIGNATURE_POLICY_1);
        assertAllSignaturesAreValid((report));
    /*
    Expected result:
{
   "stats": {
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 2,
      "vSigCt": 2,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE"},
         {"i":"TOTAL-PASSED", "cc":"EE"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Pdf-Statistics-Log-2
     *
     * TestType: Manual
     *
     * RequirementID:
     *
     * Title: Pdf invalid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: hellopades-lt1-lt2-wrongDigestValue.pdf
     */
    @Test @Ignore //TODO: One signature is valid when validationLevel is set to ARCHIVAL_DATA
    public void pdfWithInvalidSignatures() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        QualifiedReport report = postForReport("hellopades-lt1-lt2-wrongDigestValue.pdf", VALID_SIGNATURE_POLICY_1);
        assertAllSignaturesAreInvalid((report));
    /*
    Expected result:
{
   "stats": {
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 2,
      "vSigCt": 0,
      "sigRslt": [
         { "i" : "INDETERMINATE", "si" : "OUT_OF_BOUNDS_NO_POE", "cc":"EE"},
         { "i" : "TOTAL-FAILED",  "si" : "HASH_FAILURE", "cc" : "EE"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Pdf-Statistics-Log-3
     *
     * TestType: Manual
     *
     * RequirementID:
     *
     * Title: Pdf not supported file is inserted
     *
     * Expected Result: No message in statistics as the container is not validated
     *
     * File: xroad-simple.asice
     */
    @Test
    public void pdfWithErrorResponse() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        post(validationRequestWithValidKeys(encodedString, "xroad-simple.asice", "pdf", ""))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: Pdf-Statistics-Log-4
     *
     * TestType: Manual
     *
     * RequirementID:
     *
     * Title: Pdf with certificates from different countries.
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: Regulatione-signedbyco-legislators.pdf
     */
    @Test
    public void pdfWithSignaturesFromDifferentCountries() {
        setTestFilesDirectory("pdf/signing_certifacte_test_files/");
        QualifiedReport report = postForReport("Regulatione-signedbyco-legislators.pdf", VALID_SIGNATURE_POLICY_2);
        assertAllSignaturesAreInvalid((report));
    /*
    Expected result:
{
   "stats": {
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 2,
      "vSigCt": 0,
      "sigRslt": [
         { "i" : "INDETERMINATE", "si" : "OUT_OF_BOUNDS_NO_POE", "cc" : "BE"},
         { "i" : "INDETERMINATE", "si" : "TRY_LATER", "cc" : "BE"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Xroad-Statistics-Log-1
     *
     * TestType: Manual
     *
     * RequirementID:
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
        QualifiedReport report = mapToReport(
                post(validationRequestWithValidKeys(encodedString, "xroad-batchsignature.asice", "xroad", ""))
                        .body()
                        .asString()
        );
        assertAllSignaturesAreValid(report);
    /*
    Expected result:
{
   "stats": {
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 1,
      "vSigCt": 1,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"XX"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Xroad-Statistics-Log-2
     *
     * TestType: Manual
     *
     * RequirementID:
     *
     * Title: Xroad invalid container is validated
     *
     * Expected Result: Correct data is shown in the log with correct structure
     *
     * File: invalid-digest.asice
     */
    @Test @Ignore //TODO: VAL-323
    public void xroadWithInvalidSignature() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("invalid-digest.asice"));
        QualifiedReport report = mapToReport(
                post(validationRequestWithValidKeys(encodedString, "invalid-digest.asice", "xroad", ""))
                        .body()
                        .asString()
        );
        assertAllSignaturesAreInvalid(report);
    /*
    Expected result:
{
   "stats": {
      "dur": 1334, <- Can vary, verify that its present
      "sigCt": 1,
      "vSigCt": 0,
      "sigRslt": [
         { "i" : "TOTAL-FAILED", "si" : "", "cc":"XX"}
      ]
   }
}        */
    }

    /**
     * TestCaseID: Xroad-Statistics-Log-3
     *
     * TestType: Manual
     *
     * RequirementID:
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
