package ee.openeid.siva.validation.service.bdoc.report.qualified;

import lombok.Data;

import java.util.List;

@Data
public class QualifiedReport {

    private Policy policy;
    private String validationTime;
    private String documentName;
    private List<SignatureValidationData> signatures;
    private Integer validSignaturesCount;
    private Integer signaturesCount;

}
