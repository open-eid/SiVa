/*
 * Copyright 2019 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timemark.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.ArchiveTimeStamp;
import ee.openeid.siva.validation.document.report.Certificate;
import ee.openeid.siva.validation.document.report.CertificateType;
import ee.openeid.siva.validation.document.report.Scope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.ValidationWarning;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.spi.x509.tsp.TimestampToken;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.digidoc4j.Container;
import org.digidoc4j.ContainerValidationResult;
import org.digidoc4j.Signature;
import org.digidoc4j.SignatureProfile;
import org.digidoc4j.ValidationResult;
import org.digidoc4j.impl.asic.AsicSignature;
import org.digidoc4j.impl.asic.asice.AsicESignature;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.getDateFormatterWithGMTZone;

public class AsicContainerValidationReportBuilder extends TimemarkContainerValidationReportBuilder {

    public AsicContainerValidationReportBuilder(
        Container container,
        ValidationDocument validationDocument,
        ValidationPolicy validationPolicy,
        ContainerValidationResult validationResult,
        boolean isReportSignatureEnabled
    ) {
        super(container, validationDocument, validationPolicy, validationResult, isReportSignatureEnabled);
    }

    @Override
    protected SignatureValidationData.Indication getIndication(Signature signature) {
        boolean isSignatureValid = getSignatureValidationResult(signature.getUniqueId())
            .map(ValidationResult::isValid)
            .orElse(false);
        if (isSignatureValid && validationResult.getErrors().isEmpty()) {
            return SignatureValidationData.Indication.TOTAL_PASSED;
        } else if (REPORT_INDICATION_INDETERMINATE.equals(getDssSimpleReport((AsicESignature) signature).getIndication(signature.getUniqueId()).name())
            && validationResult.getErrors().isEmpty()) {
            return SignatureValidationData.Indication.INDETERMINATE;
        } else {
            return SignatureValidationData.Indication.TOTAL_FAILED;
        }
    }

    @Override
    protected String getSubIndication(Signature signature) {
        if (getIndication(signature) == SignatureValidationData.Indication.TOTAL_PASSED) {
            return "";
        }
        SubIndication subindication = getDssSimpleReport((AsicESignature) signature).getSubIndication(signature.getUniqueId());
        return subindication != null ? subindication.name() : "";
    }

    @Override
    protected String getSignatureLevel(Signature signature) {
        SignatureQualification signatureLevel = getDssSimpleReport((AsicESignature) signature).getSignatureQualification(signature.getUniqueId());
        return signatureLevel != null ? signatureLevel.name() : "";

    }

    @Override
    protected List<Certificate> getCertificateList(Signature signature) {
        List<Certificate> certificateList = super.getCertificateList(signature);
        if (signature.getTimeStampTokenCertificate() != null) {
            X509Certificate x509Certificate = signature.getTimeStampTokenCertificate().getX509Certificate();
            certificateList.add(getCertificate(x509Certificate, CertificateType.SIGNATURE_TIMESTAMP));
        }
        return certificateList;
    }

    @Override
    void processSignatureIndications(ValidationConclusion validationConclusion, String policyName) {
        ReportBuilderUtils.processSignatureIndications(validationConclusion, policyName);
    }

    @Override
    List<ValidationWarning> getExtraValidationWarnings() {
        return new ArrayList<>();
    }

    @Override
    List<Scope> getSignatureScopes(Signature signature, List<String> dataFilenames) {
        AsicESignature bDocSignature = (AsicESignature) signature;
        return bDocSignature.getOrigin().getReferences()
                .stream()
                .map(r -> decodeUriIfPossible(r.getURI()))
                .filter(dataFilenames::contains) //filters out Signed Properties
                .map(AsicContainerValidationReportBuilder::createFullSignatureScopeForDataFile)
                .collect(Collectors.toList());
    }

    @Override
    String getSignatureForm() {
        return BDOC_SIGNATURE_FORM;
    }

    @Override
    String getSignatureFormat(SignatureProfile profile) {
        return XADES_FORMAT_PREFIX + profile.toString();
    }

    @Override
    List<ArchiveTimeStamp> getArchiveTimestamps(Signature signature) {
        String signatureId = signature.getUniqueId();
        DiagnosticData diagnosticData = ((AsicSignature) signature).getDssValidationReport().getReports().getDiagnosticData();

        return Optional
                .ofNullable(diagnosticData.getSignatureById(signatureId).getALevelTimestamps())
                .filter(CollectionUtils::isNotEmpty)
                .map(timestamps -> generateArchiveTimestamps(timestamps, signature))
                .orElse(null);
    }

    private static Scope createFullSignatureScopeForDataFile(String filename) {
        Scope signatureScope = new Scope();
        signatureScope.setName(filename);
        signatureScope.setScope(FULL_SIGNATURE_SCOPE);
        signatureScope.setContent(FULL_DOCUMENT);
        return signatureScope;
    }

    private String decodeUriIfPossible(String uri) {
        try {
            return URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("datafile " + uri + " has unsupported encoding", e);
            return uri;
        }
    }

    private List<ArchiveTimeStamp> generateArchiveTimestamps(List<TimestampWrapper> timestamps, Signature signature) {
        List<ArchiveTimeStamp> archiveTimestamps = new ArrayList<>();

        for (TimestampWrapper timestampWrapper : timestamps) {
            ArchiveTimeStamp archiveTimeStamp = new ArchiveTimeStamp();
            eu.europa.esig.dss.simplereport.SimpleReport simpleReport = ((AsicSignature) signature).getDssValidationReport()
                    .getReports().getSimpleReport();

            archiveTimeStamp.setSignedTime(getDateFormatterWithGMTZone().format(timestampWrapper.getProductionTime()));
            archiveTimeStamp.setIndication(simpleReport.getIndication(timestampWrapper.getId()));
            Optional.ofNullable(simpleReport.getSubIndication(timestampWrapper.getId()))
                    .map(SubIndication::name)
                    .ifPresent(archiveTimeStamp::setSubIndication);
            archiveTimeStamp.setSignedBy(timestampWrapper.getSigningCertificate().getCommonName());
            archiveTimeStamp.setCountry(timestampWrapper.getSigningCertificate().getCountryName());
            archiveTimeStamp.setContent(getContentFromTimestampToken(signature, timestampWrapper));

            archiveTimestamps.add(archiveTimeStamp);
        }

        return archiveTimestamps;
    }

    private String getContentFromTimestampToken(Signature signature, TimestampWrapper timestampWrapper) {
        String timestampWrapperId = timestampWrapper.getId();
        List<TimestampToken> archiveTimestampTokens = ((AsicSignature) signature).getOrigin().getDssSignature().getArchiveTimestamps();

        for (TimestampToken timestampToken : archiveTimestampTokens) {
            if (timestampWrapperId.equals(timestampToken.getDSSId().asXmlId())) {
                return Base64.encodeBase64String(timestampToken.getEncoded());
            }
        }

        return null;
    }

}
