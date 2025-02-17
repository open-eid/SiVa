/*
 * Copyright 2017 - 2025 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.Policy;
import ee.openeid.siva.validation.document.report.Scope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.ValidatedDocument;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.Warning;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestMatcher;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignatureScope;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import eu.europa.esig.dss.spi.DSSASN1Utils;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.TimeZone;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ReportBuilderUtils {

    public static final String ERROR_MSG_FORMAT_NOT_FOUND = "The expected format is not found! Expected minimal format LT";
    public static final String ERROR_MSG_INVALID_SIGNATURE_FORMAT_FOR_BDOC_POLICY = "Invalid signature format for BDOC policy";
    public static final String WARNING_MSG_DATAFILE_NOT_COVERED_BY_TS = "The time-stamp token does not cover container datafile!";

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String GREENWICH_MEAN_TIME = "Etc/GMT";
    private static final String DIGEST_ALGO = "SHA256";
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
        if (StringUtils.isBlank(filename) && !reportSignatureEnabled) {
            return null;
        }
        ValidatedDocument validatedDocument = new ValidatedDocument();
        if (reportSignatureEnabled) {
            String documentHash;
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                documentHash = Base64.getEncoder().encodeToString(messageDigest.digest(document));
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException(e);
            }

            validatedDocument.setFileHash(documentHash);
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
                    if (SignatureQualification.ADESEAL_QC.name().equals(signatureLevel)
                            || SignatureQualification.QESIG.name().equals(signatureLevel)
                            || SignatureQualification.QESEAL.name().equals(signatureLevel)) {
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

    public static boolean isSignatureLevelAdjustmentEligible(String policyName) {
        return QES_POLICY.equals(policyName);
    }

    public static String getValidationTime() {
        return getFormattedTimeValue(ZonedDateTime.now(ZoneId.of("GMT")));
    }

    public static Warning createValidationWarning(String content) {
        Warning warning = new Warning();
        warning.setContent(content);
        return warning;
    }

    private static Error getSignatureLevelNotAcceptedError() {
        Error error = new Error();
        error.setContent(SIGNATURE_LEVEL_ERROR);
        return error;
    }

    private static Warning getSignatureLevelWarning() {
        return createValidationWarning(SIGNATURE_LEVEL_WARNING);
    }

    private static String getFormattedTimeValue(ZonedDateTime zonedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
        return zonedDateTime.format(formatter);
    }

    public static String parseTimeAssertionMessageImprint(TimestampWrapper timestamp) throws IOException {
        if (timestamp == null || !timestamp.isMessageImprintDataFound() || !timestamp.isMessageImprintDataIntact()) {
            return "";
        }
        XmlDigestMatcher messageImprint = timestamp.getMessageImprint();
        AlgorithmIdentifier algorithm = DSSASN1Utils.getAlgorithmIdentifier(messageImprint.getDigestMethod());
        byte[] nonce = new MessageImprint(algorithm, messageImprint.getDigestValue()).getEncoded();
        return StringUtils.defaultString(org.apache.tomcat.util.codec.binary.Base64.encodeBase64String(nonce));
    }

    public static Scope parseScope(XmlSignatureScope dssScope, List<Datafile> datafiles) {
        Scope scope = new Scope();

        scope.setContent(emptyWhenNull(dssScope.getDescription()));
        scope.setName(emptyWhenNull(dssScope.getName()));
        if (dssScope.getScope() != null) {
            scope.setScope(emptyWhenNull(dssScope.getScope().name()));
        }
        if (CollectionUtils.isNotEmpty(datafiles)) {
            datafiles.stream()
                .filter(datafile -> datafile.getFilename().equals(dssScope.getName()))
                .findFirst()
                .ifPresent(dataFile -> {
                    scope.setHash(dataFile.getHash());
                    scope.setHashAlgo(dataFile.getHashAlgo().toUpperCase());
                });
        }
        return scope;
    }
}
