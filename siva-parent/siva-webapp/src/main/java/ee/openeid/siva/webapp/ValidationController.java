package ee.openeid.siva.webapp;

import ee.openeid.siva.proxy.PdfValidationProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationController {

    @Autowired
    private PdfValidationProxy validationProxy;

    @RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public String sayHello() {
        return validationProxy.validate();
    }
}
