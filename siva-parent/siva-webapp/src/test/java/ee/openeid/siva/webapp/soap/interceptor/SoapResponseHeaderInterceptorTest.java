package ee.openeid.siva.webapp.soap.interceptor;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.message.Exchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SoapResponseHeaderInterceptorTest {

    @Mock
    private SoapMessage responseMessage;

    @Mock
    private List<Header> requestSoapHeaders;

    private SoapResponseHeaderInterceptor soapResponseHeaderInterceptor = new SoapResponseHeaderInterceptor();

    @Before
    public void setUp() {
        soapResponseHeaderInterceptor = new SoapResponseHeaderInterceptor();
    }

    @Test
    public void whenSoapFaultHasNoCauseThenFaultStatusAndCodeRemainUnchanged() {
        SoapMessage requestMessage = mock(SoapMessage.class);
        Exchange exchange = mock(Exchange.class);

        doReturn(exchange).when(responseMessage).getExchange();
        doReturn(requestMessage).when(exchange).getInMessage();
        doReturn(requestSoapHeaders).when(requestMessage).getHeaders();

        soapResponseHeaderInterceptor.handleMessage(responseMessage);
        verify(responseMessage).put(Header.HEADER_LIST, requestSoapHeaders);
    }

}
