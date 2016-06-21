package ee.openeid.siva.sample.upload;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadFileCacheService {

    UploadedFile addUploadedFile(final long timestamp, final MultipartFile file) throws IOException;

    UploadedFile getUploadedFile(long timestamp);

    void deleteUploadedFile(final long timestamp) throws IOException;
}
