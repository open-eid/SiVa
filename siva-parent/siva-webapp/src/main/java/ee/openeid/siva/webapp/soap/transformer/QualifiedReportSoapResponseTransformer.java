/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.webapp.soap.transformer;

import ee.openeid.siva.webapp.soap.Error;
import ee.openeid.siva.webapp.soap.Indication;
import ee.openeid.siva.webapp.soap.Info;
import ee.openeid.siva.webapp.soap.Policy;
import ee.openeid.siva.webapp.soap.QualifiedReport;
import ee.openeid.siva.webapp.soap.SignatureScope;
import ee.openeid.siva.webapp.soap.SignatureValidationData;
import ee.openeid.siva.webapp.soap.ValidationWarning;
import ee.openeid.siva.webapp.soap.Warning;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QualifiedReportSoapResponseTransformer {

    public QualifiedReport toSoapResponse(ee.openeid.siva.validation.document.report.QualifiedReport report) {
        QualifiedReport responseReport = new QualifiedReport();
        responseReport.setSignatureForm(report.getSignatureForm());
        responseReport.setDocumentName(report.getDocumentName());
        responseReport.setPolicy(toSoapResponsePolicy(report.getPolicy()));
        responseReport.setSignaturesCount(report.getSignaturesCount());
        responseReport.setSignatures(toSoapResponseSignatures(report.getSignatures()));
        responseReport.setValidationWarnings(toSoapResponseValidationWarnings(report.getValidationWarnings()));
        responseReport.setValidSignaturesCount(report.getValidSignaturesCount());
        responseReport.setValidationTime(report.getValidationTime());
        return responseReport;
    }

    private QualifiedReport.ValidationWarnings toSoapResponseValidationWarnings(List<ee.openeid.siva.validation.document.report.ValidationWarning> validationWarnings) {
        QualifiedReport.ValidationWarnings responseValidationWarnings = new QualifiedReport.ValidationWarnings();
        for (ee.openeid.siva.validation.document.report.ValidationWarning validationWarning : validationWarnings) {
            ValidationWarning responseValidationWarning = new ValidationWarning();
            responseValidationWarning.setContent(validationWarning.getContent());
            responseValidationWarnings.getValidationWarning().add(responseValidationWarning);
        }
        return responseValidationWarnings;
    }

    private static Policy toSoapResponsePolicy(ee.openeid.siva.validation.document.report.Policy policy) {
        Policy responsePolicy = new Policy();
        responsePolicy.setPolicyDescription(policy.getPolicyDescription());
        responsePolicy.setPolicyName(policy.getPolicyName());
        responsePolicy.setPolicyUrl(policy.getPolicyUrl());
        return responsePolicy;
    }

    private QualifiedReport.Signatures toSoapResponseSignatures(List<ee.openeid.siva.validation.document.report.SignatureValidationData> signatures) {
        QualifiedReport.Signatures responseSignatures = new QualifiedReport.Signatures();

        for (ee.openeid.siva.validation.document.report.SignatureValidationData signature : signatures) {
            SignatureValidationData responseSignature = getSignatureValidationData(signature);
            responseSignatures.getSignature().add(responseSignature);
        }

        return responseSignatures;
    }

    private SignatureValidationData getSignatureValidationData(ee.openeid.siva.validation.document.report.SignatureValidationData signature) {
        SignatureValidationData responseSignature = new SignatureValidationData();
        responseSignature.setId(signature.getId());
        responseSignature.setClaimedSigningTime(signature.getClaimedSigningTime());
        responseSignature.setSignatureFormat(signature.getSignatureFormat());
        responseSignature.setSignatureLevel(signature.getSignatureLevel());
        responseSignature.setSignedBy(signature.getSignedBy());
        responseSignature.setIndication(Indication.fromValue(signature.getIndication()));
        responseSignature.setSubIndication(signature.getSubIndication());
        responseSignature.setInfo(toSoapResponseSignatureInfo(signature.getInfo()));
        responseSignature.setErrors(toSoapResponseSignatureErrors(signature.getErrors()));
        responseSignature.setWarnings(toSoapResponseSignatureWarnings(signature.getWarnings()));
        responseSignature.setSignatureScopes(toSoapResponseSignatureScopes(signature.getSignatureScopes()));

        return responseSignature;
    }

    private static Info toSoapResponseSignatureInfo(ee.openeid.siva.validation.document.report.Info signatureInfo) {
        Info responseSignatureInfo = new Info();
        responseSignatureInfo.setBestSignatureTime(signatureInfo.getBestSignatureTime());
        return responseSignatureInfo;
    }

    private SignatureValidationData.Errors toSoapResponseSignatureErrors(List<ee.openeid.siva.validation.document.report.Error> signatureErrors) {
        SignatureValidationData.Errors responseSignatureErrors = new SignatureValidationData.Errors();

        for (ee.openeid.siva.validation.document.report.Error signatureError : signatureErrors) {
            Error responseSignatureError = new Error();
            responseSignatureError.setContent(signatureError.getContent());

            responseSignatureErrors.getError().add(responseSignatureError);
        }

        return responseSignatureErrors;
    }

    private SignatureValidationData.Warnings toSoapResponseSignatureWarnings(List<ee.openeid.siva.validation.document.report.Warning> signatureWarnings) {
        SignatureValidationData.Warnings responseSignatureWarnings = new SignatureValidationData.Warnings();

        for (ee.openeid.siva.validation.document.report.Warning signatureWarning : signatureWarnings) {
            Warning responseSignatureWarning = new Warning();
            responseSignatureWarning.setDescription(signatureWarning.getDescription());
            responseSignatureWarnings.getWarning().add(responseSignatureWarning);
        }

        return responseSignatureWarnings;
    }

    private SignatureValidationData.SignatureScopes toSoapResponseSignatureScopes(List<ee.openeid.siva.validation.document.report.SignatureScope> signatureScopes) {
        SignatureValidationData.SignatureScopes responseSignatureScopes = new SignatureValidationData.SignatureScopes();

        for (ee.openeid.siva.validation.document.report.SignatureScope signatureScope : signatureScopes) {
            SignatureScope responseSignatureScope = new SignatureScope();
            responseSignatureScope.setContent(signatureScope.getContent());
            responseSignatureScope.setName(signatureScope.getName());
            responseSignatureScope.setScope(signatureScope.getScope());

            responseSignatureScopes.getSignatureScope().add(responseSignatureScope);
        }

        return responseSignatureScopes;
    }

}
