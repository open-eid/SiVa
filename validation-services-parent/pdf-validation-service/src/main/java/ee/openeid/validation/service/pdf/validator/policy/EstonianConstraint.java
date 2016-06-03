package ee.openeid.validation.service.pdf.validator.policy;

import ee.openeid.validation.service.pdf.validator.policy.rules.EstonianMessageTag;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.validation.policy.Constraint;
import eu.europa.esig.dss.validation.policy.XmlNode;
import eu.europa.esig.dss.validation.policy.rules.AttributeName;
import eu.europa.esig.dss.validation.policy.rules.NodeName;
import eu.europa.esig.dss.validation.report.Conclusion;

import java.util.List;

/**
 * This class is the customized version of {@link eu.europa.esig.dss.validation.policy.Constraint}
 */
public class EstonianConstraint extends Constraint {

    public EstonianConstraint(String level) throws DSSException {
        super(level);
    }

    public XmlNode create(final XmlNode parentNode, final EstonianMessageTag messageTag) {

        node = parentNode.addChild(NodeName.CONSTRAINT);
        node.addChild(NodeName.NAME, messageTag.getMessage()).setAttribute(AttributeName.NAME_ID, messageTag.name());
        return node;
    }

    public XmlNode getNode() {
        return node;
    }

    public Conclusion getConclusion() {
        return conclusion;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }
}
