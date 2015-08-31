package ee.sk.pdfvalidatortest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BaselineProfileTests extends PdfValidatorSoapTests {

    @Test
    public void baselineProfileBDocumentShouldFail() {
        String httpBody = post(validationRequestFor(readFile("hellopades-pades-b-sha256-auth.pdf"))).
                andReturn().body().asString();
        //System.out.println(httpBody.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&#xD;", "").replaceAll("&quot;", "\""));

        assertEquals(0, validSignatures(simpleReport(httpBody)));

    }

    @Test // need non-plugtest test file
    public void baselineProfileTDocumentShouldFail() {
        String httpBody = post(validationRequestFor(readFile("Signature-P-EE_AS-5.pdf"))).
                andReturn().body().asString();

        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void baselineProfileLTDocumentShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-pades-lt-sha256-sign.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }

    @Test // need non-plugtest test file
    public void baselineProfileLTADocumentShouldPass() {
        String httpBody = post(validationRequestFor(readFile("Signature-P-EE_AS-7.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void documentWithBaselineProfilesBAndLTASignaturesShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-b.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    protected byte[] readFile(String fileName) {
        return readFileFromTestResources("baseline_profile_test_files/", fileName);
    }

}
