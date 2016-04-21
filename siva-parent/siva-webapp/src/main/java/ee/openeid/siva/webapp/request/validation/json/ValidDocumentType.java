package ee.openeid.siva.webapp.request.validation.json;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank
@AcceptValues(AcceptedValue.DOCUMENT)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={})
@ReportAsSingleViolation
public @interface ValidDocumentType {

    String message() default "invalid documentType";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
