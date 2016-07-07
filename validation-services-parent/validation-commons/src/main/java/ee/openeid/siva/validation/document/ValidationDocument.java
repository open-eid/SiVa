package ee.openeid.siva.validation.document;

import eu.europa.esig.dss.MimeType;
import lombok.Data;

@Data
public class ValidationDocument {

    protected MimeType mimeType;

    private byte[] bytes;

    private String name;

    private String signaturePolicy;

}