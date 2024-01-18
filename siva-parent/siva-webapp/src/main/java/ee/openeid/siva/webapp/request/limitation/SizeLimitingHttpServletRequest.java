/*
 * Copyright 2024 Riigi Infosüsteemi Amet
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

import lombok.Getter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

@Getter
public class SizeLimitingHttpServletRequest extends HttpServletRequestWrapper {
    private final long maximumAllowedReadLimit;

    public SizeLimitingHttpServletRequest(HttpServletRequest request, long maximumAllowedReadLimit) {
        super(request);
        this.maximumAllowedReadLimit = maximumAllowedReadLimit;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ensureContentLengthDoesNotExceedLimit();

        return new SizeLimitingServletInputStream(super.getInputStream(), maximumAllowedReadLimit);
    }

    private void ensureContentLengthDoesNotExceedLimit() {
        long contentLength = getContentLengthLong();

        if (contentLength > maximumAllowedReadLimit) {
            throw new RequestSizeLimitExceededException(String.format(
                    "Request content-length (%d bytes) exceeds request size limit (%d bytes)",
                    contentLength, maximumAllowedReadLimit
            ));
        }
    }
}
