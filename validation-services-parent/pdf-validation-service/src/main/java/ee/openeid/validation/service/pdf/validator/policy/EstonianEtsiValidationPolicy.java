package ee.openeid.validation.service.pdf.validator.policy;

import eu.europa.esig.dss.validation.policy.EtsiValidationPolicy;
import eu.europa.esig.dss.validation.policy.RuleUtils;
import org.w3c.dom.Document;

public class EstonianEtsiValidationPolicy extends EtsiValidationPolicy {

    public EstonianEtsiValidationPolicy(Document document) {
        super(document);
    }

    public EstonianConstraint getRevocationDelayConstraint(Long delay) {
        if (delay != null) {
            if (delay > getRevocationDelayTimeForFail()) {

                final EstonianConstraint constraint = new EstonianConstraint("FAIL");
                constraint.setExpectedValue(TRUE);
                constraint.setValue(FALSE);
                return constraint;
            } else if (delay > getRevocationDelayTimeForWarn()) {
                final EstonianConstraint constraint = new EstonianConstraint("WARN");
                constraint.setExpectedValue(TRUE);
                constraint.setValue(FALSE);
                return constraint;
            }
        }
        return null;
    }

    private Long getRevocationDelayTimeForFail() {
        Long minimalDelay = getLongValue("/ConstraintsParameters/MainSignature/OcspTimeTooMuchAfterBestSignatureTime/Fail/MinimalDelay/text()");
        return getRevocationDelayTime(minimalDelay);
    }

    private Long getRevocationDelayTimeForWarn() {
        Long minimalDelay = getLongValue("/ConstraintsParameters/MainSignature/OcspTimeTooMuchAfterBestSignatureTime/Warn/MinimalDelay/text()");
        return getRevocationDelayTime(minimalDelay);
    }

    private Long getRevocationDelayTime(Long delay) {
        String delayTimeUnit = getValue("/ConstraintsParameters/MainSignature/OcspTimeTooMuchAfterBestSignatureTime/@Unit");
        Long minimalDelayInMillis = RuleUtils.convertDuration(delayTimeUnit, "MILLISECONDS", delay);
        return minimalDelayInMillis;
    }

}
