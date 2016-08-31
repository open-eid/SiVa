package ee.openeid.siva.webapp.soap;


import ee.openeid.siva.webapp.request.validation.annotations.NotNullValidFilenamePattern;
import ee.openeid.siva.webapp.request.validation.annotations.ValidSignaturePolicyPattern;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.w3c.dom.*;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoapRequestValidationInterceptor extends AbstractSoapInterceptor {

    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

    private SAAJInInterceptor saajIn = new SAAJInInterceptor();

    public SoapRequestValidationInterceptor() {
        super(Phase.POST_PROTOCOL);
        messageSource.setBasename("ValidationMessages");
    }

    @Override
    public void handleMessage(SoapMessage message){
        SOAPMessage soapMessage = message.getContent(SOAPMessage.class);
        if (soapMessage == null) {
            saajIn.handleMessage(message);
            soapMessage = message.getContent(SOAPMessage.class);
        }
        try {
            SOAPBody body = soapMessage.getSOAPPart().getEnvelope().getBody();

            validateDocumentElement(body);
            validateFilenameElement(body);
            validateSignaturePolicyElement(body);

        } catch (SOAPException e) {
            throwFault(messageSource.getMessage("Invalid request", null, null));
        }
    }

    private void validateDocumentElement(SOAPBody body) {
        Node documentNode = body.getElementsByTagName("Document").item(0);
        documentNode = documentNode == null ? null : documentNode.getFirstChild();
        String documentValue = documentNode == null ? "" : documentNode.getNodeValue();
        if (StringUtils.isBlank(documentValue) || !Base64.isBase64(documentValue)) {
            throwFault(messageSource.getMessage("validation.error.message.base64", null, null));
        }
    }

    private void validateFilenameElement(SOAPBody body) {
        Node filenameNode = body.getElementsByTagName("Filename").item(0);
        filenameNode = filenameNode == null ? null : filenameNode.getFirstChild();
        String filenameValue = filenameNode == null ? "" : filenameNode.getNodeValue();
        Pattern pattern = Pattern.compile(NotNullValidFilenamePattern.PATTERN);
        Matcher matcher = pattern.matcher(filenameValue);
        if (!matcher.matches()) {
            throwFault(messageSource.getMessage("validation.error.message.filename", null, null));
        }
    }

    private void validateSignaturePolicyElement(SOAPBody body) {
        Node signaturePolicyNode = body.getElementsByTagName("SignaturePolicy").item(0);
        signaturePolicyNode = signaturePolicyNode == null ? null : signaturePolicyNode.getFirstChild();
        String signaturePolicyValue = signaturePolicyNode == null ? "" : signaturePolicyNode.getNodeValue();
        Pattern pattern = Pattern.compile(ValidSignaturePolicyPattern.PATTERN);
        Matcher matcher = pattern.matcher(signaturePolicyValue);
        if (!matcher.matches()) {
            throwFault(messageSource.getMessage("validation.error.message.signaturePolicy", null, null));
        }
    }

    private void throwFault(String message) {
        Fault fault = new Fault(new Exception(message));
        fault.setFaultCode(new QName("Client"));
        fault.setStatusCode(400);
        throw fault;
    }

}
