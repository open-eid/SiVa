/*
 * Copyright 2024 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.validator.report;

import eu.europa.esig.dss.detailedreport.DetailedReport;
import eu.europa.esig.dss.detailedreport.jaxb.XmlSignature;
import eu.europa.esig.dss.detailedreport.jaxb.XmlSubXCV;
import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationCertificateQualification;
import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationSignatureQualification;
import eu.europa.esig.dss.enumerations.CertificateQualification;
import eu.europa.esig.dss.enumerations.ValidationTime;
import eu.europa.esig.dss.validation.reports.Reports;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public class DssDetailedReportWrapper {

    private final @NonNull DetailedReport dssDetailedReport;

    public DssDetailedReportWrapper(@NonNull Reports dssReports) {
        this(dssReports.getDetailedReport());
    }

    public String getSigningCertificateId(String entityId) {
        return Optional
                .ofNullable(getDssDetailedReport().getSigningCertificate(entityId))
                .map(XmlSubXCV::getId)
                .orElse(null);
    }

    public XmlSignature getSignature(String signatureId) {
        return Optional
                .ofNullable(getDssDetailedReport().getSignatures())
                .stream()
                .flatMap(Collection::stream)
                .filter(sig -> StringUtils.equals(signatureId, sig.getId()))
                .findFirst()
                .orElse(null);
    }

    public XmlValidationSignatureQualification getValidationSignatureQualification(String signatureId) {
        return Optional
                .ofNullable(getSignature(signatureId))
                .map(XmlSignature::getValidationSignatureQualification)
                .orElse(null);
    }

    public CertificateQualification getSigningCertificateQualification(String signatureId, ValidationTime validationTime) {
        return getSignerValidationCertificateQualifications(signatureId)
                .filter(qual -> Objects.equals(validationTime, qual.getValidationTime()))
                .map(XmlValidationCertificateQualification::getCertificateQualification)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private Stream<XmlValidationCertificateQualification> getSignerValidationCertificateQualifications(String signatureId) {
        final String certificateId = getSigningCertificateId(signatureId);
        if (StringUtils.isEmpty(certificateId)) {
            return Stream.empty();
        }
        return getAllValidationCertificateQualifications(signatureId)
                .filter(qual -> StringUtils.equals(certificateId, qual.getId()));
    }

    private Stream<XmlValidationCertificateQualification> getAllValidationCertificateQualifications(String signatureId) {
        return Optional
                .ofNullable(getValidationSignatureQualification(signatureId))
                .map(XmlValidationSignatureQualification::getValidationCertificateQualification)
                .stream()
                .flatMap(Collection::stream);
    }

}
