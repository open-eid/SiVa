package ee.openeid.siva.validation.service.bdoc.report.qualified;

import lombok.Data;

@Data
public class Policy {

    public static final Policy SIVA_DEFAULT = sivaDefaultPolicy();

    private String policyDescription;
    private String policyName;
    private String policyUrl;

    private static Policy sivaDefaultPolicy() {
        Policy policy = new Policy();
        policy.policyDescription = "SiVa validation policy";
        policy.policyName = "SiVa validation policy";
        policy.policyUrl = "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/";
        return policy;
    }
}
