package ee.openeid.validation.service.pdf.validator;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.pades.validation.PDFDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;

import java.io.InputStream;

public class EstonianPDFDocumentValidator extends PDFDocumentValidator {

    public EstonianPDFDocumentValidator() {
        super(null);
    }

    public EstonianPDFDocumentValidator(DSSDocument dssDocument) {
        super(dssDocument);
    }

    @Override
    public Reports validateDocument(final String policyResourcePath) {
        if (policyResourcePath == null) {
            return validateDocument((InputStream) null);
        }

        return validateDocument(getClass().getResourceAsStream(policyResourcePath));
    }

}
