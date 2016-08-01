package ee.openeid.siva.sample.cache;

import org.apache.commons.codec.binary.Base64;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@CacheConfig(cacheNames = "file")
public class AbstractUploadFileCacheService implements UploadFileCacheService {

    @Override
    @CachePut(key = "#timestamp")
    public UploadedFile addUploadedFile(final long timestamp, final MultipartFile file) throws IOException {
        final UploadedFile uploadedFile = new UploadedFile();
        setFilename(file, uploadedFile);
        setFileContents(file, uploadedFile);

        uploadedFile.setTimestamp(timestamp);
        return uploadedFile;
    }

    private static void setFileContents(MultipartFile file, UploadedFile uploadedFile) throws IOException {
        final String fileContents = file == null ? null : Base64.encodeBase64String(file.getBytes());
        uploadedFile.setEncodedFile(fileContents);
    }

    private static void setFilename(MultipartFile file, UploadedFile uploadedFile) {
        final String filename = file  == null ? "" : file.getOriginalFilename();
        uploadedFile.setFilename(filename);
    }

    @Override
    @Cacheable(key = "#timestamp")
    public UploadedFile getUploadedFile(long timestamp) {
        return new UploadedFile();
    }

    @Override
    @CacheEvict(key = "#timestamp")
    public void deleteUploadedFile(final long timestamp) {
    }
}
