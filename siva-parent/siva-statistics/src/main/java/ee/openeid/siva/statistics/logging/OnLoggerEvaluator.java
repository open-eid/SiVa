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

package ee.openeid.siva.statistics.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OnLoggerEvaluator extends EventEvaluatorBase<ILoggingEvent> {

    List<String> loggerList = new ArrayList<>();

    public void addLogger(String loggerStr) {
        loggerList.add(loggerStr);
    }

    @Override
    public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
        final String eventLoggerName = event.getLoggerName();

        return StringUtils.isNotBlank(eventLoggerName) && loggerList.contains(eventLoggerName);
    }

}
