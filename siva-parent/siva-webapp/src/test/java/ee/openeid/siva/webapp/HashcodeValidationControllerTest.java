/*
 * Copyright 2018 Riigi Infosüsteemide Amet
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
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.webapp.request.Datafile;
import ee.openeid.siva.webapp.request.HashcodeValidationRequest;
import ee.openeid.siva.webapp.transformer.HashcodeValidationRequestToProxyDocumentTransformer;
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

public class HashcodeValidationControllerTest {

    private HashcodeValidationProxySpy hashcodeValidationProxySpy = new HashcodeValidationProxySpy();
    private HashcodeValidationRequestToProxyDocumentTransformerSpy hashRequestTransformerSpy = new HashcodeValidationRequestToProxyDocumentTransformerSpy();

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        ValidationController validationController = new ValidationController();
        validationController.setHashRequestTransformer(hashRequestTransformerSpy);
        validationController.setHashcodeValidationProxy(hashcodeValidationProxySpy);
        mockMvc = standaloneSetup(validationController).build();
    }

    @Test
    public void requestWithEmptySignature() throws Exception {
        JSONObject request = hashcodeValidationRequest("QVNE", "", createValidDatafiles());

        mockMvc.perform(post("/validateHashcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void requestWithInvalidSignatureFilename() throws Exception {
        JSONObject request = hashcodeValidationRequest("QVNE", "test.pdf", createValidDatafiles());

        mockMvc.perform(post("/validateHashcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void validHashJsonIsCorrectlyMappedToPOJO() throws Exception {
        Datafile datafile1 = createDatafile("test-name-1", "test-hash-1", "SHA256");
        Datafile datafile2 = createDatafile("test-name-2", "test-hash-2", "SHA512");
        JSONObject request = hashcodeValidationRequest("QVNE", "filename.xml", createDatafiles(datafile1, datafile2));

        mockMvc.perform(post("/validateHashcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes())
        );
        assertEquals("filename.xml", hashRequestTransformerSpy.validationRequest.getFilename());
        assertEquals("QVNE", hashRequestTransformerSpy.validationRequest.getSignatureFile());
        assertNotNull(hashRequestTransformerSpy.validationRequest.getDatafiles());
        assertFalse(hashRequestTransformerSpy.validationRequest.getDatafiles().isEmpty());
        assertEquals(2, hashRequestTransformerSpy.validationRequest.getDatafiles().size());
    }

    @Test
    public void hashRequestWithNonBase64EncodedSignatureReturnsErroneousResponse() throws Exception {
        JSONObject request = hashcodeValidationRequest("ÖÕ::žšPQ;ÜÜ", "test.xml", createValidDatafiles());
        mockMvc.perform(post("/validateHashcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void hashRequestWithInvalidHashAlgoReturnsErroneousResponse() throws Exception {
        List<Datafile> datafiles = createDatafiles(createDatafile("test", "test-hash-1", "invalid"));
        JSONObject request = hashcodeValidationRequest("test", "test.xml", datafiles);
        mockMvc.perform(post("/validateHashcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void hashRequestWithNonBase64EncodedHashReturnsErroneousResponse() throws Exception {
        List<Datafile> datafiles = createDatafiles(createDatafile("test", "ÖÕ::žšPQ;ÜÜ", "SHA256"));
        JSONObject request = hashcodeValidationRequest("test", "test.xml", datafiles);
        mockMvc.perform(post("/validateHashcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    private class HashcodeValidationProxySpy extends HashcodeValidationProxy {

        @Override
        public SimpleReport validate(ProxyDocument document) {
            return null;
        }
    }

    private class HashcodeValidationRequestToProxyDocumentTransformerSpy extends HashcodeValidationRequestToProxyDocumentTransformer {

        private HashcodeValidationRequest validationRequest;

        @Override
        public ProxyDocument transform(HashcodeValidationRequest validationRequest) {
            this.validationRequest = validationRequest;
            return super.transform(validationRequest);
        }
    }

    private JSONObject hashcodeValidationRequest(String signature, String filename, List<Datafile> datafiles) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("signatureFile", signature);
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
