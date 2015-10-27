package ee.sk.pdfvalidatortest;

import ee.sk.pdfvalidatortest.configuration.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class SignatureRevocationValueTests extends PdfValidatorSoapTests {

    @Test
    public void documentWithOcspOver15MinDelayShouldPassButWarn() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ocsp-15min1s.pdf"))).
                andReturn().body().asString();

        assertEquals(
                "The validation failed, because OCSP is too long after the best-signature-time!",
                findWarningById("ADEST_IOTNLABST_ANS", detailedReport(httpBody)));
        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void documentWithOcspOver24hDelayShouldFail() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ocsp-28h.pdf"))).
                andReturn().body().asString();

        assertEquals(
                "The validation failed, because OCSP is too long after the best-signature-time!",
                findErrorById("ADEST_IOTNLABST_ANS", detailedReport(httpBody)));
        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }

    @Test
    @Ignore("VAL-98 File size limit exeeded with thsi file")
    public void documentWithNoOcspNorCrlInSignatureShouldFail() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lta-no-ocsp.pdf"))).
                andReturn().body().asString();

        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }
    
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithOcspTimeValueBeforeBestSignatureTimeShouldFail() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-rsa2048-ocsp-before-ts.pdf"))).
                andReturn().body().asString();

        assertEquals(
                "The validation failed, because OCSP is before the best-signature-time!",
                findErrorById("ADEST_IOABST_ANS", detailedReport(httpBody)));
        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }

    protected byte[] readFile(String fileName) {
        return readFileFromTestResources("signature_revocation_value_test_files/", fileName);
    }

}
