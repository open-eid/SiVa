package ee.openeid.siva.webapp.request.validation.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@ValidSignaturePolicyPattern
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={})
public @interface ValidSignaturePolicy {

    String message() default "{validation.error.message.signaturePolicy}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
