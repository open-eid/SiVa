package ee.openeid.siva.proxy.document;

import ee.openeid.siva.proxy.ProxyRequest;
import ee.openeid.siva.validation.document.SignatureFile;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
public class ProxyHashcodeDataSet extends ProxyRequest {
    private List<SignatureFile> signatureFiles;
}
