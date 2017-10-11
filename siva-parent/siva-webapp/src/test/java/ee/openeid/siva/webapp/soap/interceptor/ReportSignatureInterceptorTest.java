package ee.openeid.siva.webapp.soap.interceptor;

import ee.openeid.siva.signature.SignatureService;
import ee.openeid.siva.webapp.configuration.SivaWebApplicationConfigurationProperties;
import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.message.Exchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportSignatureInterceptorTest {

    @Mock
    private SoapMessage message;

    @Mock
    private SoapMessage responseMessage;

    @Mock
    private Exchange exchange;

    @Mock
    private SignatureService signatureService;

    private SOAPMessage responseSoapMessage;

    private ReportSignatureInterceptor reportSignatureInterceptor;

    @Before
    public void setUp() throws Exception {
        when(signatureService.getSignature(any(byte[].class), anyString(), anyString())).thenReturn(getRawSignatureMock());

        reportSignatureInterceptor = new ReportSignatureInterceptor();
        reportSignatureInterceptor.setSignatureService(signatureService);
        SivaWebApplicationConfigurationProperties properties = new SivaWebApplicationConfigurationProperties();
        properties.setReportSignatureEnabled(true);
        reportSignatureInterceptor.setProperties(properties);
    }

    @Test
    public void whenReportTypeInRequest_thenReportSignatureIsAdded() throws Exception {
        mockSoapMessage("Detailed");
        reportSignatureInterceptor.handleMessage(message);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        responseSoapMessage.writeTo(stream);
        assertTrue(new String(stream.toByteArray()).contains(Base64.encodeBase64String(getRawSignatureMock())));
    }

    @Test
    public void whenReportTypeInRequest_thenReportSignatureIsNotAdded() throws Exception {
        mockSoapMessage("Simple");
        reportSignatureInterceptor.handleMessage(message);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        responseSoapMessage.writeTo(stream);
        assertFalse(new String(stream.toByteArray()).contains(Base64.encodeBase64String(getRawSignatureMock())));

    }

    private void mockSoapMessage(String requestReportType) throws Exception {
        mockSoapRequestMessage(requestReportType);
        mockSoapResponseMessage();
    }

    private void mockSoapResponseMessage() throws Exception {
        responseSoapMessage = MessageFactory.newInstance("SOAP 1.1 Protocol").createMessage(null, new ByteArrayInputStream(getSoapResponseMessageMock().getBytes()));
        doReturn(responseSoapMessage).when(message).getContent(SOAPMessage.class);
    }

    private void mockSoapRequestMessage(String reportType) throws Exception {
        doReturn(exchange).when(message).getExchange();
        doReturn(responseMessage).when(exchange).getInMessage();
        SOAPMessage requestSoapMessage = MessageFactory.newInstance("SOAP 1.1 Protocol").createMessage(null, new ByteArrayInputStream(getSoapRequestMessageMock(reportType).getBytes()));
        doReturn(requestSoapMessage).when(responseMessage).getContent(SOAPMessage.class);

    }

    private String getSoapRequestMessageMock(String reportType) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">" +
                  "<soapenv:Header/>" +
                  "<soapenv:Body>" +
                     "<soap:ValidateDocument>" +
                        "<soap:ValidationRequest>" +
                           "<Document>dGVzdA==</Document>" +
                           "<Filename>test.asice</Filename>" +
                           "<ReportType>" + reportType + "</ReportType>" +
                        "</soap:ValidationRequest>" +
                     "</soap:ValidateDocument>" +
                  "</soapenv:Body>" +
               "</soapenv:Envelope>";
    }

    private String getSoapResponseMessageMock() {
        return "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                   "<SOAP-ENV:Header xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"/>" +
                   "<soap:Body>" +
                       "<ns2:ValidateDocumentResponse xmlns:ns2=\"http://soap.webapp.siva.openeid.ee/\" xmlns:ns3=\"http://dss.esig.europa.eu/validation/detailed-report\" xmlns:ns4=\"http://x-road.eu/xsd/identifiers\" xmlns:ns5=\"http://x-road.eu/xsd/xroad.xsd\">" +
                            "<ns2:ValidationReport>" +
                                "<ns2:ValidationConclusion>" +
                                    "<Policy>" +
                                        "<PolicyDescription>Policy for validating Qualified Electronic Signatures and Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) do not produce a positive validation result.</PolicyDescription>" +
                                        "<PolicyName>POLv4</PolicyName>" +
                                        "<PolicyUrl>http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv4</PolicyUrl>" +
                                    "</Policy>" +
                                    "<ValidationTime>2017-10-02T12:04:01Z</ValidationTime>" +
                                    "<ValidatedDocument>" +
                                        "<Filename>hellopades-pades-lt-sha256-sign.pdf</Filename>" +
                                        "<FileHashInHex>CAA597B767D182A512290AE23F20F23A3AD6B40C348BA5BDFDDF9568E025694B</FileHashInHex>" +
                                        "<HashAlgo>SHA-256</HashAlgo>" +
                                    "</ValidatedDocument>" +
                                    "<ValidationLevel>ARCHIVAL_DATA</ValidationLevel>" +
                                    "<ValidationWarnings/>" +
                                    "<Signatures>" +
                                        "<Signature>" +
                                            "<Id>id-65dc6b043effc2542519162d271ad4f9780e552845d04b66868301a5cf0ed8ba</Id>" +
                                            "<SignatureFormat>PAdES_BASELINE_LT</SignatureFormat>" +
                                            "<SignatureLevel>QESIG</SignatureLevel>" +
                                            "<SignedBy>SINIVEE,VEIKO,36706020210</SignedBy>" +
                                            "<Indication>TOTAL-PASSED</Indication>" +
                                            "<SubIndication/>" +
                                            "<Errors/>" +
                                            "<SignatureScopes>" +
                                                "<SignatureScope>" +
                                                    "<Name>PDF previous version #1</Name>" +
                                                    "<Scope>PdfByteRangeSignatureScope</Scope>" +
                                                    "<Content>The document byte range: [0, 14153, 52047, 491]</Content>" +
                                                "</SignatureScope>" +
                                            "</SignatureScopes>" +
                                            "<ClaimedSigningTime>2015-07-09T07:00:48Z</ClaimedSigningTime>" +
                                            "<Warnings/>" +
                                            "<Info>" +
                                                "<bestSignatureTime>2015-07-09T07:00:55Z</bestSignatureTime>" +
                                            "</Info>" +
                                        "</Signature>" +
                                    "</Signatures>" +
                                    "<ValidSignaturesCount>1</ValidSignaturesCount>" +
                                    "<SignaturesCount>1</SignaturesCount>" +
                                "</ns2:ValidationConclusion>" +
                            "</ns2:ValidationReport>" +
                       "</ns2:ValidateDocumentResponse>" +
                   "</soap:Body>" +
               "</soap:Envelope>";
    }

    private byte[] getRawSignatureMock() {
        return "test".getBytes();
    }

} 
