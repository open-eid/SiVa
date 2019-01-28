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

package ee.openeid.siva.webapp.soap.interceptor;

import ee.openeid.siva.proxy.document.ReportType;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.SoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SoapRequestHashcodeValidationInterceptorTest {

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
    @Mock
    private SoapInterceptor mockSaajIn;

    @InjectMocks
    private SoapRequestHashcodeValidationInterceptor validationInterceptor = new SoapRequestHashcodeValidationInterceptor();

    @Before
    public void setUp() throws SOAPException {
        doNothing().when(mockSaajIn).handleMessage(any());
        mockDataFileChildNode(dataFilesFilenameNode, "test.txt");
        mockDataFileChildNode(dataFilesHashAlgoNode, "SHA256");
        mockDataFileChildNode(dataFilesHashNode, "dGVzdA==");
        mockDataFileChildNode(signatureNode, "dGVzdA==");
        mockValidSoapMessage();
    }

    @Test
    public void validSoapMessage() {
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    @Test
    public void whenSoapMessageIsNull_thenFaultIsThrownWithInvalidRequestMessage() {
        mockNullReturningSoapMessage();
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, INVALID_REQUEST_MESSAGE);
    }

    @Test
    public void whenSignatureNotBase64Encoded_thenFaultIsThrown() {
        mockDataFileChildNode(signatureNode, "NOT.BASE64.ENCODED.TEXT");
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, SIGNATURE_FILE_INVALID_BASE64_ERROR_MESSAGE);
    }

    @Test
    public void whenSignatureFileEmpty_thenNotValidated() {
        mockDataFileChildNode(signatureNode, null);
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    @Test
    public void reportTypeIsCastedUpperForCaseInsensitivity() {
        mockReportTypeNode("Simple");
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);

        verify(reportTypeNode, times(1)).setNodeValue("SIMPLE");
    }

    @Test
    public void dataFileHashAlgorithmIsCastedUpperForCaseInsensitivity() {
        mockDataFileChildNode(dataFilesHashAlgoNode, "sha256");
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);

        verify(dataFilesHashAlgoNode, times(1)).setTextContent("SHA256");
    }

    @Test
    public void dataFileHashAlgoIsEmpty(){
        mockDataFileChildNode(dataFilesHashAlgoNode, "");
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, INVALID_HASH_ALGORITHM);
    }

    @Test
    public void dataFileHashAlgoIsNull(){
        mockDataFileChildNode(dataFilesHashAlgoNode, null);
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, INVALID_HASH_ALGORITHM);
    }

    @Test
    public void dataFileFilenameFormatIsNotValidated() {
        mockDataFileChildNode(dataFilesFilenameNode, "FILENAME_WITH_INVALID_ELEMENTS_&*:%.xml");
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    @Test
    public void whenDataFileFilenameNull_thenNotValidated() {
        mockDataFileChildNode(dataFilesFilenameNode, null);
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, FILENAME_INVALID_FORMAT_ERROR_MESSAGE);
    }

    @Test
    public void whenDataFileFilenameEmpty_thenNotValidated() {
        mockDataFileChildNode(dataFilesFilenameNode, "");
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, FILENAME_INVALID_FORMAT_ERROR_MESSAGE);
    }

    @Test
    public void dataFileFilenameExtensionNotValidated() {
        mockDataFileChildNode(dataFilesFilenameNode, "VALID_DATAFILE.random_extension");
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    @Test
    public void whenDataFileHashInvalidFormat_thenFaultIsThrown() {
        mockDataFileChildNode(dataFilesHashNode, "NOT.VALID.BASE64.ENCODED.CONTENT");
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, "Document is not encoded in a valid base64 string");
    }

    @Test
    public void whenDataFileHashNull_thenNotValidated() {
        mockDataFileChildNode(dataFilesHashNode, null);
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    @Test
    public void whenDataFileHashEmpty_thenNotValidated() {
        mockDataFileChildNode(dataFilesHashNode, "");
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    @Test
    public void noSoapFaultIsThrownWithValidRequest() {
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
        assertTrue(EXPECTED_STATUS_CODE == soapFault.getStatusCode());
        assertEquals(message, soapFault.getMessage());
    }

    private void mockNullReturningSoapMessage() {
        doReturn(null).when(message).getContent(any());
    }

    private void mockValidSoapMessage() throws SOAPException {
        doReturn(body).when(envelope).getBody();
        doReturn(envelope).when(soapPart).getEnvelope();
        doReturn(soapPart).when(soapMessage).getSOAPPart();
        doReturn(soapMessage).when(message).getContent(SOAPMessage.class);
        mockSignatureFilesNode();
        mockReportTypeNode(ReportType.DETAILED.getValue());
        mockSignaturePolicyNode("POLv3");
    }

    private void mockSignatureFilesNode() {

        Node signatureFilesNode = mock(Node.class);
        NodeList nodeList = new MockNodeList(signatureFilesNode);
        doReturn(nodeList).when(body).getElementsByTagName("SignatureFiles");
        doReturn((short) 1).when(signatureFilesNode).getNodeType();

        NodeList signatureFilesChildNodes = new MockNodeList( signatureFileNode);
        doReturn(signatureFilesChildNodes).when(signatureFilesNode).getChildNodes();
        doReturn((short) 1).when(signatureFileNode).getNodeType();

        NodeList signatureFileChildNodes = new MockNodeList(signatureNode, dataFilesNode);
        doReturn(signatureFileChildNodes).when(signatureFileNode).getChildNodes();
        doReturn((short) 1).when(signatureNode).getNodeType();
        doReturn((short) 1).when(dataFilesNode).getNodeType();

        doReturn("Signature").when(signatureNode).getLocalName();
        doReturn("DataFiles").when(dataFilesNode).getLocalName();


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
        doReturn(childNodes).when(dataFilesNode).getChildNodes();
        doReturn((short) 1).when(dataFileNode).getNodeType();

        NodeList parameterChildNodes = new MockNodeList(dataFilesFilenameNode, dataFilesHashAlgoNode, dataFilesHashNode);
        doReturn(parameterChildNodes).when(dataFileNode).getChildNodes();

        doReturn("Filename").when(dataFilesFilenameNode).getLocalName();
        doReturn("HashAlgo").when(dataFilesHashAlgoNode).getLocalName();
        doReturn("Hash").when(dataFilesHashNode).getLocalName();
        doReturn((short) 1).when(dataFilesFilenameNode).getNodeType();
        doReturn((short) 1).when(dataFilesHashAlgoNode).getNodeType();
        doReturn((short) 1).when(dataFilesHashNode).getNodeType();
    }

    private void mockDataFileChildNode(Node dataFileChildNode, String textContent) {
        doReturn(textContent).when(dataFileChildNode).getTextContent();
    }

    private void mockNode(Node node, String tagName, String value) {
        NodeList nodeList = new MockNodeList(node);
        doReturn(node).when(node).getFirstChild();
        doReturn(value).when(node).getNodeValue();
        doReturn(nodeList).when(body).getElementsByTagName(tagName);
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
