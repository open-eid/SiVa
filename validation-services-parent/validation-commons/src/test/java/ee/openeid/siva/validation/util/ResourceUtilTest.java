/*
 * Copyright 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.validation.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ResourceUtilTest {

    @ParameterizedTest
    @MethodSource("existingResources")
    void loadResourceTo_WhenResourceExists_InputStreamContainsExpectedBytes(Resource resource) {
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        Consumer<InputStream> consumer = mockInputStreamConsumer(sink);

        ResourceUtil.loadResourceTo(resource, consumer);

        verify(consumer).accept(any(InputStream.class));
        verifyNoMoreInteractions(consumer);
        assertThat(
                sink.toByteArray(),
                is("This is a test file.".getBytes(StandardCharsets.UTF_8))
        );
    }

    @ParameterizedTest
    @MethodSource("inexistentResources")
    void loadResourceTo_WhenResourceDoesNotExist_ThrowsIllegalStateException(Resource resource) {
        @SuppressWarnings("unchecked")
        Consumer<InputStream> consumer = (Consumer<InputStream>) mock(Consumer.class);

        IllegalStateException caughtException = assertThrows(
                IllegalStateException.class,
                () -> ResourceUtil.loadResourceTo(resource, consumer)
        );

        verifyNoInteractions(consumer);
        assertThat(
                caughtException.getMessage(),
                equalTo("Failed to open " + resource.toString())
        );
    }

    static Stream<Resource> existingResources() throws Exception {
        return Stream.of(
                new ClassPathResource("/test-files/test.txt", ResourceUtilTest.class.getClassLoader()),
                new FileUrlResource("src/test/resources/test-files/test.txt")
        );
    }

    static Stream<Resource> inexistentResources() throws Exception {
        return Stream.of(
                new ClassPathResource("/no-such-directory/no-such-file.txt", ResourceUtilTest.class.getClassLoader()),
                new FileUrlResource("src/test/resources/no-such-directory/no-such-file.txt")
        );
    }

    @SuppressWarnings("unchecked")
    private static Consumer<InputStream> mockInputStreamConsumer(ByteArrayOutputStream sink) {
        final Consumer<InputStream> inputStreamConsumer = (Consumer<InputStream>) mock(Consumer.class);
        doAnswer(invocationOnMock -> {
            InputStream inputStream = invocationOnMock.getArgument(0, InputStream.class);
            inputStream.transferTo(sink);
            return null;
        }).when(inputStreamConsumer).accept(any(InputStream.class));
        return inputStreamConsumer;
    }

}
