package ee.openeid.siva.validation.document;


import lombok.Data;

import java.util.List;

@Data
public class SignatureFile {

    private byte[] signature;

    private List<Datafile> datafiles;
}
