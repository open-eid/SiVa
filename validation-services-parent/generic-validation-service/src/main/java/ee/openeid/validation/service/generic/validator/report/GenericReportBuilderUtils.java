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

package ee.openeid.validation.service.generic.validator.report;

import ee.openeid.siva.validation.document.report.builder.SignatureLevelAdjuster;
import ee.openeid.siva.validation.document.report.builder.SignatureValidationDataProcessor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.BiConsumer;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.isSignatureLevelAdjustmentEligible;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenericReportBuilderUtils {

    public static final SignatureValidationDataProcessor<String> NO_OP_SIGNATURE_LEVEL_ADJUSTER = (data, id) -> {};

    public static SignatureValidationDataProcessor<String> createSignatureLevelAdjuster(ReportBuilderData reportData) {
        if (isSignatureLevelAdjustmentEligible(reportData.getPolicy().getName())) {
            return createSignatureLevelAdjuster(new DssDetailedReportWrapper(reportData.getDssReports()));
        } else {
            return NO_OP_SIGNATURE_LEVEL_ADJUSTER;
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

}
