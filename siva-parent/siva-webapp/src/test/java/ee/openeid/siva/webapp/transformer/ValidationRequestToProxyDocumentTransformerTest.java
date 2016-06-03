package ee.openeid.siva.webapp.transformer;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.testutils.MockValidationRequestBuilder;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class ValidationRequestToProxyDocumentTransformerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String VALID_PDF_FILE = "test-files/sample.pdf";
    private ValidationRequestToProxyDocumentTransformer transformer = new ValidationRequestToProxyDocumentTransformer();
    private MockValidationRequestBuilder.MockValidationRequest validationRequest;

    @Before
    public void setUp() throws Exception {
        setValidPdfValidationRequest();
    }

    @Test
    public void fileNameRemainsUnchanged() {
        assertEquals(validationRequest.getFilename(), transformer.transform(validationRequest).getName());
    }

    @Test
    public void pdfTypeIsCorrectlyTransformedToDocumentType() {
        assertEquals(DocumentType.PDF, transformer.transform(validationRequest).getDocumentType());
    }

    @Test
    public void bdocTypeIsCorrectlyTransformedToDocumentType() {
        validationRequest.setType("bdoc");
        assertEquals(DocumentType.BDOC, transformer.transform(validationRequest).getDocumentType());
    }

    @Test
    public void ddocTypeIsCorrectlyTransformedToDocumentType() {
        validationRequest.setType("ddoc");
        assertEquals(DocumentType.DDOC, transformer.transform(validationRequest).getDocumentType());
    }

    @Test
    public void simpleReportTypeIsCorrectlyTransformedToReportType() {
        validationRequest.setReportType("SimPle");
        assertEquals(ReportType.SIMPLE, transformer.transform(validationRequest).getReportType());
    }

    @Test
    public void detailedReportTypeIsCorrectlyTransformedToReportType() {
        validationRequest.setReportType("detailed");
        assertEquals(ReportType.DETAILED, transformer.transform(validationRequest).getReportType());
    }

    @Test
    public void diagnosticDataReportTypeIsCorrectlyTransformedToReportType() {
        validationRequest.setReportType("diagnosticData");
        assertEquals(ReportType.DIAGNOSTICDATA, transformer.transform(validationRequest).getReportType());
    }

    @Test
    public void contentIsCorrectlyTransformedToBytes() {
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        Assert.assertEquals(validationRequest.getDocument(), Base64.encodeBase64String(proxyDocument.getBytes()));
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
