package ee.sk.pdf.validator.monitoring.template;

import com.google.common.base.Strings;
import com.google.common.io.Resources;
import ee.sk.pdf.validator.monitoring.message.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

@Component
public class RequestTemplateLoader implements TemplateLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTemplateLoader.class);

    private static final String RESPONSE_BODY_TEMPLATE_KEY = "{{messageBody}}";
    private static final String EMPTY_STRING = "";
    private String templateLocation;

    @Autowired
    private MessageService messageService;

    @Override
    public String parsedTemplate(CharSequence replaceWith) {
        try {
            return getResponseBodyTemplate().replace(RESPONSE_BODY_TEMPLATE_KEY, replaceWith);
        } catch (IOException e) {
            LOGGER.warn(messageService.getMessage("monitoring.xmlTemplateLoadingFailed", e.getMessage()), e);
        }

        return EMPTY_STRING;
    }

    private String getResponseBodyTemplate() throws IOException {
        if (Strings.isNullOrEmpty(templateLocation)) {
            LOGGER.warn(messageService.getMessage("monitoring.pdfLocationError"));
            throw new FileNotFoundException("Invalid PDF file location");
        }

        URL templateURI = getClass().getResource(templateLocation);
        LOGGER.info("Trying to load request XML template from path: " + templateURI);

        return Resources.toString(templateURI, Charset.defaultCharset());
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }
}
