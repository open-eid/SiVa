package ee.openeid.siva.validation.document;

import lombok.Data;

@Data
public class Datafile {

    private String filename;

    private String hashAlgo;

    private String hash;

}
