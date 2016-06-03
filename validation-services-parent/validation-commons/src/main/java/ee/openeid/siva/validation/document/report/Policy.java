package ee.openeid.siva.validation.document.report;

import lombok.Data;

@Data
public class Policy {

    public static final Policy SIVA_DEFAULT = sivaDefaultPolicy();

    private String policyDescription;
    private String policyName;
    private String policyUrl;
    private String policyVersion;

    private static Policy sivaDefaultPolicy() {
        Policy policy = new Policy();
        policy.policyDescription = "SiVa validation policy";
        policy.policyName = "SiVa validation policy";
        policy.policyUrl = "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/";
        policy.policyVersion = "1.0";
        return policy;
    }
}
