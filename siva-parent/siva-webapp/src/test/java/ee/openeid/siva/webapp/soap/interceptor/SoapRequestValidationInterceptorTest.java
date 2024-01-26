/*
 * Copyright 2019 - 2024 Riigi Infosüsteemi Amet
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

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class SoapRequestValidationInterceptorTest {

    private static final String INVALID_REQUEST = "Invalid request";
    private static final int EXPECTED_STATUS_CODE = 400;
    private static final String EXPECTED_FAULT_CODE = "Client";
    private static final String INVALID_BASE64 = "Document is not encoded in a valid base64 string";
    private static final String INVALID_POLICY = "Invalid signature policy";
    private static final String INVALID_DOCUMENTTYPE = "Invalid document type";

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
    private Node filenameNode;
    @Mock
    private Node documentNode;
    @Mock
    private Node documentTypeNode;
    @Mock
    private Node policyNode;
    private NodeList filenameNodeList;
    private NodeList documentNodeList;
    private NodeList documentTypeNodeList;
    private NodeList policyNodeList;

    @InjectMocks
    private SoapRequestValidationInterceptor validationInterceptor = new SoapRequestValidationInterceptor();

    @Test
    void whenSoapMessageIsNullThenFaultIsThrownWithInvalidRequestMessage() {
        mockNullReturningSoapMessage();
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, INVALID_REQUEST);
    }

    @Test
    void whenDocumentIsInvalidThenFaultIsThrownWithInvalidDocumentMessage() throws SOAPException {
        mockSoapMessage("filename", "ÖÄÜ", "AA");
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, INVALID_BASE64);
    }

    @Test
    void whenPolicyIsInvalidThenFaultIsThrownWithInvalidPolicyMessage() throws SOAPException {
        mockSoapMessage("filename", "AABBBAA", ";:::;;");
        Fault soapFault = handleMessageInInterceptor(message);
        assertFaultWithExpectedMessage(soapFault, INVALID_POLICY);
    }

    @Test
    void noSoapFaultIsThrownWithValidRequest() throws SOAPException {
        mockSoapMessage("filename", "c2Q=", "AA");
        Fault soapFault = handleMessageInInterceptor(message);
        assertNull(soapFault);
    }

    private Fault handleMessageInInterceptor(SoapMessage soapMessage) {
        try {
            validationInterceptor.handleMessage(soapMessage);
        } catch (Fault soapFault) {
            return soapFault;
        }
        return null;
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

    private void mockSoapMessage(String filename, String document, String policy) throws SOAPException {
        doReturn(body).when(envelope).getBody();
        doReturn(envelope).when(soapPart).getEnvelope();
        doReturn(soapPart).when(soapMessage).getSOAPPart();
        doReturn(soapMessage).when(message).getContent(SOAPMessage.class);
        mockFilenameNode(filename);
        mockDocumentNode(document);
        mockPolicyNode(policy);
    }

    private void mockDocumentNode(String document) {
        mockNode(documentNode, documentNodeList, "Document", document);
    }

    private void mockFilenameNode(String filename) {
        mockNode(filenameNode, filenameNodeList, "Filename", filename);
    }

    private void mockDocumentTypeNode(String documentType) {
        mockNode(documentTypeNode, documentTypeNodeList, "DocumentType", documentType);
    }

    private void mockPolicyNode(String policy) {
        mockNode(policyNode, policyNodeList, "SignaturePolicy", policy);
    }

    private void mockNode(Node node, NodeList nodeList, String tagName, String value) {
        nodeList = new MockNodeList(node);
        lenient().doReturn(value).when(node).getNodeValue();
        lenient().doReturn(nodeList).when(body).getElementsByTagName(tagName);
    }

    static class MockNodeList implements NodeList {
        Node node;
        MockNodeList(Node node) {
            this.node = node;
        }
        @Override
        public Node item(int index) {
            return node;
        }
        @Override
        public int getLength() {
            return 1;
        }
    }
}
