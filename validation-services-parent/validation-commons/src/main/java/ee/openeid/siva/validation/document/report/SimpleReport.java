package ee.openeid.siva.validation.document.report;

import lombok.Data;

@Data
public class SimpleReport implements Report {

    private ValidationConclusion validationConclusion;

    public SimpleReport() {

    }

    public SimpleReport(ValidationConclusion validationConclusion) {
        this.validationConclusion = validationConclusion;
    }
}
