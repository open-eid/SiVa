package ee.openeid.siva.webapp;

import ee.openeid.siva.proxy.PdfValidationProxy;
import ee.openeid.siva.webapp.request.JSONValidationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationController {

    private PdfValidationProxy validationProxy;

    @RequestMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public String validate(@RequestBody JSONValidationRequest validationRequest) {
        return validationProxy.validate(validationRequest);
    }

    @Autowired
    public void setValidationProxy(PdfValidationProxy validationProxy) {
        this.validationProxy = validationProxy;
    }

}
