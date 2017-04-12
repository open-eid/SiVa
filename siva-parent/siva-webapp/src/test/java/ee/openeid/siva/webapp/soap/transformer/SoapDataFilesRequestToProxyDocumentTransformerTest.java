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

package ee.openeid.siva.webapp.soap.transformer;

import ee.openeid.siva.webapp.soap.DocumentType;
import ee.openeid.siva.webapp.soap.SoapDataFilesRequest;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SoapDataFilesRequestToProxyDocumentTransformerTest {

    private SoapDataFilesRequestToProxyDocumentTransformer transformer = new SoapDataFilesRequestToProxyDocumentTransformer();

    @Test
    public void contentIsCorrectlyTransformedToBytes() {
        String documentContent = "ZmlsZWNvbnRlbnQ=";
        SoapDataFilesRequest dataFilesRequest = createSoapDataFilesRequest(documentContent, DocumentType.DDOC);
        assertEquals(dataFilesRequest.getDocument(), Base64.encodeBase64String(transformer.transform(dataFilesRequest).getBytes()));
    }

    @Test
    public void pdfTypeIsCorrectlyTransformedToDocumentType() {
        SoapDataFilesRequest dataFilesRequest = createSoapDataFilesRequest("Ymxh", DocumentType.PDF);
        assertEquals(dataFilesRequest.getDocumentType().name(), transformer.transform(dataFilesRequest).getDocumentType().name());
    }

    @Test
    public void bdocTypeIsCorrectlyTransformedToDocumentType() {
        SoapDataFilesRequest dataFilesRequest = createSoapDataFilesRequest("Ymxh", DocumentType.BDOC);
        assertEquals(dataFilesRequest.getDocumentType().name(), transformer.transform(dataFilesRequest).getDocumentType().name());
    }

    @Test
    public void ddocTypeIsCorrectlyTransformedToDocumentType() {
        SoapDataFilesRequest dataFilesRequest = createSoapDataFilesRequest("Ymxh", DocumentType.DDOC);
        assertEquals(dataFilesRequest.getDocumentType().name(), transformer.transform(dataFilesRequest).getDocumentType().name());
    }

    @Test
    public void xroadTypeIsCorrectlyTransformedToDocumentType() {
        SoapDataFilesRequest dataFilesRequest = createSoapDataFilesRequest("Ymxh", DocumentType.XROAD);
        assertEquals(dataFilesRequest.getDocumentType().name(), transformer.transform(dataFilesRequest).getDocumentType().name());
    }

    private SoapDataFilesRequest createSoapDataFilesRequest(String document, DocumentType docType) {
        SoapDataFilesRequest dataFilesRequest = new SoapDataFilesRequest();
        dataFilesRequest.setDocument(document);
        dataFilesRequest.setDocumentType(docType);
        return dataFilesRequest;
    }

}
