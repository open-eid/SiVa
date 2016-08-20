package ee.openeid.siva.sample.cache;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadedFile implements Serializable {
    private long timestamp;
    private String filename;
    private String encodedFile;
}
