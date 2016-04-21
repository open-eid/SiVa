package ee.openeid.siva.testutils;

import ee.openeid.siva.webapp.request.ValidationRequest;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MockValidationRequestBuilder {

    public static final String DEFAULT_FILENAME = "filename.pdf";
    public static final String DEFAULT_TYPE = "pdf";
    public static final String DEFAULT_REPORT = "simple";
    public static final String DEFAULT_DOCUMENT = "ABC";

    private MockValidationRequest validationRequest;

    public static MockValidationRequestBuilder aValidationRequest() {
        MockValidationRequestBuilder builder = new MockValidationRequestBuilder();
        builder.validationRequest = new MockValidationRequest();
        return builder;
    }

    public MockValidationRequestBuilder withFilename(String filename) {
        this.validationRequest.filename = filename;
        return this;
    }

    public MockValidationRequestBuilder withType(String type) {
        this.validationRequest.type = type;
        return this;
    }

    public MockValidationRequestBuilder withReportType(String reportType) {
        this.validationRequest.reportType = reportType;
        return this;
    }

    public MockValidationRequestBuilder withDocument(String document) {
        this.validationRequest.base64Document = document;
        return this;
    }

    public MockValidationRequestBuilder withDocument(Path documentPath) throws IOException {
        this.validationRequest.base64Document = Base64.encodeBase64String(Files.readAllBytes(documentPath));
        return this;
    }

    public ValidationRequest build() {
        return validationRequest;
    }

    private static class MockValidationRequest implements ValidationRequest {

        private String base64Document = DEFAULT_DOCUMENT;
        private String filename = DEFAULT_FILENAME;
        private String type = DEFAULT_TYPE;
        private String reportType = DEFAULT_REPORT;

        @Override
        public String getBase64Document() {
            return base64Document;
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public String getDocumentType() {
            return type;
        }

        @Override
        public String getReportType() {
            return reportType;
        }
    }
}
