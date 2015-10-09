package ee.sk.pdfvalidatortest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LargeFileTests extends PdfValidatorSoapTests {

    @Test
    public void nineMegabyteFilesWithLtSignatureAreAccepted () {
        String httpBody = post(validationRequestFor(readFile("scout_x4-manual-signed_lt_9mb.pdf"))).
                andReturn().body().asString();
        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }
    @Test
    public void nineMegabyteFilesWithLtaSignatureAreAccepted () {
        String httpBody = post(validationRequestFor(readFile("scout_x4-manual-signed_lta_9mb.pdf"))).
                andReturn().body().asString();
        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }
    @Test
     public void oneMegabyteFilesWithLtSignatureAreAccepted () {
        String httpBody = post(validationRequestFor(readFile("digidocservice-signed-lt-1-2mb.pdf"))).
                andReturn().body().asString();
        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }
    @Test
    public void oneMegabyteFilesWithLtaSignatureAreAccepted () {
        String httpBody = post(validationRequestFor(readFile("digidocservice-signed-lta-1-2mb.pdf"))).
                andReturn().body().asString();
        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }
    @Test
    public void fourMegabyteFilesWithLtSignatureAreAccepted () {
        String httpBody = post(validationRequestFor(readFile("egovernment-benchmark-lt-3-8mb.pdf"))).
                andReturn().body().asString();
        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }
    @Test
    public void fourMegabyteFilesWithLtaSignatureAreAccepted () {
        String httpBody = post(validationRequestFor(readFile("egovenrment-benchmark-lta-3-8mb.pdf"))).
                andReturn().body().asString();
        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }



    protected byte[] readFile(String fileName) {
        return readFileFromTestResources("large_pdf_files/", fileName);
    }

}
