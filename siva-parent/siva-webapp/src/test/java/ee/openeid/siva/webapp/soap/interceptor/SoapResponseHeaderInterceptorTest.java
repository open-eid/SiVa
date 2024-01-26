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

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.message.Exchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoapResponseHeaderInterceptorTest {

    @Mock
    private SoapMessage responseMessage;

    @Mock
    private List<Header> requestSoapHeaders;

    private SoapResponseHeaderInterceptor soapResponseHeaderInterceptor = new SoapResponseHeaderInterceptor();

    @BeforeEach
    void setUp() {
        soapResponseHeaderInterceptor = new SoapResponseHeaderInterceptor();
    }

    @Test
    void whenSoapFaultHasNoCauseThenFaultStatusAndCodeRemainUnchanged() {
        SoapMessage requestMessage = mock(SoapMessage.class);
        Exchange exchange = mock(Exchange.class);

        doReturn(exchange).when(responseMessage).getExchange();
        doReturn(requestMessage).when(exchange).getInMessage();
        doReturn(requestSoapHeaders).when(requestMessage).getHeaders();

        soapResponseHeaderInterceptor.handleMessage(responseMessage);
        verify(responseMessage).put(Header.HEADER_LIST, requestSoapHeaders);
    }

}
