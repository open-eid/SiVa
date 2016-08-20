package ee.openeid.siva.validation.document.report;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@XmlRootElement(name = "ValidationReport")
@XmlAccessorType(XmlAccessType.FIELD)
public class QualifiedReport {

    @XmlElement(name = "Policy", required = true)
    private Policy policy;

    @XmlElement(name = "ValidationTime", required = true)
    private String validationTime;

    @XmlElement(name = "DocumentName", required = true)
    private String documentName;

    @XmlElement(name = "SignatureForm", required = true)
    private String signatureForm;

    @XmlElement(name = "Signature")
    @XmlElementWrapper(name = "Signatures", required = true)
    private List<SignatureValidationData> signatures;

    @XmlElement(name = "ValidSignaturesCount", required = true)
    private Integer validSignaturesCount;

    @XmlElement(name = "SignaturesCount", required = true)
    private Integer signaturesCount;

}
