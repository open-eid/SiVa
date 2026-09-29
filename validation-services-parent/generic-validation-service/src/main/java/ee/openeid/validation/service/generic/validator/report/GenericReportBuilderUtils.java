/*
 * Copyright 2024 - 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.validator.report;

import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.document.report.builder.SignatureLevelAdjuster;
import ee.openeid.siva.validation.document.report.builder.SignatureValidationDataProcessor;
import ee.openeid.siva.validation.util.ListUtil;
import eu.europa.esig.dss.enumerations.ValidationLevel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.isSignatureLevelAdjustmentEligible;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.isSignatureProfileLta;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenericReportBuilderUtils {

    static final String LTA_VALIDATION_LEVEL_WARNING_TEMPLATE =
            "LTA archive timestamps have no effect on the validation result of the signature at the %s validation level.";

    public static List<SignatureValidationDataProcessor<String>> createSignatureValidationDataAdjusters(ReportBuilderData reportData) {
        return Stream.of(
                createSignatureLevelAdjusterIfRequired(reportData),
                createValidationLevelWarnerIfRequired(reportData)
        )
                .filter(Objects::nonNull)
                .toList();
    }

    static SignatureValidationDataProcessor<String> createSignatureLevelAdjusterIfRequired(ReportBuilderData reportData) {
        if (isSignatureLevelAdjustmentEligible(reportData.getPolicy().getName())) {
            return createSignatureLevelAdjuster(new DssDetailedReportWrapper(reportData.getDssReports()));
        } else {
            return null;
        }
    }

    static SignatureLevelAdjuster<String> createSignatureLevelAdjuster(DssDetailedReportWrapper detailedReportWrapper) {
        return SignatureLevelAdjuster.<String>builder()
                .certificateQualificationResolver(detailedReportWrapper::getSigningCertificateQualification)
                .signatureQualificationAdjustmentLister(createSignatureLevelAdjustmentListener(detailedReportWrapper))
                .build();
    }

    static BiConsumer<String, SignatureLevelAdjuster.Event> createSignatureLevelAdjustmentListener(
            DssDetailedReportWrapper detailedReportWrapper
    ) {
        return (signatureId, event) -> {
            log.info(
                    "The reported qualification level of signature '{}' has been re-evaluated from {} to {}",
                    signatureId,
                    event.getOldSignatureQualification(),
                    event.getNewSignatureQualification()
            );
            Optional
                    .ofNullable(detailedReportWrapper.getValidationSignatureQualification(signatureId))
                    .ifPresent(q -> q.setSignatureQualification(event.getNewSignatureQualification()));
        };
    }

    static SignatureValidationDataProcessor<String> createValidationLevelWarnerIfRequired(ReportBuilderData reportData) {
        if (reportData.getValidationLevel() != ValidationLevel.ARCHIVAL_DATA) {
            return createValidationLevelWarnerForLta(reportData.getValidationLevel());
        } else {
            return null;
        }
    }

    static SignatureValidationDataProcessor<String> createValidationLevelWarnerForLta(ValidationLevel validationLevel) {
        return (validationData, signatureId) -> {
            if (isSignatureProfileLta(validationData)) {
                String warningMessage = String.format(LTA_VALIDATION_LEVEL_WARNING_TEMPLATE, validationLevel);
                ListUtil.getOrCreateList(validationData::getWarnings, validationData::setWarnings)
                        .add(ReportBuilderUtils.createValidationWarning(warningMessage));
            }
        };
    }

}
