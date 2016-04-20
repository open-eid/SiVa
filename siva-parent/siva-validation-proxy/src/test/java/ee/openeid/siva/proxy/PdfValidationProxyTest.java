package ee.openeid.siva.proxy;


import ee.openeid.pdf.webservice.json.PDFDocument;
import ee.openeid.pdf.webservice.json.ValidationService;

import ee.openeid.siva.proxy.PdfValidationProxy;
import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.document.RequestProtocol;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PdfValidationProxyTest {

    private PdfValidationProxy pdfValidationProxy = new PdfValidationProxy();
    private ValidationServiceSpy validationServiceSpy;

    @Before
    public void setUp() {
        validationServiceSpy = new ValidationServiceSpy();
        pdfValidationProxy.setValidationService(validationServiceSpy);
        pdfValidationProxy.setXMLToJSONConverter(new XMLToJSONConverter());
    }

    @Test
    public void returnSimpleReportAsJSON() {
        ProxyDocument proxyDocument= new ProxyDocument();
        proxyDocument.setRequestProtocol(RequestProtocol.JSON);
        proxyDocument.setReportType(ReportType.SIMPLE);
        String report = pdfValidationProxy.validate(proxyDocument);
        assertEquals("{\"SimpleReport\":{\"content\":\"data\"}}", report);
    }

    @Test
    public void returnDetailedReportAsJSON() {
        ProxyDocument proxyDocument= new ProxyDocument();
        proxyDocument.setRequestProtocol(RequestProtocol.JSON);
        proxyDocument.setReportType(ReportType.DETAILED);
        String report = pdfValidationProxy.validate(proxyDocument);
        assertEquals("{\"DetailedReport\":\"data\"}", report);
    }

    @Test
    public void returnDiagnosticDataAsJSON() {
        ProxyDocument proxyDocument= new ProxyDocument();
        proxyDocument.setRequestProtocol(RequestProtocol.JSON);
        proxyDocument.setReportType(ReportType.DIAGNOSTICDATA);
        String report = pdfValidationProxy.validate(proxyDocument);
        assertEquals("{\"DiagnosticData\":\"data\"}", report);
    }

    @Test
    public void returnSimpleReportAsXML() {
        ProxyDocument proxyDocument= new ProxyDocument();
        proxyDocument.setRequestProtocol(RequestProtocol.XML);
        proxyDocument.setReportType(ReportType.SIMPLE);
        String report = pdfValidationProxy.validate(proxyDocument);
        assertEquals("<SimpleReport><xmlns>blah</xmlns>data</SimpleReport>", report);
    }

    private class ValidationServiceSpy implements ValidationService {

        @Override
        public Map<String,String> validateDocument(PDFDocument pdfDocument) {

            Map<String,String> reportMap = new HashMap<>();
            reportMap.put("SIMPLE", "<SimpleReport><xmlns>blah</xmlns>data</SimpleReport>");
            reportMap.put("DETAILED", "<DetailedReport>data</DetailedReport>");
            reportMap.put("DIAGNOSTICDATA", "<DiagnosticData>data</DiagnosticData>");
            return reportMap;
        }
    }

}
