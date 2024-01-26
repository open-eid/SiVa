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

package ee.openeid.siva.validation.document.report.builder;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportBuilderUtilsTest {

    private static final String QES_POLICY = "POLv4";

    @Test
    void indicationNotChangingQesigSignatureLevel() {
        ValidationConclusion validationConclusion = getDefaultValidationConclusion(SignatureQualification.QESIG);
        ReportBuilderUtils.processSignatureIndications(validationConclusion, QES_POLICY);
        assertTotalPassed(validationConclusion);
    }

    @Test
    void indicationNotChangingQesealSignatureLevel() {
        ValidationConclusion validationConclusion = getDefaultValidationConclusion(SignatureQualification.QESEAL);
        ReportBuilderUtils.processSignatureIndications(validationConclusion, QES_POLICY);
        assertTotalPassed(validationConclusion);
    }

    @Test
    void indicationNotChangingAdesealQsSignatureLevel() {
        ValidationConclusion validationConclusion = getDefaultValidationConclusion(SignatureQualification.ADESEAL_QC);
        ReportBuilderUtils.processSignatureIndications(validationConclusion, QES_POLICY);
        assertTotalPassed(validationConclusion);
    }

    @Test
    void indicationToPassedWithWarningFailedAdesigQsSignatureLevel() {
        ValidationConclusion validationConclusion = getDefaultValidationConclusion(SignatureQualification.ADESIG_QC);
        ReportBuilderUtils.processSignatureIndications(validationConclusion, QES_POLICY);
        assertEquals("TOTAL-PASSED", validationConclusion.getSignatures().get(0).getIndication());
        assertEquals(1, validationConclusion.getSignatures().get(0).getWarnings().size());
        assertTrue(validationConclusion.getSignatures().get(0).getErrors().isEmpty());
        assertEquals("The signature is not in the Qualified Electronic Signature level", validationConclusion.getSignatures().get(0).getWarnings().get(0).getContent());
    }

    private void assertTotalPassed(ValidationConclusion validationConclusion) {
        SignatureValidationData signatureValidationData = validationConclusion.getSignatures().get(0);
        assertEquals("TOTAL-PASSED", signatureValidationData.getIndication());
        assertTrue(signatureValidationData.getWarnings().isEmpty());
        assertTrue(signatureValidationData.getErrors().isEmpty());
    }

    private void assertTotalFailed(ValidationConclusion validationConclusion) {
        assertEquals("TOTAL-FAILED", validationConclusion.getSignatures().get(0).getIndication());
        assertTrue(validationConclusion.getSignatures().get(0).getWarnings().isEmpty());
        List<Error> errors = validationConclusion.getSignatures().get(0).getErrors();
        assertEquals(1, errors.size());
        assertEquals("Signature/seal level do not meet the minimal level required by applied policy", errors.get(0).getContent());
    }

    private ValidationConclusion getDefaultValidationConclusion(SignatureQualification signatureQualification) {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        Policy policy = new Policy();
        policy.setPolicyName("POLv4");
        validationConclusion.setPolicy(policy);
        validationConclusion.setSignatures(getSignatures(signatureQualification));
        return validationConclusion;
    }

    private List<SignatureValidationData> getSignatures(SignatureQualification signatureQualification) {
        List<SignatureValidationData> signatures = new ArrayList<>();
        SignatureValidationData totalPassedSignature = new SignatureValidationData();
        totalPassedSignature.setIndication(SignatureValidationData.Indication.TOTAL_PASSED);
        totalPassedSignature.setSignatureLevel(signatureQualification.name());
        totalPassedSignature.setErrors(new ArrayList<>());
        totalPassedSignature.setWarnings(new ArrayList<>());
        signatures.add(totalPassedSignature);
        return signatures;

    }


    @Test
    void utilClassConstructorMustBePrivate() throws Exception {
        final Constructor<ReportBuilderUtils> constructor = ReportBuilderUtils.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    void givenNullValueWillReturnEmptyString() throws Exception {
        assertThat(ReportBuilderUtils.emptyWhenNull(null)).isEmpty();
    }

    @Test
    void givenNotEmptyStringWillReturnItUnchanged() throws Exception {
        assertThat(ReportBuilderUtils.emptyWhenNull("random")).isEqualTo("random");
    }

    @Test
    void validValidatedDocumentReturned() {
        byte[] data = "testData".getBytes();
        ValidatedDocument response = ReportBuilderUtils.createValidatedDocument(true, "filename.asice", data);
        assertEquals("filename.asice", response.getFilename());
        assertEquals("SHA256", response.getHashAlgo());
        assertEquals("ukd6CsV+EN2Qu1vwKJxZkP6DnGGbJv3nwqrGL1JtQRM=", response.getFileHash());
    }

    @Test
    void validValidatedDocumentReturnedWithoutReportSignature() {
        byte[] data = "testData".getBytes();
        ValidatedDocument response = ReportBuilderUtils.createValidatedDocument(false, "filename.asice", data);
        assertEquals("filename.asice", response.getFilename());
        assertNull(response.getHashAlgo());
        assertNull(response.getFileHash());
    }
}
