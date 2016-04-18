package ee.openeid.siva.webapp;

import ee.openeid.pdf.webservice.json.JSONDocument;
import ee.openeid.siva.proxy.service.ValidationProxyService;
import ee.openeid.siva.webapp.transformer.ValidationRequestToJSONDocumentTransformer;
import eu.europa.esig.dss.MimeType;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ValidationControllerTest {

    private ValidationProxyServiceSpy validationProxyServiceSpy = new ValidationProxyServiceSpy();

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        ValidationController validationController = new ValidationController();
        validationController.setValidationProxy(validationProxyServiceSpy);
        validationController.setTransformer(new ValidationRequestToJSONDocumentTransformer());
        mockMvc = standaloneSetup(validationController).build();
    }

    @Test
    public void jsonIsCorrectlyMappedToPOJO() throws Exception {
        mockMvc.perform(post("/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mockRequest().getBytes())
        );
        assertEquals("filename.asd", validationProxyServiceSpy.document.getName());
        assertEquals("QVNE", Base64.encodeBase64String(validationProxyServiceSpy.document.getBytes()));
        assertEquals(MimeType.PDF , validationProxyServiceSpy.document.getMimeType());
    }

    private String mockRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", "QVNE");
        jsonObject.put("filename", "filename.asd");
        jsonObject.put("documentType", "PDF");
        return jsonObject.toString();
    }

    private class ValidationProxyServiceSpy extends ValidationProxyService {

        private JSONDocument document;

        @Override
        public String validate(JSONDocument document) {
            this.document = document;
            return null;
        }
    }
}