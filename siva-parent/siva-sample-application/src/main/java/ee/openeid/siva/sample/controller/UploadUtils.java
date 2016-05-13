package ee.openeid.siva.sample.controller;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

final class UploadUtils {
    private static String uploadDirectory = "upload-dir";

    private UploadUtils() {
    }

    static String getUploadedFile(final MultipartFile file) throws IOException {
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

    private static Path getUploadDirectory() {
        return Paths.get(uploadDirectory).toAbsolutePath();
    }

    static void deleteUploadedFile(final Path filename) throws IOException {
        if (!Files.exists(filename)) {
            return;
        }

        Files.delete(filename);
    }
}
