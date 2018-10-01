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

package ee.openeid.siva.webapp.transformer;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.testutils.MockHashcodeValidationRequestBuilder;
import ee.openeid.siva.testutils.MockHashcodeValidationRequestBuilder.MockHashcodeValidationRequest;
import ee.openeid.siva.webapp.request.Datafile;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class HashcodeValidationRequestToProxyDocumentTransformerTest {

    private static final String SIGNATURE_FILE = "test-files/signatures0.xml";
    private HashcodeValidationRequestToProxyDocumentTransformer transformer = new HashcodeValidationRequestToProxyDocumentTransformer();
    private MockHashcodeValidationRequest validationRequest;

    @Before
    public void setUp() throws Exception {
        setHashcodeValidationRequest();
    }

    @Test
    public void filenameRemainsUnchanged() {
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        assertEquals(validationRequest.getFilename(), proxyDocument.getName());
    }

    @Test
    public void contentIsCorrectlyTransformedToBytes() {
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        assertEquals(validationRequest.getSignatureFile(), Base64.encodeBase64String(proxyDocument.getBytes()));
    }

    @Test
    public void reportTypeDefaultsToSimple() throws URISyntaxException, IOException {
        MockHashcodeValidationRequest validationRequest = MockHashcodeValidationRequestBuilder
                .aHashcodeValidationRequest()
                .withSignaturePolicy("POLv3")
                .withReportType(null)
                .withSignature(Paths.get(getClass().getClassLoader().getResource(SIGNATURE_FILE).toURI()))
                .build();

        assertNull(validationRequest.getReportType());
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        assertSame(proxyDocument.getReportType(), ReportType.SIMPLE);
    }

    @Test
    public void reportTypeRemainsUnchangedIfNotNull() throws URISyntaxException, IOException {
        MockHashcodeValidationRequest validationRequest = MockHashcodeValidationRequestBuilder
                .aHashcodeValidationRequest()
                .withSignaturePolicy("POLv3")
                .withReportType("Detailed")
                .withSignature(Paths.get(getClass().getClassLoader().getResource(SIGNATURE_FILE).toURI()))
                .build();

        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        assertSame(proxyDocument.getReportType(), ReportType.DETAILED);
    }

    @Test
    public void reportTypeIsCaseInsensitive() throws URISyntaxException, IOException {
        ProxyDocument proxyDocument = transformer.transform(
                MockHashcodeValidationRequestBuilder
                    .aHashcodeValidationRequest()
                    .withSignaturePolicy("POLv3")
                    .withReportType("simple")
                    .withSignature(Paths.get(getClass().getClassLoader().getResource(SIGNATURE_FILE).toURI()))
                    .build());
        assertSame(proxyDocument.getReportType(), ReportType.SIMPLE);

        ProxyDocument proxyDocument2 = transformer.transform(
                MockHashcodeValidationRequestBuilder
                    .aHashcodeValidationRequest()
                    .withSignaturePolicy("POLv3")
                    .withReportType("SIMPLE")
                    .withSignature(Paths.get(getClass().getClassLoader().getResource(SIGNATURE_FILE).toURI()))
                    .build());
        assertSame(proxyDocument2.getReportType(), ReportType.SIMPLE);
    }

    @Test
    public void signaturePolicyRemainsUnchanged() {
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        assertEquals(validationRequest.getSignaturePolicy(), proxyDocument.getSignaturePolicy());
    }

    @Test
    public void dataFilesTransformedCorrectlyWhileValuesRemainingUnchanged() throws URISyntaxException, IOException {
        Datafile datafile1 = new Datafile();
        datafile1.setFilename("test.pdf");
        datafile1.setHashAlgo("SHA256");
        datafile1.setHash("IucjUcbRo9Rke0bZLiHcwiIiplP9pSrSPr7LKln1EiI=");

        Datafile datafile2 = new Datafile();
        datafile2.setFilename("test2.pdf");
        datafile2.setHashAlgo("SHA512");
        datafile2.setHash("AucjUcbRo9Rke0bZLiHcwiIiplP9pSrSPr7LKln1EiI=");

        MockHashcodeValidationRequest validationRequest = MockHashcodeValidationRequestBuilder
                .aHashcodeValidationRequest()
                .withSignaturePolicy("POLv3")
                .withReportType("Simple")
                .withSignature(Paths.get(getClass().getClassLoader().getResource(SIGNATURE_FILE).toURI()))
                .withDataFile(datafile1)
                .withDataFile(datafile2)
                .build();

        ProxyDocument proxyDocument = transformer.transform(validationRequest);

        assertFalse(proxyDocument.getDatafiles().isEmpty());
        assertSame(validationRequest.getDatafiles().size(), proxyDocument.getDatafiles().size());
        assertDataFileValues(proxyDocument.getDatafiles().get(0), datafile1);
        assertDataFileValues(proxyDocument.getDatafiles().get(1), datafile2);
    }

    private void assertDataFileValues(ee.openeid.siva.proxy.document.Datafile proxyDataFile, Datafile requestDataFile) {
        assertSame(proxyDataFile.getFilename(), requestDataFile.getFilename());
        assertSame(proxyDataFile.getHash(),     requestDataFile.getHash());
        assertSame(proxyDataFile.getHashAlgo(), requestDataFile.getHashAlgo());
    }

    private void setHashcodeValidationRequest() throws Exception {
        Path filepath = Paths.get(getClass().getClassLoader().getResource(SIGNATURE_FILE).toURI());
        validationRequest = MockHashcodeValidationRequestBuilder
                .aHashcodeValidationRequest()
                .withSignaturePolicy("POLv3")
                .withSignature(filepath)
                .build();
    }
}
