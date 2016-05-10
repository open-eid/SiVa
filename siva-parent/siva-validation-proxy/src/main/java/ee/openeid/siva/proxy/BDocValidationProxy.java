package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.bdoc.BDOCValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BDocValidationProxy extends AbstractValidationProxy {

    private XMLToJSONConverter converter;
    private BDOCValidationService bdocValidationService;

    QualifiedValidationResult validateInService(ValidationDocument validationDocument) {
        return bdocValidationService.validateDocument(validationDocument);
    }

    String toJSON(String report) {
        return converter.toJSON(report);
    }

    @Autowired
    public void setBDOCValidationService(BDOCValidationService bdocValidationService) {
        this.bdocValidationService = bdocValidationService;
    }

    @Autowired
    public void setXMLToJSONConverter(XMLToJSONConverter converter) {
        this.converter = converter;
    }

}
