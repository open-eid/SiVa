package ee.openeid.siva.webapp.soap.interceptor;

import ee.openeid.siva.proxy.http.RESTProxyError;
import ee.openeid.siva.proxy.http.RESTValidationProxyException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.common.i18n.Exception;
import org.apache.cxf.interceptor.Fault;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.xml.bind.UnmarshalException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SoapFaultResponseInterceptorTest {

    @Mock
    private SoapMessage message;

    private SoapFaultResponseInterceptor soapFaultResponseInterceptor;

    @Before
    public void setUp() {
        soapFaultResponseInterceptor = new SoapFaultResponseInterceptor();
    }

    @Test
    public void whenSoapFaultHasNoCauseThenFaultStatusAndCodeRemainUnchanged() {
        Fault fault = mock(Fault.class);
        doReturn(null).when(fault).getCause();
        doReturn(fault).when(message).getContent(any());
        soapFaultResponseInterceptor.handleMessage(message);
        verify(fault).getCause();
        verifyNoMoreInteractions(fault);
    }

    @Test
    public void whenSoapFaultIsCausedByMalformedDocumentExceptionThenFaultStatusAndCodeAreChanged() {
        Fault fault = new Fault(new MalformedDocumentException());
        doReturn(fault).when(message).getContent(any());
        soapFaultResponseInterceptor.handleMessage(message);
        assertTrue(fault.getStatusCode() == 200);
        assertEquals("Client", fault.getFaultCode().toString());
    }

    @Test
    public void whenSoapFaultIsCausedByInvalidPolicyExceptionThenFaultStatusAndCodeAreChanged() {
        Fault fault = new Fault(new InvalidPolicyException(""));
        doReturn(fault).when(message).getContent(any());
        soapFaultResponseInterceptor.handleMessage(message);
        assertTrue(fault.getStatusCode() == 200);
        assertEquals("Client", fault.getFaultCode().toString());
    }

    @Test
    public void whenSoapFaultIsCausedByUnmarshalExceptionThenFaultStatusAndCodeAreChanged() {
        Fault fault = new Fault(new UnmarshalException("Some message.."));
        doReturn(fault).when(message).getContent(any());
        soapFaultResponseInterceptor.handleMessage(message);
        assertTrue(fault.getStatusCode() == 200);
        assertEquals("Client", fault.getFaultCode().toString());
    }

    @Test
    public void whenSoapFaultIsCausedByRESTValidationProxyExceptionWithBadRequestStatusCodeThenFaultStatusAndCodeAreChanged() {
        RESTProxyError error = new RESTProxyError();
        error.setHttpStatus(HttpStatus.BAD_REQUEST);
        Fault fault = new Fault(new RESTValidationProxyException(error));
        doReturn(fault).when(message).getContent(any());
        soapFaultResponseInterceptor.handleMessage(message);
        assertTrue(fault.getStatusCode() == 200);
        assertEquals("Client", fault.getFaultCode().toString());
    }
}
