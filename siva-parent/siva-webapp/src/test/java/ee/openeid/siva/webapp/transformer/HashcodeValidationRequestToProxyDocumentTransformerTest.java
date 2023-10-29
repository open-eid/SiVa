/*
 * Copyright 2018 - 2023 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.proxy.ProxyRequest;
import ee.openeid.siva.proxy.document.ProxyHashcodeDataSet;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.webapp.request.Datafile;
import ee.openeid.siva.webapp.request.HashcodeValidationRequest;
import ee.openeid.siva.webapp.request.SignatureFile;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class HashcodeValidationRequestToProxyDocumentTransformerTest {

    private static final String SIGNATURE_FILE = "test-files/signatures0.xml";
    public static final String DEFAULT_SIGNATURE_POLICY = null;
    public static final String DEFAULT_REPORT_TYPE = "Simple";

    private HashcodeValidationRequestToProxyDocumentTransformer transformer = new HashcodeValidationRequestToProxyDocumentTransformer();

    @Test
    void contentIsCorrectlyTransformedToBytes() throws IOException, URISyntaxException {

        SignatureFile signatureFile = createSignatureFile(getSignature(), Collections.emptyList());
        MockHashcodeValidationRequest validationRequest = createHashcodeValidationRequest(null, Collections.singletonList(signatureFile));

        ProxyHashcodeDataSet proxyDocument = transformer.transform(validationRequest);
        assertEquals(validationRequest.getSignatureFiles().get(0).getSignature(), Base64.encodeBase64String(proxyDocument.getSignatureFiles().get(0).getSignature()));
    }

    @Test
    void reportTypeDefaultsToSimple() throws URISyntaxException, IOException {
        SignatureFile signatureFile = createSignatureFile(getSignature(), Collections.emptyList());
        MockHashcodeValidationRequest validationRequest = createHashcodeValidationRequest(null, Collections.singletonList(signatureFile));
        assertNull(validationRequest.getReportType());
        ProxyRequest proxyRequest = transformer.transform(validationRequest);
        assertSame(proxyRequest.getReportType(), ReportType.SIMPLE);
    }

    @Test
    void reportTypeRemainsUnchangedIfNotNull() throws URISyntaxException, IOException {
        SignatureFile signatureFile = createSignatureFile(getSignature(), Collections.emptyList());
        MockHashcodeValidationRequest validationRequest = createHashcodeValidationRequest("Detailed", Collections.singletonList(signatureFile));
        ProxyRequest proxyRequest = transformer.transform(validationRequest);
        assertSame(proxyRequest.getReportType(), ReportType.DETAILED);
    }

    @Test
    void reportTypeIsCaseInsensitive() throws URISyntaxException, IOException {

        SignatureFile signatureFile = createSignatureFile(getSignature(), Collections.emptyList());
        MockHashcodeValidationRequest validationRequest = createHashcodeValidationRequest("simple", Collections.singletonList(signatureFile));
        ProxyRequest proxyRequest = transformer.transform(validationRequest);
        assertSame(proxyRequest.getReportType(), ReportType.SIMPLE);


        SignatureFile signatureFile2 = createSignatureFile(getSignature(), Collections.emptyList());
        MockHashcodeValidationRequest validationRequest2 = createHashcodeValidationRequest("SIMPLE", Collections.singletonList(signatureFile2));

        ProxyRequest proxyRequest2 = transformer.transform(validationRequest2);
        assertSame(proxyRequest2.getReportType(), ReportType.SIMPLE);
    }

    @Test
    void signaturePolicyRemainsUnchanged() throws IOException, URISyntaxException {
        SignatureFile signatureFile = createSignatureFile(getSignature(), Collections.emptyList());
        MockHashcodeValidationRequest validationRequest = createHashcodeValidationRequest(null, Collections.singletonList(signatureFile));
        ProxyRequest proxyRequest = transformer.transform(validationRequest);
        assertEquals(validationRequest.getSignaturePolicy(), proxyRequest.getSignaturePolicy());
    }

    @Test
    void dataFilesTransformedCorrectlyWhileValuesRemainingUnchanged() throws URISyntaxException, IOException {
        Datafile datafile1 = new Datafile();
        datafile1.setFilename("test.pdf");
        datafile1.setHashAlgo("SHA256");
        datafile1.setHash("IucjUcbRo9Rke0bZLiHcwiIiplP9pSrSPr7LKln1EiI=");

        Datafile datafile2 = new Datafile();
        datafile2.setFilename("test2.pdf");
        datafile2.setHashAlgo("SHA512");
        datafile2.setHash("AucjUcbRo9Rke0bZLiHcwiIiplP9pSrSPr7LKln1EiI=");

        List<Datafile> datafiles = new ArrayList<>();
        datafiles.add(datafile1);
        datafiles.add(datafile2);
        SignatureFile signatureFile = createSignatureFile(getSignature(), datafiles);
        MockHashcodeValidationRequest validationRequest = createHashcodeValidationRequest("Simple", Collections.singletonList(signatureFile));


        ProxyHashcodeDataSet proxyDocument = transformer.transform(validationRequest);

        List<ee.openeid.siva.validation.document.Datafile> dataFiles = proxyDocument.getSignatureFiles().get(0).getDatafiles();

        assertFalse(dataFiles.isEmpty());
        assertSame(validationRequest.getSignatureFiles().get(0).getDatafiles().size(), dataFiles.size());
        assertDataFileValues(dataFiles.get(0), datafile1);
        assertDataFileValues(dataFiles.get(1), datafile2);
    }

    private void assertDataFileValues(ee.openeid.siva.validation.document.Datafile proxyDataFile, Datafile requestDataFile) {
        assertSame(proxyDataFile.getFilename(), requestDataFile.getFilename());
        assertSame(proxyDataFile.getHash(), requestDataFile.getHash());
        assertSame(proxyDataFile.getHashAlgo(), requestDataFile.getHashAlgo());
    }

    private String getSignature() throws URISyntaxException, IOException {
        return Base64.encodeBase64String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(SIGNATURE_FILE).toURI())));
    }

    private SignatureFile createSignatureFile(String signature, List<Datafile> dataFiles) {
        SignatureFile signatureFile = new SignatureFile();
        signatureFile.setDatafiles(dataFiles);
        signatureFile.setSignature(signature);
        return signatureFile;
    }

    private MockHashcodeValidationRequest createHashcodeValidationRequest(String reportType, List<SignatureFile> signatureFiles) {
        MockHashcodeValidationRequest request = new MockHashcodeValidationRequest();
        request.setSignaturePolicy("POLv3");
        request.setReportType(reportType);
        request.setSignatureFiles(signatureFiles);
        return request;
    }

    @Data
    public static class MockHashcodeValidationRequest implements HashcodeValidationRequest {

        private List<SignatureFile> signatureFiles = new ArrayList<>();
        private String signaturePolicy = DEFAULT_SIGNATURE_POLICY;
        private String reportType = DEFAULT_REPORT_TYPE;

        @Override
        public List<SignatureFile> getSignatureFiles() {
            return signatureFiles;
        }

        @Override
        public String getSignaturePolicy() {
            return signaturePolicy;
        }

        @Override
        public String getReportType() {
            return reportType;
        }

    }
}

