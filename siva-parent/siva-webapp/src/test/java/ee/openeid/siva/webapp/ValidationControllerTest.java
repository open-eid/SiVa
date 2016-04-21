package ee.openeid.siva.webapp;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.service.ValidationProxyService;
import ee.openeid.siva.webapp.request.ValidationRequest;
import ee.openeid.siva.webapp.transformer.ValidationRequestToProxyDocumentTransformer;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ValidationControllerTest {

    private ValidationProxyServiceSpy validationProxyServiceSpy = new ValidationProxyServiceSpy();
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
        assertEquals("QVNE", transformerSpy.validationRequest.getBase64Document());
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
                .andExpect(jsonPath("$.requestErrors[0].field", is("reportType")))
                .andExpect(jsonPath("$.requestErrors[0].message", containsString("invalid reportType")));
    }

    @Test
    public void requestWithInvalidDocumentTypeReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidDocumentType().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.requestErrors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.requestErrors[0].field", is("documentType")))
                .andExpect(jsonPath("$.requestErrors[0].message", containsString("invalid documentType")));
    }

    @Test
    public void requestWithEmptyFilenameReturnsErroneousResponse() throws Exception {
        testIllegalFilename("");
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
                .andExpect(jsonPath("$.requestErrors", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.requestErrors[*].field", containsInAnyOrder("filename", "documentType", "reportType")))
                .andExpect(jsonPath("$.requestErrors[*].message", containsInAnyOrder("invalid filename", "invalid documentType", "invalid reportType")));
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
                .andExpect(jsonPath("$.requestErrors[0].field", is("filename")))
                .andExpect(jsonPath("$.requestErrors[0].message", containsString("invalid filename")));
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

    private JSONObject invalidRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", "ÖÕÜÜ");
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

    private class ValidationProxyServiceSpy extends ValidationProxyService {

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
            return null;
        }
    }
}