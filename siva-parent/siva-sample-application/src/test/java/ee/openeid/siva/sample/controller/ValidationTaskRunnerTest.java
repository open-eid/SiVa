package ee.openeid.siva.sample.controller;

import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.siva.SivaServiceType;
import ee.openeid.siva.sample.siva.ValidationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ValidationTaskRunnerTest {
    @Autowired
    private ValidationTaskRunner validationTaskRunner;

    @MockBean(name = SivaServiceType.SOAP_SERVICE)
    private ValidationService validationServiceSoap;

    @MockBean(name = SivaServiceType.JSON_SERVICE)
    private ValidationService validationServiceJson;

    @Before
    public void setUp() throws Exception {
        given(validationServiceJson.validateDocument(any(UploadedFile.class)))
                .willReturn(Observable.just("{}"));

        given(validationServiceSoap.validateDocument(any(UploadedFile.class)))
                .willReturn(Observable.just("<soap></soap>"));
    }

    @Test
    public void givenValidUploadFileReturnsValidationResultOfAllServices() throws Exception {
        validationTaskRunner.run(new UploadedFile());

        assertThat(validationTaskRunner.getValidationResult(ValidationResultType.JSON)).isEqualTo("{}");
        assertThat(validationTaskRunner.getValidationResult(ValidationResultType.SOAP)).isEqualTo("<soap></soap>");
    }
}