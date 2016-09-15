package ee.openeid.siva.validation.document.report;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Policy {

    @XmlElement(name = "PolicyDescription", required = true)
    private String policyDescription;

    @XmlElement(name = "PolicyName", required = true)
    private String policyName;

    @XmlElement(name = "PolicyUrl", required = true)
    private String policyUrl;
}
