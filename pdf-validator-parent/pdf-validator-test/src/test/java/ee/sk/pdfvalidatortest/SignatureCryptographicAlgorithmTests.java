package ee.sk.pdfvalidatortest;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SignatureCryptographicAlgorithmTests extends PdfValidatorSoapTests {

    @Test
    public void documentSignedWithSha512CertificateShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha512.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void documentSignedWithSha1CertificateShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha1.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Ignore // current test file's signature doesn't contain ocsp
    @Test
    public void documentSignedWithSha256EcdsaAlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-ecdsa.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void documentSignedWithSha256Ec224AlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ec224.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void documentSignedWithSha256Ec256AlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ec256.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithSha256Rsa1024AlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-rsa1024.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithRsa1023AlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-rsa1023.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithRsa2047AlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-rsa2047.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithRsa2048AlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-rsa2048.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    protected byte[] readFile(String fileName) {
        return readFileFromTestResources("signature_cryptographic_algorithm_test_files/", fileName);
    }
}
