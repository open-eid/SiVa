/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 *
 * This file is part of the "DSS - Digital Signature Services" project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.ec.markt.dss.validation102853.policy;

import eu.europa.ec.markt.dss.validation102853.xml.XmlNode;

import static eu.europa.ec.markt.dss.validation102853.rules.MessageTag.BBB_VCI_ISFC_ANS_1;

public class SignatureFormatConstraint extends Constraint {

    protected String currentSignatureLevel;

    /**
     * This is the default constructor. It takes a level of the constraint as parameter. The string representing the level is trimmed and capitalized. If there is no corresponding
     * {@code Level} then the {@code Level.IGNORE} is set and a warning is logged.
     *
     * @param level the constraint level string.
     */
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
            node.addChild(STATUS, IGNORED);
            return true;
        }

        if (inform()) {
            node.addChild(STATUS, INFORMATION);
            node.addChild(INFO, null, messageAttributes).setAttribute(EXPECTED_VALUE, expectedValue).setAttribute("ConstraintValue", currentSignatureLevel);
            return true;
        }

        if (!checkConstraint()) {
            if (warn()) {
                node.addChild(STATUS, WARN);
                node.addChild(WARNING, BBB_VCI_ISFC_ANS_1).setAttribute(EXPECTED_VALUE, expectedValue);
                conclusion.addWarning(BBB_VCI_ISFC_ANS_1).setAttribute(EXPECTED_VALUE, expectedValue);

                return true;
            }

            node.addChild(STATUS, KO);
            node.addChild(ERROR, BBB_VCI_ISFC_ANS_1);
            conclusion.setIndication(INVALID);
            conclusion.addError(BBB_VCI_ISFC_ANS_1).setAttribute(EXPECTED_VALUE, expectedValue);

            return false;
        }

        node.addChild(STATUS, OK);
        final XmlNode info = node.addChild(INFO);
        info.setAttribute(IDENTIFIER, currentSignatureLevel);

        return true;
    }

    private boolean checkConstraint() {
        return identifiers.contains(currentSignatureLevel);
    }
}

