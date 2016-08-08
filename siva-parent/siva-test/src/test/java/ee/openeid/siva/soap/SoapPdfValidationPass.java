package ee.openeid.siva.soap;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;

import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class SoapPdfValidationPass extends SiVaSoapTests {

    @Test
    public void validPdfSignatureWithSoapEndpoint() {
        Document report = extractReportDom(post(validationRequestForDocument("PdfValidSingleSignature.pdf")).andReturn().body().asString());
        assertTrue(1 == validSignatures(report));
        assertTrue(1 == getQualifiedReportFromDom(report).getValidSignaturesCount());
    }

    @Override
    protected String getTestFilesDirectory() {
        return "document_format_test_files/";
    }
}
