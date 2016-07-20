package ee.openeid.siva.sample.siva;

import ee.openeid.siva.sample.upload.UploadedFile;
import rx.Observable;

import java.io.IOException;

@FunctionalInterface
public interface ValidationService {
    Observable<String> validateDocument(UploadedFile file) throws IOException;
}
