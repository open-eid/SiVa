package ee.openeid.siva.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.EncoderConfig.encoderConfig;

public abstract class SiVaRestTests extends SiVaIntegrationTestsBase {

    protected static final String DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE = "Document malformed or not matching documentType";
    protected static final String INVALID_DOCUMENT_TYPE = "Invalid document type";
    protected static final String INVALID_FILENAME = "Invalid filename";
    protected static final String MAY_NOT_BE_EMPTY = "may not be empty";
    protected static final String INVALID_BASE_64 = "Document is not encoded in a valid base64 string";
    protected static final String DOCUMENT_TYPE = "documentType";
    protected static final String FILENAME = "filename";
    protected static final String DOCUMENT = "document";
    protected static final String SIGNATURE_POLICY = "signaturePolicy";

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

    protected Response postWithXAuthUsrHeader(String request, String xAuthUser) {
        return given()
                .config(RestAssured.config().encoderConfig(encoderConfig().defaultContentCharset("UTF-8")))
                .header("x-authenticated-user", xAuthUser)
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

    protected QualifiedReport postForReport(String file, String signaturePolicy) {
        if (shouldPrintResponse()) {
            return postForReportAndPrintResponse(file, signaturePolicy);
        }
        return mapToReport(post(validationRequestFor(file, signaturePolicy)).andReturn().body().asString());
    }

    @Override
    protected QualifiedReport postForReport(String file) {
        return postForReport(file, VALID_SIGNATURE_POLICY_2);
    }

    protected QualifiedReport postForReportAndPrintResponse(String file, String signaturePolicy) {
        return mapToReport(post(validationRequestFor(file, signaturePolicy)).andReturn().body().prettyPrint());
    }

    protected String validationRequestFor(String file, String signaturePolicy) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", Base64.encodeBase64String(readFileFromTestResources(file)));
        jsonObject.put("filename", file);
        jsonObject.put("documentType", parseFileExtension(file));
        if (signaturePolicy != null) {
            jsonObject.put("signaturePolicy", signaturePolicy);
        }
        String output = jsonObject.toString();
        return output;
    }

    protected String validationRequestFor(String file) {
        return validationRequestFor(file, null);
    }

    protected String validationRequestForExtended(String documentKey, String encodedDocument,
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

    protected String validationRequestWithValidKeys(String encodedString, String filename, String documentType, String signaturePolicy) {
        return validationRequestForExtended(
            DOCUMENT, encodedString,
            FILENAME, filename,
            DOCUMENT_TYPE, documentType,
            SIGNATURE_POLICY, signaturePolicy);
    }

    protected QualifiedReport mapToReport(String json) {
        try {
            return new ObjectMapper().readValue(json, QualifiedReport.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}