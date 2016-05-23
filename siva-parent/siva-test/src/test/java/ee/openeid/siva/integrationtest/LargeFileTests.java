package ee.openeid.siva.integrationtest;

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class LargeFileTests extends SiVaRestTests{


    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private static final String TEST_FILES_DIRECTORY = "pdf/large_pdf_files/";

    /**
     * TestCaseID: PDF-LargeFiles-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Larger signed PDF files (PAdES Baseline LT).
     *
     * Expected Result: Bigger documents with valid signature should pass
     *
     * File: scout_x4-manual-signed_lt_9mb.pdf
     */
    @Test
    @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void nineMegabyteFilesWithLtSignatureAreAccepted () {
        assertSomeSignaturesAreValid(postForReport("scout_x4-manual-signed_lt_9mb.pdf"), 1);
    }

    /**
     * TestCaseID: PDF-LargeFiles-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Larger signed PDF files (PAdES Baseline LT).
     *
     * Expected Result: Bigger documents with valid signature should pass
     *
     * File: scout_x4-manual-signed_lta_9mb.pdf
     */
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void nineMegabyteFilesWithLtaSignatureAreAccepted () {
        assertSomeSignaturesAreValid(postForReport("scout_x4-manual-signed_lta_9mb.pdf"), 1);
    }

    /**
     * TestCaseID: PDF-LargeFiles-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Larger signed PDF files (PAdES Baseline LT).
     *
     * Expected Result: Bigger documents with valid signature should pass
     *
     * File: digidocservice-signed-lt-1-2mb.pdf
     */
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void oneMegabyteFilesWithLtSignatureAreAccepted () {
        assertSomeSignaturesAreValid(postForReport("digidocservice-signed-lt-1-2mb.pdf"), 1);
    }

    /**
     * TestCaseID: PDF-LargeFiles-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Larger signed PDF files (PAdES Baseline LT).
     *
     * Expected Result: Bigger documents with valid signature should pass
     *
     * File: digidocservice-signed-lta-1-2mb.pdf
     */
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void oneMegabyteFilesWithLtaSignatureAreAccepted () {
        assertSomeSignaturesAreValid(postForReport("digidocservice-signed-lta-1-2mb.pdf"), 1);
    }

    /**
     * TestCaseID: PDF-LargeFiles-5
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Larger signed PDF files (PAdES Baseline LT).
     *
     * Expected Result: Bigger documents with valid signature should pass
     *
     * File: egovernment-benchmark-lt-3-8mb.pdf
     */
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void fourMegabyteFilesWithLtSignatureAreAccepted () {
        assertSomeSignaturesAreValid(postForReport("egovernment-benchmark-lt-3-8mb.pdf"), 1);
    }

    /**
     * TestCaseID: PDF-LargeFiles-6
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Larger signed PDF files (PAdES Baseline LT).
     *
     * Expected Result: Bigger documents with valid signature should pass
     *
     * File: egovenrment-benchmark-lta-3-8mb.pdf
     */
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void fourMegabyteFilesWithLtaSignatureAreAccepted () {
        assertSomeSignaturesAreValid(postForReport("egovenrment-benchmark-lta-3-8mb.pdf"), 1);
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
