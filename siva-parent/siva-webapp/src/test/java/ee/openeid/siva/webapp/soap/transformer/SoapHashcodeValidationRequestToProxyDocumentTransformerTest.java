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

package ee.openeid.siva.webapp.soap.transformer;

import ee.openeid.siva.proxy.document.ProxyHashcodeDataSet;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.webapp.soap.HashAlgorithm;
import ee.openeid.siva.webapp.soap.HashDataFile;
import ee.openeid.siva.webapp.soap.SignatureFile;
import ee.openeid.siva.webapp.soap.SoapHashcodeValidationRequest;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class SoapHashcodeValidationRequestToProxyDocumentTransformerTest {

    private static final String SIGNATURE_FILE = "test-files/signatures0.xml";
    private SoapHashcodeValidationRequestToProxyDocumentTransformer transformer = new SoapHashcodeValidationRequestToProxyDocumentTransformer();

    @Test
    public void contentIsCorrectlyTransformedToBytes() throws IOException, URISyntaxException {
        SoapHashcodeValidationRequest validationRequest = mockValidValidationRequest();
        ProxyHashcodeDataSet proxyDocument = transformer.transform(validationRequest);
        assertEquals(validationRequest.getSignatureFiles().getSignatureFile().get(0).getSignature(), Base64.encodeBase64String(proxyDocument.getSignatureFiles().get(0).getSignature()));
    }

    @Test
    public void reportTypeDefaultsToSimple() throws URISyntaxException, IOException {
        SoapHashcodeValidationRequest validationRequest = mockValidValidationRequest();
        validationRequest.setReportType(null);

        assertNull(validationRequest.getReportType());
        ProxyHashcodeDataSet proxyDocument = transformer.transform(validationRequest);
        assertSame(proxyDocument.getReportType(), ReportType.SIMPLE);
    }

    @Test
    public void reportTypeRemainsUnchangedIfNotNull() throws URISyntaxException, IOException {
        SoapHashcodeValidationRequest validationRequest = mockValidValidationRequest();
        validationRequest.setReportType(ee.openeid.siva.webapp.soap.ReportType.DETAILED);

        ProxyHashcodeDataSet proxyDocument = transformer.transform(validationRequest);
        assertSame(proxyDocument.getReportType(), ReportType.DETAILED);
    }

    @Test
    public void signaturePolicyRemainsUnchanged() throws IOException, URISyntaxException {
        SoapHashcodeValidationRequest validationRequest = mockValidValidationRequest();
        ProxyHashcodeDataSet proxyDocument = transformer.transform(validationRequest);
        assertEquals(proxyDocument.getSignaturePolicy(), proxyDocument.getSignaturePolicy());
    }

    @Test
    public void dataFilesTransformedCorrectlyWhileValuesRemainingUnchanged() throws URISyntaxException, IOException {
        SoapHashcodeValidationRequest validationRequest = mockValidValidationRequest();

        ProxyHashcodeDataSet proxyDocument = transformer.transform(validationRequest);
        List<HashDataFile> requestDatafiles = validationRequest.getSignatureFiles().getSignatureFile().get(0).getDataFiles().getDataFile();
        List<Datafile> proxyDataFiles = proxyDocument.getSignatureFiles().get(0).getDatafiles();

        assertFalse(proxyDataFiles.isEmpty());
        assertSame(requestDatafiles.size(), proxyDataFiles.size());
        assertDataFileValues(proxyDataFiles.get(0), requestDatafiles.get(0));
    }

    private void assertDataFileValues(ee.openeid.siva.validation.document.Datafile proxyDataFile, HashDataFile requestDataFile) {
        assertSame(proxyDataFile.getFilename(), requestDataFile.getFilename());
        assertSame(proxyDataFile.getHash(), requestDataFile.getHash());
        assertSame(proxyDataFile.getHashAlgo(), requestDataFile.getHashAlgo().value());
    }

    private SoapHashcodeValidationRequest mockValidValidationRequest() throws URISyntaxException, IOException {
        String base64EncodedSignatureFile = Base64.encodeBase64String(
                Files.readAllBytes(
                        Paths.get(getClass().getClassLoader().getResource(SIGNATURE_FILE).toURI())));

        HashDataFile hashDataFile = new HashDataFile();
        hashDataFile.setFilename("test.pdf");
        hashDataFile.setHashAlgo(HashAlgorithm.SHA_256);
        hashDataFile.setHash("IucjUcbRo9Rke0bZLiHcwiIipplP9pSrSPr7LKln1EiI=");

        SignatureFile signatureFile = new SignatureFile();
        SoapHashcodeValidationRequest.SignatureFiles signatureFiles = new SoapHashcodeValidationRequest.SignatureFiles();

        SignatureFile.DataFiles dataFiles = new SignatureFile.DataFiles();
        dataFiles.getDataFile().add(hashDataFile);

        signatureFile.setDataFiles(dataFiles);
        signatureFile.setSignature(base64EncodedSignatureFile);
        signatureFiles.getSignatureFile().add(signatureFile);

        SoapHashcodeValidationRequest validationRequest = new SoapHashcodeValidationRequest();
        validationRequest.setReportType(ee.openeid.siva.webapp.soap.ReportType.SIMPLE);
        validationRequest.setSignaturePolicy("POLv3");
        validationRequest.setSignatureFiles(signatureFiles);
        return validationRequest;
    }
}
