package ee.openeid.siva.restAPItest;

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;


import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;


@Category(IntegrationTest.class)
public class ValidationReportJsonStructureVerification extends SiVaRestTests {

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
     * TestCaseID: Bdoc-ValidationReport-1
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
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
         post(validationRequestFor("Valid_ID_sig.bdoc"))
            .then()
                 .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: Bdoc-ValidationReport-2
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: JSON structure has all elements (Bdoc valid multiple signatures)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: Baltic MoU digital signing_EST_LT_LV.bdoc
     *
     ***/
    @Test @Ignore//TODO: VAL-244 was found with Valid_IDCard_MobID_signatures.bdoc file.
    public void BdocAllElementsArePresentValidMultipleSignatures() {
        post(validationRequestFor("Baltic MoU digital signing_EST_LT_LV.bdoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: Bdoc-ValidationReport-3
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
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
        post(validationRequestFor("IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: Bdoc-ValidationReport-4
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
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
        post(validationRequestFor("test1-bdoc-unknown.bdoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: Bdoc-ValidationReport-5
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Check for optional subindication and error elements
     *
     * Expected Result: Error and subindication elements are present
     *
     * File: IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc
     *
     ***/
    @Test
    public void BdocOptionalSubindicationAndErrorElementsArePresent() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc"))
                .then()
                .body("signatures.indication", Matchers.hasItem("TOTAL-FAILED"))
                .body("signatures.subIndication", Matchers.hasItem("SIG_CRYPTO_FAILURE"))
                .body("signatures.errors[0].nameId", Matchers.hasItems("BBB_CV_ISI_ANS","GENERIC"))
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: Bdoc-ValidationReport-6
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Check for optional warning element
     *
     * Expected Result: Warning element is present
     *
     * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc
     *
     ***/
    @Test //TODO: this file gives different result in digidoc4j utility. Needs investigation.
    public void BdocOptionalWarningElementIsPresent() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("23200_weakdigest-wrong-nonce.asice"));
        post(validationRequestWithValidKeys(encodedString, "23200_weakdigest-wrong-nonce.asice", "bdoc"))
                .then()
                .body("signatures.indication", Matchers.hasItem("TOTAL-FAILED"))
                .body("signatures.warnings.nameId[0]", Matchers.hasItem("BBB_XCV_CMDCISSCD_ANS"))
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: Bdoc-ValidationReport-7
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title:  Bdoc report with no signatures
     *
     * Expected Result: Report is returned with required elements
     *
     * File:BdocContainerNoSignature.bdoc
     *
     ***/
    @Test
    public void BdocNoSignature() {
        setTestFilesDirectory("document_format_test_files/");
        post(validationRequestFor("BdocContainerNoSignature.bdoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: Pdf-ValidationReport-8
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: JSON structure has all elements (Pdf valid single signature)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: hellopades-lt-sha256-ec256.pdf
     *
     ***/
    @Test
    public void PdfAllElementsArePresentValidSignature() {
        setTestFilesDirectory("pdf/signature_cryptographic_algorithm_test_files/");
        post(validationRequestFor("hellopades-lt-sha256-ec256.pdf"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: Pdf-ValidationReport-9
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: JSON structure has all elements (Pdf valid Multiple signatures)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File:
     *
     ***/
    @Test
    public void PdfAllElementsArePresentValidmultipleSignatures() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestFor("pades_lt_two_valid_sig.pdf"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("validSignaturesCount",equalTo(2));
    }

    /***
     *
     * TestCaseID: Pdf-ValidationReport-10
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: JSON structure has all elements (Pdf invalid signature)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: hellopades-lt-b.pdf
     *
     ***/
    @Test @Ignore //TODO: VAL-242
    public void PdfAllElementsArePresentInvalidSignature() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestFor("hellopades-lt-b.pdf"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: Pdf-ValidationReport-11
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: JSON structure has all elements (Pdf indeterminate status)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: hellopades-lt-rsa1024-sha1-expired.pdf
     *
     ***/
    @Test
    public void PdfAllElementsArePresentIndeterminateSignature() {
        setTestFilesDirectory("pdf/signing_certifacte_test_files/");
        post(validationRequestFor("hellopades-lt-rsa1024-sha1-expired.pdf"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: Pdf-ValidationReport-12
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title:  Pdf report with no signatures
     *
     * Expected Result: Report is returned with required elements
     *
     * File: PdfNoSignature.pdf
     *
     ***/
    @Test
    public void PdfNoSignature() {
        setTestFilesDirectory("document_format_test_files/");
        post(validationRequestFor("PdfNoSignature.pdf"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: Ddoc-ValidationReport-13
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: JSON structure has all elements (ddoc valid single signature)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: 18912.ddoc
     *
     ***/
    @Test
    public void DdocAllElementsArePresentValidSignature() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("18912.ddoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"));
    }

    /***
     *
     * TestCaseID: Ddoc-ValidationReport-14
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: JSON structure has all elements (ddoc valid Multiple signatures)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: igasugust1.1.ddoc
     *
     ***/
    @Test
    public void DdocAllElementsArePresentValidMultipleSignatures() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("igasugust1.1.ddoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"));
    }

    /***
     *
     * TestCaseID: Ddoc-ValidationReport-15
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: JSON structure has all elements (ddoc invalid signature)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: test1-ddoc-revoked.ddoc
     *
     ***/
    @Test
    public void DdocAllElementsArePresentInvalidSignature() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("test1-ddoc-revoked.ddoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"));
    }

    /***
     *
     * TestCaseID: Ddoc-ValidationReport-16
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: JSON structure has all elements (ddoc indeterminate status)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: test1-ddoc-unknown.ddoc
     *
     ***/
    @Test
    public void DdocAllElementsArePresentIndeterminateSignature() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("test1-ddoc-unknown.ddoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"));
    }

    /***
     *
     * TestCaseID: Ddoc-ValidationReport-17
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Check for optional subindication and error elements
     *
     * Expected Result: Error and subindication elements are present
     *
     * File: test1-ddoc-unknown.ddoc
     *
     ***/
    @Test
    public void DdocOptionalSubindicationAndErrorElementsArePresent() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("test1-ddoc-unknown.ddoc"))
                .then()
                .body("signatures.indication", Matchers.hasItem("TOTAL-FAILED"))
                .body("signatures.subIndication", Matchers.hasItem(""))
                .body("signatures.errors.nameId[0]", Matchers.hasItem("70"))
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"));
    }

    /***
     *
     * TestCaseID: Ddoc-ValidationReport-18
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Check for optional warning element
     *
     * Expected Result: Warning element is present
     *
     * File:
     *
     ***/
    @Test @Ignore //TODO: File is needed!
    public void DdocOptionalWarningElementIsPresent() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("NeedFile.ddoc"))
                .then()
                .body("signatures.indication", Matchers.hasItem(""))
                .body("signatures.warnings.nameId", Matchers.hasItem("need value for this"))
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    /***
     *
     * TestCaseID: Ddoc-ValidationReport-19
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title:  Ddoc report with no signatures
     *
     * Expected Result: Report is returned with required elements
     *
     * File: DdocContainerNoSignature.ddoc
     *
     ***/
    @Test
    public void DdocNoSignature() {
        setTestFilesDirectory("document_format_test_files/");
        post(validationRequestFor("DdocContainerNoSignature.ddoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}