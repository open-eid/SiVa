/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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
import lombok.Data;

import java.util.List;

@Data
public class QualifiedReport {

    eu.europa.esig.dss.jaxb.detailedreport.DetailedReport detailedReport;

    private Policy policy;

    private String validationTime;

    private String documentName;

    private String signatureForm;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ValidationWarning> validationWarnings;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SignatureValidationData> signatures;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer validSignaturesCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer signaturesCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TimeStampTokenValidationData> timeStampTokens;

}
