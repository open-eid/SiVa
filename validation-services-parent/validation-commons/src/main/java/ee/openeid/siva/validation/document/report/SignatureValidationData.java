/*
 * Copyright 2020 Riigi Infosüsteemide Amet
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class SignatureValidationData {

    private String id;

    private String signatureFormat;

    private String signatureMethod;

    private String signatureLevel;

    private String signedBy;

    private SubjectDistinguishedName subjectDistinguishedName;

    private Indication indication;

    private String subIndication;

    private List<Error> errors;

    private List<SignatureScope> signatureScopes;

    private String claimedSigningTime;

    private List<Warning> warnings;

    private Info info;

    private List<Certificate> certificates;

    @JsonIgnore
    private String countryCode;

    public List<Certificate> getCertificatesByType(CertificateType type) {
        return certificates.stream()
                .filter(certificate -> type == certificate.getType())
                .collect(Collectors.toList());
    }

    public String getIndication() {
        return indication.toString();
    }

    public enum Indication {
        @JsonProperty("TOTAL-PASSED")
        TOTAL_PASSED("TOTAL-PASSED"),

        @JsonProperty("TOTAL-FAILED")
        TOTAL_FAILED("TOTAL-FAILED"),

        @JsonProperty("INDETERMINATE")
        INDETERMINATE("INDETERMINATE");

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
