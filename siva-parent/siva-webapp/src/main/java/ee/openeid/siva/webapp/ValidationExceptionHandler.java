/*
 * Copyright 2018 - 2025 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.validation.exception.DocumentRequirementsException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.MalformedSignatureFileException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.webapp.request.limitation.RequestSizeLimitExceededException;
import ee.openeid.siva.webapp.response.erroneus.RequestValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.MimeType;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ValidationExceptionHandler {

    private MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RequestValidationError invalidRequest(MethodArgumentNotValidException exception) {
        RequestValidationError requestValidationError = new RequestValidationError();
        BindingResult bindingResult = exception.getBindingResult();
        bindingResult.getFieldErrors().forEach(br -> requestValidationError.addFieldError(br.getField(), br.getDefaultMessage()));
        return requestValidationError;
    }

    @ExceptionHandler(MalformedDocumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RequestValidationError handleMalformedDocumentException(MalformedDocumentException e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError("document", getMessage("validation.error.message.document.malformed"));
        return requestValidationError;
    }

    @ExceptionHandler(MalformedSignatureFileException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RequestValidationError handleMalformedSignatureFileException(MalformedSignatureFileException e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError("signatureFiles.signature", getMessage("validation.error.message.signatureFile.malformed"));
        return requestValidationError;
    }

    @ExceptionHandler(DocumentRequirementsException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RequestValidationError handleDocumentRequirementsException(DocumentRequirementsException e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError("document", getMessage("validation.error.message.document.requirements"));
        return requestValidationError;
    }

    @ExceptionHandler(ValidationServiceException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public RequestValidationError handleValidationServiceException(ValidationServiceException e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError("document", getMessage("validation.service.error.message"));
        return requestValidationError;
    }

    @ExceptionHandler(InvalidPolicyException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RequestValidationError handleInvalidPolicyException(InvalidPolicyException e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError("signaturePolicy", e.getMessage());
        return requestValidationError;
    }

    @ExceptionHandler(RequestSizeLimitExceededException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RequestValidationError handleRequestSizeLimitExceededException(RequestSizeLimitExceededException e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError("request", e.getMessage());
        return requestValidationError;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public RequestValidationError handleNoHandlerFoundException(NoHandlerFoundException e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError("endpointNotFound", "Endpoint not found");
        return requestValidationError;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    public RequestValidationError handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError("methodNotAllowed", "Only the following request methods are supported: "
                + asCommaSeparatedList(e.getSupportedHttpMethods(), HttpMethod::name));
        return requestValidationError;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RequestValidationError handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError("requestBodyNotReadable", "Request body is malformed and cannot be read");
        return requestValidationError;
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public RequestValidationError handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError("contentTypeNotSupported", "Only the following content types are supported: "
                + asCommaSeparatedList(e.getSupportedMediaTypes(), MimeType::toString));
        return requestValidationError;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public RequestValidationError handleAllOtherExceptions(Exception e) {
        RequestValidationError requestValidationError = new RequestValidationError();
        requestValidationError.addFieldError("unexpectedError", "An unexpected error has occurred");
        log.error("Unexpected error: {}", e.getMessage());
        return requestValidationError;
    }


    private String getMessage(String key) {
        return messageSource.getMessage(key, null, null);
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private static <T> String asCommaSeparatedList(Collection<T> elements, Function<T, String> mapper) {
        List<String> mappedElements = Optional
                .ofNullable(elements).stream().flatMap(Collection::stream)
                .map(mapper)
                .collect(Collectors.toList());

        return String.join(", ", mappedElements);
    }

}
