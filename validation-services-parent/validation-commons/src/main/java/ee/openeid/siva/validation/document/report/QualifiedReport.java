package ee.openeid.siva.validation.document.report;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
public class QualifiedReport {

    private Policy policy;

    private String validationTime;

    private String documentName;

    private String signatureForm;

    private List<SignatureValidationData> signatures;

    private Integer validSignaturesCount;

    private Integer signaturesCount;

}
