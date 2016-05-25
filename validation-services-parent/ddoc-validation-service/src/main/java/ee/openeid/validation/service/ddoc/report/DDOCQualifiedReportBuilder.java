package ee.openeid.validation.service.ddoc.report;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.sk.digidoc.DataFile;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.Signature;
import ee.sk.digidoc.SignedDoc;
import org.apache.commons.lang.StringUtils;
import org.cryptacular.util.CertUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class DDOCQualifiedReportBuilder {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String FULL_DOCUMENT = "Full document";

    private SignedDoc signedDoc;
    private String documentName;
    private Date validationTime;

    public DDOCQualifiedReportBuilder(SignedDoc signedDoc, String documentName, Date validationTime) {
        this.signedDoc = signedDoc;
        this.documentName = documentName;
        this.validationTime = validationTime;
    }

    public QualifiedReport build() {
        QualifiedReport qualifiedReport = new QualifiedReport();
        qualifiedReport.setValidationTime(getDateFormatterWithGMTZone().format(validationTime));
        qualifiedReport.setDocumentName(documentName);
        qualifiedReport.setSignaturesCount(signedDoc.getSignatures().size());
        qualifiedReport.setSignatures(createSignaturesForReport(signedDoc));
        qualifiedReport.setValidSignaturesCount(
                qualifiedReport.getSignatures()
                        .stream()
                        .filter(vd -> StringUtils.equals(vd.getIndication(), SignatureValidationData.Indication.TOTAL_PASSED.toString()))
                        .collect(Collectors.toList())
                        .size());

        return qualifiedReport;
    }

    private SimpleDateFormat getDateFormatterWithGMTZone() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf;
    }

    @SuppressWarnings("unchecked")
    private List<SignatureValidationData> createSignaturesForReport(SignedDoc signedDoc) {
        List<Signature> signatures = (List<Signature>) signedDoc.getSignatures();
        return signatures.stream().map(this::createSignatureValidationData).collect(Collectors.toList());
    }

    private SignatureValidationData createSignatureValidationData(Signature signature) {
        SignatureValidationData signatureValidationData = new SignatureValidationData();

        signatureValidationData.setId(signature.getId());
        signatureValidationData.setSignatureFormat(getSignatureFormat());
        signatureValidationData.setSignedBy(CertUtil.subjectCN(signature.getKeyInfo().getSignersCertificate()));
        signatureValidationData.setErrors(getErrors(signature));
        signatureValidationData.setSignatureScopes(getSignatureScopes());
        signatureValidationData.setClaimedSigningTime(getDateFormatterWithGMTZone().format(signature.getSignedProperties().getSigningTime()));
        signatureValidationData.setIndication(getIndication(signature));

        return signatureValidationData;

    }

    private String getSignatureFormat() {
        return signedDoc.getFormat().replaceAll("-", "_") + "_" + signedDoc.getVersion();
    }

    private List<Error> getErrors(Signature signature) {
        List<DigiDocException> signatureValidationErrors = signature.validate();
        List<DigiDocException> signatureVerificationErrors = signature.verify(signedDoc, true, true);

        List<Error> errors = signatureValidationErrors
                .stream()
                .map(this::mapDigiDocException)
                .collect(Collectors.toList());

        errors.addAll(signatureVerificationErrors
                .stream()
                .map(this::mapDigiDocException)
                .collect(Collectors.toList()));

        return errors;
    }

    private Error mapDigiDocException(DigiDocException dde) {
        Error error = new Error();
        error.setNameId(Integer.toString(dde.getCode()));
        error.setContent(dde.getMessage());
        return error;
    }

    private List<SignatureScope> getSignatureScopes() {
        List<DataFile> dataFiles = (List<DataFile>) signedDoc.getDataFiles();

        return dataFiles
                .stream()
                .map(this::mapDataFile)
                .collect(Collectors.toList());
    }

    private SignatureScope mapDataFile(DataFile dataFile) {
        SignatureScope signatureScope = new SignatureScope();
        signatureScope.setName(dataFile.getFileName());
        signatureScope.setContent(FULL_DOCUMENT);
        return signatureScope;
    }

    private SignatureValidationData.Indication getIndication(Signature signature) {
        if (!signature.validate().isEmpty()) {
            return SignatureValidationData.Indication.INDETERMINATE;
        } else if (!signature.verify(signedDoc, true, true).isEmpty()) {
            return SignatureValidationData.Indication.TOTAL_FAILED;
        }

        return SignatureValidationData.Indication.TOTAL_PASSED;
    }

}
