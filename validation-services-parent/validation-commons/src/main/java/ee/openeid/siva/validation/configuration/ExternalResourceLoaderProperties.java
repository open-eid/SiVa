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

package ee.openeid.siva.validation.configuration;

import eu.europa.esig.dss.spi.client.http.Protocol;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.util.List;

@Getter
@Setter
public abstract class ExternalResourceLoaderProperties {

    @NotNull
    private List<@NotNull Protocol> acceptedProtocols = List.of(Protocol.HTTP, Protocol.HTTPS);
    @NotNull
    private Duration connectTimeout = Duration.ofSeconds(5);
    @NotNull
    private Duration readTimeout = Duration.ofSeconds(10);
    @NotNull
    private DataSize maxResponseSize;

    protected ExternalResourceLoaderProperties(DataSize defaultMaxResponseSize) {
        this.maxResponseSize = defaultMaxResponseSize;
    }

    @AssertTrue(message = "connect-timeout, read-timeout and max-response-size must be positive and at most "
            + Integer.MAX_VALUE + " (milliseconds or bytes)")
    public boolean isInRange() {
        return (connectTimeout == null || isInIntRange(connectTimeout.toMillis()))
                && (readTimeout == null || isInIntRange(readTimeout.toMillis()))
                && (maxResponseSize == null || isInIntRange(maxResponseSize.toBytes()));
    }

    private static boolean isInIntRange(long value) {
        return value > 0 && value <= Integer.MAX_VALUE;
    }

    public int connectTimeoutMillis() {
        return Math.toIntExact(connectTimeout.toMillis());
    }

    public int readTimeoutMillis() {
        return Math.toIntExact(readTimeout.toMillis());
    }

    public int maxResponseSizeBytes() {
        return Math.toIntExact(maxResponseSize.toBytes());
    }
}
