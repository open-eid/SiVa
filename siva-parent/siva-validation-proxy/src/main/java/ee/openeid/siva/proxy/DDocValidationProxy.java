package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.validation.service.ddoc.DDOCValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DDocValidationProxy extends AbstractValidationProxy {

    private XMLToJSONConverter converter;
    private DDOCValidationService ddocValidationService;

    QualifiedValidationResult validateInService(ValidationDocument validationDocument) {
        return ddocValidationService.validateDocument(validationDocument);
    }

    @Override
    String toJSON(String report) {
        return converter.toJSON(report);
    }

    @Autowired
    public void setDDOCValidationService(DDOCValidationService ddocValidationService) {
        this.ddocValidationService = ddocValidationService;
    }

    @Autowired
    public void setXMLToJSONConverter(XMLToJSONConverter converter) {
        this.converter = converter;
    }

}
