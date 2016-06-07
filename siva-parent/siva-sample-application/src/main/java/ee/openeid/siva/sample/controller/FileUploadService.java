package ee.openeid.siva.sample.controller;

import ee.openeid.siva.sample.configuration.SivaConfigurationProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
class FileUploadService {
    private SivaConfigurationProperties properties;

    String getUploadedFile(final MultipartFile file) throws IOException {
        if (file == null) {
            return StringUtils.EMPTY;
        }

        final Path uploadDir = getUploadDirectory();
        final String fullFilename = uploadDir + File.separator + file.getOriginalFilename();
        if (!Files.exists(uploadDir)) {
            Files.createDirectory(uploadDir);
        }

        final BufferedOutputStream stream = new BufferedOutputStream(
                new FileOutputStream(new File(fullFilename))
        );
        FileCopyUtils.copy(file.getInputStream(), stream);

        return fullFilename;
    }

    Path getUploadDirectory() {
        return Paths.get(properties.getUploadDirectory()).toAbsolutePath();
    }

    void deleteUploadedFile(final String filename) throws IOException {
        Files.deleteIfExists(Paths.get(properties.getUploadDirectory() + File.separator + filename));
    }

    @Autowired
    public void setProperties(final SivaConfigurationProperties properties) {
        this.properties = properties;
    }
}
