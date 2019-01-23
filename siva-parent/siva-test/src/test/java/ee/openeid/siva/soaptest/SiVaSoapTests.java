/*
 * Copyright 2018 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.soaptest;

import ee.openeid.siva.integrationtest.SiVaIntegrationTestsBase;
import ee.openeid.siva.webapp.request.Datafile;
import ee.openeid.siva.webapp.request.JSONHashcodeValidationRequest;
import ee.openeid.siva.webapp.soap.DataFilesReport;
import ee.openeid.siva.webapp.soap.ValidateDocumentResponse;
import ee.openeid.siva.webapp.soap.ValidationReport;
import io.restassured.response.Response;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;


public abstract class SiVaSoapTests extends SiVaIntegrationTestsBase {

    private static final String SOAP_ENDPOINT = "/soap/validationWebService";
    private static final String SOAP_HASHCODE_VALIDATION_ENDPOINT = "/soap/hashcodeValidationWebService";
    private static final String SOAP_DATA_FILES_ENDPOINT = "soap/dataFilesWebService";

    protected static final String CLIENT_FAULT = "soap:Client";
    protected static final String SERVER_FAULT = "soap:Server";

    protected static final String DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE = "Document malformed or not matching documentType";
    protected static final String DOCUMENT_MALFORMED = "Document malformed or not matching documentType";
    protected static final String DOCUMENT_NOT_BASE64 = "Document is not encoded in a valid base64 string";
    protected static final String INVALID_FILENAME = "Invalid filename";
    protected static final String FILENAME_MAY_NOT_BE_EMPTY = "Filename may not be empty";
    protected static final String INVALID_SIGNATURE_POLICY = "Invalid signature policy";
    protected static final String INVALID_DOCUMENT_TYPE = "Invalid document type";
    protected static final String INVALID_DATA_FILE_FILENAME = "Invalid file name. Can only return data files for DDOC type containers.";
    protected static final String DATAFILE_FILENAME_MAY_NOT_BE_EMPTY = "Datafile filename may not be empty";

    protected static String createXMLValidationRequest(String base64Document, String filename) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <soap:ValidateDocument>\n" +
                "         <soap:ValidationRequest>\n" +
                "            <Document>" + base64Document + "</Document>\n" +
                "            <Filename>" + filename + "</Filename>\n" +
                "         </soap:ValidationRequest>\n" +
                "      </soap:ValidateDocument>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    protected String validationRequestForDocument(String filename) {
        return createXMLValidationRequest(
                Base64.encodeBase64String(readFileFromTestResources(filename)),
                filename);
    }

    protected static String createXMLValidationRequestExtended(String base64Document, String filename, String documentType, String signaturePolicy) {

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

    protected static String createXMLValidationRequestWithReportType(String base64Document, String filename, String reportType) {

        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <soap:ValidateDocument>\n" +
                "         <soap:ValidationRequest>\n" +
                "            <Document>" + base64Document + "</Document>\n" +
                "            <Filename>" + filename + "</Filename>\n" +
                "            <ReportType>" + reportType + "</ReportType>\n" +
                "         </soap:ValidationRequest>\n" +
                "      </soap:ValidateDocument>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    protected static String createXMLValidationRequestWithoutDocumentType(String base64Document, String filename, String signaturePolicy) {

        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <soap:ValidateDocument>\n" +
                "         <soap:ValidationRequest>\n" +
                "            <Document>" + base64Document + "</Document>\n" +
                "            <Filename>" + filename + "</Filename>\n" +
                "            <SignaturePolicy>" + signaturePolicy + "</SignaturePolicy>\n" +
                "         </soap:ValidationRequest>\n" +
                "      </soap:ValidateDocument>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    protected static String createXMLValidationRequestForDataFiles(String base64Document, String filename) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <soap:GetDocumentDataFiles>\n" +
                "         <soap:DataFilesRequest>\n" +
                "            <Document>" + base64Document + "</Document>\n" +
                "            <Filename>" + filename + "</Filename>\n" +
                "            </soap:DataFilesRequest>\n" +
                "      </soap:GetDocumentDataFiles>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    protected String validationRequestForDocumentExtended(String document, String filename, String documentType, String signaturePolicy) {
        if(documentType==null){
            return createXMLValidationRequestWithoutDocumentType(document, filename, signaturePolicy);
        }
        return createXMLValidationRequestExtended(
                document,
                filename,
                documentType,
                signaturePolicy);
    }

    protected String validationRequestForDocumentReportType(String filename, String reportType) {
        return createXMLValidationRequestWithReportType(
                Base64.encodeBase64String(readFileFromTestResources(filename)),
                filename, reportType);
    }

    protected String validationRequestForDocumentDataFilesExtended(String document, String filename) {
        return createXMLValidationRequestForDataFiles(
                document,
                filename);
    }

    protected Response post(String request) {
        return given().
                contentType("text/xml;charset=UTF-8").
                body(request).
                when().
                post(SOAP_ENDPOINT);
    }

    protected Response postDataFiles(String request) {
        return given().
                contentType("text/xml;charset=UTF-8").
                body(request).
                when().
                post(SOAP_DATA_FILES_ENDPOINT);
    }

    protected Response postHashcodeValidation(JSONHashcodeValidationRequest request) {
        return postHashcodeValidation(createXMLHashcodeValidationRequest(request));
    }

    protected Response postHashcodeValidation(String request) {
        return given().
                contentType("text/xml;charset=UTF-8").
                body(request).
                when().
                post(SOAP_HASHCODE_VALIDATION_ENDPOINT);
    }

    protected static String createXMLHashcodeValidationRequest(JSONHashcodeValidationRequest request) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" + //TODO: uncomment
//                "      <soap:HashcodeValidationDocument>\n" +
//                "         <soap:HashcodeValidationRequest>\n" +
//                "           " + addParameter("SignatureFile", request.getSignatureFile()) +
//                "           " + addParameter("Filename", request.getFilename()) +
//                "           " + addParameter("ReportType", request.getReportType()) +
//                "           " + addParameter("SignaturePolicy", request.getSignaturePolicy()) +
//                "           " + formDataFilesBlock(request.getDatafiles()) +
                "         </soap:HashcodeValidationRequest>\n" +
                "      </soap:HashcodeValidationDocument>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    private static String formDataFilesBlock(List<Datafile> datafiles) {
        if (datafiles == null) {
            return "";
        }
        StringBuilder dataFilesBlockBuilder = new StringBuilder();
        dataFilesBlockBuilder.append("<DataFiles>");
        datafiles.forEach(datafile -> {
            dataFilesBlockBuilder
                    .append("<DataFile>")
                    .append("   " + addParameter("Filename", datafile.getFilename()))
                    .append("   " + addParameter("HashAlgo", datafile.getHashAlgo()))
                    .append("   " + addParameter("Hash", datafile.getHash()))
                    .append("</DataFile>");
        });
        dataFilesBlockBuilder.append("</DataFiles>");
        return dataFilesBlockBuilder.toString();
    }

    private static String addParameter(String tag, String value) {
        if (value != null) {
            return "<" + tag + ">" + value + "</" + tag + ">\n";
        } else {
            return "";
        }
    }

    protected Document extractReportDom(String httpBody) {
        Document document = XMLUtils.parseXml(httpBody);
        Element element = XMLUtils.findElementByXPath(document, "//d:ValidateDocumentResponse/d:ValidationReport", Collections.singletonMap("d", "http://soap.webapp.siva.openeid.ee/"));
        return XMLUtils.documentFromNode(element);
    }

    protected Document extractValidateDocumentResponseDom(String httpBody) {
        Document document = XMLUtils.parseXml(httpBody);
        Element element = XMLUtils.findElementByXPath(document, "//d:ValidateDocumentResponse", Collections.singletonMap("d", "http://soap.webapp.siva.openeid.ee/"));
        return XMLUtils.documentFromNode(element);
    }

    protected Document extractDataFilesReportDom(String httpBody) {
        Document document = XMLUtils.parseXml(httpBody);
        Element element = XMLUtils.findElementByXPath(document, "//d:GetDocumentDataFilesResponse/d:DataFilesReport", Collections.singletonMap("d", "http://soap.webapp.siva.openeid.ee/"));
        return XMLUtils.documentFromNode(element);
    }

    protected ValidationReport getValidationReportFromDom(Document reportDom) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ValidationReport.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Node xmlNode = reportDom.getDocumentElement();
            JAXBElement<ValidationReport> jaxbElement = unmarshaller.unmarshal(xmlNode, ValidationReport.class);
            return jaxbElement.getValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ValidateDocumentResponse getValidateDocumentResponseFromDom(Document reportDom) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ValidateDocumentResponse.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Node xmlNode = reportDom.getDocumentElement();
            JAXBElement<ValidateDocumentResponse> jaxbElement = unmarshaller.unmarshal(xmlNode, ValidateDocumentResponse.class);
            return jaxbElement.getValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected DataFilesReport getDataFilesReportFromDom(Document reportDom) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DataFilesReport.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Node xmlNode = reportDom.getDocumentElement();
            JAXBElement<DataFilesReport> jaxbElement = unmarshaller.unmarshal(xmlNode, DataFilesReport.class);
            return jaxbElement.getValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String currentDateTime(String timeZone, String timeFormat){
        final Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);

        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        return sdf.format(currentTime);
    }
}
