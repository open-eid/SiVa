package ee.openeid.siva.webapp.request;

import ee.openeid.siva.webapp.request.validation.annotations.ValidBase64String;
import ee.openeid.siva.webapp.request.validation.annotations.ValidFilename;
import ee.openeid.siva.webapp.request.validation.annotations.ValidHashAlgo;
import lombok.Data;

@Data
public class Datafile {

    @ValidFilename
    private String filename;

    @ValidHashAlgo
    private String hashAlgo;

    @ValidBase64String
    private String hash;

}
