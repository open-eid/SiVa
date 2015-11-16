package ee.sk.pdf.validator.monitoring.message;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;


@Service
public class MessageService {
    private static final Locale defaultLocale = Locale.ENGLISH;
    private String logMessage;
    private Object[] params;
    private MessageSource messageSource;

    public String getLastLoggedMessage() {
        return formatMessage();
    }

    public String getMessage(String messageKey, Object... params) {
        this.params = params;
        logMessage = messageSource.getMessage(messageKey, null, defaultLocale);

        return formatMessage();
    }

    private String formatMessage() {
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(logMessage, params);
        return formattingTuple.getMessage();
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
