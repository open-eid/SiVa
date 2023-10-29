/*
 * Copyright 2020 - 2023 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timemark;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.validation.service.timemark.report.AsicContainerValidationReportBuilder;
import ee.openeid.validation.service.timemark.report.DDOCContainerValidationReportBuilder;
import ee.openeid.validation.service.timemark.report.TimemarkContainerValidationReportBuilder;
import org.digidoc4j.Container;
import org.digidoc4j.ValidationResult;
import org.digidoc4j.impl.ddoc.DDocContainer;

public class TimemarkContainerValidationReportBuilderFactory {

    public TimemarkContainerValidationReportBuilder getReportBuilder(Container container, ValidationDocument validationDocument,
                                                                     ValidationPolicy validationPolicy, ValidationResult validationResult,
                                                                     boolean isReportSignatureEnabled) {
        if (container instanceof DDocContainer) {
            return new DDOCContainerValidationReportBuilder(container, validationDocument, validationPolicy, validationResult, isReportSignatureEnabled);
        } else {
            return new AsicContainerValidationReportBuilder(container, validationDocument, validationPolicy, validationResult, isReportSignatureEnabled);
        }
    }
}
