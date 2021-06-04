/*
 * Copyright 2020 - 2021 Riigi Infosüsteemi Amet
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

import eu.europa.esig.dss.diagnostic.CertificateRevocationWrapper;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.RevocationWrapper;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.TokenProxy;
import eu.europa.esig.dss.enumerations.CertificateStatus;
import eu.europa.esig.dss.enumerations.RevocationType;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.validation.reports.Reports;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Duration;
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

        Predicate<Date> timestampNotAfterPredicate = Predicate.not(timestampProductionTime::after);

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
                addSignatureWarning(signatureWrapper.getId(), REVOCATION_FRESHNESS_FAULT);
                return; // OCSP is not taken later than the WARNING limit after timestamp
            }
        } else if (CollectionUtils.isNotEmpty(validOcspProducedAtDates)) {
            addSignatureError(signatureWrapper.getId(), TIMESTAMP_OCSP_ORDER_FAULT);
            return; // There are valid OCSP responses, but all of them are taken before the timestamp
        }

        // Every other case is considered as revocation freshness error
        addSignatureError(signatureWrapper.getId(), REVOCATION_FRESHNESS_FAULT);
    }

    private static Date findEarliestValidSignatureTimestampProductionTime(SignatureWrapper signatureWrapper) {
        return signatureWrapper.getTimestampList().stream()
                .filter(timestamp -> TimestampType.SIGNATURE_TIMESTAMP.equals(timestamp.getType()))
                .filter(RevocationFreshnessValidator::isTokenSignatureIntactAndSignatureValidAndTrustedChain)
                .filter(timestamp -> timestamp.isMessageImprintDataFound() && timestamp.isMessageImprintDataIntact())
                .flatMap(timestamp -> Optional.ofNullable(timestamp.getProductionTime()).stream())
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private static List<Date> findValidCRLNextUpdateDates(CertificateWrapper certificateWrapper) {
        return certificateWrapper.getCertificateRevocationData().stream()
                .filter(revocation -> RevocationType.CRL.equals(revocation.getRevocationType()))
                .filter(RevocationFreshnessValidator::isTokenSignatureIntactAndSignatureValidAndTrustedChain)
                .filter(RevocationFreshnessValidator::isRevocationForCertificateAndCertificateStatusGood)
                .flatMap(crl -> Optional.ofNullable(crl.getNextUpdate()).stream())
                .collect(Collectors.toList());
    }

    private static List<Date> findValidOCSPProducedAtDates(CertificateWrapper certificateWrapper) {
        return certificateWrapper.getCertificateRevocationData().stream()
                .filter(revocation -> RevocationType.OCSP.equals(revocation.getRevocationType()))
                .filter(RevocationFreshnessValidator::isTokenSignatureIntactAndSignatureValidAndTrustedChain)
                .filter(RevocationFreshnessValidator::isRevocationForCertificateAndCertificateStatusGood)
                .flatMap(ocsp -> Optional.ofNullable(ocsp.getProductionDate()).stream())
                .collect(Collectors.toList());
    }

    private static boolean isTokenSignatureIntactAndSignatureValidAndTrustedChain(TokenProxy token) {
        return token.isSignatureIntact() && token.isSignatureValid() && (token.isTrustedChain() || Optional
                .ofNullable(token.getSigningCertificate()).map(TokenProxy::isTrustedChain).orElse(false)
        );
    }

    private static boolean isRevocationForCertificateAndCertificateStatusGood(RevocationWrapper revocation) {
        if (revocation instanceof CertificateRevocationWrapper) {
            CertificateRevocationWrapper certificateRevocation = (CertificateRevocationWrapper) revocation;
            return (certificateRevocation.getStatus() == CertificateStatus.GOOD);
        } else {
            return false;
        }
    }

    private static Date findEarliestTime(List<Date> dateList, Predicate<Date> datePredicate) {
        return dateList.stream()
                .filter(datePredicate)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private void addSignatureWarning(String signatureId, String warning) {
        reports.getSimpleReport().getWarnings(signatureId).add(warning);
    }

    private void addSignatureError(String signatureId, String error) {
        reports.getSimpleReport().getErrors(signatureId).add(error);
    }

}
