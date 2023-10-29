/*
 * Copyright 2017 - 2023 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.webapp.request.DataFilesRequest;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MockDataFilesRequestBuilder {

    public static final String DEFAULT_FILENAME = "test.ddoc";
    public static final String DEFAULT_DOCUMENT = "ABC";

    private MockDataFilesRequestBuilder.MockDataFilesRequest dataFilesRequest;

    public static MockDataFilesRequestBuilder aDataFilesRequest() {
        MockDataFilesRequestBuilder builder = new MockDataFilesRequestBuilder();
        builder.dataFilesRequest = new MockDataFilesRequestBuilder.MockDataFilesRequest();
        return builder;
    }

    public MockDataFilesRequestBuilder withType(String filename) {
        this.dataFilesRequest.filename = filename;
        return this;
    }

    public MockDataFilesRequestBuilder withDocument(String document) {
        this.dataFilesRequest.document = document;
        return this;
    }

    public MockDataFilesRequestBuilder withDocument(Path documentPath) throws IOException {
        this.dataFilesRequest.document = Base64.encodeBase64String(Files.readAllBytes(documentPath));
        return this;
    }

    public MockDataFilesRequestBuilder.MockDataFilesRequest build() {
        return dataFilesRequest;
    }

    public static class MockDataFilesRequest implements DataFilesRequest {

        private String document = DEFAULT_DOCUMENT;
        private String filename = DEFAULT_FILENAME;

        @Override
        public String getDocument() {
            return document;
        }

        @Override
        public String getFilename() {
            return filename;
        }

        public void setDocument(String document) {
            this.document = document;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

    }

}
