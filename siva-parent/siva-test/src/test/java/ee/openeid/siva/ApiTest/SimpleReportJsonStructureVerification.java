package ee.openeid.siva.ApiTest;

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.json.JSONObject;


import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;


@Category(IntegrationTest.class)
public class SimpleReportJsonStructureVerification extends SiVaRestTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }


    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timemark/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /***
     *
     * TestCaseID: ValidationReport-1
     *
     * TestType: Automated
     *
     * RequirementID: Validation report - WIP (TBD)
     *
     * Title: JSON structure has all elements (Bdoc valid single signature)
     *
     * Expected Result: All required elements are present according to BdocDocSimpleReportSchema.json
     *
     * File: Valid_ID_sig.bdoc
     *
     ***/
    @Test
    public void BdocAllElementsArePresentValidSingleSignature() {
         post(validationRequestFor("Valid_ID_sig.bdoc", "simple"))
            .then()
                 .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }



    /***
     *
     * TestCaseID: ValidationReport-2
     *
     * TestType: Automated
     *
     * RequirementID: Validation report - WIP (TBD)
     *
     * Title: JSON structure has all elements (Bdoc valid multiple signatures)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void BdocAllElementsArePresentValidMultipleSignatures() {
        post(validationRequestFor("Valid_IDCard_MobID_signatures.bdoc", "simple"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: ValidationReport-3
     *
     * TestType: Automated
     *
     * RequirementID: Validation report - WIP (TBD)
     *
     * Title: JSON structure has all elements (Bdoc invalid single signature)
     *
     * Expected Result: All required elements are present according to BdocDocSimpleReportSchema.json
     *
     * File: IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc
     *
     ***/
    @Test
    public void BdocAllElementsArePresentInvalidSignature() {
        post(validationRequestFor("IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc", "simple"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }


    /***
     *
     * TestCaseID: ValidationReport-4
     *
     * TestType: Automated
     *
     * RequirementID: Validation report - WIP (TBD)
     *
     * Title: JSON structure has all elements (Bdoc indeterminate status)
     *
     * Expected Result: All required elements are present according to BdocDocSimpleReportSchema.json
     *
     * File: test1-bdoc-unknown.bdoc
     *
     ***/
    @Test
    public void BdocAllElementsArePresentIndeterminateSignature() {
        post(validationRequestFor("test1-bdoc-unknown.bdoc", "simple"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }


    /***
     *
     * TestCaseID: ValidationReport-5
     *
     * TestType: Automated
     *
     * RequirementID: Validation report - WIP (TBD)
     *
     * Title: JSON structure has all elements (Pdf valid single signature)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: Valid_ID_sig.bdoc
     *
     ***/
    @Test
    public void PdfAllElementsArePresentValidSignature() {
        setTestFilesDirectory("pdf/signature_cryptographic_algorithm_test_files/");
        post(validationRequestFor("hellopades-lt-sha256-ec256.pdf", "simple"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: ValidationReport-6
     *
     * TestType: Automated
     *
     * RequirementID: Validation report - WIP (TBD)
     *
     * Title: JSON structure has all elements (Pdf valid Multiple signatures)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: Valid_ID_sig.bdoc
     *
     ***/
    @Test
    public void PdfAllElementsArePresentValidmultipleSignatures() {
        setTestFilesDirectory("");
        post(validationRequestFor("needfile", "simple"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }


    /***
     *
     * TestCaseID: ValidationReport-7
     *
     * TestType: Automated
     *
     * RequirementID: Validation report - WIP (TBD)
     *
     * Title: JSON structure has all elements (Pdf invalid signature)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: hellopades-lt-b.bdoc
     *
     ***/
    @Test
    public void PdfAllElementsArePresentInvalidSignature() {
        setTestFilesDirectory("baseline_profile_test_files/");
        post(validationRequestFor("hellopades-lt-b.pdf", "simple"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }


    /***
     *
     * TestCaseID: ValidationReport-8
     *
     * TestType: Automated
     *
     * RequirementID: Validation report - WIP (TBD)
     *
     * Title: JSON structure has all elements (Pdf indeterminate status)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: Valid_ID_sig.bdoc
     *
     ***/

    @Test
    public void PdfAllElementsArePresentIndeterminateSignature() {
        setTestFilesDirectory("signing_certifacte_test_files/");
        post(validationRequestFor("hellopades-lt-rsa1024-sha1-expired.pdf", "simple"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }


    /***
     *
     * TestCaseID: ValidationReport-9
     *
     * TestType: Automated
     *
     * RequirementID: Validation report - WIP (TBD)
     *
     * Title: JSON structure has all elements (ddoc valid single signature)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: Valid_ID_sig.bdoc
     *
     ***/
    @Test @Ignore //Ddoc not supported yet
    public void DdocAllElementsArePresentValidSignature() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("18912.ddoc", "simple"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: ValidationReport-10
     *
     * TestType: Automated
     *
     * RequirementID: Validation report - WIP (TBD)
     *
     * Title: JSON structure has all elements (ddoc valid Multiple signatures)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: Valid_ID_sig.bdoc
     *
     ***/
    @Test @Ignore //Ddoc not supported yet
    public void DdocAllElementsArePresentValidMultipleSignatures() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("igasugust1.1.ddoc", "simple"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }


    /***
     *
     * TestCaseID: ValidationReport-11
     *
     * TestType: Automated
     *
     * RequirementID: Validation report - WIP (TBD)
     *
     * Title: JSON structure has all elements (ddoc invalid signature)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: hellopades-lt-b.bdoc
     *
     ***/
    @Test @Ignore //ddoc not yet supported
    public void DdocAllElementsArePresentInvalidSignature() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("test1-ddoc-revoked.ddoc", "simple"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }


    /***
     *
     * TestCaseID: ValidationReport-12
     *
     * TestType: Automated
     *
     * RequirementID: Validation report - WIP (TBD)
     *
     * Title: JSON structure has all elements (ddoc indeterminate status)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: Valid_ID_sig.bdoc
     *
     ***/

    @Test @Ignore //Ddoc not supported yet
    public void DdocAllElementsArePresentIndeterminateSignature() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("test1-ddoc-unknown.ddoc", "simple"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}