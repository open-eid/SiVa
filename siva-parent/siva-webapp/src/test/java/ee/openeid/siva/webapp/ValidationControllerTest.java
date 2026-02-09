/*
 * Copyright 2019 - 2026 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.proxy.ContainerValidationProxy;
import ee.openeid.siva.proxy.ProxyRequest;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.validation.ZipMimetypeValidator;
import ee.openeid.siva.statistics.StatisticsService;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.webapp.request.ValidationRequest;
import ee.openeid.siva.webapp.transformer.ValidationRequestToProxyDocumentTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
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

@ExtendWith(MockitoExtension.class)
class ValidationControllerTest {

    private ValidationRequestToProxyDocumentTransformerSpy transformerSpy = new ValidationRequestToProxyDocumentTransformerSpy();
    @Mock
    private StatisticsService statisticsService;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private Environment environment;
    @Mock
    private ZipMimetypeValidator zipMimetypeValidator;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        ValidationController validationController = new ValidationController();
        ValidationProxySpy validationProxyServiceSpy = new ValidationProxySpy(statisticsService, applicationContext, environment, zipMimetypeValidator);
        validationController.setContainerValidationProxy(validationProxyServiceSpy);
        validationController.setTransformer(transformerSpy);
        mockMvc = standaloneSetup(validationController).build();
    }

    @Test
    void validJsonIsCorrectlyMappedToPOJO() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(validRequest()))
        );
        assertEquals("filename.asd", transformerSpy.validationRequest.getFilename());
        assertEquals("QVNE", transformerSpy.validationRequest.getDocument());
    }

    @Test
    void requestWithNonBase64EncodedDocumentReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(requestWithInvalidDocumentEncoding())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestWithEmptyDocumentReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(requestWithEmptyDocument())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestWithInvalidReportTypeReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(requestWithInvalidReportType())))
               .andExpect(status().isBadRequest());
    }

    @Test
    void requestWithInvalidKeysShouldBeRejectedWithError() throws Exception {
        byte[] request = toJsonBytes(Map.of(
                "filename", "filename.exe",
                "documentType", "BDOC",
                "Document", "QVNE")
        );

        String responseContent = mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(responseContent);
    }

    @Test
    void requestWithEmptyFilenameReturnsErroneousResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(requestWithFilename(""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestWithUnusualButLegalCharacterInFilenameShouldBeValid() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(requestWithFilename("üõäöžš.pdf"))));
        assertEquals("üõäöžš.pdf", transformerSpy.validationRequest.getFilename());
    }

    @Test
    void requestWithMultipleErrorsReturnsAllErrorsInResponse() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(invalidRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void aRequestWithNoRequiredKeysReturnsAllErrorsForEachMissingKey() throws Exception {
        mockMvc.perform(post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonBytes(Map.of())))
                .andExpect(status().isBadRequest());
    }

    private String filenameWithIllegalCharacter(String illegalCharacter) {
        return "file" + illegalCharacter + "name.pdf";
    }

    private ObjectNode validRequest() {
        return toJsonNode(Map.of(
                "document", "QVNE",
                "filename", "filename.asd"
        ));
    }

    private ObjectNode requestWithInvalidDocumentEncoding() {
        return with(validRequest(), Map.of("document", "ÖÕ::žšPQ;ÜÜ"));
    }

    private ObjectNode requestWithEmptyDocument() {
        return with(validRequest(), Map.of("document", ""));
    }

    private ObjectNode requestWithInvalidReportType() {
        return with(validRequest(), Map.of("reportType", "INVALID_REPORT_TYPE"));
    }

    private ObjectNode invalidRequest() {
        return toJsonNode(Map.of(
                "document", "ÖÕ::žšPQ;ÜÜ",
                "filename", filenameWithIllegalCharacter("/"),
                "documentType", "BLAMA",
                "reportType", "very complicated"
        ));
    }

    private ObjectNode requestWithFilename(String filename) {
        return with(validRequest(), Map.of("filename", filename));
    }

    private static class ValidationProxySpy extends ContainerValidationProxy {

        ValidationProxySpy(
                StatisticsService statisticsService,
                ApplicationContext applicationContext,
                Environment environment,
                ZipMimetypeValidator zipMimetypeValidator) {
            super(statisticsService, applicationContext, environment, zipMimetypeValidator);
        }

        @Override
        public SimpleReport validateRequest(ProxyRequest proxyRequest) {
            return new SimpleReport();
        }
    }

    private static class ValidationRequestToProxyDocumentTransformerSpy extends ValidationRequestToProxyDocumentTransformer {

        private ValidationRequest validationRequest;

        @Override
        public ProxyDocument transform(ValidationRequest validationRequest) {
            this.validationRequest = validationRequest;
            return super.transform(validationRequest);
        }
    }
}
