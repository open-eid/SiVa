package ee.openeid.siva.integrationtest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.Warning;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.EncoderConfig.encoderConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class SiVaRestTests extends SiVaIntegrationTestsBase {

    protected static final String DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE = "document malformed or not matching documentType";
    protected static final String INVALID_DOCUMENT_TYPE = "invalid document type";
    protected static final String INVALID_REPORT_TYPE = "invalid report type";
    protected static final String INVALID_FILENAME = "invalid filename";
    protected static final String MAY_NOT_BE_EMPTY = "may not be empty";
    protected static final String INVALID_BASE_64 = "not valid base64 encoded string";
    protected static final String DOCUMENT_TYPE = "documentType";
    protected static final String REPORT_TYPE = "reportType";
    protected static final String FILENAME = "filename";
    protected static final String DOCUMENT = "document";

    private static final String VALIDATION_ENDPOINT = "/validate";
    private static final boolean PRINT_RESPONSE = false;

    protected Response post(String request) {
        return given()
                .config(RestAssured.config().encoderConfig(encoderConfig().defaultContentCharset("UTF-8")))
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(VALIDATION_ENDPOINT);
    }

    /**
     * Override to enable/disable printing the response per class
     * @return
     */
    protected boolean shouldPrintResponse() {
        return PRINT_RESPONSE;
    }

    protected QualifiedReport postForReport(String file) {
        if (shouldPrintResponse()) {
            return postForReportAndPrintResponse(file);
        }
        return mapToReport(post(validationRequestFor(file, "simple")).andReturn().body().asString());
    }

    protected QualifiedReport postForReportAndPrintResponse(String file) {
        return mapToReport(post(validationRequestFor(file, "simple")).andReturn().body().prettyPrint());
    }

    protected String validationRequestFor(String file, String reportType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", Base64.encodeBase64String(readFileFromTestResources(file)));
        jsonObject.put("filename", file);
        jsonObject.put("documentType", parseFileExtension(file));
        jsonObject.put("reportType", reportType);
        return jsonObject.toString();
    }

    protected String validationRequestForExtended(String documentKey, String encodedDocument,
                                                  String filenameKey, String file,
                                                  String documentTypeKey, String documentType,
                                                  String reportTypeKey, String reportType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(documentKey, encodedDocument);
        jsonObject.put(filenameKey, file);
        jsonObject.put(documentTypeKey, documentType);
        jsonObject.put(reportTypeKey, reportType);
        return jsonObject.toString();
    }

    protected String validationRequestWithValidKeys(String encodedString, String filename, String documentType, String reportType) {
        return validationRequestForExtended(
                DOCUMENT, encodedString,
                FILENAME, filename,
                DOCUMENT_TYPE, documentType,
                REPORT_TYPE, reportType
        );
    }

    private QualifiedReport mapToReport(String json) {
        try {
            return new ObjectMapper().readValue(json, QualifiedReport.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
