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

package ee.openeid.siva.validation.document.report.builder;

import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SignatureValidationData.Indication;
import ee.openeid.siva.validation.document.report.Warning;
import eu.europa.esig.dss.enumerations.CertificateQualification;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import eu.europa.esig.dss.enumerations.ValidationTime;
import lombok.Builder;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@Builder
public class SignatureLevelAdjuster<T> implements SignatureValidationDataProcessor<T> {

    // For any adjustment mappings to apply, the following global condition must be met first:
    //  * signature indication is TOTAL_PASSED
    private static final List<AdjustmentMapping> ADJUSTMENT_MAPPINGS = List.of(
            // If the following conditions are met:
            //  * signature level is UNKNOWN_QC
            //  * signing certificate qualification at CERTIFICATE_ISSUANCE_TIME is QCERT_FOR_ESIG_QSCD
            //  * signing certificate qualification at BEST_SIGNATURE_TIME is QCERT_FOR_ESEAL
            // Then the following adjustments are needed:
            //  * signature level to be adjusted to ADESEAL_QC
            //  * a warning to be added to the signature
            AdjustmentMapping.builder()
                    .expectedSignatureQualification(SignatureQualification.UNKNOWN_QC)
                    .expectedCertificateQualifications(Set.of(
                            CertificateQualificationMapping.builder()
                                    .validationTime(ValidationTime.CERTIFICATE_ISSUANCE_TIME)
                                    .certificateQualification(CertificateQualification.QCERT_FOR_ESIG_QSCD)
                                    .build(),
                            CertificateQualificationMapping.builder()
                                    .validationTime(ValidationTime.BEST_SIGNATURE_TIME)
                                    .certificateQualification(CertificateQualification.QCERT_FOR_ESEAL)
                                    .build()
                    ))
                    .signatureQualificationToAdjustTo(SignatureQualification.ADESEAL_QC)
                    .warningMessageOnAdjustment(String.format(
                            "The signature level has been re-evaluated from initial %s to %s by SiVa validation policy!",
                            SignatureQualification.UNKNOWN_QC,
                            SignatureQualification.ADESEAL_QC
                    ))
                    .build()
    );

    private final @NonNull BiFunction<T, ValidationTime, CertificateQualification> certificateQualificationResolver;
    private final BiConsumer<T, SignatureLevelAdjuster.Event> signatureQualificationAdjustmentLister;

    @Override
    public void process(SignatureValidationData signatureValidationData, T signatureIdentifier) {
        if (!Indication.TOTAL_PASSED.toString().equals(signatureValidationData.getIndication())) {
            return;
        }

        String initialSignatureLevel = signatureValidationData.getSignatureLevel();

        for (AdjustmentMapping mapping : ADJUSTMENT_MAPPINGS) {
            if (isAdjustmentNeeded(mapping, initialSignatureLevel, signatureIdentifier)) {
                adjustSignatureData(mapping, signatureValidationData);

                if (signatureQualificationAdjustmentLister != null) {
                    signatureQualificationAdjustmentLister.accept(signatureIdentifier, mapping);
                }

                return;
            }
        }
    }

    private boolean isAdjustmentNeeded(AdjustmentMapping mapping, String initialSignatureLevel, T signatureIdentifier) {
        if (!mapping.expectedSignatureQualification().name().equals(initialSignatureLevel)) {
            return false;
        }

        for (CertificateQualificationMapping qualificationMapping : mapping.expectedCertificateQualifications()) {
            CertificateQualification actualQualification = certificateQualificationResolver
                    .apply(signatureIdentifier, qualificationMapping.validationTime());

            if (!qualificationMapping.certificateQualification().equals(actualQualification)) {
                return false;
            }
        }

        return true;
    }

    private void adjustSignatureData(AdjustmentMapping mapping, SignatureValidationData signatureValidationData) {
        signatureValidationData.setSignatureLevel(mapping.signatureQualificationToAdjustTo().name());

        List<Warning> warningList = signatureValidationData.getWarnings();

        if (warningList == null) {
            warningList = new ArrayList<>();
            signatureValidationData.setWarnings(warningList);
        }

        warningList.add(ReportBuilderUtils.createValidationWarning(mapping.warningMessageOnAdjustment()));
    }

    public interface Event {
        SignatureQualification getOldSignatureQualification();
        SignatureQualification getNewSignatureQualification();
    }

    @Builder
    private static record AdjustmentMapping(
            @NonNull SignatureQualification expectedSignatureQualification,
            @NonNull Set<CertificateQualificationMapping> expectedCertificateQualifications,
            @NonNull SignatureQualification signatureQualificationToAdjustTo,
            @NonNull String warningMessageOnAdjustment
    ) implements Event {

        @Override
        public SignatureQualification getOldSignatureQualification() {
            return expectedSignatureQualification();
        }

        @Override
        public SignatureQualification getNewSignatureQualification() {
            return signatureQualificationToAdjustTo();
        }

    }

    @Builder
    private static record CertificateQualificationMapping(
            @NonNull ValidationTime validationTime,
            @NonNull CertificateQualification certificateQualification
    ) {
    }

}
