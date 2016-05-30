package ee.openeid.siva.webapp;

import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.webapp.request.ValidationRequest;
import ee.openeid.siva.webapp.transformer.ValidationRequestToProxyDocumentTransformer;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ValidationControllerTest {

    private ValidationProxySpy validationProxyServiceSpy = new ValidationProxySpy();
    private ValidationRequestToProxyDocumentTransformerSpy transformerSpy = new ValidationRequestToProxyDocumentTransformerSpy();

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        ValidationController validationController = new ValidationController();
        validationController.setValidationProxy(validationProxyServiceSpy);
        validationController.setTransformer(transformerSpy);
        mockMvc = standaloneSetup(validationController).build();
    }

    @Test
    public void validJsonIsCorrectlyMappedToPOJO() throws Exception {
        mockMvc.perform(post("/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validRequest().toString().getBytes())
        );
        assertEquals("filename.asd", transformerSpy.validationRequest.getFilename());
        assertEquals("QVNE", transformerSpy.validationRequest.getDocument());
        assertEquals("PDF" , transformerSpy.validationRequest.getDocumentType());
        assertEquals("simple" , transformerSpy.validationRequest.getReportType());
    }

    @Test
    public void requestWithInvalidReportTypeReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidReportType().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.requestErrors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.requestErrors[0].key", is("reportType")))
                .andExpect(jsonPath("$.requestErrors[0].message", containsString("invalid report type")));
    }

    @Test
    public void requestWithInvalidDocumentTypeReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidDocumentType().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.requestErrors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.requestErrors[0].key", is("documentType")))
                .andExpect(jsonPath("$.requestErrors[0].message", containsString("invalid document type")));
    }

    @Test
    public void requestWithNonBase64EncodedDocumentReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidDocumentEncoding().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.requestErrors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.requestErrors[0].key", is("document")))
                .andExpect(jsonPath("$.requestErrors[0].message", containsString("not valid base64 encoded string")));
    }

    @Test
    public void requestWithEmptyDocumentReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithEmptyDocument().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.requestErrors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.requestErrors[0].key", is("document")))
                .andExpect(jsonPath("$.requestErrors[0].message", containsString("may not be empty")));
    }

    @Test
    public void requestWithInvalidKeysShouldBeRejectedWithError() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reportType", "simple");
        jsonObject.put("filename", "filename.exe");
        jsonObject.put("documentType", "BDOC");
        jsonObject.put("Document", "QVNE");
        String responseContent = mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString().getBytes()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(responseContent);
    }

    @Test
    public void requestWithEmptyFilenameReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithFilename("").toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.requestErrors", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.requestErrors[*].key", containsInAnyOrder("filename", "filename")))
                .andExpect(jsonPath("$.requestErrors[*].message", containsInAnyOrder("invalid filename", "may not be empty")));
    }

    @Test
    public void requestWithForwardSlashInFilenameReturnsErroneousResponse() throws Exception {
        testIllegalFilename(filenameWithIllegalCharacter("/"));
    }

    @Test
    public void requestWithBackslashInFilenameReturnsErroneousResponse() throws Exception {
        testIllegalFilename(filenameWithIllegalCharacter("\\"));
    }

    @Test
    public void requestWithNulTerminatorInFilenameReturnsErroneousResponse() throws Exception {
        testIllegalFilename(filenameWithIllegalCharacter("\\0"));
    }

    @Test
    public void requestWithQuotesInFilenameReturnsErroneousResponse() throws Exception {
        testIllegalFilename(filenameWithIllegalCharacter("\""));
    }

    @Test
    public void requestWithStarInFilenameReturnsErroneousResponse() throws Exception {
        testIllegalFilename(filenameWithIllegalCharacter("*"));
    }

    @Test
    public void requestWithColonInFilenameReturnsErroneousResponse() throws Exception {
        testIllegalFilename(filenameWithIllegalCharacter(":"));
    }

    @Test
    public void requestWithQuestionmarkInFilenameReturnsErroneousResponse() throws Exception {
        testIllegalFilename(filenameWithIllegalCharacter("?"));
    }

    @Test
    public void requestWithPercentSymbolInFilenameReturnsErroneousResponse() throws Exception {
        testIllegalFilename(filenameWithIllegalCharacter("%"));
    }

    @Test
    public void requestWithAndSymbolInFilenameReturnsErroneousResponse() throws Exception {
        testIllegalFilename(filenameWithIllegalCharacter("&"));
    }

    @Test
    public void requestWithMultipleErrorsReturnsAllErrorsInResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.requestErrors", Matchers.hasSize(4)))
                .andExpect(jsonPath("$.requestErrors[*].key", containsInAnyOrder("filename", "documentType", "reportType", "document")))
                .andExpect(jsonPath("$.requestErrors[*].message", containsInAnyOrder("invalid filename", "invalid document type", "invalid report type", "not valid base64 encoded string")));
    }

    @Test
    public void aRequestWithNoRequiredKeysReturnsAllErrorsForEachMissingKey() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(blankRequest().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.requestErrors", Matchers.hasSize(8)))
                .andExpect(jsonPath("$.requestErrors[*].key", containsInAnyOrder("filename",
                        "documentType", "reportType", "document", "filename", "documentType", "reportType", "document")))
                .andExpect(jsonPath("$.requestErrors[*].message", containsInAnyOrder(
                        "invalid filename",
                        "invalid document type",
                        "invalid report type",
                        "not valid base64 encoded string",
                        "may not be empty",
                        "may not be empty",
                        "may not be empty",
                        "may not be empty"
                )));
    }

    private String filenameWithIllegalCharacter(String illegalCharacter) {
        return "file" + illegalCharacter + "name.pdf";
    }

    private void testIllegalFilename(String filename) throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithFilename(filename).toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.requestErrors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.requestErrors[0].key", is("filename")))
                .andExpect(jsonPath("$.requestErrors[0].message", containsString("invalid filename")));
    }

    private JSONObject blankRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("filename", "");
        return jsonObject;
    }

    private JSONObject validRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", "QVNE");
        jsonObject.put("filename", "filename.asd");
        jsonObject.put("documentType", "PDF");
        jsonObject.put("reportType", "simple");
        return jsonObject;
    }

    private JSONObject requestWithInvalidReportType() {
        JSONObject jsonObject = validRequest();
        jsonObject.put("reportType", "asd");
        return jsonObject;
    }

    private JSONObject requestWithInvalidDocumentType() {
        JSONObject jsonObject = validRequest();
        jsonObject.put("documentType", "asd");
        return jsonObject;
    }

    private JSONObject requestWithInvalidDocumentEncoding() {
        JSONObject jsonObject = validRequest();
        jsonObject.put("document", "ÖÕ::žšPQ;ÜÜ");
        return jsonObject;
    }

    private JSONObject requestWithEmptyDocument() {
        JSONObject jsonObject = validRequest();
        jsonObject.put("document", "");
        return jsonObject;
    }

    private JSONObject invalidRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", "ÖÕ::žšPQ;ÜÜ");
        jsonObject.put("filename", filenameWithIllegalCharacter("/"));
        jsonObject.put("documentType", "BLAMA");
        jsonObject.put("reportType", "very complicated");
        return jsonObject;
    }

    private JSONObject requestWithFilename(String filename) {
        JSONObject jsonObject = validRequest();
        jsonObject.put("filename", filename);
        return jsonObject;
    }

    private class ValidationProxySpy extends ValidationProxy {

        private ProxyDocument document;

        @Override
        public String validate(ProxyDocument document) {
            this.document = document;
            return null;
        }
    }

    private class ValidationRequestToProxyDocumentTransformerSpy extends ValidationRequestToProxyDocumentTransformer {

        private ValidationRequest validationRequest;

        @Override
        public ProxyDocument transform(ValidationRequest validationRequest) {
            this.validationRequest = validationRequest;
            return super.transform(validationRequest);
        }
    }
}