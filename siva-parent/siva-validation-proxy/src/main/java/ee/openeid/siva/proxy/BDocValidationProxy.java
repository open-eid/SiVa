package ee.openeid.siva.proxy;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.validation.service.bdoc.BDOCValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BDocValidationProxy extends AbstractValidationProxy {

    private BDOCValidationService bdocValidationService;

    @Override
    QualifiedReport validateInService(ValidationDocument validationDocument) {
        return bdocValidationService.validateDocument(validationDocument);
    }

    @Autowired
    public void setBDOCValidationService(BDOCValidationService bdocValidationService) {
        this.bdocValidationService = bdocValidationService;
    }

}
