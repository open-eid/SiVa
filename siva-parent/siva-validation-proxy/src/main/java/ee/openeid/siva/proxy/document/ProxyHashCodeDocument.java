package ee.openeid.siva.proxy.document;

import lombok.Data;

@Data
public class ProxyHashCodeDocument {

    private String fileName;
    private String base64Digest;
    private String digestAlgorithm;

}
