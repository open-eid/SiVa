package ee.openeid.siva.xroad;

import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class XROADValidationExceptionHandler {

    private MessageSource messageSource;

    @ExceptionHandler(MalformedDocumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErroneousResponse handleMalformedDocumentException(MalformedDocumentException e) {
        return new ErroneousResponse("document", messageSource.getMessage("error.document.malformed", null, null));
    }

    @ExceptionHandler(InvalidPolicyException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErroneousResponse handleInvalidPolicyException(InvalidPolicyException e) {
        return new ErroneousResponse("signaturePolicy", e.getMessage());
    }

    @ExceptionHandler(ValidationServiceException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErroneousResponse handleValidationServiceException(ValidationServiceException e) {
        return new ErroneousResponse(null, messageSource.getMessage("validation.service.error.message", null, null));
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
