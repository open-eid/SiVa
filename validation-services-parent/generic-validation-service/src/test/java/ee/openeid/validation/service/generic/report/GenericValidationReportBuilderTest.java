/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.validation.service.generic.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.validation.service.generic.validator.report.GenericValidationReportBuilder;
import ee.openeid.validation.service.generic.validator.report.ReportBuilderData;
import eu.europa.esig.dss.diagnostic.jaxb.*;
import eu.europa.esig.dss.enumerations.*;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.pades.validation.PDFDocumentValidator;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class GenericValidationReportBuilderTest {

    @Mock
    TrustedListsCertificateSource trustedListsCertificateSource;

    private static final String TM_POLICY_OID = "1.3.6.1.4.1.10015.1000.3.2.1";

    @Test
    public void totalPassedIndicationReportBuild() {

        ReportBuilderData reportBuilderData = getReportBuilderData(getDssReports(""));
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();
        ValidationConclusion validationConclusion = reports.getSimpleReport().getValidationConclusion();
        Assert.assertEquals(Integer.valueOf(1), validationConclusion.getValidSignaturesCount());
        Assert.assertEquals("TOTAL-PASSED", validationConclusion.getSignatures().get(0).getIndication());
        Assert.assertEquals("XAdES_BASELINE_LT", validationConclusion.getSignatures().get(0).getSignatureFormat());
    }

    @Test
    public void totalPassedIndicationTimeMarkReportBuild() {

        ReportBuilderData reportBuilderData = getReportBuilderData(getDssReports(TM_POLICY_OID));
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();
        Assert.assertEquals(Integer.valueOf(1), reports.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assert.assertEquals("TOTAL-PASSED", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getIndication());
        Assert.assertEquals("XAdES_BASELINE_LT_TM", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getSignatureFormat());
    }

    @Test
    public void totalFailedIndicationReportBuild() {
        eu.europa.esig.dss.validation.reports.Reports dssReports = getDssReports("");
        dssReports.getSimpleReportJaxb().getSignature().get(0).setIndication(Indication.TOTAL_FAILED);
        dssReports.getSimpleReportJaxb().getSignature().get(0).getErrors().add("Something is wrong");

        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();
        Assert.assertEquals(Integer.valueOf(0), reports.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assert.assertEquals("TOTAL-FAILED", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getIndication());
        Assert.assertEquals("Something is wrong", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getErrors().get(0).getContent());
    }

    @Test
    public void indeterminateIndicationReportBuild() {
        eu.europa.esig.dss.validation.reports.Reports dssReports = getDssReports("");
        dssReports.getSimpleReportJaxb().getSignature().get(0).setIndication(Indication.INDETERMINATE);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();
        Assert.assertEquals(Integer.valueOf(0), reports.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assert.assertEquals("INDETERMINATE", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getIndication());
    }

    @Test
    public void signatureMissingSigningCertificateResultsWithEmptySubjectDN() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        diagnosticData.getSignatures().get(0).setSigningCertificate(null);
        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        SubjectDistinguishedName subjectDN = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getSubjectDistinguishedName();
        Assert.assertEquals("", subjectDN.getCommonName());
        Assert.assertEquals("", subjectDN.getSerialNumber());
    }

    @Test
    public void populatesNonEmptyClaimedRolesAsSignerRole() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        XmlSignerRole claimedRole = new XmlSignerRole();
        claimedRole.setRole("role1");
        claimedRole.setCategory(EndorsementType.CLAIMED);

        XmlSignerRole role = new XmlSignerRole();
        role.setRole("role2");

        XmlSignerRole emptyRole = new XmlSignerRole();
        emptyRole.setRole("");
        emptyRole.setCategory(EndorsementType.CLAIMED);

        diagnosticData.getSignatures().get(0).getSignerRole().clear();
        diagnosticData.getSignatures().get(0).getSignerRole().add(claimedRole);
        diagnosticData.getSignatures().get(0).getSignerRole().add(role);
        diagnosticData.getSignatures().get(0).getSignerRole().add(emptyRole);

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        List<SignerRole> signerRole = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getSignerRole();
        Assert.assertEquals(1, signerRole.size());
        Assert.assertEquals("role1", signerRole.get(0).getClaimedRole());
    }

    @Test
    public void noSignerRolePresentResultsWithEmptyList() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        diagnosticData.getSignatures().get(0).getSignerRole().clear();

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        List<SignerRole> signerRole = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getSignerRole();
        Assert.assertTrue(signerRole.isEmpty());
    }

    @Test
    public void populatesSignatureProductionPlace() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        XmlSignatureProductionPlace xmlSignatureProductionPlace = new XmlSignatureProductionPlace();
        xmlSignatureProductionPlace.setCity("Tallinn");
        xmlSignatureProductionPlace.setCountryName("Eesti");
        xmlSignatureProductionPlace.setPostalCode("12345");
        xmlSignatureProductionPlace.setStateOrProvince("Harjumaa");
        diagnosticData.getSignatures().get(0).setSignatureProductionPlace(xmlSignatureProductionPlace);

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        SignatureProductionPlace signatureProductionPlace = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getSignatureProductionPlace();
        Assert.assertEquals("Tallinn", signatureProductionPlace.getCity());
        Assert.assertEquals("Eesti", signatureProductionPlace.getCountryName());
        Assert.assertEquals("12345", signatureProductionPlace.getPostalCode());
        Assert.assertEquals("Harjumaa", signatureProductionPlace.getStateOrProvince());
    }

    @Test
    public void signatureProductionPlaceNotPresentResultsWithNull() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        diagnosticData.getSignatures().get(0).setSignatureProductionPlace(null);

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        SignatureProductionPlace signatureProductionPlace = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getSignatureProductionPlace();
        Assert.assertNull(signatureProductionPlace);
    }

    @Test
    public void signatureProductionPlaceWithNullOrEmptyFieldsResultsWithNull() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        XmlSignatureProductionPlace xmlSignatureProductionPlace = new XmlSignatureProductionPlace();
        xmlSignatureProductionPlace.setCountryName("");
        diagnosticData.getSignatures().get(0).setSignatureProductionPlace(xmlSignatureProductionPlace);

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        SignatureProductionPlace signatureProductionPlace = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getSignatureProductionPlace();
        Assert.assertNull(signatureProductionPlace);
    }

    @Test
    public void populatesSignatureMethod() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        Assert.assertEquals("http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1",
                reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getSignatureMethod());
    }

    @Test
    public void timeAssertionMessageImprintIsEmptyOnTimestampParseError() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        XmlDigestMatcher xmlDigestMatcher = new XmlDigestMatcher();
        xmlDigestMatcher.setDigestValue(new byte[0]);
        xmlDigestMatcher.setDataFound(true);
        xmlDigestMatcher.setDataIntact(true);

        XmlTimestamp xmlTimestamp = new XmlTimestamp();
        xmlTimestamp.setDigestMatcher(xmlDigestMatcher);
        xmlTimestamp.setType(TimestampType.SIGNATURE_TIMESTAMP);

        XmlFoundTimestamp xmlFoundTimestamp = new XmlFoundTimestamp();
        xmlFoundTimestamp.setTimestamp(xmlTimestamp);

        diagnosticData.getSignatures().get(0).setFoundTimestamps(Collections.singletonList(xmlFoundTimestamp));

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        Assert.assertEquals("", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getTimeAssertionMessageImprint());
    }

    @Test
    public void timeAssertionMessageImprintIsEmptyOnOcspNonceParseError() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        XmlRevocation xmlRevocation = new XmlRevocation();
        xmlRevocation.setBase64Encoded(new byte[0]);

        XmlRelatedRevocation xmlRelatedRevocation = new XmlRelatedRevocation();
        xmlRelatedRevocation.setRevocation(xmlRevocation);

        XmlFoundRevocations xmlFoundRevocations = new XmlFoundRevocations();
        xmlFoundRevocations.getRelatedRevocations().add(xmlRelatedRevocation);

        diagnosticData.getSignatures().get(0).setFoundRevocations(xmlFoundRevocations);

        XmlPolicy xmlPolicy = new XmlPolicy();
        xmlPolicy.setId(TM_POLICY_OID);
        diagnosticData.getSignatures().get(0).setPolicy(xmlPolicy);

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        Assert.assertEquals("", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getTimeAssertionMessageImprint());
    }

    private ReportBuilderData getReportBuilderData(eu.europa.esig.dss.validation.reports.Reports reports){
        return ReportBuilderData.builder()
                .dssReports(reports)
                .validationLevel(ValidationLevel.ARCHIVAL_DATA)
                .validationDocument(getValidationDocument())
                .policy(getValidationPolicy())
                .isReportSignatureEnabled(false)
                .trustedListsCertificateSource(trustedListsCertificateSource)
                .signatures(getSignatures())
                .build();
    }
    private ValidationDocument getValidationDocument() {
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setName("filename.bdoc");
        validationDocument.setBytes("dGVzdA==".getBytes());
        return validationDocument;
    }

    private List<AdvancedSignature> getSignatures() {
        return new PDFDocumentValidator(new FileDocument("src/test/resources/test-files/no-signatures.pdf")).getSignatures();
    }

    private ConstraintDefinedPolicy getValidationPolicy() {
        ValidationPolicy validationPolicy = new ValidationPolicy();
        validationPolicy.setName("POLv3");
        validationPolicy.setDescription("description");
        validationPolicy.setUrl("localhost");
        return new ConstraintDefinedPolicy(validationPolicy);
    }

    private eu.europa.esig.dss.validation.reports.Reports getDssReports(String policyId) {
        return new eu.europa.esig.dss.validation.reports.Reports(getDiagnosticDataJaxb(policyId), null, getSimpleReport(), null);
    }

    private XmlDiagnosticData getDiagnosticDataJaxb(String policyId) {
        XmlDiagnosticData diagnosticData = new XmlDiagnosticData();
        XmlContainerInfo xmlContainerInfo = new XmlContainerInfo();
        xmlContainerInfo.setContainerType("ASIC-E");
        diagnosticData.setContainerInfo(xmlContainerInfo);
        XmlSignature xmlSignature = new XmlSignature();
        XmlSignatureScope xmlSignatureScope = new XmlSignatureScope();
        xmlSignature.setSignatureScopes(Collections.singletonList(xmlSignatureScope));
        xmlSignature.setId("SIG-id");

        XmlFoundTimestamp xmlFoundTimestamp = new XmlFoundTimestamp();
        xmlFoundTimestamp.setTimestamp(new XmlTimestamp());
        xmlSignature.setFoundTimestamps(Collections.singletonList(xmlFoundTimestamp));

        XmlFoundRevocations xmlFoundRevocations = new XmlFoundRevocations();
        xmlSignature.setFoundRevocations(xmlFoundRevocations);

        XmlPolicy xmlPolicy = new XmlPolicy();
        xmlPolicy.setId(policyId);
        xmlSignature.setPolicy(xmlPolicy);

        XmlSigningCertificate signingCertificate = new XmlSigningCertificate();
        XmlCertificate certificate = new XmlCertificate();
        certificate.setId("SIG-CERT-id");
        signingCertificate.setCertificate(certificate);

        xmlSignature.setSigningCertificate(signingCertificate);

        XmlBasicSignature xmlBasicSignature = new XmlBasicSignature();
        xmlBasicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA256);
        xmlBasicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.RSA);
        xmlBasicSignature.setMaskGenerationFunctionUsedToSignThisToken(MaskGenerationFunction.MGF1);
        xmlSignature.setBasicSignature(xmlBasicSignature);

        diagnosticData.setSignatures(Collections.singletonList(xmlSignature));

        XmlCertificate xmlCertificate = new XmlCertificate();
        xmlCertificate.setId(signingCertificate.getCertificate().getId());
        XmlDistinguishedName distinguishedName = new XmlDistinguishedName();
        distinguishedName.setFormat("RFC2253");
        distinguishedName.setValue("2.5.4.5=#130b3437313031303130303333,2.5.4.42=#0c05504552454e494d49,2.5.4.4=#0c074545534e494d49,CN=PERENIMI\\,EESNIMI\\,47101010033,OU=digital signature,O=ESTEID,C=EE");
        xmlCertificate.getSubjectDistinguishedName().add(distinguishedName);
        XmlDigestAlgoAndValue xmlDigestAlgoAndValue = new XmlDigestAlgoAndValue();
        xmlCertificate.setDigestAlgoAndValue(xmlDigestAlgoAndValue);
        xmlCertificate.setCountryName("EE");
        diagnosticData.setUsedCertificates(Collections.singletonList(xmlCertificate));
        return diagnosticData;
    }

    private XmlSimpleReport getSimpleReport() {
        XmlSimpleReport simpleReport = new XmlSimpleReport();
        XmlSigningCertificate xmlSigningCertificate = new XmlSigningCertificate();
        XmlCertificate certificate = new XmlCertificate();
        certificate.setId("CERT-id");
        xmlSigningCertificate.setCertificate(certificate);

        eu.europa.esig.dss.simplereport.jaxb.XmlSignature xmlSignature = new eu.europa.esig.dss.simplereport.jaxb.XmlSignature();
        xmlSignature.setId("SIG-id");
        xmlSignature.setIndication(Indication.TOTAL_PASSED);
        xmlSignature.setSignatureFormat(SignatureLevel.XAdES_BASELINE_LT);
        simpleReport.getSignature().add(xmlSignature);
        return simpleReport;
    }
}
