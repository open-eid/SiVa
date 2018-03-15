package ee.openeid.siva.webapp.request;

import lombok.Data;

@Data
public class SignatureContent {

    private String fileName;
    private String base64Value;

}
