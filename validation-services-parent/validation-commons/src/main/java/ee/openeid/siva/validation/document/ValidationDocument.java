package ee.openeid.siva.validation.document;

import lombok.Data;

@Data
public class ValidationDocument {

    private byte[] bytes;

    private String name;

    private String signaturePolicy;

    private String dataBase64Encoded;

}