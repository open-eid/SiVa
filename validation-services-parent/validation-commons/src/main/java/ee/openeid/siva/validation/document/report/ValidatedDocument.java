package ee.openeid.siva.validation.document.report;

import lombok.Data;

@Data
public class ValidatedDocument {
    private String filename;
    private String fileHashInHex;
    private String hashAlgo;
}
