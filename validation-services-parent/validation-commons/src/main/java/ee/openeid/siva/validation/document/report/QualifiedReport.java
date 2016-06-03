package ee.openeid.siva.validation.document.report;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement
public class QualifiedReport {

    private Policy policy;
    private String validationTime;
    private String documentName;
    private List<SignatureValidationData> signatures;
    private Integer validSignaturesCount;
    private Integer signaturesCount;

}
