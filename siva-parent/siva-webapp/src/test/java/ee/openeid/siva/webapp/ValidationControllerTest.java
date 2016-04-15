package ee.openeid.siva.webapp;

import ee.openeid.pdf.webservice.json.JSONDocument;
import ee.openeid.siva.proxy.PdfValidationProxy;
import ee.openeid.siva.webapp.request.model.JSONValidationRequest;
import ee.openeid.siva.webapp.transformer.RequestToJsonDocumentTransformer;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ValidationControllerTest {

    private PdfValidationProxySpy validationProxy = new PdfValidationProxySpy();
    private RequestToJsonDocumentTransformerSpy transformer = new RequestToJsonDocumentTransformerSpy();

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        ValidationController validationController = new ValidationController();
        validationController.setValidationProxy(validationProxy);
        validationController.setTransformer(transformer);
        mockMvc = standaloneSetup(validationController).build();
    }

    @Test
    public void jsonIsCorrectlyMappedToPOJO() throws Exception {
        JSONValidationRequest request = new JSONValidationRequest();
        mockMvc.perform(post("/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mockRequest().getBytes())
        );
        assertEquals(transformer.validationRequest.getFilename(), "filename.asd");
        assertEquals(transformer.validationRequest.getBase64Document(), "ASD");
        assertEquals(transformer.validationRequest.getType(), "some type");
        assertEquals(transformer.validationRequest.getReportType(), "simple");
    }

    private String mockRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("base64Document", "ASD");
        jsonObject.put("filename", "filename.asd");
        jsonObject.put("type", "some type");
        jsonObject.put("reportType", "simple");
        return jsonObject.toString();
    }

    private class RequestToJsonDocumentTransformerSpy extends RequestToJsonDocumentTransformer {

        private JSONValidationRequest validationRequest;

        @Override
        public JSONDocument transform(JSONValidationRequest validationRequest) {
            this.validationRequest = validationRequest;
            return new JSONDocument();
        }
    }

    private class PdfValidationProxySpy extends PdfValidationProxy {

        @Override
        public String validate(JSONDocument document) {
            return "OK";
        }
    }
}