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

import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.document.report.ValidatedDocument;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.webapp.soap.QualifiedReport;
import ee.openeid.siva.webapp.soap.ValidateDocumentResponse;
import eu.europa.esig.dss.jaxb.detailedreport.XmlQMatrixBlock;
import eu.europa.esig.dss.jaxb.detailedreport.XmlTLAnalysis;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class QualifiedReportSoapResponseTransformerTest {

    private QualifiedReportSoapResponseTransformer transformer = new QualifiedReportSoapResponseTransformer();

    @Test
    public void qualifiedSimpleReportIsCorrectlyTransformedToSoapResponseReport() {
        ValidationConclusion validationConclusion = createMockedValidationConclusion();
        SimpleReport simpleReport = new SimpleReport(validationConclusion);

        ValidateDocumentResponse responseReport = transformer.toSoapResponse(simpleReport);
        QualifiedReport qualifiedReport = responseReport.getValidationConclusion();
        Assert.assertEquals(validationConclusion.getValidatedDocument().getFilename(), qualifiedReport.getValidatedDocument().getFilename());
        Assert.assertEquals(validationConclusion.getValidatedDocument().getFileHashInHex(), qualifiedReport.getValidatedDocument().getFileHashInHex());
        Assert.assertEquals(validationConclusion.getValidatedDocument().getHashAlgo(), qualifiedReport.getValidatedDocument().getHashAlgo());
        Assert.assertEquals(validationConclusion.getSignatureForm(), qualifiedReport.getSignatureForm());
        Assert.assertEquals(validationConclusion.getValidationTime(), qualifiedReport.getValidationTime());
        Assert.assertEquals(validationConclusion.getValidationWarnings().get(0).getContent(), qualifiedReport.getValidationWarnings().getValidationWarning().get(0).getContent());
        Assert.assertEquals(validationConclusion.getSignaturesCount(), qualifiedReport.getSignaturesCount());
        Assert.assertEquals(validationConclusion.getValidSignaturesCount(), qualifiedReport.getValidSignaturesCount());
        Assert.assertEquals(validationConclusion.getPolicy().getPolicyDescription(), qualifiedReport.getPolicy().getPolicyDescription());
        Assert.assertEquals(validationConclusion.getPolicy().getPolicyName(), qualifiedReport.getPolicy().getPolicyName());
        Assert.assertEquals(validationConclusion.getPolicy().getPolicyUrl(), qualifiedReport.getPolicy().getPolicyUrl());

        Assert.assertEquals(validationConclusion.getTimeStampTokens().get(0).getIndication().name(), qualifiedReport.getTimeStampTokens().getTimeStampToken().get(0).getIndication().name());
        Assert.assertEquals(validationConclusion.getTimeStampTokens().get(0).getError().get(0).getContent(), qualifiedReport.getTimeStampTokens().getTimeStampToken().get(0).getErrors().getError().get(0).getContent());
        Assert.assertEquals(validationConclusion.getTimeStampTokens().get(0).getSignedBy(), qualifiedReport.getTimeStampTokens().getTimeStampToken().get(0).getSignedBy());
        Assert.assertEquals(validationConclusion.getTimeStampTokens().get(0).getSignedTime(), qualifiedReport.getTimeStampTokens().getTimeStampToken().get(0).getSignedTime());

        Assert.assertEquals(validationConclusion.getSignatures().get(0).getClaimedSigningTime(), qualifiedReport.getSignatures().getSignature().get(0).getClaimedSigningTime());
        Assert.assertEquals(validationConclusion.getSignatures().get(0).getId(), qualifiedReport.getSignatures().getSignature().get(0).getId());
        Assert.assertEquals(validationConclusion.getSignatures().get(0).getIndication(), qualifiedReport.getSignatures().getSignature().get(0).getIndication().value());
        Assert.assertEquals(validationConclusion.getSignatures().get(0).getSignatureFormat(), qualifiedReport.getSignatures().getSignature().get(0).getSignatureFormat());
        Assert.assertEquals(validationConclusion.getSignatures().get(0).getSignatureLevel(), qualifiedReport.getSignatures().getSignature().get(0).getSignatureLevel());
        Assert.assertEquals(validationConclusion.getSignatures().get(0).getSignedBy(), qualifiedReport.getSignatures().getSignature().get(0).getSignedBy());
        Assert.assertEquals(validationConclusion.getSignatures().get(0).getSubIndication(), qualifiedReport.getSignatures().getSignature().get(0).getSubIndication());
        Assert.assertEquals(validationConclusion.getSignatures().get(0).getInfo().getBestSignatureTime(), qualifiedReport.getSignatures().getSignature().get(0).getInfo().getBestSignatureTime());
        Assert.assertEquals(validationConclusion.getSignatures().get(0).getErrors().get(0).getContent(), qualifiedReport.getSignatures().getSignature().get(0).getErrors().getError().get(0).getContent());
        Assert.assertEquals(validationConclusion.getSignatures().get(0).getWarnings().get(0).getContent(), qualifiedReport.getSignatures().getSignature().get(0).getWarnings().getWarning().get(0).getContent());
        Assert.assertEquals(validationConclusion.getSignatures().get(0).getSignatureScopes().get(0).getContent(), qualifiedReport.getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getContent());
        Assert.assertEquals(validationConclusion.getSignatures().get(0).getSignatureScopes().get(0).getName(), qualifiedReport.getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getName());
        Assert.assertEquals(validationConclusion.getSignatures().get(0).getSignatureScopes().get(0).getScope(), qualifiedReport.getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getScope());
    }

    @Test
    public void qualifiedDetailedReportIsCorrectlyTransformedToSoapResponseReport() {
        ValidationConclusion validationConclusion = createMockedValidationConclusion();
        ee.openeid.siva.validation.document.report.DetailedReport detailedReport = new ee.openeid.siva.validation.document.report.DetailedReport(validationConclusion, null);
        detailedReport.setValidationProcess(createMockedDetailedReport());
        ValidateDocumentResponse responseReport = transformer.toSoapResponse(detailedReport);
        Assert.assertEquals("EE", responseReport.getValidationProcess().getQMatrixBlock().getTLAnalysis().get(0).getCountryCode());
    }

    @Test
    public void qualifiedDetailedReportIsNull() {
        ValidationConclusion validationConclusion = createMockedValidationConclusion();
        ee.openeid.siva.validation.document.report.DetailedReport detailedReport = new ee.openeid.siva.validation.document.report.DetailedReport(validationConclusion, null);
        detailedReport.setValidationProcess(null);
        ValidateDocumentResponse responseReport = transformer.toSoapResponse(detailedReport);
        QualifiedReport qualifiedReport = responseReport.getValidationConclusion();
        Assert.assertEquals(validationConclusion.getSignatures().get(0).getIndication(), qualifiedReport.getSignatures().getSignature().get(0).getIndication().value());

    }

    private eu.europa.esig.dss.jaxb.detailedreport.DetailedReport createMockedDetailedReport() {
        eu.europa.esig.dss.jaxb.detailedreport.DetailedReport euDetailedReport = new eu.europa.esig.dss.jaxb.detailedreport.DetailedReport();
        XmlQMatrixBlock xmlQMatrixBlock = new XmlQMatrixBlock();
        XmlTLAnalysis xmlTLAnalysis = new XmlTLAnalysis();
        xmlTLAnalysis.setCountryCode("EE");
        xmlQMatrixBlock.getTLAnalysis().add(xmlTLAnalysis);
        euDetailedReport.setQMatrixBlock(xmlQMatrixBlock);
        return euDetailedReport;
    }

    private ValidationConclusion createMockedValidationConclusion() {
        ValidationConclusion report = new ValidationConclusion();
        report.setValidationTime("2016-09-21T15:00:00Z");
        report.setValidationWarnings(createMockedValidationWarnings());
        report.setValidatedDocument(createMockedValidatedDocument());
        report.setSignatureForm("PAdES");
        report.setPolicy(createMockedSignaturePolicy());
        report.setSignaturesCount(1);
        report.setValidSignaturesCount(1);
        report.setSignatures(createMockedSignatures());
        report.setTimeStampTokens(createMockedTimeStampTokens());
        return report;
    }

    private List<TimeStampTokenValidationData> createMockedTimeStampTokens() {
        List<TimeStampTokenValidationData> timeStampTokens = new ArrayList<>();
        TimeStampTokenValidationData timeStampTokenValidationData = new TimeStampTokenValidationData();
        timeStampTokenValidationData.setSignedTime("2017-09-18T15:00:00Z");
        timeStampTokenValidationData.setSignedBy("Signer");
        timeStampTokenValidationData.setIndication(TimeStampTokenValidationData.Indication.TOTAL_FAILED);
        timeStampTokenValidationData.setError(createMockedSignatureErrors());
        timeStampTokens.add(timeStampTokenValidationData);
        return timeStampTokens;
    }

    private List<ee.openeid.siva.validation.document.report.ValidationWarning> createMockedValidationWarnings() {
        List<ee.openeid.siva.validation.document.report.ValidationWarning> validationWarnings = new ArrayList<>();
        ee.openeid.siva.validation.document.report.ValidationWarning validationWarning = new ee.openeid.siva.validation.document.report.ValidationWarning();
        validationWarning.setContent("some validation warning");
        validationWarnings.add(validationWarning);
        return validationWarnings;
    }

    private ValidatedDocument createMockedValidatedDocument() {
        ValidatedDocument validatedDocument = new ValidatedDocument();
        validatedDocument.setFilename("document.pdf");
        return validatedDocument;
    }

    private ee.openeid.siva.validation.document.report.Policy createMockedSignaturePolicy() {
        ee.openeid.siva.validation.document.report.Policy policy = new ee.openeid.siva.validation.document.report.Policy();
        policy.setPolicyDescription("desc");
        policy.setPolicyName("pol");
        policy.setPolicyUrl("http://pol.eu");
        return policy;
    }

    private List<ee.openeid.siva.validation.document.report.SignatureValidationData> createMockedSignatures() {
        List<ee.openeid.siva.validation.document.report.SignatureValidationData> signatures = new ArrayList<>();
        ee.openeid.siva.validation.document.report.SignatureValidationData signature = new ee.openeid.siva.validation.document.report.SignatureValidationData();
        signature.setCountryCode("EE");
        signature.setSubIndication("");
        signature.setId("S0");
        signature.setIndication(ee.openeid.siva.validation.document.report.SignatureValidationData.Indication.TOTAL_FAILED);
        signature.setClaimedSigningTime("2016-09-21T14:00:00Z");
        signature.setSignatureFormat("PAdES_LT");
        signature.setSignatureLevel("QES");
        signature.setSignedBy("nobody");
        signature.setErrors(createMockedSignatureErrors());
        signature.setInfo(createMockedSignatureInfo());
        signature.setSignatureScopes(createMockedSignatureScopes());
        signature.setWarnings(createMockedSignatureWarnings());
        signatures.add(signature);
        return signatures;
    }

    private List<ee.openeid.siva.validation.document.report.Error> createMockedSignatureErrors() {
        List<ee.openeid.siva.validation.document.report.Error> errors = new ArrayList<>();
        ee.openeid.siva.validation.document.report.Error error = new ee.openeid.siva.validation.document.report.Error();
        error.setContent("some error");
        errors.add(error);
        return errors;
    }

    private ee.openeid.siva.validation.document.report.Info createMockedSignatureInfo() {
        ee.openeid.siva.validation.document.report.Info info = new ee.openeid.siva.validation.document.report.Info();
        info.setBestSignatureTime("2016-09-21T14:00:00Z");
        return info;
    }

    private List<ee.openeid.siva.validation.document.report.SignatureScope> createMockedSignatureScopes() {
        List<ee.openeid.siva.validation.document.report.SignatureScope> signatureScopes = new ArrayList<>();
        ee.openeid.siva.validation.document.report.SignatureScope signatureScope = new ee.openeid.siva.validation.document.report.SignatureScope();
        signatureScope.setContent("some content");
        signatureScope.setName("some name");
        signatureScope.setScope("some scope");
        signatureScopes.add(signatureScope);
        return signatureScopes;
    }

    private List<ee.openeid.siva.validation.document.report.Warning> createMockedSignatureWarnings() {
        List<ee.openeid.siva.validation.document.report.Warning> warnings = new ArrayList<>();
        ee.openeid.siva.validation.document.report.Warning warning = new ee.openeid.siva.validation.document.report.Warning();
        warning.setContent("some warning");
        warnings.add(warning);
        return warnings;
    }

}
