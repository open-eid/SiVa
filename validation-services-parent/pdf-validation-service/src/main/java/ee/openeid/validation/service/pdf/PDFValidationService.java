package ee.openeid.validation.service.pdf;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.validation.service.pdf.validator.EstonianPDFDocumentValidator;
import ee.openeid.validation.service.pdf.validator.report.PDFQualifiedReportBuilder;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.InMemoryDocument;
import eu.europa.esig.dss.MimeType;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.DocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class PDFValidationService implements ValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PDFValidationService.class);

    private CertificateVerifier certificateVerifier;
    private ConstraintLoadingSignaturePolicyService signaturePolicyService;
    private final Object lock = new Object();

    @Override
    public QualifiedReport validateDocument(ValidationDocument validationDocument) throws DSSException {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("WsValidateDocument: begin");
            }

            if (validationDocument == null) {
                throw new ValidationServiceException(getClass().getSimpleName(), new Exception("No request document found"));
            }

            final DSSDocument dssDocument = createDssDocument(validationDocument);
            if (!new EstonianPDFDocumentValidator().isSupported(dssDocument)) {
                throw new MalformedDocumentException();
            }

            final DocumentValidator validator = new EstonianPDFDocumentValidator(dssDocument);
            LOGGER.info("PDF certificate pool size: {}", getCertificatePoolSize());
            validator.setCertificateVerifier(certificateVerifier);

            final Reports reports;
            final ConstraintDefinedPolicy policy = signaturePolicyService.getPolicy(validationDocument.getSignaturePolicy());
            synchronized (lock) {
                reports = validator.validateDocument(policy.getConstraintDataStream());
            }

            final ZonedDateTime validationTimeInGMT = ZonedDateTime.now(ZoneId.of("GMT"));
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(
                        "Validation completed. Total signature count: {} and valid signature count: {}",
                        reports.getSimpleReport().getSignaturesCount(),
                        reports.getSimpleReport().getValidSignaturesCount()
                );

                LOGGER.info("WsValidateDocument: end");
            }

            final PDFQualifiedReportBuilder reportBuilder = new PDFQualifiedReportBuilder(
                    reports,
                    validationTimeInGMT,
                    validationDocument.getName(),
                    policy
            );
            return reportBuilder.build();
        } catch (MalformedDocumentException | InvalidPolicyException e) {
            endExceptionally(e);
            throw e;
        } catch (Exception e) {
            endExceptionally(e);
            throw new ValidationServiceException(getClass().getSimpleName(), e);
        }
    }

    private int getCertificatePoolSize() {
        return certificateVerifier.getTrustedCertSource().getCertificatePool().getNumberOfCertificates();
    }

    private DSSDocument createDssDocument(final ValidationDocument ValidationDocument) {
        if (ValidationDocument == null) {
            return null;
        }
        final InMemoryDocument dssDocument = new InMemoryDocument(ValidationDocument.getBytes());
        dssDocument.setName(ValidationDocument.getName());
        dssDocument.setMimeType(MimeType.PDF);

        return dssDocument;
    }

    private void endExceptionally(Exception e) {
        LOGGER.error(e.getMessage(), e);
        LOGGER.info("WsValidateDocument: end with exception");
    }

    @Autowired
    public void setCertificateVerifier(CertificateVerifier certificateVerifier) {
        this.certificateVerifier = certificateVerifier;
    }

    @Autowired
    @Qualifier(value = "PDFPolicyService")
    public void setSignaturePolicyService(ConstraintLoadingSignaturePolicyService signaturePolicyService) {
        this.signaturePolicyService = signaturePolicyService;
    }
}