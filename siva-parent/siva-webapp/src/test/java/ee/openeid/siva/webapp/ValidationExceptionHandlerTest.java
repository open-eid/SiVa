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

package ee.openeid.siva.webapp;

import ee.openeid.siva.proxy.DataFilesProxy;
import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.http.RESTValidationProxyException;
import ee.openeid.siva.proxy.http.RESTValidationProxyRequestException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.webapp.transformer.DataFilesRequestToProxyDocumentTransformer;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MessageSourceAutoConfiguration.class)
public class ValidationExceptionHandlerTest {

    private static final String VALIDATE_URL_TEMPLATE = "/validate";
    private static final String GET_DATA_FILES_URL_TEMPLATE = "/getDataFiles";
    private ValidationController validationController;
    private DataFilesController dataFilesController;
    private ValidationProxy validationProxy;
    private DataFilesProxy dataFilesProxy;
    private MockMvc mockMvc;
    private MockMvc mockMvcDataFiles;
    @Autowired
    private MessageSource messageSource;

    @Before
    public void SetUp() {
        validationController = new ValidationController();
        dataFilesController = new DataFilesController();
        validationProxy = mock(ValidationProxy.class);
        dataFilesProxy = mock(DataFilesProxy.class);
        validationController.setValidationProxy(validationProxy);
        dataFilesController.setDataFilesProxy(dataFilesProxy);
        validationController.setTransformer(new ValidationRequestToProxyDocumentTransformer());
        dataFilesController.setDataFilesTransformer(new DataFilesRequestToProxyDocumentTransformer());

        ValidationExceptionHandler validationExceptionHandler = new ValidationExceptionHandler();
        validationExceptionHandler.setMessageSource(messageSource);
        mockMvc = standaloneSetup(validationController).setControllerAdvice(validationExceptionHandler).build();
        mockMvcDataFiles = standaloneSetup(dataFilesController).setControllerAdvice(validationExceptionHandler).build();
    }

    @Test
    public void testMethodArgumentNotValidExceptionOnValidateExceptionHandler() throws Exception {
        mockMvc.perform(post(VALIDATE_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidDocumentType().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(jsonPath("$.requestErrors[0].key", is("documentType")))
                .andExpect(jsonPath("$.requestErrors[0].message", containsString("Invalid document type")))
                .andReturn();
    }

    @Test
    public void testMethodArgumentNotValidExceptionOnReportTypeExceptionHandler() throws Exception{
        mockMvc.perform(post(VALIDATE_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidReportType().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(jsonPath("$.requestErrors[0].key", is("reportType")))
                .andExpect(jsonPath("$.requestErrors[0].message", containsString("Invalid report type")))
                .andReturn();
    }

    @Test
    public void testMethodArgumentNotValidExceptionOnGetDataFilesExceptionHandler() throws Exception {
        mockMvcDataFiles.perform(post(GET_DATA_FILES_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidDocumentType().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(jsonPath("$.requestErrors[0].key", is("documentType")))
                .andExpect(jsonPath("$.requestErrors[0].message", containsString("Invalid document type. Can only return data files for DDOC type containers.")))
                .andReturn();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMalformedDocumentExceptionOnValidationExceptionHandler() throws Exception {
        when(validationProxy.validate(any(ProxyDocument.class))).thenThrow(MalformedDocumentException.class);
        MvcResult result = mockMvc.perform(post(VALIDATE_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content, "{\"requestErrors\":[{\"key\":\"document\",\"message\":\"Document malformed or not matching documentType\"}]}");
    }

    @Test
    public void testMalformedDocumentExceptionOnGetDataFilesExceptionHandler() throws Exception {
        when(dataFilesProxy.getDataFiles(any(ProxyDocument.class))).thenThrow(MalformedDocumentException.class);
        MvcResult result = mockMvcDataFiles.perform(post(GET_DATA_FILES_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataFileRequest().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content, "{\"requestErrors\":[{\"key\":\"document\",\"message\":\"Document malformed or not matching documentType\"}]}");
    }

    @Test
    public void handlingRESTValidationProxyRequestExceptionPreservesInitialErrorInformation() throws Exception {
        when(validationProxy.validate(any(ProxyDocument.class)))
                .thenThrow(new RESTValidationProxyRequestException("some key", "some message", HttpStatus.BAD_REQUEST));
        MvcResult result = mockMvc.perform(post(VALIDATE_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content, "{\"requestErrors\":[{\"key\":\"some key\",\"message\":\"some message\"}]}");
    }

    @Test
    public void handlingRESTDataFilesProxyRequestExceptionPreservesInitialErrorInformation() throws Exception {
        when(dataFilesProxy.getDataFiles(any(ProxyDocument.class)))
                .thenThrow(new RESTValidationProxyRequestException("some key", "some message", HttpStatus.BAD_REQUEST));
        MvcResult result = mockMvcDataFiles.perform(post(GET_DATA_FILES_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataFileRequest().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content, "{\"requestErrors\":[{\"key\":\"some key\",\"message\":\"some message\"}]}");
    }

    @Test
    public void handlingRESTValidationProxyExceptionPreservesInitialErrorInformation() throws Exception {
        when(validationProxy.validate(any(ProxyDocument.class)))
                .thenThrow(new RESTValidationProxyException("some message", HttpStatus.INTERNAL_SERVER_ERROR));
        MvcResult result = mockMvc.perform(post(VALIDATE_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content, "some message");
    }

    @Test
    public void handlingRESTDataFilesProxyExceptionPreservesInitialErrorInformation() throws Exception {
        when(dataFilesProxy.getDataFiles(any(ProxyDocument.class)))
                .thenThrow(new RESTValidationProxyException("some message", HttpStatus.INTERNAL_SERVER_ERROR));
        MvcResult result = mockMvcDataFiles.perform(post(GET_DATA_FILES_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataFileRequest().toString().getBytes()))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content, "some message");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void givenInvalidPolicyWillReturnErrorJson() throws Exception {
        when(validationProxy.validate(any(ProxyDocument.class))).thenThrow(InvalidPolicyException.class);

        MvcResult mvcResult = mockMvc.perform(post(VALIDATE_URL_TEMPLATE)
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
        MvcResult result = mockMvc.perform(post(VALIDATE_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content, "Unfortunately there was an error validating your document");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDataFilesServiceExceptionOnValidationExceptionHandler() throws Exception {
        when(dataFilesProxy.getDataFiles(any(ProxyDocument.class))).thenThrow(ValidationServiceException.class);
        MvcResult result = mockMvcDataFiles.perform(post(GET_DATA_FILES_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataFileRequest().toString().getBytes()))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content, "Unfortunately there was an error validating your document");
    }

    private JSONObject request() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", "dGVzdA0K");
        jsonObject.put("filename", "file.bdoc");
        return jsonObject;
    }
    private JSONObject dataFileRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", "dGVzdA0K");
        jsonObject.put("filename", "file.bdoc");
        jsonObject.put("documentType", "DDOC");
        return jsonObject;
    }
    private JSONObject requestWithInvalidDocumentType() {
        JSONObject jsonObject = request();
        jsonObject.put("documentType", "asd");
        return jsonObject;
    }
    private JSONObject requestWithInvalidReportType() {
        JSONObject jsonObject = request();
        jsonObject.put("reportType", "asd");
        return jsonObject;
    }

}
