package ee.openeid.siva.sample.siva;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SivaValidationServiceTest {

    @Autowired
    private SivaValidationService sivaValidationService;

    @Rule
    public TemporaryFolder testingFolder = new TemporaryFolder();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Captor
    private ArgumentCaptor<ValidationRequest> validationRequestCaptor;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void validRequestReturnsCorrectValidationResult() throws Exception {
        final String mockResponse = mockServiceResponse();
        final String fileContents = "Hello Testing World";
        final String fileName = "testing.bdoc";
        final File inputFile = getFile(fileContents, fileName);

        final String result = sivaValidationService.validateDocument(inputFile);
        assertEquals(mockResponse, result);

        verify(restTemplate).postForObject(anyString(), validationRequestCaptor.capture(), any());
        assertEquals(FileType.BDOC, validationRequestCaptor.getValue().getDocumentType());
        assertEquals(fileName, validationRequestCaptor.getValue().getFilename());
        assertEquals(Base64Utils.encodeToString(fileContents.getBytes()), validationRequestCaptor.getValue().getDocument());
    }

    @Test
    public void invalidFileTypeGivenRequestDocumentTypeIsNull() throws Exception {
        mockServiceResponse();

        final File file = getFile("error in file", "testing.exe");
        sivaValidationService.validateDocument(file);

        verify(restTemplate).postForObject(anyString(), validationRequestCaptor.capture(), any());
        assertEquals(null, validationRequestCaptor.getValue().getDocumentType());
    }

    @Test
    public void inputFileIsNullThrowsException() throws Exception {
        exception.expect(IOException.class);
        exception.expectMessage("Invalid file object given");

        sivaValidationService.validateDocument(null);
    }

    @Test
    public void givenRestServiceIsUnreachableReturnsGenericSystemError() throws Exception {
        final File file = getFile("simple file", "testing.bdoc");
        given(restTemplate.postForObject(anyString(), any(ValidationRequest.class), any()))
                .willThrow(new ResourceAccessException("Failed to connect to SiVa REST"));

        String result = sivaValidationService.validateDocument(file);
        verify(restTemplate).postForObject(anyString(), validationRequestCaptor.capture(), any());

        assertThat(result).contains("errorCode");
        assertThat(result).contains("errorMessage");
    }



    private File getFile(String fileContents, String fileName) throws IOException {
        final File inputFile = testingFolder.newFile(fileName);
        FileUtils.writeStringToFile(inputFile, fileContents);
        return inputFile;
    }

    private String mockServiceResponse() {
        final String mockResponse = "{\"validationResult\": \"TOTAL-PASSED\"}";
        given(restTemplate.postForObject(anyString(), any(ValidationRequest.class), any()))
                .willReturn(mockResponse);

        return mockResponse;
    }
}