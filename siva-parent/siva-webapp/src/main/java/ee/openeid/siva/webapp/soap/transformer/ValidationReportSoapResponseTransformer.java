/*
 * Copyright 2020 Riigi Infosüsteemide Amet
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

import ee.openeid.siva.validation.document.report.DetailedReport;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.webapp.soap.response.Certificate;
import ee.openeid.siva.webapp.soap.response.Error;
import ee.openeid.siva.webapp.soap.response.Info;
import ee.openeid.siva.webapp.soap.response.Policy;
import ee.openeid.siva.webapp.soap.response.SignatureProductionPlace;
import ee.openeid.siva.webapp.soap.response.SignatureScope;
import ee.openeid.siva.webapp.soap.response.SignatureValidationData;
import ee.openeid.siva.webapp.soap.response.SignerRole;
import ee.openeid.siva.webapp.soap.response.SubjectDistinguishedName;
import ee.openeid.siva.webapp.soap.response.ValidationConclusion;
import ee.openeid.siva.webapp.soap.response.ValidationWarning;
import ee.openeid.siva.webapp.soap.response.Warning;
import ee.openeid.siva.webapp.soap.response.*;
import ee.openeid.siva.webapp.soap.transformer.report.DetailedReportTransformer;
import ee.openeid.siva.webapp.soap.transformer.report.DiagnosticDataTransformer;
import eu.europa.esig.dss.detailedreport.jaxb.XmlDetailedReport;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ValidationReportSoapResponseTransformer {

    private static final DetailedReportTransformer DETAILED_REPORT_TRANSFORMER = new DetailedReportTransformer();
    private static final DiagnosticDataTransformer DIAGNOSTIC_DATA_TRANSFORMER = new DiagnosticDataTransformer();

    private static Policy toSoapResponsePolicy(ee.openeid.siva.validation.document.report.Policy policy) {
        Policy responsePolicy = new Policy();
        responsePolicy.setPolicyDescription(policy.getPolicyDescription());
        responsePolicy.setPolicyName(policy.getPolicyName());
        responsePolicy.setPolicyUrl(policy.getPolicyUrl());
        return responsePolicy;
    }

    private static Info toSoapResponseSignatureInfo(ee.openeid.siva.validation.document.report.Info signatureInfo) {
        if (signatureInfo == null)
            return null;
        Info responseSignatureInfo = new Info();
        responseSignatureInfo.setTimestampCreationTime(signatureInfo.getTimestampCreationTime());
        responseSignatureInfo.setOcspResponseCreationTime(signatureInfo.getOcspResponseCreationTime());
        responseSignatureInfo.setBestSignatureTime(signatureInfo.getBestSignatureTime());
        responseSignatureInfo.setTimeAssertionMessageImprint(signatureInfo.getTimeAssertionMessageImprint());
        responseSignatureInfo.setSigningReason(signatureInfo.getSigningReason());

        if (signatureInfo.getSignerRole() != null) {
            responseSignatureInfo.getSignerRole().addAll(toSoapSignerRole(signatureInfo.getSignerRole()));
        }

        responseSignatureInfo.setSignatureProductionPlace(toSoapSignatureProductionPlace(signatureInfo.getSignatureProductionPlace()));
        return responseSignatureInfo;
    }

    private static List<SignerRole> toSoapSignerRole(List<ee.openeid.siva.validation.document.report.SignerRole> signerRole) {
        return signerRole.stream()
                .map(ValidationReportSoapResponseTransformer::toSoapSignerRole)
                .collect(Collectors.toList());
    }

    private static SignerRole toSoapSignerRole(ee.openeid.siva.validation.document.report.SignerRole signerRole) {
        SignerRole soapSignerRole = new SignerRole();
        soapSignerRole.setClaimedRole(signerRole.getClaimedRole());
        return soapSignerRole;
    }

    private static SignatureProductionPlace toSoapSignatureProductionPlace(
            ee.openeid.siva.validation.document.report.SignatureProductionPlace signatureProductionPlace) {
        if (signatureProductionPlace == null) {
            return null;
        }

        SignatureProductionPlace soapSignatureProductionPlace = new SignatureProductionPlace();
        soapSignatureProductionPlace.setCountryName(signatureProductionPlace.getCountryName());
        soapSignatureProductionPlace.setStateOrProvince(signatureProductionPlace.getStateOrProvince());
        soapSignatureProductionPlace.setCity(signatureProductionPlace.getCity());
        soapSignatureProductionPlace.setPostalCode(signatureProductionPlace.getPostalCode());
        return soapSignatureProductionPlace;
    }

    public ValidationReport toSoapResponse(SimpleReport report) {
        ValidationReport validationReport = new ValidationReport();
        ValidationConclusion responseValidationConclusion = toSoapValidationConclusion(report.getValidationConclusion());
        validationReport.setValidationConclusion(responseValidationConclusion);

        if (report instanceof DetailedReport) {
            XmlDetailedReport xmlDetailedReport = ((DetailedReport) report).getValidationProcess();
            validationReport.setValidationProcess(DETAILED_REPORT_TRANSFORMER.transform(xmlDetailedReport));

        } else if (report instanceof DiagnosticReport) {
            XmlDiagnosticData diagnosticData = ((DiagnosticReport) report).getDiagnosticData();
            validationReport.setDiagnosticData(DIAGNOSTIC_DATA_TRANSFORMER.transform(diagnosticData));
        }
        return validationReport;
    }

    private ValidationConclusion toSoapValidationConclusion(ee.openeid.siva.validation.document.report.ValidationConclusion validationConclusion) {
        ValidationConclusion responseValidationConclusion = new ValidationConclusion();
        responseValidationConclusion.setSignatureForm(validationConclusion.getSignatureForm());
        responseValidationConclusion.setPolicy(toSoapResponsePolicy(validationConclusion.getPolicy()));
        responseValidationConclusion.setValidationLevel(validationConclusion.getValidationLevel());
        if (validationConclusion.getValidatedDocument() != null)
            responseValidationConclusion.setValidatedDocument(toSoapValidatedDocument(validationConclusion.getValidatedDocument()));
        responseValidationConclusion.setSignaturesCount(validationConclusion.getSignaturesCount());
        if (validationConclusion.getSignatures() != null)
            responseValidationConclusion.setSignatures(toSoapResponseSignatures(validationConclusion.getSignatures()));
        if (validationConclusion.getValidationWarnings() != null)
            responseValidationConclusion.setValidationWarnings(toSoapResponseValidationWarnings(validationConclusion.getValidationWarnings()));

        responseValidationConclusion.setValidSignaturesCount(validationConclusion.getValidSignaturesCount());
        responseValidationConclusion.setValidationTime(validationConclusion.getValidationTime());
        if (validationConclusion.getTimeStampTokens() != null)
            responseValidationConclusion.setTimeStampTokens(toSoapResponseResponseTimeStamps(validationConclusion.getTimeStampTokens()));
        return responseValidationConclusion;
    }

    private ValidatedDocumentData toSoapValidatedDocument(ValidatedDocument validatedDocument) {
        ValidatedDocumentData validatedDocumentData = new ValidatedDocumentData();
        validatedDocumentData.setFilename(validatedDocument.getFilename());
        validatedDocumentData.setFileHash(validatedDocument.getFileHash());
        validatedDocumentData.setHashAlgo(validatedDocument.getHashAlgo());
        return validatedDocumentData;

    }

    private ValidationConclusion.ValidationWarnings toSoapResponseValidationWarnings(List<ee.openeid.siva.validation.document.report.ValidationWarning> validationWarnings) {
        ValidationConclusion.ValidationWarnings responseValidationWarnings = new ValidationConclusion.ValidationWarnings();
        validationWarnings.stream()
                .map(this::mapValidationWarning)
                .forEach(validationWarning -> responseValidationWarnings.getValidationWarning().add(validationWarning));
        return responseValidationWarnings;
    }

    private ValidationWarning mapValidationWarning(ee.openeid.siva.validation.document.report.ValidationWarning validationWarning) {
        ValidationWarning responseValidationWarning = new ValidationWarning();
        responseValidationWarning.setContent(validationWarning.getContent());
        return responseValidationWarning;
    }

    private ValidationConclusion.TimeStampTokens toSoapResponseResponseTimeStamps(List<TimeStampTokenValidationData> timeStampTokenValidationDataList) {
        ValidationConclusion.TimeStampTokens responseTimeStamps = new ValidationConclusion.TimeStampTokens();

        timeStampTokenValidationDataList.stream()
                .map(this::getTimeStampTokenData)
                .forEach(tst -> responseTimeStamps.getTimeStampToken().add(tst));
        return responseTimeStamps;
    }

    private ValidationConclusion.Signatures toSoapResponseSignatures(List<ee.openeid.siva.validation.document.report.SignatureValidationData> signatures) {
        ValidationConclusion.Signatures responseSignatures = new ValidationConclusion.Signatures();

        for (ee.openeid.siva.validation.document.report.SignatureValidationData signature : signatures) {
            SignatureValidationData responseSignature = getSignatureValidationData(signature);
            responseSignatures.getSignature().add(responseSignature);
        }

        return responseSignatures;
    }

    private TimeStampTokenData getTimeStampTokenData(TimeStampTokenValidationData timeStampTokenValidationData) {
        TimeStampTokenData timeStampTokenData = new TimeStampTokenData();
        timeStampTokenData.setIndication(Indication.valueOf(timeStampTokenValidationData.getIndication().name()));
        timeStampTokenData.setSignedBy(timeStampTokenValidationData.getSignedBy());
        timeStampTokenData.setSignedTime(timeStampTokenValidationData.getSignedTime());
        if (timeStampTokenValidationData.getError() != null)
            timeStampTokenData.setErrors(toSoapResponseTimeStampsErrors(timeStampTokenValidationData.getError()));
        return timeStampTokenData;
    }

    private SignatureValidationData getSignatureValidationData(ee.openeid.siva.validation.document.report.SignatureValidationData signature) {
        SignatureValidationData responseSignature = new SignatureValidationData();
        responseSignature.setId(signature.getId());
        responseSignature.setClaimedSigningTime(signature.getClaimedSigningTime());
        responseSignature.setSignatureFormat(signature.getSignatureFormat());
        responseSignature.setSignatureMethod(signature.getSignatureMethod());
        responseSignature.setSignatureLevel(signature.getSignatureLevel());
        responseSignature.setSignedBy(signature.getSignedBy());
        responseSignature.setSubjectDistinguishedName(toSoapResponseSignatureSubjectDN(signature.getSubjectDistinguishedName()));
        responseSignature.setIndication(Indication.fromValue(signature.getIndication()));
        responseSignature.setSubIndication(signature.getSubIndication());
        responseSignature.setInfo(toSoapResponseSignatureInfo(signature.getInfo()));
        responseSignature.setErrors(toSoapResponseSignatureErrors(signature.getErrors()));
        responseSignature.setWarnings(toSoapResponseSignatureWarnings(signature.getWarnings()));
        responseSignature.setSignatureScopes(toSoapResponseSignatureScopes(signature.getSignatureScopes()));
        responseSignature.setCertificates(toSoapResponseCertificates(signature.getCertificates()));

        return responseSignature;
    }

    private SignatureValidationData.Certificates toSoapResponseCertificates(List<ee.openeid.siva.validation.document.report.Certificate> certificateList) {
        if (certificateList == null) {
            return null;
        }
        SignatureValidationData.Certificates certificates = new SignatureValidationData.Certificates();
        List<Certificate> responseCertificateList = certificateList.stream()
                .map(this::toSoapResponseCertificate)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        certificates.getCertificate().addAll(responseCertificateList);
        return certificates;
    }

    private Certificate toSoapResponseCertificate(ee.openeid.siva.validation.document.report.Certificate certificate) {
        if (certificate == null) {
            return null;
        }
        Certificate responseCertificate = new Certificate();
        responseCertificate.setContent(certificate.getContent());
        responseCertificate.setCommonName(certificate.getCommonName());
        if (certificate.getType() != null) {
            responseCertificate.setType(certificate.getType().name());
        }
        responseCertificate.setIssuer(toSoapResponseCertificate(certificate.getIssuer()));
        return responseCertificate;
    }


    private SubjectDistinguishedName toSoapResponseSignatureSubjectDN(ee.openeid.siva.validation.document.report.SubjectDistinguishedName subjectDistinguishedName) {
        if (subjectDistinguishedName == null) {
            return null;
        }

        SubjectDistinguishedName responseSubjectDN = new SubjectDistinguishedName();
        responseSubjectDN.setCommonName(subjectDistinguishedName.getCommonName());
        responseSubjectDN.setSerialNumber(subjectDistinguishedName.getSerialNumber());
        responseSubjectDN.setGivenName(subjectDistinguishedName.getGivenName());
        responseSubjectDN.setSurname(subjectDistinguishedName.getSurname());
        return responseSubjectDN;
    }

    private TimeStampTokenData.Errors toSoapResponseTimeStampsErrors(List<ee.openeid.siva.validation.document.report.Error> timeStampsErrors) {
        TimeStampTokenData.Errors responseTimeStampsErrors = new TimeStampTokenData.Errors();

        for (ee.openeid.siva.validation.document.report.Error timeStampError : timeStampsErrors) {
            Error responseTimeStampError = new Error();
            responseTimeStampError.setContent(timeStampError.getContent());
            responseTimeStampsErrors.getError().add(responseTimeStampError);
        }
        return responseTimeStampsErrors;
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
            responseSignatureWarning.setContent(signatureWarning.getContent());
            responseSignatureWarnings.getWarning().add(responseSignatureWarning);
        }

        return responseSignatureWarnings;
    }

    private SignatureValidationData.SignatureScopes toSoapResponseSignatureScopes(List<ee.openeid.siva.validation.document.report.SignatureScope> signatureScopes) {
        if (signatureScopes == null) {
            return null;
        }

        SignatureValidationData.SignatureScopes responseSignatureScopes = new SignatureValidationData.SignatureScopes();

        for (ee.openeid.siva.validation.document.report.SignatureScope signatureScope : signatureScopes) {
            SignatureScope responseSignatureScope = new SignatureScope();
            responseSignatureScope.setContent(signatureScope.getContent());
            responseSignatureScope.setName(signatureScope.getName());
            responseSignatureScope.setScope(signatureScope.getScope());
            responseSignatureScope.setHashAlgo(signatureScope.getHashAlgo());
            responseSignatureScope.setHash(signatureScope.getHash());
            responseSignatureScopes.getSignatureScope().add(responseSignatureScope);
        }

        return responseSignatureScopes;
    }
}
