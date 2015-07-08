package ee.sk.pdfvalidatortest;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;
import org.w3c.dom.Document;

public class InvalidDocumentFormatTests extends PdfValidatorSoapTests {
	
	@Test
    public void PAdESDocumentShouldPass() {
        String httpBody = post(validationRequestFor(readFile("Signature-P-EE_AS-7.pdf"))).
                andReturn().body().asString();
        
        assertEquals("PAdES_BASELINE_LT", signatureFormat(simpleReport(httpBody)));
    }
	
	@Test
    public void ASiCDocumentShouldFail() {
        String httpBody = post(validationRequestFor(readFile("Signature-A-EE-1.asice"))).
                andReturn().body().asString();
        
        assertEquals("Document format not recognized/handled", soapFaultstring(httpBody));
    }
	
	@Test
    public void XAdESDocumentShouldFail() {
        String httpBody = post(validationRequestFor(readFile("Signature-X-AT-1.xml"))).
                andReturn().body().asString();
        
        assertEquals("Document format not recognized/handled", soapFaultstring(httpBody));
    }
	
	@Test
    public void CAdESDocumentShouldFail() {
        String httpBody = post(validationRequestFor(readFile("Signature-C-AT-1.p7"))).
                andReturn().body().asString();
        
        assertEquals("Document format not recognized/handled", soapFaultstring(httpBody));
    }
	
	private byte[] readFile(String fileName) {
    	return readFileFromTestResources("invalid_format_documents/", fileName);
    }
	
	private String signatureFormat(Document simpleReport) {
        String stringResult = XmlUtil.findElementByXPath(
                simpleReport,
                "//d:SimpleReport/d:Signature",
                Collections.singletonMap("d", "http://dss.esig.europa.eu/validation/diagnostic")).getAttribute("SignatureFormat");

        return stringResult;
    }

	private Document simpleReport(String httpBody) {
        Document document = XmlUtil.parseXml(httpBody);
        String detailedReportString = XmlUtil.findElementByXPath(document, "//xmlSimpleReport").getTextContent();
        return XmlUtil.parseXml(detailedReportString);
    }
	
	private String soapFaultstring(String httpBody) {
		Document document = XmlUtil.parseXml(httpBody);
        String stringResult = XmlUtil.findElementByXPath(
                document,
                "//soap:Fault/faultstring",
                Collections.singletonMap("soap", "http://schemas.xmlsoap.org/soap/envelope/")).getTextContent();

        return stringResult;
    }

}