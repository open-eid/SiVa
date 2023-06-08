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

package ee.openeid.siva.webapp.transformer;

import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.testutils.MockDataFilesRequestBuilder;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataFilesRequestToProxyDocumentTransformerTest {

    private static final String VALID_DDOC_FILE = "test-files/ddoc_valid_2_signatures.ddoc";
    private DataFilesRequestToProxyDocumentTransformer transformer = new DataFilesRequestToProxyDocumentTransformer();
    private MockDataFilesRequestBuilder.MockDataFilesRequest dataFilesRequest;

    @BeforeEach
    public void setUp() throws Exception {
        setValidDdocDataFilesRequest();
    }

    @Test
    void ddocTypeIsCorrectlyTransformedToDocumentType() {
        assertEquals(DocumentType.DDOC.name().toLowerCase(), transformer.transform(dataFilesRequest).getName());
    }

    @Test
    void contentIsCorrectlyTransformedToBytes() {
        ProxyDocument proxyDocument = transformer.transform(dataFilesRequest);
        assertEquals(dataFilesRequest.getDocument(), Base64.encodeBase64String(proxyDocument.getBytes()));
    }


    private void setValidDdocDataFilesRequest() throws Exception {
        Path filepath = Paths.get(getClass().getClassLoader().getResource(VALID_DDOC_FILE).toURI());
        dataFilesRequest = MockDataFilesRequestBuilder
                .aDataFilesRequest()
                .withType("ddoc")
                .withDocument(filepath)
                .build();
    }
}
