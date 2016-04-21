package ee.openeid.siva.proxy.service;

import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.factory.ValidationProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationProxyService {

    private ValidationProxyFactory validationProxyFactory;

    public String validate(ProxyDocument document) {
        ValidationProxy validationProxy = validationProxyFactory.getProxyForType(document.getDocumentType());
        return validationProxy.validate(document);
    }

    @Autowired
    public void setValidationProxyFactory(ValidationProxyFactory validationProxyFactory) {
        this.validationProxyFactory = validationProxyFactory;
    }
}
