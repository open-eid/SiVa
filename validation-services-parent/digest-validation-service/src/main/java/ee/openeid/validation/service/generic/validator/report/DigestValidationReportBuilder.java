package ee.openeid.validation.service.generic.validator.report;

import org.apache.commons.collections4.CollectionUtils;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.dss.validation.reports.wrapper.SignatureWrapper;

/**
 * Created by Andrei on 15.03.2018.
 */
public class DigestValidationReportBuilder extends GenericValidationReportBuilder {

    public DigestValidationReportBuilder(Reports dssReports, ValidationLevel validationLevel, ValidationDocument validationDocument, ConstraintDefinedPolicy policy) {
        super(dssReports, validationLevel, validationDocument, policy, false);
    }

    @Override
    protected String changeSignatureFormat(String signatureFormat, String signatureId) {
        if (signatureFormat.contains("LT") && this.isBDoc(signatureId)) {
            signatureFormat = String.format("%s%s", signatureFormat, "-TM");
        }
        return signatureFormat.replace("-", "_");
    }

    private boolean isBDoc(String signatureId) {
        SignatureWrapper wrapper = this.getSignature(signatureId);
        if (CollectionUtils.isEmpty(wrapper.getTimestampList())) {
            return true;
        }
        return false;
    }

    private SignatureWrapper getSignature(final String signatureId) {
        return this.dssReports.getDiagnosticData().getAllSignatures().stream().filter(s -> signatureId.equalsIgnoreCase(s.getId())).findFirst().orElse(null);
    }

}
