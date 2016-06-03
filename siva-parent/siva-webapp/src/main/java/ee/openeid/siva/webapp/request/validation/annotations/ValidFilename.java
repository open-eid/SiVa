package ee.openeid.siva.webapp.request.validation.annotations;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank
@NotNullValidFilenamePattern
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={})
public @interface ValidFilename {

    String message() default "{validation.error.message.filename}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
