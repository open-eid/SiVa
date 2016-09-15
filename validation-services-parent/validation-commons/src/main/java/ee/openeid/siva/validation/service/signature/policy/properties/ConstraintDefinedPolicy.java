package ee.openeid.siva.validation.service.signature.policy.properties;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Getter @Setter
@NoArgsConstructor
@ToString(exclude = "constraintData")
public class ConstraintDefinedPolicy extends ValidationPolicy {
    private String constraintPath;
    private byte[] constraintData;

    public ConstraintDefinedPolicy(ValidationPolicy validationPolicy) {
        setName(validationPolicy.getName());
        setDescription(validationPolicy.getDescription());
        setUrl(validationPolicy.getUrl());
    }

    public InputStream getConstraintDataStream() {
        return new ByteArrayInputStream(constraintData);
    }
}
