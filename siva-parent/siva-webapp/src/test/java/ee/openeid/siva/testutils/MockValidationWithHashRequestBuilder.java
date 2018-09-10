package ee.openeid.siva.testutils;

import ee.openeid.siva.webapp.request.Datafile;
import ee.openeid.siva.webapp.request.ValidationWithHashRequest;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MockValidationWithHashRequestBuilder {

    public static final String DEFAULT_FILENAME = "signatures0.xml";
    public static final String DEFAULT_DOCUMENT = "ABC";
    public static final String DEFAULT_SIGNATURE_POLICY = null;
    public static final String DEFAULT_REPORT_TYPE = "Simple";

    private MockValidationWithHashRequest validationWithHashRequest;

    public static MockValidationWithHashRequestBuilder aValidationWithHashRequest() {
        MockValidationWithHashRequestBuilder builder = new MockValidationWithHashRequestBuilder();
        builder.validationWithHashRequest = new MockValidationWithHashRequestBuilder.MockValidationWithHashRequest();
        return builder;
    }

    public MockValidationWithHashRequestBuilder withFilename(String filename) {
        this.validationWithHashRequest.filename = filename;
        return this;
    }

    public MockValidationWithHashRequestBuilder withReportType(String reportType) {
        this.validationWithHashRequest.reportType = reportType;
        return this;
    }

    public MockValidationWithHashRequestBuilder withSignaturePolicy(String policy) {
        this.validationWithHashRequest.signaturePolicy = policy;
        return this;
    }

    public MockValidationWithHashRequestBuilder withSignature(String signature) {
        this.validationWithHashRequest.signature = signature;
        return this;
    }

    public MockValidationWithHashRequestBuilder withSignature(Path signaturePath) throws IOException {
        this.validationWithHashRequest.signature = Base64.encodeBase64String(Files.readAllBytes(signaturePath));
        return this;
    }

    public MockValidationWithHashRequestBuilder.MockValidationWithHashRequest build() {
        return validationWithHashRequest;
    }

    public static class MockValidationWithHashRequest implements ValidationWithHashRequest {

        private String signature = DEFAULT_DOCUMENT;
        private String filename = DEFAULT_FILENAME;
        private String signaturePolicy = DEFAULT_SIGNATURE_POLICY;
        private String reportType = DEFAULT_REPORT_TYPE;
        private List<Datafile> datafiles = new ArrayList<>();

        @Override
        public String getSignature() {
            return signature;
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public String getSignaturePolicy() {
            return signaturePolicy;
        }

        @Override
        public String getReportType() {
            return reportType;
        }

        @Override
        public List<Datafile> getDatafiles() {
            return datafiles;
        }
    }
}
