package ee.openeid.validation.service.bdoc.report;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import eu.europa.esig.dss.validation.policy.rules.SubIndication;
import eu.europa.esig.dss.validation.reports.SignatureType;
import eu.europa.esig.dss.validation.reports.SimpleReport;
import org.apache.commons.lang.StringUtils;
import org.apache.xml.security.signature.Reference;
import org.digidoc4j.*;
import org.digidoc4j.exceptions.DigiDoc4JException;
import org.digidoc4j.impl.bdoc.BDocSignature;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;
import static org.digidoc4j.X509Cert.SubjectName.CN;


public class BDOCQualifiedReportBuilder {

    private static final String FULL_SIGNATURE_SCOPE = "FullSignatureScope";
    private static final String FULL_DOCUMENT = "Full document";
    private static final String GENERIC = "GENERIC";
    private static final String XADES_FORMAT_PREFIX = "XAdES_BASELINE_";
    private static final String REPORT_INDICATION_INDETERMINATE = "INDETERMINATE";
    private static final String BDOC_SIGNATURE_FORM = "ASiC_E";

    private Container container;
    private String documentName;
    private Date validationTime;

    public BDOCQualifiedReportBuilder(Container container, String documentName, Date validationTime) {
        this.container = container;
        this.documentName = documentName;
        this.validationTime = validationTime;
    }

    public QualifiedReport build() {
        QualifiedReport qualifiedReport = new QualifiedReport();
        qualifiedReport.setPolicy(Policy.SIVA_DEFAULT);
        qualifiedReport.setValidationTime(ReportBuilderUtils.getDateFormatterWithGMTZone().format(validationTime));
        qualifiedReport.setDocumentName(documentName);
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
        signatureValidationData.setSignatureForm(BDOC_SIGNATURE_FORM);
        signatureValidationData.setSignedBy(removeQuotes(bDocSignature.getSigningCertificate().getSubjectName(CN)));
        signatureValidationData.setErrors(getErrors(bDocSignature));
        signatureValidationData.setSignatureScopes(getSignatureScopes(bDocSignature, dataFileNames));
        signatureValidationData.setClaimedSigningTime(ReportBuilderUtils.getDateFormatterWithGMTZone().format(bDocSignature.getClaimedSigningTime()));
        signatureValidationData.setWarnings(getWarnings(bDocSignature));
        signatureValidationData.setInfo(getInfo(bDocSignature));
        signatureValidationData.setIndication(getIndication(bDocSignature));
        signatureValidationData.setSubIndication(getSubIndication(bDocSignature));

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
        List<Warning> warnings = getDssSimpleReport(bDocSignature).getWarnings(bDocSignature.getId())
                .stream()
                .map(BDOCQualifiedReportBuilder::mapDssWarning)
                .collect(Collectors.toList());

        List<String> dssWarningMessages = warnings.stream().map(Warning::getDescription).collect(Collectors.toList());

        bDocSignature.validateSignature().getWarnings()
                .stream()
                .filter(e -> !isRepeatingMessage(dssWarningMessages, e.getMessage()))
                .map(BDOCQualifiedReportBuilder::mapDigidoc4JWarning)
                .forEach(warnings::add);

        return warnings;
    }

    private static Warning mapDssWarning(String dssWarning) {
        Warning warning = new Warning();
        warning.setDescription(emptyWhenNull(dssWarning));
        return warning;
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
        //First get DSS errors as they have error codes
        List<Error> errors = getDssSimpleReport(bDocSignature).getErrors(bDocSignature.getId())
                .stream()
                .map(BDOCQualifiedReportBuilder::mapDssError)
                .collect(Collectors.toList());

        List<String> dssErrorMessages = errors
                .stream()
                .map(Error::getContent)
                .collect(Collectors.toList());

        //Add additional digidoc4j errors
        bDocSignature.validateSignature().getErrors()
                .stream()
                .filter(e -> !isRepeatingMessage(dssErrorMessages, e.getMessage()))
                .map(BDOCQualifiedReportBuilder::mapDigidoc4JException)
                .forEach(errors::add);

        return errors;
    }

    private static Error mapDssError(String dssError) {
        Error error = new Error();
        error.setContent(emptyWhenNull(dssError));
        return error;
    }

    private static boolean isRepeatingMessage(List<String> dssMessages, String digidoc4jExceptionMessage) {
        for (String dssMessage : dssMessages) {
            if (digidoc4jExceptionMessage.contains(dssMessage)) {
                return true;
            }
        }
        return false;
    }

    private static Error mapDigidoc4JException(DigiDoc4JException digiDoc4JException) {
        Error error = new Error();
        error.setContent(emptyWhenNull(digiDoc4JException.getMessage()));
        return error;
    }

    private String getSignatureFormat(SignatureProfile profile) {
        return XADES_FORMAT_PREFIX + profile.name();
    }
}
