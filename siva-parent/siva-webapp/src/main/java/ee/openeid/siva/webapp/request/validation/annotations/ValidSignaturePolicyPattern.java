package ee.openeid.siva.webapp.request.validation.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ee.openeid.siva.webapp.request.validation.annotations.ValidSignaturePolicyPattern.PATTERN;


@Pattern(regexp = PATTERN)
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={})
@ReportAsSingleViolation
public @interface ValidSignaturePolicyPattern {

    String PATTERN = "^[A-Za-z0-9_]*$";

    String message() default "{validation.error.message.signaturePolicy}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
