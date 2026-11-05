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

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

// One INFO line per finished siva.* observation under the logger ee.openeid.siva.observation: the stage name, its
// duration, every key-value and the error type if the stage failed, inline in the message for the text output
// and as the nested member "observation" for the structured formats. With the request id in the MDC, these lines
// and the libraries' lines between them are a request's record. Boot's other observations (http.server.requests,
// the probes and scrapes with them) are left alone. Silence it with logging.level.ee.openeid.siva.observation=OFF.
@Slf4j(topic = LoggingObservationHandler.LOGGER_NAME)
public class LoggingObservationHandler implements ObservationHandler<Observation.Context> {

    public static final String LOGGER_NAME = "ee.openeid.siva.observation";
    static final String NAME_PREFIX = "siva.";

    private static final String STARTED_AT = LoggingObservationHandler.class.getName() + ".startedAt";

    @Override
    public boolean supportsContext(Observation.Context context) {
        return context.getName() != null && context.getName().startsWith(NAME_PREFIX);
    }

    @Override
    public void onStart(Observation.Context context) {
        context.put(STARTED_AT, System.nanoTime());
    }

    @Override
    public void onStop(Observation.Context context) {
        if (!log.isInfoEnabled()) {
            return;
        }
        Long startedAt = context.get(STARTED_AT);
        long durationMillis = startedAt == null ? 0 : (System.nanoTime() - startedAt) / 1_000_000;

        Map<String, Object> record = new LinkedHashMap<>();
        record.put("name", context.getName());
        record.put("duration_ms", durationMillis);
        StringBuilder message = new StringBuilder(context.getName()).append(' ').append(durationMillis).append("ms");
        context.getAllKeyValues().forEach(keyValue -> {
            record.put(keyValue.getKey(), keyValue.getValue());
            message.append(' ').append(keyValue.getKey()).append('=').append(textForm(keyValue.getValue()));
        });
        Throwable error = context.getError();
        if (error != null) {
            record.put("error", error.getClass().getName());
            message.append(" error=").append(error.getClass().getSimpleName());
        }
        log.atInfo().addKeyValue("observation", record).log(message.toString());
    }

    // A value with a space in it is quoted, so the text line still splits on spaces; the structured record
    // carries the value as it is.
    private static String textForm(String value) {
        return value != null && value.contains(" ") ? '"' + value + '"' : value;
    }

}
