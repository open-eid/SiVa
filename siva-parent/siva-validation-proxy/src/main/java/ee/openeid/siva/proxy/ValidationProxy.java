package ee.openeid.siva.proxy;

import ee.openeid.pdf.webservice.json.JSONDocument;

public interface ValidationProxy {

    String validate(final JSONDocument document);

}
