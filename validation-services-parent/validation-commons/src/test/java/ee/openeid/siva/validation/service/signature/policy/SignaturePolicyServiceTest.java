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

    private static final String POLICY = "EE";

    private static final String VALID_CLASSPATH_CONSTRAINT = "valid-constraint.xml";
    private static final String INVALID_CLASSPATH_CONSTRAINT = "invalid-constraint.xml";

    private static final String VALID_ABSOLUTE_PATH_CONSTRAINT = getResourceAbsolutePath("/valid-constraint.xml");
    private static final String INVALID_ABSOLUTE_PATH_CONSTRAINT = getResourceAbsolutePath("/invalid-constraint.xml");

    private static final String NON_EXISITNG_CLASSPATH_CONSTRAINT = "non-existing-constraint.xml";
    private static final String NON_EXISITNG_ABSOLUTE_PATH_CONSTRAINT = "/non-existing-constraint.xml";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void settingValidPolicyFromClasspathResourceGetsLoaded() {
        SignaturePolicyService signaturePolicyService = createSignaturePolicyServiceFromPolicyPaths(VALID_CLASSPATH_CONSTRAINT);

        assertEquals(1, signaturePolicyService.getSignaturePolicies().size());
        assertNotNull(signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY + "1"));
    }

    @Test
    public void settingInvalidPolicyFromClasspathResourceGetsNotLoaded() {
        expectedException.expect(InvalidPolicyException.class);

        SignaturePolicyService signaturePolicyService = createSignaturePolicyServiceFromPolicyPaths(INVALID_CLASSPATH_CONSTRAINT);

        assertEquals(0, signaturePolicyService.getSignaturePolicies().size());
        signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY + "1");
    }

    @Test
    public void settingValidPolicyFromAbsolutePathGetsLoaded() {
        SignaturePolicyService signaturePolicyService = createSignaturePolicyServiceFromPolicyPaths(VALID_ABSOLUTE_PATH_CONSTRAINT);

        assertEquals(1, signaturePolicyService.getSignaturePolicies().size());
        assertNotNull(signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY + "1"));
    }

    @Test
    public void settingInValidPolicyFromAbsolutePathGetsNotLoaded() {
        expectedException.expect(InvalidPolicyException.class);

        SignaturePolicyService signaturePolicyService = createSignaturePolicyServiceFromPolicyPaths(INVALID_ABSOLUTE_PATH_CONSTRAINT);

        assertEquals(0, signaturePolicyService.getSignaturePolicies().size());
        signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY + "1");
    }

    @Test
    public void settingNonExistingPolicyFromClasspathGetsNotLoaded() {
        expectedException.expect(InvalidPolicyException.class);

        SignaturePolicyService signaturePolicyService = createSignaturePolicyServiceFromPolicyPaths(NON_EXISITNG_CLASSPATH_CONSTRAINT);

        assertEquals(0, signaturePolicyService.getSignaturePolicies().size());
        signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY + "1");
    }

    @Test
    public void settingNonExistingPolicyFromAbsolutePathGetsNotLoaded() {
        expectedException.expect(InvalidPolicyException.class);

        SignaturePolicyService signaturePolicyService = createSignaturePolicyServiceFromPolicyPaths(NON_EXISITNG_ABSOLUTE_PATH_CONSTRAINT);

        assertEquals(0, signaturePolicyService.getSignaturePolicies().size());
        signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY + "1");
    }

    @Test
    public void settingMultipleValidPoliciesResultsInAllGetLoaded() throws IOException {
        SignaturePolicyService signaturePolicyService = createSignaturePolicyServiceFromPolicyPaths(VALID_CLASSPATH_CONSTRAINT, VALID_ABSOLUTE_PATH_CONSTRAINT);

        assertEquals(2, signaturePolicyService.getSignaturePolicies().size());
        assertNotNull(signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY + "1"));
        assertNotNull(signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY + "2"));
    }

    @Test
    public void settingValidAndInvalidPoliciesResultsInOneGetsLoaded() {
        expectedException.expect(InvalidPolicyException.class);

        SignaturePolicyService signaturePolicyService = createSignaturePolicyServiceFromPolicyPaths(VALID_CLASSPATH_CONSTRAINT, INVALID_CLASSPATH_CONSTRAINT);

        assertEquals(1, signaturePolicyService.getSignaturePolicies().size());
        assertNotNull(signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY + "1"));
        signaturePolicyService.getPolicyDataStreamFromPolicy(POLICY + "2");
    }

    private SignaturePolicyService createSignaturePolicyServiceFromPolicyPaths(String... policyPaths) {
        Map<String, String> policies = new HashMap<>();
        int i = 1;
        for (String policyPath : policyPaths) {
            policies.put(POLICY + i++, policyPath);

        }
        SignaturePolicySettings signaturePolicySettings = new SignaturePolicySettings();
        signaturePolicySettings.setPolicies(policies);
        signaturePolicySettings.setDefaultPolicy(POLICY + "1");

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
