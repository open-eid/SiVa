package ee.openeid.validation.service.pdf.validator.policy;

import ee.openeid.validation.service.pdf.validator.policy.rules.SignatureFormatConstraint;
import eu.europa.esig.dss.XmlDom;
import eu.europa.esig.dss.validation.policy.EtsiValidationPolicy;
import eu.europa.esig.dss.validation.policy.RuleUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import java.util.List;

public class EstonianEtsiValidationPolicy extends EtsiValidationPolicy {

    public EstonianEtsiValidationPolicy(Document document) {
        super(document);
    }

    public SignatureFormatConstraint getSignatureFormatConstraint() {
        final String level = getValue("/ConstraintsParameters/MainSignature/AcceptableSignatureFormats/@Level");
        if (StringUtils.isNotBlank(level)) {
            final SignatureFormatConstraint constraint = new SignatureFormatConstraint(level);

            final List<XmlDom> profilesAsDom = getElements("/ConstraintsParameters/MainSignature/AcceptableSignatureFormats/Id");
            final List<String> profiles = XmlDom.convertToStringList(profilesAsDom);
            constraint.setIdentifiers(profiles);
            constraint.setExpectedValue(profiles.toString());
            return constraint;
        }
        return null;
    }

    public EstonianConstraint getOcspEarlierThanBestSignatureTimeConstraint() {
        final String XP_ROOT = "/ConstraintsParameters/MainSignature/OcspTimeBeforeBestSignatureTime";
        return getBasicConstraint(XP_ROOT, true);
    }

    @Override
    protected EstonianConstraint getBasicConstraint(final String XP_ROOT, final boolean defaultExpectedValue) {

        final String level = getValue(XP_ROOT + "/@Level");
        if (StringUtils.isNotBlank(level)) {

            final EstonianConstraint constraint = new EstonianConstraint(level);
            String expectedValue = getValue(XP_ROOT + "/text()");
            if (StringUtils.isBlank(expectedValue)) {
                expectedValue = defaultExpectedValue ? TRUE : FALSE;
            }
            constraint.setExpectedValue(expectedValue);
            return constraint;
        }
        return null;
    }

    public EstonianConstraint getRevocationDelayConstraint(Long delay) {
        if (delay != null && getElement("/ConstraintsParameters/MainSignature/OcspTimeTooMuchAfterBestSignatureTime") != null) {
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
