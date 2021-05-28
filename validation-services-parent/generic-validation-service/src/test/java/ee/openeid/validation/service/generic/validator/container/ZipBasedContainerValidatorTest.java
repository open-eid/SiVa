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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ExtendWith(MockitoExtension.class)
public class ZipBasedContainerValidatorTest {

    @Mock
    private ValidationDocument validationDocument;
    @Mock
    private ZipBasedContainerValidator.EntryValidator entryValidator;
    @InjectMocks
    private ZipBasedContainerValidator validator;

    @Test
    public void testValidationThrowsOnMissingValidationDocumentBytes() {
        Mockito.doReturn(null).when(validationDocument).getBytes();

        NullPointerException caughtException = Assertions.assertThrows(
                NullPointerException.class,
                () -> validator.validate()
        );

        Assertions.assertEquals("No document bytes available", caughtException.getMessage());
        Mockito.verifyNoMoreInteractions(validationDocument, entryValidator);
    }

    @Test
    public void testValidationDoesNotInvokeEntryValidatorOnEmptyContainer() {
        Mockito.doReturn(new byte[0]).when(validationDocument).getBytes();

        validator.validate();

        Mockito.verify(validationDocument).getBytes();
        Mockito.verifyNoMoreInteractions(validationDocument, entryValidator);
    }

    @Test
    public void testValidationThrowsMalformedDocumentExceptionOnIOException() throws IOException {
        Pair<String, byte[]> containerEntry = Pair.of("entry", new byte[0]);
        byte[] containerBytes = createZipFile(containerEntry);
        Mockito.doReturn(containerBytes).when(validationDocument).getBytes();
        IOException entryValidatorIOException = new IOException("Something went wrong");
        Mockito.doThrow(entryValidatorIOException).when(entryValidator)
                .validate(Mockito.any(ZipEntry.class), Mockito.any(InputStream.class));

        MalformedDocumentException caughtException = Assertions.assertThrows(
                MalformedDocumentException.class,
                () -> validator.validate()
        );

        Assertions.assertNotNull(caughtException.getMessage());
        Assertions.assertSame(entryValidatorIOException, caughtException.getCause());
        Mockito.verify(validationDocument).getBytes();
        assertEntryValidationsInvokedFor(containerEntry.getKey());
        Mockito.verifyNoMoreInteractions(validationDocument, entryValidator);
    }

    @ParameterizedTest
    @MethodSource("entryLists")
    public void testValidationInvokesEntryValidatorForEachEntry(List<Pair<String, byte[]>> entries) throws IOException {
        byte[] containerBytes = createZipFile(entries);
        Mockito.doReturn(containerBytes).when(validationDocument).getBytes();
        final List<byte[]> caughtEntryBytes = new ArrayList<>();
        Mockito.doAnswer(invocationOnMock -> {
            InputStream inputStream = invocationOnMock.getArgument(1, InputStream.class);
            caughtEntryBytes.add(IOUtils.toByteArray(inputStream));
            return null;
        }).when(entryValidator).validate(Mockito.any(ZipEntry.class), Mockito.any(InputStream.class));

        validator.validate();

        Mockito.verify(validationDocument).getBytes();
        assertEntryValidationsInvokedFor(entries.stream().map(Pair::getKey).toArray(String[]::new));
        Mockito.verifyNoMoreInteractions(validationDocument, entryValidator);
        for (int i = 0; i < entries.size(); ++i) {
            Assertions.assertArrayEquals(entries.get(i).getValue(), caughtEntryBytes.get(i));
        }
    }

    private static Stream<List<Pair<String, byte[]>>> entryLists() {
        return Stream.of(
                List.of(
                        Pair.of("entry", "entry-bytes".getBytes(StandardCharsets.UTF_8))
                ),
                List.of(
                        Pair.of("entry1", "entry1-bytes".getBytes(StandardCharsets.UTF_8)),
                        Pair.of("entry2", "entry2-bytes".getBytes(StandardCharsets.UTF_8)),
                        Pair.of("empty", new byte[0])
                )
        );
    }

    private void assertEntryValidationsInvokedFor(String... entries) throws IOException {
        ArgumentCaptor<ZipEntry> zipEntryArgumentCaptor = ArgumentCaptor.forClass(ZipEntry.class);
        Mockito.verify(entryValidator, Mockito.times(entries.length)).validate(zipEntryArgumentCaptor.capture(), Mockito.any(InputStream.class));
        List<ZipEntry> caughtEntries = zipEntryArgumentCaptor.getAllValues();

        Assertions.assertNotNull(caughtEntries);
        Assertions.assertEquals(entries.length, caughtEntries.size());
        for (int i = 0; i < entries.length; ++i) {
            ZipEntry zipEntry = caughtEntries.get(i);
            Assertions.assertNotNull(zipEntry);
            Assertions.assertEquals(entries[i], zipEntry.getName());
        }
    }

    @SafeVarargs
    private static byte[] createZipFile(Pair<String, byte[]>... entries) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (Pair<String, byte[]> entry : entries) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());

                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(entry.getValue());
                zipOutputStream.closeEntry();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write ZIP stream");
        }
        return byteArrayOutputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private static byte[] createZipFile(List<Pair<String, byte[]>> entries) {
        return createZipFile(entries.toArray(new Pair[0]));
    }

}
