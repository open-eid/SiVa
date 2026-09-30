/*
 * Copyright 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Info;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.ValidationLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static ee.openeid.siva.validation.helper.matcher.IsArchiveTimeStamp.isArchiveTimeStampWithIndications;
import static ee.openeid.siva.validation.helper.matcher.IsError.error;
import static ee.openeid.siva.validation.helper.matcher.IsWarning.warning;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ActiveProfiles("test")
public class HashcodeGenericValidationServiceValidationLevelTest extends HashcodeGenericValidationServiceTestBase {

    @ParameterizedTest
    @NullSource
    @EnumSource(value = ValidationLevel.class, names = "ARCHIVAL_DATA")
    void validate_WhenValidationLevelIsDefaultOrArchivalDataForLta_HashFailure(ValidationLevel validationLevel) {
        ValidationDocument validationDocument = buildValidationDocument("valid_xades_lta.xml", buildDefaultDatafile());
        validationDocument.setValidationLevel(validationLevel != null ? validationLevel.name() : null);

        Reports reports = validationService.validate(List.of(validationDocument));

        ValidationConclusion validationConclusion = reports.getSimpleReport().getValidationConclusion();
        assertThat(validationConclusion.getSignaturesCount(), is(1));
        assertThat(validationConclusion.getValidSignaturesCount(), is(0));
        assertThat(validationConclusion.getValidationLevel(), equalTo("ARCHIVAL_DATA"));
        SignatureValidationData signatureValidationData = validationConclusion.getSignatures().get(0);
        assertThat(signatureValidationData.getSignatureFormat(), equalTo("XAdES_BASELINE_LTA"));
        assertThat(signatureValidationData.getIndication(), equalTo("TOTAL-FAILED"));
        assertThat(signatureValidationData.getErrors(), contains(
                error("The time-stamp message imprint is not intact!")
        ));
        assertThat(signatureValidationData.getWarnings(), empty());
        Info info = signatureValidationData.getInfo();
        assertThat(info.getArchiveTimeStamps(), contains(
                isArchiveTimeStampWithIndications(Indication.FAILED, "HASH_FAILURE")
        ));
    }

    @Test
    void validate_WhenValidationLevelIsLongTermDataForLta_PassedWithWarning() {
        ValidationDocument validationDocument = buildValidationDocument("valid_xades_lta.xml", buildDefaultDatafile());
        validationDocument.setValidationLevel(ValidationLevel.LONG_TERM_DATA.name());

        Reports reports = validationService.validate(List.of(validationDocument));

        ValidationConclusion validationConclusion = reports.getSimpleReport().getValidationConclusion();
        assertThat(validationConclusion.getSignaturesCount(), is(1));
        assertThat(validationConclusion.getValidSignaturesCount(), is(1));
        assertThat(validationConclusion.getValidationLevel(), equalTo("LONG_TERM_DATA"));
        SignatureValidationData signatureValidationData = validationConclusion.getSignatures().get(0);
        assertThat(signatureValidationData.getSignatureFormat(), equalTo("XAdES_BASELINE_LTA"));
        assertThat(signatureValidationData.getIndication(), equalTo("TOTAL-PASSED"));
        assertThat(signatureValidationData.getErrors(), empty());
        assertThat(signatureValidationData.getWarnings(), contains(
                warning("LTA archive timestamps have no effect on the validation result of the signature at the LONG_TERM_DATA validation level.")
        ));
        Info info = signatureValidationData.getInfo();
        assertThat(info.getArchiveTimeStamps(), contains(
                isArchiveTimeStampWithIndications(Indication.FAILED, "HASH_FAILURE")
        ));
    }

    @ParameterizedTest
    @NullSource
    @EnumSource(value = ValidationLevel.class, names = {"LONG_TERM_DATA", "ARCHIVAL_DATA"})
    void validate_WhenValidationLevelIsAnyForLt_PassedWithoutWarnings(ValidationLevel validationLevel) {
        ValidationDocument validationDocument = buildValidationDocument("valid_xades_lt.xml", buildDefaultDatafile());
        validationDocument.setValidationLevel(validationLevel != null ? validationLevel.name() : null);

        Reports reports = validationService.validate(List.of(validationDocument));

        ValidationConclusion validationConclusion = reports.getSimpleReport().getValidationConclusion();
        assertThat(validationConclusion.getSignaturesCount(), is(1));
        assertThat(validationConclusion.getValidSignaturesCount(), is(1));
        assertThat(validationConclusion.getValidationLevel(), equalTo(validationLevel != null ? validationLevel.name() : "ARCHIVAL_DATA"));
        SignatureValidationData signatureValidationData = validationConclusion.getSignatures().get(0);
        assertThat(signatureValidationData.getSignatureFormat(), equalTo("XAdES_BASELINE_LT"));
        assertThat(signatureValidationData.getIndication(), equalTo("TOTAL-PASSED"));
        assertThat(signatureValidationData.getErrors(), empty());
        assertThat(signatureValidationData.getWarnings(), empty());
        Info info = signatureValidationData.getInfo();
        assertThat(info.getArchiveTimeStamps(), anyOf(empty(), nullValue()));
    }

    private static Datafile buildDefaultDatafile() {
        Datafile datafile = new Datafile();
        datafile.setFilename("test.txt");
        datafile.setHashAlgo("SHA256");
        datafile.setHash("m+EEKU331aWcMoJB1JrAYuLHuWYGNuf1EeOh3D2Rk3c=");
        return datafile;
    }

}
