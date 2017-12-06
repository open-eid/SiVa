/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import eu.europa.esig.dss.validation.SignatureQualification;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ReportBuilderUtils {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String GREENWICH_MEAN_TIME = "Etc/GMT";
    private static final String DIGEST_ALGO = "SHA-256";
    private static final String UNKNOWN_VALUE = "NA";
    private static final String QES_POLICY = "POLv4";
    private static final String SIGNATURE_LEVEL_ERROR = "Signature/seal level do not meet the minimal level required by applied policy";
    private static final String SIGNATURE_LEVEL_WARNING = "The signature is not in the Qualified Electronic Signature level";

    public static String emptyWhenNull(String value) {
        return value != null ? value : valueNotPresent();
    }

    public static String valueNotPresent() {
        return StringUtils.EMPTY;
    }

    public static String valueNotKnown() {
        return UNKNOWN_VALUE;
    }

    public static SimpleDateFormat getDateFormatterWithGMTZone() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone(GREENWICH_MEAN_TIME));
        return sdf;
    }

    public static Policy createReportPolicy(ValidationPolicy validationPolicy) {
        Policy reportPolicy = new Policy();
        reportPolicy.setPolicyName(validationPolicy.getName());
        reportPolicy.setPolicyDescription(validationPolicy.getDescription());
        reportPolicy.setPolicyUrl(validationPolicy.getUrl());
        return reportPolicy;
    }

    public static ValidatedDocument createValidatedDocument(boolean reportSignatureEnabled, String filename, byte[] document) {
        ValidatedDocument validatedDocument = new ValidatedDocument();
        if (reportSignatureEnabled) {
            String documentHash;
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
                documentHash = Hex.toHexString(messageDigest.digest(document)).toUpperCase();
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException(e);
            }

            validatedDocument.setFileHashInHex(documentHash);
            validatedDocument.setHashAlgo(DIGEST_ALGO);
        }
        validatedDocument.setFilename(filename);
        return validatedDocument;
    }

    public static void processSignatureIndications(ValidationConclusion validationConclusion, String policyName) {
        if (QES_POLICY.equals(policyName)) {
            for (SignatureValidationData signature : validationConclusion.getSignatures()) {
                if (SignatureValidationData.Indication.TOTAL_PASSED.toString().equals(signature.getIndication())) {
                    String signatureLevel = signature.getSignatureLevel();
                    if (SignatureQualification.ADESEAL_QC.name().equals(signatureLevel) || SignatureQualification.QES.name().equals(signatureLevel)
                            || SignatureQualification.QESIG.name().equals(signatureLevel) || SignatureQualification.QESEAL.name().equals(signatureLevel)) {
                        continue;
                    } else if (SignatureQualification.ADESIG_QC.name().equals(signatureLevel)) {
                        signature.getWarnings().add(getSignatureLevelWarning());
                        continue;
                    }
                    signature.setIndication(SignatureValidationData.Indication.TOTAL_FAILED);
                    signature.getErrors().add(getSignatureLevelNotAcceptedError());
                    validationConclusion.setValidSignaturesCount(validationConclusion.getValidSignaturesCount() - 1);
                }
            }
        }
    }

    public static String getValidationTime() {
        return getFormattedTimeValue(ZonedDateTime.now(ZoneId.of("GMT")));
    }

    private static Error getSignatureLevelNotAcceptedError() {
        Error error = new Error();
        error.setContent(SIGNATURE_LEVEL_ERROR);
        return error;
    }

    private static Warning getSignatureLevelWarning() {
        Warning warning = new Warning();
        warning.setContent(SIGNATURE_LEVEL_WARNING);
        return warning;
    }

    private static String getFormattedTimeValue(ZonedDateTime zonedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
        return zonedDateTime.format(formatter);
    }
}
