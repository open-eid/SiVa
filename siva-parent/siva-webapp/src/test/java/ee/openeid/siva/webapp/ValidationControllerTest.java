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

import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.validation.document.report.Report;
import ee.openeid.siva.webapp.request.ValidationRequest;
import ee.openeid.siva.webapp.transformer.ValidationRequestToProxyDocumentTransformer;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    }

    @Test
    public void requestWithInvalidDocumentTypeReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidDocumentType().toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void requestWithNonBase64EncodedDocumentReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidDocumentEncoding().toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void requestWithEmptyDocumentReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithEmptyDocument().toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void requestWithInvalidKeysShouldBeRejectedWithError() throws Exception {
        JSONObject jsonObject = new JSONObject();
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
                .andExpect(status().isBadRequest());
    }

    @Test
    public void requestWithUnusualButLegalCharacterInFilenameShouldBeValid() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithFilename("üõäöžš.pdf").toString().getBytes()));
        assertEquals("üõäöžš.pdf", transformerSpy.validationRequest.getFilename());
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
                .andExpect(status().isBadRequest());
    }

    @Test
    public void aRequestWithNoRequiredKeysReturnsAllErrorsForEachMissingKey() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject().toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    private String filenameWithIllegalCharacter(String illegalCharacter) {
        return "file" + illegalCharacter + "name.pdf";
    }

    private void testIllegalFilename(String filename) throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithFilename(filename).toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    private JSONObject validRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", "QVNE");
        jsonObject.put("filename", "filename.asd");
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
        public Report validate(ProxyDocument document) {
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
