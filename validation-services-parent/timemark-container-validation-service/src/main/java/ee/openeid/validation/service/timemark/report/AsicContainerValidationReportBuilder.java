/*
 * Copyright 2019 - 2024 Riigi Infosüsteemi Amet
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
import ee.openeid.siva.validation.document.report.Certificate;
import ee.openeid.siva.validation.document.report.CertificateType;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.ValidationWarning;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import eu.europa.esig.dss.enumerations.SubIndication;
import org.digidoc4j.Container;
import org.digidoc4j.Signature;
import org.digidoc4j.SignatureProfile;
import org.digidoc4j.ValidationResult;
import org.digidoc4j.impl.asic.AsicSignature;
import org.digidoc4j.impl.asic.asice.AsicESignature;

import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ee.openeid.validation.service.timemark.util.SignatureScopeParser.getAsicSignatureScopes;
import static ee.openeid.validation.service.timemark.util.SignatureCertificateParser.getCertificate;

public class AsicContainerValidationReportBuilder extends TimemarkContainerValidationReportBuilder {
    public AsicContainerValidationReportBuilder(Container container, ValidationDocument validationDocument, ValidationPolicy validationPolicy, ValidationResult validationResult, boolean isReportSignatureEnabled) {
        super(container, validationDocument, validationPolicy, validationResult, isReportSignatureEnabled);
    }

    @Override
    protected SignatureValidationData.Indication getIndication(Signature signature, Map<String, ValidationResult> signatureValidationResults) {
        ValidationResult signatureValidationResult = signatureValidationResults.get(signature.getUniqueId());
        if (signatureValidationResult.isValid() && validationResult.getErrors().isEmpty()) {
            return SignatureValidationData.Indication.TOTAL_PASSED;
        } else if (REPORT_INDICATION_INDETERMINATE.equals(getDssSimpleReport((AsicESignature) signature).getIndication(signature.getUniqueId()).name())
                && validationResult.getErrors().isEmpty()) {
            return SignatureValidationData.Indication.INDETERMINATE;
        } else {
            return SignatureValidationData.Indication.TOTAL_FAILED;
        }
    }

    @Override
    protected String getSubIndication(Signature signature, Map<String, ValidationResult> signatureValidationResults) {
        if (getIndication(signature, signatureValidationResults) == SignatureValidationData.Indication.TOTAL_PASSED) {
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
        return Collections.emptyList();
    }

    @Override
    List<SignatureScope> getSignatureScopes(Signature signature, List<String> dataFilenames) {
        return getAsicSignatureScopes((AsicSignature) signature, dataFilenames);
    }

    @Override
    String getSignatureForm() {
        return BDOC_SIGNATURE_FORM;
    }

    @Override
    String getSignatureFormat(SignatureProfile profile) {
        return XADES_FORMAT_PREFIX + profile.toString();
    }
}
