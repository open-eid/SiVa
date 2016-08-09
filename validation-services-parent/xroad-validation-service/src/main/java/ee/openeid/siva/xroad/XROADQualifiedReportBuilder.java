package ee.openeid.siva.xroad;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.ria.xroad.common.asic.AsicContainerVerifier;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.security.auth.x500.X500Principal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;


public class XROADQualifiedReportBuilder {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String XADES_FORMAT_PREFIX = "XAdES_BASELINE_";
    private static final String REPORT_INDICATION_INDETERMINATE = "INDETERMINATE";
    private static final String GREENWICH_MEAN_TIME = "Etc/GMT";
    private static final String XROAD_SIGNATURE_FORM = "ASiC_E_batchsignature";

    private static final Logger LOGGER = LoggerFactory.getLogger(XROADQualifiedReportBuilder.class);

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
        qualifiedReport.setSignatures(Collections.singletonList(createSignatureValidationData()));
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

    private SignatureValidationData createSignatureValidationData() throws Exception {
        SignatureValidationData signatureValidationData = new SignatureValidationData();
        signatureValidationData.setId(verifier.getSignature().getXmlSignature().getId());
        signatureValidationData.setSignatureFormat(XADES_FORMAT_PREFIX + "LT");
        signatureValidationData.setSignatureLevel(getSignatureLevel());
        signatureValidationData.setSignatureForm(getSignatureForm());
        //verifier.getAsic().getSignature().isBatchSignature();

        signatureValidationData.setSignedBy(parseCNFromX500Principal(verifier.getSignerCert().getSubjectX500Principal()));
        signatureValidationData.setIndication(getIndication());
        signatureValidationData.setSubIndication(getSubIndication());
        signatureValidationData.setErrors(getErrors());
        signatureValidationData.setSignatureScopes(getSignatureScopes());

        signatureValidationData.setClaimedSigningTime(getClaimedSigningTime());

        signatureValidationData.setWarnings(getWarnings());
        signatureValidationData.setInfo(getInfo());

        return signatureValidationData;
    }

    private String getSignatureForm() {
        //TODO: What to use when isBatchSignature() returns false?
        return verifier.getAsic().getSignature().isBatchSignature() ? XROAD_SIGNATURE_FORM : null;
    }

    private String getClaimedSigningTime() {
        //TODO: figure out if we should get it from DOM?
        return "";
    }

    private List<SignatureScope> getSignatureScopes() {
        //TODO: What should the actual scope be for XROAD signature?
        SignatureScope scope = new SignatureScope();
        scope.setContent("");
        scope.setName("");
        scope.setScope("");
        return Collections.singletonList(scope);
    }

    private List<Warning> getWarnings() {
        return Collections.emptyList();
    }

    private List<Error> getErrors() {
        return Collections.emptyList();
    }

    private String parseCNFromX500Principal(X500Principal x500Principal)  {
        String distinguishedName = x500Principal.getName();
        try {
            return readCommonNameFromDistinguishedName(distinguishedName);
        } catch (InvalidNameException e) {
            LOGGER.warn("Unable to parse CN from certificate, using distinguished name", e);
            return removeQuotes(distinguishedName);
        }
    }

    private String readCommonNameFromDistinguishedName(String distinguishedName) throws InvalidNameException {
        return new LdapName(distinguishedName)
                .getRdns()
                .stream()
                .filter(rdn -> StringUtils.equals("CN", rdn.getType()))
                .map(rdn -> rdn.getValue().toString())
                .findFirst()
                .orElse(removeQuotes(distinguishedName));
    }

    private SimpleDateFormat getDateFormatterWithGMTZone() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone(GREENWICH_MEAN_TIME));
        return sdf;
    }

    private String removeQuotes(String subjectName) {
        return subjectName.replaceAll("^\"|\"$", "");
    }

    private String getSignatureLevel() {
        return "random-level"; //TODO: Can't leave it to random level
    }

    private SignatureValidationData.Indication getIndication() {
        return SignatureValidationData.Indication.TOTAL_PASSED;
    }

    private String getSubIndication() {
        //TODO: can't get subindication from API is there another way to determine it?
        return "";
    }

    private Info getInfo() {
        Info info = new Info();
        Date trustedTime = verifier.getTimestampDate();
        info.setBestSignatureTime(emptyWhenNull(getDateFormatterWithGMTZone().format(trustedTime)));
        return info;
    }
}
