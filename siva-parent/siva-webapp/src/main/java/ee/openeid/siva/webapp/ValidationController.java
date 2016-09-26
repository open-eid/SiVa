/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.webapp;

import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.webapp.request.JSONValidationRequest;
import ee.openeid.siva.webapp.response.erroneus.RequestValidationError;
import ee.openeid.siva.webapp.transformer.ValidationRequestToProxyDocumentTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class ValidationController {

    private ValidationProxy validationProxy;
    private ValidationRequestToProxyDocumentTransformer transformer;

    @RequestMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public QualifiedReport validate(@Valid @RequestBody JSONValidationRequest validationRequest) {
        return validationProxy.validate(transformer.transform(validationRequest));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RequestValidationError invalidRequest(MethodArgumentNotValidException exception) {
        RequestValidationError requestValidationError = new RequestValidationError();
        BindingResult bindingResult = exception.getBindingResult();
        bindingResult.getFieldErrors().forEach(br -> requestValidationError.addFieldError(br.getField(), br.getDefaultMessage()));
        return requestValidationError;
    }

    @Autowired
    public void setValidationProxy(ValidationProxy validationProxy) {
        this.validationProxy = validationProxy;
    }

    @Autowired
    public void setTransformer(ValidationRequestToProxyDocumentTransformer transformer) {
        this.transformer = transformer;
    }

}
