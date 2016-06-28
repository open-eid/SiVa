package ee.openeid.siva.sample.upload;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HazelcastUploadFileCacheServiceTest {

    @Autowired
    private UploadFileCacheService fileUploadService;

    private static final String uploadFilename = "random.txt";
    private long timestamp;

    @Before
    public void setUp() throws Exception {
        timestamp = System.currentTimeMillis() / 1000L;
    }

    @Test
    public void uploadFileWithNameRandomTxtWillReturnFilename() throws Exception {
        final MockMultipartFile file = createFile();
        final UploadedFile uploadedFile = fileUploadService.addUploadedFile(timestamp, file);

        assertThat(uploadedFile.getFilename()).contains(uploadFilename);
        assertThat(uploadedFile.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    public void uploadFileWhenNullGiven() throws Exception {
        final UploadedFile uploadedFile = fileUploadService.addUploadedFile(timestamp, null);
        assertThat(uploadedFile.getFilename()).isEqualTo("");
    }

    @Test
    public void deleteUploadedFileWhenPresentWithoutErrors() throws Exception {
        final MultipartFile file = createFile();

        fileUploadService.addUploadedFile(timestamp, file);
        final UploadedFile shouldNotBeNull = fileUploadService.getUploadedFile(timestamp);
        fileUploadService.deleteUploadedFile(timestamp);
        final UploadedFile shouldBeNull = fileUploadService.getUploadedFile(timestamp);

        assertThat(shouldNotBeNull.getFilename()).isEqualTo(uploadFilename);
        assertThat(shouldBeNull.getFilename()).isNull();
    }

    private MockMultipartFile createFile() {
        return new MockMultipartFile("file", uploadFilename, "txt/plain", "hello".getBytes());
    }
}