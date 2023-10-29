/*
 * Copyright 2019 - 2023 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timestamptoken.validator.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.tsp.TimeStampToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.*;

public class TimeStampTokenValidationReportBuilder {

    private static final String ASICS_SIGNATURE_FORMAT = "ASiC-S";

    private final ValidationDocument validationDocument;
    private final ValidationPolicy validationPolicy;
    private final boolean isReportSignatureEnabled;
    private final TimeStampToken timeStampToken;
    private final List<Error> errors;

    public TimeStampTokenValidationReportBuilder(ValidationDocument validationDocument, TimeStampToken timeStampToken,
                                                 ValidationPolicy validationPolicy, List<Error> errors, boolean isReportSignatureEnabled) {
        this.validationDocument = validationDocument;
        this.validationPolicy = validationPolicy;
        this.isReportSignatureEnabled = isReportSignatureEnabled;
        this.timeStampToken = timeStampToken;
        this.errors = errors;
    }

    public Reports build() {
        ValidationConclusion validationConclusion = getValidationConclusion();
        SimpleReport simpleReport = new SimpleReport(validationConclusion);
        DetailedReport detailedReport = new DetailedReport(validationConclusion, null);
        DiagnosticReport diagnosticReport = new DiagnosticReport(validationConclusion, null);
        return new Reports(simpleReport, detailedReport, diagnosticReport);
    }

    private ValidationConclusion getValidationConclusion() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setPolicy(createReportPolicy(validationPolicy));
        validationConclusion.setValidationTime(getValidationTime());
        validationConclusion.setSignatureForm(ASICS_SIGNATURE_FORMAT);
        List<TimeStampTokenValidationData> timeStampTokenValidationDataList = new ArrayList<>();
        timeStampTokenValidationDataList.add(generateTimeStampTokenData());
        validationConclusion.setTimeStampTokens(timeStampTokenValidationDataList);
        validationConclusion.setValidatedDocument(ReportBuilderUtils.createValidatedDocument(isReportSignatureEnabled, validationDocument.getName(), validationDocument.getBytes()));
        return validationConclusion;
    }

    private TimeStampTokenValidationData generateTimeStampTokenData() {
        TimeStampTokenValidationData timeStampTokenValidationData = new TimeStampTokenValidationData();
        Date signedTime = timeStampToken.getTimeStampInfo().getGenTime();
        String signedBy = getTimeStampTokenSigner(timeStampToken);
        timeStampTokenValidationData.setCertificates(getCertificateList());
        timeStampTokenValidationData.setSignedBy(signedBy);
        timeStampTokenValidationData.setSignedTime(getDateFormatterWithGMTZone().format(signedTime));
        if (!errors.isEmpty()) {
            timeStampTokenValidationData.setError(errors);
            timeStampTokenValidationData.setIndication(TimeStampTokenValidationData.Indication.TOTAL_FAILED);
        } else {
            timeStampTokenValidationData.setIndication(TimeStampTokenValidationData.Indication.TOTAL_PASSED);
        }
        return timeStampTokenValidationData;
    }

    private List<Certificate> getCertificateList() {
        List<Certificate> certificateList = new ArrayList<>();
        timeStampToken.getCertificates().getMatches(null).forEach(cert -> {
            try {
                Certificate certificate = new Certificate();
                certificate.setContent(Base64.getEncoder().encodeToString(cert.getEncoded()));
                RDN c = cert.getSubject().getRDNs(BCStyle.CN)[0];
                certificate.setCommonName(IETFUtils.valueToString(c.getFirst().getValue()));
                certificate.setType(CertificateType.CONTENT_TIMESTAMP);
                certificateList.add(certificate);
            } catch (IOException ioException) {
                throw new IllegalStateException("Could not parse certificate");
            }
        });
        return certificateList;
    }

    private String getTimeStampTokenSigner(TimeStampToken timeStampToken) {
        ASN1Encodable x500Name = timeStampToken.getTimeStampInfo().getTsa().getName();
        if (x500Name instanceof X500Name) {
            return IETFUtils.valueToString(((X500Name) x500Name).getRDNs(BCStyle.CN)[0].getFirst().getValue());
        }
        return null;
    }

}
