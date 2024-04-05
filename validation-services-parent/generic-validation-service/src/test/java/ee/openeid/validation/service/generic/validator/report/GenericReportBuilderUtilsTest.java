/*
 * Copyright 2024 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SignatureValidationData.Indication;
import ee.openeid.siva.validation.document.report.builder.SignatureLevelAdjuster;
import ee.openeid.siva.validation.document.report.builder.SignatureValidationDataProcessor;
import ee.openeid.siva.validation.helper.TestLog;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import eu.europa.esig.dss.detailedreport.DetailedReport;
import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationSignatureQualification;
import eu.europa.esig.dss.enumerations.CertificateQualification;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import eu.europa.esig.dss.enumerations.ValidationTime;
import eu.europa.esig.dss.validation.reports.Reports;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GenericReportBuilderUtilsTest {

    private static final String SIGNATURE_ID = "mock-signatureId-123";
    private static final String LOG_MESSAGE_TEMPLATE = "The reported qualification level of signature '" +
            SIGNATURE_ID + "' has been re-evaluated from %s to %s";

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "unrelated", "POLv3"})
    void createSignatureLevelAdjuster_WhenNotPolV4Policy_ReturnsNoOpProcessor(String policyName) {
        ReportBuilderData reportData = mock(ReportBuilderData.class);
        ConstraintDefinedPolicy policy = mock(ConstraintDefinedPolicy.class);
        doReturn(policy).when(reportData).getPolicy();
        doReturn(policyName).when(policy).getName();

        SignatureValidationDataProcessor<String> result = GenericReportBuilderUtils
                .createSignatureLevelAdjuster(reportData);

        assertThat(result, sameInstance(GenericReportBuilderUtils.NO_OP_SIGNATURE_LEVEL_ADJUSTER));
        verifyNoMoreInteractions(reportData, policy);
    }

    @Test
    void createSignatureLevelAdjuster_WhenPolV4Policy_ReturnsSignatureLevelAdjuster() {
        ReportBuilderData reportData = mock(ReportBuilderData.class);
        ConstraintDefinedPolicy policy = mock(ConstraintDefinedPolicy.class);
        doReturn(policy).when(reportData).getPolicy();
        doReturn("POLv4").when(policy).getName();
        Reports dssReports = mock(Reports.class);
        doReturn(dssReports).when(reportData).getDssReports();
        DetailedReport detailedReport = mock(DetailedReport.class);
        doReturn(detailedReport).when(dssReports).getDetailedReport();

        SignatureValidationDataProcessor<String> result = GenericReportBuilderUtils
                .createSignatureLevelAdjuster(reportData);

        assertThat(result, instanceOf(SignatureLevelAdjuster.class));
        verifyNoMoreInteractions(reportData, policy, dssReports);
        verifyNoInteractions(detailedReport);
    }

    @Test
    void signatureLevelAdjusterProcess_WhenAdjustmentIsApplicable_SignatureQualificationsAreAdjusted() {
        DssDetailedReportWrapper detailedReportWrapper = mock(DssDetailedReportWrapper.class);
        SignatureLevelAdjuster<String> signatureLevelAdjuster = GenericReportBuilderUtils
                .createSignatureLevelAdjuster(detailedReportWrapper);
        SignatureValidationData signatureValidationData = mock(SignatureValidationData.class);
        doReturn(Indication.TOTAL_PASSED.toString()).when(signatureValidationData).getIndication();
        doReturn(SignatureQualification.UNKNOWN_QC.name()).when(signatureValidationData).getSignatureLevel();
        doReturn(CertificateQualification.QCERT_FOR_ESIG_QSCD).when(detailedReportWrapper)
                .getSigningCertificateQualification(SIGNATURE_ID, ValidationTime.CERTIFICATE_ISSUANCE_TIME);
        doReturn(CertificateQualification.QCERT_FOR_ESEAL).when(detailedReportWrapper)
                .getSigningCertificateQualification(SIGNATURE_ID, ValidationTime.BEST_SIGNATURE_TIME);
        XmlValidationSignatureQualification signatureQualification = mock(XmlValidationSignatureQualification.class);
        doReturn(signatureQualification).when(detailedReportWrapper).getValidationSignatureQualification(SIGNATURE_ID);

        signatureLevelAdjuster.process(signatureValidationData, SIGNATURE_ID);

        verify(signatureValidationData).setSignatureLevel(SignatureQualification.ADESEAL_QC.name());
        verify(signatureValidationData).getWarnings();
        verify(signatureQualification).setSignatureQualification(SignatureQualification.ADESEAL_QC);
        verifyNoMoreInteractions(detailedReportWrapper, signatureValidationData, signatureQualification);
    }

    @ParameterizedTest
    @MethodSource("signatureQualificationCombinations")
    void signatureLevelAdjustmentListenerAccept_WhenDetailedReportQualificationBlockIsMissing_OnlyEventIsLogged(
            SignatureQualification oldQualification, SignatureQualification newQualification
    ) {
        TestLog testLog = new TestLog(GenericReportBuilderUtils.class);
        DssDetailedReportWrapper detailedReportWrapper = mock(DssDetailedReportWrapper.class);
        doReturn(null).when(detailedReportWrapper).getValidationSignatureQualification(SIGNATURE_ID);
        BiConsumer<String, SignatureLevelAdjuster.Event> adjustmentListener = GenericReportBuilderUtils
                .createSignatureLevelAdjustmentListener(detailedReportWrapper);
        SignatureLevelAdjuster.Event adjustmentEvent = mock(SignatureLevelAdjuster.Event.class);
        doReturn(oldQualification).when(adjustmentEvent).getOldSignatureQualification();
        doReturn(newQualification).when(adjustmentEvent).getNewSignatureQualification();

        adjustmentListener.accept(SIGNATURE_ID, adjustmentEvent);

        verifyNoMoreInteractions(detailedReportWrapper, adjustmentEvent);
        testLog.verifyLogInOrder(containsString(String.format(
                LOG_MESSAGE_TEMPLATE, oldQualification, newQualification
        )));
    }

    @ParameterizedTest
    @MethodSource("signatureQualificationCombinations")
    void signatureLevelAdjustmentListenerAccept_WhenDetailedReportQualificationBlockIsPresent_DetailedReportQualificationIsUpdated(
            SignatureQualification oldQualification, SignatureQualification newQualification
    ) {
        TestLog testLog = new TestLog(GenericReportBuilderUtils.class);
        DssDetailedReportWrapper detailedReportWrapper = mock(DssDetailedReportWrapper.class);
        XmlValidationSignatureQualification validationQualification = mock(XmlValidationSignatureQualification.class);
        doReturn(validationQualification).when(detailedReportWrapper).getValidationSignatureQualification(SIGNATURE_ID);
        BiConsumer<String, SignatureLevelAdjuster.Event> adjustmentListener = GenericReportBuilderUtils
                .createSignatureLevelAdjustmentListener(detailedReportWrapper);
        SignatureLevelAdjuster.Event adjustmentEvent = mock(SignatureLevelAdjuster.Event.class);
        doReturn(oldQualification).when(adjustmentEvent).getOldSignatureQualification();
        doReturn(newQualification).when(adjustmentEvent).getNewSignatureQualification();

        adjustmentListener.accept(SIGNATURE_ID, adjustmentEvent);

        verify(validationQualification).setSignatureQualification(newQualification);
        verifyNoMoreInteractions(detailedReportWrapper, validationQualification, adjustmentEvent);
        testLog.verifyLogInOrder(containsString(String.format(
                LOG_MESSAGE_TEMPLATE, oldQualification, newQualification
        )));
    }

    static Stream<Arguments> signatureQualificationCombinations() {
        return Stream.of(
                Arguments.of(SignatureQualification.UNKNOWN, SignatureQualification.ADESEAL),
                Arguments.of(SignatureQualification.UNKNOWN_QC, SignatureQualification.ADESEAL_QC),
                Arguments.of(SignatureQualification.UNKNOWN_QC_QSCD, SignatureQualification.QESIG)
        );
    }

}
