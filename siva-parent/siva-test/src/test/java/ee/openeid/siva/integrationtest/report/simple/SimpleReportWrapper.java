package ee.openeid.siva.integrationtest.report.simple;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SimpleReportWrapper {

    @JsonProperty("SimpleReport")
    private SimpleReport simpleReport;

}
