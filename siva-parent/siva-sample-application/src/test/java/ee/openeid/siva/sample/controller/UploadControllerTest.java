package ee.openeid.siva.sample.controller;

import com.domingosuarez.boot.autoconfigure.jade4j.Jade4JAutoConfiguration;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import ee.openeid.siva.sample.siva.SivaValidationService;
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

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
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

    @MockBean
    private SivaValidationService sivaValidationService;

    @MockBean
    private FileUploadService fileUploadService;

    @Test
    public void displayStartPageCheckPresenceOfUploadForm() throws Exception {
        final HtmlPage startPage = webClient.getPage("/");

        assertThat(startPage.getHtmlElementById("validate-btn").getTextContent()).isEqualTo("Validate");
        assertThat(startPage.getBody().getElementsByAttribute("input", "type", "file").size()).isEqualTo(1);
    }

    @Test
    public void uploadPageWithFileReturnsValidationResult() throws Exception {
        given(sivaValidationService.validateDocument(any(File.class)))
                .willReturn("{\"documentName\": \"random.bdoc\", \"validSignaturesCount\": 1, \"signaturesCount\": 1}");
        given(fileUploadService.getUploadedFile(any(MultipartFile.class)))
                .willReturn("random.bdoc");

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
}