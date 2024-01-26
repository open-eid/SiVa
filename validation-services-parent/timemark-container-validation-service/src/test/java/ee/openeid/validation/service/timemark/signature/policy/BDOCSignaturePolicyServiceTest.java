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

package ee.openeid.validation.service.timemark.signature.policy;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mockStatic;

import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@ExtendWith(MockitoExtension.class)
class BDOCSignaturePolicyServiceTest {

    private BDOCSignaturePolicyService bdocSignaturePolicyService;

    @Mock
    private ConstraintLoadingSignaturePolicyService signaturePolicyService;

    @Mock
    private ConstraintDefinedPolicy policy;

    @BeforeEach
    public void setUp() {
        bdocSignaturePolicyService = new BDOCSignaturePolicyService();
        bdocSignaturePolicyService.setSignaturePolicyService(signaturePolicyService);
    }

    @Test
    void givenInvalidPolicyWillThrowException() {
        try (MockedStatic<IOUtils> ioUtils = mockStatic(IOUtils.class)) {
            ioUtils.when(() -> IOUtils.copy(any(InputStream.class), any(OutputStream.class))).thenThrow(new IOException("Copy error"));
            given(policy.getConstraintDataStream()).willReturn(new ByteArrayInputStream("hello".getBytes()));
            given(signaturePolicyService.getPolicy(anyString())).willReturn(policy);
            assertThrows(
                    BdocPolicyFileCreationException.class, () -> bdocSignaturePolicyService.getAbsolutePath("random")
            );
        }
    }
}
