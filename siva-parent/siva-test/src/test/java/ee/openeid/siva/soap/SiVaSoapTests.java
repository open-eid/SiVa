package ee.openeid.siva.soap;

import com.jayway.restassured.response.Response;
import ee.openeid.siva.integrationtest.SiVaIntegrationTestsBase;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.util.Collections;

import static com.jayway.restassured.RestAssured.given;

public abstract class SiVaSoapTests extends SiVaIntegrationTestsBase {

    private static final String SOAP_ENDPOINT = "/soap/validationWebService";

    protected static String createXMLValidationRequest(String base64Document, String documentType, String filename) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <soap:ValidateDocument>\n" +
                "         <soap:ValidationRequest>\n" +
                "            <Document>" + base64Document + "</Document>\n" +
                "            <Filename>" + filename + "</Filename>\n" +
                "            <DocumentType>" + documentType + "</DocumentType>\n" +
                "         </soap:ValidationRequest>\n" +
                "      </soap:ValidateDocument>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    protected String validationRequestForDocument(String filename) {
        return createXMLValidationRequest(
                Base64.encodeBase64String(readFileFromTestResources(filename)),
                parseFileExtension(filename),
                filename);
    }

    protected static String createXMLValidationRequestExtended(String base64Document, String documentType, String filename, String signaturePolicy) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <soap:ValidateDocument>\n" +
                "         <soap:ValidationRequest>\n" +
                "            <Document>" + base64Document + "</Document>\n" +
                "            <Filename>" + filename + "</Filename>\n" +
                "            <DocumentType>" + documentType + "</DocumentType>\n" +
                "            <SignaturePolicy>" + signaturePolicy + "</SignaturePolicy>\n" +
                "         </soap:ValidationRequest>\n" +
                "      </soap:ValidateDocument>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    protected String validationRequestForDocumentExtended(String document, String documentType, String filename, String signaturePolicy) {
        return createXMLValidationRequestExtended(
                document,
                documentType,
                filename,
                signaturePolicy);
    }

    protected Response post(String request) {
        return given().
                contentType("text/xml;charset=UTF-8").
                body(request).
                when().
                post(SOAP_ENDPOINT);
    }

    protected Document extractReportDom(String httpBody) {
        Document document = XMLUtils.parseXml(httpBody);
        Element element = XMLUtils.findElementByXPath(document, "//d:ValidateDocumentResponse/d:ValidationReport", Collections.singletonMap("d", "http://soap.webapp.siva.openeid.ee/"));
        return XMLUtils.documentFromNode(element);
    }

    protected QualifiedReport getQualifiedReportFromDom(Document reportDom) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(QualifiedReport.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Node xmlNode = reportDom.getDocumentElement();
            JAXBElement<QualifiedReport> jaxbElement = unmarshaller.unmarshal(xmlNode, QualifiedReport.class);
            return jaxbElement.getValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected int validSignatures(Document report) {
        String stringResult = XMLUtils.findElementByXPath(
                report,
                "//d:ValidationReport/ValidSignaturesCount",
                Collections.singletonMap("d", "http://soap.webapp.siva.openeid.ee/")).getTextContent();
        return Integer.parseInt(stringResult);
    }

    @Override
    protected QualifiedReport postForReport(String filename) {
        return getQualifiedReportFromDom(extractReportDom(post(validationRequestForDocument(filename)).andReturn().body().asString()));
    }

}
