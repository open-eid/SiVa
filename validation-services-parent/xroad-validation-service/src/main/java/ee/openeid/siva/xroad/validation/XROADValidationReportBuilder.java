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

package ee.openeid.siva.xroad.validation;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.DetailedReport;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.ria.xroad.common.CodedException;
import ee.ria.xroad.common.asic.AsicContainer;
import ee.ria.xroad.common.asic.AsicContainerVerifier;
import ee.ria.xroad.common.signature.Signature;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.SignatureValidationData.Indication.TOTAL_PASSED;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.*;

public class XROADValidationReportBuilder {

    private static final String XROAD_SIGNATURE_FORM = "ASiC-E_batchsignature";
    private static final String ASICE_SIGNATURE_FORM = "ASiC-E";

    private AsicContainerVerifier verifier;
    private ValidationDocument validationDocument;
    private ValidationPolicy validationPolicy;
    private XROADSignatureValidationDataBuilder signatureValidationDataBuilder;
    private boolean isReportSignatureEnabled;

    public XROADValidationReportBuilder(AsicContainerVerifier verifier,
                                        ValidationDocument validationDocument,
                                        ValidationPolicy validationPolicy,
                                        boolean isReportSignatureEnabled,
                                        CodedException... exceptions) {
        this.verifier = verifier;
        this.validationDocument = validationDocument;
        this.validationPolicy = validationPolicy;
        this.isReportSignatureEnabled = isReportSignatureEnabled;
        this.signatureValidationDataBuilder = new XROADSignatureValidationDataBuilder(verifier, Arrays.asList(exceptions));
    }

    public Reports build() {
        ValidationConclusion validationConclusion = getValidationConclusion();
        SimpleReport simpleReport = new SimpleReport(validationConclusion);
        DetailedReport detailedReport = new DetailedReport(validationConclusion, null);
        return new Reports(simpleReport, detailedReport);
    }

    private ValidationConclusion getValidationConclusion() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setPolicy(createReportPolicy(validationPolicy));
        validationConclusion.setValidationTime(getValidationTime());
        validationConclusion.setSignatureForm(getSignatureForm(verifier.getAsic()));
        validationConclusion.setSignaturesCount(getTotalSignatureCount(verifier.getSignature()));
        validationConclusion.setValidationWarnings(Collections.emptyList());
        validationConclusion.setSignatures(Collections.singletonList(signatureValidationDataBuilder.build()));
        validationConclusion.setValidSignaturesCount(
                validationConclusion.getSignatures()
                        .stream()
                        .filter(signatures -> StringUtils.equals(signatures.getIndication(), TOTAL_PASSED.toString()))
                        .collect(Collectors.toList())
                        .size());
        validationConclusion.setValidatedDocument(ReportBuilderUtils.createValidatedDocument(isReportSignatureEnabled, validationDocument.getName(), validationDocument.getBytes()));
        return validationConclusion;
    }

    private int getTotalSignatureCount(Signature signature) {
        return signature != null ? 1 : 0;
    }

    private String getSignatureForm(AsicContainer asicContainer) {
        if (asicContainer != null && asicContainer.getSignature() != null) {
            return asicContainer.getSignature().isBatchSignature() ? XROAD_SIGNATURE_FORM : ASICE_SIGNATURE_FORM;
        }
        return valueNotPresent();
    }
}
