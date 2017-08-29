/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.validation.service.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PredefinedValidationPolicySourceTest {

    private static final String EXPECTED_ADES_POLICY_NAME = "POLv3";
    private static final String EXPECTED_ADES_POLICY_URL = "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv3";
    private static final String EXPECTED_ADES_POLICY_DESCRIPTION = "Policy for validating Electronic Signatures and Electronic Seals "
            + "regardless of the legal type of the signature or seal (according to Regulation (EU) No 910/2014), "
            + "i.e. the fact that the electronic signature or electronic seal is either Advanced electronic Signature "
            + "(AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES) "
            + "does not change the total validation result of the signature.";

    private static final String EXPECTED_ADES_QS_POLICY_NAME = "POLv4";
    private static final String EXPECTED_ADES_QS_POLICY_URL = "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv4";
    private static final String EXPECTED_ADES_QS_POLICY_DESCRIPTION = "Policy for validating Qualified Electronic Signatures and " +
            "Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been recognized as" +
            " Advanced electronic Signatures (AdES) do not produce a positive validation result.";

    private static final String EXPECTED_QES_POLICY_NAME = "POLv5";
    private static final String EXPECTED_QES_POLICY_URL = "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv5";
    private static final String EXPECTED_QES_POLICY_DESCRIPTION = "Policy for validating Qualified Electronic Signatures and "
            + "Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been "
            + "recognized as Advanced electronic Signatures (AdES) and AdES supported by a "
            + "Qualified Certificate (AdES/QC) do not produce a positive validation result.";

    @Test
    public void qesValidationPolicyShouldMatchSpecification() {
        ValidationPolicy qesPol = PredefinedValidationPolicySource.QES_POLICY;
        assertEquals(EXPECTED_QES_POLICY_NAME, qesPol.getName());
        assertEquals(EXPECTED_QES_POLICY_URL, qesPol.getUrl());
        assertEquals(EXPECTED_QES_POLICY_DESCRIPTION, qesPol.getDescription());
    }

    @Test
    public void adesValidationPolicyShouldMatchSpecification() {
        ValidationPolicy noTypePol = PredefinedValidationPolicySource.ADES_POLICY;
        assertEquals(EXPECTED_ADES_POLICY_NAME, noTypePol.getName());
        assertEquals(EXPECTED_ADES_POLICY_URL, noTypePol.getUrl());
        assertEquals(EXPECTED_ADES_POLICY_DESCRIPTION, noTypePol.getDescription());
    }

    @Test
    public void adesQsValidationPolicyShouldMatchSpecification() {
        ValidationPolicy noTypePol = PredefinedValidationPolicySource.ADES_QS_POLICY;
        assertEquals(EXPECTED_ADES_QS_POLICY_NAME, noTypePol.getName());
        assertEquals(EXPECTED_ADES_QS_POLICY_URL, noTypePol.getUrl());
        assertEquals(EXPECTED_ADES_QS_POLICY_DESCRIPTION, noTypePol.getDescription());
    }
}
