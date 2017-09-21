package ee.openeid.siva.validation.document.report;

import lombok.Data;

@Data
public class Reports {

    SimpleReport simpleReport;
    DetailedReport detailedReport;

    public Reports() {

    }

    public Reports(SimpleReport simpleReport, DetailedReport detailedReport) {
        this.simpleReport = simpleReport;
        this.detailedReport = detailedReport;
    }

} 
