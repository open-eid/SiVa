package ee.openeid.validation.service.generic.validator.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.reports.Reports;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReportBuilderData {

    private Reports dssReports;
    private ValidationLevel validationLevel;
    private ValidationDocument validationDocument;
    private ConstraintDefinedPolicy policy;
    private boolean isReportSignatureEnabled;
    private SignedDocumentValidator validator;
    private TrustedListsCertificateSource trustedListsCertificateSource;
    private List<AdvancedSignature> signatures;
}
