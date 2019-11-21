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

package ee.openeid.siva.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.webapp.request.Datafile;
import ee.openeid.siva.webapp.request.SignatureFile;
import ee.openeid.siva.webapp.response.ValidationResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;

public abstract class SiVaRestTests extends SiVaIntegrationTestsBase {



    private static final String VALIDATION_ENDPOINT = "/validate";
    private static final String HASHCODE_VALIDATION_ENDPOINT = "/validateHashcode";
    private static final String DATA_FILES_ENDPOINT = "/getDataFiles";
    private static final String MONITORING_ENDPOINT = "/monitoring/health";
    private static final boolean PRINT_RESPONSE = false;

    protected Response post(String request) {
        return given()
                .config(RestAssured.config().encoderConfig(encoderConfig().defaultContentCharset("UTF-8")))
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(createUrl(VALIDATION_ENDPOINT));
    }

    protected Response postWithXAuthUsrHeader(String request, String xAuthUser) {
        return given()
                .config(RestAssured.config().encoderConfig(encoderConfig().defaultContentCharset("UTF-8")))
                .header("x-authenticated-user", xAuthUser)
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(createUrl(VALIDATION_ENDPOINT));
    }

    protected Response postForDataFiles(String request) {
        return given()
                .config(RestAssured.config().encoderConfig(encoderConfig().defaultContentCharset("UTF-8")))
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(createUrl(DATA_FILES_ENDPOINT));
    }

    protected Response postHashcodeValidation(String request) {
        return given()
                .config(RestAssured.config().encoderConfig(encoderConfig().defaultContentCharset("UTF-8")))
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(createUrl(HASHCODE_VALIDATION_ENDPOINT));
    }

    protected Response getMonitoring() {
        return given()
                .config(RestAssured.config().encoderConfig(encoderConfig().defaultContentCharset("UTF-8")))
                .when()
                .get(createUrl(MONITORING_ENDPOINT));
    }

    /**
     * Override to enable/disable printing the response per class
     *
     * @return
     */
    protected boolean shouldPrintResponse() {
        return PRINT_RESPONSE;
    }

    protected SimpleReport postForReport(String file, String signaturePolicy) {
        if (shouldPrintResponse()) {
            return postForReportAndPrintResponse(file, signaturePolicy);
        }
        return mapToReport(post(validationRequestFor(file, signaturePolicy, null)).andReturn().body().asString()).getValidationReport();
    }

    protected SimpleReport postForReport(String file) {
        return postForReport(file, VALID_SIGNATURE_POLICY_4);
    }

    protected SimpleReport postForReportAndPrintResponse(String file, String signaturePolicy) {
        return mapToReport(post(validationRequestFor(file, signaturePolicy, null)).andReturn().body().prettyPrint()).getValidationReport();
    }

    protected String validationRequestFor(String file, String signaturePolicy, String reportType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", Base64.encodeBase64String(readFileFromTestResources(file)));
        jsonObject.put("filename", file);
        if (signaturePolicy != null) {
            jsonObject.put("signaturePolicy", signaturePolicy);
        }
        if (reportType != null) {
            jsonObject.put("reportType", reportType);
        }
        return jsonObject.toString();
    }

