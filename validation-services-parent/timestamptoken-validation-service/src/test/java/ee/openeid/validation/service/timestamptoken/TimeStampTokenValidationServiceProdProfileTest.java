/*
 * Copyright 2024 - 2025 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static ee.openeid.validation.service.timestamptoken.TestUtils.NOT_GRANTED_WARNING;
import static ee.openeid.validation.service.timestamptoken.TestUtils.POE_TIME_MESSAGE_TEXT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(SpringExtension.class)
class TimeStampTokenValidationServiceProdProfileTest extends BaseTimeStampTokenValidationServiceTest {

    @Test
    void validate_WhenAsicsWithOneExpiredTimestamp_ValidationResultIsValidAndContainsWarnings() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("timestamptoken-ddoc.asics")).getSimpleReport();
        ValidationConclusion validationConclusion = simpleReport.getValidationConclusion();

        assertThat(validationConclusion.getValidationWarnings(), notNullValue());
        assertThat(validationConclusion.getValidationWarnings(), hasSize(1));
        assertThat(validationConclusion.getValidationWarnings().get(0).getContent(), equalTo(NOT_GRANTED_WARNING));

        {
            List<TimeStampTokenValidationData> validationData = validationConclusion.getTimeStampTokens();
            assertThat(validationData, notNullValue());
            assertThat(validationData, hasSize(1));
            assertThat(validationData.get(0).getIndication(), equalTo(TimeStampTokenValidationData.Indication.TOTAL_PASSED));
            assertThat(validationData.get(0).getError(), nullValue());
            assertThat(validationData.get(0).getWarning(), notNullValue());
            assertThat(validationData.get(0).getWarning(), hasSize(1));
            assertThat(validationData.get(0).getWarning().get(0).getContent(), equalTo(POE_TIME_MESSAGE_TEXT));
        }
    }

    @Test
    void validate_WhenAsicsWithOneExpiredAndOneValidTimestamp_ValidationResultIsValidAndContainsWarnings() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("2xTST-text-data-file-expired-tst-and-valid-tst.asics")).getSimpleReport();
        ValidationConclusion validationConclusion = simpleReport.getValidationConclusion();

        assertThat(validationConclusion.getValidationWarnings(), notNullValue());
        assertThat(validationConclusion.getValidationWarnings(), hasSize(1));
        assertThat(validationConclusion.getValidationWarnings().get(0).getContent(), equalTo(NOT_GRANTED_WARNING));

        {
            List<TimeStampTokenValidationData> validationData = validationConclusion.getTimeStampTokens();
            assertThat(validationData, notNullValue());
            assertThat(validationData, hasSize(2));

            assertThat(validationData.get(0).getIndication(), equalTo(TimeStampTokenValidationData.Indication.TOTAL_PASSED));
            assertThat(validationData.get(0).getError(), nullValue());
            assertThat(validationData.get(0).getWarning(), notNullValue());
            assertThat(validationData.get(0).getWarning(), hasSize(1));
            assertThat(validationData.get(0).getWarning().get(0).getContent(), equalTo(POE_TIME_MESSAGE_TEXT));

            assertThat(validationData.get(1).getIndication(), equalTo(TimeStampTokenValidationData.Indication.TOTAL_PASSED));
            assertThat(validationData.get(1).getError(), nullValue());
            assertThat(validationData.get(1).getWarning(), notNullValue());
            assertThat(validationData.get(1).getWarning(), empty());
        }
    }

    @Test
    void validate_WhenAsicsWithInvalidAndNonCoveringTimestamp_ValidationResultIsValidButContainsAnErrorAndUnrelatedWarning() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("2xTST-text-data-file-1st-tst-invalid-2nd-tst-no-coverage.asics")).getSimpleReport();
        ValidationConclusion validationConclusion = simpleReport.getValidationConclusion();

        assertThat(validationConclusion.getValidationWarnings(), nullValue());

        {
            List<TimeStampTokenValidationData> validationData = validationConclusion.getTimeStampTokens();
            assertThat(validationData, notNullValue());
            assertThat(validationData, hasSize(2));

            assertThat(validationData.get(0).getIndication(), equalTo(TimeStampTokenValidationData.Indication.TOTAL_FAILED));
            assertThat(validationData.get(0).getError(), notNullValue());
            assertThat(validationData.get(0).getError(), hasSize(1));
            assertThat(validationData.get(0).getError().get(0).getContent(), equalTo("The time-stamp message imprint is not intact!"));
            assertThat(validationData.get(0).getWarning(), empty());

            assertThat(validationData.get(1).getIndication(), equalTo(TimeStampTokenValidationData.Indication.TOTAL_PASSED));
            assertThat(validationData.get(1).getError(), nullValue());
            assertThat(validationData.get(1).getWarning(), notNullValue());
            assertThat(validationData.get(1).getWarning(), hasSize(1));
            assertThat(validationData.get(1).getWarning().get(0).getContent(), equalTo("The time-stamp token does not cover container datafile!"));
        }
    }

}
