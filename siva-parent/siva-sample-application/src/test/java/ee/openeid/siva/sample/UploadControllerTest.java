package ee.openeid.siva.sample;

import com.gargoylesoftware.htmlunit.WebClient;
import ee.openeid.siva.sample.siva.SivaValidationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WebMvcTest(UploadController.class)
public class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebClient webClient;

    @MockBean
    private SivaValidationService sivabhValidationService;

    @Test
    public void loadingStartPageShouldDisplayUploadForm() throws Exception {
        this.mockMvc.perform(get("/").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk());

    }
}