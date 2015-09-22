package ee.sk.pdf.validator.monitoring.logging;

import org.slf4j.Logger;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;


@Service
public class LoggingService {
    private static final Locale defaultLocale = Locale.ENGLISH;
    private String logMessage;
    private Object[] params;

    @Autowired
    private MessageSource messageSource;

    public void logInfo(Logger logger, String messageSourceKey, Object... params) {
        this.params = params;
        logger.info(getMessage(messageSourceKey), params);
    }

    public void logWarning(Logger logger, String messageSourceKey, Object... params) {
        this.params = params;
        logger.warn(getMessage(messageSourceKey), params);
    }

    public void logError(Logger logger, String messageSourceKey, Object... params) {
        this.params = params;
        logger.error(getMessage(messageSourceKey), params);
    }

    public String getLastLoggedMessage() {
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(logMessage, params);
        return formattingTuple.getMessage();
    }

    private String getMessage(String messageKey) {
        logMessage = messageSource.getMessage(messageKey, null, defaultLocale);
        return logMessage;
    }
}
