/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

import ee.openeid.siva.proxy.HashcodeValidationProxy;
import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.webapp.request.Datafile;
import ee.openeid.siva.webapp.request.ValidationRequest;
import ee.openeid.siva.webapp.request.ValidationWithHashRequest;
import ee.openeid.siva.webapp.transformer.ValidationRequestToProxyDocumentTransformer;
import ee.openeid.siva.webapp.transformer.ValidationWithHashRequestToProxyDocumentTransformer;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ValidationControllerTest {

    private ValidationProxySpy validationProxyServiceSpy = new ValidationProxySpy();
    private HashcodeValidationProxySpy hashcodeValidationProxySpy = new HashcodeValidationProxySpy();
    private ValidationRequestToProxyDocumentTransformerSpy transformerSpy = new ValidationRequestToProxyDocumentTransformerSpy();
    private ValidationWithHashRequestToProxyDocumentTransformerSpy hashRequestTransformerSpy = new ValidationWithHashRequestToProxyDocumentTransformerSpy();

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        ValidationController validationController = new ValidationController();
        validationController.setValidationProxy(validationProxyServiceSpy);
        validationController.setTransformer(transformerSpy);
        validationController.setHashRequestTransformer(hashRequestTransformerSpy);
        validationController.setHashcodeValidationProxy(hashcodeValidationProxySpy);
        mockMvc = standaloneSetup(validationController).build();
    }

    @Test
    public void requestWithEmptySignature() throws Exception {
        JSONObject request = withHashRequest("QVNE", "", createValidDatafiles());

        mockMvc.perform(post("/validateWithHash")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void requestWithInvalidSignatureFilename() throws Exception {
        JSONObject request = withHashRequest("QVNE", "test.pdf", createValidDatafiles());

        mockMvc.perform(post("/validateWithHash")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void validHashJsonIsCorrectlyMappedToPOJO() throws Exception {
        Datafile datafile1 = createDatafile("test-name-1", "test-hash-1", "SHA256");
        Datafile datafile2 = createDatafile("test-name-2", "test-hash-2", "SHA512");
        JSONObject request = withHashRequest("QVNE", "filename.xml", createDatafiles(datafile1, datafile2));

        mockMvc.perform(post("/validateWithHash")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes())
        );
        assertEquals("filename.xml", hashRequestTransformerSpy.validateWithHashRequest.getFilename());
        assertEquals("QVNE", hashRequestTransformerSpy.validateWithHashRequest.getSignature());
        assertNotNull(hashRequestTransformerSpy.validateWithHashRequest.getDatafiles());
        assertFalse(hashRequestTransformerSpy.validateWithHashRequest.getDatafiles().isEmpty());
        assertEquals(2, hashRequestTransformerSpy.validateWithHashRequest.getDatafiles().size());
    }

    @Test
    public void hashRequestWithNonBase64EncodedSignatureReturnsErroneousResponse() throws Exception {
        JSONObject request = withHashRequest("ÖÕ::žšPQ;ÜÜ", "test.xml", createValidDatafiles());
        mockMvc.perform(post("/validateWithHash")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void hashRequestWithInvalidHashAlgoReturnsErroneousResponse() throws Exception {
        List<Datafile> datafiles = createDatafiles(createDatafile("test", "test-hash-1", "invalid"));
        JSONObject request = withHashRequest("test", "test.xml", datafiles);
        mockMvc.perform(post("/validateWithHash")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void hashRequestWithNonBase64EncodedHashReturnsErroneousResponse() throws Exception {
        List<Datafile> datafiles = createDatafiles(createDatafile("test", "ÖÕ::žšPQ;ÜÜ", "SHA256"));
        JSONObject request = withHashRequest("test", "test.xml", datafiles);
        mockMvc.perform(post("/validateWithHash")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest());
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
        public SimpleReport validate(ProxyDocument document) {
            this.document = document;
            return null;
        }
    }

    private class HashcodeValidationProxySpy extends HashcodeValidationProxy {

        @Override
        public SimpleReport validate(ProxyDocument document) {
            return null;
        }
    }

    private class ValidationWithHashRequestToProxyDocumentTransformerSpy extends ValidationWithHashRequestToProxyDocumentTransformer {

        private ValidationWithHashRequest validateWithHashRequest;

        @Override
        public ProxyDocument transform(ValidationWithHashRequest validateWithHashRequest) {
            this.validateWithHashRequest = validateWithHashRequest;
            return super.transform(validateWithHashRequest);
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

    private JSONObject withHashRequest(String signature, String filename, List<Datafile> datafiles) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("signature", signature);
        jsonObject.put("filename", filename);
        jsonObject.put("datafiles", datafiles);
        return jsonObject;
    }

    private List<Datafile> createValidDatafiles() {
        return createDatafiles(createDatafile("test", "test-hash-1", "SHA256"));
    }

    private List<Datafile> createDatafiles(Datafile... datafiles) {
        return Arrays.asList(datafiles);
    }

    private Datafile createDatafile(String filename, String hash, String hashAlgo) {
        Datafile datafile = new Datafile();
        datafile.setFilename(filename);
        datafile.setHash(hash);
        datafile.setHashAlgo(hashAlgo);
        return datafile;
    }
}
