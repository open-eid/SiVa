/*
 * Copyright 2020 - 2023 Riigi Infosüsteemi Amet
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

import ee.openeid.validation.service.generic.validator.TokenUtils;
import ee.openeid.validation.service.generic.validator.report.DssSimpleReportWrapper;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.RevocationType;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.validation.reports.Reports;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RevocationFreshnessValidator {

    private static final Duration REVOCATION_FRESHNESS_OK_LIMIT = Duration.ofMinutes(15);
    private static final Duration REVOCATION_FRESHNESS_WARNING_LIMIT = Duration.ofHours(24);

    private static final String REVOCATION_FRESHNESS_FAULT = "The revocation information is not considered as 'fresh'.";
    private static final String TIMESTAMP_OCSP_ORDER_FAULT = "OCSP response production time is before timestamp time";

    @NonNull
    private final Reports reports;

    public void validate() {
        reports.getDiagnosticData().getSignatures().forEach(this::validate);
    }

    private void validate(SignatureWrapper signatureWrapper) {
        CertificateWrapper signingCertificateWrapper = signatureWrapper.getSigningCertificate();
        if (signingCertificateWrapper == null) {
            return; // No signing certificate present in signature
        }

        Date timestampProductionTime = findEarliestValidSignatureTimestampProductionTime(signatureWrapper);
        if (timestampProductionTime == null) {
            return; // No valid timestamp present for this signature
        }

        Predicate<Date> timestampNotAfterPredicate = notAfterPredicateWithSamePrecision(timestampProductionTime);

        List<Date> validCrlNextUpdateDates = findValidCRLNextUpdateDates(signingCertificateWrapper);
        if (validCrlNextUpdateDates.stream().anyMatch(timestampNotAfterPredicate)) {
            return; // At least one CRL is present, for which timestamp production time is not after CRL next update time
        }

        List<Date> validOcspProducedAtDates = findValidOCSPProducedAtDates(signingCertificateWrapper);
        if (CollectionUtils.isEmpty(validCrlNextUpdateDates) && CollectionUtils.isEmpty(validOcspProducedAtDates)) {
            return; // No valid CRL-s nor OCSP responses present for this signature, no more checks to do
        }

        Date earliestPostTimestampOcspProducedAtDate = findEarliestTime(validOcspProducedAtDates, timestampNotAfterPredicate);
        if (earliestPostTimestampOcspProducedAtDate != null) {
            Duration timestampOcspDifference = Duration.between(timestampProductionTime.toInstant(), earliestPostTimestampOcspProducedAtDate.toInstant());
            if (timestampOcspDifference.compareTo(REVOCATION_FRESHNESS_OK_LIMIT) <= 0) {
                return; // OCSP is not taken later than the OK limit after timestamp
            } else if (timestampOcspDifference.compareTo(REVOCATION_FRESHNESS_WARNING_LIMIT) <= 0) {
                addSignatureAdESWarning(signatureWrapper.getId(), REVOCATION_FRESHNESS_FAULT);
                return; // OCSP is not taken later than the WARNING limit after timestamp
            }
        } else if (CollectionUtils.isNotEmpty(validOcspProducedAtDates)) {
            addSignatureAdESError(signatureWrapper.getId(), TIMESTAMP_OCSP_ORDER_FAULT);
            return; // There are valid OCSP responses, but all of them are taken before the timestamp
        }

        // Every other case is considered as revocation freshness error
        addSignatureAdESError(signatureWrapper.getId(), REVOCATION_FRESHNESS_FAULT);
    }

    private static Date findEarliestValidSignatureTimestampProductionTime(SignatureWrapper signatureWrapper) {
        return signatureWrapper.getTimestampList().stream()
                .filter(timestamp -> TimestampType.SIGNATURE_TIMESTAMP.equals(timestamp.getType()))
                .filter(TokenUtils::isTokenSignatureIntactAndSignatureValidAndTrustedChain)
                .filter(TokenUtils::isTimestampTokenMessageImprintDataFoundAndMessageImprintDataIntact)
                .flatMap(timestamp -> Optional.ofNullable(timestamp.getProductionTime()).stream())
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private static List<Date> findValidCRLNextUpdateDates(CertificateWrapper certificateWrapper) {
        return certificateWrapper.getCertificateRevocationData().stream()
                .filter(revocation -> RevocationType.CRL.equals(revocation.getRevocationType()))
                .filter(TokenUtils::isTokenSignatureIntactAndSignatureValidAndTrustedChain)
                .filter(TokenUtils::isRevocationTokenForCertificateAndCertificateStatusGood)
                .flatMap(crl -> Optional.ofNullable(crl.getNextUpdate()).stream())
                .collect(Collectors.toList());
    }

    private static List<Date> findValidOCSPProducedAtDates(CertificateWrapper certificateWrapper) {
        return certificateWrapper.getCertificateRevocationData().stream()
                .filter(revocation -> RevocationType.OCSP.equals(revocation.getRevocationType()))
                .filter(TokenUtils::isTokenSignatureIntactAndSignatureValidAndTrustedChain)
                .filter(TokenUtils::isRevocationTokenForCertificateAndCertificateStatusGood)
                .flatMap(ocsp -> Optional.ofNullable(ocsp.getProductionDate()).stream())
                .collect(Collectors.toList());
    }

    private static Predicate<Date> notAfterPredicateWithSamePrecision(final Date referenceTime) {
        return timeToCompare -> {
            Instant referenceInstant = referenceTime.toInstant();
            Instant instantToCompare = timeToCompare.toInstant();

            boolean referenceInstantHighPrecision = referenceInstant.getNano() != 0L;
            boolean instantToCompareHighPrecision = instantToCompare.getNano() != 0L;

            if (referenceInstantHighPrecision && !instantToCompareHighPrecision) {
                referenceInstant = referenceInstant.truncatedTo(ChronoUnit.SECONDS);
            }
            if (instantToCompareHighPrecision && !referenceInstantHighPrecision) {
                instantToCompare = instantToCompare.truncatedTo(ChronoUnit.SECONDS);
            }

            return !referenceInstant.isAfter(instantToCompare);
        };
    }

    private static Date findEarliestTime(List<Date> dateList, Predicate<Date> datePredicate) {
        return dateList.stream()
                .filter(datePredicate)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private void addSignatureAdESWarning(String signatureId, String warning) {
        new DssSimpleReportWrapper(reports).getSignatureAdESValidationXmlDetails(signatureId)
                .getWarning().add(DssSimpleReportWrapper.createXmlMessage(warning));
    }

    private void addSignatureAdESError(String signatureId, String error) {
        new DssSimpleReportWrapper(reports).getSignatureAdESValidationXmlDetails(signatureId)
                .getError().add(DssSimpleReportWrapper.createXmlMessage(error));
    }

}
