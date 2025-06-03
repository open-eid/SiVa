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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class SizeLimitingServletInputStream extends ServletInputStream {

    private final @NonNull ServletInputStream servletInputStream;
    private final long maximumAllowedReadLimit;

    private long bytesRead;

    @Override
    public int available() throws IOException {
        return servletInputStream.available();
    }

    @Override
    public void close() throws IOException {
        servletInputStream.close();
    }

    @Override
    public boolean isFinished() {
        return servletInputStream.isFinished();
    }

    @Override
    public boolean isReady() {
        return servletInputStream.isReady();
    }

    @Override
    public synchronized void mark(int readlimit) {
        throw new UnsupportedOperationException("Mark not supported");
    }

    @Override
    public void setReadListener(ReadListener listener) {
        throw new UnsupportedOperationException("Setting read listener not supported");
    }

    @Override
    public int read() throws IOException {
        final int result = servletInputStream.read();

        if (result >= 0) {
            ++bytesRead;
            ensureLimitNotExceeded();
        }

        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        final int result = servletInputStream.read(b, off, calculateMaximumBytesToRead(len));

        if (result >= 0) {
            bytesRead += result;
            ensureLimitNotExceeded();
        }

        return result;
    }

    @Override
    public synchronized void reset() {
        throw new UnsupportedOperationException("Mark not supported");
    }

    private int calculateMaximumBytesToRead(int requestedBytesToRead) {
        final long remainingAllowedReadLength = maximumAllowedReadLimit - bytesRead;
        return remainingAllowedReadLength < requestedBytesToRead
                ? (int) (remainingAllowedReadLength + 1)
                : requestedBytesToRead;
    }

    private void ensureLimitNotExceeded() {
        if (bytesRead > maximumAllowedReadLimit) {
            throw new RequestSizeLimitExceededException(String.format(
                    "Request body length exceeds request size limit (%d bytes)",
                    maximumAllowedReadLimit
            ));
        }
    }
}
