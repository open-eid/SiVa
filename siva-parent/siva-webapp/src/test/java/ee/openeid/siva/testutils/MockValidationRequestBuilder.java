/*
 * Copyright 2019 - 2023 Riigi Infosüsteemi Amet
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
    public static final String DEFAULT_REPORT_TYPE = "Simple";
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

    public MockValidationRequestBuilder withReportType(String reportType) {
        this.validationRequest.reportType = reportType;
        return this;
    }

    public MockValidationRequestBuilder withSignaturePolicy(String policy) {
        this.validationRequest.signaturePolicy = policy;
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
        private String reportType = DEFAULT_REPORT_TYPE;

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

        @Override
        public String getReportType() {
            return reportType;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setReportType(String reportType) {
            this.reportType = reportType;
        }
    }
}
