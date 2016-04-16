package ee.openeid.siva.webapp;

import ee.openeid.siva.model.ValidationRequest;
import ee.openeid.siva.proxy.PdfValidationProxy;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ValidationControllerTest {

    private PdfValidationProxySpy validationProxySpy = new PdfValidationProxySpy();

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        ValidationController validationController = new ValidationController();
        validationController.setValidationProxy(validationProxySpy);
        mockMvc = standaloneSetup(validationController).build();
    }

    @Test
    public void jsonIsCorrectlyMappedToPOJO() throws Exception {
        mockMvc.perform(post("/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mockRequest().getBytes())
        );
        assertEquals("filename.asd", validationProxySpy.validationRequest.getFilename());
        assertEquals("ASD", validationProxySpy.validationRequest.getBase64Document());
        assertEquals("some type", validationProxySpy.validationRequest.getType());
        assertEquals("simple", validationProxySpy.validationRequest.getReportType());
    }

    private String mockRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", "ASD");
        jsonObject.put("filename", "filename.asd");
        jsonObject.put("document-type", "some type");
        jsonObject.put("report-type", "simple");
        return jsonObject.toString();
    }

    private class PdfValidationProxySpy extends PdfValidationProxy {

        private ValidationRequest validationRequest;

        @Override
        public String validate(ValidationRequest validationRequest) {
            this.validationRequest = validationRequest;
            return null;
        }
    }
}