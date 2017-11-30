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

package ee.openeid.siva.sample.siva;

import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.test.utils.TestFileUtils;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SivaJSONValidationServiceClientTest {

    @Rule
    public TemporaryFolder testingFolder = new TemporaryFolder();
    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Autowired
    @Qualifier(value = "sivaJSON")
    private ValidationService validationService;
    @Captor
    private ArgumentCaptor<ValidationRequest> validationRequestCaptor;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void validRequestReturnsCorrectValidationResult() throws Exception {
        final String mockResponse = mockServiceResponse();
        final String fileContents = "Hello Testing World";
        final String filename = "testing.bdoc";
        final UploadedFile inputFile = TestFileUtils.generateUploadFile(testingFolder, filename, fileContents);

        final Observable<String> result = validationService.validateDocument("", "", inputFile);
        assertEquals(mockResponse, result.toBlocking().first());

        verify(restTemplate).postForObject(anyString(), validationRequestCaptor.capture(), any());

        assertEquals(filename, validationRequestCaptor.getValue().getFilename());
        assertEquals(Base64Utils.encodeToString(fileContents.getBytes()), validationRequestCaptor.getValue().getDocument());
    }

    @Test
    public void invalidFileTypeGivenRequestDocumentTypeIsNull() throws Exception {
        mockServiceResponse();

        final UploadedFile file = TestFileUtils.generateUploadFile(testingFolder, "testing.exe", "error in file");
        validationService.validateDocument("POLv3","Simple",  file);

        verify(restTemplate).postForObject(anyString(), validationRequestCaptor.capture(), any());
        assertEquals(null, validationRequestCaptor.getValue().getDocumentType());
    }

    @Test
    public void inputFileIsNullThrowsException() throws Exception {
        exception.expect(IOException.class);
        exception.expectMessage("Invalid file object given");

        validationService.validateDocument(null, null, null);
    }

    @Test
    public void givenRestServiceIsUnreachableReturnsGenericSystemError() throws Exception {
        final UploadedFile file = TestFileUtils.generateUploadFile(testingFolder, "testing.bdoc", "simple file");
        BDDMockito.given(restTemplate.postForObject(anyString(), any(ValidationRequest.class), any()))
                .willThrow(new ResourceAccessException("Failed to connect to SiVa REST"));

        Observable<String> result = validationService.validateDocument("", "", file);
        verify(restTemplate).postForObject(anyString(), validationRequestCaptor.capture(), any());

        Assertions.assertThat(result.toBlocking().first()).contains("errorCode");
        Assertions.assertThat(result.toBlocking().first()).contains("errorMessage");
    }

    private String mockServiceResponse() {
        final String mockResponse = "{\"jsonValidationResult\": \"TOTAL-PASSED\"}";
        BDDMockito.given(restTemplate.postForObject(anyString(), any(ValidationRequest.class), any()))
                .willReturn(mockResponse);

        return mockResponse;
    }
}
