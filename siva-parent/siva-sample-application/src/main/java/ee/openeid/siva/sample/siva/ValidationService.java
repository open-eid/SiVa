package ee.openeid.siva.sample.siva;

import ee.openeid.siva.sample.upload.UploadedFile;

import java.io.IOException;

public interface ValidationService {
    String validateDocument(UploadedFile file) throws IOException;
}
