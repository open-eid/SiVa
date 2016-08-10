package ee.openeid.siva.restAPItest;


import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Category(IntegrationTest.class)

public class ValidationReportValueVerification extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timemark/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /***
     *
     * TestCaseID: Bdoc-ValidationReportValue-1
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, QES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: Valid_ID_sig.bdoc
     *
     ***/
    @Test
    public void BdocCorrectValuesArePresentValidLtTmSignature() {
        post(validationRequestFor("Valid_ID_sig.bdoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /***
     *
     * TestCaseID: Bdoc-ValidationReportValue-2
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT, QES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: 23635_bdoc_ts_OCSP_random_nonce.bdoc
     *
     ***/
    @Test
    public void BdocCorrectValuesArePresentValidLtSignature() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("23635_bdoc_ts_OCSP_random_nonce.bdoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /***
     *
     * TestCaseID: Bdoc-ValidationReportValue-3
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT, AdES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc
     *
     ***/
    @Test
    public void BdocCorrectValuesArePresentValidLtSignatureAdes() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestFor("23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /***
     *
     * TestCaseID: Bdoc-ValidationReportValue-4
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, AdESqc, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: Baltic MoU digital signing_EST_LT_LV.bdoc
     *
     ***/
    @Test @Ignore //TODO: VAL-294
    public void BdocCorrectValuesArePresentValidLtTmSignatureAdesqc() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("Baltic MoU digital signing_EST_LT_LV.bdoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[2].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[2].signatureLevel", Matchers.is("AdESqc"))
                .body("signatures[2].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[2].indication", Matchers.is("TOTAL-FAILED"));
    }

    /***
     *
     * TestCaseID: Bdoc-ValidationReportValue-5
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT-TM, AdESqc
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: 23200_weakdigest-wrong-nonce.asice
     *
     ***/
    @Test
    public void BdocCorrectValuesArePresentInvalidLtSignatureAdesqc() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestFor("23200_weakdigest-wrong-nonce.asice"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].signatureLevel", Matchers.is("AdESqc"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     *
     * TestCaseID: Ddoc-ValidationReportValue-6
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report, xml v1.0, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: SK-XML1.0.ddoc
     *
     ***/
    @Test @Ignore //TODO: VAL-238 Travis fails the test, although in local machine it passes
    public void DdocCorrectValuesArePresentV1_0() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("SK-XML1.0.ddoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("signatures[0].signatureFormat", Matchers.is("SK_XML_1.0"))
                .body("signatures[0].signatureLevel", Matchers.isEmptyString())
                .body("signatures[0].signatureScopes[0].scope", Matchers.isEmptyString())
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].info.bestSignatureTime", Matchers.isEmptyString())
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validSignaturesCount", Matchers.is(2));
    }

    /***
     *
     * TestCaseID: Ddoc-ValidationReportValue-7
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report, xml v1.1, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: Igasugust1.1.ddoc
     *
     ***/
    @Test
    public void DdocCorrectValuesArePresentV1_1() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("igasugust1.1.ddoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.1"))
                .body("signatures[0].signatureLevel", Matchers.isEmptyString())
                .body("signatures[0].signatureScopes[0].scope", Matchers.isEmptyString())
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].info.bestSignatureTime", Matchers.isEmptyString())
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validSignaturesCount", Matchers.is(3));
    }

    /***
     *
     * TestCaseID: Ddoc-ValidationReportValue-8
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report, xml v1.2, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: Igasugust1.2.ddoc
     *
     ***/
    @Test
    public void DdocCorrectValuesArePresentV1_2() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("igasugust1.2.ddoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.2"))
                .body("signatures[0].signatureLevel",  Matchers.isEmptyString())
                .body("signatures[0].signatureScopes[0].scope",  Matchers.isEmptyString())
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validSignaturesCount", Matchers.is(3));
    }

    /***
     *
     * TestCaseID: Ddoc-ValidationReportValue-9
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report, xml v1.3, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: Igasugust1.3.ddoc
     *
     ***/
    @Test
    public void DdocCorrectValuesArePresentV1_3() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("igasugust1.3.ddoc"))
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].signatureLevel", Matchers.isEmptyString())
                .body("signatures[0].signatureScopes[0].scope", Matchers.isEmptyString())
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].info.bestSignatureTime", Matchers.isEmptyString())
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validSignaturesCount", Matchers.is(3));
    }


    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
