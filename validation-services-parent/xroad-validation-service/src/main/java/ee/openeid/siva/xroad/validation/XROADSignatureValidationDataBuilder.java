/*
 * Copyright 2020 Riigi Infosüsteemide Amet
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
import ee.openeid.siva.validation.document.report.Info;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SubjectDistinguishedName;
import ee.openeid.siva.validation.util.CertUtil;
import ee.openeid.siva.validation.util.SubjectDNParser;
import ee.ria.xroad.common.CodedException;
import ee.ria.xroad.common.asic.AsicContainer;
import ee.ria.xroad.common.asic.AsicContainerVerifier;
import ee.ria.xroad.common.signature.Signature;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.SignatureValidationData.Indication.TOTAL_FAILED;
import static ee.openeid.siva.validation.document.report.SignatureValidationData.Indication.TOTAL_PASSED;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.getDateFormatterWithGMTZone;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.valueNotPresent;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class XROADSignatureValidationDataBuilder {

    private static final String XADES_FORMAT_PREFIX = "XAdES_BASELINE_";
    private static final String XADES_BASELINE_B_BES_SUFFIX = "B_BES";
    private static final String XADES_BASELINE_LT_SUFFIX = "LT";

    private final AsicContainerVerifier verifier;
    private final List<CodedException> validationExceptions;

    SignatureValidationData build() {
        SignatureValidationData signatureValidationData = new SignatureValidationData();
        signatureValidationData.setId(getSignatureId(verifier.getSignature()));
        signatureValidationData.setSignatureFormat(getSignatureFormat(verifier.getAsic()));
        signatureValidationData.setSignatureMethod(getSignatureMethod(verifier.getSignature()));
        signatureValidationData.setSignatureLevel(valueNotPresent());
        signatureValidationData.setSignedBy(getSignedBy(verifier.getSignerCert()));
        signatureValidationData.setSubjectDistinguishedName(getSubjectDistinguishedName(verifier.getSignerCert()));
        signatureValidationData.setSubIndication(valueNotPresent());
        signatureValidationData.setErrors(mapValidationExceptionsToErrors(validationExceptions));
        signatureValidationData.setSignatureScopes(getSignatureScopes());

        signatureValidationData.setClaimedSigningTime(valueNotPresent());

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
        return valueNotPresent();
    }

    private String getCountryCode(X509Certificate signerCert) {
        return signerCert != null ? CertUtil.getCountryCode(signerCert) : valueNotPresent();
    }

    private String getSignedBy(X509Certificate signerCert) {
        if (signerCert != null) {
            return emptyWhenNull(CertUtil.getCommonName(signerCert));
        }
        return valueNotPresent();
    }

    private SubjectDistinguishedName getSubjectDistinguishedName(X509Certificate certificate) {
        if (certificate == null) {
            return null;
        }

        String subjectDistinguishedName = certificate.getSubjectDN().getName();
        return SubjectDistinguishedName.builder()
               .commonName(SubjectDNParser.parse(subjectDistinguishedName, SubjectDNParser.RDN.CN))
               .serialNumber(SubjectDNParser.parse(subjectDistinguishedName, SubjectDNParser.RDN.SERIALNUMBER))
               .build();
    }

    private String getSignatureFormat(AsicContainer asicContainer) {
        if (asicContainer != null && asicContainer.getSignature() != null) {
            return XADES_FORMAT_PREFIX + (asicContainer.getSignature().isBatchSignature() ? XADES_BASELINE_B_BES_SUFFIX : XADES_BASELINE_LT_SUFFIX);
        }
        return valueNotPresent();
    }

    private String getSignatureMethod(Signature signature) {
        if (signature == null || signature.getXmlSignature() == null || signature.getXmlSignature().getSignedInfo() == null) {
            return valueNotPresent();
        }

        return signature.getXmlSignature().getSignedInfo().getSignatureMethodURI();
    }

    private List<SignatureScope> getSignatureScopes() {
        return null;
    }

    private List<Error> mapValidationExceptionsToErrors(Collection<CodedException> validationExceptions) {
        return validationExceptions.stream().map(this::mapCodedExceptionToError).collect(Collectors.toList());
    }

    private Error mapCodedExceptionToError(CodedException codedException) {
        Error error = new Error();
        error.setContent(codedException.getFaultCode() + ": " + codedException.getFaultString());
        return error;
    }

    private SignatureValidationData.Indication getIndication(Collection<Error> errors) {
        return errors.isEmpty() ? TOTAL_PASSED : TOTAL_FAILED;
    }

    private Info getInfo(Date timestampDate) {
        String bestSignatureTime = timestampDate != null ? emptyWhenNull(getDateFormatterWithGMTZone().format(timestampDate)) : valueNotPresent();

        if (StringUtils.isNotBlank(bestSignatureTime)) {
            Info info = new Info();
            info.setBestSignatureTime(bestSignatureTime);
            return info;
        } else {
            return null;
        }
    }
}
