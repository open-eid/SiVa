package ee.openeid.siva.sample.controller;

import com.domingosuarez.boot.autoconfigure.jade4j.Jade4JAutoConfiguration;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import ee.openeid.siva.sample.ci.info.BuildInfo;
import ee.openeid.siva.sample.configuration.GoogleAnalyticsProperties;
import ee.openeid.siva.sample.siva.ValidationService;
import ee.openeid.siva.sample.upload.UploadFileCacheService;
import ee.openeid.siva.sample.upload.UploadedFile;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UploadController.class)
@ImportAutoConfiguration(Jade4JAutoConfiguration.class)
public class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebClient webClient;

    @MockBean(name = "sivaJSON")
    private ValidationService validationService;

    @MockBean
    private BuildInfo buildInfo;

    @MockBean
    private UploadFileCacheService hazelcastUploadFileCacheService;

    @MockBean
    private GoogleAnalyticsProperties googleAnalyticsProperties;

    @Before
    public void setUp() throws Exception {
        given(googleAnalyticsProperties.getTrackingId()).willReturn("random-tracking-id");
    }

    @Test
    @Ignore
    public void displayStartPageCheckPresenceOfUploadForm() throws Exception {
        final HtmlPage startPage = webClient.getPage("/");

        assertThat(startPage.getHtmlElementById("validate-btn").getTextContent()).isEqualTo("Validate");
        assertThat(startPage.getBody().getElementsByAttribute("input", "type", "file").size()).isEqualTo(1);
    }

    @Test
    public void uploadPageWithFileReturnsValidationResult() throws Exception {
        given(validationService.validateDocument(any(UploadedFile.class)))
                .willReturn("{\"documentName\": \"random.bdoc\", \"validSignaturesCount\": 1, \"signaturesCount\": 1}");

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFilename("random.bdoc");
        given(hazelcastUploadFileCacheService.addUploadedFile(anyLong(), any(MultipartFile.class)))
                .willReturn(uploadedFile);

        final MockMultipartFile uploadFile = new MockMultipartFile(
                "file",
                "random.bdoc",
                "application/vnd.etsi.asic-e+zip",
                "bdoc content".getBytes()
        );

        mockMvc.perform(fileUpload("/upload").file(uploadFile))
                .andExpect(status().is(302))
                .andExpect(content().string(""));
    }

    @Test
    public void fileUploadFailedRedirectedBackToStartPage() throws Exception {
        given(hazelcastUploadFileCacheService.addUploadedFile(anyLong(), any(MultipartFile.class)))
                .willThrow(new IOException("File upload failed"));

        final MockMultipartFile uploadFile = new MockMultipartFile(
                "file",
                "random.bdoc",
                "application/vnd.etsi.asic-e+zip",
                "bdoc content".getBytes()
        );

        mockMvc.perform(fileUpload("/upload").file(uploadFile))
                .andExpect(status().is(302));
    }

    @Test
    public void emptyFileUploadedRedirectsBackToStartPage() throws Exception {
        final MockMultipartFile uploadFile = new MockMultipartFile(
                "file",
                "random.bdoc",
                "application/vnd.etsi.asic-e+zip",
                "".getBytes()
        );

        mockMvc.perform(fileUpload("/upload").file(uploadFile))
                .andExpect(status().is(302));
    }
}