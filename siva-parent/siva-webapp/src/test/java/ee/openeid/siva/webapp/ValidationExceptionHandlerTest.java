/*
 * Copyright 2018 - 2025 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.proxy.ContainerValidationProxy;
import ee.openeid.siva.proxy.DataFilesProxy;
import ee.openeid.siva.proxy.HashcodeValidationProxy;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.validation.exception.DocumentRequirementsException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.MalformedSignatureFileException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.webapp.request.Datafile;
import ee.openeid.siva.webapp.request.SignatureFile;
import ee.openeid.siva.webapp.transformer.DataFilesRequestToProxyDocumentTransformer;
import ee.openeid.siva.webapp.transformer.HashcodeValidationRequestToProxyDocumentTransformer;
import ee.openeid.siva.webapp.transformer.ValidationRequestToProxyDocumentTransformer;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MessageSourceAutoConfiguration.class)
class ValidationExceptionHandlerTest {

    private static final String VALIDATE_URL_TEMPLATE = "/validate";
    private static final String HASHCODE_VALIDATION_URL_TEMPLATE = "/validateHashcode";
    private static final String GET_DATA_FILES_URL_TEMPLATE = "/getDataFiles";
    private ValidationController validationController;
    private DataFilesController dataFilesController;
    private ContainerValidationProxy validationProxy;
    private DataFilesProxy dataFilesProxy;
    private HashcodeValidationProxy hashcodeValidationProxy;
    private MockMvc mockMvc;
    private MockMvc mockMvcDataFiles;
    @Autowired
    private MessageSource messageSource;

    @BeforeEach
    public void SetUp() {
        validationController = new ValidationController();
        dataFilesController = new DataFilesController();
        validationProxy = Mockito.mock(ContainerValidationProxy.class);
        dataFilesProxy = Mockito.mock(DataFilesProxy.class);
        hashcodeValidationProxy = Mockito.mock(HashcodeValidationProxy.class);

        validationController.setContainerValidationProxy(validationProxy);
        validationController.setTransformer(new ValidationRequestToProxyDocumentTransformer());
        validationController.setHashcodeValidationProxy(hashcodeValidationProxy);
        validationController.setHashRequestTransformer(new HashcodeValidationRequestToProxyDocumentTransformer());
        dataFilesController.setDataFilesProxy(dataFilesProxy);
        dataFilesController.setDataFilesTransformer(new DataFilesRequestToProxyDocumentTransformer());

        ValidationExceptionHandler validationExceptionHandler = new ValidationExceptionHandler();
        validationExceptionHandler.setMessageSource(messageSource);
        mockMvc = MockMvcBuilders.standaloneSetup(validationController).setControllerAdvice(validationExceptionHandler).build();
        mockMvcDataFiles = MockMvcBuilders.standaloneSetup(dataFilesController).setControllerAdvice(validationExceptionHandler).build();
    }

    @Test
    void testMethodArgumentNotValidExceptionOnReportTypeExceptionHandler() throws Exception{
        mockMvc.perform(post(VALIDATE_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidReportType().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].key", is("reportType")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].message", containsString("Invalid report type")))
                .andReturn();
    }

    @Test
    void testMethodArgumentNotValidExceptionOnGetDataFilesExceptionHandler() throws Exception {
        mockMvcDataFiles.perform(post(GET_DATA_FILES_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidDataFileFilename().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].key", is("filename")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].message", containsString("Invalid filename. Can only return data files for DDOC type containers.")))
                .andReturn();
    }

    @SuppressWarnings("unchecked")
    @Test
    void testMalformedDocumentExceptionOnValidationExceptionHandler() throws Exception {
        when(validationProxy.validate(any(ProxyDocument.class))).thenThrow(MalformedDocumentException.class);
        MvcResult result = mockMvc.perform(post(VALIDATE_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"requestErrors\":[{\"key\":\"document\",\"message\":\"Document malformed or not matching documentType\"}]}", content);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testDocumentRequirementsExceptionOnValidationExceptionHandler() throws Exception {
        when(validationProxy.validate(any(ProxyDocument.class))).thenThrow(DocumentRequirementsException.class);
        MvcResult result = mockMvc.perform(post(VALIDATE_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"requestErrors\":[{\"key\":\"document\",\"message\":\"Document does not meet the requirements\"}]}", content);
    }

    @Test
    void testMalformedDocumentExceptionOnGetDataFilesExceptionHandler() throws Exception {
        when(dataFilesProxy.getDataFiles(any(ProxyDocument.class))).thenThrow(MalformedDocumentException.class);
        MvcResult result = mockMvcDataFiles.perform(post(GET_DATA_FILES_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataFileRequest().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"requestErrors\":[{\"key\":\"document\",\"message\":\"Document malformed or not matching documentType\"}]}", content);
    }

    @SuppressWarnings("unchecked")
    @Test
    void givenInvalidPolicyWillReturnErrorJson() throws Exception {
        when(validationProxy.validate(any(ProxyDocument.class))).thenThrow(InvalidPolicyException.class);

        MvcResult mvcResult = mockMvc.perform(post(VALIDATE_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertThat(mvcResult.getResponse().getContentAsString()).contains("signaturePolicy");
    }

    @SuppressWarnings("unchecked")
    @Test
    void testValidationServiceExceptionOnValidationExceptionHandler() throws Exception {
        when(validationProxy.validate(any(ProxyDocument.class))).thenThrow(ValidationServiceException.class);
        MvcResult result = mockMvc.perform(post(VALIDATE_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"requestErrors\":[{\"key\":\"document\",\"message\":\"Unfortunately there was an error validating your document\"}]}", content);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testDataFilesServiceExceptionOnValidationExceptionHandler() throws Exception {
        when(dataFilesProxy.getDataFiles(any(ProxyDocument.class))).thenThrow(ValidationServiceException.class);
        MvcResult result = mockMvcDataFiles.perform(post(GET_DATA_FILES_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataFileRequest().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"requestErrors\":[{\"key\":\"document\",\"message\":\"Unfortunately there was an error validating your document\"}]}", content);
    }

    @Test
    void testHashcodeValidationMalformedSignatureFileExceptionHandler() throws Exception {
        when(hashcodeValidationProxy.validate(any())).thenThrow(MalformedSignatureFileException.class);
        mockMvc.perform(post(HASHCODE_VALIDATION_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidFormatSignatureFile().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].key", is("signatureFiles.signature")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].message", containsString("Signature file malformed")))
                .andReturn();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/asd", "/!@#", "/", "/012", "/你好"})
    void performPostRequest_WhenEndpointIsInvalid_ThrowsNoHandlerFoundException(String endpoint) throws Exception {
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].key", is("endpointNotFound")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].message", containsString("Endpoint not found")))
                .andReturn();
    }

    @ParameterizedTest
    @MethodSource("provideHttpMethodsAndValidationEndpoints")
    void performRequest_WhenEndpointIsSetToValidateOrHashcodeAndHttpMethodIsInvalid_ThrowsHttpRequestMethodNotSupportedException(HttpMethod httpMethod, String endpoint) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(httpMethod, endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].key", is("methodNotAllowed")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].message", containsString("Only the following request methods are supported: POST")))
                .andReturn();
    }

    @ParameterizedTest
    @MethodSource("provideHttpMethods")
    void performRequest_WhenEndpointIsSetToGetDataFilesAndHttpMethodIsInvalid_ThrowsHttpRequestMethodNotSupportedException(HttpMethod httpMethod) throws Exception {
        mockMvcDataFiles.perform(MockMvcRequestBuilders.request(httpMethod, GET_DATA_FILES_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataFileRequest().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].key", is("methodNotAllowed")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].message", containsString("Only the following request methods are supported: POST")))
                .andReturn();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidJsonContentAndValidationEndpoints")
    void performPostRequest_WhenEndpointIsSetToValidateOrHashcodeAndJsonContentIsInvalid_ThrowsHttpMessageNotReadableException(String endpoint, String invalidJsonContent) throws Exception {
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJsonContent.getBytes()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].key", is("requestBodyNotReadable")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].message", containsString("Request body is malformed and cannot be read")))
                .andReturn();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidJsonContent")
    void performPostRequest_WhenEndpointIsSetToGetDataFilesAndJsonContentIsInvalid_ThrowsHttpMessageNotReadableException(String invalidJsonContent) throws Exception {
        mockMvcDataFiles.perform(post(GET_DATA_FILES_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJsonContent.getBytes()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].key", is("requestBodyNotReadable")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].message", containsString("Request body is malformed and cannot be read")))
                .andReturn();
    }

    @ParameterizedTest
    @MethodSource("provideUnsupportedMediaTypesAndValidationEndpoints")
    void performPostRequest_WhenEndpointIsSetToValidateOrHashcodeAndContentTypeIsInvalid_ThrowsHttpMediaTypeNotSupportedException(String mediaType, String endpoint) throws Exception {
        mockMvc.perform(post(endpoint)
                .contentType(mediaType)
                .content(request().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].key", is("contentTypeNotSupported")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].message", containsString("Only the following content types are supported: application/json, application/*+json")))
                .andReturn();
    }

    @ParameterizedTest
    @MethodSource("provideUnsupportedMediaTypes")
    void performPostRequest_WhenEndpointIsSetToGetDataFilesAndContentTypeIsInvalid_ThrowsHttpMediaTypeNotSupportedException(String mediaType) throws Exception {
        mockMvcDataFiles.perform(post(GET_DATA_FILES_URL_TEMPLATE)
                .contentType(mediaType)
                .content(dataFileRequest().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].key", is("contentTypeNotSupported")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].message", containsString("Only the following content types are supported: application/json, application/*+json")))
                .andReturn();
    }

    @Test
    void performPostRequest_WhenValidationProxyThrowsNewRuntimeException_ThrowsException() throws Exception {
        when(validationProxy.validate(any())).thenThrow(new RuntimeException("Unexpected error"));
        mockMvc.perform(post(VALIDATE_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].key", is("unexpectedError")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].message", containsString("An unexpected error has occurred")))
                .andReturn();
    }

    @Test
    void performPostRequest_WhenHashcodeValidationProxyThrowsNewRuntimeException_ThrowsException() throws Exception {
        when(hashcodeValidationProxy.validate(any())).thenThrow(new RuntimeException("Unexpected error"));
        mockMvc.perform(post(HASHCODE_VALIDATION_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidFormatSignatureFile().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].key", is("unexpectedError")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].message", containsString("An unexpected error has occurred")))
                .andReturn();
    }

    @Test
    void performPostRequest_WhenDataFilesProxyThrowsNewRuntimeException_ThrowsException() throws Exception {
        when(dataFilesProxy.getDataFiles(any(ProxyDocument.class))).thenThrow(new RuntimeException("Unexpected error"));
        mockMvcDataFiles.perform(post(GET_DATA_FILES_URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataFileRequest().toString().getBytes()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].key", is("unexpectedError")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestErrors[0].message", containsString("An unexpected error has occurred")))
                .andReturn();
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
        jsonObject.put("filename", "file.ddoc");
        return jsonObject;
    }
    private JSONObject requestWithInvalidDocumentType() {
        JSONObject jsonObject = request();
        jsonObject.put("documentType", "asd");
        return jsonObject;
    }

    private JSONObject requestWithInvalidDataFileFilename() {
        JSONObject jsonObject = request();
        jsonObject.put("filename", "test.pdf");
        return jsonObject;
    }

    private JSONObject requestWithInvalidReportType() {
        JSONObject jsonObject = request();
        jsonObject.put("reportType", "asd");
        return jsonObject;
    }

    private JSONObject requestWithInvalidFormatSignatureFile() {
        JSONObject jsonObject = new JSONObject();
        Datafile datafile = new Datafile();
        datafile.setFilename("test");
        datafile.setHash("test-hash-1");
        datafile.setHashAlgo("SHA256");
        SignatureFile signatureFile = new SignatureFile();
        signatureFile.setSignature("NOT_XML_FORMATTED_FILE_CONTENT");
        signatureFile.setDatafiles(Collections.singletonList(datafile));
        jsonObject.put("signatureFiles", Arrays.asList(signatureFile));

        jsonObject.put("reportType", ReportType.SIMPLE);
        jsonObject.put("signaturePolicy", "POLv3");

        jsonObject.put("datafiles", Arrays.asList(datafile));
        return jsonObject;
    }

    private static Stream<Arguments> provideHttpMethodsAndValidationEndpoints() {
        return provideHttpMethods()
                .flatMap(httpMethod -> provideValidationEndpoints()
                .map(endpoint -> Arguments.of(httpMethod, endpoint)));
    }

    private static Stream<HttpMethod> provideHttpMethods() {
        return Stream.of(
                HttpMethod.GET,
                HttpMethod.PUT,
                HttpMethod.DELETE,
                HttpMethod.PATCH,
                HttpMethod.HEAD
        );
    }

    private static Stream<Arguments> provideInvalidJsonContentAndValidationEndpoints() {
        return provideValidationEndpoints()
                .flatMap(endpoint -> provideInvalidJsonContent()
                .map(invalidJsonContent -> Arguments.of(endpoint, invalidJsonContent)));
    }

    private static Stream<String> provideInvalidJsonContent() {
        return Stream.of(
                "",
                "{filename: \"\"}",
                "{\"filename\": ",
                "{\"filename\": \"file\"",
                "{\"filename\": \"file\", ",
                "{\"filename\": \"file\" \"reportType\": simple}",
                "{\"filename\": \"file\", \"reportType\": simple",
                "{\"filename\": \"file\", \"reportType\": simple, ",
                "{filename: \"file\", \"reportType\": simple}",
                "{\"filename\": \"file\", \"reportType\": }",
                "{\"filename\": \"file\", \"reportType\": simple, \"document\": }"
        );
    }

    private static Stream<Arguments> provideUnsupportedMediaTypesAndValidationEndpoints() {
        return provideUnsupportedMediaTypes()
                .flatMap(mediaType -> provideValidationEndpoints()
                .map(endpoint -> Arguments.of(mediaType, endpoint)));
    }

    private static Stream<String> provideUnsupportedMediaTypes() {
        return Stream.of(
                MediaType.APPLICATION_XML_VALUE,
                MediaType.TEXT_PLAIN_VALUE,
                "application/x-yaml",
                MediaType.TEXT_HTML_VALUE,
                ""
        );
    }

    private static Stream<String> provideValidationEndpoints() {
        return Stream.of(VALIDATE_URL_TEMPLATE, HASHCODE_VALIDATION_URL_TEMPLATE);
    }

}
