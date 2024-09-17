/*
 * Copyright 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timemark;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.validation.service.timemark.report.TimemarkContainerValidationReportBuilder;
import ee.openeid.validation.service.timemark.signature.policy.BDOCConfigurationService;
import ee.openeid.validation.service.timemark.signature.policy.PolicyConfigurationWrapper;
import org.digidoc4j.ContainerBuilder;
import org.digidoc4j.impl.asic.asice.bdoc.BDocContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static ee.openeid.validation.service.timemark.BDOCTestUtils.VALID_ASICE;
import static ee.openeid.validation.service.timemark.BDOCTestUtils.buildValidationDocument;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimemarkContainerValidationServiceValidationTimeTest {

    @Mock
    private BDOCConfigurationService bDOCConfigurationService;

    @Mock
    private ReportConfigurationProperties reportConfigurationProperties;

    @Mock
    private PolicyConfigurationWrapper policyConfigurationWrapper;

    @Mock
    private ConstraintDefinedPolicy constraintDefinedPolicy;

    @Mock
    private TimemarkContainerValidationReportBuilderFactory timemarkContainerValidationReportBuilderFactory;

    @Mock
    private TimemarkContainerValidationReportBuilder timemarkContainerValidationReportBuilder;

    @Mock
    private ContainerBuilder containerBuilder;

    @Mock
    private BDocContainer containerMock;

    private ValidationDocument validationDocument;

    @BeforeEach
    public void setUp() {
        validationDocument = buildValidationDocument(VALID_ASICE);

        when(containerBuilder.fromStream(any())).thenReturn(containerBuilder);
        when(containerBuilder.withConfiguration(any())).thenReturn(containerBuilder);
        when(containerBuilder.build()).thenReturn(containerMock);

        when(timemarkContainerValidationReportBuilderFactory.getReportBuilder(any(), any(), any(), any(), anyBoolean()))
            .thenReturn(timemarkContainerValidationReportBuilder);
        when(policyConfigurationWrapper.getPolicy()).thenReturn(constraintDefinedPolicy);
        when(bDOCConfigurationService.loadPolicyConfiguration(validationDocument.getSignaturePolicy())).thenReturn(policyConfigurationWrapper);
    }

    @Test
    void validateDocument_ValidationTimePresentInValidationDocument_ValidateAtMethodCalledWithDateArgument() {
        try (MockedStatic<ContainerBuilder> mockedStaticContainerBuilder = Mockito.mockStatic(ContainerBuilder.class)) {
            mockedStaticContainerBuilder.when(ContainerBuilder::aContainer).thenReturn(containerBuilder);

            Date validationTime = new Date();
            validationDocument.setValidationTime(validationTime);

            createValidationService().validateDocument(validationDocument);

            verify(containerMock).validateAt(validationTime);
            verify(containerMock, never()).validate();
        }
    }

    @Test
    void validateDocument_ValidationTimeAbsentInValidationDocument_ValidateMethodCalledWithoutDateArgument() {
        try (MockedStatic<ContainerBuilder> mockedStaticContainerBuilder = Mockito.mockStatic(ContainerBuilder.class)) {
            mockedStaticContainerBuilder.when(ContainerBuilder::aContainer).thenReturn(containerBuilder);

            createValidationService().validateDocument(validationDocument);

            verify(containerMock).validate();
            verify(containerMock, never()).validateAt(any(Date.class));
        }
    }

    private TimemarkContainerValidationService createValidationService() {
        TimemarkContainerValidationService service = new TimemarkContainerValidationService();
        service.setBdocConfigurationService(bDOCConfigurationService);
        service.setReportConfigurationProperties(reportConfigurationProperties);
        service.setTimemarkContainerValidationReportBuilderFactory(timemarkContainerValidationReportBuilderFactory);
        return service;
    }

}
