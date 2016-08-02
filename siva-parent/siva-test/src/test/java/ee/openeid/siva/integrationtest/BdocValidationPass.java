package ee.openeid.siva.integrationtest;

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class BdocValidationPass extends SiVaRestTests{
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
     * TestCaseID: Bdoc-ValidationPass-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with single valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Valid_ID_sig.bdoc
     ***/
    @Test
    public void validSignature() {
        assertAllSignaturesAreValid(postForReport("Valid_ID_sig.bdoc"));
    }

    /***
     * TestCaseID: Bdoc-ValidationPass-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with multiple valid signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     ***/
    @Test
    public void validMultipleSignatures() {
        assertAllSignaturesAreValid(postForReport("Valid_IDCard_MobID_signatures.bdoc"));
    }

    /***
     * TestCaseID: Bdoc-ValidationPass-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with warning on signature
     *
     * Expected Result: The document should pass the validation but warning should be returned
     *
     * File:
     ***/
    @Test @Ignore //TODO: need file with warnings
    public void validSignatureWithWarning() {
        QualifiedReport report = postForReport("warning.bdoc");
        assertEquals(report.getSignaturesCount(), report.getValidSignaturesCount());
        assertTrue(report.getSignatures().get(0).getWarnings().size() > 0);
    }

    /***
     * TestCaseID: Bdoc-ValidationPass-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice One LT signature with certificates from different countries
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LT-V-30.asice
     ***/
    @Test @Ignore //TODO: This file fails in SiVA. Needs investigation.
    public void bdocDifferentCertificateCountries() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-V-30.asice"))
                .then()
                .body("signatures[0].errors.nameId", Matchers.hasItems(""))
                .body("signatures[0].errors.content", Matchers.hasItems(""))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /***
     * TestCaseID: Bdoc-ValidationPass-5
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc signed with Mobile-ID, ECC-SHA256 signature with prime256v1 key
     *
     * Expected Result: The document should pass the validation
     *
     * File: 24050_short_ecdsa_correct_file_mimetype.bdoc
     ***/
    @Test
    public void bdocEccSha256signature() {
        assertAllSignaturesAreValid(postForReport("24050_short_ecdsa_correct_file_mimetype.bdoc"));
    }
    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