    protected String validationRequestForDD4j(String file, String signaturePolicy, String reportType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, Base64.encodeBase64String(readFileFromTestResources(file)));
        String basename = FilenameUtils.getBaseName(file);
        jsonObject.put(FILENAME, basename + ".bdoc");
        if (signaturePolicy != null) {
            jsonObject.put(SIGNATURE_POLICY, signaturePolicy);
        }
        if (reportType != null) {
            jsonObject.put(REPORT_TYPE, reportType);
        }
        return jsonObject.toString();
    }

    protected String validationRequestForDSS(String file, String signaturePolicy, String reportType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, Base64.encodeBase64String(readFileFromTestResources(file)));
        String basename = FilenameUtils.getBaseName(file);
        jsonObject.put(FILENAME, basename + ".asice");
        if (signaturePolicy != null) {
            jsonObject.put(SIGNATURE_POLICY, signaturePolicy);
        }
        if (reportType != null) {
            jsonObject.put(REPORT_TYPE, reportType);
        }
        return jsonObject.toString();
    }

    protected String validationRequestFor(String file) {
        return validationRequestFor(file, null, null);
    }

    protected String dataFilesRequest(String file) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", Base64.encodeBase64String(readFileFromTestResources(file)));
        jsonObject.put("filename", file);
        return jsonObject.toString();
    }

    protected String dataFilesRequestInvalidValues(String document, String filename) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", document);
        jsonObject.put("filename", filename);
        return jsonObject.toString();
    }

    protected String dataFilesRequestExtended(String file, String filename) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", Base64.encodeBase64String(readFileFromTestResources(file)));
        jsonObject.put("filename", filename);
        return jsonObject.toString();
    }

    protected String validationRequestHashcode(String signature, String signaturePolicy, String reportType, String dataFile, String hashAlgo, String hash) {

        JSONObject jsonObject = new JSONObject();

        Datafile dataFileObject = new Datafile();
        dataFileObject.setHash(hash);
        dataFileObject.setHashAlgo(hashAlgo);
        dataFileObject.setFilename(dataFile);

        SignatureFile signatureFile = new SignatureFile();
        signatureFile.setSignature(Base64.encodeBase64String(readFileFromTestResources(signature)));
        signatureFile.setDatafiles(Collections.singletonList(dataFileObject));

        jsonObject.put(SIGNATURE_FILES, Collections.singletonList(signatureFile));
        jsonObject.put(SIGNATURE_POLICY, signaturePolicy);
        jsonObject.put(REPORT_TYPE, reportType);

        return jsonObject.toString();
    }

    protected String validationRequestHashcodeSimple(String signature, String signaturePolicy, String reportType) {

        JSONObject jsonObject = new JSONObject();

        SignatureFile signatureFile = new SignatureFile();
        signatureFile.setSignature(Base64.encodeBase64String(readFileFromTestResources(signature)));

        jsonObject.put(SIGNATURE_FILES, Collections.singletonList(signatureFile));
        jsonObject.put(SIGNATURE_POLICY, signaturePolicy);
        jsonObject.put(REPORT_TYPE, reportType);

        return jsonObject.toString();
    }

    protected String validationRequestHashcodeSimpleMultipleFiles(List<String> signatureFiles, String signaturePolicy, String reportType) {

        JSONObject jsonObject = new JSONObject();
        List<SignatureFile> signatureFileList = new ArrayList<>();
        for (String signature : signatureFiles) {
            SignatureFile signatureFile = new SignatureFile();
            signatureFile.setSignature(Base64.encodeBase64String(readFileFromTestResources(signature)));
            signatureFileList.add(signatureFile);
        }
        jsonObject.put(SIGNATURE_FILES, signatureFileList);
        jsonObject.put(SIGNATURE_POLICY, signaturePolicy);
        jsonObject.put(REPORT_TYPE, reportType);

        return jsonObject.toString();
    }

    protected String validationRequestHashcodeMultipleFiles(List<String> signatureFiles, String signaturePolicy, String reportType) throws ParserConfigurationException, IOException, SAXException {
        return validationRequestHashcodeMultipleFilesReturnsObject(signatureFiles, signaturePolicy, reportType).toString();
    }

    protected JSONObject validationRequestHashcodeMultipleFilesReturnsObject(List<String> signatureFiles, String signaturePolicy, String reportType) throws ParserConfigurationException, IOException, SAXException {
        JSONObject jsonObject = new JSONObject();
        List<SignatureFile> signatureFileList = new ArrayList<>();
        for (String signature : signatureFiles) {

            SignatureFile signatureFile = new SignatureFile();
            signatureFile.setSignature(Base64.encodeBase64String(readFileFromTestResources(signature)));
            String testFilesBase = getProjectBaseDirectory() + "src/test/resources/" + getTestFilesDirectory() + signature;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(testFilesBase));
            document.getDocumentElement().normalize();
            NodeList nList = document.getElementsByTagName("ds:Reference");

            List<Datafile> datafiles = new ArrayList<>();

            for (int k = 0; k < nList.getLength() - 1; k++) {
                Datafile dataFileObject = new Datafile();
                dataFileObject.setHash(nList.item(k).getChildNodes().item(1).getFirstChild().getNodeValue());
                String algorithm = nList.item(k).getChildNodes().item(0).getAttributes().getNamedItem("Algorithm").getNodeValue();
                dataFileObject.setHashAlgo(algorithm.substring(algorithm.lastIndexOf("#") + 1));
                dataFileObject.setFilename(nList.item(k).getAttributes().getNamedItem("URI").getNodeValue());
                datafiles.add(dataFileObject);
            }
            signatureFile.setDatafiles(datafiles);

            signatureFileList.add(signatureFile);
        }

        jsonObject.put(SIGNATURE_POLICY, signaturePolicy);
        jsonObject.put(REPORT_TYPE, reportType);
        jsonObject.put(SIGNATURE_FILES, signatureFileList);
        return jsonObject;
    }

    protected String validationRequestForExtended(String documentKey, String encodedDocument,
                                                  String filenameKey, String file,
                                                  String signaturePolicyKey, String signaturePolicy) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(documentKey, encodedDocument);
        jsonObject.put(filenameKey, file);
        jsonObject.put(signaturePolicyKey, signaturePolicy);
        return jsonObject.toString();
    }

    protected String validationRequestDocumentTypeExtended(String documentKey, String encodedDocument,
                                                           String filenameKey, String file,
                                                           String documentTypeKey, String documentType,
                                                           String signaturePolicyKey, String signaturePolicy) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(documentKey, encodedDocument);
        jsonObject.put(filenameKey, file);
        jsonObject.put(documentTypeKey, documentType);
        jsonObject.put(signaturePolicyKey, signaturePolicy);
        return jsonObject.toString();
    }

    protected String validationRequestWithValidKeys(String encodedString, String filename, String signaturePolicy) {
        return validationRequestForExtended(
                DOCUMENT, encodedString,
                FILENAME, filename,
                SIGNATURE_POLICY, signaturePolicy);
    }

    protected String validationRequestWithDocumentTypeValidKeys(String encodedString, String filename, String documentType, String signaturePolicy) {
        return validationRequestDocumentTypeExtended(
                DOCUMENT, encodedString,
                FILENAME, filename,
                DOCUMENT_TYPE, documentType,
                SIGNATURE_POLICY, signaturePolicy);
    }

    protected ValidationResponse mapToReport(String json) {
        try {
            return new ObjectMapper().readValue(json, ValidationResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String currentDateTime(String timeZone, String timeFormat) {
        final Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);

        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        return sdf.format(currentTime);
    }
}
