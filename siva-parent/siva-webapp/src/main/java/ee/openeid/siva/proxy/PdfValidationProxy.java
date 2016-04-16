package ee.openeid.siva.proxy;

import ee.openeid.pdf.webservice.json.ValidationService;
import ee.openeid.siva.model.ValidationRequest;
import ee.openeid.siva.proxy.transformer.ValidationRequestToJsonDocumentTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PdfValidationProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfValidationProxy.class);

    private ValidationService validationService;
    private ValidationRequestToJsonDocumentTransformer transformer;

    public String validate(final ValidationRequest validationRequest) {
        return validationService.validateDocument(transformer.transform(validationRequest));
    }

    @Autowired
    public void setValidationService(ValidationService validationService) {
        this.validationService = validationService;
    }

    @Autowired
    public void setTransformer(ValidationRequestToJsonDocumentTransformer transformer) {
        this.transformer = transformer;
    }
}
