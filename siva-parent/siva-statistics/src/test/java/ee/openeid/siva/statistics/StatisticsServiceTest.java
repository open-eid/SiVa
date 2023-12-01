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

package ee.openeid.siva.statistics;

import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.slf4j.MarkerFactory.getMarker;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    private static final String X_AUTHENTICATED_USER = "x-authenticated-user";
    private static final String CONTAINER_LOG_MARKER = "STATISTICS_CONTAINER_LOG";
    private static final String SIGNATURE_LOG_MARKER = "STATISTICS_SIGNATURE_LOG";

    private static StatisticsService statisticsService;
    private static MockedStatic<LoggerFactory> loggerFactoryMock;
    private static Logger loggerMock;

    @BeforeAll
    public static void setUp() {
        loggerMock = mock(Logger.class);
        loggerFactoryMock = mockStatic(LoggerFactory.class);
        loggerFactoryMock.when(() -> LoggerFactory.getLogger(StatisticsService.class)).thenReturn(loggerMock);

        statisticsService = new StatisticsService();

        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        statisticsService.setHttpRequest(mockedRequest);
    }

    @AfterAll
    public static void tearDown() {
        loggerFactoryMock.close();
    }

    @AfterEach
    public void clearMock() {
        Mockito.reset(loggerMock);
    }

    @Test
    void testValidationStatisticsLoggingWhereAllSignaturesInValidationReportAreValid() {
        long validationDurationInMillis = 1000L;
        String signatureForm = "ASiC-E";
        int validSignaturesCount = 1;
        int totalSignatureCount = 1;
        SignatureValidationData.Indication indication = SignatureValidationData.Indication.TOTAL_PASSED;
        String subindication = "";
        String countryCode = "EE";
        String signatureFormat = "XAdES_some_prefix";

        SimpleReport report = createDummySimpleReport(signatureForm, validSignaturesCount, totalSignatureCount);
        addSignatureValidationData(report.getValidationConclusion(), indication, subindication, countryCode, signatureFormat);

        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        statisticsService.setHttpRequest(mockedRequest);
        when(mockedRequest.getHeader(X_AUTHENTICATED_USER)).thenReturn("");

        statisticsService.publishValidationStatistic(TimeUnit.MILLISECONDS.toNanos(validationDurationInMillis), report.getValidationConclusion());

        verify(loggerMock).info(getMarker(CONTAINER_LOG_MARKER),
                "{\"stats\":{" +
                        "\"type\":\"ASiC-E\"," +
                        "\"usrId\":\"N/A\"," +
                        "\"dur\":1000," +
                        "\"sigCt\":1," +
                        "\"vSigCt\":1," +
                        "\"sigRslt\":[" +
                            "{\"i\":\"TOTAL-PASSED\"," +
                            "\"cc\":\"EE\"," +
                            "\"sf\":\"XAdES_some_prefix\"}]," +
                        "\"sigType\":\"XAdES\"}}");

        verify(loggerMock).info(getMarker(SIGNATURE_LOG_MARKER),
                "{\"signatureStats\":{\"i\":\"TOTAL-PASSED\",\"cc\":\"EE\",\"sf\":\"XAdES_some_prefix\"}}");
    }

    @Test
    void testValidationStatisticsLoggingWhereOneSignatureInValidationReportIsValid() {
        long validationDurationInMillis = 2000L;
        String signatureForm = "ASiC-E";
        int validSignaturesCount = 1;
        int totalSignatureCount = 2;
        SignatureValidationData.Indication firstSignatureIndication = SignatureValidationData.Indication.TOTAL_PASSED;
        String firstSignatureSubindication = "";
        String firstSignatureCountryCode = "EE";
        String firstSignatureFormat = "XAdES_some_prefix";
        SignatureValidationData.Indication secondSignatureIndication = SignatureValidationData.Indication.TOTAL_FAILED;
        String secondSignatureSubindication = "CERTIFICATE_CHAIN_NOT_FOUND";
        String secondSignatureCountryCode = "US";
        String secondSignatureFormat = "";
        String xAuthenticatedUser = "some_user";

        SimpleReport report = createDummySimpleReport(signatureForm, validSignaturesCount, totalSignatureCount);
        addSignatureValidationData(report.getValidationConclusion(), firstSignatureIndication, firstSignatureSubindication, firstSignatureCountryCode, firstSignatureFormat);
        addSignatureValidationData(report.getValidationConclusion(), secondSignatureIndication, secondSignatureSubindication, secondSignatureCountryCode, secondSignatureFormat);

        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        statisticsService.setHttpRequest(mockedRequest);
        when(mockedRequest.getHeader(X_AUTHENTICATED_USER)).thenReturn(xAuthenticatedUser);

        statisticsService.publishValidationStatistic(TimeUnit.MILLISECONDS.toNanos(validationDurationInMillis), report.getValidationConclusion());

        verify(loggerMock).info(getMarker(CONTAINER_LOG_MARKER),
                "{\"stats\":{" +
                        "\"type\":\"ASiC-E\"," +
                        "\"usrId\":\"some_user\"," +
                        "\"dur\":2000," +
                        "\"sigCt\":2," +
                        "\"vSigCt\":1," +
                        "\"sigRslt\":[" +
                            "{\"i\":\"TOTAL-PASSED\"," +
                            "\"cc\":\"EE\"," +
                            "\"sf\":\"XAdES_some_prefix\"}," +
                            "{\"i\":\"TOTAL-FAILED\"," +
                            "\"si\":\"CERTIFICATE_CHAIN_NOT_FOUND\"," +
                            "\"cc\":\"US\"}]," +
                        "\"sigType\":\"XAdES\"}}");

        verify(loggerMock).info(getMarker(SIGNATURE_LOG_MARKER),
                "{\"signatureStats\":{\"i\":\"TOTAL-PASSED\",\"cc\":\"EE\",\"sf\":\"XAdES_some_prefix\"}}");

        verify(loggerMock).info(getMarker(SIGNATURE_LOG_MARKER),
                "{\"signatureStats\":{\"i\":\"TOTAL-FAILED\",\"si\":\"CERTIFICATE_CHAIN_NOT_FOUND\",\"cc\":\"US\"}}");
    }

    @Test
    void testValidationStatisticsLoggingDoesNotLogSignatureInfoForTstTypeContainer() {
        long validationDurationInMillis = 1000L;
        String signatureForm = "ASiC-S";
        int validSignaturesCount = 1;
        int totalSignatureCount = 1;
        SignatureValidationData.Indication indication = SignatureValidationData.Indication.TOTAL_PASSED;
        String subindication = "";
        String countryCode = "EE";
        String signatureFormat = "XAdES_some_prefix";

        SimpleReport report = createDummySimpleReport(signatureForm, validSignaturesCount, totalSignatureCount);
        addSignatureValidationData(report.getValidationConclusion(), indication, subindication, countryCode, signatureFormat);
        List<TimeStampTokenValidationData> timestampTokens = new ArrayList<>();
        timestampTokens.add(new TimeStampTokenValidationData());
        report.getValidationConclusion().setTimeStampTokens(timestampTokens);

        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        statisticsService.setHttpRequest(mockedRequest);
        when(mockedRequest.getHeader(X_AUTHENTICATED_USER)).thenReturn("");

        statisticsService.publishValidationStatistic(TimeUnit.MILLISECONDS.toNanos(validationDurationInMillis), report.getValidationConclusion());

        verify(loggerMock).info(getMarker(CONTAINER_LOG_MARKER),
                "{\"stats\":{" +
                        "\"type\":\"ASiC-S\"," +
                        "\"usrId\":\"N/A\"," +
                        "\"dur\":1000," +
                        "\"sigCt\":0," +
                        "\"vSigCt\":0," +
                        "\"sigRslt\":[]," +
                        "\"sigType\":\"N/A\"}}");

        verify(loggerMock, never()).info(eq(getMarker(SIGNATURE_LOG_MARKER)), anyString());
    }

    private SimpleReport createDummySimpleReport(String signatureForm, int validSignaturesCount, int totalSignaturesCount) {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setSignaturesCount(totalSignaturesCount);
        validationConclusion.setValidSignaturesCount(validSignaturesCount);
        validationConclusion.setSignatureForm(signatureForm);
        return new SimpleReport(validationConclusion);
    }

    private void addSignatureValidationData(ValidationConclusion validationConclusion, SignatureValidationData.Indication indication, String subindication, String country, String signatureFormat) {
        if (validationConclusion.getSignatures() == null) {
            validationConclusion.setSignatures(new ArrayList<>());
        }
        SignatureValidationData sigData = new SignatureValidationData();
        sigData.setIndication(indication);
        sigData.setSubIndication(subindication);
        sigData.setCountryCode(country);
        sigData.setSignatureFormat(signatureFormat);
        validationConclusion.getSignatures().add(sigData);
    }

}
