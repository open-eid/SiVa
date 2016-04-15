package ee.openeid.siva.webapp.transformer;

import ee.openeid.pdf.webservice.json.JSONDocument;
import ee.openeid.siva.webapp.request.model.JSONValidationRequest;
import eu.europa.esig.dss.MimeType;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class RequestToJsonDocumentTransformerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String VALID_PDF_FILE = "test-files/sample.pdf";
    private RequestToJsonDocumentTransformer transformer = new RequestToJsonDocumentTransformer();
    private JSONValidationRequest validationRequest;

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
        JSONDocument jsonDocument = transformer.transform(validationRequest);
        assertEquals(validationRequest.getBase64Document(), Base64.encodeBase64String(jsonDocument.getBytes()));
    }

    @Test
    public void unsupportedTypeThrowsException() {
        validationRequest.setType("unsupported");
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("type = unsupported is unsupported");
        transformer.transform(validationRequest);
    }

    private void setValidPdfValidationRequest() throws Exception {
        validationRequest = new JSONValidationRequest();
        validationRequest.setFilename("filename.pdf");
        validationRequest.setType("pdf");
        Path filepath = Paths.get(getClass().getClassLoader().getResource(VALID_PDF_FILE).toURI());
        validationRequest.setBase64Document(Base64.encodeBase64String(Files.readAllBytes(filepath)));
    }
}
