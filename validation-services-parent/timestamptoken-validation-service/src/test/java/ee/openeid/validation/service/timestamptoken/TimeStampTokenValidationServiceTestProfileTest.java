/*
 * Copyright 2019 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timestamptoken;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.CertificateType;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.Scope;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.document.report.Warning;
import ee.openeid.siva.validation.exception.DocumentRequirementsException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.siva.validation.util.CertUtil;
import ee.openeid.validation.service.timestamptoken.configuration.TimeStampTokenSignaturePolicyProperties;
import eu.europa.esig.dss.asic.cades.validation.ASiCContainerWithCAdESValidator;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import lombok.Setter;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class TimeStampTokenValidationServiceTestProfileTest extends BaseTimeStampTokenValidationServiceTest {

    @Test
    void validTimeStampToken() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("testtimestamp.asics")).getSimpleReport();
        assertEquals(1, simpleReport.getValidationConclusion().getTimeStampTokens().size());
        TimeStampTokenValidationData validationData = simpleReport.getValidationConclusion().getTimeStampTokens().get(0);
        assertEquals(TimeStampTokenValidationData.Indication.TOTAL_PASSED, validationData.getIndication());
        assertEquals("DEMO of SK TSA 2014", validationData.getSignedBy());
        assertNull(validationData.getError());
    }

    @Test
    void certificatePresent() throws Exception{
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("timestamptoken-ddoc.asics")).getSimpleReport();
        assertThat(simpleReport.getValidationConclusion().getTimeStampTokens().size(), equalTo(1));
        TimeStampTokenValidationData timeStampTokenValidationData = simpleReport.getValidationConclusion().getTimeStampTokens().get(0);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        assertThat(timeStampTokenValidationData.getCertificates().size(), equalTo(1));

        ee.openeid.siva.validation.document.report.Certificate certificate = timeStampTokenValidationData.getCertificates().get(0);
        assertThat(certificate.getType(), equalTo(CertificateType.CONTENT_TIMESTAMP));
        assertThat(certificate.getCommonName(), equalTo("SK TIMESTAMPING AUTHORITY"));
        Certificate timeStampCertificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(certificate.getContent().getBytes())));
        assertThat(CertUtil.getCommonName((X509Certificate) timeStampCertificate), equalTo("SK TIMESTAMPING AUTHORITY"));
    }

    @Test
    void validateDocument_ContainerWithMultipleTimestamps_BothTimestampsDataPresentInReport() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("2xTST-valid-bdoc-data-file.asics")).getSimpleReport();

        List<TimeStampTokenValidationData> tokens = simpleReport.getValidationConclusion().getTimeStampTokens();
        assertThat(tokens.size(), equalTo(2));

        TimeStampTokenValidationData tokenOne = tokens.get(0);
        assertThat(tokenOne.getSignedBy(), equalTo("DEMO SK TIMESTAMPING AUTHORITY 2023E"));
        assertThat(tokenOne.getSignedTime(), equalTo("2024-03-27T12:42:57Z"));
        assertThat(tokenOne.getSubIndication(), nullValue());
        assertThat(tokenOne.getTimestampLevel(), equalTo("QTSA"));
        assertThat(tokenOne.getCertificates(), hasSize(3));
        assertThat(tokenOne.getWarning(), empty());
        assertThat(tokenOne.getError(), anyOf(nullValue(), empty()));
        assertThat(tokenOne.getTimestampScopes(), hasSize(5));

        TimeStampTokenValidationData tokenTwo = tokens.get(1);
        assertThat(tokenTwo.getSignedBy(), equalTo("DEMO SK TIMESTAMPING AUTHORITY 2023R"));
        assertThat(tokenTwo.getSignedTime(), equalTo("2024-08-26T13:31:34Z"));
        assertThat(tokenTwo.getSubIndication(), nullValue());
        assertThat(tokenTwo.getTimestampLevel(), equalTo("QTSA"));
        assertThat(tokenTwo.getCertificates().size(), equalTo(2));
        assertThat(tokenTwo.getWarning(), anyOf(nullValue(), empty()));
        assertThat(tokenTwo.getError(), anyOf(nullValue(), empty()));
        assertThat(tokenTwo.getTimestampScopes(), hasSize(7));
    }

    @Test
    void validateDocument_NotValidDocument_ReportFieldsArePresent() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("1xTST-valid-bdoc-data-file-hash-failure-in-tst.asics")).getSimpleReport();

        List<TimeStampTokenValidationData> tokens = simpleReport.getValidationConclusion().getTimeStampTokens();
        assertThat(tokens.size(), equalTo(1));

        TimeStampTokenValidationData token = tokens.get(0);
        assertThat(token.getIndication().toString(), equalTo("TOTAL-FAILED"));
        assertThat(token.getSignedBy(), equalTo("DEMO SK TIMESTAMPING AUTHORITY 2023E"));
        assertThat(token.getSignedTime(), equalTo("2024-03-27T12:42:57Z"));
        assertThat(token.getSubIndication(), equalTo("HASH_FAILURE"));
        assertThat(token.getTimestampLevel(), equalTo("QTSA"));
        assertThat(token.getWarning(), empty());
        assertThat(token.getError().stream().map(Error::getContent).collect(Collectors.toList()), containsInAnyOrder(
            "The time-stamp message imprint is not intact!"
        ));
        assertThat(token.getCertificates().size(), equalTo(2));
        assertThat(token.getTimestampScopes(), hasSize(0));
    }

    @Test
    void validateDocument_NestedContainer_NestedContainerFilesAreEnlistedAsScopes() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("1xTST-valid-bdoc-data-file.asics")).getSimpleReport();

        List<TimeStampTokenValidationData> tokens = simpleReport.getValidationConclusion().getTimeStampTokens();
        assertThat(tokens.size(), equalTo(1));

        TimeStampTokenValidationData token = tokens.get(0);
        assertThat(token.getTimestampScopes(), hasSize(5));

        assertScope(token.getTimestampScopes().get(0), "valid-bdoc-tm.bdoc","FULL","ASiCS archive");
        assertScope(token.getTimestampScopes().get(1), "mimetype","ARCHIVED","ASiCS archive content");
        assertScope(token.getTimestampScopes().get(2), "META-INF/manifest.xml","ARCHIVED","ASiCS archive content");
        assertScope(token.getTimestampScopes().get(3), "test.txt","ARCHIVED","ASiCS archive content");
        assertScope(token.getTimestampScopes().get(4), "META-INF/signatures0.xml","ARCHIVED","ASiCS archive content");
        assertThat(token.getTimestampScopes().stream().map(Scope::getHashAlgo).filter(Objects::nonNull).collect(Collectors.toList()), empty());
        assertThat(token.getTimestampScopes().stream().map(Scope::getHash).filter(Objects::nonNull).collect(Collectors.toList()), empty());
    }

    @Test
    void validateDocument_ContainerWith3TSTs_ScopesOfNextTSTsContainFilesOfPreviousTSTs() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("3xTST-text-data-file.asics")).getSimpleReport();

        List<TimeStampTokenValidationData> tokens = simpleReport.getValidationConclusion().getTimeStampTokens();
        assertThat(tokens.size(), equalTo(3));

        TimeStampTokenValidationData tokenOne = tokens.get(0);
        assertThat(tokenOne.getTimestampScopes(), hasSize(1));

        assertScope(tokenOne.getTimestampScopes().get(0), "test.txt","FULL","Full document");
        assertThat(tokenOne.getTimestampScopes().stream().map(Scope::getHashAlgo).filter(Objects::nonNull).collect(Collectors.toList()), empty());
        assertThat(tokenOne.getTimestampScopes().stream().map(Scope::getHash).filter(Objects::nonNull).collect(Collectors.toList()), empty());

        TimeStampTokenValidationData tokenTwo = tokens.get(1);
        assertThat(tokenTwo.getTimestampScopes(), hasSize(3));

        assertScope(tokenTwo.getTimestampScopes().get(0), "META-INF/ASiCArchiveManifest001.xml","FULL","Manifest document");
        assertScope(tokenTwo.getTimestampScopes().get(1), "META-INF/timestamp.tst","FULL","Full document");
        assertScope(tokenTwo.getTimestampScopes().get(2), "test.txt","FULL","Full document");
        assertThat(tokenTwo.getTimestampScopes().stream().map(Scope::getHashAlgo).filter(Objects::nonNull).collect(Collectors.toList()), empty());
        assertThat(tokenTwo.getTimestampScopes().stream().map(Scope::getHash).filter(Objects::nonNull).collect(Collectors.toList()), empty());

        TimeStampTokenValidationData tokenThree = tokens.get(2);
        assertThat(tokenThree.getTimestampScopes(), hasSize(5));

        assertScope(tokenThree.getTimestampScopes().get(0), "META-INF/ASiCArchiveManifest.xml","FULL","Manifest document");
        assertScope(tokenThree.getTimestampScopes().get(1), "META-INF/timestamp.tst","FULL","Full document");
        assertScope(tokenThree.getTimestampScopes().get(2), "META-INF/timestamp002.tst","FULL","Full document");
        assertScope(tokenThree.getTimestampScopes().get(3), "META-INF/ASiCArchiveManifest001.xml","FULL","Full document");
        assertScope(tokenThree.getTimestampScopes().get(4), "test.txt","FULL","Full document");
        assertThat(tokenThree.getTimestampScopes().stream().map(Scope::getHashAlgo).filter(Objects::nonNull).collect(Collectors.toList()), empty());
        assertThat(tokenThree.getTimestampScopes().stream().map(Scope::getHash).filter(Objects::nonNull).collect(Collectors.toList()), empty());
    }

    @Test
    void validateDocument_NotValidDocument_ReportWarningsArePresent() {
        setUpCustomValidationPolicy("tst_constraint_qes_for_test.xml"); // custom policy is needed here to produce Warnings

        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("1xTST-valid-bdoc-data-file-hash-failure-in-tst.asics")).getSimpleReport();

        assertThat(
            simpleReport.getValidationConclusion().getTimeStampTokens().get(0).getWarning().stream().map(Warning::getContent).collect(Collectors.toList()),
            containsInAnyOrder(
                "The time-stamp token does not cover container datafile!",
                "The time-stamp message imprint is not intact!",
                "The computed message-imprint does not match the value extracted from the time-stamp!"
            ));
    }

    @Test
    void validateDocument_TwoValidTSDataFileCoveredByTS_ValidationNoWarnings() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("2xTST-valid-bdoc-data-file.asics")
        ).getSimpleReport();

        TimeStampTokenValidationData tokenOne = simpleReport.getValidationConclusion().getTimeStampTokens().get(0);
        assertThat(tokenOne.getIndication(), equalTo(TimeStampTokenValidationData.Indication.TOTAL_PASSED));
        assertThat(tokenOne.getWarning(), empty());

        TimeStampTokenValidationData tokenTwo = simpleReport.getValidationConclusion().getTimeStampTokens().get(1);
        assertThat(tokenTwo.getIndication(), equalTo(TimeStampTokenValidationData.Indication.TOTAL_PASSED));
        assertThat(tokenTwo.getWarning(), empty());
    }

    @Test
    void validateDocument_OneValidTSDataFileNotCoveredByTS_ValidationPassedWithWarningThatDatafileNotCoveredByTS() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("2xTST-valid-bdoc-data-file-1st-tst-invalid-2nd-tst-no-coverage.asics")
        ).getSimpleReport();

        TimeStampTokenValidationData tokenOne = simpleReport.getValidationConclusion().getTimeStampTokens().get(0);
        assertThat(tokenOne.getIndication(), equalTo(TimeStampTokenValidationData.Indication.TOTAL_FAILED));
        assertThat(tokenOne.getWarning(), empty());

        TimeStampTokenValidationData tokenTwo = simpleReport.getValidationConclusion().getTimeStampTokens().get(1);
        assertThat(tokenTwo.getIndication(), equalTo(TimeStampTokenValidationData.Indication.TOTAL_PASSED));
        assertThat(tokenTwo.getWarning().size(), equalTo(1));
        assertThat(
            tokenTwo.getWarning().stream().map(Warning::getContent).collect(Collectors.toList()),
            containsInAnyOrder("The time-stamp token does not cover container datafile!")
        );
    }

    @Test
    void validateDocument_TwoValidTSDataFileNotCoveredByTS_ValidationPassedWithWarningThatDatafileNotCoveredByTS() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("2xTstFirstValidSecondNotCoveringNestedContainer.asics")
        ).getSimpleReport();

        TimeStampTokenValidationData tokenOne = simpleReport.getValidationConclusion().getTimeStampTokens().get(0);
        assertThat(tokenOne.getIndication(), equalTo(TimeStampTokenValidationData.Indication.TOTAL_PASSED));
        assertThat(tokenOne.getWarning(), empty());

        TimeStampTokenValidationData tokenTwo = simpleReport.getValidationConclusion().getTimeStampTokens().get(1);
        assertThat(tokenTwo.getIndication(), equalTo(TimeStampTokenValidationData.Indication.TOTAL_PASSED));
        assertThat(tokenTwo.getWarning().size(), equalTo(1));
        assertThat(
                tokenTwo.getWarning().stream().map(Warning::getContent).collect(Collectors.toList()),
                containsInAnyOrder("The time-stamp token does not cover container datafile!")
        );
    }

    @Test
    void multipleDataFile() {
        assertThrows(
            DocumentRequirementsException.class, () -> {
                validationService.validateDocument(buildValidationDocument("timestamptoken-two-data-files.asics"));
            }
        );
    }

    @Test
    void notValidTstFile() {
        assertThrows(
            MalformedDocumentException.class, () -> {
                validationService.validateDocument(buildValidationDocument("timestamptoken-invalid.asics"));
            }
        );
    }

    @Test
    void signatureNotIntact() {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("1xTST-valid-bdoc-data-file-invalid-signature-in-tst.asics")).getSimpleReport();

        assertThat(simpleReport.getValidationConclusion().getTimeStampTokens().get(0).getError().get(0).getContent(), equalTo("The signature is not intact!"));
    }

    @Test
    void onlySimpleReportPresentInDocumentValidationResultReports() {
        Reports reports = validationService.validateDocument(buildValidationDocument("timestamptoken-ddoc.asics"));

        assertThat(reports.getSimpleReport().getValidationConclusion(), notNullValue());
        assertThat(reports.getDetailedReport().getValidationConclusion(), notNullValue());
        assertThat(reports.getDiagnosticReport().getValidationConclusion(), notNullValue());

        assertThat(reports.getDetailedReport().getValidationProcess(), nullValue());
        assertThat(reports.getDiagnosticReport().getDiagnosticData(), nullValue());
    }

    @Test
    void validateDocument_ValidationTimeProvided_ValidationTimeSetForValidator() {
        ValidationDocument validationDocument = buildValidationDocument("timestamptoken-ddoc.asics");
        Date validationTime = new Date();
        validationDocument.setValidationTime(validationTime);
        ASiCContainerWithCAdESValidator validatorMock = Mockito.mock(ASiCContainerWithCAdESValidator.class);

        createServiceFake(validatorMock).validateDocument(validationDocument);

        verify(validatorMock).setValidationTime(validationTime);
    }

    @Test
    void validateDocument_ValidationTimeNotProvided_ValidationTimeNotSetForValidator() {
        ValidationDocument validationDocument = buildValidationDocument("timestamptoken-ddoc.asics");
        ASiCContainerWithCAdESValidator validatorMock = Mockito.mock(ASiCContainerWithCAdESValidator.class);

        createServiceFake(validatorMock).validateDocument(validationDocument);

        verify(validatorMock, never()).setValidationTime(any(Date.class));
    }

    private void assertScope(Scope scope, String expectedName, String expectedScope, String expectedContent) {
        assertThat(scope.getName(), equalTo(expectedName));
        assertThat(scope.getScope(), equalTo(expectedScope));
        assertThat(scope.getContent(), equalTo(expectedContent));
    }

    private void setUpCustomValidationPolicy(String xmlPath) {
        ConstraintDefinedPolicy constraintDefinedPolicy = new ConstraintDefinedPolicy(PredefinedValidationPolicySource.QES_POLICY);
        constraintDefinedPolicy.setConstraintPath(xmlPath);
        constraintDefinedPolicy.setName("custom_policy_for_testing");
        constraintDefinedPolicy.setDescription("a custom policy for unit tests");

        TimeStampTokenSignaturePolicyProperties policyProperties = new TimeStampTokenSignaturePolicyProperties();
        policyProperties.setDefaultPolicy(constraintDefinedPolicy.getName());
        policyProperties.setPolicies(List.of(constraintDefinedPolicy));
        policyProperties.initPolicySettings();

        ConstraintLoadingSignaturePolicyService signaturePolicyService = new ConstraintLoadingSignaturePolicyService(policyProperties);
        signaturePolicyService.setSignaturePolicies(Map.of(constraintDefinedPolicy.getName(), constraintDefinedPolicy));

        validationService.setSignaturePolicyService(signaturePolicyService);
    }

    private TimeStampTokenValidationServiceFake createServiceFake(ASiCContainerWithCAdESValidator validatorMock) {
        TimeStampTokenValidationServiceFake validationServiceFake = new TimeStampTokenValidationServiceFake();
        when(validatorMock.validateDocument(any(InputStream.class))).thenReturn(
            new eu.europa.esig.dss.validation.reports.Reports(null, null, new XmlSimpleReport(), null)
        );
        validationServiceFake.setValidator(validatorMock);

        validationServiceFake.setSignaturePolicyService(new ConstraintLoadingSignaturePolicyService(policyProperties));
        validationServiceFake.setTrustedListsCertificateSource(trustedListsCertificateSource);
        validationServiceFake.setReportConfigurationProperties(new ReportConfigurationProperties(true));

        return validationServiceFake;
    }

    private static class TimeStampTokenValidationServiceFake extends TimeStampTokenValidationService {

        @Setter
        private ASiCContainerWithCAdESValidator validator;

        @Override
        protected ASiCContainerWithCAdESValidator createValidatorFromDocument(final ValidationDocument validationDocument) {
            return validator;
        }
    }
}
