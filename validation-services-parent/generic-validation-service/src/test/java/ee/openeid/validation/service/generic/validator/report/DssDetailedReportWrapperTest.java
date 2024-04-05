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

import eu.europa.esig.dss.detailedreport.DetailedReport;
import eu.europa.esig.dss.detailedreport.jaxb.XmlSignature;
import eu.europa.esig.dss.detailedreport.jaxb.XmlSubXCV;
import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationCertificateQualification;
import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationSignatureQualification;
import eu.europa.esig.dss.enumerations.CertificateQualification;
import eu.europa.esig.dss.enumerations.ValidationTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DssDetailedReportWrapperTest {

    private static final String SIGNATURE_ID = "mock-signatureId-123";
    private static final String CERTIFICATE_ID = "mock-certificateId-456";

    @Mock
    private DetailedReport detailedReport;

    private DssDetailedReportWrapper reportWrapper;

    @BeforeEach
    void setUpWrapper() {
        reportWrapper = new DssDetailedReportWrapper(detailedReport);
    }

    @Test
    void getDssDetailedReport_ReturnsWrappedReport() {
        DetailedReport result = reportWrapper.getDssDetailedReport();

        assertThat(result, sameInstance(detailedReport));
        verifyNoInteractions(detailedReport);
    }

    @Test
    void getSigningCertificateId_WhenDetainedReportGetSigningCertificateReturnsNull_ReturnsNull() {
        doReturn(null).when(detailedReport).getSigningCertificate(SIGNATURE_ID);

        String result = reportWrapper.getSigningCertificateId(SIGNATURE_ID);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport);
    }

    @Test
    void getSigningCertificateId_WhenSigningCertificateGetIdReturnsNull_ReturnsNull() {
        XmlSubXCV signingCertificateBlock = mock(XmlSubXCV.class);
        doReturn(signingCertificateBlock).when(detailedReport).getSigningCertificate(SIGNATURE_ID);
        doReturn(null).when(signingCertificateBlock).getId();

        String result = reportWrapper.getSigningCertificateId(SIGNATURE_ID);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport, signingCertificateBlock);
    }

    @Test
    void getSigningCertificateId_WhenSigningCertificateGetIdReturnsId_ReturnsTheSameId() {
        XmlSubXCV signingCertificateBlock = mock(XmlSubXCV.class);
        doReturn(signingCertificateBlock).when(detailedReport).getSigningCertificate(SIGNATURE_ID);
        doReturn(CERTIFICATE_ID).when(signingCertificateBlock).getId();

        String result = reportWrapper.getSigningCertificateId(SIGNATURE_ID);

        assertThat(result, sameInstance(CERTIFICATE_ID));
        verifyNoMoreInteractions(detailedReport, signingCertificateBlock);
    }

    @Test
    void getSignature_WhenDetailedReportGetSignaturesReturnsNull_ReturnsNull() {
        doReturn(null).when(detailedReport).getSignatures();

        XmlSignature result = reportWrapper.getSignature(SIGNATURE_ID);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport);
    }

    @Test
    void getSignature_WhenDetailedReportGetSignaturesReturnsEmptyList_ReturnsNull() {
        doReturn(List.of()).when(detailedReport).getSignatures();

        XmlSignature result = reportWrapper.getSignature(SIGNATURE_ID);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport);
    }

    @Test
    void getSignature_WhenSignatureListContainsSignatureWithNoId_ReturnsNull() {
        XmlSignature signature = mock(XmlSignature.class);
        doReturn(null).when(signature).getId();
        doReturn(List.of(signature)).when(detailedReport).getSignatures();

        XmlSignature result = reportWrapper.getSignature(SIGNATURE_ID);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport, signature);
    }

    @Test
    void getSignature_WhenSignatureListContainsSignatureWithNonMatchingId_ReturnsNull() {
        XmlSignature signature = mock(XmlSignature.class);
        doReturn("some-other-id").when(signature).getId();
        doReturn(List.of(signature)).when(detailedReport).getSignatures();

        XmlSignature result = reportWrapper.getSignature(SIGNATURE_ID);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport, signature);
    }

    @Test
    void getSignature_WhenSignatureListContainsSignatureWithMatchingId_ReturnsTheMatchingSignature() {
        XmlSignature signature = mock(XmlSignature.class);
        doReturn(SIGNATURE_ID).when(signature).getId();
        doReturn(List.of(signature)).when(detailedReport).getSignatures();

        XmlSignature result = reportWrapper.getSignature(SIGNATURE_ID);

        assertThat(result, sameInstance(signature));
        verifyNoMoreInteractions(detailedReport, signature);
    }

    @Test
    void getValidationSignatureQualification_WhenSignatureHasNoQualification_ReturnsNull() {
        XmlSignature signature = mock(XmlSignature.class);
        doReturn(SIGNATURE_ID).when(signature).getId();
        doReturn(null).when(signature).getValidationSignatureQualification();
        doReturn(List.of(signature)).when(detailedReport).getSignatures();

        XmlValidationSignatureQualification result = reportWrapper.getValidationSignatureQualification(SIGNATURE_ID);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport, signature);
    }

    @Test
    void getValidationSignatureQualification_WhenSignatureHasQualification_ReturnsTheSignatureQualification() {
        XmlSignature signature = createXmlSignature(SIGNATURE_ID);
        XmlValidationSignatureQualification qualification = mock(XmlValidationSignatureQualification.class);
        signature.setValidationSignatureQualification(qualification);
        doReturn(List.of(signature)).when(detailedReport).getSignatures();

        XmlValidationSignatureQualification result = reportWrapper.getValidationSignatureQualification(SIGNATURE_ID);

        assertThat(result, sameInstance(qualification));
        verifyNoMoreInteractions(detailedReport);
        verifyNoInteractions(qualification);
    }

    @ParameterizedTest
    @EnumSource(ValidationTime.class)
    void getSigningCertificateQualification_WhenNoSigningCertificateForSignatureIsNotFound_ReturnsNull(
            ValidationTime validationTime
    ) {
        doReturn(null).when(detailedReport).getSigningCertificate(SIGNATURE_ID);

        CertificateQualification result = reportWrapper.getSigningCertificateQualification(SIGNATURE_ID, validationTime);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport);
    }

    @ParameterizedTest
    @EnumSource(ValidationTime.class)
    void getSigningCertificateQualification_WhenNoSigningCertificateForSignatureHasNoId_ReturnsNull(
            ValidationTime validationTime
    ) {
        XmlSubXCV signingCertificateBlock = mock(XmlSubXCV.class);
        doReturn(signingCertificateBlock).when(detailedReport).getSigningCertificate(SIGNATURE_ID);
        doReturn(null).when(signingCertificateBlock).getId();

        CertificateQualification result = reportWrapper.getSigningCertificateQualification(SIGNATURE_ID, validationTime);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport, signingCertificateBlock);
    }

    @ParameterizedTest
    @EnumSource(ValidationTime.class)
    void getSigningCertificateQualification_WhenSignatureCertificateQualificationsIsNull_ReturnsNull(
            ValidationTime validationTime
    ) {
        XmlSubXCV signingCertificateBlock = createXmlSubXCV(CERTIFICATE_ID);
        doReturn(signingCertificateBlock).when(detailedReport).getSigningCertificate(SIGNATURE_ID);
        XmlSignature signature = createXmlSignature(SIGNATURE_ID);
        doReturn(List.of(signature)).when(detailedReport).getSignatures();
        XmlValidationSignatureQualification signatureQualification = mock(XmlValidationSignatureQualification.class);
        signature.setValidationSignatureQualification(signatureQualification);
        doReturn(null).when(signatureQualification).getValidationCertificateQualification();

        CertificateQualification result = reportWrapper.getSigningCertificateQualification(SIGNATURE_ID, validationTime);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport, signatureQualification);
    }

    @ParameterizedTest
    @EnumSource(ValidationTime.class)
    void getSigningCertificateQualification_WhenSignatureCertificateQualificationsIsEmpty_ReturnsNull(
            ValidationTime validationTime
    ) {
        XmlSubXCV signingCertificateBlock = createXmlSubXCV(CERTIFICATE_ID);
        doReturn(signingCertificateBlock).when(detailedReport).getSigningCertificate(SIGNATURE_ID);
        XmlSignature signature = createXmlSignature(SIGNATURE_ID);
        doReturn(List.of(signature)).when(detailedReport).getSignatures();
        XmlValidationSignatureQualification signatureQualification = mock(XmlValidationSignatureQualification.class);
        signature.setValidationSignatureQualification(signatureQualification);
        doReturn(List.of()).when(signatureQualification).getValidationCertificateQualification();

        CertificateQualification result = reportWrapper.getSigningCertificateQualification(SIGNATURE_ID, validationTime);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport, signatureQualification);
    }

    @ParameterizedTest
    @EnumSource(ValidationTime.class)
    void getSigningCertificateQualification_WhenSignatureCertificateQualificationHasNullId_ReturnsNull(
            ValidationTime validationTime
    ) {
        XmlSubXCV signingCertificateBlock = createXmlSubXCV(CERTIFICATE_ID);
        doReturn(signingCertificateBlock).when(detailedReport).getSigningCertificate(SIGNATURE_ID);
        XmlSignature signature = createXmlSignature(SIGNATURE_ID);
        doReturn(List.of(signature)).when(detailedReport).getSignatures();
        XmlValidationSignatureQualification signatureQualification = new XmlValidationSignatureQualification();
        signature.setValidationSignatureQualification(signatureQualification);
        XmlValidationCertificateQualification certificateQualification = mock(XmlValidationCertificateQualification.class);
        signatureQualification.getValidationCertificateQualification().add(certificateQualification);
        doReturn(null).when(certificateQualification).getId();

        CertificateQualification result = reportWrapper.getSigningCertificateQualification(SIGNATURE_ID, validationTime);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport, certificateQualification);
    }

    @ParameterizedTest
    @EnumSource(ValidationTime.class)
    void getSigningCertificateQualification_WhenSignatureCertificateQualificationHasNullValidationTime_ReturnsNull(
            ValidationTime validationTime
    ) {
        XmlSubXCV signingCertificateBlock = createXmlSubXCV(CERTIFICATE_ID);
        doReturn(signingCertificateBlock).when(detailedReport).getSigningCertificate(SIGNATURE_ID);
        XmlSignature signature = createXmlSignature(SIGNATURE_ID);
        doReturn(List.of(signature)).when(detailedReport).getSignatures();
        XmlValidationSignatureQualification signatureQualification = new XmlValidationSignatureQualification();
        signature.setValidationSignatureQualification(signatureQualification);
        XmlValidationCertificateQualification certificateQualification = mock(XmlValidationCertificateQualification.class);
        signatureQualification.getValidationCertificateQualification().add(certificateQualification);
        doReturn(CERTIFICATE_ID).when(certificateQualification).getId();
        doReturn(null).when(certificateQualification).getValidationTime();

        CertificateQualification result = reportWrapper.getSigningCertificateQualification(SIGNATURE_ID, validationTime);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport, certificateQualification);
    }

    @ParameterizedTest
    @EnumSource(ValidationTime.class)
    void getSigningCertificateQualification_WhenSignatureCertificateQualificationHasNonMatchingValidationTime_ReturnsNull(
            ValidationTime validationTime
    ) {
        XmlSubXCV signingCertificateBlock = createXmlSubXCV(CERTIFICATE_ID);
        doReturn(signingCertificateBlock).when(detailedReport).getSigningCertificate(SIGNATURE_ID);
        XmlSignature signature = createXmlSignature(SIGNATURE_ID);
        doReturn(List.of(signature)).when(detailedReport).getSignatures();
        XmlValidationSignatureQualification signatureQualification = new XmlValidationSignatureQualification();
        signature.setValidationSignatureQualification(signatureQualification);
        XmlValidationCertificateQualification certificateQualification = mock(XmlValidationCertificateQualification.class);
        signatureQualification.getValidationCertificateQualification().add(certificateQualification);
        doReturn(CERTIFICATE_ID).when(certificateQualification).getId();
        doReturn(getFirstNonMatchingEnumValue(validationTime)).when(certificateQualification).getValidationTime();

        CertificateQualification result = reportWrapper.getSigningCertificateQualification(SIGNATURE_ID, validationTime);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport, certificateQualification);
    }

    @ParameterizedTest
    @EnumSource(ValidationTime.class)
    void getSigningCertificateQualification_WhenSignatureCertificateQualificationHasNullQualification_ReturnsNull(
            ValidationTime validationTime
    ) {
        XmlSubXCV signingCertificateBlock = createXmlSubXCV(CERTIFICATE_ID);
        doReturn(signingCertificateBlock).when(detailedReport).getSigningCertificate(SIGNATURE_ID);
        XmlSignature signature = createXmlSignature(SIGNATURE_ID);
        doReturn(List.of(signature)).when(detailedReport).getSignatures();
        XmlValidationSignatureQualification signatureQualification = new XmlValidationSignatureQualification();
        signature.setValidationSignatureQualification(signatureQualification);
        XmlValidationCertificateQualification certificateQualification = mock(XmlValidationCertificateQualification.class);
        signatureQualification.getValidationCertificateQualification().add(certificateQualification);
        doReturn(CERTIFICATE_ID).when(certificateQualification).getId();
        doReturn(validationTime).when(certificateQualification).getValidationTime();
        doReturn(null).when(certificateQualification).getCertificateQualification();

        CertificateQualification result = reportWrapper.getSigningCertificateQualification(SIGNATURE_ID, validationTime);

        assertThat(result, nullValue());
        verifyNoMoreInteractions(detailedReport, certificateQualification);
    }

    @ParameterizedTest
    @EnumSource(CertificateQualification.class)
    void getSigningCertificateQualification_WhenSignatureCertificateQualificationHasNullQualification_ReturnsNull(
            CertificateQualification qualification
    ) {
        XmlSubXCV signingCertificateBlock = createXmlSubXCV(CERTIFICATE_ID);
        doReturn(signingCertificateBlock).when(detailedReport).getSigningCertificate(SIGNATURE_ID);
        XmlSignature signature = createXmlSignature(SIGNATURE_ID);
        doReturn(List.of(signature)).when(detailedReport).getSignatures();
        XmlValidationSignatureQualification signatureQualification = new XmlValidationSignatureQualification();
        signature.setValidationSignatureQualification(signatureQualification);
        XmlValidationCertificateQualification certificateQualification = new XmlValidationCertificateQualification();
        signatureQualification.getValidationCertificateQualification().add(certificateQualification);
        certificateQualification.setId(CERTIFICATE_ID);
        ValidationTime validationTime = ValidationTime.VALIDATION_TIME;
        certificateQualification.setValidationTime(validationTime);
        certificateQualification.setCertificateQualification(qualification);

        CertificateQualification result = reportWrapper.getSigningCertificateQualification(SIGNATURE_ID, validationTime);

        assertThat(result, sameInstance(qualification));
        verifyNoMoreInteractions(detailedReport);
    }

    private static <T extends Enum<T>> T getFirstNonMatchingEnumValue(T enumValue) {
        return Optional
                .ofNullable(enumValue.getDeclaringClass().getEnumConstants())
                .stream()
                .flatMap(Stream::of)
                .filter(Predicate.not(enumValue::equals))
                .findFirst()
                .orElseThrow();
    }

    private static XmlSignature createXmlSignature(String id) {
        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setId(id);
        return xmlSignature;
    }

    private static XmlSubXCV createXmlSubXCV(String id) {
        XmlSubXCV xmlSubXCV = new XmlSubXCV();
        xmlSubXCV.setId(id);
        return xmlSubXCV;
    }

}