package ee.openeid.siva.xroad;

import ee.openeid.siva.validation.document.report.*;
import ee.ria.xroad.common.asic.AsicContainer;
import ee.ria.xroad.common.asic.AsicContainerVerifier;
import ee.ria.xroad.common.signature.Signature;
import ee.ria.xroad.common.signature.SignatureData;
import org.apache.commons.lang.StringUtils;
import org.apache.xml.security.signature.Reference;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;


public class XROADQualifiedReportBuilder {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String FULL_SIGNATURE_SCOPE = "FullSignatureScope";
    private static final String FULL_DOCUMENT = "Full document";
    private static final String GENERIC = "GENERIC";
    private static final String XADES_FORMAT_PREFIX = "XAdES_BASELINE_";
    private static final String DSS_BASIC_INFO_NAME_ID = "NameId";
    private static final String REPORT_INDICATION_INDETERMINATE = "INDETERMINATE";
    private static final String GREENWICH_MEAN_TIME = "Etc/GMT";

    private AsicContainer container;
    private AsicContainerVerifier verifier;
    private String documentName;
    private Date validationTime;

    public XROADQualifiedReportBuilder(AsicContainerVerifier verifier, String documentName, Date validationTime) {
        this.verifier = verifier;
        this.documentName = documentName;
        this.validationTime = validationTime;
    }

    public QualifiedReport build() throws Exception {
        QualifiedReport qualifiedReport = new QualifiedReport();
        qualifiedReport.setPolicy(Policy.SIVA_DEFAULT);
        qualifiedReport.setValidationTime(getDateFormatterWithGMTZone().format(validationTime));
        qualifiedReport.setDocumentName(documentName);
        qualifiedReport.setSignaturesCount(getTotalSignatureCount());
        qualifiedReport.setSignatures(Arrays.asList(createSignaturesForReport(container)));
        qualifiedReport.setValidSignaturesCount(
                qualifiedReport.getSignatures()
                        .stream()
                        .filter(vd -> StringUtils.equals(vd.getIndication(), SignatureValidationData.Indication.TOTAL_PASSED.toString()))
                        .collect(Collectors.toList())
                        .size());

        return qualifiedReport;
    }

    private int getTotalSignatureCount() {
        return verifier.getSignature() != null ? 1 : 0;
    }

    private SignatureValidationData createSignaturesForReport(AsicContainer container) throws Exception {
        return createSignatureValidationData();
    }

    private SignatureValidationData createSignatureValidationData() throws Exception {
        SignatureValidationData signatureValidationData = new SignatureValidationData();
        SignatureData bDocSignature = verifier.getAsic().getSignature();

        signatureValidationData.setId(verifier.getSignature().getXmlSignature().getId());
        signatureValidationData.setSignatureFormat(getSignatureFormat(null));
        signatureValidationData.setSignatureLevel(getSignatureLevel(bDocSignature));
        signatureValidationData.setSignedBy(removeQuotes(verifier.getSignerCert().getSubjectDN().getName()));
//        signatureValidationData.setErrors(getErrors(bDocSignature));
        signatureValidationData.setSignatureScopes(Arrays.asList(new SignatureScope()));
//        signatureValidationData.setClaimedSigningTime(getDateFormatterWithGMTZone().format(verifier.getSignature().getSignatureTimestamp()));
//        signatureValidationData.setWarnings(getWarnings(bDocSignature));
        signatureValidationData.setInfo(getInfo(verifier.getSignature()));
        signatureValidationData.setIndication(getIndication());
        signatureValidationData.setSubIndication(getSubIndication(bDocSignature));

        return signatureValidationData;

    }

    private SimpleDateFormat getDateFormatterWithGMTZone() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone(GREENWICH_MEAN_TIME));
        return sdf;
    }

    private String removeQuotes(String subjectName) {
        return subjectName.replaceAll("^\"|\"$", "");
    }

    private String getSignatureLevel(SignatureData bDocSignature) {
//        return getDssSimpleReport(bDocSignature).getSignatureLevel(bDocSignature.getId()).name();
        return "random-level";
    }

    private SignatureValidationData.Indication getIndication() {
        return SignatureValidationData.Indication.TOTAL_PASSED;
    }

    private String getSubIndication(SignatureData bDocSignature) {
        if (getIndication() == SignatureValidationData.Indication.TOTAL_PASSED) {
            return "";
        }
//        return emptyWhenNull(getDssSimpleReport(bDocSignature).getSubIndication(bDocSignature.getId()));
        return "";
    }


    private Info getInfo(Signature bDocSignature) {
        Info info = new Info();
//        Date trustedTime = new Date(bDocSignature.getSignatureTimestamp());

        Date trustedTime = new Date();
        if (trustedTime != null) {
            info.setBestSignatureTime(getDateFormatterWithGMTZone().format(trustedTime));
        } else {
            info.setBestSignatureTime("");
        }
        return info;
    }

//    private List<SignatureScope> getSignatureScopes(SignatureData bDocSignature, List<String> dataFileNames) {
//        return bDocSignature.getOrigin().getReferences()
//                .stream()
//                .filter(r -> dataFileNames.contains(r.getURI())) //filters out Signed Properties
//                .map(BDOCQualifiedReportBuilder::mapDssReference)
//                .collect(Collectors.toList());
//    }

    private static SignatureScope mapDssReference(Reference reference) {
        SignatureScope signatureScope = new SignatureScope();
        signatureScope.setName(reference.getURI());
        signatureScope.setScope(FULL_SIGNATURE_SCOPE);
        signatureScope.setContent(FULL_DOCUMENT);
        return signatureScope;
    }

    private static boolean isRepeatingMessage(List<String> dssMessages, String digidoc4jExceptionMessage) {
        for (String dssMessage : dssMessages) {
            if (digidoc4jExceptionMessage.contains(dssMessage)) {
                return true;
            }
        }
        return false;
    }

    private String getSignatureFormat(Signature profile) {
        return XADES_FORMAT_PREFIX;
    }
}
