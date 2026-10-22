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

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLException;

// Why a list had to be served from its last copy, as the health view reports it: the kind of failure, read from the
// exception's cause chain. The exception message itself (host names, proxies, the TLS alert) stays in the log line
// written where the copy is served.
public enum LastCopyReason {

    /** The host could not be resolved or the connection refused or routed. */
    UNREACHABLE,
    /** Connecting or reading took longer than the client allows. */
    TIMED_OUT,
    /** The TLS handshake failed: a certificate the trust store does not cover, a name mismatch, a protocol. */
    TLS_FAILED,
    /** Anything else: an HTTP error status, an empty response, a malformed URL. */
    DOWNLOAD_FAILED;

    public static LastCopyReason of(Throwable failure) {
        for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
            if (cause instanceof SSLException) {
                return TLS_FAILED;
            }
            if (cause instanceof InterruptedIOException) {
                return TIMED_OUT;
            }
            if (cause instanceof UnknownHostException || cause instanceof ConnectException
                    || cause instanceof NoRouteToHostException) {
                return UNREACHABLE;
            }
        }
        return DOWNLOAD_FAILED;
    }
}
