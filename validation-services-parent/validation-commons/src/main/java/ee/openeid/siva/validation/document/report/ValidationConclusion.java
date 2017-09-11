package ee.openeid.siva.validation.document.report;

import lombok.Data;

import java.util.List;

@Data
public class ValidationConclusion {

    private Policy policy;

    private String validationTime;

    private String documentName;

    private String signatureForm;

    private List<ValidationWarning> validationWarnings;

    private ValidatedDocument validatedDocument;

    private List<SignatureValidationData> signatures;

    private Integer validSignaturesCount;

    private Integer signaturesCount;

    private List<TimeStampTokenValidationData> timeStampTokens;
}
