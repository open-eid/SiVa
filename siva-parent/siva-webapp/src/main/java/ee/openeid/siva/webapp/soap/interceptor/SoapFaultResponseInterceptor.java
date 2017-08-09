/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

import ee.openeid.siva.proxy.http.RESTValidationProxyRequestException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import eu.europa.esig.dss.DSSException;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.springframework.http.HttpStatus;

import javax.xml.bind.UnmarshalException;
import javax.xml.namespace.QName;

public class SoapFaultResponseInterceptor extends AbstractSoapInterceptor {

    private static final String DOCUMENT_FORMAT_NOT_RECOGNIZED = "Document format not recognized/handled";

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

        f.setStatusCode(200);
        if (isClientException(cause)) {
            f.setFaultCode(new QName("Client"));
        }
    }

    private boolean isClientException(Throwable t) {
        if (t instanceof MalformedDocumentException || t instanceof InvalidPolicyException || t instanceof UnmarshalException) {
            return true;
        } else if (t instanceof RESTValidationProxyRequestException) {
            RESTValidationProxyRequestException restProxyException = (RESTValidationProxyRequestException) t;
            return restProxyException.getHttpStatus() == HttpStatus.BAD_REQUEST;
        } else if (t instanceof DSSException) {
            return DOCUMENT_FORMAT_NOT_RECOGNIZED.equals(t.getMessage());
        }
        return false;
    }


}
