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

package ee.openeid.siva.webapp.soap.transformer;

import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.document.typeresolver.UnsupportedTypeException;
import ee.openeid.siva.webapp.soap.SoapValidationRequest;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SoapValidationRequestToProxyDocumentTransformerTest {

    private SoapValidationRequestToProxyDocumentTransformer transformer = new SoapValidationRequestToProxyDocumentTransformer();

    @Test
    public void contentIsCorrectlyTransformedToBytes() {
        String documentContent = "ZmlsZWNvbnRlbnQ=";
        SoapValidationRequest validationRequest = createSoapValidationRequest(documentContent, "file.bdoc", "some policy");
        assertEquals(validationRequest.getDocument(), Base64.encodeBase64String(transformer.transform(validationRequest).getBytes()));
    }

    @Test
    public void reportTypeIsCorrectlyTransformed() {
        for (ReportType reportType : ReportType.values()) {
            SoapValidationRequest validationRequest = createSoapValidationRequest("ZmlsZWNvbnRlbnQ=", "file.bdoc", "some policy");
            validationRequest.setReportType(reportType.getValue());
            assertEquals(validationRequest.getReportType(), transformer.transform(validationRequest).getReportType().getValue());
        }
    }

    @Test
    public void reportTypeNullIsNotOverAssigned() {
        SoapValidationRequest validationRequest = createSoapValidationRequest("ZmlsZWNvbnRlbnQ=", "file.bdoc", "some policy");
        validationRequest.setReportType(null);
        assertEquals(null, transformer.transform(validationRequest).getReportType());
    }

    @Test
    public void invalidReportTypeThrowsUnsupportedTypeException() {
        String reportType = "INVALID_REPORT_TYPE";
        SoapValidationRequest validationRequest = createSoapValidationRequest("ZmlsZWNvbnRlbnQ=", "file.bdoc", "some policy");
        validationRequest.setReportType(reportType);

        UnsupportedTypeException caughtException = assertThrows(
                UnsupportedTypeException.class, () -> transformer.transform(validationRequest)
        );
        assertEquals("ReportType of type '" + reportType + "' is not supported", caughtException.getMessage());
    }

    @Test
    public void filenameRemainsUnchanged() {
        String filename = "random file name.bdoc";
        SoapValidationRequest validationRequest = createSoapValidationRequest("Ymxh", filename, "some policy");
        assertEquals(validationRequest.getFilename(), transformer.transform(validationRequest).getName());
    }

    @Test
    public void signaturePolicyRemainsUnchanged() {
        String policy = "policy";
        SoapValidationRequest validationRequest = createSoapValidationRequest("Ymxh", "file.bdoc", policy);
        assertEquals(validationRequest.getSignaturePolicy(), transformer.transform(validationRequest).getSignaturePolicy());
    }

    private SoapValidationRequest createSoapValidationRequest(String document, String filename, String signaturePolicy) {
        SoapValidationRequest validationRequest = new SoapValidationRequest();
        validationRequest.setDocument(document);
        validationRequest.setFilename(filename);
        validationRequest.setSignaturePolicy(signaturePolicy);
        return validationRequest;
    }

}
