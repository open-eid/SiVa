package ee.openeid.siva.webapp.soap.interceptor;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.SoapInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public abstract class AbstractRequestValidationInterceptor extends AbstractSoapInterceptor {

    private static final int ERROR_CODE = 400;

    final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    private final SoapInterceptor saajIn = new SAAJInInterceptor();

    public AbstractRequestValidationInterceptor() {
        super(Phase.POST_PROTOCOL);
        messageSource.setBasename("ValidationMessages");
    }

    @Override
    public void handleMessage(SoapMessage message) {
        saajIn.handleMessage(message);
        SOAPMessage soapMessage = message.getContent(SOAPMessage.class);
        try {
            if (soapMessage == null) {
                throw new SOAPException();
            }
            SOAPBody body = soapMessage.getSOAPPart().getEnvelope().getBody();

            validateRequestBody(body);
        } catch (SOAPException e) {
            throwFault(messageSource.getMessage("validation.error.message.invalidRequest", null, null));
        }
    }

    abstract void validateRequestBody(SOAPBody body);

    String getElementValueFromBody(SOAPBody body, String elementName, String parentNodeName) {
        Node elementNode = body.getElementsByTagName(elementName).item(0);
        if (elementNode == null || !elementNode.getParentNode().getLocalName().equals(parentNodeName)) {
            return null;
        }
        return elementNode.getNodeValue() == null ? elementNode.getTextContent() : elementNode.getNodeValue();
    }

    String getElementValueFromBody(SOAPBody body, String elementName) {
        Node elementNode = body.getElementsByTagName(elementName).item(0);
        if (elementNode == null) {
            return null;
        }
        return elementNode.getNodeValue() == null ? elementNode.getTextContent() : elementNode.getNodeValue();
    }

    void changeElementValue(Node elementNode, String newValue) {
        if (elementNode.getNodeValue() == null) {
            elementNode.setTextContent(newValue);
        } else {
            elementNode.setNodeValue(newValue);
        }
    }

    String errorMessage(String errorMessageReference) {
        return messageSource.getMessage(errorMessageReference, null, null);
    }

    void throwFault(String message) {
        Fault fault = new Fault(new Exception(message));
        fault.setFaultCode(new QName("Client"));
        fault.setStatusCode(ERROR_CODE);
        throw fault;
    }
}
