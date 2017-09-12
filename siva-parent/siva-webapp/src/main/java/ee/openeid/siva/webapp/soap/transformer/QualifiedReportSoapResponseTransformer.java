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

import ee.openeid.siva.validation.document.report.Report;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.webapp.soap.Error;
import ee.openeid.siva.webapp.soap.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

@Component
public class QualifiedReportSoapResponseTransformer {
    private static final String EU_DETAILED_REPORT_PACKAGE = "eu.europa.esig.dss.validation.detailed_report";
    private static Policy toSoapResponsePolicy(ee.openeid.siva.validation.document.report.Policy policy) {
        Policy responsePolicy = new Policy();
        responsePolicy.setPolicyDescription(policy.getPolicyDescription());
        responsePolicy.setPolicyName(policy.getPolicyName());
        responsePolicy.setPolicyUrl(policy.getPolicyUrl());
        return responsePolicy;
    }

    private static Info toSoapResponseSignatureInfo(ee.openeid.siva.validation.document.report.Info signatureInfo) {
        Info responseSignatureInfo = new Info();
        responseSignatureInfo.setBestSignatureTime(signatureInfo.getBestSignatureTime());
        return responseSignatureInfo;
    }

    public ValidateDocumentResponse toSoapResponse(Report report) {
        ValidateDocumentResponse validateDocumentResponse = new ValidateDocumentResponse();

        QualifiedReport qualifiedReport = new QualifiedReport();
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        qualifiedReport.setSignatureForm(validationConclusion.getSignatureForm());
        qualifiedReport.setDocumentName(validationConclusion.getDocumentName());

        qualifiedReport.setPolicy(toSoapResponsePolicy(validationConclusion.getPolicy()));

        qualifiedReport.setSignaturesCount(validationConclusion.getSignaturesCount());
        if (validationConclusion.getSignatures() != null)
            qualifiedReport.setSignatures(toSoapResponseSignatures(validationConclusion.getSignatures()));
        if (validationConclusion.getValidationWarnings() != null)
            qualifiedReport.setValidationWarnings(toSoapResponseValidationWarnings(validationConclusion.getValidationWarnings()));

        qualifiedReport.setValidSignaturesCount(validationConclusion.getValidSignaturesCount());
        qualifiedReport.setValidationTime(validationConclusion.getValidationTime());
        if (validationConclusion.getTimeStampTokens() != null)
            qualifiedReport.setTimeStampTokens(toSoapResponseResponseTimeStamps(validationConclusion.getTimeStampTokens()));

        validateDocumentResponse.setValidationConclusion(qualifiedReport);
        if (report instanceof ee.openeid.siva.validation.document.report.DetailedReport) {
            ee.openeid.siva.webapp.soap.DetailedReport xmlDetailedReport = toSoapDetailedReport(
                    ((ee.openeid.siva.validation.document.report.DetailedReport) report).getValidationProcess());
            validateDocumentResponse.setValidationProcess(xmlDetailedReport);
        }
        return validateDocumentResponse;
    }

    @SuppressWarnings("unchecked")
    private ee.openeid.siva.webapp.soap.DetailedReport toSoapDetailedReport(eu.europa.esig.dss.jaxb.detailedreport.DetailedReport detailedReport) {
        try {
            if (detailedReport == null)
                return null;
            JAXBContext marshallerContext = JAXBContext.newInstance(eu.europa.esig.dss.jaxb.detailedreport.DetailedReport.class);
            Marshaller marshaller = marshallerContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(detailedReport, writer);
            String requestString = writer.toString();

            JAXBContext unMarshallerContext = JAXBContext.newInstance(EU_DETAILED_REPORT_PACKAGE);
            Unmarshaller unmarshaller = unMarshallerContext.createUnmarshaller();
            StringReader reader = new StringReader(requestString);

            JAXBElement<eu.europa.esig.dss.validation.detailed_report.DetailedReport> detailedReportJAXBElement = (JAXBElement<eu.europa.esig.dss.validation.detailed_report.DetailedReport>) unmarshaller.unmarshal(reader);
            return transformDetailReport(detailedReportJAXBElement.getValue());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private ee.openeid.siva.webapp.soap.DetailedReport transformDetailReport(eu.europa.esig.dss.validation.detailed_report.DetailedReport euDetailReport) {
        ee.openeid.siva.webapp.soap.DetailedReport detailedReport = new ee.openeid.siva.webapp.soap.DetailedReport();
        detailedReport.setQMatrixBlock(euDetailReport.getQMatrixBlock());
        detailedReport.getSignatures().addAll(euDetailReport.getSignatures());
        detailedReport.getBasicBuildingBlocks().addAll(euDetailReport.getBasicBuildingBlocks());
        return detailedReport;
    }

    private QualifiedReport.ValidationWarnings toSoapResponseValidationWarnings(List<ee.openeid.siva.validation.document.report.ValidationWarning> validationWarnings) {
        QualifiedReport.ValidationWarnings responseValidationWarnings = new QualifiedReport.ValidationWarnings();
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

    private QualifiedReport.TimeStampTokens toSoapResponseResponseTimeStamps(List<TimeStampTokenValidationData> timeStampTokenValidationDataList) {
        QualifiedReport.TimeStampTokens responseTimeStamps = new QualifiedReport.TimeStampTokens();

        timeStampTokenValidationDataList.stream()
                .map(this::getTimeStampTokenData)
                .forEach(tst -> responseTimeStamps.getTimeStampToken().add(tst));
        return responseTimeStamps;
    }

    private QualifiedReport.Signatures toSoapResponseSignatures(List<ee.openeid.siva.validation.document.report.SignatureValidationData> signatures) {
        QualifiedReport.Signatures responseSignatures = new QualifiedReport.Signatures();

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
