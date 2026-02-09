/*
 * Copyright 2017 - 2026 Riigi Infosüsteemi Amet
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.node.ObjectNode;

import java.util.Map;

import static ee.openeid.siva.webapp.utils.TestJsonUtils.toJsonBytes;
import static ee.openeid.siva.webapp.utils.TestJsonUtils.toJsonNode;
import static ee.openeid.siva.webapp.utils.TestJsonUtils.with;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class DataFilesControllerTest {

    private DataFilesProxySpy dataFilesProxyServiceSpy = new DataFilesProxySpy();
    private DataFilesRequestToProxyDocumentTransformerSpy transformerSpy = new DataFilesRequestToProxyDocumentTransformerSpy();

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        DataFilesController dataFilesController = new DataFilesController();
        dataFilesController.setDataFilesProxy(dataFilesProxyServiceSpy);
        dataFilesController.setDataFilesTransformer(transformerSpy);
        mockMvc = standaloneSetup(dataFilesController).build();
    }

    @Test
    void validJsonIsCorrectlyMappedToPOJO() throws Exception {
        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(validRequest()))
        );
        assertEquals("QVNE", transformerSpy.dataFilesRequest.getDocument());
        assertEquals("test.ddoc" , transformerSpy.dataFilesRequest.getFilename());
    }

    @Test
    void requestWithInvalidDocumentTypeReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(requestWithInvalidFilename())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestWithNonBase64EncodedDocumentReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(requestWithInvalidDocumentEncoding())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestWithEmptyDocumentReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(requestWithEmptyDocument())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestWithInvalidKeysShouldBeRejectedWithError() throws Exception {
        ObjectNode request = toJsonNode(Map.of(
                "filename", "test.ddoc",
                "Document", "QVNE"
        ));

        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void requestWithMultipleErrorsReturnsAllErrorsInResponse() throws Exception {
        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(invalidRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void aRequestWithNoRequiredKeysReturnsAllErrorsForEachMissingKey() throws Exception {
        mockMvc.perform(post("/getDataFiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(Map.of())))
                .andExpect(status().isBadRequest());
    }

    private ObjectNode validRequest() {
        return toJsonNode(Map.of(
                "document", "QVNE",
                "filename", "test.ddoc"
        ));
    }

    private ObjectNode requestWithInvalidFilename() {
        return with(validRequest(), Map.of("filename", ""));
    }

    private ObjectNode requestWithInvalidDocumentEncoding() {
        return with(validRequest(), Map.of("document", "ÖÕ::žšPQ;ÜÜ"));
    }

    private ObjectNode requestWithEmptyDocument() {
        return with(validRequest(), Map.of("document", ""));
    }

    private ObjectNode invalidRequest() {
        return toJsonNode(Map.of(
                "document", "ÖÕ::žšPQ;ÜÜ",
                "documentType", "BLAMA"
        ));
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
