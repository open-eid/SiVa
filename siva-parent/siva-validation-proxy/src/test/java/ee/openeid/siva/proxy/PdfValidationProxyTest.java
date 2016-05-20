package ee.openeid.siva.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.document.RequestProtocol;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.validation.service.pdf.PDFValidationService;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PdfValidationProxyTest {


    private PdfValidationProxy pdfValidationProxy = new PdfValidationProxy();
    private ValidationServiceSpy validationServiceSpy;

    @Before
    public void setUp() {
        validationServiceSpy = new ValidationServiceSpy();
        pdfValidationProxy.setPDFValidationService(validationServiceSpy);
    }

    @Test
    public void whenRequestProtocolJSONAndReportTypeSimpleThenReturnQualifiedAsJSON() throws IOException {
        String jsonReport = pdfValidationProxy.validate(getProxyDocument(ReportType.SIMPLE));
        assertJsonReportRepresentsQualifiedReport(jsonReport);
    }

    @Test
    public void whenRequestProtocolJSONAndReportTypeDetailedThenReturnQualifiedAsJSON() throws IOException {
        String jsonReport = pdfValidationProxy.validate(getProxyDocument(ReportType.DETAILED));
        assertJsonReportRepresentsQualifiedReport(jsonReport);
    }

    @Test
    public void whenRequestProtocolJSONAndReportTypeDiagnosticDataThenReturnQualifiedAsJSON() throws IOException {
        String jsonReport = pdfValidationProxy.validate(getProxyDocument(ReportType.DIAGNOSTICDATA));
        assertJsonReportRepresentsQualifiedReport(jsonReport);
    }

    private ProxyDocument getProxyDocument(ReportType reportType) {
        ProxyDocument proxyDocument= new ProxyDocument();
        proxyDocument.setRequestProtocol(RequestProtocol.JSON);
        proxyDocument.setDocumentType(DocumentType.PDF);
        proxyDocument.setReportType(reportType);
        return proxyDocument;
    }

    @Test
    public void whenRequestProtocolXMLAndReportTypeSimpleThenReturnQualifiedReportAsXML() {
        ProxyDocument proxyDocument= getProxyDocument(ReportType.SIMPLE);
        proxyDocument.setRequestProtocol(RequestProtocol.XML);
        String xmlReport = pdfValidationProxy.validate(proxyDocument);
        assertNotNull(xmlReport);
    }

    private void assertJsonReportRepresentsQualifiedReport(String jsonReport) throws IOException {
        QualifiedReport qualifiedReport = new ObjectMapper().readValue(jsonReport, QualifiedReport.class);
        assertEquals(validationServiceSpy.qualifiedReport, qualifiedReport);
    }

    private class ValidationServiceSpy extends PDFValidationService {

        QualifiedReport qualifiedReport;

        @Override
        public QualifiedReport validateDocument(ValidationDocument validationDocument) {
            qualifiedReport = createDummyReport();
            return qualifiedReport;
        }
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
        info.setNameId("NameId");
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
