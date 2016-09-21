package ee.openeid.siva.webapp.soap.interceptor;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

import java.util.List;

public class SoapResponseHeaderInterceptor extends AbstractSoapInterceptor {

    public SoapResponseHeaderInterceptor() {
        super(Phase.PRE_STREAM);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        SoapMessage requestMessage = (SoapMessage) message.getExchange().getInMessage();
        List<Header> headers = requestMessage.getHeaders();
        message.put(Header.HEADER_LIST, headers);
    }

}
