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

package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.exception.ValidatonServiceNotFoundException;
import ee.openeid.siva.proxy.http.RESTProxyService;
import ee.openeid.siva.statistics.StatisticsService;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.exception.DocumentRequirementsException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.validation.service.bdoc.BDOCValidationService;
import ee.openeid.validation.service.ddoc.DDOCValidationService;
import ee.openeid.validation.service.generic.GenericValidationService;
import ee.openeid.validation.service.generic.configuration.GenericSignaturePolicyProperties;
import ee.openeid.validation.service.timestamptoken.TimeStampTokenValidationService;
import ee.openeid.validation.service.timestamptoken.configuration.TimeStampTokenSignaturePolicyProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class ValidationProxyTest {
    private static final String DEFAULT_DOCUMENT_NAME = "document.";
    private static final String TEST_FILES_LOCATION = "test-files/";
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ValidationProxy validationProxy;

    private ApplicationContext applicationContext;

    private ValidationServiceSpy validationServiceSpy;

    private RESTProxyService restProxyService;

    private StatisticsService statisticsService;

    @Before
    public void setUp() {
        validationProxy = new ValidationProxy();

        applicationContext = mock(ApplicationContext.class);
        validationProxy.setApplicationContext(applicationContext);

        restProxyService = mock(RESTProxyService.class);
        validationProxy.setRestProxyService(restProxyService);

        statisticsService = mock(StatisticsService.class);
        validationProxy.setStatisticsService(statisticsService);

        validationServiceSpy = new ValidationServiceSpy();
    }

    @Test
    public void givenXroadValidationWillReturnValidatedDocument() throws Exception {

        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setSignaturesCount(1);
        validationConclusion.setValidSignaturesCount(1);

        given(restProxyService.validate(any(ValidationDocument.class))).willReturn(validationConclusion);
        ProxyDocument proxyDocument = mockProxyDocumentWithDocument(DocumentType.XROAD);

        Report validationReport = validationProxy.validate(proxyDocument);
        verify(restProxyService).validate(any(ValidationDocument.class));

        assertThat(validationReport.getValidationConclusion().getValidSignaturesCount()).isEqualTo(1);
        assertThat(validationReport.getValidationConclusion().getSignaturesCount()).isEqualTo(1);
    }

    @Test
    public void applicationContextHasNoBeanWithGivenNameThrowsException() throws Exception {
        given(applicationContext.getBean(anyString())).willThrow(new NoSuchBeanDefinitionException("Bean not loaded"));

        exception.expect(ValidatonServiceNotFoundException.class);
        exception.expectMessage("genericValidationService not found");
        ProxyDocument proxyDocument = mockProxyDocumentWithDocument(DocumentType.PDF);
        validationProxy.validate(proxyDocument);

        verify(applicationContext).getBean(anyString());
    }

    @Test
    public void proxyDocumentWithBDOCDocumentTypeShouldReturnQualifiedReport() throws Exception {
        when(applicationContext.getBean(BDOCValidationService.class.getSimpleName())).thenReturn(validationServiceSpy);

        ProxyDocument proxyDocument = mockProxyDocumentWithDocument(DocumentType.BDOC);
        Report report = validationProxy.validate(proxyDocument);
        assertQualifiedReport(report);
    }

    @Test
    public void proxyDocumentWithPDFDocumentTypeShouldReturnQualifiedReport() throws Exception {
        when(applicationContext.getBean("genericValidationService")).thenReturn(validationServiceSpy);

        ProxyDocument proxyDocument = mockProxyDocumentWithDocument(DocumentType.PDF);
        Report report = validationProxy.validate(proxyDocument);
        assertQualifiedReport(report);
    }

    @Test
    public void proxyDocumentWithDDOCDocumentTypeShouldReturnQualifiedReport() throws Exception {
        when(applicationContext.getBean(DDOCValidationService.class.getSimpleName())).thenReturn(validationServiceSpy);

        ProxyDocument proxyDocument = mockProxyDocumentWithDocument(DocumentType.DDOC);
        Report report = validationProxy.validate(proxyDocument);
        assertQualifiedReport(report);
    }

    @Test
    public void proxyDocumentWithAsicsExtensionShouldReturnQualifiedReport() throws Exception {

        when(applicationContext.getBean("timeStampTokenValidationService")).thenReturn(getTimeStampValidationService());
        when(applicationContext.getBean(DDOCValidationService.class.getSimpleName())).thenReturn(validationServiceSpy);

        ProxyDocument proxyDocument = mockProxyDocumentWithExtension("asics");
        proxyDocument.setBytes(buildValidationDocument("timestamptoken-ddoc.asics"));
        Report report = validationProxy.validate(proxyDocument);
        TimeStampTokenValidationData timeStampTokenValidationData = report.getValidationConclusion().getTimeStampTokens().get(0);
        Assert.assertEquals(TimeStampTokenValidationData.Indication.TOTAL_PASSED, timeStampTokenValidationData.getIndication());
        assertQualifiedReport(report);
    }

    @Test
    public void proxyDocumentWithZipExtensionShouldReturnQualifiedReport() throws Exception {

        when(applicationContext.getBean("timeStampTokenValidationService")).thenReturn(getTimeStampValidationService());
        when(applicationContext.getBean(DDOCValidationService.class.getSimpleName())).thenReturn(validationServiceSpy);

        ProxyDocument proxyDocument = mockProxyDocumentWithExtension("zip");
        proxyDocument.setBytes(buildValidationDocument("timestamptoken-ddoc.zip"));
        Report report = validationProxy.validate(proxyDocument);
        TimeStampTokenValidationData timeStampTokenValidationData = report.getValidationConclusion().getTimeStampTokens().get(0);
        Assert.assertEquals(TimeStampTokenValidationData.Indication.TOTAL_PASSED, timeStampTokenValidationData.getIndication());
        assertQualifiedReport(report);
    }

    @Test
    public void proxyDocumentWithScsExtensionShouldReturnQualifiedReport() throws Exception {

        when(applicationContext.getBean("timeStampTokenValidationService")).thenReturn(getTimeStampValidationService());
        when(applicationContext.getBean(DDOCValidationService.class.getSimpleName())).thenReturn(validationServiceSpy);

        ProxyDocument proxyDocument = mockProxyDocumentWithExtension("scs");
        proxyDocument.setBytes(buildValidationDocument("timestamptoken-ddoc.asics"));
        Report report = validationProxy.validate(proxyDocument);
        TimeStampTokenValidationData timeStampTokenValidationData = report.getValidationConclusion().getTimeStampTokens().get(0);
        Assert.assertEquals(TimeStampTokenValidationData.Indication.TOTAL_PASSED, timeStampTokenValidationData.getIndication());
        assertQualifiedReport(report);
    }

    @Test
    public void proxyDocumentAsicsWithRandomDataFile() throws Exception {
        when(applicationContext.getBean("timeStampTokenValidationService")).thenReturn(getTimeStampValidationService());
        when(applicationContext.getBean("genericValidationService")).thenReturn(getGenericValidationService());
        ProxyDocument proxyDocument = mockProxyDocumentWithExtension("asics");
        proxyDocument.setBytes(buildValidationDocument("TXTinsideAsics.asics"));
        Report report = validationProxy.validate(proxyDocument);
        TimeStampTokenValidationData timeStampTokenValidationData = report.getValidationConclusion().getTimeStampTokens().get(0);
        Assert.assertEquals(TimeStampTokenValidationData.Indication.TOTAL_PASSED, timeStampTokenValidationData.getIndication());
    }

    @Test
    public void proxyDocumentAsicsWithTwoDataFiles() throws Exception {
        exception.expect(DocumentRequirementsException.class);
        when(applicationContext.getBean("timeStampTokenValidationService")).thenReturn(getTimeStampValidationService());
        ProxyDocument proxyDocument = mockProxyDocumentWithExtension("asics");
        proxyDocument.setBytes(buildValidationDocument("TwoDataFilesAsics.asics"));
        validationProxy.validate(proxyDocument);
    }

    @Test
    public void proxyDocumentAsicsWithDifferentMimeType() throws Exception {
        when(applicationContext.getBean("timeStampTokenValidationService")).thenReturn(getTimeStampValidationService());
        when(applicationContext.getBean("genericValidationService")).thenReturn(validationServiceSpy);
        ProxyDocument proxyDocument = mockProxyDocumentWithExtension("zip");
        proxyDocument.setBytes(buildValidationDocument("timestamptoken-different-mimetype.zip"));
        Report report = validationProxy.validate(proxyDocument);
        assertQualifiedReport(report);
    }

    private GenericValidationService getGenericValidationService() {
        GenericValidationService validationService = new GenericValidationService();
        GenericSignaturePolicyProperties policyProperties = new GenericSignaturePolicyProperties();
        policyProperties.initPolicySettings();
        ConstraintLoadingSignaturePolicyService signaturePolicyService = new ConstraintLoadingSignaturePolicyService(policyProperties);
        validationService.setSignaturePolicyService(signaturePolicyService);
        return validationService;
    }

    private TimeStampTokenValidationService getTimeStampValidationService() {
        TimeStampTokenValidationService validationService = new TimeStampTokenValidationService();
        TimeStampTokenSignaturePolicyProperties policyProperties = new TimeStampTokenSignaturePolicyProperties();
        policyProperties.initPolicySettings();
        SignaturePolicyService<ValidationPolicy> signaturePolicyService = new SignaturePolicyService<>(policyProperties);
        validationService.setSignaturePolicyService(signaturePolicyService);
        return validationService;
    }

    private ProxyDocument mockProxyDocumentWithDocument(DocumentType documentType) {
        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setDocumentType(documentType);
        proxyDocument.setName(DEFAULT_DOCUMENT_NAME + documentType.name());
        return proxyDocument;
    }

    private ProxyDocument mockProxyDocumentWithExtension(String fileName) {
        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setName(DEFAULT_DOCUMENT_NAME + fileName);
        return proxyDocument;
    }

    private void assertQualifiedReport(Report report) throws IOException {
        assertEquals(validationServiceSpy.qualifiedReport.getSimpleReport(), report);
    }

    private byte[] buildValidationDocument(String testFile) throws Exception {
        Path documentPath = Paths.get(getClass().getClassLoader().getResource(TEST_FILES_LOCATION + testFile).toURI());
        return Files.readAllBytes(documentPath);
    }

    private class ValidationServiceSpy implements ValidationService {

        QualifiedReport qualifiedReport;

        @Override
        public QualifiedReport validateDocument(ValidationDocument validationDocument) {
            qualifiedReport = createDummyReport();
            return qualifiedReport;
        }

        private QualifiedReport createDummyReport() {
            ValidationConclusion validationConclusion = new ValidationConclusion();
            validationConclusion.setValidSignaturesCount(0);
            validationConclusion.setSignaturesCount(1);
            validationConclusion.setValidationTime("ValidationTime");
            validationConclusion.setDocumentName("DocumentName");
            validationConclusion.setPolicy(createDummyPolicy());
            validationConclusion.setSignatures(createDummySignatures());
            return  new QualifiedReport(new SimpleReport(validationConclusion), null);
        }

        private List<SignatureValidationData> createDummySignatures() {
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

        private List<Error> createDummyErrors() {
            Error error = new Error();
            error.setContent("ErrorContent");
            return Collections.singletonList(error);
        }

        private Info createDummySignatureInfo() {
            Info info = new Info();
            info.setBestSignatureTime("BestSignatureTime");
            return info;
        }

        private Policy createDummyPolicy() {
            Policy policy = new Policy();
            policy.setPolicyDescription("PolicyDescription");
            policy.setPolicyName("PolicyName");
            policy.setPolicyUrl("http://policyUrl.com");
            return policy;
        }
    }

}
