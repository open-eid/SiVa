package ee.openeid.siva.webapp.request.validation.json;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ee.openeid.siva.webapp.request.validation.json.ValidFilename.PATTERN;

@NotBlank
@Pattern(regexp = PATTERN)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={})
@ReportAsSingleViolation
public @interface ValidFilename {

    String PATTERN = "^[^*&%/\"\\\\:?]+$";

    String message() default "invalid filename";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
