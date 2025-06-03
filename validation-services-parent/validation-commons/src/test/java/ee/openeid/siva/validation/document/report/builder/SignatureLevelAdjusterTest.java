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

package ee.openeid.siva.validation.document.report.builder;

import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SignatureValidationData.Indication;
import ee.openeid.siva.validation.document.report.Warning;
import eu.europa.esig.dss.enumerations.CertificateQualification;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import eu.europa.esig.dss.enumerations.ValidationTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class SignatureLevelAdjusterTest {

    private static final SignatureIdentifierType SIGNATURE_ID = new SignatureIdentifierType();
    private static final CertificateQualification AFFECTED_ISSUANCE_TIME_QUALIFICATION = CertificateQualification.QCERT_FOR_ESIG_QSCD;
    private static final CertificateQualification AFFECTED_SIGNING_TIME_QUALIFICATION = CertificateQualification.QCERT_FOR_ESEAL;

    private static final String WARNING_MESSAGE_TEMPLATE = "The signature level has been re-evaluated from initial" +
            " %s to %s by SiVa validation policy!";

    @Mock
    private BiFunction<SignatureIdentifierType, ValidationTime, CertificateQualification> certificateQualificationResolver;
    @Mock
    private BiConsumer<SignatureIdentifierType, SignatureLevelAdjuster.Event> signatureQualificationAdjustmentLister;
    @InjectMocks
    private SignatureLevelAdjuster<SignatureIdentifierType> signatureLevelAdjuster;

    @Mock
    private SignatureValidationData signatureValidationData;

    @ParameterizedTest
    @EnumSource(value = Indication.class, mode = EnumSource.Mode.EXCLUDE, names = "TOTAL_PASSED")
    void process_WhenSignatureIndicationIsNotTotalPassed_NoFurtherInteractions(Indication indication) {
        doReturn(indication.toString()).when(signatureValidationData).getIndication();

        signatureLevelAdjuster.process(signatureValidationData, SIGNATURE_ID);

        verifyNoMoreInteractions(signatureValidationData);
        verifyNoInteractions(certificateQualificationResolver, signatureQualificationAdjustmentLister);
    }

    @ParameterizedTest
    @EnumSource(value = SignatureQualification.class, mode = EnumSource.Mode.EXCLUDE, names = "UNKNOWN_QC")
    void process_WhenSignatureLevelIsNotUnknownQc_NoFurtherInteractions(SignatureQualification qualification) {
        doReturn(Indication.TOTAL_PASSED.toString()).when(signatureValidationData).getIndication();
        doReturn(qualification.name()).when(signatureValidationData).getSignatureLevel();

        signatureLevelAdjuster.process(signatureValidationData, SIGNATURE_ID);

        verifyNoMoreInteractions(signatureValidationData);
        verifyNoInteractions(certificateQualificationResolver, signatureQualificationAdjustmentLister);
    }

    @Test
    void process_WhenCertificateQualificationsAreMissing_NoFurtherInteractions() {
        doReturn(Indication.TOTAL_PASSED.toString()).when(signatureValidationData).getIndication();
        doReturn(SignatureQualification.UNKNOWN_QC.name()).when(signatureValidationData).getSignatureLevel();
        lenient().doReturn(null).when(certificateQualificationResolver)
                .apply(SIGNATURE_ID, ValidationTime.CERTIFICATE_ISSUANCE_TIME);
        lenient().doReturn(null).when(certificateQualificationResolver)
                .apply(SIGNATURE_ID, ValidationTime.BEST_SIGNATURE_TIME);

        signatureLevelAdjuster.process(signatureValidationData, SIGNATURE_ID);

        verify(certificateQualificationResolver, atMost(1))
                .apply(SIGNATURE_ID, ValidationTime.CERTIFICATE_ISSUANCE_TIME);
        verify(certificateQualificationResolver, atMost(1))
                .apply(SIGNATURE_ID, ValidationTime.BEST_SIGNATURE_TIME);
        verifyNoMoreInteractions(signatureValidationData, certificateQualificationResolver);
        verifyNoInteractions(signatureQualificationAdjustmentLister);
    }

    @ParameterizedTest
    @MethodSource("unaffectedCertificateQualificationCombinations")
    void process_WhenCertificateQualificationsDoNotRequireSignatureLevelAdjustment_NoFurtherInteractions(
            CertificateQualification issuanceTimeQualification, CertificateQualification signingTimeQualification
    ) {
        doReturn(Indication.TOTAL_PASSED.toString()).when(signatureValidationData).getIndication();
        doReturn(SignatureQualification.UNKNOWN_QC.name()).when(signatureValidationData).getSignatureLevel();
        lenient().doReturn(issuanceTimeQualification).when(certificateQualificationResolver)
                .apply(SIGNATURE_ID, ValidationTime.CERTIFICATE_ISSUANCE_TIME);
        lenient().doReturn(signingTimeQualification).when(certificateQualificationResolver)
                .apply(SIGNATURE_ID, ValidationTime.BEST_SIGNATURE_TIME);

        signatureLevelAdjuster.process(signatureValidationData, SIGNATURE_ID);

        verify(certificateQualificationResolver, atMost(1))
                .apply(SIGNATURE_ID, ValidationTime.CERTIFICATE_ISSUANCE_TIME);
        verify(certificateQualificationResolver, atMost(1))
                .apply(SIGNATURE_ID, ValidationTime.BEST_SIGNATURE_TIME);
        verifyNoMoreInteractions(signatureValidationData, certificateQualificationResolver);
        verifyNoInteractions(signatureQualificationAdjustmentLister);
    }

    @Test
    void process_WhenAllConditionsAreMet_SignatureLevelIsSetToAdesealQcAndWarningIsAdded() {
        doReturn(Indication.TOTAL_PASSED.toString()).when(signatureValidationData).getIndication();
        doReturn(SignatureQualification.UNKNOWN_QC.name()).when(signatureValidationData).getSignatureLevel();
        doReturn(AFFECTED_ISSUANCE_TIME_QUALIFICATION).when(certificateQualificationResolver)
                .apply(SIGNATURE_ID, ValidationTime.CERTIFICATE_ISSUANCE_TIME);
        doReturn(AFFECTED_SIGNING_TIME_QUALIFICATION).when(certificateQualificationResolver)
                .apply(SIGNATURE_ID, ValidationTime.BEST_SIGNATURE_TIME);
        @SuppressWarnings("unchecked")
        List<Warning> warningsList = (List<Warning>) mock(List.class);
        doReturn(warningsList).when(signatureValidationData).getWarnings();

        signatureLevelAdjuster.process(signatureValidationData, SIGNATURE_ID);

        verify(signatureValidationData).setSignatureLevel(SignatureQualification.ADESEAL_QC.name());
        verify(warningsList).add(ReportBuilderUtils.createValidationWarning(String.format(
                WARNING_MESSAGE_TEMPLATE, SignatureQualification.UNKNOWN_QC, SignatureQualification.ADESEAL_QC
        )));
        verifyEventFired(SIGNATURE_ID, SignatureQualification.UNKNOWN_QC, SignatureQualification.ADESEAL_QC);
        verifyNoMoreInteractions(signatureValidationData, certificateQualificationResolver,
                signatureQualificationAdjustmentLister, warningsList);
    }

    @Test
    void process_WhenWarningListIsMissingDuringSignatureLevelAdjustment_WarningListWithNewWarningIsAdded() {
        doReturn(Indication.TOTAL_PASSED.toString()).when(signatureValidationData).getIndication();
        doReturn(SignatureQualification.UNKNOWN_QC.name()).when(signatureValidationData).getSignatureLevel();
        doReturn(AFFECTED_ISSUANCE_TIME_QUALIFICATION).when(certificateQualificationResolver)
                .apply(SIGNATURE_ID, ValidationTime.CERTIFICATE_ISSUANCE_TIME);
        doReturn(AFFECTED_SIGNING_TIME_QUALIFICATION).when(certificateQualificationResolver)
                .apply(SIGNATURE_ID, ValidationTime.BEST_SIGNATURE_TIME);
        doReturn(null).when(signatureValidationData).getWarnings();

        signatureLevelAdjuster.process(signatureValidationData, SIGNATURE_ID);

        verify(signatureValidationData).setSignatureLevel(SignatureQualification.ADESEAL_QC.name());
        verify(signatureValidationData).setWarnings(List.of(ReportBuilderUtils.createValidationWarning(String.format(
                WARNING_MESSAGE_TEMPLATE, SignatureQualification.UNKNOWN_QC, SignatureQualification.ADESEAL_QC
        ))));
        verifyEventFired(SIGNATURE_ID, SignatureQualification.UNKNOWN_QC, SignatureQualification.ADESEAL_QC);
        verifyNoMoreInteractions(signatureValidationData, certificateQualificationResolver,
                signatureQualificationAdjustmentLister);
    }

    void verifyEventFired(
            SignatureIdentifierType expectedSignatureIdentifier,
            SignatureQualification expectedOldSignatureQualification,
            SignatureQualification expectedNewSignatureQualification
    ) {
        ArgumentCaptor<SignatureLevelAdjuster.Event> eventCaptor = ArgumentCaptor.forClass(SignatureLevelAdjuster.Event.class);
        verify(signatureQualificationAdjustmentLister).accept(same(expectedSignatureIdentifier), eventCaptor.capture());
        assertThat(eventCaptor.getAllValues(), hasSize(1));

        SignatureLevelAdjuster.Event capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getOldSignatureQualification(), equalTo(expectedOldSignatureQualification));
        assertThat(capturedEvent.getNewSignatureQualification(), equalTo(expectedNewSignatureQualification));
    }

    static Stream<Arguments> unaffectedCertificateQualificationCombinations() {
        return allCertificateQualificationCombinations()
                .filter(args -> !AFFECTED_ISSUANCE_TIME_QUALIFICATION.equals(args.get()[0])
                        && !AFFECTED_SIGNING_TIME_QUALIFICATION.equals(args.get()[0]));
    }

    static Stream<Arguments> allCertificateQualificationCombinations() {
        return Stream.of(CertificateQualification.values())
                .flatMap(q1 -> Stream.of(CertificateQualification.values())
                        .map(q2 -> Arguments.of(q1, q2))
                );
    }

    record SignatureIdentifierType() {
    }

}
