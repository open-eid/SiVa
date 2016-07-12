package ee.openeid.siva.validation.service.signature.policy;


import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicySettings;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SignaturePolicyServiceTest {

    private static final String POLICY_PREFIX = "policy-";

    private static final String VALID_CLASSPATH_CONSTRAINT = "valid-constraint.xml";
    private static final String INVALID_CLASSPATH_CONSTRAINT = "invalid-constraint.xml";

    private static final String VALID_ABSOLUTE_PATH_CONSTRAINT = getResourceAbsolutePath("/valid-constraint.xml");
    private static final String INVALID_ABSOLUTE_PATH_CONSTRAINT = getResourceAbsolutePath("/invalid-constraint.xml");

    private static final String NON_EXISITNG_CLASSPATH_CONSTRAINT = "non-existing-constraint.xml";
    private static final String NON_EXISITNG_ABSOLUTE_PATH_CONSTRAINT = "/non-existing-constraint.xml";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void whenSignaturePolicesDoNotContainDefaultPolicyThenThrowException() {
        expectedException.expect(PolicyPathNotFoundException.class);
//        expectedException.expectMessage("Default policy is not defined in signature abstractPolicies.");

        SignaturePolicyService signaturePolicyService = createSignaturePolicyService("RANDOM_PREFIX" + VALID_CLASSPATH_CONSTRAINT, VALID_CLASSPATH_CONSTRAINT);
        signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY_PREFIX + VALID_CLASSPATH_CONSTRAINT);
    }

    @Test
    public void settingValidPolicyFromClasspathResourceGetsLoaded() {
        SignaturePolicyService signaturePolicyService = createSignaturePolicyService(VALID_CLASSPATH_CONSTRAINT, VALID_CLASSPATH_CONSTRAINT);

        assertEquals(1, signaturePolicyService.getSignaturePolicies().size());
        assertNotNull(signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY_PREFIX + VALID_CLASSPATH_CONSTRAINT));
    }

    @Test
    public void settingInvalidPolicyFromClasspathResourceGetsNotLoaded() {
        expectedException.expect(InvalidPolicyException.class);

        SignaturePolicyService signaturePolicyService = createSignaturePolicyService(INVALID_CLASSPATH_CONSTRAINT, INVALID_CLASSPATH_CONSTRAINT);

        assertEquals(0, signaturePolicyService.getSignaturePolicies().size());
        signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY_PREFIX + INVALID_CLASSPATH_CONSTRAINT);
    }

    @Test
    public void settingValidPolicyFromAbsolutePathGetsLoaded() {
        SignaturePolicyService signaturePolicyService = createSignaturePolicyService(VALID_ABSOLUTE_PATH_CONSTRAINT, VALID_ABSOLUTE_PATH_CONSTRAINT);

        assertEquals(1, signaturePolicyService.getSignaturePolicies().size());
        assertNotNull(signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY_PREFIX + VALID_ABSOLUTE_PATH_CONSTRAINT));
    }

    @Test
    public void settingInValidPolicyFromAbsolutePathGetsNotLoaded() {
        expectedException.expect(InvalidPolicyException.class);

        SignaturePolicyService signaturePolicyService = createSignaturePolicyService(INVALID_ABSOLUTE_PATH_CONSTRAINT, INVALID_ABSOLUTE_PATH_CONSTRAINT);

        assertEquals(0, signaturePolicyService.getSignaturePolicies().size());
        signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY_PREFIX + INVALID_ABSOLUTE_PATH_CONSTRAINT);
    }

    @Test
    public void settingNonExistingPolicyFromClasspathGetsNotLoaded() {
        expectedException.expect(PolicyPathNotFoundException.class);

        SignaturePolicyService signaturePolicyService = createSignaturePolicyService(NON_EXISITNG_CLASSPATH_CONSTRAINT, NON_EXISITNG_CLASSPATH_CONSTRAINT);

        assertEquals(0, signaturePolicyService.getSignaturePolicies().size());
        signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY_PREFIX + NON_EXISITNG_CLASSPATH_CONSTRAINT);
    }

    @Test
    public void settingNonExistingPolicyFromAbsolutePathGetsNotLoaded() {
        expectedException.expect(PolicyPathNotFoundException.class);

        SignaturePolicyService signaturePolicyService = createSignaturePolicyService(NON_EXISITNG_ABSOLUTE_PATH_CONSTRAINT, NON_EXISITNG_ABSOLUTE_PATH_CONSTRAINT);

        assertEquals(0, signaturePolicyService.getSignaturePolicies().size());
        signaturePolicyService.getPolicyDataStreamFromPolicy(NON_EXISITNG_ABSOLUTE_PATH_CONSTRAINT);
    }

    @Test
    public void settingMultipleValidPoliciesResultsInAllGetLoaded() throws IOException {
        SignaturePolicyService signaturePolicyService = createSignaturePolicyService(VALID_CLASSPATH_CONSTRAINT, VALID_CLASSPATH_CONSTRAINT, VALID_ABSOLUTE_PATH_CONSTRAINT);

        assertEquals(2, signaturePolicyService.getSignaturePolicies().size());
        assertNotNull(signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY_PREFIX + VALID_CLASSPATH_CONSTRAINT));
        assertNotNull(signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY_PREFIX + VALID_ABSOLUTE_PATH_CONSTRAINT));
    }

    @Test
    public void settingValidAndInvalidPoliciesResultsInOneGetsLoaded() {
        expectedException.expect(InvalidPolicyException.class);

        SignaturePolicyService signaturePolicyService = createSignaturePolicyService(VALID_CLASSPATH_CONSTRAINT, VALID_CLASSPATH_CONSTRAINT, INVALID_CLASSPATH_CONSTRAINT);

        assertEquals(1, signaturePolicyService.getSignaturePolicies().size());
        assertNotNull(signaturePolicyService.getPolicyDataStreamFromPolicy(VALID_CLASSPATH_CONSTRAINT));
        signaturePolicyService.getPolicyDataStreamFromPolicy(INVALID_CLASSPATH_CONSTRAINT);
    }

    private SignaturePolicyService createSignaturePolicyService(String defaultPolicy, String... policyPaths) {
        Map<String, String> policies = new HashMap<>();
        for (String policyPath : policyPaths) {
            policies.put(POLICY_PREFIX + policyPath, policyPath);

        }
        SignaturePolicySettings signaturePolicySettings = new SignaturePolicySettings();
        signaturePolicySettings.setAbstractPolicies(policies);
        signaturePolicySettings.setAbstractDefaultPolicy(defaultPolicy);

        return new SignaturePolicyServiceImpl(signaturePolicySettings);
    }

    private static String getResourceAbsolutePath(String resourceRelativePath) {
        try {
            URL resource = SignaturePolicyServiceTest.class.getResource(resourceRelativePath);
            return Paths.get(resource.toURI()).toAbsolutePath().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private class SignaturePolicyServiceImpl extends SignaturePolicyService {
        public SignaturePolicyServiceImpl(SignaturePolicySettings signaturePolicySettings) {
            super(signaturePolicySettings);
        }
    }
}
