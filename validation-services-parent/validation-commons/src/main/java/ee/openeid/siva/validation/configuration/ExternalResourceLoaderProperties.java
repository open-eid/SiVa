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
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.util.List;

@Getter
@Setter
public abstract class ExternalResourceLoaderProperties {

    private static final String DURATION_MIN_MESSAGE = "must be at least 1 ms";
    private static final String DURATION_MAX_MESSAGE = "must be at most " + Integer.MAX_VALUE + " ms";

    @NotNull
    private List<@NotNull Protocol> acceptedProtocols = List.of(Protocol.HTTP, Protocol.HTTPS);

    @NotNull
    @DurationMin(millis = 1, message = "connect-timeout " + DURATION_MIN_MESSAGE)
    @DurationMax(millis = Integer.MAX_VALUE, message = "connect-timeout " + DURATION_MAX_MESSAGE)
    private Duration connectTimeout = Duration.ofSeconds(5);

    @NotNull
    @DurationMin(millis = 1, message = "read-timeout " + DURATION_MIN_MESSAGE)
    @DurationMax(millis = Integer.MAX_VALUE, message = "read-timeout " + DURATION_MAX_MESSAGE)
    private Duration readTimeout = Duration.ofSeconds(10);

    @NotNull
    private DataSize maxResponseSize;

    protected ExternalResourceLoaderProperties(DataSize defaultMaxResponseSize) {
        this.maxResponseSize = defaultMaxResponseSize;
    }

    @AssertTrue(message = "max-response-size must be at least 1 B and at most " + Integer.MAX_VALUE + " B")
    public boolean isMaxResponseSizeInRange() {
        return maxResponseSize == null || (maxResponseSize.toBytes() > 0 && maxResponseSize.toBytes() <= Integer.MAX_VALUE);
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
