/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.validation.service.timemark.signature.policy;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;

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

@RunWith(PowerMockRunner.class)
public class BDOCSignaturePolicyServiceTest {

    private BDOCSignaturePolicyService bdocSignaturePolicyService;

    @Mock
    private ConstraintLoadingSignaturePolicyService signaturePolicyService;

    @Mock
    private ConstraintDefinedPolicy policy;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        bdocSignaturePolicyService = new BDOCSignaturePolicyService();
        bdocSignaturePolicyService.setSignaturePolicyService(signaturePolicyService);
    }

    @Test
    @PrepareForTest(value = {IOUtils.class})
    public void givenInvalidPolicyWillThrowException() throws Exception {
        expectedException.expect(BdocPolicyFileCreationException.class);
        mockStatic(IOUtils.class);
        given(IOUtils.copy(any(InputStream.class), any(OutputStream.class))).willThrow(new IOException("Copy error"));
        given(policy.getConstraintDataStream()).willReturn(new ByteArrayInputStream("hello".getBytes()));
        given(signaturePolicyService.getPolicy(anyString())).willReturn(policy);
        bdocSignaturePolicyService.getAbsolutePath("random");
    }
}
