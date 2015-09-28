package ee.sk.pdf.validator.monitoring.template;

import com.google.common.base.Strings;
import com.google.common.io.Resources;
import ee.sk.pdf.validator.monitoring.logging.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

@Component
public class RequestTemplateLoader implements TemplateLoader {
    private final static Logger LOGGER = LoggerFactory.getLogger(RequestTemplateLoader.class);

    private final static String RESPONSE_BODY_TEMPLATE_KEY = "{{messageBody}}";
    private final static String EMPTY_STRING = "";
    private String templateLocation;

    @Autowired
    private LoggingService loggingService;

    @Override
    public String parsedTemplate(CharSequence replaceWith) {
        try {
            return getResponseBodyTemplate().replace(RESPONSE_BODY_TEMPLATE_KEY, replaceWith);
        } catch (URISyntaxException | IOException e) {
            loggingService.logWarning(LOGGER, "monitoring.xmlTemplateLoadingFailed", e.getMessage());
        }

        return EMPTY_STRING;
    }

    private String getResponseBodyTemplate() throws URISyntaxException, IOException {
        if (Strings.isNullOrEmpty(templateLocation)) {
            loggingService.logWarning(LOGGER, "monitoring.pdfLocationError");
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
