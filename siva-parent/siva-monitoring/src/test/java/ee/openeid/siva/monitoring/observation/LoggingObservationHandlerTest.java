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

package ee.openeid.siva.monitoring.observation;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.event.KeyValuePair;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoggingObservationHandlerTest {

    private final ObservationRegistry registry = ObservationRegistry.create();
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();
    private Logger logger;

    @BeforeEach
    void setUp() {
        registry.observationConfig().observationHandler(new LoggingObservationHandler());
        logger = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(LoggingObservationHandler.LOGGER_NAME);
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
    }

    @Test
    void finishedStageIsOneInfoLineWithDurationAndKeyValues() {
        Observation.createNotStarted("siva.validation.service", registry)
                .lowCardinalityKeyValue("validator", "generic")
                .highCardinalityKeyValue("tsl.anchors", "1187")
                .observe(() -> "done");

        assertThat(appender.list, hasSize(1));
        ILoggingEvent event = appender.list.get(0);
        assertThat(event.getLevel(), equalTo(Level.INFO));
        assertThat(event.getFormattedMessage(),
                matchesPattern("siva\\.validation\\.service \\d+ms tsl\\.anchors=1187 validator=generic"));
        Map<?, ?> record = observationRecord(event);
        assertThat(record.get("name"), equalTo("siva.validation.service"));
        assertThat((Long) record.get("duration_ms"), greaterThanOrEqualTo(0L));
        assertThat(record.get("validator"), equalTo("generic"));
        assertThat(record.get("tsl.anchors"), equalTo("1187"));
        assertThat(record.containsKey("error"), equalTo(false));
    }

    @Test
    void failedStageNamesTheErrorType() {
        Observation observation = Observation.createNotStarted("siva.validation", registry)
                .lowCardinalityKeyValue("request", "container");

        assertThrows(IllegalStateException.class, () -> observation.observe(() -> {
            throw new IllegalStateException("boom");
        }));

        assertThat(appender.list, hasSize(1));
        ILoggingEvent event = appender.list.get(0);
        assertThat(event.getFormattedMessage(),
                matchesPattern("siva\\.validation \\d+ms request=container error=IllegalStateException"));
        assertThat(observationRecord(event).get("error"), equalTo(IllegalStateException.class.getName()));
    }

    @Test
    void valueWithASpaceIsQuotedInTheTextLineOnly() {
        Observation.createNotStarted("siva.validation", registry)
                .highCardinalityKeyValue("policy", "POL v3")
                .observe(() -> "done");

        ILoggingEvent event = appender.list.get(0);
        assertThat(event.getFormattedMessage(), matchesPattern("siva\\.validation \\d+ms policy=\"POL v3\""));
        assertThat(observationRecord(event).get("policy"), equalTo("POL v3"));
    }

    @Test
    void observationsOfOtherNamesAreNotLogged() {
        Observation.createNotStarted("http.server.requests", registry).observe(() -> "scraped");
        Observation.createNotStarted("spring.security.filterchains", registry).observe(() -> "filtered");

        assertThat(appender.list, empty());
    }

    private static Map<?, ?> observationRecord(ILoggingEvent event) {
        assertThat(event.getKeyValuePairs(), hasSize(1));
        KeyValuePair pair = event.getKeyValuePairs().get(0);
        assertThat(pair.key, equalTo("observation"));
        assertThat(pair.value, instanceOf(Map.class));
        return (Map<?, ?>) pair.value;
    }

}
