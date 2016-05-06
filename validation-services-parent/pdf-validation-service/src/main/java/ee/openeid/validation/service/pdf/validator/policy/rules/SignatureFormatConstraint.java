package ee.openeid.validation.service.pdf.validator.policy.rules;

import ee.openeid.validation.service.pdf.validator.policy.EstonianConstraint;
import eu.europa.esig.dss.validation.policy.XmlNode;
import eu.europa.esig.dss.validation.policy.rules.*;

public class SignatureFormatConstraint extends EstonianConstraint {

    protected String currentSignatureLevel;

    public SignatureFormatConstraint(final String level) {
        super(level);
    }

    public void setCurrentSignatureLevel(final String policyId) {
        this.currentSignatureLevel = policyId;
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
            node.addChild(NodeName.STATUS, NodeValue.IGNORED);
            return true;
        }

        if (inform()) {
            node.addChild(NodeName.STATUS, NodeValue.INFORMATION);
            node.addChild(NodeName.INFO, null, messageAttributes).setAttribute(AttributeName.EXPECTED_VALUE, expectedValue).setAttribute("ConstraintValue", currentSignatureLevel);
            return true;
        }

        if (!checkConstraint()) {
            if (warn()) {
                node.addChild(NodeName.STATUS, NodeValue.WARN);
                node.addChild(NodeName.WARNING, MessageTag.EMPTY).setAttribute(AttributeName.EXPECTED_VALUE, expectedValue);
                conclusion.addWarning(MessageTag.EMPTY).setAttribute(AttributeName.EXPECTED_VALUE, expectedValue);

                return true;
            }

            node.addChild(NodeName.STATUS, NodeValue.KO);
            node.addChild(NodeName.ERROR, MessageTag.EMPTY);
            conclusion.setIndication(Indication.INVALID);
            conclusion.addError(MessageTag.EMPTY).setAttribute(AttributeName.EXPECTED_VALUE, expectedValue);

            return false;
        }

        node.addChild(NodeName.STATUS, NodeValue.OK);
        final XmlNode info = node.addChild(NodeName.INFO);
        info.setAttribute(NodeName.IDENTIFIER, currentSignatureLevel);

        return true;
    }

    private boolean checkConstraint() {
        return identifiers.contains(currentSignatureLevel);
    }


}
