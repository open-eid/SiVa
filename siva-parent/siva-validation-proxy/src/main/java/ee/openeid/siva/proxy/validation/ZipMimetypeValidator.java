package ee.openeid.siva.proxy.validation;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.exception.ContainerMimetypeFileException;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.europa.esig.dss.asic.common.ASiCUtils.MIME_TYPE;

public class ZipMimetypeValidator {
    private static final int LOCAL_FILE_HEADER_SIGNATURE = 0x04034b50;
    private static final int FILE_NAME_LENGTH_OFFSET = 26;
    private static final int FILE_NAME_OFFSET = 30;
    private static final int COMPRESSION_METHOD_OFFSET = 8;
    private static final int EXTRA_FIELD_LENGTH_OFFSET = 28;
    private static final int UNCOMPRESSED_SIZE_OFFSET = 22;
    private static final int MIMETYPE_CONTENT_OFFSET = 38;
    private static final byte[] MIMETYPE_NAME_BYTES = MIME_TYPE.getBytes(StandardCharsets.US_ASCII);

    private final List<byte[]> allowedMimetypeBytes;
    private final String unexpectedMimetypeMessage;

    public ZipMimetypeValidator(String... allowedMimetypes) {
        allowedMimetypeBytes = Stream.of(allowedMimetypes)
                .distinct()
                .map(m -> m.getBytes(StandardCharsets.US_ASCII))
                .collect(Collectors.toList());
        unexpectedMimetypeMessage = Stream.of(allowedMimetypes)
                .distinct()
                .map(m -> '"' + m + '"')
                .collect(Collectors.joining(
                        ", ",
                        "Container should have one of the expected mimetypes: ",
                        ""
                ));
    }

    public void validateZipContainerMimetype(ProxyDocument proxyDocument) {
        byte[] documentBytes = proxyDocument.getBytes();

        if (ArrayUtils.getLength(documentBytes) < FILE_NAME_OFFSET) {
            return;
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(documentBytes).order(ByteOrder.LITTLE_ENDIAN);

        if (byteBuffer.getInt(0) != LOCAL_FILE_HEADER_SIGNATURE) {
            return;
        }

        validateMimetypeName(byteBuffer);
        validateMimetypeCompression(byteBuffer);
        validateMimetypeExtraFieldLength(byteBuffer);
        validateMimetypeContent(byteBuffer);
    }

     private static void validateMimetypeName(ByteBuffer byteBuffer) {
         int fileNameLength = byteBuffer.getShort(FILE_NAME_LENGTH_OFFSET);

         if (fileNameLength == MIMETYPE_NAME_BYTES.length) {
             int fileNameOffset = byteBuffer.arrayOffset() + FILE_NAME_OFFSET;
             byte[] byteBufferArray = byteBuffer.array();

             if (byteBufferArray.length >= fileNameOffset + fileNameLength && Arrays.equals(
                     byteBufferArray, fileNameOffset, fileNameOffset + fileNameLength,
                     MIMETYPE_NAME_BYTES, 0, MIMETYPE_NAME_BYTES.length
             )) {
                 return;
             }
         }

         throw new ContainerMimetypeFileException("\"mimetype\" should be the first file in the container");
     }

     private static void validateMimetypeCompression(ByteBuffer byteBuffer) {
         if (byteBuffer.getShort(COMPRESSION_METHOD_OFFSET) != 0) {
             throw new ContainerMimetypeFileException("Container \"mimetype\" file must not be compressed");
         }
     }

    private static void validateMimetypeExtraFieldLength(ByteBuffer byteBuffer) {
        if (byteBuffer.getShort(EXTRA_FIELD_LENGTH_OFFSET) != 0) {
            throw new ContainerMimetypeFileException("Container \"mimetype\" file must not contain \"Extra fields\" in its ZIP header");
        }
    }

     private void validateMimetypeContent(ByteBuffer byteBuffer) {
         int fileContentSize = byteBuffer.getInt(UNCOMPRESSED_SIZE_OFFSET);
         int fileContentOffset = byteBuffer.arrayOffset() + MIMETYPE_CONTENT_OFFSET;
         byte[] byteBufferArray = byteBuffer.array();

         if (byteBufferArray.length >= fileContentOffset + fileContentSize) {
             for (byte[] expectedBytes : allowedMimetypeBytes) {
                 if (fileContentSize == expectedBytes.length && Arrays.equals(
                         byteBufferArray, fileContentOffset, fileContentOffset + fileContentSize,
                         expectedBytes, 0, expectedBytes.length
                 )) {
                     return;
                 }
             }
         }

         throw new ContainerMimetypeFileException(unexpectedMimetypeMessage);
     }
}
