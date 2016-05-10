package ee.openeid.siva.proxy;

import ee.openeid.pdf.webservice.PDFValidationService;
import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.ValidationDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PdfValidationProxy extends AbstractValidationProxy {

    private XMLToJSONConverter converter;
    private PDFValidationService pdfValidationService;

    QualifiedValidationResult validateInService(ValidationDocument validationDocument) {
        return pdfValidationService.validateDocument(validationDocument);
    }

    String toJSON(String report) {
        return converter.toJSON(report);
    }

    @Autowired
    public void setPDFValidationService(PDFValidationService pdfValidationService) {
        this.pdfValidationService = pdfValidationService;
    }

    @Autowired
    public void setXMLToJSONConverter(XMLToJSONConverter converter) {
        this.converter = converter;
    }

}
