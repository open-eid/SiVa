package ee.openeid.siva.validation.service.bdoc.report.qualified;

import lombok.Data;

@Data
public class Error {

    private String certificateId;
    private String nameId;
    private String content;
}
