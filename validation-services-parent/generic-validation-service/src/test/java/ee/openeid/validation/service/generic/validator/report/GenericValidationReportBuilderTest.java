/*
 * Copyright 2017 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.validator.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureProductionPlace;
import ee.openeid.siva.validation.document.report.SignerRole;
import ee.openeid.siva.validation.document.report.SubjectDistinguishedName;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.validation.service.generic.helper.TestXmlDetailUtils;
import eu.europa.esig.dss.diagnostic.CertificateRevocationWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlBasicSignature;
import eu.europa.esig.dss.diagnostic.jaxb.XmlCertificate;
import eu.europa.esig.dss.diagnostic.jaxb.XmlCertificateRevocation;
import eu.europa.esig.dss.diagnostic.jaxb.XmlContainerInfo;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestAlgoAndValue;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDistinguishedName;
import eu.europa.esig.dss.diagnostic.jaxb.XmlFoundRevocations;
import eu.europa.esig.dss.diagnostic.jaxb.XmlFoundTimestamp;
import eu.europa.esig.dss.diagnostic.jaxb.XmlPolicy;
import eu.europa.esig.dss.diagnostic.jaxb.XmlRelatedRevocation;
import eu.europa.esig.dss.diagnostic.jaxb.XmlRevocation;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignature;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignatureProductionPlace;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignatureScope;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignerRole;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSigningCertificate;
import eu.europa.esig.dss.diagnostic.jaxb.XmlTimestamp;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.CertificateStatus;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.EndorsementType;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.MaskGenerationFunction;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.pades.validation.PDFDocumentValidator;
import eu.europa.esig.dss.simplereport.jaxb.XmlDetails;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import eu.europa.esig.dss.simplereport.jaxb.XmlTimestamps;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class GenericValidationReportBuilderTest {

    @Mock
    TrustedListsCertificateSource trustedListsCertificateSource;

    private static final String TM_POLICY_OID = "1.3.6.1.4.1.10015.1000.3.2.1";

    @Test
    void totalPassedIndicationReportBuild() {
        ReportBuilderData reportBuilderData = getReportBuilderData(getDssReports("", SignatureLevel.XAdES_BASELINE_LT));
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();
        ValidationConclusion validationConclusion = reports.getSimpleReport().getValidationConclusion();
        Assertions.assertEquals(Integer.valueOf(1), validationConclusion.getValidSignaturesCount());
        Assertions.assertEquals("TOTAL-PASSED", validationConclusion.getSignatures().get(0).getIndication());
        Assertions.assertEquals("XAdES_BASELINE_LT", validationConclusion.getSignatures().get(0).getSignatureFormat());
    }

    @Test
    void build_BdocPolicyAndSignatureLT_ResultHasIndicationTotalPassedAndTMSignatureFormat() {
        ReportBuilderData reportBuilderData = getReportBuilderData(getDssReports(TM_POLICY_OID, SignatureLevel.XAdES_BASELINE_LT));

        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        Assertions.assertEquals(Integer.valueOf(1), reports.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assertions.assertEquals("TOTAL-PASSED", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getIndication());
        Assertions.assertEquals(SignatureLevel.XAdES_BASELINE_LT_TM.name(), reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getSignatureFormat());
    }

    @ParameterizedTest
    @EnumSource(value = SignatureLevel.class, mode = EnumSource.Mode.EXCLUDE, names = {"XAdES_BASELINE_LT"})
    void build_BdocPolicyAndSignatureNotLT_ResultHasIndicationTotalFailedAndSameSignatureFormat(SignatureLevel signatureLevel) {
        ReportBuilderData reportBuilderData = getReportBuilderData(getDssReports(TM_POLICY_OID, signatureLevel));

        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        Assertions.assertEquals(0, reports.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assertions.assertEquals("TOTAL-FAILED", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getIndication());
        Assertions.assertEquals(signatureLevel.name(), reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getSignatureFormat());
        Assertions.assertEquals("Invalid signature format for BDOC policy", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getErrors().get(0).getContent());
    }

    @Test
    void totalFailedIndicationWithAdESErrorReportBuild() {
        eu.europa.esig.dss.validation.reports.Reports dssReports = getDssReports("", SignatureLevel.XAdES_BASELINE_LT);
        dssReports.getSimpleReportJaxb().getSignatureOrTimestampOrEvidenceRecord().get(0).setIndication(Indication.TOTAL_FAILED);
        dssReports.getSimpleReportJaxb().getSignatureOrTimestampOrEvidenceRecord().get(0).setAdESValidationDetails(new XmlDetails());
        dssReports.getSimpleReportJaxb().getSignatureOrTimestampOrEvidenceRecord().get(0).getAdESValidationDetails().getError()
                .add(TestXmlDetailUtils.createMessage(null, "Something is wrong"));

        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();
        Assertions.assertEquals(Integer.valueOf(0), reports.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assertions.assertEquals("TOTAL-FAILED", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getIndication());
        Assertions.assertEquals("Something is wrong", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getErrors().get(0).getContent());
    }

    @Test
    void totalFailedIndicationWithQualificationErrorReportBuild() {
        eu.europa.esig.dss.validation.reports.Reports dssReports = getDssReports("", SignatureLevel.XAdES_BASELINE_LT);
        dssReports.getSimpleReportJaxb().getSignatureOrTimestampOrEvidenceRecord().get(0).setIndication(Indication.TOTAL_FAILED);
        dssReports.getSimpleReportJaxb().getSignatureOrTimestampOrEvidenceRecord().get(0).setQualificationDetails(new XmlDetails());
        dssReports.getSimpleReportJaxb().getSignatureOrTimestampOrEvidenceRecord().get(0).getQualificationDetails().getError()
                .add(TestXmlDetailUtils.createMessage(null, "Something is wrong"));

        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();
        Assertions.assertEquals(Integer.valueOf(0), reports.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assertions.assertEquals("TOTAL-FAILED", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getIndication());
        Assertions.assertEquals("Something is wrong", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getErrors().get(0).getContent());
    }

    @Test
    void totalFailedIndicationWithTimestampErrorsReportBuild() {
        eu.europa.esig.dss.validation.reports.Reports dssReports = getDssReports("", SignatureLevel.XAdES_BASELINE_LT);
        dssReports.getSimpleReportJaxb().getSignatureOrTimestampOrEvidenceRecord().get(0).setIndication(Indication.TOTAL_FAILED);
        eu.europa.esig.dss.simplereport.jaxb.XmlSignature signature = (eu.europa.esig.dss.simplereport.jaxb.XmlSignature) dssReports
                .getSimpleReportJaxb().getSignatureOrTimestampOrEvidenceRecord().get(0);
        signature.setTimestamps(new XmlTimestamps());
        signature.getTimestamps().getTimestamp().add(new eu.europa.esig.dss.simplereport.jaxb.XmlTimestamp());
        signature.getTimestamps().getTimestamp().get(0).setAdESValidationDetails(new XmlDetails());
        signature.getTimestamps().getTimestamp().get(0).getAdESValidationDetails().getError()
                .add(TestXmlDetailUtils.createMessage(null, "AdES is wrong"));
        signature.getTimestamps().getTimestamp().get(0).setQualificationDetails(new XmlDetails());
        signature.getTimestamps().getTimestamp().get(0).getQualificationDetails().getError()
                .add(TestXmlDetailUtils.createMessage(null, "Qualification is wrong"));

        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();
        Assertions.assertEquals(Integer.valueOf(0), reports.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assertions.assertEquals("TOTAL-FAILED", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getIndication());
        Assertions.assertEquals("AdES is wrong", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getErrors().get(0).getContent());
        Assertions.assertEquals("Qualification is wrong", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getErrors().get(1).getContent());
    }

    @Test
    void indeterminateIndicationReportBuild() {
        eu.europa.esig.dss.validation.reports.Reports dssReports = getDssReports("", SignatureLevel.XAdES_BASELINE_LT);
        dssReports.getSimpleReportJaxb().getSignatureOrTimestampOrEvidenceRecord().get(0).setIndication(Indication.INDETERMINATE);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();
        Assertions.assertEquals(Integer.valueOf(0), reports.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assertions.assertEquals("INDETERMINATE", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getIndication());
    }

    @Test
    void signatureMissingSigningCertificateResultsWithEmptySubjectDN() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        diagnosticData.getSignatures().get(0).setSigningCertificate(null);
        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        SubjectDistinguishedName subjectDN = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getSubjectDistinguishedName();
        Assertions.assertEquals("", subjectDN.getCommonName());
        Assertions.assertEquals("", subjectDN.getSerialNumber());
    }

    @Test
    void populatesNonEmptyClaimedRolesAsSignerRole() {
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
        Assertions.assertEquals(1, signerRole.size());
        Assertions.assertEquals("role1", signerRole.get(0).getClaimedRole());
    }

    @Test
    void noSignerRolePresentResultsWithEmptyList() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        diagnosticData.getSignatures().get(0).getSignerRole().clear();

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        List<SignerRole> signerRole = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getSignerRole();
        Assertions.assertTrue(signerRole.isEmpty());
    }

    @Test
    void populatesSignatureProductionPlace() {
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
        Assertions.assertEquals("Tallinn", signatureProductionPlace.getCity());
        Assertions.assertEquals("Eesti", signatureProductionPlace.getCountryName());
        Assertions.assertEquals("12345", signatureProductionPlace.getPostalCode());
        Assertions.assertEquals("Harjumaa", signatureProductionPlace.getStateOrProvince());
    }

    @Test
    void signatureProductionPlaceNotPresentResultsWithNull() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        diagnosticData.getSignatures().get(0).setSignatureProductionPlace(null);

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        SignatureProductionPlace signatureProductionPlace = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getSignatureProductionPlace();
        Assertions.assertNull(signatureProductionPlace);
    }

    @Test
    void signatureProductionPlaceWithNullOrEmptyFieldsResultsWithNull() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        XmlSignatureProductionPlace xmlSignatureProductionPlace = new XmlSignatureProductionPlace();
        xmlSignatureProductionPlace.setCountryName("");
        diagnosticData.getSignatures().get(0).setSignatureProductionPlace(xmlSignatureProductionPlace);

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        SignatureProductionPlace signatureProductionPlace = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getSignatureProductionPlace();
        Assertions.assertNull(signatureProductionPlace);
    }

    @Test
    void populatesSignatureMethod() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        Assertions.assertEquals("http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1",
                reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getSignatureMethod());
    }

    @Test
    void timeAssertionMessageImprintIsEmptyOnTimestampParseError() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        XmlTimestamp xmlTimestamp = new XmlTimestamp();
        xmlTimestamp.setType(TimestampType.SIGNATURE_TIMESTAMP);

        XmlFoundTimestamp xmlFoundTimestamp = new XmlFoundTimestamp();
        xmlFoundTimestamp.setTimestamp(xmlTimestamp);

        diagnosticData.getSignatures().get(0).setFoundTimestamps(Collections.singletonList(xmlFoundTimestamp));

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        Assertions.assertEquals("", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getTimeAssertionMessageImprint());
    }

    @Test
    void timeAssertionMessageImprintIsEmptyOnOcspNonceParseError() {
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

        Assertions.assertEquals("", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getTimeAssertionMessageImprint());
    }

    @Test
    void populatesTimestampCreationTime() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        Assertions.assertEquals("2018-02-11T00:00:00Z",
                reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getTimestampCreationTime());
    }

    @Test
    void timestampCreationTimeIsEmpty() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        diagnosticData.getSignatures().get(0).setFoundTimestamps(new ArrayList<>());
        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);

        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();
        Assertions.assertNull(reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getTimestampCreationTime());
    }

    @Test
    void populatesOcspResponseCreationTime(){
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);
        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();

        Assertions.assertEquals("2018-02-16T00:00:00Z",
                reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getOcspResponseCreationTime());
    }

    @Test
    void ocspResponseIsEmpty() {
        XmlDiagnosticData diagnosticData = getDiagnosticDataJaxb("");

        diagnosticData.getSignatures().get(0).getSigningCertificate().getCertificate().setRevocations(null);
        eu.europa.esig.dss.validation.reports.Reports dssReports = new eu.europa.esig.dss.validation.reports.Reports(diagnosticData, null, getSimpleReport(), null);

        ReportBuilderData reportBuilderData = getReportBuilderData(dssReports);
        Reports reports = new GenericValidationReportBuilder(reportBuilderData).build();
        Assertions.assertNull(reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getOcspResponseCreationTime());
    }

    @ParameterizedTest
    @MethodSource("earliestBestFittingTimestampTestArguments")
    void testInternalGetEarliestBestFittingTimestamp(
            List<TimestampWrapper> timestamps, TimestampWrapper expectedTimestamp
    ) {
        TimestampWrapper result = GenericValidationReportBuilder.getEarliestBestFittingTimestamp(timestamps);
        Assertions.assertSame(expectedTimestamp, result);
    }

    private static Stream<Arguments> earliestBestFittingTimestampTestArguments() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        TimestampWrapper validTimestamp = createTimestampWrapper(true, now);
        TimestampWrapper invalidTimestamp = createTimestampWrapper(false, now);
        return Stream.of(
                Arguments.of(List.of(validTimestamp), validTimestamp),
                Arguments.of(List.of(
                        createTimestampWrapper(true, now.plusSeconds(1L)),
                        createTimestampWrapper(false, null),
                        createTimestampWrapper(true, now.plusSeconds(60L * 60L * 24L)),
                        validTimestamp,
                        createTimestampWrapper(false, null)
                ), validTimestamp),
                Arguments.of(List.of(invalidTimestamp), invalidTimestamp),
                Arguments.of(List.of(
                        createTimestampWrapper(false, now.plusSeconds(60L * 60L)),
                        invalidTimestamp,
                        createTimestampWrapper(false, now.plusSeconds(60L * 60L * 24L))
                ), invalidTimestamp)
        );
    }

    @ParameterizedTest
    @MethodSource("latestBestFittingTimestampTestArguments")
    void testInternalGetLatestBestFittingTimestamp(
            List<TimestampWrapper> timestamps, TimestampWrapper expectedTimestamp
    ) {
        TimestampWrapper result = GenericValidationReportBuilder.getLatestBestFittingTimestamp(timestamps);
        Assertions.assertSame(expectedTimestamp, result);
    }

    private static Stream<Arguments> latestBestFittingTimestampTestArguments() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        TimestampWrapper validTimestamp = createTimestampWrapper(true, now);
        TimestampWrapper invalidTimestamp = createTimestampWrapper(false, now);
        return Stream.of(
                Arguments.of(List.of(validTimestamp), validTimestamp),
                Arguments.of(List.of(
                        createTimestampWrapper(true, now.minusSeconds(1L)),
                        createTimestampWrapper(false, null),
                        createTimestampWrapper(true, now.minusSeconds(60L * 60L * 24L)),
                        validTimestamp,
                        createTimestampWrapper(false, null)
                ), validTimestamp),
                Arguments.of(List.of(invalidTimestamp), invalidTimestamp),
                Arguments.of(List.of(
                        createTimestampWrapper(false, now.minusSeconds(60L * 60L)),
                        invalidTimestamp,
                        createTimestampWrapper(false, now.minusSeconds(60L * 60L * 24L))
                ), invalidTimestamp)
        );
    }

    @ParameterizedTest
    @MethodSource("bestFittingRevocationTestArguments")
    void testInternalGetBestFittingRevocation(
            List<CertificateRevocationWrapper> revocations, CertificateRevocationWrapper expectedResult
    ) {
        CertificateRevocationWrapper result = GenericValidationReportBuilder.getBestFittingRevocation(revocations);
        Assertions.assertSame(expectedResult, result);
    }

    private static Stream<Arguments> bestFittingRevocationTestArguments() {
        final CertificateRevocationWrapper validAndGoodRevocation = createCertificateRevocationWrapper(true, CertificateStatus.GOOD);
        return Stream.of(
                Arguments.of(List.of(validAndGoodRevocation), validAndGoodRevocation),
                Arguments.of(List.of(
                        createCertificateRevocationWrapper(false, null),
                        createCertificateRevocationWrapper(true, CertificateStatus.REVOKED),
                        createCertificateRevocationWrapper(true, CertificateStatus.UNKNOWN),
                        validAndGoodRevocation,
                        createCertificateRevocationWrapper(true, CertificateStatus.GOOD)
                ), validAndGoodRevocation)
        );
    }

    private ReportBuilderData getReportBuilderData(eu.europa.esig.dss.validation.reports.Reports reports) {
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

    private eu.europa.esig.dss.validation.reports.Reports getDssReports(String policyId, SignatureLevel signatureLevel) {
        return new eu.europa.esig.dss.validation.reports.Reports(getDiagnosticDataJaxb(policyId), null, getSimpleReport(signatureLevel), null);
    }

    private XmlDiagnosticData getDiagnosticDataJaxb(String policyId) {
        XmlDiagnosticData diagnosticData = new XmlDiagnosticData();
        XmlContainerInfo xmlContainerInfo = new XmlContainerInfo();
        xmlContainerInfo.setContainerType(ASiCContainerType.ASiC_E);
        diagnosticData.setContainerInfo(xmlContainerInfo);
        XmlSignature xmlSignature = new XmlSignature();
        XmlSignatureScope xmlSignatureScope = new XmlSignatureScope();
        xmlSignature.setSignatureScopes(Collections.singletonList(xmlSignatureScope));
        xmlSignature.setId("SIG-id");

        LocalDate timestampLocalDate = LocalDate.of(2018, Month.FEBRUARY, 11);
        Date timestampDate = Date.from(timestampLocalDate.atStartOfDay(ZoneId.of("Zulu")).toInstant());
        XmlFoundTimestamp xmlFoundTimestamp = new XmlFoundTimestamp();
        XmlTimestamp xmlTimestamp = new XmlTimestamp();
        xmlTimestamp.setType(TimestampType.SIGNATURE_TIMESTAMP);
        xmlTimestamp.setProductionTime(timestampDate);
        xmlFoundTimestamp.setTimestamp(xmlTimestamp);
        xmlSignature.setFoundTimestamps(Collections.singletonList(xmlFoundTimestamp));


        LocalDate revocationLocalDate = LocalDate.of(2018, Month.FEBRUARY, 16);
        Date revocationDate = Date.from(revocationLocalDate.atStartOfDay(ZoneId.of("Zulu")).toInstant());
        XmlFoundRevocations xmlFoundRevocations = new XmlFoundRevocations();
        XmlRelatedRevocation xmlRelatedRevocation = new XmlRelatedRevocation();
        XmlRevocation xmlRevocation = new XmlRevocation();
        xmlRevocation.setProductionDate(revocationDate);
        xmlRelatedRevocation.setRevocation(xmlRevocation);
        xmlFoundRevocations.getRelatedRevocations().add(xmlRelatedRevocation);
        xmlSignature.setFoundRevocations(xmlFoundRevocations);

        XmlPolicy xmlPolicy = new XmlPolicy();
        xmlPolicy.setId(policyId);
        xmlSignature.setPolicy(xmlPolicy);

        XmlSigningCertificate signingCertificate = new XmlSigningCertificate();
        XmlCertificate certificate = new XmlCertificate();
        certificate.setId("SIG-CERT-id");
        List<XmlCertificateRevocation> XmlCertificateRevocations = new ArrayList<>();
        XmlCertificateRevocation xmlCertificateRevocation = new XmlCertificateRevocation();
        xmlCertificateRevocation.setRevocation(xmlRevocation);
        XmlCertificateRevocations.add(xmlCertificateRevocation);
        certificate.setRevocations(XmlCertificateRevocations);
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
        return getSimpleReport(SignatureLevel.XAdES_BASELINE_LT);
    }

    private XmlSimpleReport getSimpleReport(SignatureLevel signatureLevel) {
        XmlSimpleReport simpleReport = new XmlSimpleReport();
        XmlSigningCertificate xmlSigningCertificate = new XmlSigningCertificate();
        XmlCertificate certificate = new XmlCertificate();
        certificate.setId("CERT-id");
        xmlSigningCertificate.setCertificate(certificate);

        eu.europa.esig.dss.simplereport.jaxb.XmlSignature xmlSignature = new eu.europa.esig.dss.simplereport.jaxb.XmlSignature();
        xmlSignature.setId("SIG-id");
        xmlSignature.setIndication(Indication.TOTAL_PASSED);
        xmlSignature.setSignatureFormat(signatureLevel);
        simpleReport.getSignatureOrTimestampOrEvidenceRecord().add(xmlSignature);
        return simpleReport;
    }

    private static TimestampWrapper createTimestampWrapper(boolean valid, Instant productionTime) {
        TimestampWrapper timestampWrapper = Mockito.mock(TimestampWrapper.class);
        Mockito.doReturn(valid).when(timestampWrapper).isMessageImprintDataFound();
        if (valid) {
            Mockito.doReturn(valid).when(timestampWrapper).isMessageImprintDataIntact();
            Mockito.doReturn(valid).when(timestampWrapper).isSignatureIntact();
            Mockito.doReturn(valid).when(timestampWrapper).isSignatureValid();
            Mockito.doReturn(valid).when(timestampWrapper).isTrustedChain();
        }
        if (productionTime != null) {
            Mockito.doReturn(Date.from(productionTime)).when(timestampWrapper).getProductionTime();
        }
        return timestampWrapper;
    }

    private static CertificateRevocationWrapper createCertificateRevocationWrapper(boolean valid, CertificateStatus certificateStatus) {
        CertificateRevocationWrapper certificateRevocationWrapper = Mockito.mock(CertificateRevocationWrapper.class);
        Mockito.doReturn(valid).when(certificateRevocationWrapper).isSignatureIntact();
        if (valid) {
            Mockito.doReturn(valid).when(certificateRevocationWrapper).isSignatureValid();
            Mockito.doReturn(valid).when(certificateRevocationWrapper).isTrustedChain();
        }
        if (certificateStatus != null) {
            Mockito.doReturn(certificateStatus).when(certificateRevocationWrapper).getStatus();
        }
        return certificateRevocationWrapper;
    }

}
