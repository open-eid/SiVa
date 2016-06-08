package ee.openeid.siva.sample.siva;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

class TestLoggingAppender extends AppenderBase<ILoggingEvent> {
    private final List<ILoggingEvent> log = new ArrayList<>();

    public List<ILoggingEvent> getLog() {
        return new ArrayList<>(log);
    }

    @Override
    protected void append(final ILoggingEvent iLoggingEvent) {
        log.add(iLoggingEvent);
    }
}
