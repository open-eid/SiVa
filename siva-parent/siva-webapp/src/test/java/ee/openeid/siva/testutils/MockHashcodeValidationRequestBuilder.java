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

package ee.openeid.siva.testutils;

import ee.openeid.siva.webapp.request.Datafile;
import ee.openeid.siva.webapp.request.HashcodeValidationRequest;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MockHashcodeValidationRequestBuilder {

    public static final String DEFAULT_FILENAME = "signatures0.xml";
    public static final String DEFAULT_DOCUMENT = "ABC";
    public static final String DEFAULT_SIGNATURE_POLICY = null;
    public static final String DEFAULT_REPORT_TYPE = "Simple";

    private MockHashcodeValidationRequest validationRequest;

    public static MockHashcodeValidationRequestBuilder aHashcodeValidationRequest() {
        MockHashcodeValidationRequestBuilder builder = new MockHashcodeValidationRequestBuilder();
        builder.validationRequest = new MockHashcodeValidationRequest();
        return builder;
    }

    public MockHashcodeValidationRequestBuilder withFilename(String filename) {
        this.validationRequest.filename = filename;
        return this;
    }

    public MockHashcodeValidationRequestBuilder withReportType(String reportType) {
        this.validationRequest.reportType = reportType;
        return this;
    }

    public MockHashcodeValidationRequestBuilder withSignaturePolicy(String policy) {
        this.validationRequest.signaturePolicy = policy;
        return this;
    }

    public MockHashcodeValidationRequestBuilder withSignature(String signature) {
        this.validationRequest.signatureFile = signature;
        return this;
    }

    public MockHashcodeValidationRequestBuilder withSignature(Path signaturePath) throws IOException {
        this.validationRequest.signatureFile = Base64.encodeBase64String(Files.readAllBytes(signaturePath));
        return this;
    }

    public MockHashcodeValidationRequestBuilder withDataFile(Datafile dataFile) {
        this.validationRequest.datafiles.add(dataFile);
        return this;
    }

    public MockHashcodeValidationRequest build() {
        return validationRequest;
    }

    public static class MockHashcodeValidationRequest implements HashcodeValidationRequest {

        private String signatureFile = DEFAULT_DOCUMENT;
        private String filename = DEFAULT_FILENAME;
        private String signaturePolicy = DEFAULT_SIGNATURE_POLICY;
        private String reportType = DEFAULT_REPORT_TYPE;
        private List<Datafile> datafiles = new ArrayList<>();

        @Override
        public String getSignatureFile() {
            return signatureFile;
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
