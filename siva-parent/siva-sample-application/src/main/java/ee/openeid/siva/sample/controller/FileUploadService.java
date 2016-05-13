package ee.openeid.siva.sample.controller;

import org.springframework.beans.factory.annotation.Value;
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
    private String uploadDirectory;

    String getUploadedFile(final MultipartFile file) throws IOException {
        if (file == null) {
            return "";
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
        return Paths.get(uploadDirectory).toAbsolutePath();
    }

    void deleteUploadedFile(final Path filename) throws IOException {
        Files.deleteIfExists(filename);
    }

    @Value("${siva.uploadDirectory}")
    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }
}
