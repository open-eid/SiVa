package ee.openeid.siva.webapp.request;

import ee.openeid.siva.webapp.request.validation.annotations.ValidBase64String;
import ee.openeid.siva.webapp.request.validation.annotations.ValidReportType;
import ee.openeid.siva.webapp.request.validation.annotations.ValidSignatureFilename;
import ee.openeid.siva.webapp.request.validation.annotations.ValidSignaturePolicy;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class JSONValidateWithHashRequest implements ValidationWithHashRequest {

    @ValidBase64String
    private String signature;

    @ValidSignatureFilename
    private String filename;

    @ValidSignaturePolicy
    private String signaturePolicy;

    @ValidReportType
    private String reportType;

    @Valid
    @NotNull
    @NotEmpty
    private List<Datafile> datafiles;
}
