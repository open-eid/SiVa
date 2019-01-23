package ee.openeid.siva.webapp.request;

import ee.openeid.siva.webapp.request.validation.annotations.ValidBase64String;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SignatureFile {

    @ValidBase64String(message = "{validation.error.message.signatureFile.invalidBase64}")
    private String signature;

    @Valid
    @NotNull
    @NotEmpty
    private List<Datafile> datafiles;
}
