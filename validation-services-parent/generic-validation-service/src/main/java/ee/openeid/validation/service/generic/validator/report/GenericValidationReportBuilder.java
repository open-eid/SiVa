/*
 * Copyright 2020 Riigi Infosüsteemide Amet
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
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.siva.validation.util.CertUtil;
import ee.openeid.siva.validation.util.SubjectDNParser;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.*;
import eu.europa.esig.dss.enumerations.*;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.*;

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
                    XmlDigestAlgoAndValue digestAlgoAndValue = usedCertificate.getDigestAlgoAndValue();
                    Digest digest = new Digest(digestAlgoAndValue.getDigestMethod(), digestAlgoAndValue.getDigestValue());
                    CertificateToken certSource = trustedListsCertificateSource.getCertificateTokenByDigest(digest);
                    if (certSource != null) {
                        return certSource;
                    }

                    for (AdvancedSignature advancedSignature : signatures) {
                        Optional<CertificateToken> optionalCertSource = advancedSignature.getCertificateListWithinSignatureAndTimestamps().stream()
                                .filter(signature -> signature.getDSSIdAsString().equals(usedCertificate.getId())).findFirst();
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
        validationConclusion.setSignatureForm(getContainerType());
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

    private String getContainerType() {
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
        signatureValidationData.setSignedBy(parseSubjectDistinguishedName(signatureId).getCommonName());
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
            TimestampWrapper lastTimestamp = getLastTimestamp(archiveTimestamps);
            if (lastTimestamp.getSigningCertificate() != null) {
                String archiveTimestampCertId = lastTimestamp.getSigningCertificate().getId();
                return getCertificate(archiveTimestampCertId, CertificateType.ARCHIVE_TIMESTAMP);
            }
        }
        return null;
    }

    private Certificate getSignatureTimestampCertificate(SignatureWrapper signatureWrapper) {
        List<TimestampWrapper> signatureTimestamps = signatureWrapper.getTimestampListByType(TimestampType.SIGNATURE_TIMESTAMP);
        if (CollectionUtils.isNotEmpty(signatureTimestamps)) {
            TimestampWrapper firstTimestamp = signatureTimestamps.get(0);
            if (firstTimestamp.getSigningCertificate() != null) {
                String timestampCertId = firstTimestamp.getSigningCertificate().getId();
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
        List<XmlRelatedRevocation> relatedRevocations = signatureWrapper.getRelatedRevocations();
        if (CollectionUtils.isNotEmpty(relatedRevocations)) {
            XmlRelatedRevocation lastRevocation = relatedRevocations.get(0);
            if (lastRevocation.getRevocation().getSigningCertificate() != null) {
                String revocationId = lastRevocation.getRevocation().getSigningCertificate().getCertificate().getId();
                return getCertificate(revocationId, CertificateType.REVOCATION);
            }
        }
        return null;
    }

    private TimestampWrapper getLastTimestamp(List<TimestampWrapper> timestamps) {
        return Collections.max(timestamps, Comparator.comparing(TimestampWrapper::getProductionTime));
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
                        && certificateToken.getSubjectX500Principal() != null
                        && certificateToken.getSubjectX500Principal().equals(x509Certificate.getIssuerX500Principal()))
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
        String DAIdentifier = dssReports.getDiagnosticData().getSignatureById(signatureId).getDAIdentifier();
        if (StringUtils.isNotBlank(DAIdentifier)) {
            return DAIdentifier;
        }
        return signatureId;
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
            dssReports.getSimpleReport().getErrors(signatureId).add(FORMAT_NOT_FOUND);
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
        Date bestSignatureTime = getBestSignatureTime(signatureFormat, signatureId);
        Info info = new Info();
        info.setBestSignatureTime(bestSignatureTime == null ? "" : ReportBuilderUtils.getDateFormatterWithGMTZone().format(bestSignatureTime));
        info.setTimeAssertionMessageImprint(parseTimeAssertionMessageImprint(signatureFormat, signatureId));
        info.setSignerRole(parseSignerRole(signatureId));
        info.setSignatureProductionPlace(parseSignatureProductionPlace(signatureId));
        return info;
    }

    private Date getBestSignatureTime(String signatureFormat, String signatureId) {
        if (signatureFormat.equals(LT_TM_XAdES_SIGNATURE_FORMAT)) {
            List<XmlRelatedRevocation> revocations = dssReports.getDiagnosticData().getSignatureById(signatureId).getRelatedRevocations();
            return revocations.isEmpty() ? null : revocations.get(0).getRevocation().getProductionDate();
        } else {
            TimestampWrapper timestamp = getBestTimestamp(signatureId);
            return timestamp == null ? null : timestamp.getProductionTime();
        }
    }

    private TimestampWrapper getBestTimestamp(String signatureId) {
        List<TimestampWrapper> timestamps = dssReports.getDiagnosticData().getSignatureById(signatureId)
                .getTimestampListByType(TimestampType.SIGNATURE_TIMESTAMP);
        return timestamps.isEmpty() ? null : timestamps.get(0);
    }

    private String parseTimeAssertionMessageImprint(String signatureFormat, String signatureId) {
        return LT_TM_XAdES_SIGNATURE_FORMAT.equals(signatureFormat)
                ? parseTimeAssertionMessageImprintFromOcspNonce(signatureId)
                : parseTimeAssertionMessageImprintFromTimestamp(signatureId);
    }

    private String parseTimeAssertionMessageImprintFromOcspNonce(String signatureId) {
        List<XmlRelatedRevocation> revocations = dssReports.getDiagnosticData().getSignatureById(signatureId).getRelatedRevocations();

        if (revocations.isEmpty()) {
            return "";
        }

        try {
            return parseTimeAssertionMessageImprint(revocations.get(0).getRevocation());
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

        if (timestamp == null || !timestamp.isMessageImprintDataFound() || !timestamp.isMessageImprintDataIntact()) {
            return "";
        }

        try {
            return parseTimeAssertionMessageImprint(timestamp);
        } catch (Exception e) {
            LOGGER.warn("Unable to parse time assertion message imprint from timestamop: ", e);
            return ""; //parse errors due to corrupted timestamp data should be present in validation errors already
        }
    }

    private String parseTimeAssertionMessageImprint(TimestampWrapper timestamp) throws IOException {
        XmlDigestMatcher messageImprint = timestamp.getMessageImprint();
        AlgorithmIdentifier algorithm = new DefaultDigestAlgorithmIdentifierFinder().find(messageImprint.getDigestMethod().getJavaName());
        byte[] nonce = new MessageImprint(algorithm, messageImprint.getDigestValue()).getEncoded();
        return StringUtils.defaultString(Base64.encodeBase64String(nonce));
    }

    private List<SignerRole> parseSignerRole(String signatureId) {
        return dssReports.getDiagnosticData().getSignatureById(signatureId).getClaimedRoles().stream()
                .filter(xmlSignerRole -> StringUtils.isNotEmpty(xmlSignerRole.getRole()))
                .map(this::mapXmlSignerRole)
                .collect(Collectors.toList());
    }

    private SignerRole mapXmlSignerRole(XmlSignerRole xmlSignerRole) {
        SignerRole signerRole = new SignerRole();
        signerRole.setClaimedRole(xmlSignerRole.getRole());
        return signerRole;
    }

    private SignatureProductionPlace parseSignatureProductionPlace(String signatureId) {
        SignatureWrapper signature = dssReports.getDiagnosticData().getSignatureById(signatureId);

        if (isSignatureProductionPlaceEmpty(signature)) {
            return null;
        }

        SignatureProductionPlace signatureProductionPlace = new SignatureProductionPlace();
        signatureProductionPlace.setCountryName(StringUtils.defaultString(signature.getCountryName()));
        signatureProductionPlace.setStateOrProvince(StringUtils.defaultString(signature.getStateOrProvince()));
        signatureProductionPlace.setCity(StringUtils.defaultString(signature.getCity()));
        signatureProductionPlace.setPostalCode(StringUtils.defaultString(signature.getPostalCode()));
        return signatureProductionPlace;
    }

    private boolean isSignatureProductionPlaceEmpty(SignatureWrapper signature) {
        return !signature.isSignatureProductionPlacePresent() || StringUtils.isAllEmpty(
                signature.getCountryName(),
                signature.getStateOrProvince(),
                signature.getCity(),
                signature.getPostalCode());
    }

    private List<Error> parseSignatureErrors(String signatureId) {
        return dssReports.getSimpleReport().getErrors(signatureId)
                .stream()
                .map(this::mapDssError)
                .collect(Collectors.toList());
    }

    private Error mapDssError(String dssError) {
        Error error = new Error();
        error.setContent(emptyWhenNull(dssError));
        return error;
    }

    private List<Warning> parseSignatureWarnings(String signatureId) {
        return dssReports.getSimpleReport().getWarnings(signatureId)
                .stream()
                .map(this::mapDssWarning)
                .collect(Collectors.toList());
    }

    private Warning mapDssWarning(String dssWarning) {
        Warning warning = new Warning();
        warning.setContent(emptyWhenNull(dssWarning));
        return warning;
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
