/*
 * Copyright 2016 - 2022 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Certificate;
import ee.openeid.siva.validation.document.report.CertificateType;
import ee.openeid.siva.validation.document.report.DetailedReport;
import ee.openeid.siva.validation.document.report.DiagnosticReport;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.Info;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureProductionPlace;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SignerRole;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.SubjectDistinguishedName;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.Warning;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.siva.validation.util.CertUtil;
import ee.openeid.siva.validation.util.DistinguishedNameUtil;
import ee.openeid.siva.validation.util.SubjectDNParser;
import ee.openeid.validation.service.generic.validator.TokenUtils;
import eu.europa.esig.dss.diagnostic.AbstractTokenProxy;
import eu.europa.esig.dss.diagnostic.CertificateRevocationWrapper;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.RelatedRevocationWrapper;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlRevocation;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignature;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignatureScope;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignerRole;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.MaskGenerationFunction;
import eu.europa.esig.dss.enumerations.RevocationType;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.simplereport.jaxb.XmlDetails;
import eu.europa.esig.dss.simplereport.jaxb.XmlMessage;
import eu.europa.esig.dss.simplereport.jaxb.XmlToken;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.reports.AbstractReports;
import eu.europa.esig.dss.validation.timestamp.TimestampToken;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.FORMAT_NOT_FOUND;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.createReportPolicy;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.getValidationTime;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.processSignatureIndications;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.valueNotKnown;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.valueNotPresent;

public class GenericValidationReportBuilder {

    protected static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GenericValidationReportBuilder.class);

    private static final String LT_SIGNATURE_FORMAT_SUFFIX = "LT";
    private static final String BASELINE_SIGNATURE_FORMAT_SUFFIX = "B";
    private static final String LT_TM_XAdES_SIGNATURE_FORMAT = "XAdES_BASELINE_LT_TM";
    private static final String LT_XAdES_SIGNATURE_FORMAT = "XAdES-BASELINE-LT";
    private static final String TM_POLICY_OID = "1.3.6.1.4.1.10015.1000.3.2.1";

    private final eu.europa.esig.dss.validation.reports.Reports dssReports;
    private final ValidationDocument validationDocument;
    private final ConstraintDefinedPolicy validationPolicy;
    private final ValidationLevel validationLevel;
    private final boolean isReportSignatureEnabled;
    private final TrustedListsCertificateSource trustedListsCertificateSource;
    private final List<AdvancedSignature> signatures;

    private List<CertificateToken> usedCertificates;

    public GenericValidationReportBuilder(ReportBuilderData reportData) {
        this.dssReports = reportData.getDssReports();
        this.validationDocument = reportData.getValidationDocument();
        this.validationPolicy = reportData.getPolicy();
        this.validationLevel = reportData.getValidationLevel();
        this.isReportSignatureEnabled = reportData.isReportSignatureEnabled();
        this.trustedListsCertificateSource = reportData.getTrustedListsCertificateSource();
        this.signatures = reportData.getSignatures();
    }

    public Reports build() {
        collectUsedCertificates();
        ValidationConclusion validationConclusion = getValidationConclusion();
        processSignatureIndications(validationConclusion, validationPolicy.getName());

        SimpleReport simpleReport = new SimpleReport(validationConclusion);
        validationConclusion.setValidationLevel(validationLevel.name());
        DetailedReport detailedReport = new DetailedReport(validationConclusion, dssReports.getDetailedReportJaxb());
        DiagnosticReport diagnosticReport = new DiagnosticReport(validationConclusion, dssReports.getDiagnosticDataJaxb());

        return new Reports(simpleReport, detailedReport, diagnosticReport);
    }

    private void collectUsedCertificates() {
        usedCertificates = dssReports.getDiagnosticDataJaxb().getUsedCertificates().stream()
                .map(usedCertificate -> {
                    Optional<CertificateToken> certificateToken = trustedListsCertificateSource.getCertificates().stream()
                            .filter(c -> c.getDSSIdAsString().equals(usedCertificate.getId()))
                            .findFirst();
                    if (certificateToken.isPresent()) {
                        return certificateToken.get();
                    }
                    for (AdvancedSignature advancedSignature : signatures) {
                        List<CertificateToken> certificates = new ArrayList<>(advancedSignature.getCertificates());
                        for (TimestampToken timestampToken : advancedSignature.getAllTimestamps()) {
                            certificates.addAll(timestampToken.getCertificates());
                        }
                        Optional<CertificateToken> optionalCertSource = certificates.stream()
                                .filter(cert -> cert.getDSSIdAsString().equals(usedCertificate.getId())).findFirst();

                        if (optionalCertSource.isPresent()) {
                            return optionalCertSource.get();
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ValidationConclusion getValidationConclusion() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setPolicy(createReportPolicy(validationPolicy));
        validationConclusion.setValidationTime(getValidationTime());
        ASiCContainerType containerType = getContainerType();
        if (containerType != null) {
            validationConclusion.setSignatureForm(containerType.toString());
        }
        validationConclusion.setValidationWarnings(Collections.emptyList());
        validationConclusion.setSignatures(buildSignatureValidationDataList());
        validationConclusion.setSignaturesCount(validationConclusion.getSignatures().size());
        validationConclusion.setValidatedDocument(ReportBuilderUtils.createValidatedDocument(isReportSignatureEnabled, validationDocument.getName(), validationDocument.getBytes()));
        validationConclusion.setValidSignaturesCount((int) validationConclusion.getSignatures()
                .stream()
                .filter(vd -> StringUtils.equals(vd.getIndication(), SignatureValidationData.Indication.TOTAL_PASSED.toString())).count());
        return validationConclusion;
    }

    private List<SignatureValidationData> buildSignatureValidationDataList() {
        return dssReports.getSimpleReport().getSignatureIdList()
                .stream()
                .map(this::buildSignatureValidationData)
                .collect(Collectors.toList());
    }

    private ASiCContainerType getContainerType() {
        if (dssReports.getDiagnosticData().getContainerInfo() != null)
            return dssReports.getDiagnosticData().getContainerInfo().getContainerType();
        return null;
    }

    private SignatureValidationData buildSignatureValidationData(String signatureId) {
        SignatureValidationData signatureValidationData = new SignatureValidationData();
        signatureValidationData.setId(getSignatureId(signatureId));
        signatureValidationData.setSignatureFormat(changeAndValidateSignatureFormat(dssReports.getSimpleReport().getSignatureFormat(signatureId).toString(), signatureId));
        signatureValidationData.setSignatureMethod(parseSignatureMethod(signatureId));
        signatureValidationData.setSignatureLevel(dssReports.getSimpleReport().getSignatureQualification(signatureId).name());
        signatureValidationData.setSignedBy(parseSignedBy(signatureId));
        signatureValidationData.setSubjectDistinguishedName(parseSubjectDistinguishedName(signatureId));
        signatureValidationData.setClaimedSigningTime(parseClaimedSigningTime(signatureId));
        signatureValidationData.setSignatureScopes(parseSignatureScopes(signatureId));
        signatureValidationData.setErrors(parseSignatureErrors(signatureId));
        signatureValidationData.setWarnings(parseSignatureWarnings(signatureId));
        signatureValidationData.setInfo(parseSignatureInfo(signatureValidationData.getSignatureFormat(), signatureId));
        signatureValidationData.setCountryCode(getCountryCode(signatureId));
        signatureValidationData.setIndication(parseIndication(signatureId, signatureValidationData.getErrors()));
        signatureValidationData.setSubIndication(parseSubIndication(signatureId, signatureValidationData.getErrors()));
        signatureValidationData.setCertificates(getCertificateList(signatureId));
        return signatureValidationData;
    }

    private List<Certificate> getCertificateList(String signatureId) {
        List<Certificate> certificateList = new ArrayList<>();
        SignatureWrapper signatureWrapper = dssReports.getDiagnosticData().getSignatureById(signatureId);

        Certificate archiveTimestampCertificate = getArchiveTimestampCertificate(signatureWrapper);
        if (archiveTimestampCertificate != null) {
            certificateList.add(archiveTimestampCertificate);
        }

        Certificate signatureTimestamp = getSignatureTimestampCertificate(signatureWrapper);
        if (signatureTimestamp != null) {
            certificateList.add(signatureTimestamp);
        }

        Certificate signingCertificate = getSigningCertificate(signatureWrapper);
        if (signingCertificate != null) {
            certificateList.add(signingCertificate);
        }

        Certificate revocationCertificate = getRevocationCertificate(signatureWrapper);
        if (revocationCertificate != null) {
            certificateList.add(revocationCertificate);
        }

        return certificateList;
    }

    private Certificate getArchiveTimestampCertificate(SignatureWrapper signatureWrapper) {
        List<TimestampWrapper> archiveTimestamps = signatureWrapper.getTimestampListByType(TimestampType.ARCHIVE_TIMESTAMP);
        if (CollectionUtils.isNotEmpty(archiveTimestamps)) {
            TimestampWrapper latestBestFittingTimestamp = getLatestBestFittingTimestamp(archiveTimestamps);
            if (latestBestFittingTimestamp.getSigningCertificate() != null) {
                String archiveTimestampCertId = latestBestFittingTimestamp.getSigningCertificate().getId();
                return getCertificate(archiveTimestampCertId, CertificateType.ARCHIVE_TIMESTAMP);
            }
        }
        return null;
    }

    private Certificate getSignatureTimestampCertificate(SignatureWrapper signatureWrapper) {
        List<TimestampWrapper> signatureTimestamps = signatureWrapper.getTimestampListByType(TimestampType.SIGNATURE_TIMESTAMP);
        if (CollectionUtils.isNotEmpty(signatureTimestamps)) {
            TimestampWrapper earliestBestFittingTimestamp = getEarliestBestFittingTimestamp(signatureTimestamps);
            if (earliestBestFittingTimestamp.getSigningCertificate() != null) {
                String timestampCertId = earliestBestFittingTimestamp.getSigningCertificate().getId();
                return getCertificate(timestampCertId, CertificateType.SIGNATURE_TIMESTAMP);
            }
        }
        return null;
    }

    private Certificate getSigningCertificate(SignatureWrapper signatureWrapper) {
        CertificateWrapper signingCertificate = signatureWrapper.getSigningCertificate();
        if (signingCertificate != null) {
            String signingCertId = signingCertificate.getId();
            return getCertificate(signingCertId, CertificateType.SIGNING);
        }
        return null;
    }

    private Certificate getRevocationCertificate(SignatureWrapper signatureWrapper) {
        List<CertificateRevocationWrapper> certificateRevocations = Optional
                .ofNullable(signatureWrapper.getSigningCertificate())
                .map(CertificateWrapper::getCertificateRevocationData)
                .orElse(null);

        if (CollectionUtils.isNotEmpty(certificateRevocations)) {
            CertificateRevocationWrapper bestFittingRevocation = getBestFittingRevocation(certificateRevocations);
            if (bestFittingRevocation.getSigningCertificate() != null) {
                String revocationId = bestFittingRevocation.getSigningCertificate().getId();
                return getCertificate(revocationId, CertificateType.REVOCATION);
            }
        }
        return null;
    }

    public static TimestampWrapper getEarliestBestFittingTimestamp(List<TimestampWrapper> timestamps) {
        // Find the earliest timestamp, but prefer valid timestamps over invalid ones
        return Collections.min(timestamps, Comparator
                .comparing(TokenUtils::isTimestampTokenValid).reversed()
                .thenComparing(TimestampWrapper::getProductionTime)
        );
    }

    protected static TimestampWrapper getLatestBestFittingTimestamp(List<TimestampWrapper> timestamps) {
        // Find the latest timestamp, but prefer valid timestamps over invalid ones
        return Collections.max(timestamps, Comparator
                .comparing(TokenUtils::isTimestampTokenValid)
                .thenComparing(TimestampWrapper::getProductionTime)
        );
    }

    protected static CertificateRevocationWrapper getBestFittingRevocation(List<CertificateRevocationWrapper> revocations) {
        // Currently just return any revocation, but prefer firstly valid revocations and then the ones with GOOD certificate status
        return Collections.max(revocations, Comparator
                .comparing((Function<CertificateRevocationWrapper, Boolean>) TokenUtils::isTokenSignatureIntactAndSignatureValidAndTrustedChain)
                .thenComparing(TokenUtils::isRevocationTokenForCertificateAndCertificateStatusGood)
        );
    }

    private Certificate getCertificate(String certificateId, CertificateType type) {
        Optional<CertificateToken> certificateToken = getCertificateTokenById(certificateId);
        if (certificateToken.isEmpty()) {
            return null;
        }
        Certificate certificate = new Certificate();
        X509Certificate x509Certificate = certificateToken.get().getCertificate();
        certificate.setCommonName(CertUtil.getCommonName(x509Certificate));
        certificate.setContent(CertUtil.encodeCertificateToBase64(x509Certificate));
        certificate.setIssuer(getIssuerCertificate(x509Certificate));
        certificate.setType(type);
        return certificate;
    }

    private Certificate getIssuerCertificate(X509Certificate x509Certificate) {
        Optional<CertificateToken> issuerCert = usedCertificates.stream()
                .filter(certificateToken -> certificateToken != null
                        && !certificateToken.isSelfSigned()
                        && certificateToken.getSubject().getPrincipal() != null
                        && certificateToken.getSubject().getPrincipal().equals(x509Certificate.getIssuerX500Principal()))
                .findFirst();
        if (issuerCert.isPresent()) {
            Certificate certificate = new Certificate();
            certificate.setCommonName(CertUtil.getCommonName(issuerCert.get().getCertificate()));
            certificate.setContent(CertUtil.encodeCertificateToBase64(issuerCert.get().getCertificate()));
            certificate.setIssuer(getIssuerCertificate(issuerCert.get().getCertificate()));
            return certificate;
        }
        return null;
    }

    private Optional<CertificateToken> getCertificateTokenById(String id) {
        return usedCertificates.stream()
                .filter(certificateToken -> certificateToken.getDSSIdAsString().equals(id))
                .findFirst();
    }

    private String getSignatureId(String signatureId) {
        String daIdentifier = dssReports.getDiagnosticData().getSignatureById(signatureId).getDAIdentifier();
        if (StringUtils.isNotBlank(daIdentifier)) {
            return daIdentifier;
        }
        return signatureId;
    }

    private String parseSignedBy(String signatureId) {
        return Optional.ofNullable(dssReports)
                .map(AbstractReports::getDiagnosticData)
                .map(diagnosticData -> diagnosticData.getSignatureById(signatureId))
                .map(AbstractTokenProxy::getSigningCertificate)
                .map(signingCertificate -> Optional
                        .ofNullable(DistinguishedNameUtil.getSurnameAndGivenNameAndSerialNumber(
                                signingCertificate.getSurname(),
                                signingCertificate.getGivenName(),
                                signingCertificate.getSubjectSerialNumber()
                        ))
                        .orElseGet(signingCertificate::getCommonName)
                )
                .orElseGet(ReportBuilderUtils::valueNotPresent);
    }

    private SubjectDistinguishedName parseSubjectDistinguishedName(String signatureId) {
        CertificateWrapper signingCertificate = dssReports.getDiagnosticData().getSignatureById(signatureId).getSigningCertificate();

        // Due to invalid signature
        if (signingCertificate == null) {
            return SubjectDistinguishedName.builder()
                    .serialNumber("")
                    .commonName("")
                    .build();
        }

        return SubjectDistinguishedName.builder()
                .serialNumber(SubjectDNParser.parse(signingCertificate.getCertificateDN(), SubjectDNParser.RDN.SERIALNUMBER))
                .commonName(signingCertificate.getCommonName())
                .build();
    }

    private String changeAndValidateSignatureFormat(String signatureFormat, String signatureId) {
        if (TM_POLICY_OID.equals(dssReports.getDiagnosticData().getSignatureById(signatureId).getPolicyId())) {
            signatureFormat = signatureFormat.replace(LT_XAdES_SIGNATURE_FORMAT, LT_TM_XAdES_SIGNATURE_FORMAT);
        }
        if (isInvalidFormat(signatureFormat, signatureId)) {
            signatureFormat = signatureFormat.replace(LT_SIGNATURE_FORMAT_SUFFIX, BASELINE_SIGNATURE_FORMAT_SUFFIX);
            new DssSimpleReportWrapper(dssReports).getSignatureAdESValidationXmlDetails(signatureId)
                    .getError().add(DssSimpleReportWrapper.createXmlMessage(FORMAT_NOT_FOUND));
        }
        signatureFormat = signatureFormat.replace("-", "_");
        return signatureFormat;
    }

    private boolean isInvalidFormat(String signatureFormat, String signatureId) {
        return Indication.TOTAL_PASSED == dssReports.getSimpleReport().getIndication(signatureId)
                && dssReports.getDiagnosticData().getSignatureById(signatureId).getTimestampList().isEmpty()
                && !signatureFormat.equals(LT_TM_XAdES_SIGNATURE_FORMAT);
    }

    private String parseSignatureMethod(String signatureId) {
        DigestAlgorithm digestAlgorithm = dssReports.getDiagnosticData().getSignatureDigestAlgorithm(signatureId);
        EncryptionAlgorithm encryptionAlgorithm = dssReports.getDiagnosticData().getSignatureEncryptionAlgorithm(signatureId);
        MaskGenerationFunction maskGenerationFunction = dssReports.getDiagnosticData().getSignatureMaskGenerationFunction(signatureId);
        SignatureAlgorithm algorithm = SignatureAlgorithm.getAlgorithm(encryptionAlgorithm, digestAlgorithm, maskGenerationFunction);
        return algorithm == null ? "" : StringUtils.defaultString(algorithm.getUri());
    }

    private Info parseSignatureInfo(String signatureFormat, String signatureId) {
        Info info = new Info();
        info.setBestSignatureTime(getBestSignatureTime(signatureFormat, signatureId));
        info.setTimestampCreationTime(getTimestampTime(signatureId));
        info.setOcspResponseCreationTime(getOcspResponseTime(signatureId));
        info.setTimeAssertionMessageImprint(parseTimeAssertionMessageImprint(signatureFormat, signatureId));
        info.setSignerRole(parseSignerRole(signatureId));
        info.setSignatureProductionPlace(parseSignatureProductionPlace(signatureId));
        info.setSigningReason(parseReason(signatureId));
        return info;
    }

    private String formatDate(Date date) {
        return date == null ? null : ReportBuilderUtils.getDateFormatterWithGMTZone().format(date);
    }

    private String getOcspResponseTime(String signatureId) {
        SignatureWrapper signatureWrapper = dssReports.getDiagnosticData().getSignatureById(signatureId);
        List<RelatedRevocationWrapper> revocations = signatureWrapper.foundRevocations().getRelatedRevocationData();
        if (revocations.isEmpty()) {
            return null;
        }
        RelatedRevocationWrapper revocationWrapper = revocations.get(0);
        return revocationWrapper.getRevocationType() == RevocationType.CRL ? null : formatDate(revocationWrapper.getProductionDate());
    }

    private String getTimestampTime(String signatureId) {
        TimestampWrapper timestamp = getBestTimestamp(signatureId);
        return timestamp == null ? null : formatDate(timestamp.getProductionTime());
    }

    private String getBestSignatureTime(String signatureFormat, String signatureId) {
        if (signatureFormat.equals(LT_TM_XAdES_SIGNATURE_FORMAT)) {
            return getOcspResponseTime(signatureId);
        } else {
            return getTimestampTime(signatureId);
        }
    }

    private TimestampWrapper getBestTimestamp(String signatureId) {
        List<TimestampWrapper> timestamps = dssReports.getDiagnosticData().getSignatureById(signatureId)
                .getTimestampListByType(TimestampType.SIGNATURE_TIMESTAMP);
        return timestamps.isEmpty() ? null : Collections.min(timestamps, Comparator.comparing(TimestampWrapper::getProductionTime));
    }

    private String parseTimeAssertionMessageImprint(String signatureFormat, String signatureId) {
        return LT_TM_XAdES_SIGNATURE_FORMAT.equals(signatureFormat)
                ? parseTimeAssertionMessageImprintFromOcspNonce(signatureId)
                : parseTimeAssertionMessageImprintFromTimestamp(signatureId);
    }

    private String parseTimeAssertionMessageImprintFromOcspNonce(String signatureId) {
        Optional<XmlSignature> signature = dssReports.getDiagnosticDataJaxb().getSignatures().stream()
                .filter(s -> s.getId().equals(signatureId))
                .findFirst();
        if (signature.isEmpty()) {
            return "";
        }
        try {
            return parseTimeAssertionMessageImprint(signature.get().getFoundRevocations().getRelatedRevocations().get(0).getRevocation());
        } catch (Exception e) {
            LOGGER.warn("Unable to parse time assertion message imprint from OCSP nonce: ", e);
            return ""; //parse errors due to corrupted OCSP data should be present in validation errors already
        }
    }

    private String parseTimeAssertionMessageImprint(XmlRevocation revocation) throws IOException, OCSPException {
        OCSPResp response = new OCSPResp(revocation.getBase64Encoded());
        BasicOCSPResp basicResponse = (BasicOCSPResp) response.getResponseObject();
        Extension nonce = basicResponse.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
        return nonce == null ? "" : StringUtils.defaultString(Base64.encodeBase64String(nonce.getExtnValue().getOctets()));
    }

    private String parseTimeAssertionMessageImprintFromTimestamp(String signatureId) {
        TimestampWrapper timestamp = getBestTimestamp(signatureId);
        try {
            return ReportBuilderUtils.parseTimeAssertionMessageImprint(timestamp);
        } catch (Exception e) {
            LOGGER.warn("Unable to parse time assertion message imprint from timestamp: ", e);
            return ""; //parse errors due to corrupted timestamp data should be present in validation errors already
        }
    }

    private List<SignerRole> parseSignerRole(String signatureId) {
        return dssReports.getDiagnosticData().getSignatureById(signatureId).getClaimedRoles().stream()
                .filter(xmlSignerRole -> StringUtils.isNotEmpty(xmlSignerRole.getRole()))
                .map(this::mapXmlSignerRole)
                .collect(Collectors.toList());
    }

    private String parseReason(String signatureId) {
        return dssReports.getDiagnosticData().getSignatureById(signatureId).getReason();
    }

    private SignerRole mapXmlSignerRole(XmlSignerRole xmlSignerRole) {
        SignerRole signerRole = new SignerRole();
        signerRole.setClaimedRole(xmlSignerRole.getRole());
        return signerRole;
    }

    private SignatureProductionPlace parseSignatureProductionPlace(String signatureId) {
        SignatureWrapper signature = dssReports.getDiagnosticData().getSignatureById(signatureId);

        if (isSignatureProductionPlaceNotEmpty(signature)) {
            SignatureProductionPlace signatureProductionPlace = new SignatureProductionPlace();
            signatureProductionPlace.setCountryName(emptyWhenNull(signature.getCountryName()));
            signatureProductionPlace.setStateOrProvince(emptyWhenNull(signature.getStateOrProvince()));
            signatureProductionPlace.setCity(emptyWhenNull(signature.getCity()));
            signatureProductionPlace.setPostalCode(emptyWhenNull(signature.getPostalCode()));
            return signatureProductionPlace;
        } else if (StringUtils.isNotEmpty(signature.getLocation())) {
            // Since DSS 5.8, PDF signatures do not have SignatureProductionPlace attached anymore.
            //  Use Location from PDFRevision > PDFSignatureDictionary as CountryName; See:
            //  https://github.com/esig/dss/blob/5.7/dss-pades/src/main/java/eu/europa/esig/dss/pades/validation/PAdESSignature.java#L128-L130
            SignatureProductionPlace signatureProductionPlace = new SignatureProductionPlace();
            signatureProductionPlace.setCountryName(signature.getLocation());
            signatureProductionPlace.setStateOrProvince(valueNotPresent());
            signatureProductionPlace.setCity(valueNotPresent());
            signatureProductionPlace.setPostalCode(valueNotPresent());
            return signatureProductionPlace;
        }

        return null;
    }

    private boolean isSignatureProductionPlaceNotEmpty(SignatureWrapper signature) {
        return signature.isSignatureProductionPlacePresent() && !StringUtils.isAllEmpty(
                signature.getCountryName(),
                signature.getStateOrProvince(),
                signature.getCity(),
                signature.getPostalCode()
        );
    }

    private List<Error> parseSignatureErrors(String signatureId) {
        return parseSignatureMessages(signatureId, XmlDetails::getError)
                .map(GenericValidationReportBuilder::mapDssXmlMessage)
                .map(GenericValidationReportBuilder::mapDssError)
                .collect(Collectors.toList());
    }

    private static Error mapDssError(String dssError) {
        Error error = new Error();
        error.setContent(emptyWhenNull(dssError));
        return error;
    }

    private List<Warning> parseSignatureWarnings(String signatureId) {
        return parseSignatureMessages(signatureId, XmlDetails::getWarning)
                .map(GenericValidationReportBuilder::mapDssXmlMessage)
                .map(GenericValidationReportBuilder::mapDssWarning)
                .collect(Collectors.toList());
    }

    private static Warning mapDssWarning(String dssWarning) {
        Warning warning = new Warning();
        warning.setContent(emptyWhenNull(dssWarning));
        return warning;
    }

    private Stream<XmlMessage> parseSignatureMessages(String signatureId, Function<XmlDetails, List<XmlMessage>> detailMessagesExtractor) {
        DssSimpleReportWrapper dssSimpleReportWrapper = new DssSimpleReportWrapper(dssReports);
        return Optional
                .ofNullable(dssSimpleReportWrapper.getXmlSignature(signatureId)).stream()
                .flatMap(signature -> Stream.concat(
                        Stream.of(signature),
                        Optional.ofNullable(dssSimpleReportWrapper.getDssSimpleReport().getSignatureTimestamps(signatureId))
                                .stream().flatMap(List::stream)
                ))
                .flatMap(token -> extractTokenMessages(token, detailMessagesExtractor));
    }

    private static Stream<XmlMessage> extractTokenMessages(XmlToken token, Function<XmlDetails, List<XmlMessage>> detailMessagesExtractor) {
        return Stream.concat(
                Optional.ofNullable(token).map(XmlToken::getAdESValidationDetails).stream(),
                Optional.ofNullable(token).map(XmlToken::getQualificationDetails).stream()
        )
                .map(detailMessagesExtractor)
                .flatMap(List::stream);
    }

    private static String mapDssXmlMessage(XmlMessage dssXmlMessage) {
        return Optional.ofNullable(dssXmlMessage)
                .map(XmlMessage::getValue)
                .orElse(null);
    }

    private List<SignatureScope> parseSignatureScopes(String signatureId) {
        return dssReports.getDiagnosticData().getSignatureById(signatureId).getSignatureScopes()
                .stream()
                .map(this::parseSignatureScope)
                .collect(Collectors.toList());
    }

    private SignatureScope parseSignatureScope(XmlSignatureScope dssSignatureScope) {
        SignatureScope signatureScope = new SignatureScope();
        signatureScope.setContent(emptyWhenNull(dssSignatureScope.getDescription()));
        signatureScope.setName(emptyWhenNull(dssSignatureScope.getName()));
        if (dssSignatureScope.getScope() != null)
            signatureScope.setScope(emptyWhenNull(dssSignatureScope.getScope().name()));
        if (CollectionUtils.isNotEmpty(validationDocument.getDatafiles())) {
            Optional<Datafile> dataFile = validationDocument.getDatafiles()
                    .stream()
                    .filter(datafile -> datafile.getFilename().equals(dssSignatureScope.getName()))
                    .findFirst();
            if (dataFile.isPresent()) {
                signatureScope.setHash(dataFile.get().getHash());
                signatureScope.setHashAlgo(dataFile.get().getHashAlgo().toUpperCase());
            }
        }
        return signatureScope;
    }

    private String parseClaimedSigningTime(String signatureId) {
        Date signingDate = dssReports.getSimpleReport().getSigningTime(signatureId);
        if (signingDate == null)
            return valueNotKnown();
        return emptyWhenNull(ReportBuilderUtils.getDateFormatterWithGMTZone().format(signingDate));
    }

    private SignatureValidationData.Indication parseIndication(String signatureId, List<Error> errors) {
        Indication dssIndication = dssReports.getSimpleReport().getIndication(signatureId);
        if (errors.isEmpty() && StringUtils.equalsIgnoreCase(dssIndication.name(), Indication.TOTAL_PASSED.name())) {
            return SignatureValidationData.Indication.TOTAL_PASSED;
        } else if (StringUtils.equalsIgnoreCase(dssIndication.name(), Indication.INDETERMINATE.name())) {
            return SignatureValidationData.Indication.INDETERMINATE;
        } else {
            return SignatureValidationData.Indication.TOTAL_FAILED;
        }
    }

    private String parseSubIndication(String signatureId, List<Error> errors) {
        if (parseIndication(signatureId, errors) == SignatureValidationData.Indication.TOTAL_PASSED) {
            return "";
        }
        SubIndication subindication = dssReports.getSimpleReport().getSubIndication(signatureId);
        return subindication != null ? subindication.name() : "";
    }

    private String getCountryCode(String signatureId) {
        Optional<SignatureWrapper> signature = Optional.ofNullable(dssReports.getDiagnosticData().getSignatureById(signatureId));
        if (signature.isPresent()) {
            Optional<CertificateWrapper> signingCertificate = Optional.ofNullable(signature.get().getSigningCertificate());
            if (signingCertificate.isPresent()) {
                return signingCertificate.get().getCountryName();
            }
        }
        return null;
    }
}
