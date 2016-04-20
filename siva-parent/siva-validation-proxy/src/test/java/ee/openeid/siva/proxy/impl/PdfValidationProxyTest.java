package ee.openeid.siva.proxy.impl;


import ee.openeid.pdf.webservice.json.PDFDocument;
import ee.openeid.pdf.webservice.json.ValidationService;

import org.junit.Before;
import org.junit.Test;

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
        pdfValidationProxy.validate(new PDFDocument());
        assertNotNull(validationServiceSpy.PDFDocument);
    }



    private class ValidationServiceSpy implements ValidationService {

        PDFDocument PDFDocument;

        @Override
        public String validateDocument(PDFDocument wsDocument) {
            PDFDocument = wsDocument;
            return null;
        }
    }

}
