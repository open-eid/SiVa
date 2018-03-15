/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.webapp;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.webapp.request.JSONValidationRequest;
import ee.openeid.siva.webapp.request.ValidateHashCodePayload;
import ee.openeid.siva.webapp.response.ValidationResponse;
import ee.openeid.siva.webapp.transformer.ValidationRequestToProxyDocumentTransformer;

@RestController
public class ValidationController {

    private ValidationProxy validationProxy;
    private ValidationRequestToProxyDocumentTransformer transformer;

    @RequestMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ValidationResponse validate(@Valid @RequestBody JSONValidationRequest validationRequest) {
        return new ValidationResponse(this.validationProxy.validate(this.transformer.transform(validationRequest)));
    }

    @RequestMapping(value = "/validateDigest", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ValidationResponse validateHashCode(@Valid @RequestBody ValidateHashCodePayload payload) {
        return new ValidationResponse(this.validationProxy.validate(this.toProxyDocument(payload), payload.toDocumentList(),
            payload.toSignatureDocumentList(), payload.toTimeStampTokenList()));
    }

    @Autowired
    public void setValidationProxy(ValidationProxy validationProxy) {
        this.validationProxy = validationProxy;
    }

    @Autowired
    public void setTransformer(ValidationRequestToProxyDocumentTransformer transformer) {
        this.transformer = transformer;
    }

    private ProxyDocument toProxyDocument(ValidateHashCodePayload payload) {
        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setName(payload.getContainerFileName());
        proxyDocument.setBytes(new byte[0]);
        if (payload.getReportType() != null) {
            proxyDocument.setReportType(ReportType.valueOf(payload.getReportType()));
        } else {
            proxyDocument.setReportType(ReportType.SIMPLE);
        }
        proxyDocument.setSignaturePolicy(payload.getSignaturePolicy());
        return proxyDocument;
    }

}
