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

import ee.openeid.siva.proxy.http.RESTValidationProxyException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.webapp.response.erroneus.RequestValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidationExceptionHandler {

    private MessageSource messageSource;

    @ExceptionHandler(MalformedDocumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RequestValidationError handleMalformedDocumentException(MalformedDocumentException e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError("document", getMessage("validation.error.message.document.malformed"));
        return requestValidationError;
    }

    @ExceptionHandler(ValidationServiceException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleValidationServiceException(ValidationServiceException e) {
        return getMessage("validation.service.error.message");
    }

    @ExceptionHandler(InvalidPolicyException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RequestValidationError handleInvalidPolicyException(InvalidPolicyException e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError("signaturePolicy", e.getMessage());
        return requestValidationError;
    }

    @ExceptionHandler(RESTValidationProxyException.class)
    public ResponseEntity<RequestValidationError> handleRESTValidationProxyException(RESTValidationProxyException e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError(e.getErrorKey(), e.getErrorMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(requestValidationError);
    }


    private String getMessage(String key) {
        return messageSource.getMessage(key, null, null);
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
