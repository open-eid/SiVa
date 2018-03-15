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

package ee.openeid.validation.service.generic;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import eu.europa.esig.dss.jaxb.diagnostic.DiagnosticData;
import eu.europa.esig.dss.jaxb.diagnostic.XmlCertificate;
import eu.europa.esig.dss.jaxb.diagnostic.XmlRevocation;
import eu.europa.esig.dss.jaxb.diagnostic.XmlSignature;
import eu.europa.esig.dss.jaxb.diagnostic.XmlSigningCertificate;
import eu.europa.esig.dss.jaxb.diagnostic.XmlTimestamp;
import eu.europa.esig.dss.validation.reports.Reports;

@RunWith(MockitoJUnitRunner.class)
public class GenericValidationServiceTest {

    private static final String SIGNATURE_ID = "sig12345";
    private static final String CERTIFICATE_ID = "12345";
    private static final String CRL_SOURCE = "CRLToken";
    private static final String OCSP_SOURCE = "OCSPToken";

    @InjectMocks
    private GenericValidationService genericValidationService;

    @Test
    public void validTimestampAndRevocationDelta() throws Exception {
        Reports reports = getDefaultReport(3, 15, OCSP_SOURCE);
        genericValidationService.validateRevocationFreshness(reports);
        Assert.assertTrue(reports.getSimpleReport().getErrors(SIGNATURE_ID).isEmpty());
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    @Test
    public void freshnessWarningCauseTimestampAndRevocationDeltaTooLong() {
        Reports reports = getDefaultReport(3, 40, OCSP_SOURCE);
        genericValidationService.validateRevocationFreshness(reports);
        Assert.assertTrue(reports.getSimpleReport().getErrors(SIGNATURE_ID).isEmpty());
        Assert.assertEquals(1, reports.getSimpleReport().getWarnings(SIGNATURE_ID).size());
    }

    @Test
    public void freshnessErrorCauseTimestampAndRevocationDeltaTooLong() {
        Reports reports = getDefaultReport(4, 17, OCSP_SOURCE);
        genericValidationService.validateRevocationFreshness(reports);
        Assert.assertEquals(1, reports.getSimpleReport().getErrors(SIGNATURE_ID).size());
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    @Test
    public void validTimestampBetweenCrlUpdates() {
        Reports reports = getDefaultReport(3, 17, CRL_SOURCE);
        genericValidationService.validateRevocationFreshness(reports);
        Assert.assertTrue(reports.getSimpleReport().getErrors(SIGNATURE_ID).isEmpty());
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    @Test
    public void timestampBeforeThisUpdate() {
        Reports reports = getDefaultReport(2, 17, CRL_SOURCE);
        genericValidationService.validateRevocationFreshness(reports);
        Assert.assertEquals(1, reports.getSimpleReport().getErrors(SIGNATURE_ID).size());
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    @Test
    public void timestampAfterNextUpdate() {
        Reports reports = getDefaultReport(4, 17, CRL_SOURCE);
        genericValidationService.validateRevocationFreshness(reports);
        Assert.assertEquals(1, reports.getSimpleReport().getErrors(SIGNATURE_ID).size());
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    public Reports getDefaultReport(int days, int minutes, String source) {
        return new Reports(getDiagnosticDataJaxb(days, minutes, source), null, getSimpleReport());
    }

    public DiagnosticData getDiagnosticDataJaxb(int days, int minutes, String source) {
        DiagnosticData diagnosticData = new DiagnosticData();
        XmlCertificate usedCertificate = new XmlCertificate();
        usedCertificate.setId(CERTIFICATE_ID);

        List<XmlCertificate> usedCertificates = new ArrayList<>();
        usedCertificates.add(usedCertificate);
        usedCertificate.setRevocations(Collections.singletonList(getXmlRevocation(source)));
        XmlSigningCertificate xmlSigningCertificate = new XmlSigningCertificate();
        xmlSigningCertificate.setId(CERTIFICATE_ID);
        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setId(SIGNATURE_ID);
        xmlSignature.setSigningCertificate(xmlSigningCertificate);
        xmlSignature.setTimestamps(Collections.singletonList(getXmlTimestamp(days, minutes)));

        diagnosticData.setSignatures(Collections.singletonList(xmlSignature));
        diagnosticData.setUsedCertificates(usedCertificates);
        return diagnosticData;
    }

    private XmlRevocation getXmlRevocation(String source) {
        XmlRevocation xmlRevocation = new XmlRevocation();
        LocalDateTime revocationProductionLocalDatetime = LocalDateTime.of(2017, Month.JULY, 3, 15, 15);
        Date revocationProductionDate = Date.from(revocationProductionLocalDatetime.atZone(ZoneId.systemDefault()).toInstant());
        LocalDateTime revocationNextUpdateLocalDatetime = LocalDateTime.of(2017, Month.JULY, 4, 4, 15);
        Date revocationNextUpdateDate = Date.from(revocationNextUpdateLocalDatetime.atZone(ZoneId.systemDefault()).toInstant());
        xmlRevocation.setProductionDate(revocationProductionDate);
        xmlRevocation.setThisUpdate(revocationProductionDate);
        xmlRevocation.setNextUpdate(revocationNextUpdateDate);
        xmlRevocation.setSource(source);
        return xmlRevocation;
    }

    private XmlTimestamp getXmlTimestamp(int days, int minutes) {
        XmlTimestamp xmlTimestamp = new XmlTimestamp();
        LocalDateTime timestampProductionLocalDatetime = LocalDateTime.of(2017, Month.JULY, days, 15, minutes);
        Date timestampProductionDate = Date.from(timestampProductionLocalDatetime.atZone(ZoneId.systemDefault()).toInstant());
        xmlTimestamp.setProductionTime(timestampProductionDate);
        return xmlTimestamp;
    }

    private eu.europa.esig.dss.jaxb.simplereport.SimpleReport getSimpleReport() {
        eu.europa.esig.dss.jaxb.simplereport.SimpleReport simpleReport = new eu.europa.esig.dss.jaxb.simplereport.SimpleReport();
        XmlSigningCertificate xmlSigningCertificate = new XmlSigningCertificate();
        xmlSigningCertificate.setId(CERTIFICATE_ID);

        eu.europa.esig.dss.jaxb.simplereport.XmlSignature xmlSignature = new eu.europa.esig.dss.jaxb.simplereport.XmlSignature();
        xmlSignature.setId(SIGNATURE_ID);
        simpleReport.getSignature().add(xmlSignature);
        return simpleReport;
    }
}
