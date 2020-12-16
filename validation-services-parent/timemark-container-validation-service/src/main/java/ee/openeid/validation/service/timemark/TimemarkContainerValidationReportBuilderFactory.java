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
