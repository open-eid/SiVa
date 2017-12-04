/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.validation.document.builder;

import ee.openeid.siva.validation.document.ValidationDocument;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Use for testing purposes only
 */
@TestOnly
public final class DummyValidationDocumentBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DummyValidationDocumentBuilder.class);

    private ValidationDocument validationDocument;

    private DummyValidationDocumentBuilder() {
        validationDocument = new ValidationDocument();
    }

    public static DummyValidationDocumentBuilder aValidationDocument() {
        return new DummyValidationDocumentBuilder();
    }

    public DummyValidationDocumentBuilder withDocument(String filePath) {
        try {
            Path documentPath = Paths.get(getClass().getClassLoader().getResource(filePath).toURI());
            validationDocument.setBytes(Files.readAllBytes(documentPath));
        } catch (IOException e) {
            LOGGER.warn("FAiled to load dummy validation document with error: {}", e.getMessage(), e);
        } catch (URISyntaxException e) {
            LOGGER.warn("Dummy document URL is invalid and ended with error: {}", e.getMessage(), e);
        }

        return this;
    }

    public DummyValidationDocumentBuilder withName(String name) {
        validationDocument.setName(name);
        return this;
    }

    public ValidationDocument build() {
        return validationDocument;
    }
}
