/*
 * Copyright 2017 - 2024 Riigi Infosüsteemi Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.validation.service.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConstraintLoadingSignaturePolicyServiceTest {

    private static final String VALID_CLASSPATH_CONSTRAINT = "valid-constraint.xml";
    private static final String INVALID_CLASSPATH_CONSTRAINT = "invalid-constraint.xml";

    private static final String VALID_ABSOLUTE_PATH_CONSTRAINT = getResourceAbsolutePath("/valid-constraint.xml");
    private static final String INVALID_ABSOLUTE_PATH_CONSTRAINT = getResourceAbsolutePath("/invalid-constraint.xml");

    private static final String NON_EXISITNG_CLASSPATH_CONSTRAINT = "non-existing-constraint.xml";
    private static final String NON_EXISITNG_ABSOLUTE_PATH_CONSTRAINT = "/non-existing-constraint.xml";

    @Test
    void whenSignaturePolicesDoNotContainDefaultPolicyThenThrowException() {
        ConstraintDefinedPolicy pol1 = createValidationPolicy("pol1", VALID_CLASSPATH_CONSTRAINT);

        assertThrows(
            DefaultPolicyNotDefinedException.class, () -> createSignaturePolicyService("RANDOM_PREFIX" + "pol1", pol1)
        );
    }

    @Test
    void whenDefaultPolicyReferencesPolicyThatCannotBeLoadedThenThrowException() {
        ConstraintDefinedPolicy pol1 = createValidationPolicy("pol1", INVALID_CLASSPATH_CONSTRAINT);

        assertThrows(
                CannotLoadPolicyReferencedByDefaultPolicyException.class, () -> createSignaturePolicyService("pol1", pol1)
        );
    }

    @Test
    void settingValidPolicyFromClasspathResourceGetsLoaded() {
        ConstraintDefinedPolicy pol1 = createValidationPolicy("pol1", VALID_CLASSPATH_CONSTRAINT);
        ConstraintLoadingSignaturePolicyService signaturePolicyService = createSignaturePolicyService("pol1", pol1);
        assertEquals(1, signaturePolicyService.getSignaturePolicies().size());
        assertNotNull(signaturePolicyService.getPolicy("pol1"));
    }

    @Test
    void settingValidPolicyFromAbsolutePathGetsLoaded() {
        ConstraintDefinedPolicy pol1 = createValidationPolicy("pol1", VALID_ABSOLUTE_PATH_CONSTRAINT);
        ConstraintLoadingSignaturePolicyService signaturePolicyService = createSignaturePolicyService("pol1", pol1);
        assertEquals(1, signaturePolicyService.getSignaturePolicies().size());
        assertNotNull(signaturePolicyService.getPolicy("pol1"));
    }

    @Test
    void settingInvalidPolicyFromClasspathResourceGetsNotLoaded() {
        ConstraintDefinedPolicy pol1 = createValidationPolicy("pol1", INVALID_CLASSPATH_CONSTRAINT);
        ConstraintDefinedPolicy pol2 = createValidationPolicy("pol2", VALID_CLASSPATH_CONSTRAINT);
        assertPolicyNotLoaded(createSignaturePolicyService("pol2", pol1, pol2), "pol1");
    }

    @Test
    void settingInvalidPolicyFromAbsolutePathGetsNotLoaded() {
        ConstraintDefinedPolicy pol1 = createValidationPolicy("pol1", INVALID_ABSOLUTE_PATH_CONSTRAINT);
        ConstraintDefinedPolicy pol2 = createValidationPolicy("pol2", VALID_CLASSPATH_CONSTRAINT);
        assertPolicyNotLoaded(createSignaturePolicyService("pol2", pol1, pol2), "pol1");
    }

    @Test
    void settingNonExistingPolicyFromClasspathGetsNotLoaded() {
        ConstraintDefinedPolicy pol1 = createValidationPolicy("pol1", NON_EXISITNG_CLASSPATH_CONSTRAINT);
        ConstraintDefinedPolicy pol2 = createValidationPolicy("pol2", VALID_CLASSPATH_CONSTRAINT);
        assertPolicyNotLoaded(createSignaturePolicyService("pol2", pol1, pol2), "pol1");
    }

    @Test
    void settingNonExistingPolicyFromAbsolutePathGetsNotLoaded() {
        ConstraintDefinedPolicy pol1 = createValidationPolicy("pol1", NON_EXISITNG_ABSOLUTE_PATH_CONSTRAINT);
        ConstraintDefinedPolicy pol2 = createValidationPolicy("pol2", VALID_CLASSPATH_CONSTRAINT);
        assertPolicyNotLoaded(createSignaturePolicyService("pol2", pol1, pol2), "pol1");
    }

    @Test
    void onlyPoliciesWithUniqueNamesAreLoaded() {
        ConstraintDefinedPolicy pol1 = createValidationPolicy("pol1", VALID_CLASSPATH_CONSTRAINT);
        ConstraintDefinedPolicy pol2 = createValidationPolicy("pol2", VALID_CLASSPATH_CONSTRAINT);
        ConstraintDefinedPolicy pol3 = createValidationPolicy("pol2", VALID_CLASSPATH_CONSTRAINT);
        ConstraintLoadingSignaturePolicyService signaturePolicyService = createSignaturePolicyService("pol1", pol1, pol2, pol3);
        assertEquals(2, signaturePolicyService.getSignaturePolicies().size());
        assertEquals(pol1, signaturePolicyService.getSignaturePolicies().get("pol1"));
        assertEquals(pol2, signaturePolicyService.getSignaturePolicies().get("pol2"));
        assertEquals(VALID_CLASSPATH_CONSTRAINT, signaturePolicyService.getSignaturePolicies().get("pol2").getConstraintPath());
    }

    @Test
    void settingMultipleValidPoliciesResultsInAllGetLoaded() throws IOException {
        ConstraintDefinedPolicy pol1 = createValidationPolicy("pol1", VALID_CLASSPATH_CONSTRAINT);
        ConstraintDefinedPolicy pol2 = createValidationPolicy("pol2", VALID_ABSOLUTE_PATH_CONSTRAINT);
        ConstraintLoadingSignaturePolicyService signaturePolicyService = createSignaturePolicyService("pol1", pol1, pol2);
        assertEquals(2, signaturePolicyService.getSignaturePolicies().size());
        assertNotNull(signaturePolicyService.getPolicy("pol1"));
        assertNotNull(signaturePolicyService.getPolicy("pol2"));
    }

    private ConstraintLoadingSignaturePolicyService createSignaturePolicyService(String defaultPolicy, ConstraintDefinedPolicy... policyPaths) {
        List<ConstraintDefinedPolicy> policies = new ArrayList<>();
        stream(policyPaths).forEach(policies::add);
        SignaturePolicyProperties<ConstraintDefinedPolicy> signaturePolicyProperties = new SignaturePolicyProperties<>();
        signaturePolicyProperties.setAbstractPolicies(policies);
        signaturePolicyProperties.setAbstractDefaultPolicy(defaultPolicy);
        return new SignaturePolicyServiceImpl(signaturePolicyProperties);
    }

    private ConstraintDefinedPolicy createValidationPolicy(String name, String constraintPath) {
        ConstraintDefinedPolicy constraintDefinedPolicy = new ConstraintDefinedPolicy();
        constraintDefinedPolicy.setName(name);
        constraintDefinedPolicy.setConstraintPath(constraintPath);
        return constraintDefinedPolicy;
    }

    private void assertPolicyNotLoaded(ConstraintLoadingSignaturePolicyService signaturePolicyService, String notLoadedPolicyName) {
        assertEquals(1, signaturePolicyService.getSignaturePolicies().size());
        assertNull(signaturePolicyService.getSignaturePolicies().get(notLoadedPolicyName));

        assertThrows(
                InvalidPolicyException.class, () -> signaturePolicyService.getPolicy(notLoadedPolicyName)
        );
    }

    private static String getResourceAbsolutePath(String resourceRelativePath) {
        try {
            URL resource = ConstraintLoadingSignaturePolicyServiceTest.class.getResource(resourceRelativePath);
            return Paths.get(resource.toURI()).toAbsolutePath().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private class SignaturePolicyServiceImpl extends ConstraintLoadingSignaturePolicyService {
        public SignaturePolicyServiceImpl(SignaturePolicyProperties<ConstraintDefinedPolicy> signaturePolicyProperties) {
            super(signaturePolicyProperties);
        }
    }
}
