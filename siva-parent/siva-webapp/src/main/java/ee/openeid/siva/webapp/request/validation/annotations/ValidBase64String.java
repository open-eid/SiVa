package ee.openeid.siva.webapp.request.validation.annotations;

import ee.openeid.siva.webapp.request.validation.validators.ValidBase64ConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidBase64ConstraintValidator.class)
public @interface ValidBase64String {

    String message() default "document not encoded in base64";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
