package ee.openeid.siva.validation.document.report;

import lombok.Data;

@Data
public class ValidatedDocument {
    private String documentName;
    private String documentHash;
    private String hashAlgo;
}
