/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.sample.cache;

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
public class AbstractUploadFileCacheServiceTest {

    @Autowired
    private UploadFileCacheService fileUploadService;

    private static final long SECOND_IN_MILLISECONDS = 1000L;
    private static final String UPLOAD_FILENAME = "random.txt";
    private long timestamp;
    private String encodedFilename = "random.bdoc";

    @Before
    public void setUp() throws Exception {
        timestamp = System.currentTimeMillis() / SECOND_IN_MILLISECONDS;
    }

    @Test
    public void uploadFileWithNameRandomTxtWillReturnFilename() throws Exception {
        final MockMultipartFile file = createFile();
        final UploadedFile uploadedFile = fileUploadService.addUploadedFile(timestamp, file, encodedFilename);

        assertThat(uploadedFile.getFilename()).contains(encodedFilename);
        assertThat(uploadedFile.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    public void uploadFileWhenNullGiven() throws Exception {
        final UploadedFile uploadedFile = fileUploadService.addUploadedFile(timestamp, null, "");
        assertThat(uploadedFile.getFilename()).isEqualTo("");
    }

    @Test
    public void deleteUploadedFileWhenPresentWithoutErrors() throws Exception {
        final MultipartFile file = createFile();

        fileUploadService.addUploadedFile(timestamp, file, encodedFilename);
        final UploadedFile shouldNotBeNull = fileUploadService.getUploadedFile(timestamp);
        fileUploadService.deleteUploadedFile(timestamp);
        final UploadedFile shouldBeNull = fileUploadService.getUploadedFile(timestamp);

        assertThat(shouldNotBeNull.getFilename()).isEqualTo(encodedFilename);
        assertThat(shouldBeNull.getFilename()).isNull();
    }

    private static MockMultipartFile createFile() {
        return new MockMultipartFile("file", UPLOAD_FILENAME, "txt/plain", "hello".getBytes());
    }
}