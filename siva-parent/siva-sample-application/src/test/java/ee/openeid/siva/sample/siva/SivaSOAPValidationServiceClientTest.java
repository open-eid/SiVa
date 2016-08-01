package ee.openeid.siva.sample.siva;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SivaSOAPValidationServiceClientTest {

    @Autowired
    @Qualifier(value = "sivaSOAP")
    private ValidationService validationService;

    @Test
    public void givenNoImplementationWillReturnNUll() throws Exception {
        assertThat(validationService.validateDocument(null)).isNull();
    }
}