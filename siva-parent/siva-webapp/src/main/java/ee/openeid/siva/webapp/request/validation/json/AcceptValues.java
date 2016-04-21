package ee.openeid.siva.webapp.request.validation.json;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=AcceptValuesConstraintValidator.class)
public @interface AcceptValues {

    AcceptedValue value();
    String message() default "invalid field value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
