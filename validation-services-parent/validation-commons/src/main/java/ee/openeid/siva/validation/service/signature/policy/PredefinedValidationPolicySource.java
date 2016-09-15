package ee.openeid.siva.validation.service.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PredefinedValidationPolicySource {

    public static final ValidationPolicy NO_TYPE_POLICY = createValidationPolicyPolV1();
    public static final ValidationPolicy QES_POLICY = createValidationPolicyPolV2();

    private static final String POL_V1_NAME = "POLv1";
    private static final String POL_V1_URL = "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv1";
    private static final String POL_V1_DESCRIPTION = "Policy for validating Electronic Signatures and Electronic Seals "
            + "regardless of the legal type of the signature or seal (according to Regulation (EU) No 910/2014), "
            + "i.e. the fact that the electronic signature or electronic seal is either Advanced electronic Signature "
            + "(AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES) "
            + "does not change the total validation result of the signature.";

    private static final String POL_V2_NAME = "POLv2";
    private static final String POL_V2_URL = "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv2";
    private static final String POL_V2_DESCRIPTION = "Policy for validating Qualified Electronic Signatures and "
            + "Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been "
            + "recognized as Advanced electronic Signatures (AdES) and AdES supported by a "
            + "Qualified Certificate (AdES/QC) do not produce a positive validation result.";

    private static ValidationPolicy createValidationPolicyPolV1() {
        return new ValidationPolicy(POL_V1_NAME, POL_V1_DESCRIPTION, POL_V1_URL);
    }

    private static ValidationPolicy createValidationPolicyPolV2() {
        return new ValidationPolicy(POL_V2_NAME, POL_V2_DESCRIPTION, POL_V2_URL);
    }
}
