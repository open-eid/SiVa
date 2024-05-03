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

package ee.openeid.siva.proxy;


import ee.openeid.siva.proxy.document.ProxyHashcodeDataSet;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.statistics.StatisticsService;
import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.SignatureFile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.DetailedReport;
import ee.openeid.siva.validation.document.report.DiagnosticReport;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.Info;
import ee.openeid.siva.validation.document.report.Policy;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.ValidatedDocument;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.validation.service.generic.HashcodeGenericValidationService;
import ee.openeid.validation.service.timemark.TimemarkHashcodeValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class HashcodeValidationProxyTest {
    private final StatisticsService statisticsService = mock(StatisticsService.class);
    private final ApplicationContext applicationContext = mock(ApplicationContext.class);
    private final Environment environment = mock(Environment.class);
    private final HasBdocTimemarkPolicyService hasBdocTimemarkPolicyService = mock(HasBdocTimemarkPolicyService.class);
    private final HashcodeValidationMapper hashcodeValidationMapper = mock(HashcodeValidationMapper.class);
    private final HashcodeGenericValidationService hashcodeGenericValidationService = mock(HashcodeGenericValidationService.class);
    private final TimemarkHashcodeValidationService timemarkHashcodeValidationService = mock(TimemarkHashcodeValidationService.class);

    private HashcodeValidationProxy hashcodeValidationProxy;

    @BeforeEach
    void setUp() {
        hashcodeValidationProxy = new HashcodeValidationProxy(
          statisticsService,
          applicationContext,
          environment,
          hasBdocTimemarkPolicyService,
          hashcodeValidationMapper,
          hashcodeGenericValidationService,
          timemarkHashcodeValidationService
        );
    }

    @Test
    void constructValidatorName_whenInvoked_shouldThrowException() {
        assertThrows(IllegalStateException.class, () ->
          hashcodeValidationProxy.constructValidatorName(mockHashCodeDataSet())
        );
    }

    @Test
    void validate_givenSignatureDoesNotHaveBdocTimemarkPolicy_shouldUseGenericValidation() {
        final Reports reports = createDummyReports();
        when(hashcodeValidationMapper.mapToValidationDocuments(any())).thenReturn(List.of(createDummyValidationDocument()));
        when(hasBdocTimemarkPolicyService.hasBdocTimemarkPolicy(any())).thenReturn(false);
        when(hashcodeGenericValidationService.validateDocument(any())).thenReturn(reports);
        when(hashcodeValidationMapper.mergeReportsToOne(any())).thenReturn(reports);

        SimpleReport report = hashcodeValidationProxy.validate(mockHashCodeDataSet());

        assertAll(
          () -> verifyNoInteractions(timemarkHashcodeValidationService),
          () -> assertEquals(reports.getSimpleReport(), report)
        );
    }

    @Test
    void validate_givenSignatureHasBdocTimemarkPolicy_shouldUseTimemarkValidation() {
        final Reports reports = createDummyReports();
        when(hashcodeValidationMapper.mapToValidationDocuments(any())).thenReturn(List.of(createDummyValidationDocument()));
        when(hasBdocTimemarkPolicyService.hasBdocTimemarkPolicy(any())).thenReturn(true);
        when(timemarkHashcodeValidationService.validateDocument(any())).thenReturn(reports);
        when(hashcodeValidationMapper.mergeReportsToOne(any())).thenReturn(reports);

        SimpleReport report = hashcodeValidationProxy.validate(mockHashCodeDataSet());

        assertAll(
          () -> verifyNoInteractions(hashcodeGenericValidationService),
          () -> assertEquals(reports.getSimpleReport(), report)
        );
    }

    @Test
    void validate_givenSignaturesWithMixedPolicies_shouldUseDifferentValidations() {
        final Reports reports = createDummyReports();
        when(hashcodeValidationMapper.mapToValidationDocuments(any()))
          .thenReturn(List.of(createDummyValidationDocument(), createDummyValidationDocument()));
        when(hasBdocTimemarkPolicyService.hasBdocTimemarkPolicy(any()))
          .thenReturn(true)
          .thenReturn(false);
        when(timemarkHashcodeValidationService.validateDocument(any())).thenReturn(reports);
        when(hashcodeGenericValidationService.validateDocument(any())).thenReturn(reports);
        when(hashcodeValidationMapper.mergeReportsToOne(any())).thenReturn(reports);

        SimpleReport report = hashcodeValidationProxy.validate(mockHashCodeDataSet());

        assertEquals(reports.getSimpleReport(), report);
    }

    @Test
    void validate_whenHashcodeValidationInvokedWithDifferentReportTypes_shouldReturnSimpleReport() {
        ProxyHashcodeDataSet proxyDocument = mockHashCodeDataSet();
        Reports reports = createDummyReports();
        reports.setDetailedReport(new DetailedReport());
        reports.setDiagnosticReport(new DiagnosticReport());

        for (ReportType reportType : ReportType.values()) {
            proxyDocument.setReportType(reportType);
            when(hashcodeValidationMapper.mapToValidationDocuments(any()))
              .thenReturn(List.of(createDummyValidationDocument()));
            when(hashcodeValidationMapper.mergeReportsToOne(any())).thenReturn(reports);

            SimpleReport report = hashcodeValidationProxy.validate(proxyDocument);

            assertTrue(report instanceof SimpleReport);
            assertFalse(report instanceof DetailedReport);
            assertFalse(report instanceof DiagnosticReport);
        }
    }

    @Test
    void validDataFromSignatureFile() {
        final Reports reports = createDummyReports();
        when(hashcodeValidationMapper.mapToValidationDocuments(any()))
          .thenReturn(List.of(DummyValidationDocumentBuilder.aValidationDocument()
            .withDocument("test-files/signatures.xml")
            .withName("signatures.xml")
            .build()));
        when(hashcodeValidationMapper.mergeReportsToOne(any())).thenReturn(reports);

        SimpleReport report = hashcodeValidationProxy.validate(mockHashCodeDataSet());

        assertEquals(reports.getSimpleReport(), report);
    }

    private ProxyHashcodeDataSet mockHashCodeDataSet() {
        ProxyHashcodeDataSet proxyHashcodeDataSet = new ProxyHashcodeDataSet();
        SignatureFile signatureFile = new SignatureFile();
        signatureFile.setSignature("hash".getBytes());
        signatureFile.setDatafiles(createDatafiles(createDatafile("test", "test-hash-1", "SHA256")));
        proxyHashcodeDataSet.setSignatureFiles(Collections.singletonList(signatureFile));
        return proxyHashcodeDataSet;
    }

    private static ValidationDocument createDummyValidationDocument() {
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setBytes(new byte[1]);
        validationDocument.setDatafiles(List.of(new Datafile()));
        return validationDocument;
    }

    private List<Datafile> createDatafiles(Datafile... datafiles) {
        return Arrays.asList(datafiles);
    }

    private Datafile createDatafile(String filename, String hash, String hashAlgo) {
        Datafile datafile = new Datafile();
        datafile.setHash(hash);
        datafile.setFilename(filename);
        datafile.setHashAlgo(hashAlgo);
        return datafile;
    }

    private static Reports createDummyReports() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setValidSignaturesCount(0);
        validationConclusion.setSignaturesCount(1);
        validationConclusion.setValidationTime("ValidationTime");
        validationConclusion.setValidatedDocument(createDummyValidatedDocument());
        validationConclusion.setPolicy(createDummyPolicy());
        validationConclusion.setSignatures(createDummySignatures());
        SimpleReport simpleReport = new SimpleReport(validationConclusion);
        return new Reports(simpleReport, null, null);
    }

    private static ValidatedDocument createDummyValidatedDocument() {
        ValidatedDocument validatedDocument = new ValidatedDocument();
        validatedDocument.setFilename("DocumentName");
        return validatedDocument;
    }

    private static List<SignatureValidationData> createDummySignatures() {
        SignatureValidationData signature = new SignatureValidationData();
        signature.setSignatureLevel("SignatureLevel");
        signature.setClaimedSigningTime("ClaimedSigningTime");
        signature.setInfo(createDummySignatureInfo());
        signature.setSignatureFormat("SingatureFormat");
        signature.setId("id1");
        signature.setSignedBy("Some Name 123456789");
        signature.setIndication(SignatureValidationData.Indication.TOTAL_FAILED);
        signature.setWarnings(Collections.emptyList());
        signature.setErrors(createDummyErrors());
        return Collections.singletonList(signature);
    }

    private static List<Error> createDummyErrors() {
        Error error = new Error();
        error.setContent("ErrorContent");
        return Collections.singletonList(error);
    }

    private static Info createDummySignatureInfo() {
        Info info = new Info();
        info.setBestSignatureTime("BestSignatureTime");
        return info;
    }

    private static Policy createDummyPolicy() {
        Policy policy = new Policy();
        policy.setPolicyDescription("PolicyDescription");
        policy.setPolicyName("PolicyName");
        policy.setPolicyUrl("http://policyUrl.com");
        return policy;
    }
}
