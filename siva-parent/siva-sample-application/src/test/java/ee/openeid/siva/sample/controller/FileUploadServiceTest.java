package ee.openeid.siva.sample.controller;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"siva.uploadDirectory=test-upload-dir"})
public class FileUploadServiceTest {
    @Value("${siva.uploadDirectory}")
    private String uploadDirectory;

    @Autowired
    private FileUploadService fileUploadService;
    private static final String uploadFilename = "random.txt";

    @After
    public void tearDown() throws Exception {
        deleteUploadDirectory(Paths.get(fileUploadService.getUploadDirectory() + File.separator + uploadFilename));
        deleteUploadDirectory(fileUploadService.getUploadDirectory());
    }

    @Test
    public void uploadFileWithNameRandomTxtWillReturnFilename() throws Exception {
        final MockMultipartFile file = createFile();
        final String uploadedFilename = fileUploadService.getUploadedFile(file);

        assertThat(uploadedFilename).contains(fileUploadService.getUploadDirectory() + File.separator + uploadFilename);
    }

    @Test
    public void uploadFileWhenUploadDirectoryNotPresentWillReturnFilename() throws Exception {
        deleteUploadDirectory(fileUploadService.getUploadDirectory());
        final MockMultipartFile file = createFile();
        final String uploadedFile = fileUploadService.getUploadedFile(file);

        assertThat(uploadedFile).isEqualTo(fileUploadService.getUploadDirectory() + File.separator + uploadFilename);
    }

    @Test
    public void uploadFileWhenNullGiven() throws Exception {
        final String uploadedFile = fileUploadService.getUploadedFile(null);

        assertThat(uploadedFile).isEqualTo("");
    }

    @Test
    public void deleteUploadedFileWhenPresentWithoutErrors() throws Exception {
        final MultipartFile file = createFile();
        final String uploadedFile = fileUploadService.getUploadedFile(file);
        fileUploadService.deleteUploadedFile(Paths.get(uploadedFile));

        assertThat(Files.notExists(Paths.get(uploadedFile))).isTrue();
    }

    private MockMultipartFile createFile() {
        return new MockMultipartFile("file", uploadFilename, "txt/plain", "hello".getBytes());
    }

    private void deleteUploadDirectory(final Path deletePath) throws IOException {
        Files.deleteIfExists(deletePath);
    }


}