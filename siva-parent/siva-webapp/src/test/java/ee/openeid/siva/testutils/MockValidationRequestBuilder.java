package ee.openeid.siva.testutils;

import ee.openeid.siva.webapp.request.ValidationRequest;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MockValidationRequestBuilder {

    public static final String DEFAULT_FILENAME = "filename.pdf";
    public static final String DEFAULT_TYPE = "pdf";
    public static final String DEFAULT_DOCUMENT = "ABC";
    public static final String DEFAULT_SIGNATURE_POLICY = null;

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

    public MockValidationRequestBuilder withDocument(String document) {
        this.validationRequest.document = document;
        return this;
    }

    public MockValidationRequestBuilder withDocument(Path documentPath) throws IOException {
        this.validationRequest.document = Base64.encodeBase64String(Files.readAllBytes(documentPath));
        return this;
    }

    public MockValidationRequest build() {
        return validationRequest;
    }

    public static class MockValidationRequest implements ValidationRequest {

        private String document = DEFAULT_DOCUMENT;
        private String filename = DEFAULT_FILENAME;
        private String type = DEFAULT_TYPE;
        private String signaturePolicy = DEFAULT_SIGNATURE_POLICY;

        @Override
        public String getDocument() {
            return document;
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
        public String getSignaturePolicy() {
            return signaturePolicy;
        }

        public void setDocument(String document) {
            this.document = document;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public void setType(String type) {
            this.type = type;
        }

    }
}
