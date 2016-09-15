package ee.openeid.siva.validation.service.signature.policy.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SignaturePolicyProperties<T extends ValidationPolicy> {
    private String abstractDefaultPolicy;
    private List<T> abstractPolicies = new ArrayList<>();
}
