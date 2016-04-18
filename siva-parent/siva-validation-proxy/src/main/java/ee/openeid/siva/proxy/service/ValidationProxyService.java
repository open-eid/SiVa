package ee.openeid.siva.proxy.service;

import ee.openeid.pdf.webservice.json.JSONDocument;
import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.proxy.factory.ValidationProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationProxyService {

    private ValidationProxyFactory validationProxyFactory;

    public String validate(JSONDocument document) {
        ValidationProxy validationProxy = validationProxyFactory.getValidationProxy(document.getMimeType());
        validationProxy.validate(document);
        return null;
    }

    @Autowired
    public void setValidationProxyFactory(ValidationProxyFactory validationProxyFactory) {
        this.validationProxyFactory = validationProxyFactory;
    }
}
