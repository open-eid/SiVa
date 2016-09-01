package ee.openeid.siva.xroad;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.ValidationService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MessageSourceAutoConfiguration.class)
public class XROADValidationExceptionHandlerTest {

    private XROADValidationApplication validationController;

    private ValidationService validationService;

    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @Before
    public void SetUp() {
        validationController = new XROADValidationApplication();
        validationService = mock(ValidationService.class);
        validationController.setValidationService(validationService);
        XROADValidationExceptionHandler validationExceptionHandler = new XROADValidationExceptionHandler();
        validationExceptionHandler.setMessageSource(messageSource);
        mockMvc = standaloneSetup(validationController).setControllerAdvice(validationExceptionHandler).build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMalformedDocumentExceptionOnValidationExceptionHandler() throws Exception {
        when(validationService.validateDocument(any(ValidationDocument.class))).thenThrow(MalformedDocumentException.class);
        MvcResult result = mockMvc.perform(post("/xroad-validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockRequest().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals(content, "{\"key\":\"document\",\"message\":\"Document malformed or not matching documentType\"}");
    }

    private JSONObject mockRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bytes", "");
        jsonObject.put("name", "");
        jsonObject.put("signaturePolicy", "");
        jsonObject.put("dataBase64Encoded", "");
        return jsonObject;
    }
}
