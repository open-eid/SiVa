/*
 * Copyright 2020 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.statistics;

import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;

import java.util.ArrayList;
import java.util.List;

class ValidationConclusionBuilder {
    private String signatureForm;
    private List<SignatureValidationData> signatures;
    private List<TimeStampTokenValidationData> timeStampTokens;


    ValidationConclusionBuilder withSignatureForm(String signatureForm) {
        this.signatureForm = signatureForm;
        return this;
    }

    ValidationConclusionBuilder addSignatureWithFormat(String signatureFormat) {
        if (signatures == null) {
            signatures = new ArrayList<>();
        }
        SignatureValidationData signature = new SignatureValidationData();
        signature.setSignatureFormat(signatureFormat);
        signatures.add(signature);
        return this;
    }

    ValidationConclusionBuilder addTimeStampToken(TimeStampTokenValidationData timeStampTokenValidationData) {
        if (timeStampTokens == null) {
            timeStampTokens = new ArrayList<>();
        }
        timeStampTokens.add(timeStampTokenValidationData);
        return this;
    }

    ValidationConclusion build() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setSignatureForm(signatureForm);
        validationConclusion.setSignatures(signatures);
        validationConclusion.setTimeStampTokens(timeStampTokens);
        return validationConclusion;
    }
}
