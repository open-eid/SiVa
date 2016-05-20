package ee.openeid.siva.proxy;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.validation.service.ddoc.DDOCValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DDocValidationProxy extends AbstractValidationProxy {

    private DDOCValidationService ddocValidationService;

    QualifiedReport validateInService(ValidationDocument validationDocument) {
        return ddocValidationService.validateDocument(validationDocument);
    }

    @Autowired
    public void setDDOCValidationService(DDOCValidationService ddocValidationService) {
        this.ddocValidationService = ddocValidationService;
    }

}
