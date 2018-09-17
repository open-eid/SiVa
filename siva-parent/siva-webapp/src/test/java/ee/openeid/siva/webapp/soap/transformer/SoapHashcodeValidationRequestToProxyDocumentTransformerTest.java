package ee.openeid.siva.webapp.soap.transformer;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.webapp.soap.HashAlgorithm;
import ee.openeid.siva.webapp.soap.HashDataFile;
import ee.openeid.siva.webapp.soap.SoapHashcodeValidationRequest;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class SoapHashcodeValidationRequestToProxyDocumentTransformerTest {

    private static final String SIGNATURE_FILE = "test-files/signatures0.xml";
    private SoapHashcodeValidationRequestToProxyDocumentTransformer transformer = new SoapHashcodeValidationRequestToProxyDocumentTransformer();

    @Test
    public void filenameRemainsUnchanged() throws IOException, URISyntaxException {
        SoapHashcodeValidationRequest validationRequest = mockValidValidationRequest();
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        assertEquals(validationRequest.getFilename(), proxyDocument.getName());
    }

    @Test
    public void contentIsCorrectlyTransformedToBytes() throws IOException, URISyntaxException {
        SoapHashcodeValidationRequest validationRequest = mockValidValidationRequest();
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        assertEquals(validationRequest.getSignatureFile(), Base64.encodeBase64String(proxyDocument.getBytes()));
    }

    @Test
    public void reportTypeDefaultsToSimple() throws URISyntaxException, IOException {
        SoapHashcodeValidationRequest validationRequest = mockValidValidationRequest();
        validationRequest.setReportType(null);

        assertNull(validationRequest.getReportType());
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        assertSame(proxyDocument.getReportType(), ReportType.SIMPLE);
    }

    @Test
    public void reportTypeRemainsUnchangedIfNotNull() throws URISyntaxException, IOException {
        SoapHashcodeValidationRequest validationRequest = mockValidValidationRequest();
        validationRequest.setReportType(ee.openeid.siva.webapp.soap.ReportType.DETAILED);

        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        assertSame(proxyDocument.getReportType(), ReportType.DETAILED);
    }

    @Test
    public void signaturePolicyRemainsUnchanged() throws IOException, URISyntaxException {
        SoapHashcodeValidationRequest validationRequest = mockValidValidationRequest();
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        assertEquals(proxyDocument.getSignaturePolicy(), proxyDocument.getSignaturePolicy());
    }

    @Test
    public void dataFilesTransformedCorrectlyWhileValuesRemainingUnchanged() throws URISyntaxException, IOException {
        SoapHashcodeValidationRequest validationRequest = mockValidValidationRequest();

        ProxyDocument proxyDocument = transformer.transform(validationRequest);

        assertFalse(proxyDocument.getDatafiles().isEmpty());
        assertSame(validationRequest.getDataFiles().getDataFile().size(), proxyDocument.getDatafiles().size());
        assertDataFileValues(proxyDocument.getDatafiles().get(0), validationRequest.getDataFiles().getDataFile().get(0));
    }

    private void assertDataFileValues(ee.openeid.siva.proxy.document.Datafile proxyDataFile, HashDataFile requestDataFile) {
        assertSame(proxyDataFile.getFilename(), requestDataFile.getFilename());
        assertSame(proxyDataFile.getHash(),     requestDataFile.getHash());
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

        SoapHashcodeValidationRequest.DataFiles dataFiles = new SoapHashcodeValidationRequest.DataFiles();
        dataFiles.getDataFile().add(hashDataFile);

        SoapHashcodeValidationRequest validationRequest = new SoapHashcodeValidationRequest();
        validationRequest.setSignatureFile(base64EncodedSignatureFile);
        validationRequest.setFilename("signature0.xml");
        validationRequest.setReportType(ee.openeid.siva.webapp.soap.ReportType.SIMPLE);
        validationRequest.setSignaturePolicy("POLv3");
        validationRequest.setDataFiles(dataFiles);
        return validationRequest;
    }
}
