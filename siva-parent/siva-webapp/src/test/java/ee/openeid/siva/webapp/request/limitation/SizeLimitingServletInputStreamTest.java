/*
 * Copyright 2024 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.webapp.request.limitation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Stubber;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.IOException;
import java.util.function.IntFunction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class SizeLimitingServletInputStreamTest {

    @Mock
    private ServletInputStream servletInputStream;

    @Test
    void available_WhenWrappedInputStreamAvailableReturnsGivenValue_ReturnsTheValue() throws IOException {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(0L);
        doReturn(15).when(servletInputStream).available();

        int result = limitingServletInputStream.available();

        assertThat(result, equalTo(15));
        verify(servletInputStream).available();
        verifyNoMoreInteractions(servletInputStream);
    }

    @Test
    void available_WhenWrappedInputStreamAvailableThrowsIOException_ReThrowsTheException() throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(0L);
        IOException ioException = new IOException("Exception message");
        doThrow(ioException).when(servletInputStream).available();

        IOException caughtException = assertThrows(
                IOException.class,
                limitingServletInputStream::available
        );

        assertThat(caughtException, sameInstance(ioException));
        verify(servletInputStream).available();
        verifyNoMoreInteractions(servletInputStream);
    }

    @Test
    void close_WhenWrappedInputStreamCloseSucceeds_Succeeds() throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(0L);

        limitingServletInputStream.close();

        verify(servletInputStream).close();
        verifyNoMoreInteractions(servletInputStream);
    }

    @Test
    void close_WhenWrappedInputStreamCloseThrowsIOException_ReThrowsTheException() throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(0L);
        IOException ioException = new IOException("Exception message");
        doThrow(ioException).when(servletInputStream).close();

        IOException caughtException = assertThrows(
                IOException.class,
                limitingServletInputStream::close
        );

        assertThat(caughtException, sameInstance(ioException));
        verify(servletInputStream).close();
        verifyNoMoreInteractions(servletInputStream);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void idFinished_WhenWrappedInputStreamIsFinishedReturnsGivenValue_ReturnsTheValue(boolean value) {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(0L);
        doReturn(value).when(servletInputStream).isFinished();

        boolean result = limitingServletInputStream.isFinished();

        assertThat(result, equalTo(value));
        verify(servletInputStream).isFinished();
        verifyNoMoreInteractions(servletInputStream);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void idReady_WhenWrappedInputStreamIsReadyReturnsGivenValue_ReturnsTheValue(boolean value) {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(0L);
        doReturn(value).when(servletInputStream).isReady();

        boolean result = limitingServletInputStream.isReady();

        assertThat(result, equalTo(value));
        verify(servletInputStream).isReady();
        verifyNoMoreInteractions(servletInputStream);
    }

    @Test
    void mark_ThrowsUnsupportedOperationExceptionWithoutInteractingWithWrappedInputStream() {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(0L);

        UnsupportedOperationException caughtException = assertThrows(
                UnsupportedOperationException.class,
                () -> limitingServletInputStream.mark(1)
        );

        assertThat(caughtException.getMessage(), equalTo("Mark not supported"));
        verifyNoInteractions(servletInputStream);
    }

    @Test
    void markSupported_ReturnsFalseWithoutInteractingWithWrappedInputStream() {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(0L);

        boolean result = limitingServletInputStream.markSupported();

        assertThat(result, equalTo(false));
        verifyNoInteractions(servletInputStream);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void read_WhenReadIsCalledLessTimesThanOrAsManyTimesAsReadLimit_ReturnsReadBytes(int timesToRead) throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(3L);
        doReturnMultiple(timesToRead, i -> i).when(servletInputStream).read();

        for (int i = 0; i < timesToRead; ++i) {
            int result = limitingServletInputStream.read();

            assertThat(result, equalTo(i));
        }
        verify(servletInputStream, times(timesToRead)).read();
        verifyNoMoreInteractions(servletInputStream);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void read_WhenReadIsCalledMoreTimesThanReadLimit_ThrowsLimitExceededException(int limit) throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(limit);
        doReturnMultiple(limit + 1, i -> i).when(servletInputStream).read();

        for (int i = 0; i < limit; ++i) {
            int result = limitingServletInputStream.read();
            assertThat(result, equalTo(i));
        }

        RequestSizeLimitExceededException caughtException = assertThrows(
                RequestSizeLimitExceededException.class,
                limitingServletInputStream::read
        );

        assertThat(caughtException.getMessage(), equalTo(String.format(
                "Request body length exceeds request size limit (%d bytes)",
                limit
        )));
        verify(servletInputStream, times(limit + 1)).read();
        verifyNoMoreInteractions(servletInputStream);
    }

    @Test
    void read_WhenReadIsCalledMoreTimesThanReadLimitButStreamEndsBefore_ReturnsReadBytes() throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(1L);
        doReturn(7, -1).when(servletInputStream).read();

        int result1 = limitingServletInputStream.read();
        int result2 = limitingServletInputStream.read();

        assertThat(result1, equalTo(7));
        assertThat(result2, equalTo(-1));
        verify(servletInputStream, times(2)).read();
        verifyNoMoreInteractions(servletInputStream);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void read_WhenReadIsCalledWithArrayShorterThanOrAsLongAsReadLimit_ReturnsNumberOfBytesRead(
            int lengthToRead
    ) throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(3L);
        byte[] buffer = new byte[lengthToRead];
        doAnswerToBulkRead(lengthToRead, i -> (byte) i).when(servletInputStream).read(buffer, 0, lengthToRead);

        int result = limitingServletInputStream.read(buffer);

        assertThat(result, equalTo(lengthToRead));
        assertArrayEquals(generateByteArray(lengthToRead, i -> (byte) i), buffer);
        verify(servletInputStream).read(buffer, 0, lengthToRead);
        verifyNoMoreInteractions(servletInputStream);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void read_WhenReadIsCalledWithArrayLongerThanReadLimit_ThrowsLimitExceededException(int limit) throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(limit);
        byte[] buffer = new byte[limit + 1];
        doAnswerToBulkRead(buffer.length, i -> (byte) i).when(servletInputStream).read(buffer, 0, limit + 1);

        RequestSizeLimitExceededException caughtException = assertThrows(
                RequestSizeLimitExceededException.class,
                () -> limitingServletInputStream.read(buffer)
        );

        assertThat(caughtException.getMessage(), equalTo(String.format(
                "Request body length exceeds request size limit (%d bytes)",
                limit
        )));
        verify(servletInputStream).read(buffer, 0, limit + 1);
        verifyNoMoreInteractions(servletInputStream);
    }

    @Test
    void read_WhenReadIsCalledWithArrayLongerThanReadLimitButStreamEndsBefore_ReturnsNumberOfBytesRead() throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(1L);
        byte[] buffer = new byte[2];
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgument(0, byte[].class)[0] = 7;
            return 1;
        }).when(servletInputStream).read(buffer, 0, 2);

        int result = limitingServletInputStream.read(buffer);

        assertThat(result, equalTo(1));
        assertArrayEquals(new byte[] {7, 0}, buffer);
        verify(servletInputStream).read(buffer, 0, 2);
        verifyNoMoreInteractions(servletInputStream);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void read_WhenReadIsCalledWithLengthShorterThanOrAsLongAsReadLimit_ReturnsNumberOfBytesRead(
            int lengthToRead
    ) throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(3);
        byte[] buffer = new byte[lengthToRead + 2];
        doAnswerToBulkRead(lengthToRead, i -> (byte) i).when(servletInputStream).read(buffer, 1, lengthToRead);

        int result = limitingServletInputStream.read(buffer, 1, lengthToRead);

        assertThat(result, equalTo(lengthToRead));
        assertArrayEquals(generateByteArray(lengthToRead + 2, i -> (i < 1 || i > lengthToRead) ? 0 : (byte) (i - 1)), buffer);
        verify(servletInputStream).read(buffer, 1, lengthToRead);
        verifyNoMoreInteractions(servletInputStream);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void read_WhenReadIsCalledWithLengthLongerThanReadLimit_ThrowsLimitExceededException(int limit) throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(limit);
        byte[] buffer = new byte[limit + 2];
        doAnswerToBulkRead(buffer.length, i -> (byte) i).when(servletInputStream).read(buffer, 1, limit + 1);

        RequestSizeLimitExceededException caughtException = assertThrows(
                RequestSizeLimitExceededException.class,
                () -> limitingServletInputStream.read(buffer, 1, limit + 1)
        );

        assertThat(caughtException.getMessage(), equalTo(String.format(
                "Request body length exceeds request size limit (%d bytes)",
                limit
        )));
        verify(servletInputStream).read(buffer, 1, limit + 1);
        verifyNoMoreInteractions(servletInputStream);
    }

    @Test
    void read_WhenReadIsCalledWithLengthLongerThanReadLimitButStreamEndsBefore_ReturnsNumberOfBytesRead() throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(1L);
        byte[] buffer = new byte[3];
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgument(0, byte[].class)[1] = 7;
            return 1;
        }).when(servletInputStream).read(buffer, 1, 2);

        int result = limitingServletInputStream.read(buffer, 1, 2);

        assertThat(result, equalTo(1));
        assertArrayEquals(new byte[] {0, 7, 0}, buffer);
        verify(servletInputStream).read(buffer, 1, 2);
        verifyNoMoreInteractions(servletInputStream);
    }

    @Test
    void readAllBytes_WhenStreamLengthIs0_DelegatesToBulkReadAndReturnsEmptyByteArray() throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(0L);
        doReturn(-1).when(servletInputStream)
                .read(any(byte[].class), eq(0), eq(1));

        byte[] result = limitingServletInputStream.readAllBytes();

        assertArrayEquals(new byte[0], result);
        verify(servletInputStream).read(any(byte[].class), eq(0), eq(1));
        verifyNoMoreInteractions(servletInputStream);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void readAllBytes_WhenStreamLengthIsLessThanOrEqualToReadLimit_DelegatesToBulkReadAndReturnsEmptyByteArray(
            int streamLength
    ) throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(2L);
        doAnswerToBulkRead(streamLength, i -> (byte) i).when(servletInputStream)
                .read(any(byte[].class), eq(0), eq(3));
        doReturn(-1).when(servletInputStream)
                .read(any(byte[].class), eq(streamLength), eq(3 - streamLength));

        byte[] result = limitingServletInputStream.readAllBytes();

        assertArrayEquals(generateByteArray(streamLength, i -> (byte) i), result);
        verify(servletInputStream).read(any(byte[].class), eq(0), eq(3));
        verify(servletInputStream).read(any(byte[].class), eq(streamLength), eq(3 - streamLength));
        verifyNoMoreInteractions(servletInputStream);
    }

    @Test
    void readAllBytes_WhenStreamLengthIsMoreThanReadLimit_ThrowsLimitExceededException() throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(1L);
        doAnswerToBulkRead(2, i -> (byte) i).when(servletInputStream)
                .read(any(byte[].class), eq(0), eq(2));

        RequestSizeLimitExceededException caughtException = assertThrows(
                RequestSizeLimitExceededException.class,
                limitingServletInputStream::readAllBytes
        );

        assertThat(
                caughtException.getMessage(),
                equalTo("Request body length exceeds request size limit (1 bytes)")
        );
        verify(servletInputStream).read(any(byte[].class), eq(0), eq(2));
        verifyNoMoreInteractions(servletInputStream);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void readLine_WhenNewlineCharacterIsReachedBeforeReadLimit_DelegatesToReadAndReturnsNumberOfBytesRead(
            int bytesBeforeNewline
    ) throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(3L);
        doReturnMultiple(bytesBeforeNewline + 1, i -> (i < bytesBeforeNewline) ? i : '\n')
                .when(servletInputStream).read();
        byte[] buffer = new byte[bytesBeforeNewline + 1];

        int result = limitingServletInputStream.readLine(buffer, 0, buffer.length);

        assertThat(result, equalTo(bytesBeforeNewline + 1));
        assertArrayEquals(
                generateByteArray(bytesBeforeNewline + 1, i -> (i < bytesBeforeNewline) ? (byte) i : (byte) '\n'),
                buffer
        );
        verify(servletInputStream, times(bytesBeforeNewline + 1)).read();
        verifyNoMoreInteractions(servletInputStream);
    }

    @Test
    void readLine_WhenReadLimitIsReachedBeforeNewlineCharacter_ThrowsLimitExceededException() throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(0L);
        doReturn((int) '\n').when(servletInputStream).read();
        byte[] buffer = new byte[1];

        RequestSizeLimitExceededException caughtException = assertThrows(
                RequestSizeLimitExceededException.class,
                () -> limitingServletInputStream.readLine(buffer, 0, buffer.length)
        );

        assertThat(
                caughtException.getMessage(),
                equalTo("Request body length exceeds request size limit (0 bytes)")
        );
        verify(servletInputStream).read();
        verifyNoMoreInteractions(servletInputStream);
    }

    @Test
    void reset_ThrowsUnsupportedOperationExceptionWithoutInteractingWithWrappedInputStream() {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(0L);

        UnsupportedOperationException caughtException = assertThrows(
                UnsupportedOperationException.class,
                limitingServletInputStream::reset
        );

        assertThat(caughtException.getMessage(), equalTo("Mark not supported"));
        verifyNoInteractions(servletInputStream);
    }

    @Test
    void setReadListener_ThrowsUnsupportedOperationExceptionWithoutInteractingWithWrappedInputStream() {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(0L);
        ReadListener readListener = mock(ReadListener.class);

        UnsupportedOperationException caughtException = assertThrows(
                UnsupportedOperationException.class,
                () -> limitingServletInputStream.setReadListener(readListener)
        );

        assertThat(caughtException.getMessage(), equalTo("Setting read listener not supported"));
        verifyNoInteractions(servletInputStream);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void skip_WhenSkipIsCalledWithNumberLessThanOrEqualToReadLimit_ReturnsNumberOfBytesSkipped(
            int bytesToSkip
    ) throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(2L);
        doReturn(bytesToSkip).when(servletInputStream).read(any(byte[].class), eq(0), anyInt());

        long result = limitingServletInputStream.skip(bytesToSkip);

        assertThat(result, equalTo((long) bytesToSkip));
        verify(servletInputStream).read(any(byte[].class), eq(0), anyInt());
        verifyNoMoreInteractions(servletInputStream);
    }

    @Test
    void skip_WhenSkipIsCalledWithNumberGreaterThanReadLimit_ReturnsNumberOfBytesSkipped() throws Exception {
        SizeLimitingServletInputStream limitingServletInputStream = createLimitingInputStream(1L);
        doReturn(2).when(servletInputStream).read(any(byte[].class), eq(0), anyInt());

        RequestSizeLimitExceededException caughtException = assertThrows(
                RequestSizeLimitExceededException.class,
                () -> limitingServletInputStream.skip(2L)
        );

        assertThat(
                caughtException.getMessage(),
                equalTo("Request body length exceeds request size limit (1 bytes)")
        );
        verify(servletInputStream).read(any(byte[].class), eq(0), anyInt());
        verifyNoMoreInteractions(servletInputStream);
    }

    private SizeLimitingServletInputStream createLimitingInputStream(long limit) {
        return new SizeLimitingServletInputStream(servletInputStream, limit);
    }

    private static <T> Stubber doReturnMultiple(int count, IntFunction<T> elementResolver) {
        if (count > 1) {
            Object toBeReturned = elementResolver.apply(0);
            Object[] toBeReturnedNext = new Object[count - 1];
            for (int i = 0; i < toBeReturnedNext.length; ++i) {
                toBeReturnedNext[i] = elementResolver.apply(i + 1);
            }
            return doReturn(toBeReturned, toBeReturnedNext);
        } else if (count == 1) {
            return doReturn(elementResolver.apply(0));
        } else {
            throw new IllegalArgumentException("Invalid count: " + count);
        }
    }

    private static Stubber doAnswerToBulkRead(int maxLengthToRead, IntFunction<Byte> byteResolver) {
        return doAnswer(invocationOnMock -> {
            byte[] array = invocationOnMock.getArgument(0, byte[].class);
            int offset = invocationOnMock.getArgument(1, Integer.class);
            int length = invocationOnMock.getArgument(2, Integer.class);
            int bytesRead = Math.min(maxLengthToRead, length);
            for (int i = 0; i < bytesRead; ++i) {
                array[offset + i] = byteResolver.apply(i);
            }
            return bytesRead;
        });
    }

    private static byte[] generateByteArray(int length, IntFunction<Byte> byteResolver) {
        byte[] array = new byte[length];
        for (int i = 0; i < length; ++i) {
            array[i] = byteResolver.apply(i);
        }
        return array;
    }
}
