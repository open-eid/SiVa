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

import ee.openeid.siva.proxy.DataFilesProxy;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.validation.document.report.DataFilesReport;
import ee.openeid.siva.webapp.request.DataFilesRequest;
import ee.openeid.siva.webapp.transformer.DataFilesRequestToProxyDocumentTransformer;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class DataFilesControllerTest {

    private DataFilesProxySpy dataFilesProxyServiceSpy = new DataFilesProxySpy();
    private DataFilesRequestToProxyDocumentTransformerSpy transformerSpy = new DataFilesRequestToProxyDocumentTransformerSpy();

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        DataFilesController dataFilesController = new DataFilesController();
        dataFilesController.setDataFilesProxy(dataFilesProxyServiceSpy);
        dataFilesController.setDataFilesTransformer(transformerSpy);
        mockMvc = standaloneSetup(dataFilesController).build();
    }

    @Test
    public void validJsonIsCorrectlyMappedToPOJO() throws Exception {
        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest().toString().getBytes())
        );
        assertEquals("QVNE", transformerSpy.dataFilesRequest.getDocument());
        assertEquals("DDOC" , transformerSpy.dataFilesRequest.getDocumentType());
    }

    @Test
    public void requestWithInvalidDocumentTypeReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidDocumentType().toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void requestWithNonBase64EncodedDocumentReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithInvalidDocumentEncoding().toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void requestWithEmptyDocumentReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithEmptyDocument().toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void requestWithInvalidKeysShouldBeRejectedWithError() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dcumentType", "DDOC");
        jsonObject.put("Document", "QVNE");

        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString().getBytes()))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void requestWithMultipleErrorsReturnsAllErrorsInResponse() throws Exception {
        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest().toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void aRequestWithNoRequiredKeysReturnsAllErrorsForEachMissingKey() throws Exception {
        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject().toString().getBytes()))
                .andExpect(status().isBadRequest());
    }

    private JSONObject validRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", "QVNE");
        jsonObject.put("documentType", "DDOC");
        return jsonObject;
    }

    private JSONObject requestWithInvalidDocumentType() {
        JSONObject jsonObject = validRequest();
        jsonObject.put("documentType", "PDF");
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
        jsonObject.put("documentType", "BLAMA");
        return jsonObject;
    }

    private class DataFilesProxySpy extends DataFilesProxy {

        @Override
        public DataFilesReport getDataFiles(ProxyDocument proxyDocument) {
            return null;
        }
    }

    private class DataFilesRequestToProxyDocumentTransformerSpy extends DataFilesRequestToProxyDocumentTransformer {

        private DataFilesRequest dataFilesRequest;

        @Override
        public ProxyDocument transform(DataFilesRequest dataFilesRequest) {
            this.dataFilesRequest = dataFilesRequest;
            return super.transform(dataFilesRequest);
        }

    }
}