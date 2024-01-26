/*
 * Copyright 2017 - 2024 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.validation.exception.DocumentRequirementsException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.bind.UnmarshalException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoapFaultResponseInterceptorTest {

    @Mock
    private SoapMessage message;

    private SoapFaultResponseInterceptor soapFaultResponseInterceptor;

    @BeforeEach
    public void setUp() {
        soapFaultResponseInterceptor = new SoapFaultResponseInterceptor();
    }

    @Test
    void whenSoapFaultHasNoCauseThenFaultStatusAndCodeRemainUnchanged() {
        Fault fault = mock(Fault.class);
        doReturn(null).when(fault).getCause();
        doReturn(fault).when(message).getContent(any());
        soapFaultResponseInterceptor.handleMessage(message);
        verify(fault).getCause();
        verifyNoMoreInteractions(fault);
    }

    @Test
    void whenSoapFaultIsCausedByMalformedDocumentExceptionThenFaultStatusAndCodeAreChanged() {
        Fault fault = new Fault(new MalformedDocumentException());
        doReturn(fault).when(message).getContent(any());
        soapFaultResponseInterceptor.handleMessage(message);
        assertEquals(200, fault.getStatusCode());
        assertEquals("Client", fault.getFaultCode().toString());
    }

    @Test
    void whenSoapFaultIsCausedByDocumentRequirementsExceptionThenFaultStatusAndCodeAreChanged() {
        Fault fault = new Fault(new DocumentRequirementsException());
        doReturn(fault).when(message).getContent(any());
        soapFaultResponseInterceptor.handleMessage(message);
        assertEquals(200, fault.getStatusCode());
        assertEquals("Client", fault.getFaultCode().toString());
    }

    @Test
    void whenSoapFaultIsCausedByInvalidPolicyExceptionThenFaultStatusAndCodeAreChanged() {
        Fault fault = new Fault(new InvalidPolicyException(""));
        doReturn(fault).when(message).getContent(any());
        soapFaultResponseInterceptor.handleMessage(message);
        assertEquals(200, fault.getStatusCode());
        assertEquals("Client", fault.getFaultCode().toString());
    }

    @Test
    void whenSoapFaultIsCausedByUnmarshalExceptionThenFaultStatusAndCodeAreChanged() {
        Fault fault = new Fault(new UnmarshalException("Some message.."));
        doReturn(fault).when(message).getContent(any());
        soapFaultResponseInterceptor.handleMessage(message);
        assertEquals(200, fault.getStatusCode());
        assertEquals("Client", fault.getFaultCode().toString());
    }
}
