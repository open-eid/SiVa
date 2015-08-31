package ee.sk.pdfvalidatortest;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SignatureCryptographicAlgorithmTests extends PdfValidatorSoapTests {

    @Test
    public void DocumentSignedWithSha512CertifikateShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha512.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void DocumentSignedWithSha1CertifikateShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha1.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Ignore // current test file's signature doesn't contain ocsp
    @Test
    public void DocumentSignedWithSha256EcdsaAlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-ecdsa.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void DocumentSignedWithSha256Ec224AlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ec224.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void DocumentSignedWithSha256Ec256AlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ec256.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void DocumentSignedWithSha256Rsa1024AlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-rsa1024.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void DocumentSignedWithRsa1023AlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-rsa1023.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void DocumentSignedWithRsa2047AlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-rsa2047.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void DocumentSignedWithRsa2048AlgoShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-rsa2048.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    protected byte[] readFile(String fileName) {
        return readFileFromTestResources("signature_cryptographic_algorithm_test_files/", fileName);
    }
}
