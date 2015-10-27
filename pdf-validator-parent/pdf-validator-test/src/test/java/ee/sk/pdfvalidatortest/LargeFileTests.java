package ee.sk.pdfvalidatortest;

import ee.sk.pdfvalidatortest.configuration.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class LargeFileTests extends PdfValidatorSoapTests {

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void nineMegabyteFilesWithLtSignatureAreAccepted () {
        String httpBody = post(validationRequestFor(readFile("scout_x4-manual-signed_lt_9mb.pdf"))).
                andReturn().body().asString();
        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void nineMegabyteFilesWithLtaSignatureAreAccepted () {
        String httpBody = post(validationRequestFor(readFile("scout_x4-manual-signed_lta_9mb.pdf"))).
                andReturn().body().asString();
        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
     public void oneMegabyteFilesWithLtSignatureAreAccepted () {
        String httpBody = post(validationRequestFor(readFile("digidocservice-signed-lt-1-2mb.pdf"))).
                andReturn().body().asString();
        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void oneMegabyteFilesWithLtaSignatureAreAccepted () {
        String httpBody = post(validationRequestFor(readFile("digidocservice-signed-lta-1-2mb.pdf"))).
                andReturn().body().asString();
        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void fourMegabyteFilesWithLtSignatureAreAccepted () {
        String httpBody = post(validationRequestFor(readFile("egovernment-benchmark-lt-3-8mb.pdf"))).
                andReturn().body().asString();
        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void fourMegabyteFilesWithLtaSignatureAreAccepted () {
        String httpBody = post(validationRequestFor(readFile("egovenrment-benchmark-lta-3-8mb.pdf"))).
                andReturn().body().asString();
        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }



    protected byte[] readFile(String fileName) {
        return readFileFromTestResources("large_pdf_files/", fileName);
    }

}
