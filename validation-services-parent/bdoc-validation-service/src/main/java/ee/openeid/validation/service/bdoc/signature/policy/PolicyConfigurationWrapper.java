package ee.openeid.validation.service.bdoc.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.digidoc4j.Configuration;

@Getter @Setter
@AllArgsConstructor
public class PolicyConfigurationWrapper {
    private Configuration configuration;
    private ConstraintDefinedPolicy policy;
}
