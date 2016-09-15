package ee.openeid.siva.validation.service.signature.policy;


import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PredefinedValidationPolicySourceTest {

    private static final String EXPECTED_NO_TYPE_POLICY_NAME = "POLv1";
    private static final String EXPECTED_NO_TYPE_POLICY_URL = "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv1";
    private static final String EXPECTED_NO_TYPE_POLICY_DESCRIPTION = "Policy for validating Electronic Signatures and Electronic Seals "
            + "regardless of the legal type of the signature or seal (according to Regulation (EU) No 910/2014), "
            + "i.e. the fact that the electronic signature or electronic seal is either Advanced electronic Signature "
            + "(AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES) "
            + "does not change the total validation result of the signature.";

    private static final String EXPECTED_QES_POLICY_NAME = "POLv2";
    private static final String EXPECTED_QES_POLICY_URL = "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv2";
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
    public void noTypeValidationPolicyShouldMatchSpecification() {
        ValidationPolicy noTypePol = PredefinedValidationPolicySource.NO_TYPE_POLICY;
        assertEquals(EXPECTED_NO_TYPE_POLICY_NAME, noTypePol.getName());
        assertEquals(EXPECTED_NO_TYPE_POLICY_URL, noTypePol.getUrl());
        assertEquals(EXPECTED_NO_TYPE_POLICY_DESCRIPTION, noTypePol.getDescription());
    }
}
