package ee.openeid.validation.service.pdf.validator;

import ee.openeid.validation.service.pdf.validator.policy.EstonianCustomProcessExecutor;
import ee.openeid.validation.service.pdf.validator.policy.EstonianEtsiValidationPolicy;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.pades.validation.PDFDocumentValidator;
import eu.europa.esig.dss.validation.policy.ProcessExecutor;
import eu.europa.esig.dss.validation.policy.ValidationPolicy;
import eu.europa.esig.dss.validation.report.Reports;
import org.w3c.dom.Document;

import java.io.InputStream;

public class EstonianPDFDocumentValidator extends PDFDocumentValidator {

    public EstonianPDFDocumentValidator() {
        super(null);
    }

    public EstonianPDFDocumentValidator(DSSDocument dssDocument) {
        super(dssDocument);
    }

    @Override
    public Reports validateDocument(final Document validationPolicyDom) {

        final ValidationPolicy validationPolicy = new EstonianEtsiValidationPolicy(validationPolicyDom);
        return validateDocument(validationPolicy);
    }

    @Override
    public Reports validateDocument(final String policyResourcePath) {

        if (policyResourcePath == null) {
            return validateDocument((InputStream) null);
        }
        return validateDocument(getClass().getResourceAsStream(policyResourcePath));
    }

    @Override
    public ProcessExecutor provideProcessExecutorInstance() {

        if (processExecutor == null) {
            processExecutor = new EstonianCustomProcessExecutor();
        }
        return processExecutor;
    }

}
