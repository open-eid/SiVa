package ee.openeid.siva.validation.service.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicySettings;
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

    protected static final Logger log = LoggerFactory.getLogger(SignaturePolicyService.class);

    protected String defaultPolicy;

    protected Map<String, byte[]> signaturePolicies = new HashMap<>();

    public SignaturePolicyService(SignaturePolicySettings signaturePolicySettings) {
        log.info("Loading signature policies for: " + signaturePolicySettings.getClass().getSimpleName());
        loadSignaturePolicies(signaturePolicySettings);
    }

    public InputStream getPolicyDataStreamFromPolicy(String policy) {
        if (StringUtils.isEmpty(policy)) {
            policy = defaultPolicy;
        }
        byte[] policyData = signaturePolicies.get(policy);
        if (policyData == null) {
            throw new InvalidPolicyException("Invalid signature policy: " + policy + "; Available policies: " + signaturePolicies.keySet() );
        }
        return new ByteArrayInputStream(policyData);
    }

    protected void loadSignaturePolicies(SignaturePolicySettings signaturePolicySettings) {
        signaturePolicySettings.getPolicies().forEach((policy, policyPath) -> {
            log.info("Validating policy: " + policy);
            try {
                byte[] policyData = getContentFromPolicyPath(policyPath);
                InputStream policyDataStream = new ByteArrayInputStream(policyData);
                validateAgainstSchema(policyDataStream);
                signaturePolicies.put(policy, policyData);
                log.info("Policy: " + policy + " loaded successfully");
            } catch (Exception e) {
                log.error("Could not load policy " + policy + " due to: " + e);
            }
        });

        if (!signaturePolicies.containsKey(signaturePolicySettings.getDefaultPolicy())) {
            throw new InvalidPolicyException("Default policy is not defined in signature policies.");
        }
        this.defaultPolicy = signaturePolicySettings.getDefaultPolicy();
    }

    protected byte[] getContentFromPolicyPath(String policyPath) throws IOException {
        InputStream policyDataStream = null;
        if (new File(policyPath).isAbsolute()) {
            log.info("Reading policy from absolute path: " + policyPath);
            try {
                policyDataStream = new FileInputStream(new File(policyPath));
            } catch (FileNotFoundException e) {
                log.warn(e.getMessage());
            }
        } else {
            log.info("Reading policy from classpath: " + policyPath);
            policyDataStream = getClass().getClassLoader().getResourceAsStream(policyPath);
        }
        if (policyDataStream == null) {
            throw new PolicyPathNotFoundException("Could not find: " + policyPath);
        }
        return IOUtils.toByteArray(policyDataStream);
    }

    protected void validateAgainstSchema(InputStream policyDataStream) {
        PolicySchemaValidator.validate(policyDataStream);
    }

}
