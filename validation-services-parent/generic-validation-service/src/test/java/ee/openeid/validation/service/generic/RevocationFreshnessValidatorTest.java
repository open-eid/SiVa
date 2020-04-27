package ee.openeid.validation.service.generic;

import ee.openeid.validation.service.generic.helper.MockSignature;
import eu.europa.esig.dss.crl.CRLValidity;
import eu.europa.esig.dss.diagnostic.jaxb.*;
import eu.europa.esig.dss.enumerations.TimestampLocation;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import eu.europa.esig.dss.spi.x509.revocation.crl.OfflineCRLSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OfflineOCSPSource;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.reports.Reports;
import org.bouncycastle.cms.CMSException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class RevocationFreshnessValidatorTest {

    private static final String SIGNATURE_ID = "S-12345";
    private static final String CERTIFICATE_ID = "C-12345";

    @Test
    public void emptySignatureList() {
        Reports reports = getDefaultReport(3, 12, 15);
        RevocationFreshnessValidator validator = new RevocationFreshnessValidator(reports, Collections.emptyList());
        validator.validate();

        Assert.assertTrue(reports.getSimpleReport().getErrors(SIGNATURE_ID).isEmpty());
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    @Test
    public void validTimestampAndRevocationDelta() throws Exception {
        Reports reports = getDefaultReport(3, 12, 15);

        List<AdvancedSignature> signatures = getDefaultSignatures(new MockSignature.MockOfflineCRLSource(), null);

        RevocationFreshnessValidator validator = Mockito.spy(new RevocationFreshnessValidator(reports, signatures));
        Date thisUpdate = getDate(3);
        Date nextUpdate = getDate(4);
        Mockito.doReturn(getCrlValidity(thisUpdate, nextUpdate, signatures)).when(validator).buildCRLValidity(any(), any());

        validator.validate();
        Assert.assertTrue(reports.getSimpleReport().getErrors(SIGNATURE_ID).isEmpty());
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    @Test
    public void freshnessError_timestampBeforeThisUpdate() throws Exception {
        Reports reports = getDefaultReport(3, 12, 13);
        List<AdvancedSignature> signatures = getDefaultSignatures(new MockSignature.MockOfflineCRLSource(), null);
        RevocationFreshnessValidator validator = Mockito.spy(new RevocationFreshnessValidator(reports, signatures));
        Date thisUpdate = getDate(3);
        Date nextUpdate = getDate(4);
        Mockito.doReturn(getCrlValidity(thisUpdate, nextUpdate, signatures)).when(validator).buildCRLValidity(any(), any());

        validator.validate();
        Assert.assertEquals(1, reports.getSimpleReport().getErrors(SIGNATURE_ID).size());
        Assert.assertEquals("The revocation information is not considered as 'fresh'.", reports.getSimpleReport().getErrors(SIGNATURE_ID).get(0));
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    @Test
    public void freshnessError_timestampAfterNextUpdate() throws Exception {
        Reports reports = getDefaultReport(4, 12, 15);
        List<AdvancedSignature> signatures = getDefaultSignatures(new MockSignature.MockOfflineCRLSource(), null);
        RevocationFreshnessValidator validator = Mockito.spy(new RevocationFreshnessValidator(reports, getDefaultSignatures(new MockSignature.MockOfflineCRLSource(), null)));
        Date thisUpdate = getDate(3);
        Date nextUpdate = getDate(4);
        Mockito.doReturn(getCrlValidity(thisUpdate, nextUpdate, signatures)).when(validator).buildCRLValidity(any(), any());

        validator.validate();
        Assert.assertEquals(1, reports.getSimpleReport().getErrors(SIGNATURE_ID).size());
        Assert.assertEquals("The revocation information is not considered as 'fresh'.", reports.getSimpleReport().getErrors(SIGNATURE_ID).get(0));
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    @Test
    public void validTimestampAndOcspDelta() throws Exception {
        Reports reports = getDefaultReport(15, 12, 47);
        List<AdvancedSignature> signatures = getDefaultSignatures(null, new MockSignature.MockOfflineOCSPSource());
        RevocationFreshnessValidator validator = new RevocationFreshnessValidator(reports, signatures);

        validator.validate();
        Assert.assertTrue(reports.getSimpleReport().getErrors(SIGNATURE_ID).isEmpty());
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
    }

    @Test
    public void freshnessWarning_timestampBeforeOcsp() throws Exception {
        Reports reports = getDefaultReport(15, 12, 30);
        List<AdvancedSignature> signatures = getDefaultSignatures(null, new MockSignature.MockOfflineOCSPSource());
        RevocationFreshnessValidator validator = new RevocationFreshnessValidator(reports, signatures);

        validator.validate();
        Assert.assertTrue(reports.getSimpleReport().getErrors(SIGNATURE_ID).isEmpty());
        Assert.assertEquals(1, reports.getSimpleReport().getWarnings(SIGNATURE_ID).size());
        Assert.assertEquals("The revocation information is not considered as 'fresh'.", reports.getSimpleReport().getWarnings(SIGNATURE_ID).get(0));
    }

    @Test
    public void freshnessWarning_timestampAfterOcsp() throws Exception {
        Reports reports = getDefaultReport(15, 13, 10);
        List<AdvancedSignature> signatures = getDefaultSignatures(null, new MockSignature.MockOfflineOCSPSource());
        RevocationFreshnessValidator validator = new RevocationFreshnessValidator(reports, signatures);

        validator.validate();
        Assert.assertTrue(reports.getSimpleReport().getErrors(SIGNATURE_ID).isEmpty());
        Assert.assertEquals(1, reports.getSimpleReport().getWarnings(SIGNATURE_ID).size());
        Assert.assertEquals("The revocation information is not considered as 'fresh'.", reports.getSimpleReport().getWarnings(SIGNATURE_ID).get(0));
    }

    @Test
    public void freshnessError_timestampAfterOcsp() throws Exception {
        Reports reports = getDefaultReport(16, 12, 48);
        List<AdvancedSignature> signatures = getDefaultSignatures(null, new MockSignature.MockOfflineOCSPSource());
        RevocationFreshnessValidator validator = new RevocationFreshnessValidator(reports, signatures);

        validator.validate();
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
        Assert.assertEquals(1, reports.getSimpleReport().getErrors(SIGNATURE_ID).size());
        Assert.assertEquals("The revocation information is not considered as 'fresh'.", reports.getSimpleReport().getErrors(SIGNATURE_ID).get(0));
    }

    @Test
    public void freshnessError_timestampBeforeOcsp() throws Exception {
        Reports reports = getDefaultReport(14, 12, 46);
        List<AdvancedSignature> signatures = getDefaultSignatures(null, new MockSignature.MockOfflineOCSPSource());
        RevocationFreshnessValidator validator = new RevocationFreshnessValidator(reports, signatures);

        validator.validate();
        Assert.assertTrue(reports.getSimpleReport().getWarnings(SIGNATURE_ID).isEmpty());
        Assert.assertEquals(1, reports.getSimpleReport().getErrors(SIGNATURE_ID).size());
        Assert.assertEquals("The revocation information is not considered as 'fresh'.", reports.getSimpleReport().getErrors(SIGNATURE_ID).get(0));
    }

    private Date getDate(int dayOfMonth) {
        return Date.from(LocalDateTime.of(2019, Month.MARCH, dayOfMonth, 12, 14).atZone(ZoneId.systemDefault()).toInstant());
    }

    private CRLValidity getCrlValidity(Date thisUpdate, Date nextUpdate, List<AdvancedSignature> signatures) throws CMSException {
        CRLValidity crlValidity = new CRLValidity(signatures.get(0).getCRLSource().getCRLBinaryList().iterator().next());
        crlValidity.setNextUpdate(nextUpdate);
        crlValidity.setThisUpdate(thisUpdate);
        return crlValidity;
    }

    private List<AdvancedSignature> getDefaultSignatures(OfflineCRLSource offlineCRLSource, OfflineOCSPSource offlineOCSPSource) throws CMSException {
        AdvancedSignature signature = new MockSignature(offlineCRLSource, offlineOCSPSource);
        return Collections.singletonList(signature);
    }

    public Reports getDefaultReport(int days, int hours, int minutes) {
        return new Reports(getDiagnosticDataJaxb(days, hours, minutes), null, getSimpleReport(), null);
    }

    public XmlDiagnosticData getDiagnosticDataJaxb(int days, int hours, int minutes) {
        XmlDiagnosticData diagnosticData = new XmlDiagnosticData();
        XmlCertificate usedCertificate = new XmlCertificate();
        usedCertificate.setId(CERTIFICATE_ID);

        XmlDistinguishedName distinguishedName = new XmlDistinguishedName();
        distinguishedName.setValue("SERIALNUMBER=333,CN=SOME\\,THING\\,333");
        usedCertificate.getSubjectDistinguishedName().add(distinguishedName);

        List<XmlCertificate> usedCertificates = new ArrayList<>();
        usedCertificates.add(usedCertificate);

        XmlSigningCertificate xmlSigningCertificate = new XmlSigningCertificate();
        xmlSigningCertificate.setCertificate(usedCertificate);

        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setId(SIGNATURE_ID);
        xmlSignature.setSigningCertificate(xmlSigningCertificate);
        xmlSignature.setFoundTimestamps(Collections.singletonList(getXmlTimestamp(days, hours, minutes)));

        diagnosticData.setSignatures(Collections.singletonList(xmlSignature));
        diagnosticData.setUsedCertificates(usedCertificates);
        return diagnosticData;
    }

    private XmlFoundTimestamp getXmlTimestamp(int days, int hours, int minutes) {
        XmlFoundTimestamp xmlFoundTimestamp = new XmlFoundTimestamp();
        xmlFoundTimestamp.setLocation(TimestampLocation.XAdES);
        XmlTimestamp xmlTimestamp = new XmlTimestamp();
        LocalDateTime timestampProductionLocalDatetime = LocalDateTime.of(2019, Month.MARCH, days, hours, minutes);
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
