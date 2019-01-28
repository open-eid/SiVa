package ee.openeid.siva.webapp.request;

import ee.openeid.siva.webapp.request.validation.annotations.ValidBase64String;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class SignatureFile {

    @ValidBase64String(message = "{validation.error.message.signatureFile.signature.invalidBase64}")
    private String signature;

    @Valid
    private List<Datafile> datafiles;
}
