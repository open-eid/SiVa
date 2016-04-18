package ee.openeid.siva.service;

import ee.openeid.pdf.webservice.json.JSONDocument;
import ee.openeid.siva.ValidationProxy;
import ee.openeid.siva.factory.ValidationProxyFactory;
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
