package ee.openeid.siva.webapp.soap.interceptor;

import ee.openeid.siva.singature.XadesSignatureService;
import ee.openeid.siva.singature.SignatureService;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Alters the SOAP response by creating a signature from the existing response's SOAP body and adding the signature
 * into the SOAP body. {@link javax.xml.soap.SOAPMessage} is used in order to achieve the described behaviour.
 * This means that {@link org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor SAAJOutInterceptor} needs to be added into the outbound interceptor chain
 * otherwise {@link javax.xml.soap.SOAPMessage} never will be created
 */
public class ReportSignatureInterceptor extends AbstractSoapInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportSignatureInterceptor.class);

    private SignatureService signatureService = new XadesSignatureService();

    public ReportSignatureInterceptor() {
        super(Phase.POST_PROTOCOL);
    }

    @Override
    public void handleMessage(SoapMessage message) {
        SOAPMessage soapMessage = message.getContent(SOAPMessage.class);
        try {
            if (soapMessage != null) {
                SOAPBody soapBody = soapMessage.getSOAPPart().getEnvelope().getBody();
                byte[] validationReportBytes = getValidationReportContent(soapBody);
                byte[] validationReportSignature = signatureService.getSignature(validationReportBytes);
                addValidationReportSignature(soapBody, validationReportSignature);
            }
        } catch (Exception e) {
            LOGGER.error("Error producing report singature", e);
        }
    }

    private byte[] getValidationReportContent(SOAPBody soapBody) throws IOException, SOAPException, TransformerException {
        Node validationReportNode = soapBody.getFirstChild().getFirstChild();
        DOMSource source = new DOMSource(validationReportNode);
        StringWriter stringWriter = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(source, new StreamResult(stringWriter));
        String soapBodyString = stringWriter.toString();
        return soapBodyString.getBytes();
    }

    private void addValidationReportSignature(SOAPBody soapBody, byte[] validationReportSignature) throws SOAPException {
        SOAPElement validationResponseElement = (SOAPElement) soapBody.getFirstChild();
        validationResponseElement.addChildElement("ValidationReportSignature").setTextContent(Base64.encodeBase64String(validationReportSignature));
    }

}
