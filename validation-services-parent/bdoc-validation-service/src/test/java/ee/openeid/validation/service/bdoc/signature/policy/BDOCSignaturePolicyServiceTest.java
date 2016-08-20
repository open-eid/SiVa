package ee.openeid.validation.service.bdoc.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.validation.service.bdoc.configuration.BDOCSignaturePolicyProperties;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
public class BDOCSignaturePolicyServiceTest {

    private BDOCSignaturePolicyService bdocSignaturePolicyService;

    @Mock
    private SignaturePolicyService signaturePolicyService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private BDOCSignaturePolicyProperties properties = new BDOCSignaturePolicyProperties();

    @Before
    public void setUp() throws Exception {
        Map<String, String> invalidPolicyMap = new HashMap<>();
        invalidPolicyMap.put("random", "invalid-policy.xml");
        properties.setAbstractDefaultPolicy("EE");
        properties.setAbstractPolicies(invalidPolicyMap);

        bdocSignaturePolicyService = new BDOCSignaturePolicyService();
        bdocSignaturePolicyService.setSignaturePolicyService(signaturePolicyService);
    }

    @Test
    @PrepareForTest(value = {IOUtils.class})
    public void givenInvalidPolicyWillThrowException() throws Exception {
        expectedException.expect(BdocPolicyFileCreationException.class);

        mockStatic(IOUtils.class);
        given(IOUtils.copy(any(InputStream.class), any(OutputStream.class))).willThrow(new IOException("Copy error"));
        given(signaturePolicyService.getPolicyDataStreamFromPolicy(anyString())).willReturn(new ByteArrayInputStream("hello".getBytes()));

        bdocSignaturePolicyService.getAbsolutePath("random");
    }
}