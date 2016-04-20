package ee.openeid.siva.proxy.impl;

import ee.openeid.pdf.webservice.json.PDFDocument;
import ee.openeid.pdf.webservice.json.ValidationService;
import ee.openeid.siva.proxy.ValidationProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PdfValidationProxy implements ValidationProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfValidationProxy.class);

    private ValidationService validationService;

    public String validate(final PDFDocument document) {
        return validationService.validateDocument(document);
    }

    @Autowired
    public void setValidationService(ValidationService validationService) {
        this.validationService = validationService;
    }

}
