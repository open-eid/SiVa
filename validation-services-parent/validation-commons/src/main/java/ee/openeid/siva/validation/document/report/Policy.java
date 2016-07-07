package ee.openeid.siva.validation.document.report;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Policy {

    public static final Policy SIVA_DEFAULT = sivaDefaultPolicy();

    @XmlElement(name = "PolicyDescription", required = true)
    private String policyDescription;

    @XmlElement(name = "PolicyName", required = true)
    private String policyName;

    @XmlElement(name = "PolicyUrl", required = true)
    private String policyUrl;

    @XmlElement(name = "PolicyVersion", required = true)
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
