package ee.openeid.siva.webapp.request.validation.annotations;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ee.openeid.siva.webapp.request.validation.annotations.ValidFilename.MESSAGE;
import static ee.openeid.siva.webapp.request.validation.annotations.ValidFilename.PATTERN;

@NotBlank
@Pattern(regexp = PATTERN, message = MESSAGE)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={})
public @interface ValidFilename {

    String MESSAGE = "{validation.error.message.filename}";
    String PATTERN = "^[^*&%/\"\\\\:?]+$";

    String message() default MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
