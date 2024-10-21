/*
 * Copyright 2019 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.exception.ContainerMimetypeFileException;
import ee.openeid.siva.proxy.exception.ValidatonServiceNotFoundException;
import ee.openeid.siva.proxy.validation.ZipMimetypeValidator;
import ee.openeid.siva.statistics.StatisticsService;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.DetailedReport;
import ee.openeid.siva.validation.document.report.DiagnosticReport;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.ValidationWarning;
import ee.openeid.siva.validation.document.report.Warning;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.validation.service.generic.GenericValidationService;
import ee.openeid.validation.service.timemark.report.DDOCContainerValidationReportBuilder;
import ee.openeid.validation.service.timestamptoken.TimeStampTokenValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.WARNING_MSG_DATAFILE_NOT_COVERED_BY_TS;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ValidationProxyTest {
    private static final String DEFAULT_DOCUMENT_NAME = "document.";
    private static final String TEST_FILES_LOCATION = "test-files/";
    private static final String TIMEMARK_CONTAINER_VALIDATION_SERVICE_BEAN = "timemarkContainerValidationService";
    private static final String TIMESTAMP_TOKEN_VALIDATION_SERVICE_BEAN = "timeStampTokenValidationService";
    private static final String GENERIC_VALIDATION_SERVICE_BEAN = "genericValidationService";
    private static final Instant APRIL = ZonedDateTime.of(2024, 4, 1, 1, 0, 0, 0, ZoneId.of("UTC")).toInstant();
    private static final Instant MAY = ZonedDateTime.of(2024, 5, 1, 1, 0, 0, 0, ZoneId.of("UTC")).toInstant();
    private static final Instant JUNE = ZonedDateTime.of(2024, 6, 1, 1, 0, 0, 0, ZoneId.of("UTC")).toInstant();
    private static final String VALIDATION_TIME_NOW = ReportBuilderUtils.getValidationTime();

    private static final ValidationWarning TEST_ENV_WARNING = getTestEnvironmentValidationWarning();

    @InjectMocks
    private ContainerValidationProxy validationProxy;

    @Mock
    private StatisticsService statisticsService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    ZipMimetypeValidator zipMimetypeValidator;

    @Spy
    private StandardEnvironment environment;

    @Test
    void validate_ApplicationContextHasNoBeanWithGivenName_ThrowsException() {
        BDDMockito.given(applicationContext.getBean(anyString())).willThrow(new NoSuchBeanDefinitionException("Bean not loaded"));
        ProxyDocument proxyDocument = mockProxyDocumentWithDocument(DocumentType.PDF);

        ValidatonServiceNotFoundException caughtException = assertThrows(
                ValidatonServiceNotFoundException.class, () -> validationProxy.validate(proxyDocument)
        );
        assertEquals("genericValidationService not found", caughtException.getMessage());
        verify(applicationContext).getBean(anyString());
    }

    @Test
    void validate_ProxyDocumentWithBDOCDocumentType_ReturnsValidationReport() {
        Reports dummyReports = createDummyReports();
        ValidationService validationServiceMock = Mockito.mock(ValidationService.class);
        when(validationServiceMock.validateDocument(any())).thenReturn(dummyReports);
        when(applicationContext.getBean(TIMEMARK_CONTAINER_VALIDATION_SERVICE_BEAN)).thenReturn(validationServiceMock);

        ProxyDocument proxyDocument = mockProxyDocumentWithDocument(DocumentType.BDOC);

        SimpleReport report = validationProxy.validate(proxyDocument);

        assertSame(dummyReports.getSimpleReport(), report);
        assertEquals(VALIDATION_TIME_NOW, report.getValidationConclusion().getValidationTime());
    }

    @Test
    void validate_ProxyDocumentWithPDFDocumentType_ReturnsValidationReport() {
        ValidationService validationServiceMock = Mockito.mock(ValidationService.class);
        Reports dummyReports = createDummyReports();
        when(validationServiceMock.validateDocument(any())).thenReturn(dummyReports);
        when(applicationContext.getBean(GENERIC_VALIDATION_SERVICE_BEAN)).thenReturn(validationServiceMock);

        ProxyDocument proxyDocument = mockProxyDocumentWithDocument(DocumentType.PDF);

        SimpleReport report = validationProxy.validate(proxyDocument);

        assertSame(dummyReports.getSimpleReport(), report);
        assertEquals(VALIDATION_TIME_NOW, report.getValidationConclusion().getValidationTime());
    }

    @Test
    void validate_ProxyDocumentWithDDOCDocumentType_ReturnsValidationReport() {
        ValidationService validationServiceMock = Mockito.mock(ValidationService.class);
        Reports dummyReports = createDummyReports();
        when(validationServiceMock.validateDocument(any())).thenReturn(dummyReports);
        when(applicationContext.getBean(TIMEMARK_CONTAINER_VALIDATION_SERVICE_BEAN)).thenReturn(validationServiceMock);

        ProxyDocument proxyDocument = mockProxyDocumentWithDocument(DocumentType.DDOC);

        SimpleReport report = validationProxy.validate(proxyDocument);

        assertSame(dummyReports.getSimpleReport(), report);
        assertEquals(VALIDATION_TIME_NOW, report.getValidationConclusion().getValidationTime());
    }

    @ParameterizedTest
    @MethodSource("getFileNameAndTestFile")
    void validate_ProxyDocumentWithDifferentExtensions_ReturnsValidationReport(String fileName, String testFile) throws Exception {
        TimeStampTokenValidationService timeStampTokenValidationServiceMock = Mockito.mock(TimeStampTokenValidationService.class);
        when(timeStampTokenValidationServiceMock.validateDocument(any())).thenReturn(createDummyReportsWith3PassedTSTValidations());
        when(applicationContext.getBean(TIMESTAMP_TOKEN_VALIDATION_SERVICE_BEAN)).thenReturn(timeStampTokenValidationServiceMock);

        ValidationService validationServiceMock = Mockito.mock(ValidationService.class);
        Reports dummyReports = createDummyReports();
        when(validationServiceMock.validateDocument(any())).thenReturn(dummyReports);
        when(applicationContext.getBean(TIMEMARK_CONTAINER_VALIDATION_SERVICE_BEAN)).thenReturn(validationServiceMock);

        ProxyDocument proxyDocument = mockProxyDocumentWithExtension(fileName);
        proxyDocument.setBytes(buildValidationDocument(testFile));

        SimpleReport report = validationProxy.validate(proxyDocument);

        assertSame(dummyReports.getSimpleReport(), report);
        assertEquals(VALIDATION_TIME_NOW, report.getValidationConclusion().getValidationTime());

        ArgumentCaptor<ValidationDocument> validationDocumentArgumentCaptor = ArgumentCaptor.forClass(ValidationDocument.class);
        verify(validationServiceMock).validateDocument(validationDocumentArgumentCaptor.capture());
        verifyNoMoreInteractions(validationServiceMock);
        assertEquals(Date.from(MAY), validationDocumentArgumentCaptor.getValue().getValidationTime());
    }

    @Test
    void validate_ProxyDocumentAsicsWithRandomDataFile_ReturnsValidationReport() throws Exception {
        TimeStampTokenValidationService timeStampTokenValidationService = Mockito.mock(TimeStampTokenValidationService.class);
        when(timeStampTokenValidationService.validateDocument(any())).thenReturn(createDummyReportsWith3PassedTSTValidations());
        when(applicationContext.getBean(TIMESTAMP_TOKEN_VALIDATION_SERVICE_BEAN)).thenReturn(timeStampTokenValidationService);

        GenericValidationService genericValidationServiceMock = Mockito.mock(GenericValidationService.class);
        Reports dummyReports = createDummyReports();
        when(genericValidationServiceMock.validateDocument(any())).thenReturn(dummyReports);
        when(applicationContext.getBean(GENERIC_VALIDATION_SERVICE_BEAN)).thenReturn(genericValidationServiceMock);

        ProxyDocument proxyDocument = mockProxyDocumentWithExtension("asics");
        proxyDocument.setBytes(buildValidationDocument("TXTinsideAsics.asics"));

        SimpleReport report = validationProxy.validate(proxyDocument);

        assertSame(dummyReports.getSimpleReport(), report);
        assertEquals(VALIDATION_TIME_NOW, report.getValidationConclusion().getValidationTime());

        ArgumentCaptor<ValidationDocument> validationDocumentArgumentCaptor = ArgumentCaptor.forClass(ValidationDocument.class);
        verify(genericValidationServiceMock).validateDocument(validationDocumentArgumentCaptor.capture());
        verifyNoMoreInteractions(genericValidationServiceMock);
        assertEquals(Date.from(MAY), validationDocumentArgumentCaptor.getValue().getValidationTime());
    }

    @Test
    void validate_ProxyDocumentAsicsWithDifferentMimeType_ReturnsValidationReport() throws Exception {
        TimeStampTokenValidationService timeStampTokenValidationService = Mockito.mock(TimeStampTokenValidationService.class);
        when(applicationContext.getBean(TIMESTAMP_TOKEN_VALIDATION_SERVICE_BEAN)).thenReturn(timeStampTokenValidationService);

        ValidationService validationServiceMock = Mockito.mock(ValidationService.class);
        Reports dummyReports = createDummyReports();
        when(validationServiceMock.validateDocument(any())).thenReturn(dummyReports);
        when(applicationContext.getBean(GENERIC_VALIDATION_SERVICE_BEAN)).thenReturn(validationServiceMock);

        ProxyDocument proxyDocument = mockProxyDocumentWithExtension("zip");
        proxyDocument.setBytes(buildValidationDocument("timestamptoken-different-mimetype.zip"));

        SimpleReport report = validationProxy.validate(proxyDocument);

        assertSame(dummyReports.getSimpleReport(), report);
        assertEquals(VALIDATION_TIME_NOW, report.getValidationConclusion().getValidationTime());
    }

    @Test
    void validate_ProxyDocumentAsics_ReturnsValidationReportNoTeraWarning() throws Exception {
        TimeStampTokenValidationService timeStampTokenValidationService = Mockito.mock(TimeStampTokenValidationService.class);
        when(timeStampTokenValidationService.validateDocument(any())).thenReturn(createDummyReportsWith3PassedTSTValidations());
        when(applicationContext.getBean(TIMESTAMP_TOKEN_VALIDATION_SERVICE_BEAN)).thenReturn(timeStampTokenValidationService);

        ValidationService timemarkContainerValidationServiceMock = Mockito.mock(ValidationService.class);
        Reports dummyReports = createDummyReports();
        when(timemarkContainerValidationServiceMock.validateDocument(any())).thenReturn(dummyReports);
        when(applicationContext.getBean(TIMEMARK_CONTAINER_VALIDATION_SERVICE_BEAN)).thenReturn(timemarkContainerValidationServiceMock);

        ProxyDocument proxyDocument = mockProxyDocumentWithExtension("asics");
        proxyDocument.setBytes(buildValidationDocument("timestamptoken-ddoc.asics"));

        SimpleReport report = validationProxy.validate(proxyDocument);

        assertSame(dummyReports.getSimpleReport(), report);
        assertEquals(VALIDATION_TIME_NOW, report.getValidationConclusion().getValidationTime());

        ArgumentCaptor<ValidationDocument> validationDocumentArgumentCaptor = ArgumentCaptor.forClass(ValidationDocument.class);
        verify(timemarkContainerValidationServiceMock).validateDocument(validationDocumentArgumentCaptor.capture());
        verifyNoMoreInteractions(timemarkContainerValidationServiceMock);
        assertEquals(Date.from(MAY), validationDocumentArgumentCaptor.getValue().getValidationTime());
    }

    @Test
    void validate_TSTWithPassedValidationAndDatafileNotCoveredWarning_ValidateDocumentNotInvoked() throws Exception {
        TimeStampTokenValidationService timeStampTokenValidationService = Mockito.mock(TimeStampTokenValidationService.class);
        when(timeStampTokenValidationService.validateDocument(any())).thenReturn(
            createDummyReportsWith1PassedTSTValidationAndDatafileNotCoveredWarning()
        );
        when(applicationContext.getBean(TIMESTAMP_TOKEN_VALIDATION_SERVICE_BEAN)).thenReturn(timeStampTokenValidationService);

        GenericValidationService genericValidationServiceMock = Mockito.mock(GenericValidationService.class);
        when(applicationContext.getBean(GENERIC_VALIDATION_SERVICE_BEAN)).thenReturn(genericValidationServiceMock);

        ProxyDocument proxyDocument = mockProxyDocumentWithExtension("asics");
        proxyDocument.setBytes(buildValidationDocument("TXTinsideAsics.asics"));

        validationProxy.validate(proxyDocument);

        verify(genericValidationServiceMock, never()).validateDocument(any());
        verifyNoInteractions(genericValidationServiceMock);
    }

    @Test
    void validate_TwoPassedTSTsFirstWithDatafileNotCoveredWarning_ValidateDocumentInvokedAccordingToTSTWithoutWarningDate() throws Exception {
        TimeStampTokenValidationService timeStampTokenValidationService = Mockito.mock(TimeStampTokenValidationService.class);
        when(timeStampTokenValidationService.validateDocument(any())).thenReturn(
            createDummyReportsWith1PassedTSTValidationAndDatafileNotCoveredWarningAnd1WithoutWarning()
        );
        when(applicationContext.getBean(TIMESTAMP_TOKEN_VALIDATION_SERVICE_BEAN)).thenReturn(timeStampTokenValidationService);

        GenericValidationService genericValidationServiceMock = Mockito.mock(GenericValidationService.class);
        Reports dummyReports = createDummyReports();
        when(genericValidationServiceMock.validateDocument(any())).thenReturn(dummyReports);
        when(applicationContext.getBean(GENERIC_VALIDATION_SERVICE_BEAN)).thenReturn(genericValidationServiceMock);

        ProxyDocument proxyDocument = mockProxyDocumentWithExtension("asics");
        proxyDocument.setBytes(buildValidationDocument("TXTinsideAsics.asics"));

        SimpleReport report = validationProxy.validate(proxyDocument);

        assertSame(dummyReports.getSimpleReport(), report);
        assertEquals(VALIDATION_TIME_NOW, report.getValidationConclusion().getValidationTime());

        ArgumentCaptor<ValidationDocument> validationDocumentArgumentCaptor = ArgumentCaptor.forClass(ValidationDocument.class);
        verify(genericValidationServiceMock).validateDocument(validationDocumentArgumentCaptor.capture());
        verifyNoMoreInteractions(genericValidationServiceMock);
        assertEquals(Date.from(JUNE), validationDocumentArgumentCaptor.getValue().getValidationTime());
    }

    @Test
    void removeUnnecessaryWarning_InputWithWarning_WarningRemovedFromResult() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        ValidationWarning validationWarning = new ValidationWarning();
        validationWarning.setContent(DDOCContainerValidationReportBuilder.DDOC_TIMESTAMP_WARNING);
        validationConclusion.setValidationWarnings(Collections.singletonList(validationWarning));

        validationProxy.removeUnnecessaryWarning(validationConclusion);

        assertTrue(validationConclusion.getValidationWarnings().isEmpty());
    }

    @Test
    void validateRequest_DifferentReportTypesRequested_ReturnsReportInRequestedType() {
        mockValidationServices();
        for (DocumentType documentType : DocumentType.values()) {
            validateRequestAndAssertExpectedReportType(mockProxyDocumentWithDocument(documentType, ReportType.SIMPLE),     SimpleReport.class);
            validateRequestAndAssertExpectedReportType(mockProxyDocumentWithDocument(documentType, ReportType.DETAILED),   DetailedReport.class);
            validateRequestAndAssertExpectedReportType(mockProxyDocumentWithDocument(documentType, ReportType.DIAGNOSTIC), DiagnosticReport.class);
            validateRequestAndAssertExpectedReportType(mockProxyDocumentWithDocument(documentType, null),                  SimpleReport.class);

            System.out.println("Successfully validated for document of type " + documentType);
        }
    }

    @Test
    void validate_TestProfileSet_AddsTestEnvironmentWarningToReport() {
        environment.setActiveProfiles("test");
        SimpleReport report = getValidationProxySpy().validate(new ProxyDocument());
        assertThat(report.getValidationConclusion().getValidationWarnings(), contains(TEST_ENV_WARNING));
    }

    @Test
    void validate_TestProfileNotSet_NoTestEnvironmentWarningInReport() {
        SimpleReport report = getValidationProxySpy().validate(new ProxyDocument());
        assertNull(report.getValidationConclusion().getValidationWarnings());
    }

    @Test
    void validate_TestProfileSetAndWarningsPresent_AddsAdditionalWarningToReport() {
        environment.setActiveProfiles("test");
        ValidationWarning warning1 = new ValidationWarning();
        warning1.setContent("warning1");
        ValidationWarning warning2 = new ValidationWarning();
        warning1.setContent("warning2");

        SimpleReport report = getValidationProxySpyWithValidationWarningsInReport(Arrays.asList(warning1, warning2))
                .validate(new ProxyDocument());

        assertThat(report.getValidationConclusion().getValidationWarnings(), contains(TEST_ENV_WARNING, warning1, warning2));
    }

    @Test
    void validate_AsicsWithoutMimetype_ThrowsContainerMimetypeFileException() throws Exception {
        String errorMessage = "some exception message";
        doThrow(new ContainerMimetypeFileException(errorMessage)).when(zipMimetypeValidator).validateZipContainerMimetype(any());
        TimeStampTokenValidationService timeStampTokenValidationService = Mockito.mock(TimeStampTokenValidationService.class);
        when(applicationContext.getBean(TIMESTAMP_TOKEN_VALIDATION_SERVICE_BEAN)).thenReturn(timeStampTokenValidationService);
        when(timeStampTokenValidationService.validateDocument(any())).thenReturn(createDummyReports());

        ProxyDocument proxyDocument = mockProxyDocumentWithExtension("asics");
        proxyDocument.setBytes(buildValidationDocument("TXTinsideAsics.asics"));

        SimpleReport report = validationProxy.validate(proxyDocument);

        assertThat(
            report.getValidationConclusion().getValidationWarnings().stream().map(ValidationWarning::getContent).collect(Collectors.toList()),
            contains(errorMessage)
        );
    }

    private void mockValidationServices() {
        Reports mockReports = mockReports();
        ValidationService validationServiceMock = mock(ValidationService.class);
        when(applicationContext.getBean(anyString())).thenReturn(validationServiceMock);
        when(validationServiceMock.validateDocument(any())).thenReturn(mockReports);
    }

    private void validateRequestAndAssertExpectedReportType(ProxyDocument proxyDocument, Class<? extends SimpleReport> expectedReportClass) {
        SimpleReport report = validationProxy.validateRequest(proxyDocument);
        assertTrue(expectedReportClass.isInstance(report));
    }

    private Reports mockReports() {
        Reports reports = new Reports();
        reports.setSimpleReport(new SimpleReport());
        reports.setDetailedReport(new DetailedReport());
        reports.setDiagnosticReport(new DiagnosticReport());
        return reports;
    }

    private Reports createDummyReportsWith3PassedTSTValidations() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setTimeStampTokens(List.of(
            createTimeStampTokenValidationData(TimeStampTokenValidationData.Indication.TOTAL_PASSED, JUNE),
            createTimeStampTokenValidationData(TimeStampTokenValidationData.Indication.TOTAL_FAILED, APRIL),
            createTimeStampTokenValidationData(TimeStampTokenValidationData.Indication.TOTAL_PASSED, null),
            createTimeStampTokenValidationData(TimeStampTokenValidationData.Indication.TOTAL_PASSED, MAY)
        ));
        validationConclusion.setValidationTime(VALIDATION_TIME_NOW);
        return new Reports(new SimpleReport(validationConclusion), null, null);
    }

    private Reports createDummyReportsWith1PassedTSTValidationAndDatafileNotCoveredWarning() {
        ValidationConclusion validationConclusion = new ValidationConclusion();

        TimeStampTokenValidationData validationData = createTimeStampTokenValidationData(TimeStampTokenValidationData.Indication.TOTAL_PASSED, MAY);
        validationData.setWarning(List.of(new Warning(WARNING_MSG_DATAFILE_NOT_COVERED_BY_TS)));

        validationConclusion.setTimeStampTokens(List.of(validationData));
        validationConclusion.setValidationTime(VALIDATION_TIME_NOW);
        return new Reports(new SimpleReport(validationConclusion), null, null);
    }

    private Reports createDummyReportsWith1PassedTSTValidationAndDatafileNotCoveredWarningAnd1WithoutWarning() {
        ValidationConclusion validationConclusion = new ValidationConclusion();

        TimeStampTokenValidationData validationDataWithWarning = createTimeStampTokenValidationData(TimeStampTokenValidationData.Indication.TOTAL_PASSED, MAY);
        validationDataWithWarning.setWarning(List.of(new Warning(WARNING_MSG_DATAFILE_NOT_COVERED_BY_TS)));

        TimeStampTokenValidationData validationDataWithoutWarning = createTimeStampTokenValidationData(TimeStampTokenValidationData.Indication.TOTAL_PASSED, JUNE);

        validationConclusion.setTimeStampTokens(List.of(validationDataWithWarning, validationDataWithoutWarning));
        validationConclusion.setValidationTime(VALIDATION_TIME_NOW);
        return new Reports(new SimpleReport(validationConclusion), null, null);
    }

    private TimeStampTokenValidationData createTimeStampTokenValidationData(TimeStampTokenValidationData.Indication indication, Instant signedTime) {
        TimeStampTokenValidationData data = new TimeStampTokenValidationData();
        data.setIndication(indication);
        data.setSignedTime(Optional.ofNullable(signedTime).map(Instant::toString).orElse(null));
        return data;
    }

    private ProxyDocument mockProxyDocumentWithDocument(DocumentType documentType) {
        return mockProxyDocumentWithDocument(documentType, ReportType.SIMPLE);
    }

    private ProxyDocument mockProxyDocumentWithDocument(DocumentType documentType, ReportType reportType) {
        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setName(DEFAULT_DOCUMENT_NAME + documentType.name());
        proxyDocument.setBytes("TEST_FILE_CONTENT".getBytes(StandardCharsets.UTF_8));
        proxyDocument.setReportType(reportType);
        return proxyDocument;
    }

    private ProxyDocument mockProxyDocumentWithExtension(String extension) {
        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setName(DEFAULT_DOCUMENT_NAME + extension);
        return proxyDocument;
    }

    private byte[] buildValidationDocument(String testFile) throws Exception {
        Path documentPath = Paths.get(getClass().getClassLoader().getResource(TEST_FILES_LOCATION + testFile).toURI());
        return Files.readAllBytes(documentPath);
    }

    private Reports createDummyReports() {
        ValidationConclusion vc = new ValidationConclusion();
        vc.setValidationLevel("DUMMY");
        vc.setValidationTime(VALIDATION_TIME_NOW);
        vc.setTimeStampTokens(Collections.emptyList());
        return new Reports(new SimpleReport(vc), null, null);
    }

    private static Stream<Arguments> getFileNameAndTestFile() {
        return Stream.of(
                arguments("asics", "timestamptoken-ddoc.asics"),
                arguments("zip", "timestamptoken-ddoc.zip"),
                arguments("scs", "timestamptoken-ddoc.asics")
        );
    }

    private static class ValidationProxySpy extends ValidationProxy {

        private SimpleReport report = new SimpleReport(new ValidationConclusion());

        public ValidationProxySpy(StatisticsService statisticsService, ApplicationContext applicationContext, Environment environment) {
            super(statisticsService, applicationContext, environment);
        }

        @Override
        String constructValidatorName(ProxyRequest proxyRequest) {
            return null;
        }

        @Override
        SimpleReport validateRequest(ProxyRequest proxyRequest) {
            return report;
        }

        private void setReportToReturn(SimpleReport simpleReport) {
            this.report = simpleReport;
        }
    }

    private static ValidationWarning getTestEnvironmentValidationWarning() {
        ValidationWarning validationWarning = new ValidationWarning();
        validationWarning.setContent("This is validation service demo. Use it for testing purposes only");
        return validationWarning;
    }

    private ValidationProxySpy getValidationProxySpyWithValidationWarningsInReport(List<ValidationWarning> warnings) {
        SimpleReport simpleReport = new SimpleReport(new ValidationConclusion());
        simpleReport.getValidationConclusion().setValidationWarnings(warnings);
        ValidationProxySpy validationProxySpy = getValidationProxySpy();
        validationProxySpy.setReportToReturn(simpleReport);
        return validationProxySpy;
    }

    private ValidationProxySpy getValidationProxySpy() {
        return new ValidationProxySpy(statisticsService, applicationContext, environment);
    }

}
