package ee.openeid.siva.integrationtest;


import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.jayway.restassured.RestAssured;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;

@Category(IntegrationTest.class)
public class AsiceDigestValidationPassIT extends AbstractDigestValidationTest {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timestamp/";

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Before
    public void DirectoryBackToDefault() {
        this.testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;
    }

    /**
     * TestCaseID: Asice-ValidationPass-1
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     * <p>
     * Title: Asice with single valid signature
     * <p>
     * Expected Result: The document should pass the validation
     * <p>
     * File: ValidLiveSignature.asice
     */
    @Test
    public void validAsiceSingleSignature() {
        this.assertAllSignaturesAreValid(this.postForReport("ValidLiveSignature.asice"));
    }

    /**
     * TestCaseID: Asice-ValidationPass-2
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     * <p>
     * Title: Asice TM with multiple valid signatures
     * <p>
     * Expected Result: The document should pass the validation
     * <p>
     * File: BDOC-TS.asice
     */
    @Test
    public void validAsiceMultipleSignatures() {
        this.assertAllSignaturesAreValid(this.postForReport("BDOC-TS.asice"));
    }

    /**
     * TestCaseID: Asice-ValidationPass-4
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     * <p>
     * Title: Asice One LT signature with certificates from different countries
     * <p>
     * Expected Result: The document should pass the validation
     * <p>
     * File: EE_SER-AEX-B-LT-V-30.asice
     */
    @Test
    public void asiceDifferentCertificateCountries() {
        this.post(this.validationRequestFor("EE_SER-AEX-B-LT-V-30.asice"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationPass-12
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice unsigned data files in the container
     * <p>
     * Expected Result: The document should pass the validation with warning
     * <p>
     * File: EE_SER-AEX-B-LT-V-34.asice
     */
    @Test
    public void asiceUnsignedDataFiles() {
        this.post(this.validationRequestFor("EE_SER-AEX-B-LT-V-34.asice"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            //.body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("All files are not signed!"))
            .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));

    }

    /**
     * TestCaseID: Asice-ValidationPass-13
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: New Estonian ECC signature
     * <p>
     * Expected Result: The document should pass the validation
     * <p>
     * File: Mac_AS0099904_EsimeneAmetlikSKTestElliptilistega_TS.asice
     */
    @Test
    public void asiceEccSignatureShouldPass() {
        this.testFilesDirectory = "bdoc/test/timestamp/";
        this.post(this.validationRequestFor("Mac_AS0099904_EsimeneAmetlikSKTestElliptilistega_TS.asice"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
            .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));

    }

}
