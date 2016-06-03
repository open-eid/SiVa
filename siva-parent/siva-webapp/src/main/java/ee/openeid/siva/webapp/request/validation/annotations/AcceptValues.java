package ee.openeid.siva.webapp.request.validation.annotations;

import ee.openeid.siva.webapp.request.validation.AcceptedValue;
import ee.openeid.siva.webapp.request.validation.validators.AcceptValuesConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=AcceptValuesConstraintValidator.class)
public @interface AcceptValues {

    AcceptedValue value();
    String message() default "{validation.error.message.general}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
