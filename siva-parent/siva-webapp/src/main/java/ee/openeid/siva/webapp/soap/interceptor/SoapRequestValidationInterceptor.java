package ee.openeid.siva.webapp.soap.interceptor;


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
            if (soapMessage == null) {
                throw new SOAPException();
            }
            SOAPBody body = soapMessage.getSOAPPart().getEnvelope().getBody();

            validateDocumentElement(body);
            validateFilenameElement(body);
            validateSignaturePolicyElement(body);

        } catch (SOAPException e) {
            throwFault(messageSource.getMessage("validation.error.message.invalidRequest", null, null));
        }
    }

    private void validateDocumentElement(SOAPBody body) {
        String documentValue = getElementValueFromBody(body, "Document");
        if (StringUtils.isBlank(documentValue) || !Base64.isBase64(documentValue)) {
            throwFault(messageSource.getMessage("validation.error.message.base64", null, null));
        }
    }

    private void validateFilenameElement(SOAPBody body) {
        String filenameValue = getElementValueFromBody(body, "Filename");
        Pattern pattern = Pattern.compile(NotNullValidFilenamePattern.PATTERN);
        Matcher matcher = pattern.matcher(filenameValue);
        if (!matcher.matches()) {
            throwFault(messageSource.getMessage("validation.error.message.filename", null, null));
        }
    }

    private void validateSignaturePolicyElement(SOAPBody body) {
        String signaturePolicyValue = getElementValueFromBody(body, "SignaturePolicy");
        Pattern pattern = Pattern.compile(ValidSignaturePolicyPattern.PATTERN);
        Matcher matcher = pattern.matcher(signaturePolicyValue);
        if (!matcher.matches()) {
            throwFault(messageSource.getMessage("validation.error.message.signaturePolicy", null, null));
        }
    }

    private String getElementValueFromBody(SOAPBody body, String elementName) {
        Node elementNode = body.getElementsByTagName(elementName).item(0);
        elementNode = elementNode == null ? null : elementNode.getFirstChild();
        return elementNode == null ? "" : elementNode.getNodeValue();
    }

    private void throwFault(String message) {
        Fault fault = new Fault(new Exception(message));
        fault.setFaultCode(new QName("Client"));
        fault.setStatusCode(400);
        throw fault;
    }

}
