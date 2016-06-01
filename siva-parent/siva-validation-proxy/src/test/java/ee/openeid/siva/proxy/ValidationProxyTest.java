package ee.openeid.siva.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.RequestProtocol;
import ee.openeid.siva.proxy.exception.ValidatonServiceNotFoundException;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.validation.service.bdoc.BDOCValidationService;
import ee.openeid.validation.service.ddoc.DDOCValidationService;
import ee.openeid.validation.service.pdf.PDFValidationService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ValidationProxyTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ValidationProxy validationProxy;

    private ApplicationContext applicationContext;

    private ValidationServiceSpy validationServiceSpy;

    @Before
    public void setUp() {
        validationProxy = new ValidationProxy();
        applicationContext = Mockito.mock(ApplicationContext.class);
        validationProxy.setApplicationContext(applicationContext);
        validationServiceSpy = new ValidationServiceSpy();
    }

    @Test
    public void ProxyDocumentWithASICEDocumentTypeShouldThrowValidationServiceNotFoundException() {
        exception.expect(ValidatonServiceNotFoundException.class);
        exception.expectMessage("ASICEValidationService not found");
        when(applicationContext.getBean("ASICEValidationService")).thenThrow(new ValidatonServiceNotFoundException("ASICEValidationService not found"));

        ProxyDocument proxyDocument = mockProxyDocumentWithDocument(DocumentType.ASICE, RequestProtocol.JSON);
        validationProxy.validate(proxyDocument);
    }

    @Test
    public void ProxyDocumentWithBDOCDocumentTypeAndJSONRequestProtocolShouldReturnQualifiedReportInJSON() throws Exception {
        when(applicationContext.getBean(BDOCValidationService.class.getSimpleName())).thenReturn(validationServiceSpy);

        ProxyDocument proxyDocument = mockProxyDocumentWithDocument(DocumentType.BDOC, RequestProtocol.JSON);
        String report = validationProxy.validate(proxyDocument);
        assertJsonReportRepresentsQualifiedReport(report);
    }

    @Test
    public void ProxyDocumentWithPDFDocumentTypeAndJSONRequestProtocolShouldReturnQualifiedReportInJSON() throws Exception {
        when(applicationContext.getBean(PDFValidationService.class.getSimpleName())).thenReturn(validationServiceSpy);

        ProxyDocument proxyDocument = mockProxyDocumentWithDocument(DocumentType.PDF, RequestProtocol.JSON);
        String report = validationProxy.validate(proxyDocument);
        assertJsonReportRepresentsQualifiedReport(report);
    }

    @Test
    public void ProxyDocumentWithDDOCDocumentTypeAndJSONRequestProtocolShouldReturnQualifiedReportInJSON() throws Exception {
        when(applicationContext.getBean(DDOCValidationService.class.getSimpleName())).thenReturn(validationServiceSpy);

        ProxyDocument proxyDocument = mockProxyDocumentWithDocument(DocumentType.DDOC, RequestProtocol.JSON);
        String report = validationProxy.validate(proxyDocument);
        assertJsonReportRepresentsQualifiedReport(report);
    }

    private ProxyDocument mockProxyDocumentWithDocument(DocumentType documentType, RequestProtocol requestProtocol) {
        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setDocumentType(documentType);
        proxyDocument.setRequestProtocol(requestProtocol);
        return proxyDocument;
    }

    private void assertJsonReportRepresentsQualifiedReport(String jsonReport) throws IOException {
        QualifiedReport qualifiedReport = new ObjectMapper().readValue(jsonReport, QualifiedReport.class);
        assertEquals(validationServiceSpy.qualifiedReport, qualifiedReport);
    }

    private class ValidationServiceSpy implements ValidationService {

        QualifiedReport qualifiedReport;

        @Override
        public QualifiedReport validateDocument(ValidationDocument validationDocument) {
            qualifiedReport = createDummyReport();
            return qualifiedReport;
        }

        private QualifiedReport createDummyReport() {
            QualifiedReport report = new QualifiedReport();
            report.setValidSignaturesCount(0);
            report.setSignaturesCount(1);
            report.setValidationTime("ValidationTime");
            report.setDocumentName("DocumentName");
            report.setPolicy(createDummyPolicy());
            report.setSignatures(createDummySignatures());
            return report;
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
            error.setNameId("NameId");
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