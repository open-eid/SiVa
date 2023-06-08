/*
 * Copyright 2019 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.webapp.transformer;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.document.typeresolver.UnsupportedTypeException;
import ee.openeid.siva.testutils.MockValidationRequestBuilder;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationRequestToProxyDocumentTransformerTest {

    private static final String VALID_PDF_FILE = "test-files/sample.pdf";
    private ValidationRequestToProxyDocumentTransformer transformer = new ValidationRequestToProxyDocumentTransformer();
    private MockValidationRequestBuilder.MockValidationRequest validationRequest;

    @BeforeEach
    public void setUp() throws Exception {
        setValidPdfValidationRequest();
    }

    @Test
    void filenameRemainsUnchanged() {
        assertEquals(validationRequest.getFilename(), transformer.transform(validationRequest).getName());
    }

    @Test
    void reportTypeIsCorrectlyTransformedToReportType() {
        for (ReportType reportType : ReportType.values()) {
            validationRequest.setReportType(reportType.name());
            assertEquals(reportType, transformer.transform(validationRequest).getReportType());
        }
    }

    @Test
    void nullReportTypeIsTransformedToSimpleReportType() {
        validationRequest.setReportType(null);
        assertEquals(ReportType.SIMPLE, transformer.transform(validationRequest).getReportType());
    }

    @Test
    void invalidReportTypeThrowsUnsupportedTypeException() {
        validationRequest.setReportType("INVALID_MISS_TYPED_OR_MISSING_REPORT_TYPE");

        UnsupportedTypeException caughtException = assertThrows(
                UnsupportedTypeException.class, () -> {
                    transformer.transform(validationRequest).getReportType();
                }
        );
        assertEquals("ReportType of type 'INVALID_MISS_TYPED_OR_MISSING_REPORT_TYPE' is not supported", caughtException.getMessage());
    }

    @Test
    void contentIsCorrectlyTransformedToBytes() {
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        assertEquals(validationRequest.getDocument(), Base64.encodeBase64String(proxyDocument.getBytes()));
    }

    @Test
    void signaturePolicyRemainsUnchanged() {
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        assertEquals(validationRequest.getSignaturePolicy(), proxyDocument.getSignaturePolicy());
    }

    private void setValidPdfValidationRequest() throws Exception {
        Path filepath = Paths.get(getClass().getClassLoader().getResource(VALID_PDF_FILE).toURI());
        validationRequest = MockValidationRequestBuilder
                .aValidationRequest()
                .withType("pdf")
                .withSignaturePolicy("POL")
                .withDocument(filepath)
                .build();
    }
}
