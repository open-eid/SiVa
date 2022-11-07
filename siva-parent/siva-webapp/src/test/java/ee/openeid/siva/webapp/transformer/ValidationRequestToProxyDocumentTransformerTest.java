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

import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.document.typeresolver.UnsupportedTypeException;
import ee.openeid.siva.testutils.MockValidationRequestBuilder;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class ValidationRequestToProxyDocumentTransformerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String VALID_PDF_FILE = "test-files/sample.pdf";
    private ValidationRequestToProxyDocumentTransformer transformer = new ValidationRequestToProxyDocumentTransformer();
    private MockValidationRequestBuilder.MockValidationRequest validationRequest;

    @Before
    public void setUp() throws Exception {
        setValidPdfValidationRequest();
    }

    @Test
    public void filenameRemainsUnchanged() {
        assertEquals(validationRequest.getFilename(), transformer.transform(validationRequest).getName());
    }

    @Test
    public void reportTypeIsCorrectlyTransformedToReportType() {
        for (ReportType reportType : ReportType.values()) {
            validationRequest.setReportType(reportType.name());
            assertEquals(reportType, transformer.transform(validationRequest).getReportType());
        }
    }

    @Test
    public void nullReportTypeIsTransformedToSimpleReportType() {
        validationRequest.setReportType(null);
        assertEquals(ReportType.SIMPLE, transformer.transform(validationRequest).getReportType());
    }

    @Test
    public void invalidReportTypeThrowsUnsupportedTypeException() {
        expectedException.expect(UnsupportedTypeException.class);
        expectedException.expectMessage("ReportType of type 'INVALID_MISS_TYPED_OR_MISSING_REPORT_TYPE' is not supported");

        validationRequest.setReportType("INVALID_MISS_TYPED_OR_MISSING_REPORT_TYPE");
        transformer.transform(validationRequest).getReportType();
    }

    @Test
    public void contentIsCorrectlyTransformedToBytes() {
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        assertEquals(validationRequest.getDocument(), Base64.encodeBase64String(proxyDocument.getBytes()));
    }

    @Test
    public void signaturePolicyRemainsUnchanged() {
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
