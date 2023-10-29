/*
 * Copyright 2017 - 2023 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.webapp.soap.interceptor;

import ee.openeid.siva.proxy.document.ReportType;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoapRequestHashcodeValidationInterceptorTest {

    private static final int EXPECTED_STATUS_CODE = 400;
    private static final String EXPECTED_FAULT_CODE = "Client";
    private static final String INVALID_REQUEST_MESSAGE = "Invalid request";
    private static final String SIGNATURE_FILE_INVALID_BASE64_ERROR_MESSAGE = "Signature file is not valid base64 encoded string";
    private static final String SIGNATURE_FILE_INVALID_FORMAT_ERROR_MESSAGE = "Invalid filename format";
    private static final String FILENAME_INVALID_FORMAT_ERROR_MESSAGE = "Invalid datafile filename format";
    private static final String FILENAME_INVALID_EXTENSION_ERROR_MESSAGE = "Invalid filename extension. Only xml files accepted.";
    private static final String INVALID_HASH_ALGORITHM = "Invalid hash algorithm";

    @Mock
    private SoapMessage message;
    @Mock
    private SOAPMessage soapMessage;
    @Mock
    private SOAPPart soapPart;
    @Mock
    private SOAPEnvelope envelope;
    @Mock
    private SOAPBody body;
    @Mock
    private Node signatureNode;
    @Mock
    private Node signatureFilesNode;
    @Mock
    private Node signatureFileNode;
    @Mock
    private Node dataFilesNode;
    @Mock
    private Node signaturePolicyNode;
    @Mock
    private Node reportTypeNode;
    @Mock
    private Node dataFilesFilenameNode;
    @Mock
    private Node dataFilesHashAlgoNode;
    @Mock
    private Node dataFilesHashNode;

    @InjectMocks
    private SoapRequestHashcodeValidationInterceptor validationInterceptor = new SoapRequestHashcodeValidationInterceptor();

    @BeforeEach
    public void setUp() throws SOAPException {
        mockDataFileChildNode(dataFilesFilenameNode, "test.txt");
        mockDataFileChildNode(dataFilesHashAlgoNode, "SHA256");
        mockDataFileChildNode(dataFilesHashNode, "dGVzdA==");
        mockDataFileChildNode(signatureNode, "dGVzdA==");
        mockValidSoapMessage();
    }

    @Test
    void validSoapMessage() {
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    @Test
    void whenSoapMessageIsNull_thenFaultIsThrownWithInvalidRequestMessage() {
        mockNullReturningSoapMessage();
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, INVALID_REQUEST_MESSAGE);
    }

    @Test
    void whenSignatureNotBase64Encoded_thenFaultIsThrown() {
        mockDataFileChildNode(signatureNode, "NOT.BASE64.ENCODED.TEXT");
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, SIGNATURE_FILE_INVALID_BASE64_ERROR_MESSAGE);
    }

    @Test
    void whenSignatureFileEmpty_thenNotValidated() {
        mockDataFileChildNode(signatureNode, null);
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    @Test
    void reportTypeIsCastedUpperForCaseInsensitivity() {
        mockReportTypeNode("Simple");
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);

        verify(reportTypeNode, times(1)).setNodeValue("SIMPLE");
    }

    @Test
    void dataFileHashAlgorithmIsCastedUpperForCaseInsensitivity() {
        mockDataFileChildNode(dataFilesHashAlgoNode, "sha256");
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);

        verify(dataFilesHashAlgoNode, times(1)).setTextContent("SHA256");
    }

    @Test
    void dataFileHashAlgoIsEmpty(){
        mockDataFileChildNode(dataFilesHashAlgoNode, "");
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, INVALID_HASH_ALGORITHM);
    }

    @Test
    void dataFileHashAlgoIsNull(){
        mockDataFileChildNode(dataFilesHashAlgoNode, null);
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, INVALID_HASH_ALGORITHM);
    }

    @Test
    void dataFileFilenameFormatIsNotValidated() {
        mockDataFileChildNode(dataFilesFilenameNode, "FILENAME_WITH_INVALID_ELEMENTS_&*:%.xml");
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    @Test
    void whenDataFileFilenameNull_thenNotValidated() {
        mockDataFileChildNode(dataFilesFilenameNode, null);
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, FILENAME_INVALID_FORMAT_ERROR_MESSAGE);
    }

    @Test
    void whenDataFileFilenameEmpty_thenNotValidated() {
        mockDataFileChildNode(dataFilesFilenameNode, "");
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, FILENAME_INVALID_FORMAT_ERROR_MESSAGE);
    }

    @Test
    void dataFileFilenameExtensionNotValidated() {
        mockDataFileChildNode(dataFilesFilenameNode, "VALID_DATAFILE.random_extension");
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    @Test
    void whenDataFileHashInvalidFormat_thenFaultIsThrown() {
        mockDataFileChildNode(dataFilesHashNode, "NOT.VALID.BASE64.ENCODED.CONTENT");
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, "Document is not encoded in a valid base64 string");
    }

    @Test
    void whenDataFileHashNull_thenNotValidated() {
        mockDataFileChildNode(dataFilesHashNode, null);
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    @Test
    void whenDataFileHashEmpty_thenNotValidated() {
        mockDataFileChildNode(dataFilesHashNode, "");
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    @Test
    void noSoapFaultIsThrownWithValidRequest() {
        noFaultThrown(handleMessageInInterceptor(message));
    }

    private Fault handleMessageInInterceptor(SoapMessage soapMessage) {
        try {
            validationInterceptor.handleMessage(soapMessage);
        } catch (Fault soapFault) {
            return soapFault;
        }
        return null;
    }

    private void noFaultThrown(Fault soapFault) {
        assertNull(soapFault);
    }

    private void assertFaultWithExpectedMessage(Fault soapFault, String message) {
        assertNotNull(soapFault);
        assertEquals(EXPECTED_FAULT_CODE, soapFault.getFaultCode().toString());
        assertEquals(EXPECTED_STATUS_CODE, soapFault.getStatusCode());
        assertEquals(message, soapFault.getMessage());
    }

    private void mockNullReturningSoapMessage() {
        doReturn(null).when(message).getContent(any());
    }

    private void mockValidSoapMessage() throws SOAPException {
        lenient().doReturn(body).when(envelope).getBody();
        lenient().doReturn(envelope).when(soapPart).getEnvelope();
        lenient().doReturn(soapPart).when(soapMessage).getSOAPPart();
        lenient().doReturn(soapMessage).when(message).getContent(SOAPMessage.class);
        mockSignatureFilesNode();
        mockReportTypeNode(ReportType.DETAILED.getValue());
        mockSignaturePolicyNode("POLv3");
    }

    private void mockSignatureFilesNode() {

        Node signatureFilesNode = mock(Node.class);
        NodeList nodeList = new MockNodeList(signatureFilesNode);
        lenient().doReturn(nodeList).when(body).getElementsByTagName("SignatureFiles");
        lenient().doReturn((short) 1).when(signatureFilesNode).getNodeType();

        NodeList signatureFilesChildNodes = new MockNodeList( signatureFileNode);
        lenient().doReturn(signatureFilesChildNodes).when(signatureFilesNode).getChildNodes();
        lenient().doReturn((short) 1).when(signatureFileNode).getNodeType();

        NodeList signatureFileChildNodes = new MockNodeList(signatureNode, dataFilesNode);
        lenient().doReturn(signatureFileChildNodes).when(signatureFileNode).getChildNodes();
        lenient().doReturn((short) 1).when(signatureNode).getNodeType();
        lenient().doReturn((short) 1).when(dataFilesNode).getNodeType();

        lenient().doReturn("Signature").when(signatureNode).getLocalName();
        lenient().doReturn("DataFiles").when(dataFilesNode).getLocalName();


        mockDataFileNode();
    }

    private void mockSignaturePolicyNode(String signaturePolicy) {
        mockNode(signaturePolicyNode, "SignaturePolicy", signaturePolicy);
    }

    private void mockReportTypeNode(String reportType) {
        mockNode(reportTypeNode, "ReportType", reportType);
    }

    private void mockSignatureNode(String reportType) {
        mockNode(signatureNode, "ReportType", reportType);
    }

    private void mockDataFileNode() {

        Node dataFileNode = mock(Node.class);
        NodeList childNodes = new MockNodeList(dataFileNode);
        lenient().doReturn(childNodes).when(dataFilesNode).getChildNodes();
        lenient().doReturn((short) 1).when(dataFileNode).getNodeType();

        NodeList parameterChildNodes = new MockNodeList(dataFilesFilenameNode, dataFilesHashAlgoNode, dataFilesHashNode);
        lenient().doReturn(parameterChildNodes).when(dataFileNode).getChildNodes();

        lenient().doReturn("Filename").when(dataFilesFilenameNode).getLocalName();
        lenient().doReturn("HashAlgo").when(dataFilesHashAlgoNode).getLocalName();
        lenient().doReturn("Hash").when(dataFilesHashNode).getLocalName();
        lenient().doReturn((short) 1).when(dataFilesFilenameNode).getNodeType();
        lenient().doReturn((short) 1).when(dataFilesHashAlgoNode).getNodeType();
        lenient().doReturn((short) 1).when(dataFilesHashNode).getNodeType();
    }

    private void mockDataFileChildNode(Node dataFileChildNode, String textContent) {
        lenient().doReturn(textContent).when(dataFileChildNode).getTextContent();
    }

    private void mockNode(Node node, String tagName, String value) {
        NodeList nodeList = new MockNodeList(node);
        lenient().doReturn(value).when(node).getNodeValue();
        lenient().doReturn(nodeList).when(body).getElementsByTagName(tagName);
    }

    static class MockNodeList implements NodeList {
        Node[] node;
        MockNodeList(Node... node) {
            this.node = node;
        }
        @Override
        public Node item(int index) {
            return node[index];
        }
        @Override
        public int getLength() {
            return node.length;
        }
    }
}
