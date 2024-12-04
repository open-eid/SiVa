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

package ee.openeid.validation.service.timestamptoken;

import ee.openeid.siva.validation.document.report.ValidationWarning;
import ee.openeid.siva.validation.document.report.Warning;
import ee.openeid.validation.service.timestamptoken.util.TimestampNotGrantedValidationUtils;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.simplereport.jaxb.XmlDetails;
import eu.europa.esig.dss.simplereport.jaxb.XmlEvidenceRecord;
import eu.europa.esig.dss.simplereport.jaxb.XmlMessage;
import eu.europa.esig.dss.simplereport.jaxb.XmlSignature;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import eu.europa.esig.dss.simplereport.jaxb.XmlTimestamp;
import eu.europa.esig.dss.simplereport.jaxb.XmlToken;
import eu.europa.esig.dss.validation.reports.Reports;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ee.openeid.validation.service.generic.validator.report.DssSimpleReportWrapper.createXmlMessage;
import static ee.openeid.validation.service.timestamptoken.TestUtils.NOT_GRANTED_WARNING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class TimestampNotGrantedValidationUtilsTest {

    @Test
    void convertNotGrantedErrorsToWarnings_WhenSimpleReportHasNoTokens_NothingChanged() {
        XmlSimpleReport simpleReport = mockSimpleReportWithTokens();
        Reports reports = mockReports(simpleReport);

        TimestampNotGrantedValidationUtils.convertNotGrantedErrorsToWarnings(reports);

        verify(reports).getSimpleReportJaxb();
        verify(simpleReport).getSignatureOrTimestampOrEvidenceRecord();
        verifyNoMoreInteractions(reports, simpleReport);
    }

    @Test
    void convertNotGrantedErrorsToWarnings_WhenSimpleReportHasNullToken_NothingChanged() {
        XmlSimpleReport simpleReport = mockSimpleReportWithTokens(Collections.singletonList(null));
        Reports reports = mockReports(simpleReport);

        TimestampNotGrantedValidationUtils.convertNotGrantedErrorsToWarnings(reports);

        verify(reports).getSimpleReportJaxb();
        verify(simpleReport).getSignatureOrTimestampOrEvidenceRecord();
        verifyNoMoreInteractions(reports, simpleReport);
    }

    @Test
    void convertNotGrantedErrorsToWarnings_WhenSimpleReportHasSignatureToken_NoInteractionWithToken() {
        XmlSignature signature = mock(XmlSignature.class);
        XmlSimpleReport simpleReport = mockSimpleReportWithTokens(Collections.singletonList(signature));
        Reports reports = mockReports(simpleReport);

        TimestampNotGrantedValidationUtils.convertNotGrantedErrorsToWarnings(reports);

        verify(reports).getSimpleReportJaxb();
        verify(simpleReport).getSignatureOrTimestampOrEvidenceRecord();
        verifyNoMoreInteractions(reports, simpleReport);
        verifyNoInteractions(signature);
    }

    @Test
    void convertNotGrantedErrorsToWarnings_WhenSimpleReportHasEvidenceRecordToken_NoInteractionWithToken() {
        XmlEvidenceRecord evidenceRecord = mock(XmlEvidenceRecord.class);
        XmlSimpleReport simpleReport = mockSimpleReportWithTokens(Collections.singletonList(evidenceRecord));
        Reports reports = mockReports(simpleReport);

        TimestampNotGrantedValidationUtils.convertNotGrantedErrorsToWarnings(reports);

        verify(reports).getSimpleReportJaxb();
        verify(simpleReport).getSignatureOrTimestampOrEvidenceRecord();
        verifyNoMoreInteractions(reports, simpleReport);
        verifyNoInteractions(evidenceRecord);
    }

    @ParameterizedTest
    @EnumSource(value = Indication.class, mode = EnumSource.Mode.EXCLUDE, names = "PASSED")
    void convertNotGrantedErrorsToWarnings_WhenSimpleReportHasNonPassedTimestamp_NoFurtherInteraction(Indication indication) {
        XmlTimestamp timestamp = mock(XmlTimestamp.class);
        doReturn(indication).when(timestamp).getIndication();
        XmlSimpleReport simpleReport = mockSimpleReportWithTokens(Collections.singletonList(timestamp));
        Reports reports = mockReports(simpleReport);

        TimestampNotGrantedValidationUtils.convertNotGrantedErrorsToWarnings(reports);

        verify(reports).getSimpleReportJaxb();
        verify(simpleReport).getSignatureOrTimestampOrEvidenceRecord();
        verify(timestamp).getIndication();
        verifyNoMoreInteractions(reports, simpleReport, timestamp);
    }

    @Test
    void convertNotGrantedErrorsToWarnings_WhenPassedTimestampHasNotGrantedErrorInAdesDetailsBlock_NothingChanged() {
        XmlTimestamp timestamp = createXmlTimestampWithIndication(Indication.PASSED);
        XmlMessage notGrantedMessage = createXmlMessage(
                MessageTag.QUAL_HAS_GRANTED_AT_ANS.getId(),
                new I18nProvider().getMessage(MessageTag.QUAL_HAS_GRANTED_AT_ANS, MessageTag.VT_TST_POE_TIME)
        );
        timestamp.setAdESValidationDetails(createXmlDetailsWithErrors(notGrantedMessage));
        XmlSimpleReport simpleReport = mockSimpleReportWithTokens(Collections.singletonList(timestamp));
        Reports reports = mockReports(simpleReport);

        TimestampNotGrantedValidationUtils.convertNotGrantedErrorsToWarnings(reports);

        assertThat(timestamp.getAdESValidationDetails(), notNullValue());
        assertThat(timestamp.getAdESValidationDetails().getError(), equalTo(Collections.singletonList(notGrantedMessage)));
        assertThat(timestamp.getAdESValidationDetails().getWarning(), empty());
        assertThat(timestamp.getAdESValidationDetails().getInfo(), empty());
        assertThat(timestamp.getQualificationDetails(), nullValue());
        verify(reports).getSimpleReportJaxb();
        verify(simpleReport).getSignatureOrTimestampOrEvidenceRecord();
        verifyNoMoreInteractions(reports, simpleReport);
    }

    @Test
    void convertNotGrantedErrorsToWarnings_WhenPassedTimestampHasUnrelatedErrorInQualificationBlock_NothingChanged() {
        XmlTimestamp timestamp = createXmlTimestampWithIndication(Indication.PASSED);
        XmlMessage testMessage = createXmlMessage("TEST", "Some test message");
        timestamp.setQualificationDetails(createXmlDetailsWithErrors(testMessage));
        XmlSimpleReport simpleReport = mockSimpleReportWithTokens(Collections.singletonList(timestamp));
        Reports reports = mockReports(simpleReport);

        TimestampNotGrantedValidationUtils.convertNotGrantedErrorsToWarnings(reports);

        assertThat(timestamp.getAdESValidationDetails(), nullValue());
        assertThat(timestamp.getQualificationDetails(), notNullValue());
        assertThat(timestamp.getQualificationDetails().getError(), equalTo(Collections.singletonList(testMessage)));
        assertThat(timestamp.getQualificationDetails().getWarning(), empty());
        assertThat(timestamp.getQualificationDetails().getInfo(), empty());
        verify(reports).getSimpleReportJaxb();
        verify(simpleReport).getSignatureOrTimestampOrEvidenceRecord();
        verifyNoMoreInteractions(reports, simpleReport);
    }

    @Test
    void convertNotGrantedErrorsToWarnings_WhenPassedTimestampHasNotGrantedErrorInQualificationBlock_ConvertsToWarning() {
        XmlTimestamp timestamp = createXmlTimestampWithIndication(Indication.PASSED);
        XmlMessage notGrantedMessage = createXmlMessage(
                MessageTag.QUAL_HAS_GRANTED_AT_ANS.getId(),
                new I18nProvider().getMessage(MessageTag.QUAL_HAS_GRANTED_AT_ANS, MessageTag.VT_TST_POE_TIME)
        );
        timestamp.setQualificationDetails(createXmlDetailsWithErrors(notGrantedMessage));
        XmlSimpleReport simpleReport = mockSimpleReportWithTokens(Collections.singletonList(timestamp));
        Reports reports = mockReports(simpleReport);

        TimestampNotGrantedValidationUtils.convertNotGrantedErrorsToWarnings(reports);

        assertThat(timestamp.getAdESValidationDetails(), nullValue());
        assertThat(timestamp.getQualificationDetails(), notNullValue());
        assertThat(timestamp.getQualificationDetails().getError(), empty());
        assertThat(timestamp.getQualificationDetails().getWarning(), equalTo(Collections.singletonList(notGrantedMessage)));
        assertThat(timestamp.getQualificationDetails().getInfo(), empty());
        verify(reports).getSimpleReportJaxb();
        verify(simpleReport).getSignatureOrTimestampOrEvidenceRecord();
        verifyNoMoreInteractions(reports, simpleReport);
    }

    @Test
    void convertNotGrantedErrorsToWarnings_WhenPassedTimestampHasNotGrantedErrorInQualificationBlockButAlsoQualificationWarnings_ConvertsToWarning() {
        XmlTimestamp timestamp = createXmlTimestampWithIndication(Indication.PASSED);
        XmlMessage notGrantedMessage = createXmlMessage(
                MessageTag.QUAL_HAS_GRANTED_AT_ANS.getId(),
                new I18nProvider().getMessage(MessageTag.QUAL_HAS_GRANTED_AT_ANS, MessageTag.VT_TST_POE_TIME)
        );
        timestamp.setQualificationDetails(createXmlDetailsWithErrors(notGrantedMessage));
        XmlMessage testMessage = createXmlMessage("TEST", "Some test message");
        timestamp.getQualificationDetails().getWarning().add(testMessage);
        XmlSimpleReport simpleReport = mockSimpleReportWithTokens(Collections.singletonList(timestamp));
        Reports reports = mockReports(simpleReport);

        TimestampNotGrantedValidationUtils.convertNotGrantedErrorsToWarnings(reports);

        assertThat(timestamp.getAdESValidationDetails(), nullValue());
        assertThat(timestamp.getQualificationDetails(), notNullValue());
        assertThat(timestamp.getQualificationDetails().getError(), empty());
        assertThat(timestamp.getQualificationDetails().getWarning(), equalTo(Arrays.asList(testMessage, notGrantedMessage)));
        assertThat(timestamp.getQualificationDetails().getInfo(), empty());
        verify(reports).getSimpleReportJaxb();
        verify(simpleReport).getSignatureOrTimestampOrEvidenceRecord();
        verifyNoMoreInteractions(reports, simpleReport);
    }

    @Test
    void convertNotGrantedErrorsToWarnings_WhenPassedTimestampHasNotGrantedErrorInQualificationBlockButAlsoAdesErrors_NothingChanged() {
        XmlTimestamp timestamp = createXmlTimestampWithIndication(Indication.PASSED);
        XmlMessage testMessage = createXmlMessage("TEST", "Some test message");
        timestamp.setAdESValidationDetails(createXmlDetailsWithErrors(testMessage));
        XmlMessage notGrantedMessage = createXmlMessage(
                MessageTag.QUAL_HAS_GRANTED_AT_ANS.getId(),
                new I18nProvider().getMessage(MessageTag.QUAL_HAS_GRANTED_AT_ANS, MessageTag.VT_TST_POE_TIME)
        );
        timestamp.setQualificationDetails(createXmlDetailsWithErrors(notGrantedMessage));
        XmlSimpleReport simpleReport = mockSimpleReportWithTokens(Collections.singletonList(timestamp));
        Reports reports = mockReports(simpleReport);

        TimestampNotGrantedValidationUtils.convertNotGrantedErrorsToWarnings(reports);

        assertThat(timestamp.getAdESValidationDetails(), notNullValue());
        assertThat(timestamp.getAdESValidationDetails().getError(), equalTo(Collections.singletonList(testMessage)));
        assertThat(timestamp.getAdESValidationDetails().getWarning(), empty());
        assertThat(timestamp.getAdESValidationDetails().getInfo(), empty());
        assertThat(timestamp.getQualificationDetails(), notNullValue());
        assertThat(timestamp.getQualificationDetails().getError(), equalTo(Collections.singletonList(notGrantedMessage)));
        assertThat(timestamp.getQualificationDetails().getWarning(), empty());
        assertThat(timestamp.getQualificationDetails().getInfo(), empty());
        verify(reports).getSimpleReportJaxb();
        verify(simpleReport).getSignatureOrTimestampOrEvidenceRecord();
        verifyNoMoreInteractions(reports, simpleReport);
    }

    @Test
    void convertNotGrantedErrorsToWarnings_WhenPassedTimestampHasNotGrantedErrorAndOtherErrorsInQualificationBlock_NothingChanged() {
        XmlTimestamp timestamp = createXmlTimestampWithIndication(Indication.PASSED);
        XmlMessage notGrantedMessage = createXmlMessage(
                MessageTag.QUAL_HAS_GRANTED_AT_ANS.getId(),
                new I18nProvider().getMessage(MessageTag.QUAL_HAS_GRANTED_AT_ANS, MessageTag.VT_TST_POE_TIME)
        );
        XmlMessage testMessage = createXmlMessage("TEST", "Some test message");
        timestamp.setQualificationDetails(createXmlDetailsWithErrors(notGrantedMessage, testMessage));
        XmlSimpleReport simpleReport = mockSimpleReportWithTokens(Collections.singletonList(timestamp));
        Reports reports = mockReports(simpleReport);

        TimestampNotGrantedValidationUtils.convertNotGrantedErrorsToWarnings(reports);

        assertThat(timestamp.getAdESValidationDetails(), nullValue());
        assertThat(timestamp.getQualificationDetails(), notNullValue());
        assertThat(timestamp.getQualificationDetails().getError(), equalTo(Arrays.asList(notGrantedMessage, testMessage)));
        assertThat(timestamp.getQualificationDetails().getWarning(), empty());
        assertThat(timestamp.getQualificationDetails().getInfo(), empty());
        verify(reports).getSimpleReportJaxb();
        verify(simpleReport).getSignatureOrTimestampOrEvidenceRecord();
        verifyNoMoreInteractions(reports, simpleReport);
    }

    @Test
    void getValidationWarningIfNotGrantedTimestampExists_WhenWarningsIsNull_ReturnsNull() {
        ValidationWarning validationWarning = TimestampNotGrantedValidationUtils
                .getValidationWarningIfNotGrantedTimestampExists(null);

        assertNull(validationWarning);
    }

    @Test
    void getValidationWarningIfNotGrantedTimestampExists_WhenWarningsIsEmpty_ReturnsNull() {
        ValidationWarning validationWarning = TimestampNotGrantedValidationUtils
                .getValidationWarningIfNotGrantedTimestampExists(new ArrayList<>());

        assertNull(validationWarning);
    }

    @Test
    void getValidationWarningIfNotGrantedTimestampExists_WhenWarningsContainsUnrelatedWarning_ReturnsNull() {
        List<Warning> timestampWarnings = new ArrayList<>();
        Warning unrelatedWarning = new Warning("Unrelated warning message");
        timestampWarnings.add(unrelatedWarning);

        ValidationWarning validationWarning = TimestampNotGrantedValidationUtils
                .getValidationWarningIfNotGrantedTimestampExists(timestampWarnings);

        assertNull(validationWarning);
    }

    @Test
    void getValidationWarningIfNotGrantedTimestampExists_WhenWarningsContainsNotGrantedWarning_ValidationWarningIsReturned() {
        List<Warning> timestampWarnings = new ArrayList<>();
        Warning notGrantedWarning = new Warning(new I18nProvider().getMessage(MessageTag.QUAL_HAS_GRANTED_AT_ANS, MessageTag.VT_TST_POE_TIME));
        timestampWarnings.add(notGrantedWarning);

        ValidationWarning validationWarning = TimestampNotGrantedValidationUtils
                .getValidationWarningIfNotGrantedTimestampExists(timestampWarnings);

        assertThat(validationWarning, notNullValue());
        assertThat(validationWarning.getContent(), equalTo(NOT_GRANTED_WARNING));
    }

    @Test
    void getValidationWarningIfNotGrantedTimestampExists_WhenWarningsContainsNotGrantedWarningsAmongOthers_ValidationWarningIsReturned() {
        List<Warning> timestampWarnings = new ArrayList<>();
        Warning notGrantedWarning = new Warning(new I18nProvider().getMessage(MessageTag.QUAL_HAS_GRANTED_AT_ANS, MessageTag.VT_TST_POE_TIME));
        Warning unrelatedWarning1 = new Warning("Unrelated warning 1");
        Warning unrelatedWarning2 = new Warning("Unrelated warning 2");
        Warning unrelatedWarning3 = new Warning("Unrelated warning 3");
        Collections.addAll(timestampWarnings, unrelatedWarning1, notGrantedWarning, unrelatedWarning2, unrelatedWarning3);

        ValidationWarning validationWarning = TimestampNotGrantedValidationUtils
                .getValidationWarningIfNotGrantedTimestampExists(timestampWarnings);

        assertThat(validationWarning, notNullValue());
        assertThat(validationWarning.getContent(), equalTo(NOT_GRANTED_WARNING));
    }

    private static XmlTimestamp createXmlTimestampWithIndication(Indication indication) {
        XmlTimestamp timestamp = new XmlTimestamp();
        timestamp.setIndication(indication);
        return timestamp;
    }

    private static XmlDetails createXmlDetailsWithErrors(XmlMessage... errors) {
        return createXmlDetailsWithErrors(Arrays.asList(errors));
    }

    private static XmlDetails createXmlDetailsWithErrors(List<XmlMessage> errors) {
        XmlDetails details = new XmlDetails();
        details.getError().addAll(errors);
        return details;
    }

    private static XmlSimpleReport mockSimpleReportWithTokens(XmlToken... tokens) {
        return mockSimpleReportWithTokens(Arrays.asList(tokens));
    }

    private static XmlSimpleReport mockSimpleReportWithTokens(List<XmlToken> tokens) {
        XmlSimpleReport simpleReport = mock(XmlSimpleReport.class);
        doReturn(tokens).when(simpleReport).getSignatureOrTimestampOrEvidenceRecord();
        return simpleReport;
    }

    private static Reports mockReports(XmlSimpleReport simpleReport) {
        Reports reports = mock(Reports.class);
        doReturn(simpleReport).when(reports).getSimpleReportJaxb();
        return reports;
    }
}
