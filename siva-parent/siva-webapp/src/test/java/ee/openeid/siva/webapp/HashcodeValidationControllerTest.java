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
import ee.openeid.siva.proxy.ProxyRequest;
import ee.openeid.siva.proxy.document.ProxyHashcodeDataSet;
import ee.openeid.siva.statistics.StatisticsService;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.webapp.request.Datafile;
import ee.openeid.siva.webapp.request.HashcodeValidationRequest;
import ee.openeid.siva.webapp.request.SignatureFile;
import ee.openeid.siva.webapp.transformer.HashcodeValidationRequestToProxyDocumentTransformer;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
@RunWith(MockitoJUnitRunner.class)
public class HashcodeValidationControllerTest {

    private HashcodeValidationProxySpy hashcodeValidationProxySpy = new HashcodeValidationProxySpy();
    private HashcodeValidationRequestToProxyDocumentTransformerSpy hashRequestTransformerSpy = new HashcodeValidationRequestToProxyDocumentTransformerSpy();
    @Mock
    private StatisticsService statisticsService;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        ValidationController validationController = new ValidationController();
        hashcodeValidationProxySpy.setStatisticsService(statisticsService);
        validationController.setHashRequestTransformer(hashRequestTransformerSpy);
        validationController.setHashcodeValidationProxy(hashcodeValidationProxySpy);
        mockMvc = standaloneSetup(validationController).build();
    }

    @Test
    public void requestWithEmptySignatureFiles() throws Exception {
        JSONObject request = new JSONObject();

        mockMvc.perform(post("/validateHashcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void validHashJsonIsCorrectlyMappedToPOJO() throws Exception {
        Datafile datafile1 = createDatafile("test-name-1", "test-hash-1", "SHA256");
        Datafile datafile2 = createDatafile("test-name-2", "test-hash-2", "SHA512");
        List<SignatureFile> signatureFiles = createSignatureFiles("QVNE", createDatafiles(datafile1, datafile2));
        JSONObject request = hashcodeValidationRequest(signatureFiles);
        mockMvc.perform(post("/validateHashcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes())
        );
        SignatureFile signatureFile = hashRequestTransformerSpy.validationRequest.getSignatureFiles().get(0);
        assertEquals("QVNE", signatureFile.getSignature());
        assertNotNull(signatureFile.getDatafiles());
        assertFalse(signatureFile.getDatafiles().isEmpty());
        assertEquals(2, signatureFile.getDatafiles().size());
    }

    @Test
    public void hashRequestWithNonBase64EncodedSignatureReturnsErroneousResponse() throws Exception {
        List<SignatureFile> signatureFiles = createSignatureFiles("ÖÕ::žšPQ;ÜÜ", createValidDatafiles());
        JSONObject request = hashcodeValidationRequest(signatureFiles);
        mockMvc.perform(post("/validateHashcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void hashRequestWithInvalidHashAlgoReturnsErroneousResponse() throws Exception {
        List<Datafile> datafiles = createDatafiles(createDatafile("test", "test-hash-1", "invalid"));
        List<SignatureFile> signatureFiles = createSignatureFiles("test", datafiles);
        JSONObject request = hashcodeValidationRequest(signatureFiles);
        mockMvc.perform(post("/validateHashcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void hashRequestWithNonBase64EncodedHashReturnsErroneousResponse() throws Exception {
        List<Datafile> datafiles = createDatafiles(createDatafile("test", "ÖÕ::žšPQ;ÜÜ", "SHA256"));
        List<SignatureFile> signatureFiles = createSignatureFiles("test", datafiles);
        JSONObject request = hashcodeValidationRequest(signatureFiles);
        mockMvc.perform(post("/validateHashcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    private class HashcodeValidationProxySpy extends HashcodeValidationProxy {
        @Override
        public SimpleReport validateRequest(ProxyRequest proxyRequest) {
            return new SimpleReport();
        }
    }

    private class HashcodeValidationRequestToProxyDocumentTransformerSpy extends HashcodeValidationRequestToProxyDocumentTransformer {

        private HashcodeValidationRequest validationRequest;

        @Override
        public ProxyHashcodeDataSet transform(HashcodeValidationRequest validationRequest) {
            this.validationRequest = validationRequest;
            return super.transform(validationRequest);
        }
    }

    private JSONObject hashcodeValidationRequest(List<SignatureFile> signatureFiles) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("signatureFiles", signatureFiles);
        return jsonObject;
    }

    private List<SignatureFile> createSignatureFiles(String signature, List<Datafile> datafiles) {
        List<SignatureFile> signatureFiles = new ArrayList<>();
        SignatureFile signatureFile = new SignatureFile();
        signatureFile.setSignature(signature);
        signatureFile.setDatafiles(datafiles);
        signatureFiles.add(signatureFile);
        return signatureFiles;
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
