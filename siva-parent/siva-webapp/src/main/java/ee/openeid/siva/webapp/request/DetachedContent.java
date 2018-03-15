package ee.openeid.siva.webapp.request;

import org.hibernate.validator.constraints.NotEmpty;

import ee.openeid.siva.webapp.request.validation.annotations.ValidFilename;
import lombok.Data;

@Data
public class DetachedContent {

    @ValidFilename
    private String fileName;

    @NotEmpty
    private String base64Digest;

    @NotEmpty
    private String digestAlgorithm;

}
