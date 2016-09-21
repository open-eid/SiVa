package ee.openeid.siva.webapp.soap.transformer;

import ee.openeid.siva.webapp.soap.*;
import ee.openeid.siva.webapp.soap.Error;
import ee.openeid.siva.webapp.soap.Info;
import ee.openeid.siva.webapp.soap.Policy;
import ee.openeid.siva.webapp.soap.QualifiedReport;
import ee.openeid.siva.webapp.soap.SignatureScope;
import ee.openeid.siva.webapp.soap.SignatureValidationData;
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
        responseReport.setValidSignaturesCount(report.getValidSignaturesCount());
        responseReport.setValidationTime(report.getValidationTime());
        return responseReport;
    }

    private Policy toSoapResponsePolicy(ee.openeid.siva.validation.document.report.Policy policy) {
        Policy responsePolicy = new Policy();
        responsePolicy.setPolicyDescription(policy.getPolicyDescription());
        responsePolicy.setPolicyName(policy.getPolicyName());
        responsePolicy.setPolicyUrl(policy.getPolicyUrl());
        return responsePolicy;
    }

    private QualifiedReport.Signatures toSoapResponseSignatures(List<ee.openeid.siva.validation.document.report.SignatureValidationData> signatures) {
        QualifiedReport.Signatures responseSignatures = new QualifiedReport.Signatures();
        for (ee.openeid.siva.validation.document.report.SignatureValidationData signature : signatures) {
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

            responseSignatures.getSignature().add(responseSignature);
        }
        return responseSignatures;
    }

    private Info toSoapResponseSignatureInfo(ee.openeid.siva.validation.document.report.Info signatureInfo) {
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
