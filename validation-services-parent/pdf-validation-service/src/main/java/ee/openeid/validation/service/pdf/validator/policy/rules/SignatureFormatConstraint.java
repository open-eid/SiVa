package ee.openeid.validation.service.pdf.validator.policy.rules;

import ee.openeid.validation.service.pdf.validator.policy.EstonianConstraint;
import eu.europa.esig.dss.validation.policy.XmlNode;
import eu.europa.esig.dss.validation.policy.rules.*;

public class SignatureFormatConstraint extends EstonianConstraint {

    private String currentSignatureLevel;

    public SignatureFormatConstraint(final String level) {
        super(level);
    }

    public void setCurrentSignatureLevel(final String policyId) {
        currentSignatureLevel = policyId;
    }

    public String getCurrentSignatureLevel() {
        return currentSignatureLevel;
    }

    /**
     * This method carries out the validation of the constraint.
     *
     * @return true if the constraint is met, false otherwise.
     */
    @Override
    public boolean check() {
        if (ignore()) {
            getNode().addChild(NodeName.STATUS, NodeValue.IGNORED);
            return true;
        }

        if (inform()) {
            getNode().addChild(NodeName.STATUS, NodeValue.INFORMATION);
            getNode().addChild(NodeName.INFO, null, messageAttributes).setAttribute(AttributeName.EXPECTED_VALUE, expectedValue).setAttribute("ConstraintValue", currentSignatureLevel);
            return true;
        }

        if (!checkConstraint()) {
            if (warn()) {
                getNode().addChild(NodeName.STATUS, NodeValue.WARN);
                getNode().addChild(NodeName.WARNING, MessageTag.EMPTY).setAttribute(AttributeName.EXPECTED_VALUE, expectedValue);
                getConclusion().addWarning(MessageTag.EMPTY).setAttribute(AttributeName.EXPECTED_VALUE, expectedValue);

                return true;
            }

            getNode().addChild(NodeName.STATUS, NodeValue.KO);
            getNode().addChild(NodeName.ERROR, MessageTag.EMPTY);
            getConclusion().setIndication(Indication.INVALID);
            getConclusion().addError(MessageTag.EMPTY).setAttribute(AttributeName.EXPECTED_VALUE, expectedValue);

            return false;
        }

        getNode().addChild(NodeName.STATUS, NodeValue.OK);
        final XmlNode info = getNode().addChild(NodeName.INFO);
        info.setAttribute(NodeName.IDENTIFIER, currentSignatureLevel);

        return true;
    }

    private boolean checkConstraint() {
        return getIdentifiers().contains(currentSignatureLevel);
    }
}
