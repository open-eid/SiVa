package ee.openeid.siva.validation.service.properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;

public class PolicySettingsTest {

    private static final String VALID_CONSTRAINT = "/valid-constraint.xml";
    private static final String INVALID_CONSTRAINT = "/invalid-constraint.xml";

    private PolicySettings policySettings = new PolicySettings();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void settingValidPolicyFromClasspathResourceResultsInNoException() {
        policySettings.setPolicy(VALID_CONSTRAINT);
        assertNotNull(policySettings.getPolicyDataStream());
    }

    @Test
    public void settingValidPolicyAsAbsolutePathResultsInNoException() throws URISyntaxException {
        URL resource = getClass().getResource(VALID_CONSTRAINT);
        policySettings.setPolicy(Paths.get(resource.toURI()).toAbsolutePath().toString());
        assertNotNull(policySettings.getPolicyDataStream());
    }

    @Test
    public void settingInvalidPolicyFromClasspathResourceResultsInException() {
        expectedException.expect(PolicyValidationException.class);
        policySettings.setPolicy(INVALID_CONSTRAINT);
    }

    @Test
    public void settingInvalidPolicyAsAbsolutePathResultsInException() throws URISyntaxException {
        expectedException.expect(PolicyValidationException.class);
        URL resource = getClass().getResource(INVALID_CONSTRAINT);
        policySettings.setPolicy(Paths.get(resource.toURI()).toAbsolutePath().toString());
    }

    @Test
    public void settingNonExistingPolicyLocationResultsInException() {
        expectedException.expect(PolicyValidationException.class);
        policySettings.setPolicy("some-non-existing-policy.xml");
    }

}
