package ee.openeid.siva.integrationtest.report.simple;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Error {

    @JsonProperty("NameId")
    private String nameId;

    @JsonProperty("content")
    private String content;

}
