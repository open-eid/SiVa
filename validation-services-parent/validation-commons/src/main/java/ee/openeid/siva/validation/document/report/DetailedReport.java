package ee.openeid.siva.validation.document.report;

import lombok.Data;

@Data
public class DetailedReport implements Report {

    private ValidationConclusion validationConclusion;
    private eu.europa.esig.dss.jaxb.detailedreport.DetailedReport validationProcess;

    public DetailedReport(ValidationConclusion validationConclusion, eu.europa.esig.dss.jaxb.detailedreport.DetailedReport validationProcess) {
        this.validationConclusion = validationConclusion;
        this.validationProcess = validationProcess;
    }
}
