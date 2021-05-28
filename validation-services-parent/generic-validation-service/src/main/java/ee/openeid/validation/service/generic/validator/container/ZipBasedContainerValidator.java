/*
 * Copyright 2021 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.validator.container;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * An implementation of {@link ContainerValidator} for parsing and validating individual entries of a ZIP-based container.
 *
 * The container passed into the validator in {@code validationDocument} is parsed using {@link ZipInputStream}, all ZIP
 * entries are read sequentially, and a pair of {@link ZipEntry} and {@link InputStream} representing each such entry
 * is passed into {@code entryValidator} for further validation processing.
 */
@RequiredArgsConstructor
public class ZipBasedContainerValidator implements ContainerValidator {

    /**
     * A validator that is invoked for each individual entry parsed from the ZIP-based container.
     */
    @FunctionalInterface
    public interface EntryValidator {
        /**
         * Performs the validation process of a specific entry.
         * <b>NB:</b> {@code entryInputStream} passed into this method <b>must not</b> be closed inside this method!
         *
         * @param entry an instance of {@link ZipEntry} that represents the container entry to validate
         * @param entryInputStream an instance of {@link InputStream} where this entry's bytes can be read from
         * @throws IOException if an error occurs while reading the input stream of this entry
         */
        void validate(@NonNull ZipEntry entry, @NonNull InputStream entryInputStream) throws IOException;
    }

    @NonNull
    private final ValidationDocument validationDocument;
    @NonNull
    private final EntryValidator entryValidator;

    /**
     * Performs the validation process of the entire container.
     *
     * @throws NullPointerException if {@code validationDocument} container no document container bytes
     * @throws MalformedDocumentException if an {@link IOException} is thrown while parsing the container
     */
    @Override
    public void validate() {
        byte[] documentBytes = Objects.requireNonNull(validationDocument.getBytes(), "No document bytes available");
        try (
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(documentBytes);
                ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);
        ) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                entryValidator.validate(zipEntry, zipInputStream);
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            throw new MalformedDocumentException(e);
        }
    }

}
