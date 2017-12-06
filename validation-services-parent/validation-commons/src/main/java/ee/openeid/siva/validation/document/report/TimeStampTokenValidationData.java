/*
 * Copyright 2017 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.validation.document.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TimeStampTokenValidationData {
    private Indication indication;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String signedBy;
    private String signedTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Error> error;

    public enum Indication {
        @JsonProperty("TOTAL-PASSED")
        TOTAL_PASSED("TOTAL-PASSED"),

        @JsonProperty("TOTAL-FAILED")
        TOTAL_FAILED("TOTAL-FAILED");

        private final String stringRepresentation;

        Indication(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        @Override
        public String toString() {
            return stringRepresentation;
        }

    }
}
