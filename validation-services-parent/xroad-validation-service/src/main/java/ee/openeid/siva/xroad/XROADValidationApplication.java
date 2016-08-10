package ee.openeid.siva.xroad;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class XROADValidationApplication {
    private ValidationService validationService;

    public static void main(String... args) {
        SpringApplication.run(XROADValidationApplication.class, args);
    }

    @RequestMapping(value = "/xroad-validation", method = RequestMethod.POST)
    public QualifiedReport validateXroad(@RequestBody ValidationDocument validationDocument) {
        return validationService.validateDocument(validationDocument);
    }

    @Autowired
    public void setValidationService(ValidationService validationService) {
        this.validationService = validationService;
    }
}
