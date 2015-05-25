package ee.sk.pdfvalidatortest;

import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;

import java.util.Collections;

public class InvalidSignaturesTest extends PdfValidatorSoapTests {
    @Test
    public void missingSignedAttributeForSigningCertificate() {
        String httpBody = post(validationRequestFor(PDF_MISSING_SIGNING_CERT_ATTR)).
                andReturn().body().asString();

        assertEquals(
                "The signed attribute: 'signing-certificate' is absent!",
                findErrorById("BBB_ICS_ISASCP_ANS", detailedReport(httpBody)));
    }

    private String findErrorById(String errorId, Document detailedReport) {
        return XmlUtil.findElementByXPath(
                detailedReport,
                "//d:Error[@NameId='" + errorId + "']",
                Collections.singletonMap("d", "http://dss.markt.ec.europa.eu/validation/diagnostic")).getTextContent();
    }

    private Document detailedReport(String httpBody) {
        Document document = XmlUtil.parseXml(httpBody);
        String detailedReportString = XmlUtil.findElementByXPath(document, "//xmlDetailedReport").getTextContent();
        return XmlUtil.parseXml(detailedReportString);
    }


}