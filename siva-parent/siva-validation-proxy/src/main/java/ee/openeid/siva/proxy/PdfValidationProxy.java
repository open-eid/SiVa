package ee.openeid.siva.proxy;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.validation.service.pdf.PDFValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PdfValidationProxy extends AbstractValidationProxy {

    private PDFValidationService pdfValidationService;

    @Override
    QualifiedReport validateInService(ValidationDocument validationDocument) {
        return pdfValidationService.validateDocument(validationDocument);
    }

    @Autowired
    public void setPDFValidationService(PDFValidationService pdfValidationService) {
        this.pdfValidationService = pdfValidationService;
    }

}
