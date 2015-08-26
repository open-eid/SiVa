package ee.sk.pdf.validator.monitoring.template;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import ee.sk.pdf.validator.monitoring.logging.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        URI templateURI = this.getClass().getResource(templateLocation).toURI();

        LOGGER.info("Trying to load request XML template from path: " + templateURI);
        Path templatePath = Paths.get(templateURI);
        return Joiner.on("").join(Files.readAllLines(templatePath, Charset.defaultCharset()));
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }
}
