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

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationConclusion {

    private Policy policy;

    private String validationTime;

    private String signatureForm;

    private List<ValidationWarning> validationWarnings = new ArrayList<>();

    private ValidatedDocument validatedDocument;

    private String validationLevel;

    private List<SignatureValidationData> signatures = new ArrayList<>();

    private Integer validSignaturesCount = 0;

    private Integer signaturesCount = 0;

    private List<TimeStampTokenValidationData> timeStampTokens = new ArrayList<>();
}
