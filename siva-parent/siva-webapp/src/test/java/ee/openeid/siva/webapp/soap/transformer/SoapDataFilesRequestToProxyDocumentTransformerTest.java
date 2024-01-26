/*
 * Copyright 2017 - 2024 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.webapp.soap.SoapDataFilesRequest;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SoapDataFilesRequestToProxyDocumentTransformerTest {

    private SoapDataFilesRequestToProxyDocumentTransformer transformer = new SoapDataFilesRequestToProxyDocumentTransformer();

    @Test
    void contentIsCorrectlyTransformedToBytes() {
        String documentContent = "ZmlsZWNvbnRlbnQ=";
        SoapDataFilesRequest dataFilesRequest = createSoapDataFilesRequest(documentContent, "test.ddoc");
        assertEquals(dataFilesRequest.getDocument(), Base64.encodeBase64String(transformer.transform(dataFilesRequest).getBytes()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"test.pdf", "test.bdoc", "test.ddoc", "test.xroad"})
    void documentIsCorrectlyTransformedToDocumentType(String fileName) {
        SoapDataFilesRequest dataFilesRequest = createSoapDataFilesRequest("Ymxh", fileName);
        assertEquals(dataFilesRequest.getFilename(), transformer.transform(dataFilesRequest).getName());
    }

    private SoapDataFilesRequest createSoapDataFilesRequest(String document, String filename) {
        SoapDataFilesRequest dataFilesRequest = new SoapDataFilesRequest();
        dataFilesRequest.setDocument(document);
        dataFilesRequest.setFilename(filename);
        return dataFilesRequest;
    }

}
