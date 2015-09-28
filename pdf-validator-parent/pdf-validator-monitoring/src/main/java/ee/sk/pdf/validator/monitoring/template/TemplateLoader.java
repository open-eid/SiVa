package ee.sk.pdf.validator.monitoring.template;

public interface TemplateLoader {
    String parsedTemplate(CharSequence replaceWith);
}
