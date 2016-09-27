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

import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.ria.xroad.common.CodedException;
import ee.ria.xroad.common.asic.AsicContainer;
import ee.ria.xroad.common.asic.AsicContainerVerifier;
import ee.ria.xroad.common.signature.Signature;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.SignatureValidationData.Indication.TOTAL_PASSED;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.*;

public class XROADQualifiedReportBuilder {

    private static final String XROAD_SIGNATURE_FORM = "ASiC_E_batchsignature";
    private static final String ASICE_SIGNATURE_FORM = "ASiC_E";

    private AsicContainerVerifier verifier;
    private String documentName;
    private Date validationTime;
    private ValidationPolicy validationPolicy;
    private XROADSignatureValidationDataBuilder signatureValidationDataBuilder;

    public XROADQualifiedReportBuilder(AsicContainerVerifier verifier,
                                       String documentName,
                                       Date validationTime,
                                       ValidationPolicy validationPolicy,
                                       CodedException... exceptions)
    {
        this.verifier = verifier;
        this.documentName = documentName;
        this.validationTime = validationTime;
        this.validationPolicy = validationPolicy;
        this.signatureValidationDataBuilder = new XROADSignatureValidationDataBuilder(verifier, Arrays.asList(exceptions));
    }

    public QualifiedReport build() {
        QualifiedReport qualifiedReport = new QualifiedReport();
        qualifiedReport.setPolicy(createReportPolicy(validationPolicy));
        qualifiedReport.setValidationTime(getDateFormatterWithGMTZone().format(validationTime));
        qualifiedReport.setDocumentName(documentName);
        qualifiedReport.setSignatureForm(getSignatureForm(verifier.getAsic()));
        qualifiedReport.setSignaturesCount(getTotalSignatureCount(verifier.getSignature()));
        qualifiedReport.setSignatures(Collections.singletonList(signatureValidationDataBuilder.build()));
        qualifiedReport.setValidSignaturesCount(
                qualifiedReport.getSignatures()
                        .stream()
                        .filter(signatures -> StringUtils.equals(signatures.getIndication(), TOTAL_PASSED.toString()))
                        .collect(Collectors.toList())
                        .size());
        return qualifiedReport;
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
