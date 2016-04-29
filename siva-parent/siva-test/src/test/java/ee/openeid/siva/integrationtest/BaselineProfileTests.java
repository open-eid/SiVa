package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class BaselineProfileTests extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "baseline_profile_test_files/";

    @Test // this file is actually invalid
    public void baselineProfileBDocumentShouldFail() {
        post(validationRequestFor("hellopades-pades-b-sha256-auth.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(0));

        /*TODO: implement finding errors by id from json
        System.out.println(post(validationRequestFor("hellopades-pades-lt-sha256-sign.pdf", "detailed"))
                .andReturn().body().asString());

        assertEquals(
                "The signature format is not allowed by the validation policy constraint!",
                findErrorById("BBB_VCI_ISFC_ANS_1", detailedReport(httpBody)));
        */

    }

    @Ignore
    @Test // need non-plugtest test file
    public void baselineProfileTDocumentShouldFail() {
        /*  TODO: implement finding errors by id from json
        String httpBody = post(validationRequestFor(readFile("some_file.pdf"))).
                andReturn().body().asString();
        assertEquals(
                "The signature format is not allowed by the validation policy constraint!",
                findErrorById("BBB_VCI_ISFC_ANS_1", detailedReport(httpBody)));
        */
        post(validationRequestFor("some_file.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(0));
    }

    @Test
    public void baselineProfileLTDocumentShouldPass() {
        post(validationRequestFor("hellopades-pades-lt-sha256-sign.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Ignore
    @Test // need non-plugtest test file
    public void baselineProfileLTADocumentShouldPass() {
        post(validationRequestFor("some_file.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test
    public void documentWithBaselineProfilesBAndLTASignaturesShouldPass() {
        post(validationRequestFor("hellopades-lt-b.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentMessageDigestAttributeValueDoesNotMatchCalculatedValue() {
        post(validationRequestFor("hellopades-lt1-lt2-wrongDigestValue.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(0));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithMultipleSignersSerialSignature() {
        post(validationRequestFor("hellopades-lt1-lt2-Serial.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(2));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithMultipleSignersParallelSignature() {
        post(validationRequestFor("hellopades-lt1-lt2-parallel3.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(2));
    }

    @Test
    public void ifSignerCertificateIsNotQualifiedAndWithoutSscdItIsRejected() {
        post(validationRequestFor("hellopades-lt1-lt2-parallel3.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(0));

        /*TODO: implement finding errors by id from json
        System.out.println(post(validationRequestFor("hellopades-lt1-lt2-parallel3.pdf", "detailed"))
                .andReturn().body().asString());

        assertEquals(
                "The certificate is not qualified!",
                findErrorById("BBB_XCV_CMDCIQC_ANS", simpleReport(httpBody)));
        */
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
