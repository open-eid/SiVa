/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.statistics.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({ "type", "dur", "sigCt", "vSigCt", "sigRslt" })
public class SimpleValidationReport {

    @JsonProperty("dur")
    private Long duration;

    @JsonProperty("sigCt")
    private Integer signatureCount;

    @JsonProperty("vSigCt")
    private Integer validSignatureCount;

    @JsonProperty("sigRslt")
    private List<SimpleSignatureReport> simpleSignatureReports;

    @JsonProperty("type")
    private String containerType;

}
