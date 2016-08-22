package ee.openeid.siva.webapp;

import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.http.RESTProxyError;
import ee.openeid.siva.proxy.http.RESTValidationProxyException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.webapp.transformer.ValidationRequestToProxyDocumentTransformer;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MessageSourceAutoConfiguration.class)
public class ValidationExceptionHandlerTest {

    private ValidationController validationController;

    private ValidationProxy validationProxy;

    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @Before
    public void SetUp() {
        validationController = new ValidationController();
        validationProxy = mock(ValidationProxy.class);
        validationController.setValidationProxy(validationProxy);
        validationController.setTransformer(new ValidationRequestToProxyDocumentTransformer());

        ValidationExceptionHandler validationExceptionHandler = new ValidationExceptionHandler();
        validationExceptionHandler.setMessageSource(messageSource);
        mockMvc = standaloneSetup(validationController).setControllerAdvice(validationExceptionHandler).build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMalformedDocumentExceptionOnValidationExceptionHandler() throws Exception {
        when(validationProxy.validate(any(ProxyDocument.class))).thenThrow(MalformedDocumentException.class);
        MvcResult result = mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content, "{\"requestErrors\":[{\"key\":\"document\",\"message\":\"document malformed or not matching documentType\"}]}");
    }

    @Test
    public void handlingRESTValidationProxyExceptionPreservesInitialErrorInformation() throws Exception {
        RESTProxyError initialError = new RESTProxyError();
        initialError.setHttpStatus(HttpStatus.BAD_REQUEST);
        initialError.setKey("some key");
        initialError.setMessage("some message");
        when(validationProxy.validate(any(ProxyDocument.class))).thenThrow(new RESTValidationProxyException(initialError));
        MvcResult result = mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content, "{\"requestErrors\":[{\"key\":\"some key\",\"message\":\"some message\"}]}");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void givenInvalidPolicyWillReturnErrorJson() throws Exception {
        when(validationProxy.validate(any(ProxyDocument.class))).thenThrow(InvalidPolicyException.class);

        MvcResult mvcResult = mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).contains("signaturePolicy");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValidationServiceExceptionOnValidationExceptionHandler() throws Exception {
        when(validationProxy.validate(any(ProxyDocument.class))).thenThrow(ValidationServiceException.class);
        MvcResult result = mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content, "unfortunately there was an error validating your document");
    }

    private JSONObject request() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", "dGVzdA0K");
        jsonObject.put("filename", "file.bdoc");
        jsonObject.put("documentType", "BDOC");
        return jsonObject;
    }


}
