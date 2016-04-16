package ee.openeid.siva.proxy;

import ee.openeid.pdf.webservice.json.JSONDocument;
import ee.openeid.pdf.webservice.json.ValidationService;
import ee.openeid.siva.model.ValidationRequest;
import ee.openeid.siva.proxy.transformer.ValidationRequestToJsonDocumentTransformer;
import eu.europa.esig.dss.MimeType;
import org.junit.Before;
import org.junit.Test;

import static ee.openeid.siva.testutils.MockValidationRequestBuilder.aValidationRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PdfValidationProxyTest {

    private PdfValidationProxy pdfValidationProxy = new PdfValidationProxy();
    private ValidationRequestToJsonDocumentTransformer transformer = new ValidationRequestToJsonDocumentTransformer();
    private ValidationServiceSpy validationServiceSpy;

    @Before
    public void setUp() {
        validationServiceSpy = new ValidationServiceSpy();
        pdfValidationProxy.setTransformer(transformer);
        pdfValidationProxy.setValidationService(validationServiceSpy);
    }

    @Test
    public void validationRequestIsTranformedAndForwardedToValidationService() {
        ValidationRequest request = aValidationRequest().build();
        pdfValidationProxy.validate(request);
        assertNotNull(validationServiceSpy.jsonDocument);
        assertEquals(MimeType.PDF, validationServiceSpy.jsonDocument.getMimeType());
    }

    private class ValidationServiceSpy implements ValidationService {

        JSONDocument jsonDocument;

        @Override
        public String validateDocument(JSONDocument wsDocument) {
            jsonDocument = wsDocument;
            return null;
        }
    }

}
