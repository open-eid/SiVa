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

package ee.openeid.siva.xroad.validation;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.siva.validation.util.CertUtil;
import ee.ria.xroad.common.CodedException;
import ee.ria.xroad.common.asic.AsicContainer;
import ee.ria.xroad.common.asic.AsicContainerVerifier;
import ee.ria.xroad.common.signature.Signature;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.SignatureValidationData.Indication.TOTAL_FAILED;
import static ee.openeid.siva.validation.document.report.SignatureValidationData.Indication.TOTAL_PASSED;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.createReportPolicy;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;


public class XROADQualifiedReportBuilder {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String XADES_FORMAT_PREFIX = "XAdES_BASELINE_";
    private static final String XADES_BASELINE_B_BES_SUFFIX = "B_BES";
    private static final String XADES_BASELINE_LT_SUFFIX = "LT";
    private static final String GREENWICH_MEAN_TIME = "Etc/GMT";
    private static final String XROAD_SIGNATURE_FORM = "ASiC_E_batchsignature";
    private static final String ASICE_SIGNATURE_FORM = "ASiC_E";

    private static final Logger LOGGER = LoggerFactory.getLogger(XROADQualifiedReportBuilder.class);

    private AsicContainerVerifier verifier;
    private String documentName;
    private Date validationTime;
    private ValidationPolicy validationPolicy;
    private List<CodedException> validationExceptions;

    public XROADQualifiedReportBuilder(AsicContainerVerifier verifier, String documentName, Date validationTime, ValidationPolicy validationPolicy, CodedException... exceptions) {
        this.verifier = verifier;
        this.documentName = documentName;
        this.validationTime = validationTime;
        this.validationPolicy = validationPolicy;
        this.validationExceptions = Arrays.asList(exceptions);
    }

    public QualifiedReport build() {
        QualifiedReport qualifiedReport = new QualifiedReport();
        qualifiedReport.setPolicy(createReportPolicy(validationPolicy));
        qualifiedReport.setValidationTime(getDateFormatterWithGMTZone().format(validationTime));
        qualifiedReport.setDocumentName(documentName);
        qualifiedReport.setSignatureForm(getSignatureForm(verifier.getAsic()));
        qualifiedReport.setSignaturesCount(getTotalSignatureCount(verifier.getSignature()));
        qualifiedReport.setSignatures(Collections.singletonList(createSignatureValidationData()));
        qualifiedReport.setValidSignaturesCount(
                qualifiedReport.getSignatures()
                        .stream()
                        .filter(signatures -> StringUtils.equals(signatures.getIndication(), TOTAL_PASSED.toString()))
                        .collect(Collectors.toList())
                        .size());
        return qualifiedReport;
    }

    private int getTotalSignatureCount(Signature signature) {
        return signature != null ? 1 : 0;
    }

    private SignatureValidationData createSignatureValidationData() {
        SignatureValidationData signatureValidationData = new SignatureValidationData();
        signatureValidationData.setId(getSignatureId(verifier.getSignature()));
        signatureValidationData.setSignatureFormat(getSignatureFormat(verifier.getAsic()));
        signatureValidationData.setSignatureLevel(getSignatureLevel());

        signatureValidationData.setSignedBy(getSignedBy(verifier.getSignerCert()));
        signatureValidationData.setSubIndication(getSubIndication());
        signatureValidationData.setErrors(mapValidationExceptionsToErrors(validationExceptions));
        signatureValidationData.setSignatureScopes(getSignatureScopes());

        signatureValidationData.setClaimedSigningTime(getClaimedSigningTime());

        signatureValidationData.setWarnings(Collections.emptyList());
        signatureValidationData.setInfo(getInfo(verifier.getTimestampDate()));

        signatureValidationData.setCountryCode(getCountryCode(verifier.getSignerCert()));

        signatureValidationData.setIndication(getIndication(signatureValidationData.getErrors()));
        return signatureValidationData;
    }

    private String getSignatureId(Signature signature) {
        if (signature != null && signature.getXmlSignature() != null) {
            return signature.getXmlSignature().getId();
        }
        return StringUtils.EMPTY;
    }

    private String getCountryCode(X509Certificate signerCert) {
        return signerCert != null ? CertUtil.getCountryCode(signerCert) : StringUtils.EMPTY;
    }

    private String getSignedBy(X509Certificate signerCert) {
        if (signerCert != null && signerCert.getSubjectX500Principal() != null) {
            return parseCNFromX500Principal(signerCert.getSubjectX500Principal());
        }
        return StringUtils.EMPTY;
    }

    private String getSignatureFormat(AsicContainer asicContainer) {
        if (asicContainer != null && asicContainer.getSignature() != null) {
            return XADES_FORMAT_PREFIX + (asicContainer.getSignature().isBatchSignature() ? XADES_BASELINE_B_BES_SUFFIX : XADES_BASELINE_LT_SUFFIX);
        }
        return StringUtils.EMPTY;

    }

    private String getSignatureForm(AsicContainer asicContainer) {
        if (asicContainer != null && asicContainer.getSignature() != null) {
            return asicContainer.getSignature().isBatchSignature() ? XROAD_SIGNATURE_FORM : ASICE_SIGNATURE_FORM;
        }
        return StringUtils.EMPTY;
    }

    private String getClaimedSigningTime() {
        return StringUtils.EMPTY;
    }

    private List<SignatureScope> getSignatureScopes() {
        SignatureScope scope = new SignatureScope();
        scope.setContent(StringUtils.EMPTY);
        scope.setName(StringUtils.EMPTY);
        scope.setScope(StringUtils.EMPTY);
        return Collections.singletonList(scope);
    }

    private List<Error> mapValidationExceptionsToErrors(Collection<CodedException> validationExceptions) {
        return validationExceptions.stream().map(this::mapCodedExceptionToError).collect(Collectors.toList());
    }

    private Error mapCodedExceptionToError(CodedException codedException) {
        Error error = new Error();
        error.setContent(codedException.getFaultCode() + ": " + codedException.getFaultString());
        return error;
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
        return subjectName.replaceAll("^\"|\"$", StringUtils.EMPTY);
    }

    private String getSignatureLevel() {
        return StringUtils.EMPTY;
    }

    private SignatureValidationData.Indication getIndication(Collection<Error> errors) {
        return errors.isEmpty() ? TOTAL_PASSED : TOTAL_FAILED;
    }

    private String getSubIndication() {
        return StringUtils.EMPTY;
    }

    private Info getInfo(Date timestampDate) {
        String bestSignatureTime = timestampDate != null ? emptyWhenNull(getDateFormatterWithGMTZone().format(timestampDate)) : StringUtils.EMPTY;
        Info info = new Info();
        info.setBestSignatureTime(bestSignatureTime);
        return info;
    }

}
