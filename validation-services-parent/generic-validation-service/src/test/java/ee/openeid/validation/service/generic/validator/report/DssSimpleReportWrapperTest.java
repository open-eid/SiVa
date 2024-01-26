/*
 * Copyright 2022 - 2024 Riigi Infosüsteemi Amet
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

import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.simplereport.jaxb.XmlDetails;
import eu.europa.esig.dss.simplereport.jaxb.XmlMessage;
import eu.europa.esig.dss.simplereport.jaxb.XmlSignature;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import eu.europa.esig.dss.simplereport.jaxb.XmlToken;
import eu.europa.esig.dss.validation.reports.Reports;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class DssSimpleReportWrapperTest {

    @Mock
    private SimpleReport simpleReport;
    @Mock
    private XmlSimpleReport xmlSimpleReport;

    @Test
    void testWrapperCreationFromReports() {
        Reports dssReports = Mockito.mock(Reports.class);
        Mockito.doReturn(simpleReport).when(dssReports).getSimpleReport();

        DssSimpleReportWrapper wrapper = new DssSimpleReportWrapper(dssReports);
        SimpleReport result = wrapper.getDssSimpleReport();

        Assertions.assertSame(simpleReport, result);
        Mockito.verify(dssReports).getSimpleReport();
        Mockito.verifyNoMoreInteractions(dssReports);
        Mockito.verifyNoInteractions(simpleReport, xmlSimpleReport);
    }

    @Test
    void testWrapperCreationFromSimpleReport() {
        DssSimpleReportWrapper wrapper = new DssSimpleReportWrapper(simpleReport);
        SimpleReport result = wrapper.getDssSimpleReport();

        Assertions.assertSame(simpleReport, result);
        Mockito.verifyNoInteractions(simpleReport, xmlSimpleReport);
    }

    @Test
    void testGetXmlSimpleReport() {
        DssSimpleReportWrapper wrapper = createWrapperWithMockedReport();

        XmlSimpleReport result = wrapper.getXmlSimpleReport();

        Assertions.assertSame(xmlSimpleReport, result);
        Mockito.verify(simpleReport).getJaxbModel();
        Mockito.verifyNoMoreInteractions(simpleReport);
        Mockito.verifyNoInteractions(xmlSimpleReport);
    }

    @Test
    void testGetXmlSignatureReturnsNullWhenReportContainsNoSignatures() {
        mockXmlSimpleReportGetSignatureOrTimestamp();
        DssSimpleReportWrapper wrapper = createWrapperWithMockedReport();

        XmlSignature result = wrapper.getXmlSignature("ID-123");

        Assertions.assertNull(result);
        verifySimpleReportGetJaxbModelCalled();
        Mockito.verify(xmlSimpleReport).getSignatureOrTimestamp();
        Mockito.verifyNoMoreInteractions(xmlSimpleReport);
    }

    @Test
    void testGetXmlSignatureReturnsNullWhenReportContainsNoSignaturesWithMatchingId() {
        XmlSignature signatureMock = createMockSignatureWithId("ID-234");
        mockXmlSimpleReportGetSignatureOrTimestamp(signatureMock);
        DssSimpleReportWrapper wrapper = createWrapperWithMockedReport();

        XmlSignature result = wrapper.getXmlSignature("ID-123");

        Assertions.assertNull(result);
        verifySimpleReportGetJaxbModelCalled();
        Mockito.verify(xmlSimpleReport).getSignatureOrTimestamp();
        Mockito.verify(signatureMock).getId();
        Mockito.verifyNoMoreInteractions(xmlSimpleReport, signatureMock);
    }

    @Test
    void testGetXmlSignatureReturnsSignatureWhenReportContainsSingleSignatureWithMatchingId() {
        XmlSignature signatureMock = createMockSignatureWithId("ID-123");
        mockXmlSimpleReportGetSignatureOrTimestamp(signatureMock);
        DssSimpleReportWrapper wrapper = createWrapperWithMockedReport();

        XmlSignature result = wrapper.getXmlSignature("ID-123");

        Assertions.assertSame(signatureMock, result);
        verifySimpleReportGetJaxbModelCalled();
        Mockito.verify(xmlSimpleReport).getSignatureOrTimestamp();
        Mockito.verify(signatureMock).getId();
        Mockito.verifyNoMoreInteractions(xmlSimpleReport, signatureMock);
    }

    @Test
    void testGetXmlSignatureReturnsSignatureWhenReportContainsMultipleSignaturesAndOneWithMatchingId() {
        XmlSignature signatureMock1 = createMockSignatureWithId("ID-012");
        XmlSignature signatureMock2 = createMockSignatureWithId("ID-123");
        XmlSignature signatureMock3 = createMockSignature();
        mockXmlSimpleReportGetSignatureOrTimestamp(signatureMock1, signatureMock2, signatureMock3);
        DssSimpleReportWrapper wrapper = createWrapperWithMockedReport();

        XmlSignature result = wrapper.getXmlSignature("ID-123");

        Assertions.assertSame(signatureMock2, result);
        verifySimpleReportGetJaxbModelCalled();
        Mockito.verify(xmlSimpleReport).getSignatureOrTimestamp();
        Mockito.verify(signatureMock1).getId();
        Mockito.verify(signatureMock2).getId();
        Mockito.verifyNoMoreInteractions(xmlSimpleReport, signatureMock1, signatureMock2);
        Mockito.verifyNoInteractions(signatureMock3);
    }

    @ParameterizedTest
    @MethodSource("xmlDetailsGetters")
    void testGetSignatureXmlDetailsReturnsNullWhenReportContainsNoSignatures(BiFunction<DssSimpleReportWrapper, String, XmlDetails> xmlDetailsGetter) {
        mockXmlSimpleReportGetSignatureOrTimestamp();
        DssSimpleReportWrapper wrapper = createWrapperWithMockedReport();

        XmlDetails result = xmlDetailsGetter.apply(wrapper, "ID-123");

        Assertions.assertNull(result);
        verifySimpleReportGetJaxbModelCalled();
        Mockito.verify(xmlSimpleReport).getSignatureOrTimestamp();
        Mockito.verifyNoMoreInteractions(xmlSimpleReport);
    }

    @ParameterizedTest
    @MethodSource("xmlDetailsGetters")
    void testGetSignatureXmlDetailsReturnsNullWhenReportContainsNoSignaturesWithMatchingId(BiFunction<DssSimpleReportWrapper, String, XmlDetails> xmlDetailsGetter) {
        XmlSignature signatureMock = createMockSignatureWithId("ID-234");
        mockXmlSimpleReportGetSignatureOrTimestamp(signatureMock);
        DssSimpleReportWrapper wrapper = createWrapperWithMockedReport();

        XmlDetails result = xmlDetailsGetter.apply(wrapper, "ID-123");

        Assertions.assertNull(result);
        verifySimpleReportGetJaxbModelCalled();
        Mockito.verify(xmlSimpleReport).getSignatureOrTimestamp();
        Mockito.verify(signatureMock).getId();
        Mockito.verifyNoMoreInteractions(xmlSimpleReport, signatureMock);
    }

    static Stream<BiFunction<DssSimpleReportWrapper, String, XmlDetails>> xmlDetailsGetters() {
        return Stream.of(
                DssSimpleReportWrapper::getSignatureAdESValidationXmlDetails,
                DssSimpleReportWrapper::getSignatureQualificationXmlDetails
        );
    }

    @Test
    void testGetSignatureAdESValidationXmlDetailsReturnsXmlDetailsWhenReportContainsSignatureWithMatchingIdAndAdESValidationDetails() {
        XmlSignature signatureMock1 = createMockSignatureWithId("ID-012");
        XmlSignature signatureMock2 = createMockSignatureWithId("ID-123");
        XmlSignature signatureMock3 = createMockSignature();
        XmlDetails adESValidationDetailsMock = createMockDetails();
        Mockito.doReturn(adESValidationDetailsMock).when(signatureMock2).getAdESValidationDetails();
        mockXmlSimpleReportGetSignatureOrTimestamp(signatureMock1, signatureMock2, signatureMock3);
        DssSimpleReportWrapper wrapper = createWrapperWithMockedReport();

        XmlDetails result = wrapper.getSignatureAdESValidationXmlDetails("ID-123");

        Assertions.assertSame(adESValidationDetailsMock, result);
        verifySimpleReportGetJaxbModelCalled();
        Mockito.verify(xmlSimpleReport).getSignatureOrTimestamp();
        Mockito.verify(signatureMock1).getId();
        Mockito.verify(signatureMock2).getId();
        Mockito.verify(signatureMock2).getAdESValidationDetails();
        Mockito.verifyNoMoreInteractions(xmlSimpleReport, signatureMock1, signatureMock2);
        Mockito.verifyNoInteractions(signatureMock3, adESValidationDetailsMock);
    }

    @Test
    void testGetSignatureQualificationXmlDetailsReturnsXmlDetailsWhenReportContainsSignatureWithMatchingIdAndQualificationDetails() {
        XmlSignature signatureMock1 = createMockSignatureWithId("ID-012");
        XmlSignature signatureMock2 = createMockSignatureWithId("ID-123");
        XmlSignature signatureMock3 = createMockSignature();
        XmlDetails qualificationDetailsMock = createMockDetails();
        Mockito.doReturn(qualificationDetailsMock).when(signatureMock2).getQualificationDetails();
        mockXmlSimpleReportGetSignatureOrTimestamp(signatureMock1, signatureMock2, signatureMock3);
        DssSimpleReportWrapper wrapper = createWrapperWithMockedReport();

        XmlDetails result = wrapper.getSignatureQualificationXmlDetails("ID-123");

        Assertions.assertSame(qualificationDetailsMock, result);
        verifySimpleReportGetJaxbModelCalled();
        Mockito.verify(xmlSimpleReport).getSignatureOrTimestamp();
        Mockito.verify(signatureMock1).getId();
        Mockito.verify(signatureMock2).getId();
        Mockito.verify(signatureMock2).getQualificationDetails();
        Mockito.verifyNoMoreInteractions(xmlSimpleReport, signatureMock1, signatureMock2);
        Mockito.verifyNoInteractions(signatureMock3, qualificationDetailsMock);
    }

    @Test
    void testGetSignatureAdESValidationXmlDetailsCreatesNewXmlDetailsWhenReportContainsSignatureWithMatchingIdButNoAdESValidationDetails() {
        XmlSignature signatureMock1 = createMockSignatureWithId("ID-012");
        XmlSignature signatureMock2 = createMockSignatureWithId("ID-123");
        XmlSignature signatureMock3 = createMockSignature();
        Mockito.doReturn(null).when(signatureMock2).getAdESValidationDetails();
        mockXmlSimpleReportGetSignatureOrTimestamp(signatureMock1, signatureMock2, signatureMock3);
        DssSimpleReportWrapper wrapper = createWrapperWithMockedReport();

        XmlDetails result = wrapper.getSignatureAdESValidationXmlDetails("ID-123");

        Assertions.assertNotNull(result);
        verifySimpleReportGetJaxbModelCalled();
        Mockito.verify(xmlSimpleReport).getSignatureOrTimestamp();
        Mockito.verify(signatureMock1).getId();
        Mockito.verify(signatureMock2).getId();
        Mockito.verify(signatureMock2).getAdESValidationDetails();
        Mockito.verify(signatureMock2).setAdESValidationDetails(Mockito.any(XmlDetails.class));
        Mockito.verifyNoMoreInteractions(xmlSimpleReport, signatureMock1, signatureMock2);
        Mockito.verifyNoInteractions(signatureMock3);
    }

    @Test
    void testGetSignatureQualificationXmlDetailsCreatesNewXmlDetailsWhenReportContainsSignatureWithMatchingIdButNoQualificationDetails() {
        XmlSignature signatureMock1 = createMockSignatureWithId("ID-012");
        XmlSignature signatureMock2 = createMockSignatureWithId("ID-123");
        XmlSignature signatureMock3 = createMockSignature();
        Mockito.doReturn(null).when(signatureMock2).getQualificationDetails();
        mockXmlSimpleReportGetSignatureOrTimestamp(signatureMock1, signatureMock2, signatureMock3);
        DssSimpleReportWrapper wrapper = createWrapperWithMockedReport();

        XmlDetails result = wrapper.getSignatureQualificationXmlDetails("ID-123");

        Assertions.assertNotNull(result);
        verifySimpleReportGetJaxbModelCalled();
        Mockito.verify(xmlSimpleReport).getSignatureOrTimestamp();
        Mockito.verify(signatureMock1).getId();
        Mockito.verify(signatureMock2).getId();
        Mockito.verify(signatureMock2).getQualificationDetails();
        Mockito.verify(signatureMock2).setQualificationDetails(Mockito.any(XmlDetails.class));
        Mockito.verifyNoMoreInteractions(xmlSimpleReport, signatureMock1, signatureMock2);
        Mockito.verifyNoInteractions(signatureMock3);
    }

    @Test
    void testCreateXmlMessageWithKeyAndValue() {
        XmlMessage result = DssSimpleReportWrapper.createXmlMessage("key", "value");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("key", result.getKey());
        Assertions.assertEquals("value", result.getValue());
    }

    @Test
    void testCreateXmlMessageWithValueOnly() {
        XmlMessage result = DssSimpleReportWrapper.createXmlMessage("value");

        Assertions.assertNotNull(result);
        Assertions.assertNull(result.getKey());
        Assertions.assertEquals("value", result.getValue());
    }

    private DssSimpleReportWrapper createWrapperWithMockedReport() {
        Mockito.doReturn(xmlSimpleReport).when(simpleReport).getJaxbModel();
        return new DssSimpleReportWrapper(simpleReport);
    }

    private void verifySimpleReportGetJaxbModelCalled() {
        Mockito.verify(simpleReport).getJaxbModel();
        Mockito.verifyNoMoreInteractions(simpleReport);
    }

    private void mockXmlSimpleReportGetSignatureOrTimestamp(List<XmlToken> tokens) {
        Mockito.doReturn(tokens).when(xmlSimpleReport).getSignatureOrTimestamp();
    }

    private void mockXmlSimpleReportGetSignatureOrTimestamp(XmlToken... tokens) {
        List<XmlToken> tokenList = Arrays.stream(tokens).collect(Collectors.toList());
        mockXmlSimpleReportGetSignatureOrTimestamp(tokenList);
    }

    private XmlSignature createMockSignature() {
        return Mockito.mock(XmlSignature.class);
    }

    private XmlSignature createMockSignatureWithId(String id) {
        XmlSignature xmlSignature = createMockSignature();
        Mockito.doReturn(id).when(xmlSignature).getId();
        return xmlSignature;
    }

    private XmlDetails createMockDetails() {
        return Mockito.mock(XmlDetails.class);
    }

}