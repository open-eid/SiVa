package ee.openeid.siva;


import ee.openeid.pdf.webservice.json.JSONDocument;
import ee.openeid.pdf.webservice.json.ValidationService;
import ee.openeid.siva.impl.PdfValidationProxy;
import eu.europa.esig.dss.MimeType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PdfValidationProxyTest {

    private PdfValidationProxy pdfValidationProxy = new PdfValidationProxy();
    private ValidationServiceSpy validationServiceSpy;

    @Before
    public void setUp() {
        validationServiceSpy = new ValidationServiceSpy();
        pdfValidationProxy.setValidationService(validationServiceSpy);
    }

    @Test
    public void validationRequestIsTranformedAndForwardedToValidationService() {
        pdfValidationProxy.validate(new JSONDocument());
        assertNotNull(validationServiceSpy.jsonDocument);
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
