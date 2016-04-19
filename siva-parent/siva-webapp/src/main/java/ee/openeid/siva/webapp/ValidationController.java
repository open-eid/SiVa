package ee.openeid.siva.webapp;

import ee.openeid.siva.proxy.service.ValidationProxyService;
import ee.openeid.siva.webapp.transformer.ValidationRequestToJSONDocumentTransformer;
import ee.openeid.siva.webapp.request.impl.JSONValidationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationController {

    private ValidationProxyService validationProxyService;
    private ValidationRequestToJSONDocumentTransformer transformer;

    @RequestMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public String validate(@RequestBody JSONValidationRequest validationRequest) {
        return validationProxyService.validate(transformer.transform(validationRequest));
    }

    @Autowired
    public void setValidationProxy(ValidationProxyService validationProxy) {
        validationProxyService = validationProxy;
    }

    @Autowired
    public void setTransformer(ValidationRequestToJSONDocumentTransformer transformer) {
        this.transformer = transformer;
    }

}
