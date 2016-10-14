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

import ee.openeid.siva.webapp.soap.QualifiedReport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class QualifiedReportSoapResponseTransformerTest {

    QualifiedReportSoapResponseTransformer transformer = new QualifiedReportSoapResponseTransformer();

    @Test
    public void qualifiedReportIsCorrectlyTransformedToSoapResponseReport() {
        ee.openeid.siva.validation.document.report.QualifiedReport qualifiedReport = createMockedQualifedReport();
        QualifiedReport responseReport = transformer.toSoapResponse(qualifiedReport);

        assertEquals(qualifiedReport.getDocumentName(), responseReport.getDocumentName());
        assertEquals(qualifiedReport.getSignatureForm(), responseReport.getSignatureForm());
        assertEquals(qualifiedReport.getValidationTime(), responseReport.getValidationTime());
        assertEquals(qualifiedReport.getSignaturesCount().longValue(), responseReport.getSignaturesCount());
        assertEquals(qualifiedReport.getValidSignaturesCount().longValue(), responseReport.getValidSignaturesCount());

        assertEquals(qualifiedReport.getPolicy().getPolicyDescription(), responseReport.getPolicy().getPolicyDescription());
        assertEquals(qualifiedReport.getPolicy().getPolicyName(), responseReport.getPolicy().getPolicyName());
        assertEquals(qualifiedReport.getPolicy().getPolicyUrl(), responseReport.getPolicy().getPolicyUrl());

        assertEquals(qualifiedReport.getSignatures().get(0).getClaimedSigningTime(), responseReport.getSignatures().getSignature().get(0).getClaimedSigningTime());
        assertEquals(qualifiedReport.getSignatures().get(0).getId(), responseReport.getSignatures().getSignature().get(0).getId());
        assertEquals(qualifiedReport.getSignatures().get(0).getIndication(), responseReport.getSignatures().getSignature().get(0).getIndication().value());
        assertEquals(qualifiedReport.getSignatures().get(0).getSignatureFormat(), responseReport.getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals(qualifiedReport.getSignatures().get(0).getSignatureLevel(), responseReport.getSignatures().getSignature().get(0).getSignatureLevel());
        assertEquals(qualifiedReport.getSignatures().get(0).getSignedBy(), responseReport.getSignatures().getSignature().get(0).getSignedBy());
        assertEquals(qualifiedReport.getSignatures().get(0).getSubIndication(), responseReport.getSignatures().getSignature().get(0).getSubIndication());
        assertEquals(qualifiedReport.getSignatures().get(0).getInfo().getBestSignatureTime(), responseReport.getSignatures().getSignature().get(0).getInfo().getBestSignatureTime());
        assertEquals(qualifiedReport.getSignatures().get(0).getErrors().get(0).getContent(), responseReport.getSignatures().getSignature().get(0).getErrors().getError().get(0).getContent());
        assertEquals(qualifiedReport.getSignatures().get(0).getWarnings().get(0).getDescription(), responseReport.getSignatures().getSignature().get(0).getWarnings().getWarning().get(0).getDescription());
        assertEquals(qualifiedReport.getSignatures().get(0).getSignatureScopes().get(0).getContent(), responseReport.getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getContent());
        assertEquals(qualifiedReport.getSignatures().get(0).getSignatureScopes().get(0).getName(), responseReport.getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getName());
        assertEquals(qualifiedReport.getSignatures().get(0).getSignatureScopes().get(0).getScope(), responseReport.getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getScope());
    }

    private ee.openeid.siva.validation.document.report.QualifiedReport createMockedQualifedReport() {
        ee.openeid.siva.validation.document.report.QualifiedReport report = new ee.openeid.siva.validation.document.report.QualifiedReport();
        report.setValidationTime("2016-09-21T15:00:00Z");
        report.setDocumentName("document.pdf");
        report.setSignatureForm("PAdES");
        report.setPolicy(createMockedSignaturePolicy());
        report.setSignaturesCount(1);
        report.setValidSignaturesCount(1);
        report.setSignatures(createMockedSignatures());
        return report;
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
        warning.setDescription("some warning");
        warnings.add(warning);
        return warnings;
    }

}
