package ee.openeid.siva.sample.siva;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SivaSOAPValidationServiceClientTest {

    @Autowired
    @Qualifier(value = "sivaSOAP")
    private ValidationService validationService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void givenNoImplementationWillReturnNUll() throws Exception {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("File not found");
        validationService.validateDocument(null);
    }
}