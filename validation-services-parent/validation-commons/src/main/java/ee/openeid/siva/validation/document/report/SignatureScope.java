package ee.openeid.siva.validation.document.report;

import lombok.Data;

@Data
public class SignatureScope {

    private String name;
    private String scope;
    private String content;
}
