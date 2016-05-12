package ee.openeid.siva.validation.document.report;

import lombok.Data;

@Data
public class Info {

    private String bestSignatureTime; //TODO: use some common object for time data in report fields
    private String nameId;

}
