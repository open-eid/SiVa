/*
 * Copyright 2023 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.validation.helper;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TestLog {

    private final Appender<ILoggingEvent> mockedAppender;

    public TestLog(Class<?> loggerClass) {
        this(loggerClass.getCanonicalName());
    }

    @SuppressWarnings("unchecked")
    public TestLog(String loggerName) {
        mockedAppender = (Appender<ILoggingEvent>) Mockito.mock(Appender.class);
        Logger logger = (Logger) LoggerFactory.getLogger(loggerName);
        logger.addAppender(mockedAppender);
    }

    public void verifyLogInOrder(Matcher<?>... matchers) {
        verifyLogInOrder(logEvent -> true, matchers);
    }

    @SuppressWarnings("unchecked")
    public void verifyLogInOrder(Predicate<ILoggingEvent> logEventFilter, Matcher<?>... matchers) {
        ArgumentCaptor<ILoggingEvent> argumentCaptor = ArgumentCaptor.forClass(ILoggingEvent.class);
        Mockito.verify(mockedAppender, Mockito.atLeast(1)).doAppend(argumentCaptor.capture());
        List<String> listOfMessages = argumentCaptor.getAllValues().stream().filter(logEventFilter).map(ILoggingEvent::getFormattedMessage).collect(Collectors.toList());
        // NB: Make sure the correct overload of IsIterableContainingInOrder.contains is called, otherwise the matchers are wrapped twice and matching won't work!
        MatcherAssert.assertThat(listOfMessages, IsIterableContainingInOrder.contains((Matcher[]) matchers));
    }

    public void verifyLogEmpty() {
        Mockito.verifyNoInteractions(mockedAppender);
    }

    @SuppressWarnings("unchecked")
    public void reset() {
        Mockito.reset(mockedAppender);
    }

}
