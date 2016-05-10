package ee.openeid.siva.proxy;

import ee.openeid.pdf.webservice.PDFValidationService;
import ee.openeid.pdf.webservice.document.transformer.PDFValidationResult;
import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.document.RequestProtocol;
import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.ValidationDocument;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PdfValidationProxyTest {

    private PdfValidationProxy pdfValidationProxy = new PdfValidationProxy();
    private ValidationServiceSpy validationServiceSpy;

    @Before
    public void setUp() {
        validationServiceSpy = new ValidationServiceSpy();
        pdfValidationProxy.setPDFValidationService(validationServiceSpy);
        pdfValidationProxy.setXMLToJSONConverter(new XMLToJSONConverter());
    }

    @Test
    public void whenRequestProtocolJSONAndReportTypeSimpleThenReturnSimpleReportAsJSON() {
        ProxyDocument proxyDocument= new ProxyDocument();
        proxyDocument.setRequestProtocol(RequestProtocol.JSON);
        proxyDocument.setDocumentType(DocumentType.PDF);
        proxyDocument.setReportType(ReportType.SIMPLE);
        String report = pdfValidationProxy.validate(proxyDocument);
        assertEquals("{\"SimpleReport\":{\"content\":\"data\"}}", report);
    }

    @Test
    public void whenRequestProtocolJSONAndReportTypeDetailedThenReturnDetailedReportAsJSON() {
        ProxyDocument proxyDocument= new ProxyDocument();
        proxyDocument.setRequestProtocol(RequestProtocol.JSON);
        proxyDocument.setDocumentType(DocumentType.PDF);
        proxyDocument.setReportType(ReportType.DETAILED);
        String report = pdfValidationProxy.validate(proxyDocument);
        assertEquals("{\"DetailedReport\":{\"content\":\"data\"}}", report);
    }

    @Test
    public void whenRequestProtocolJSONAndReportTypeDiagnosticDataThenReturnDiagnosticDataAsJSON() {
        ProxyDocument proxyDocument= new ProxyDocument();
        proxyDocument.setRequestProtocol(RequestProtocol.JSON);
        proxyDocument.setDocumentType(DocumentType.PDF);
        proxyDocument.setReportType(ReportType.DIAGNOSTICDATA);
        String report = pdfValidationProxy.validate(proxyDocument);
        assertEquals("{\"DiagnosticData\":{\"content\":\"data\"}}", report);
    }

    @Test
    public void whenRequestProtocolXMLAndReportTypeSimpleThenReturnSimpleReportSimplereportAsXML() {
        ProxyDocument proxyDocument= new ProxyDocument();
        proxyDocument.setRequestProtocol(RequestProtocol.XML);
        proxyDocument.setDocumentType(DocumentType.PDF);
        proxyDocument.setReportType(ReportType.SIMPLE);
        String report = pdfValidationProxy.validate(proxyDocument);
        assertEquals("<SimpleReport xmlns=\"xmlnamespace\"><content>data</content></SimpleReport>", report);
    }

    private class ValidationServiceSpy extends PDFValidationService {

        @Override
        public QualifiedValidationResult validateDocument(ValidationDocument validationDocument) {
            PDFValidationResult validationResult = new PDFValidationResult();
            validationResult.setSimpleReport("<SimpleReport xmlns=\"xmlnamespace\"><content>data</content></SimpleReport>");
            validationResult.setDetailedReport("<DetailedReport xmlns=\"xmlnamespace\"><content>data</content></DetailedReport>");
            validationResult.setDiagnosticData("<DiagnosticData xmlns=\"xmlnamespace\"><content>data</content></DiagnosticData>");
            return validationResult;
        }
    }

}
