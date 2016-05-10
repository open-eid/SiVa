package ee.openeid.siva.validation.service.bdoc.report.qualified;

import lombok.Data;

@Data
public class Info {

    private String bestSignatureTime; //TODO: use some common object for time data in report fields
    private String nameId;

}
