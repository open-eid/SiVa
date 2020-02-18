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

package ee.openeid.validation.service.generic;

import eu.europa.esig.dss.diagnostic.jaxb.*;
import eu.europa.esig.dss.enumerations.RevocationReason;
import eu.europa.esig.dss.enumerations.RevocationType;
import eu.europa.esig.dss.enumerations.TimestampLocation;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import eu.europa.esig.dss.validation.reports.Reports;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static eu.europa.esig.dss.enumerations.RevocationType.CRL;
import static eu.europa.esig.dss.enumerations.RevocationType.OCSP;

@RunWith(MockitoJUnitRunner.class)
public class GenericValidationServiceTest {

    private static final String SIGNATURE_ID = "sig12345";
    private static final String CERTIFICATE_ID = "12345";

    @InjectMocks
    private GenericValidationService genericValidationService;

    @Test
    public void validTimestampAndRevocationDelta() {
        Reports reports = getDefaultReport(3, 15, OCSP);
        genericValidationService.validateRevocationFreshness(reports);
        Assert.assertTrue(reports.getSimpleReport().getErrors(SIGNATURE_ID).isEmpty());
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    @Test
    public void freshnessWarningCauseTimestampAndRevocationDeltaTooLong() {
        Reports reports = getDefaultReport(3, 40, OCSP);
        genericValidationService.validateRevocationFreshness(reports);
        Assert.assertTrue(reports.getSimpleReport().getErrors(SIGNATURE_ID).isEmpty());
        Assert.assertEquals(1, reports.getSimpleReport().getWarnings(SIGNATURE_ID).size());
    }

    @Test
    public void freshnessErrorCauseTimestampAndRevocationDeltaTooLong() {
        Reports reports = getDefaultReport(4, 17, OCSP);
        genericValidationService.validateRevocationFreshness(reports);
        Assert.assertEquals(1, reports.getSimpleReport().getErrors(SIGNATURE_ID).size());
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    @Test
    public void validTimestampBetweenCrlUpdates() {
        Reports reports = getDefaultReport(3, 17, CRL);
        genericValidationService.validateRevocationFreshness(reports);
        Assert.assertTrue(reports.getSimpleReport().getErrors(SIGNATURE_ID).isEmpty());
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    @Test
    public void timestampBeforeThisUpdate() {
        Reports reports = getDefaultReport(2, 17, CRL);
        genericValidationService.validateRevocationFreshness(reports);
        Assert.assertEquals(1, reports.getSimpleReport().getErrors(SIGNATURE_ID).size());
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    @Test
    public void timestampAfterNextUpdate() {
        Reports reports = getDefaultReport(4, 17, CRL);
        genericValidationService.validateRevocationFreshness(reports);
        Assert.assertEquals(1, reports.getSimpleReport().getErrors(SIGNATURE_ID).size());
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    public Reports getDefaultReport(int days, int minutes, RevocationType revocationType) {
        return new Reports(getDiagnosticDataJaxb(days, minutes, revocationType), null, getSimpleReport(), null);
    }

    public XmlDiagnosticData getDiagnosticDataJaxb(int days, int minutes, RevocationType revocationType) {
        XmlDiagnosticData diagnosticData = new XmlDiagnosticData();
        XmlCertificate usedCertificate = new XmlCertificate();
        usedCertificate.setId(CERTIFICATE_ID);

        XmlDistinguishedName distinguishedName = new XmlDistinguishedName();
        distinguishedName.setValue("SERIALNUMBER=333,CN=SOME\\,THING\\,333");
        usedCertificate.getSubjectDistinguishedName().add(distinguishedName);

        List<XmlCertificate> usedCertificates = new ArrayList<>();
        usedCertificates.add(usedCertificate);

        usedCertificate.setRevocations(Collections.singletonList(getXmlCertificateRevocation(revocationType)));
        XmlSigningCertificate xmlSigningCertificate = new XmlSigningCertificate();
        xmlSigningCertificate.setCertificate(usedCertificate);

        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setId(SIGNATURE_ID);
        xmlSignature.setSigningCertificate(xmlSigningCertificate);
        xmlSignature.setFoundTimestamps(Collections.singletonList(getXmlTimestamp(days, minutes)));

        diagnosticData.setSignatures(Collections.singletonList(xmlSignature));
        diagnosticData.setUsedCertificates(usedCertificates);
        return diagnosticData;
    }

    private XmlCertificateRevocation getXmlCertificateRevocation(RevocationType revocationType) {
        XmlCertificateRevocation xmlCertificateRevocation = new XmlCertificateRevocation();
        xmlCertificateRevocation.setReason(RevocationReason.AA_COMPROMISE);

        XmlRevocation xmlRevocation = new XmlRevocation();
        LocalDateTime revocationProductionLocalDatetime = LocalDateTime.of(2017, Month.JULY, 3, 15, 15);
        Date revocationProductionDate = Date.from(revocationProductionLocalDatetime.atZone(ZoneId.systemDefault()).toInstant());
        LocalDateTime revocationNextUpdateLocalDatetime = LocalDateTime.of(2017, Month.JULY, 4, 4, 15);
        Date revocationNextUpdateDate = Date.from(revocationNextUpdateLocalDatetime.atZone(ZoneId.systemDefault()).toInstant());
        xmlRevocation.setProductionDate(revocationProductionDate);
        xmlRevocation.setThisUpdate(revocationProductionDate);
        xmlRevocation.setNextUpdate(revocationNextUpdateDate);
        xmlRevocation.setType(revocationType);

        xmlCertificateRevocation.setRevocation(xmlRevocation);
        return xmlCertificateRevocation;
    }

    private XmlFoundTimestamp getXmlTimestamp(int days, int minutes) {
        XmlFoundTimestamp xmlFoundTimestamp = new XmlFoundTimestamp();
        xmlFoundTimestamp.setLocation(TimestampLocation.XAdES);
        XmlTimestamp xmlTimestamp = new XmlTimestamp();
        LocalDateTime timestampProductionLocalDatetime = LocalDateTime.of(2017, Month.JULY, days, 15, minutes);
        Date timestampProductionDate = Date.from(timestampProductionLocalDatetime.atZone(ZoneId.systemDefault()).toInstant());
        xmlTimestamp.setProductionTime(timestampProductionDate);
        xmlFoundTimestamp.setTimestamp(xmlTimestamp);
        return xmlFoundTimestamp;
    }

    private XmlSimpleReport getSimpleReport() {
        eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport simpleReport = new eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport();
        eu.europa.esig.dss.simplereport.jaxb.XmlSignature xmlSignature = new eu.europa.esig.dss.simplereport.jaxb.XmlSignature();
        xmlSignature.setId(SIGNATURE_ID);
        simpleReport.getSignature().add(xmlSignature);
        return simpleReport;
    }
}
