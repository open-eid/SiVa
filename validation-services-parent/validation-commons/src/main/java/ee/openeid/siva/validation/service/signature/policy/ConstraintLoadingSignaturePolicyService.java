package ee.openeid.siva.validation.service.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ConstraintLoadingSignaturePolicyService extends SignaturePolicyService<ConstraintDefinedPolicy> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintLoadingSignaturePolicyService.class);

    public ConstraintLoadingSignaturePolicyService(SignaturePolicyProperties<ConstraintDefinedPolicy> signaturePolicyProperties) {
        super(signaturePolicyProperties);
    }

    @Override
    void loadSignaturePolicies(SignaturePolicyProperties<ConstraintDefinedPolicy> signaturePolicyProperties) {
        signaturePolicyProperties.getAbstractPolicies().forEach(policy -> {
            LOGGER.info("Loading policy: " + policy);
            try {
                byte[] policyData = getContentFromPolicyPath(policy.getConstraintPath());
                InputStream policyDataStream = new ByteArrayInputStream(policyData);
                validateAgainstSchema(policyDataStream);
                policy.setConstraintData(policyData);
                ConstraintDefinedPolicy existingPolicyData = getSignaturePolicies().putIfAbsent(policy.getName(), policy);
                if (existingPolicyData == null) {
                    LOGGER.info("Policy: " + policy + " loaded successfully");
                } else {
                    LOGGER.error("Policy: " + policy + " was not loaded as it already exists");
                }
            } catch (Exception e) {
                LOGGER.error("Could not load policy " + policy + " due to: " + e);
            }
        });
        loadDefaultPolicy(signaturePolicyProperties);
    }

    private void validateAgainstSchema(InputStream policyDataStream) {
        PolicySchemaValidator.validate(policyDataStream);
    }

    private byte[] getContentFromPolicyPath(String policyPath) throws IOException {
        InputStream policyDataStream = null;
        if (new File(policyPath).isAbsolute()) {
            LOGGER.info("Reading policy from absolute path: " + policyPath);
            try {
                policyDataStream = new FileInputStream(new File(policyPath));
            } catch (FileNotFoundException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        } else {
            LOGGER.info("Reading policy from classpath: " + policyPath);
            policyDataStream = getClass().getClassLoader().getResourceAsStream(policyPath);
        }

        if (policyDataStream == null) {
            throw new PolicyPathNotFoundException("Could not find: " + policyPath);
        }

        return IOUtils.toByteArray(policyDataStream);
    }
}
