package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class LargeFileTests extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "large_pdf_files/";

    @Test
    @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void nineMegabyteFilesWithLtSignatureAreAccepted () {
        post(validationRequestFor("scout_x4-manual-signed_lt_9mb.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void nineMegabyteFilesWithLtaSignatureAreAccepted () {
        post(validationRequestFor("scout_x4-manual-signed_lta_9mb.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void oneMegabyteFilesWithLtSignatureAreAccepted () {
        post(validationRequestFor("digidocservice-signed-lt-1-2mb.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void oneMegabyteFilesWithLtaSignatureAreAccepted () {
        post(validationRequestFor("digidocservice-signed-lta-1-2mb.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void fourMegabyteFilesWithLtSignatureAreAccepted () {
        post(validationRequestFor("egovernment-benchmark-lt-3-8mb.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void fourMegabyteFilesWithLtaSignatureAreAccepted () {
        post(validationRequestFor("egovenrment-benchmark-lta-3-8mb.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
