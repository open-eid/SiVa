/*
 * Copyright 2018 - 2024 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.proxy.ContainerValidationProxy;
import ee.openeid.siva.proxy.HashcodeValidationProxy;
import ee.openeid.siva.proxy.document.ProxyHashcodeDataSet;
import ee.openeid.siva.webapp.request.JSONHashcodeValidationRequest;
import ee.openeid.siva.webapp.request.JSONValidationRequest;
import ee.openeid.siva.webapp.response.ValidationResponse;
import ee.openeid.siva.webapp.transformer.HashcodeValidationRequestToProxyDocumentTransformer;
import ee.openeid.siva.webapp.transformer.ValidationRequestToProxyDocumentTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class ValidationController {

    private ContainerValidationProxy containerValidationProxy;
    private HashcodeValidationProxy hashcodeValidationProxy;
    private ValidationRequestToProxyDocumentTransformer transformer;
    private HashcodeValidationRequestToProxyDocumentTransformer hashRequestTransformer;

    @PostMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValidationResponse validate(@Valid @RequestBody JSONValidationRequest validationRequest) {
        return new ValidationResponse(containerValidationProxy.validate(transformer.transform(validationRequest)));
    }

    @PostMapping(value = "/validateHashcode", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValidationResponse validateHashcode(@Valid @RequestBody JSONHashcodeValidationRequest validationRequest) {
        ProxyHashcodeDataSet proxyDocument = hashRequestTransformer.transform(validationRequest);
        return new ValidationResponse(hashcodeValidationProxy.validate(proxyDocument));
    }

    @Autowired
    public void setContainerValidationProxy(ContainerValidationProxy containerValidationProxy) {
        this.containerValidationProxy = containerValidationProxy;
    }

    @Autowired
    public void setTransformer(ValidationRequestToProxyDocumentTransformer transformer) {
        this.transformer = transformer;
    }

    @Autowired
    public void setHashRequestTransformer(HashcodeValidationRequestToProxyDocumentTransformer hashRequestTransformer) {
        this.hashRequestTransformer = hashRequestTransformer;
    }

    @Autowired
    public void setHashcodeValidationProxy(HashcodeValidationProxy hashcodeValidationProxy) {
        this.hashcodeValidationProxy = hashcodeValidationProxy;
    }
}
