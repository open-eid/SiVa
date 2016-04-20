package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.ProxyDocument;

public interface ValidationProxy {

    String validate(final ProxyDocument document);

}
