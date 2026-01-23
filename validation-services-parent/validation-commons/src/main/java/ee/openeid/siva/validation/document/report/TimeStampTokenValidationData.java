/*
 * Copyright 2017 - 2026 Riigi Infosüsteemi Amet
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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
public class TimeStampTokenValidationData {
    private Indication indication;
    private String subIndication;
    private String timestampLevel;
    private String signedBy;
    private String signedTime;
    /**
     * @deprecated Deprecated for removal. Use {@link #errors} instead.
     */
    @Deprecated
    @Setter(AccessLevel.NONE)
    private List<Error> error;
    private List<Error> errors;
    /**
     * @deprecated Deprecated for removal. Use {@link #warnings} instead.
     */
    @Deprecated
    @Setter(AccessLevel.NONE)
    private List<Warning> warning;
    private List<Warning> warnings;
    private List<Certificate> certificates;
    private List<Scope> timestampScopes;

    // Remove when the deprecated error field is removed
    public void setErrors(List<Error> errors) {
        this.errors = errors;
        this.error = errors;
    }

    // Remove when the deprecated warning field is removed
    public void setWarnings(List<Warning> warnings) {
        this.warnings = warnings;
        this.warning = warnings;
    }

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
