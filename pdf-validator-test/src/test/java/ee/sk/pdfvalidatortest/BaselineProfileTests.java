package ee.sk.pdfvalidatortest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BaselineProfileTests extends PdfValidatorSoapTests {

    @Test
    public void RevokedBaselineProfileBDocumentShouldFail() {
        String httpBody = post(validationRequestFor(readFile("Signature-P-EE_AS-1.pdf"))).
                andReturn().body().asString();
        //System.out.println(httpBody.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&#xD;", "").replaceAll("&quot;", "\""));

        assertEquals(0, validSignatures(simpleReport(httpBody)));

    }

    @Test
    public void noOcspRequestsAreMadeForBaselineProfileBDocument() {
        String httpBody = post(validationRequestFor(readFile("Signature-P-AT-1.pdf"))).
                andReturn().body().asString();

        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void BaselineProfileTDocumentShouldFail() {
        String httpBody = post(validationRequestFor(readFile("Signature-P-EE_AS-5.pdf"))).
                andReturn().body().asString();

        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void BaselineProfileLTDocumentShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-pades-lt-sha256-sign.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }

    @Test
    public void BaselineProfileLTADocumentShouldPass() {
        String httpBody = post(validationRequestFor(readFile("Signature-P-EE_AS-7.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void BaselineProfileLTADocumentWithBaselineProfileBSignatureShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-b.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    protected byte[] readFile(String fileName) {
        return readFileFromTestResources("baseline_profile_test_files/", fileName);
    }

}
