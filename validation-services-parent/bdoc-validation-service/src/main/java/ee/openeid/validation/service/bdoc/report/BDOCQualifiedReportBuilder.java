/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.validation.service.bdoc.report;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import eu.europa.esig.dss.validation.policy.rules.SubIndication;
import eu.europa.esig.dss.validation.reports.SignatureType;
import eu.europa.esig.dss.validation.reports.SimpleReport;
import org.apache.commons.lang.StringUtils;
import org.apache.xml.security.signature.Reference;
import org.digidoc4j.*;
import org.digidoc4j.exceptions.DigiDoc4JException;
import org.digidoc4j.impl.bdoc.BDocSignature;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.createReportPolicy;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;
import static org.digidoc4j.X509Cert.SubjectName.CN;

public class BDOCQualifiedReportBuilder {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BDOCQualifiedReportBuilder.class);

    private static final String FULL_SIGNATURE_SCOPE = "FullSignatureScope";
    private static final String FULL_DOCUMENT = "Full document";
    private static final String XADES_FORMAT_PREFIX = "XAdES_BASELINE_";
    private static final String REPORT_INDICATION_INDETERMINATE = "INDETERMINATE";
    private static final String BDOC_SIGNATURE_FORM = "ASiC_E";

    private Container container;
    private String documentName;
    private Date validationTime;
    private ValidationPolicy validationPolicy;

    public BDOCQualifiedReportBuilder(Container container, String documentName, Date validationTime, ValidationPolicy validationPolicy) {
        this.container = container;
        this.documentName = documentName;
        this.validationTime = validationTime;
        this.validationPolicy = validationPolicy;
    }

    public QualifiedReport build() {
        QualifiedReport qualifiedReport = new QualifiedReport();
        qualifiedReport.setPolicy(createReportPolicy(validationPolicy));
        qualifiedReport.setValidationTime(ReportBuilderUtils.getDateFormatterWithGMTZone().format(validationTime));
        qualifiedReport.setDocumentName(documentName);
        qualifiedReport.setSignatureForm(BDOC_SIGNATURE_FORM);
        qualifiedReport.setSignaturesCount(container.getSignatures().size());
        qualifiedReport.setSignatures(createSignaturesForReport(container));
        qualifiedReport.setValidSignaturesCount(
                qualifiedReport.getSignatures()
                        .stream()
                        .filter(vd -> StringUtils.equals(vd.getIndication(), SignatureValidationData.Indication.TOTAL_PASSED.toString()))
                        .collect(Collectors.toList())
                        .size());

        return qualifiedReport;
    }

    private List<SignatureValidationData> createSignaturesForReport(Container container) {
        List<String> dataFileNames = container.getDataFiles().stream().map(DataFile::getName).collect(Collectors.toList());
        return container.getSignatures().stream().map(sig -> createSignatureValidationData(sig, dataFileNames)).collect(Collectors.toList());
    }

    private SignatureValidationData createSignatureValidationData(Signature signature, List<String> dataFileNames) {
        SignatureValidationData signatureValidationData = new SignatureValidationData();
        BDocSignature bDocSignature = (BDocSignature) signature;

        signatureValidationData.setId(bDocSignature.getId());
        signatureValidationData.setSignatureFormat(getSignatureFormat(bDocSignature.getProfile()));
        signatureValidationData.setSignatureLevel(getSignatureLevel(bDocSignature));
        signatureValidationData.setSignedBy(removeQuotes(bDocSignature.getSigningCertificate().getSubjectName(CN)));
        signatureValidationData.setErrors(getErrors(bDocSignature));
        signatureValidationData.setSignatureScopes(getSignatureScopes(bDocSignature, dataFileNames));
        signatureValidationData.setClaimedSigningTime(ReportBuilderUtils.getDateFormatterWithGMTZone().format(bDocSignature.getClaimedSigningTime()));
        signatureValidationData.setWarnings(getWarnings(bDocSignature));
        signatureValidationData.setInfo(getInfo(bDocSignature));
        signatureValidationData.setIndication(getIndication(bDocSignature));
        signatureValidationData.setSubIndication(getSubIndication(bDocSignature));
        signatureValidationData.setCountryCode(getCountryCode(bDocSignature));

        return signatureValidationData;

    }

    private String removeQuotes(String subjectName) {
        return subjectName.replaceAll("^\"|\"$", "");
    }

    private String getSignatureLevel(BDocSignature bDocSignature) {
        SignatureType signatureLevel = getDssSimpleReport(bDocSignature).getSignatureLevel(bDocSignature.getId());
        return signatureLevel != null ? signatureLevel.name() : "";
    }

    private SimpleReport getDssSimpleReport(BDocSignature bDocSignature) {
        return bDocSignature.getDssValidationReport().getReport().getSimpleReport();
    }

    private SignatureValidationData.Indication getIndication(BDocSignature bDocSignature) {
        SignatureValidationResult validationResult = bDocSignature.validateSignature();
        if (validationResult.isValid()) {
            return SignatureValidationData.Indication.TOTAL_PASSED;
        } else if (REPORT_INDICATION_INDETERMINATE.equals(getDssSimpleReport(bDocSignature).getIndication(bDocSignature.getId()))) {
            return SignatureValidationData.Indication.INDETERMINATE;
        } else {
            return SignatureValidationData.Indication.TOTAL_FAILED;
        }
    }

    private String getSubIndication(BDocSignature bDocSignature) {
        if (getIndication(bDocSignature) == SignatureValidationData.Indication.TOTAL_PASSED) {
            return "";
        }
        SubIndication subindication = getDssSimpleReport(bDocSignature).getSubIndication(bDocSignature.getId());
        return subindication != null ? subindication.name() : "";
    }

    private Info getInfo(BDocSignature bDocSignature) {
        Info info = new Info();
        Date trustedTime = bDocSignature.getTrustedSigningTime();
        if (trustedTime != null) {
            info.setBestSignatureTime(ReportBuilderUtils.getDateFormatterWithGMTZone().format(trustedTime));
        } else {
            info.setBestSignatureTime("");
        }
        return info;
    }

    private List<Warning> getWarnings(BDocSignature bDocSignature) {
        List<Warning> warnings = bDocSignature.validateSignature().getWarnings()
                .stream()
                .map(BDOCQualifiedReportBuilder::mapDigidoc4JWarning)
                .collect(Collectors.toList());

        return warnings;
    }

    private static Warning mapDigidoc4JWarning(DigiDoc4JException digiDoc4JException) {
        Warning warning = new Warning();
        warning.setDescription(emptyWhenNull(digiDoc4JException.getMessage()));
        return warning;
    }

    private List<SignatureScope> getSignatureScopes(BDocSignature bDocSignature, List<String> dataFileNames) {
        return bDocSignature.getOrigin().getReferences()
                .stream()
                .filter(r -> dataFileNames.contains(r.getURI())) //filters out Signed Properties
                .map(BDOCQualifiedReportBuilder::mapDssReference)
                .collect(Collectors.toList());
    }

    private static SignatureScope mapDssReference(Reference reference) {
        SignatureScope signatureScope = new SignatureScope();
        signatureScope.setName(reference.getURI());
        signatureScope.setScope(FULL_SIGNATURE_SCOPE);
        signatureScope.setContent(FULL_DOCUMENT);
        return signatureScope;
    }

    private List<Error> getErrors(BDocSignature bDocSignature) {
        List<Error> errors = bDocSignature.validateSignature().getErrors()
                .stream()
                .map(BDOCQualifiedReportBuilder::mapDigidoc4JException)
                .collect(Collectors.toList());

        return errors;
    }

    private static Error mapDigidoc4JException(DigiDoc4JException digiDoc4JException) {
        Error error = new Error();
        error.setContent(emptyWhenNull(digiDoc4JException.getMessage()));
        return error;
    }

    private String getSignatureFormat(SignatureProfile profile) {
        return XADES_FORMAT_PREFIX + profile.name();
    }

    private String getCountryCode(BDocSignature bDocSignature) {
        return bDocSignature.getSigningCertificate().getSubjectName(X509Cert.SubjectName.C);
    }

}
