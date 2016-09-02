package ee.openeid.siva.webapp.soap.interceptor;


import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

import javax.xml.namespace.QName;

public class SoapFaultResponseInterceptor extends AbstractSoapInterceptor {

    public SoapFaultResponseInterceptor() {
        super(Phase.SETUP);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        Fault f = (Fault) message.getContent(Exception.class);
        if (f == null) {
            return;
        }

        Throwable cause = f.getCause();
        if (cause == null) {
            return;
        }

        if (cause instanceof MalformedDocumentException || cause instanceof InvalidPolicyException) {
            f.setStatusCode(400);
            f.setFaultCode(new QName("Client"));
        }
    }
}
