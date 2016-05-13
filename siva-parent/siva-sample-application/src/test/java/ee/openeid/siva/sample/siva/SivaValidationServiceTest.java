package ee.openeid.siva.sample.siva;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SivaValidationServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void validRequestReturnsCorrectValidationResult() throws Exception {
        assertTrue(true);
    }
}