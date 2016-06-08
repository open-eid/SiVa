package ee.openeid.validation.service.pdf;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.validation.service.pdf.configuration.PDFPolicySettings;
import ee.openeid.validation.service.pdf.document.transformer.ValidationDocumentToDSSDocumentTransformerUtils;
import ee.openeid.validation.service.pdf.validator.EstonianPDFDocumentValidator;
import ee.openeid.validation.service.pdf.validator.report.PDFQualifiedReportBuilder;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.DocumentValidator;
import eu.europa.esig.dss.validation.report.Reports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class PDFValidationService implements ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PDFValidationService.class);

    private CertificateVerifier certificateVerifier;
    private PDFPolicySettings policySettings;

    @Override
    public QualifiedReport validateDocument(ValidationDocument validationDocument) throws DSSException {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("WsValidateDocument: begin");
            }

            if (validationDocument == null) {
                throw new ValidationServiceException(getClass().getSimpleName(), new Exception("No request document found"));
            }

            final DSSDocument dssDocument = ValidationDocumentToDSSDocumentTransformerUtils.createDssDocument(validationDocument);

            if (!new EstonianPDFDocumentValidator().isSupported(dssDocument)) {
                throw new MalformedDocumentException();
            }

            final DocumentValidator validator = new EstonianPDFDocumentValidator(dssDocument);
            validator.setCertificateVerifier(certificateVerifier);

            final Reports reports = validator.validateDocument(policySettings.getPolicyDataStream());

            final ZonedDateTime validationTimeInGMT = ZonedDateTime.now(ZoneId.of("GMT"));
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(
                        "Validation completed. Total signature count: {} and valid signature count: {}",
                        reports.getSimpleReport().getElement("//SignaturesCount").getText(),
                        reports.getSimpleReport().getElement("//ValidSignaturesCount").getText()
                );

                LOGGER.info("WsValidateDocument: end");
            }

            final PDFQualifiedReportBuilder reportBuilder = new PDFQualifiedReportBuilder(
                    reports,
                    validationTimeInGMT,
                    validationDocument.getName()
            );
            return reportBuilder.build();
        } catch (MalformedDocumentException e) {
            endExceptionally(e);
            throw e;
        } catch (Exception e) {
            endExceptionally(e);
            throw new ValidationServiceException(getClass().getSimpleName(), e);
        }
    }

    private void endExceptionally(Exception e) {
        LOGGER.error(e.getMessage(), e);
        LOGGER.info("WsValidateDocument: end with exception");
    }

    @Autowired
    public void setPolicySettings(PDFPolicySettings policySettings) {
        this.policySettings = policySettings;
    }

    @Autowired
    public void setCertificateVerifier(CertificateVerifier certificateVerifier) {
        this.certificateVerifier = certificateVerifier;
    }


}