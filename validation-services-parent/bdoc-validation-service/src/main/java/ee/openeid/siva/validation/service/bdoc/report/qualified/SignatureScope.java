package ee.openeid.siva.validation.service.bdoc.report.qualified;

import lombok.Data;

@Data
public class SignatureScope {

    private String name;
    private String scope; //TODO: enum?
    private String content;
}
