package ee.openeid.pdf.webservice;

import ee.openeid.pdf.webservice.document.PDFDocument;

import java.util.Map;

public interface ValidationService {
    Map<String, String> validateDocument(PDFDocument wsDocument);
}
