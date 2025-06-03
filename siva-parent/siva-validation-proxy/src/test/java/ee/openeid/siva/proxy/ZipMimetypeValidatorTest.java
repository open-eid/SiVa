/*
 * Copyright 2023 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.exception.ContainerMimetypeFileException;
import ee.openeid.siva.proxy.validation.ZipMimetypeValidator;
import eu.europa.esig.dss.asic.common.ASiCUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ZipMimetypeValidatorTest {

    @Test
    void validMimetypeOfSingleExpectedValueShouldNotThrowException() {
        ZipMimetypeValidator zipMimetypeValidator = new ZipMimetypeValidator("valid-mimetype");
        ProxyDocument proxyDocument = proxyDocumentBuilder()
                .fileContent("valid-mimetype")
                .build();

        zipMimetypeValidator.validateZipContainerMimetype(proxyDocument);
    }

    @Test
    void validMimetypeOfMultipleExpectedValuesShouldNotThrowException() {
        ZipMimetypeValidator zipMimetypeValidator = new ZipMimetypeValidator("valid-mimetype1", "valid-mimetype2");
        ProxyDocument proxyDocument = proxyDocumentBuilder()
                .fileContent("valid-mimetype2")
                .build();

        zipMimetypeValidator.validateZipContainerMimetype(proxyDocument);
    }

    @Test
    void invalidMimetypeLengthThrowsException() {
        ZipMimetypeValidator zipMimetypeValidator = new ZipMimetypeValidator("valid-mimetype");
        ProxyDocument proxyDocument = proxyDocumentBuilder()
                .fileName("mimetyp")
                .fileContent("valid-mimetype")
                .build();

        ContainerMimetypeFileException caughtException = assertThrows(
                ContainerMimetypeFileException.class, () -> zipMimetypeValidator.validateZipContainerMimetype(proxyDocument)
        );
        assertEquals("\"mimetype\" should be the first file in the container", caughtException.getMessage());
    }

    @Test
    void invalidMimetypeFileNameThrowsException() {
        ZipMimetypeValidator zipMimetypeValidator = new ZipMimetypeValidator("valid-mimetype");
        ProxyDocument proxyDocument = proxyDocumentBuilder()
                .fileName("mimetipe")
                .fileContent("valid-mimetype")
                .build();

        ContainerMimetypeFileException caughtException = assertThrows(
                ContainerMimetypeFileException.class, () -> zipMimetypeValidator.validateZipContainerMimetype(proxyDocument)
        );
        assertEquals("\"mimetype\" should be the first file in the container", caughtException.getMessage());
    }

    @Test
    void invalidMimetypeCompressionTypeThrowsException() {
        ZipMimetypeValidator zipMimetypeValidator = new ZipMimetypeValidator("valid-mimetype");
        ProxyDocument proxyDocument = proxyDocumentBuilder()
                .compressed(true)
                .fileContent("valid-mimetype")
                .build();

        ContainerMimetypeFileException caughtException = assertThrows(
                ContainerMimetypeFileException.class, () -> zipMimetypeValidator.validateZipContainerMimetype(proxyDocument)
        );
        assertEquals("Container \"mimetype\" file must not be compressed", caughtException.getMessage());
    }

    @Test
    void invalidMimetypeExtraFieldLengthThrowsException() {
        short invalidExtraFieldLength = 1;
        ZipMimetypeValidator zipMimetypeValidator = new ZipMimetypeValidator("valid-mimetype");
        ProxyDocument proxyDocument = proxyDocumentBuilder()
                .fileContent("valid-mimetype")
                .extraFieldLength(invalidExtraFieldLength)
                .build();

        ContainerMimetypeFileException caughtException = assertThrows(
                ContainerMimetypeFileException.class, () -> zipMimetypeValidator.validateZipContainerMimetype(proxyDocument)
        );
        assertEquals("Container \"mimetype\" file must not contain \"Extra fields\" in its ZIP header", caughtException.getMessage());
    }

    @Test
    void invalidMimetypeUncompressedSizeThrowsException() {
        int invalidUncompressedSize = 20;
        ZipMimetypeValidator zipMimetypeValidator = new ZipMimetypeValidator("valid-mimetype");
        ProxyDocument proxyDocument = proxyDocumentBuilder()
                .uncompressedSize(invalidUncompressedSize)
                .fileContent("valid-mimetype")
                .build();

        ContainerMimetypeFileException caughtException = assertThrows(
                ContainerMimetypeFileException.class, () -> zipMimetypeValidator.validateZipContainerMimetype(proxyDocument)
        );
        assertEquals(
                "Container should have one of the expected mimetypes: \"valid-mimetype\"", caughtException.getMessage()
        );
    }

    @Test
    void invalidMimetypeFileContentThrowsException() {
        ZipMimetypeValidator zipMimetypeValidator = new ZipMimetypeValidator("valid-mimetype");
        ProxyDocument proxyDocument = proxyDocumentBuilder()
                .fileContent("invalid-mimetype")
                .build();

        ContainerMimetypeFileException caughtException = assertThrows(
                ContainerMimetypeFileException.class, () -> zipMimetypeValidator.validateZipContainerMimetype(proxyDocument)
        );
        assertEquals(
                "Container should have one of the expected mimetypes: \"valid-mimetype\"", caughtException.getMessage()
        );
    }

    @Builder(
            access = AccessLevel.PRIVATE,
            builderClassName = "ProxyDocumentBuilder",
            builderMethodName = "proxyDocumentBuilder"
    )
    private static ProxyDocument createProxyDocument(
            Integer localFileHeaderSignature,
            boolean compressed,
            Integer uncompressedSize,
            String fileName,
            @NonNull String fileContent,
            short extraFieldLength
    ) {
        if (fileName == null) {
            fileName = ASiCUtils.MIME_TYPE;
        }

        byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
        byte[] fileContentBytes = fileContent.getBytes(StandardCharsets.UTF_8);

        byte[] documentBytes = new byte[30 + fileNameBytes.length + fileContentBytes.length];
        ByteBuffer byteBuffer = ByteBuffer.wrap(documentBytes).order(ByteOrder.LITTLE_ENDIAN);

        byteBuffer.putInt(0, localFileHeaderSignature != null ? localFileHeaderSignature : 0x04034b50);
        byteBuffer.putShort(8, (short) (compressed ? 8 : 0));
        byteBuffer.putInt(18, fileContentBytes.length);
        byteBuffer.putInt(22, uncompressedSize != null ? uncompressedSize : fileContentBytes.length);
        byteBuffer.putShort(26, (short) fileNameBytes.length);
        byteBuffer.putShort(28, extraFieldLength);

        System.arraycopy(fileNameBytes, 0, documentBytes, 30, fileNameBytes.length);
        System.arraycopy(fileContentBytes, 0, documentBytes, 30 + fileNameBytes.length, fileContentBytes.length);

        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setBytes(documentBytes);
        return proxyDocument;
    }
}
