package ee.openeid.siva.validation.document.report;

import lombok.Data;

@Data
public class DetailedReport extends SimpleReport {

    private eu.europa.esig.dss.jaxb.detailedreport.DetailedReport validationProcess;

    public DetailedReport() {

    }

    public DetailedReport(ValidationConclusion validationConclusion, eu.europa.esig.dss.jaxb.detailedreport.DetailedReport validationProcess) {
        super(validationConclusion);
        this.validationProcess = validationProcess;
    }

} 
