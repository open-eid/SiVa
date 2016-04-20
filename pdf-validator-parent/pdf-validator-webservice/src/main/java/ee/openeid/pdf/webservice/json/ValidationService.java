package ee.openeid.pdf.webservice.json;

import java.util.Map;

public interface ValidationService {
    Map<String, String> validateDocument(PDFDocument wsDocument);
}
