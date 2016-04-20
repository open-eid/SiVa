package ee.openeid.siva.webapp.transformer;

import ee.openeid.pdf.webservice.json.PDFDocument;
import ee.openeid.siva.webapp.request.ValidationRequest;
import ee.openeid.siva.testutils.MockValidationRequestBuilder;
import eu.europa.esig.dss.MimeType;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class ValidationRequestToPDFDocumentTransformerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String VALID_PDF_FILE = "test-files/sample.pdf";
    private ValidationRequestToJSONDocumentTransformer transformer = new ValidationRequestToJSONDocumentTransformer();
    private ValidationRequest validationRequest;

    @Before
    public void setUp() throws Exception {
        setValidPdfValidationRequest();
    }

    @Test
    public void fileNameRemainsUnchanged() {
        assertEquals(validationRequest.getFilename(), transformer.transform(validationRequest).getName());
    }

    @Test
    public void typeIsCorrectlyTransformedToMimeType() {
        assertEquals(MimeType.PDF, transformer.transform(validationRequest).getMimeType());
    }

    @Test
    public void contentIsCorrectlyTransformedToBytes() {
        PDFDocument PDFDocument = transformer.transform(validationRequest);
        Assert.assertEquals(validationRequest.getBase64Document(), Base64.encodeBase64String(PDFDocument.getBytes()));
    }

    @Test
    public void unsupportedTypeThrowsException() {
        validationRequest = MockValidationRequestBuilder.aValidationRequest().withType("unsupported").build();
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("type = unsupported is unsupported");
        transformer.transform(validationRequest);
    }

    private void setValidPdfValidationRequest() throws Exception {
        Path filepath = Paths.get(getClass().getClassLoader().getResource(VALID_PDF_FILE).toURI());
        validationRequest = MockValidationRequestBuilder
                .aValidationRequest()
                .withType("pdf")
                .withDocument(filepath)
                .build();
    }
}
