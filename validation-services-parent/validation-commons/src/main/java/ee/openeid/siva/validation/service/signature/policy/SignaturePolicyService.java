package ee.openeid.siva.validation.service.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class SignaturePolicyService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SignaturePolicyService.class);

    protected byte[] defaultPolicy;
    protected Map<String, byte[]> signaturePolicies = new HashMap<>();

    public SignaturePolicyService(SignaturePolicyProperties signaturePolicyProperties) {
        LOGGER.info("Loading signature abstractPolicies for: " + signaturePolicyProperties.getClass().getSimpleName());
        loadSignaturePolicies(signaturePolicyProperties);
    }

    public InputStream getPolicyDataStreamFromPolicy(String policy) {
        byte[] policyData = StringUtils.isEmpty(policy) ? defaultPolicy : signaturePolicies.get(policy);
        if (policyData == null) {
            throw new InvalidPolicyException("Invalid signature policy: " + policy + "; Available abstractPolicies: " + signaturePolicies.keySet() );
        }

        return new ByteArrayInputStream(policyData);
    }

    protected byte[] getContentFromPolicyPath(String policyPath) throws IOException {
        InputStream policyDataStream = null;
        if (new File(policyPath).isAbsolute()) {
            LOGGER.info("Reading policy from absolute path: " + policyPath);
            try {
                policyDataStream = new FileInputStream(new File(policyPath));
            } catch (FileNotFoundException e) {
                LOGGER.warn(e.getMessage());
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

    private void loadSignaturePolicies(SignaturePolicyProperties signaturePolicyProperties) {
        signaturePolicyProperties.getAbstractPolicies().forEach((policy, policyPath) -> {
            LOGGER.info("Loading policy: " + policy);
            try {
                byte[] policyData = getContentFromPolicyPath(policyPath);
                InputStream policyDataStream = new ByteArrayInputStream(policyData);
                validateAgainstSchema(policyDataStream);
                signaturePolicies.put(policy, policyData);
                LOGGER.info("Policy: " + policy + " loaded successfully");
            } catch (Exception e) {
                LOGGER.error("Could not load policy " + policy + " due to: " + e);
            }
        });

        defaultPolicyDataLoading(signaturePolicyProperties);
    }

    private void defaultPolicyDataLoading(SignaturePolicyProperties signaturePolicyProperties) {
        try {
            defaultPolicy = getContentFromPolicyPath(signaturePolicyProperties.getAbstractDefaultPolicy());
        } catch (IOException e) {
            LOGGER.warn("Failed to load default policy data from file: {} with message: {}",
                    signaturePolicyProperties.getAbstractDefaultPolicy(),
                    e.getMessage(), e);

            throw new InvalidPolicyException("Default policy is not defined in signature abstractPolicies.");
        }
    }

    private void validateAgainstSchema(InputStream policyDataStream) {
        PolicySchemaValidator.validate(policyDataStream);
    }

}
