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

import eu.europa.esig.dss.spi.exception.DSSExternalResourceException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.stream.Stream;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class LastCopyReasonTest {

    @ParameterizedTest
    @MethodSource("failures")
    void ofReadsTheKindOfFailureFromTheCauseChain(Throwable failure, LastCopyReason expected) {
        assertThat(LastCopyReason.of(failure), is(expected));
    }

    static Stream<Arguments> failures() {
        return Stream.of(
                arguments(download(new UnknownHostException("tl.example")), LastCopyReason.UNREACHABLE),
                arguments(download(new ConnectException("Connection refused")), LastCopyReason.UNREACHABLE),
                arguments(download(new NoRouteToHostException("No route to host")), LastCopyReason.UNREACHABLE),
                arguments(download(new SocketTimeoutException("Read timed out")), LastCopyReason.TIMED_OUT),
                arguments(download(new SSLHandshakeException("certificate_unknown")), LastCopyReason.TLS_FAILED),
                // The outermost cause decides: a handshake that timed out is still a handshake that failed.
                arguments(download(new SSLException("handshake", new SocketTimeoutException())),
                        LastCopyReason.TLS_FAILED),
                arguments(download(new IOException("Not acceptable HTTP Status (HTTP status code : 503)")),
                        LastCopyReason.DOWNLOAD_FAILED),
                arguments(new DSSExternalResourceException("Cannot retrieve data from url. Empty content is obtained!"),
                        LastCopyReason.DOWNLOAD_FAILED),
                arguments(new DSSExternalResourceException("Invalid URI", new IllegalArgumentException("uri")),
                        LastCopyReason.DOWNLOAD_FAILED));
    }

    // What DSS's data loader throws for a failed GET: its own exception around the client's.
    private static DSSExternalResourceException download(Throwable cause) {
        return new DSSExternalResourceException("Unable to process GET call for url [https://tl.example/EE.xml]. "
                + "Reason : [" + cause.getMessage() + "]", cause);
    }
}
