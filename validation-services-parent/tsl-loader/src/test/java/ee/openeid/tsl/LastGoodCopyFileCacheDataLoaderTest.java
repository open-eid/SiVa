/*
 * Copyright 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.tsl;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.exception.DSSExternalResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LastGoodCopyFileCacheDataLoaderTest {

    private static final String URL = "https://tl.example/EE.xml";

    @TempDir
    private Path cache;
    @Mock
    private DataLoader dataLoader;

    private LastGoodCopyFileCacheDataLoader loader;

    @BeforeEach
    void setUp() {
        loader = new LastGoodCopyFileCacheDataLoader();
        loader.setFileCacheDirectory(cache.toFile());
        loader.setCacheExpirationTime(0);
        loader.setDataLoader(dataLoader);
    }

    @Test
    void getDocument_WhenDownloadSucceeds_FreshContentIsCachedAndReturned() {
        when(dataLoader.get(URL)).thenReturn("fresh".getBytes(StandardCharsets.UTF_8));

        DSSDocument document = loader.getDocument(URL);

        assertThat(content(document), is("fresh"));
        assertThat(cachedContent(), is("fresh"));
    }

    @Test
    void getDocument_WhenDownloadFailsAndACopyIsCached_TheCopyIsReturned() throws IOException {
        Files.writeString(cache.resolve(DSSUtils.getNormalizedString(URL)), "stale");
        when(dataLoader.get(URL)).thenThrow(new DSSExternalResourceException("unreachable"));

        DSSDocument document = loader.getDocument(URL);

        assertThat(content(document), is("stale"));
    }

    @Test
    void getDocument_WhenTheCopyIsServed_ItIsRecordedWithTheKindOfFailureAndTheFileIsLeftAsItWas() throws IOException {
        Path file = cache.resolve(DSSUtils.getNormalizedString(URL));
        Files.writeString(file, "stale");
        FileTime downloaded = FileTime.from(Instant.parse("2026-10-20T06:00:00Z"));
        Files.setLastModifiedTime(file, downloaded);
        when(dataLoader.get(URL)).thenThrow(new DSSExternalResourceException("Unable to process GET call for url",
                new UnknownHostException("tl.example")));
        assertThat(loader.getLastCopies(), is(Map.of()));

        loader.getDocument(URL);

        assertThat(loader.getLastCopies(), is(Map.of(URL, "UNREACHABLE")));
        assertThat(Files.getLastModifiedTime(file), is(downloaded));
    }

    @Test
    void resetLastCopies_ForgetsTheCopiesServed() throws IOException {
        Files.writeString(cache.resolve(DSSUtils.getNormalizedString(URL)), "stale");
        when(dataLoader.get(URL)).thenThrow(new DSSExternalResourceException("unreachable"));
        loader.getDocument(URL);

        loader.resetLastCopies();

        assertThat(loader.getLastCopies(), is(Map.of()));
    }

    @Test
    void getDocument_WhenDownloadFailsAndNothingIsCached_TheFailureIsPropagated() {
        when(dataLoader.get(URL)).thenThrow(new DSSExternalResourceException("unreachable"));

        assertThrows(DSSExternalResourceException.class, () -> loader.getDocument(URL));
    }

    private String cachedContent() {
        try {
            return Files.readString(cache.resolve(DSSUtils.getNormalizedString(URL)));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String content(DSSDocument document) {
        return new String(DSSUtils.toByteArray(document), StandardCharsets.UTF_8);
    }
}
