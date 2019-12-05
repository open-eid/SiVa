/*
 * Copyright 2019 Riigi Infosüsteemide Amet
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

import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DiagnosticReport extends SimpleReport {

    private XmlDiagnosticData diagnosticData;

    public DiagnosticReport() {
    }

    public DiagnosticReport(ValidationConclusion validationConclusion, XmlDiagnosticData diagnosticData) {
        super(validationConclusion);
        this.diagnosticData = diagnosticData;
    }

}
