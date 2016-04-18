package ee.openeid.siva;

import ee.openeid.pdf.webservice.json.JSONDocument;

public interface ValidationProxy {

    String validate(final JSONDocument document);

}
