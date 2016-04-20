package ee.openeid.siva.proxy;

import ee.openeid.pdf.webservice.json.PDFDocument;

public interface ValidationProxy {

    String validate(final PDFDocument document);

}
