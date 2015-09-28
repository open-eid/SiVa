package eu.europa.esig.dss.web.monitoring;

import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource.DiagnosticInfo;
import org.junit.Test;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

public class MonitoringInfoTest {

    private static final String TSL_URL = "http://sr.riik.ee/tsl/estonian-tsl.xml";
    private static final String LOADING_DIAGNOSTIC_MESSAGE = "Loaded Mon Sep 28 12:04:11 EEST 2015";
    private static final List<Date> CERTIFICATE_END_DATES = singletonList(new GregorianCalendar(2020, 1, 10).getTime());
    private static final Date NEXT_TSL_UPDATE = new GregorianCalendar(2015, 12, 1).getTime();

    @Test
    public void convertingTslLoadingDiagnosticsToJsonDataStructure() throws Exception {
        Map<String, TslDiagnosticInfo> result = new MonitoringInfo().buildJson(new LinkedHashMap<String, DiagnosticInfo>() {{
            put(TSL_URL, new DiagnosticInfo() {{
                setLoadingDiagnosticMessage(LOADING_DIAGNOSTIC_MESSAGE);
                setUsedSigningCertificatesValidityEndDates(CERTIFICATE_END_DATES);
                setTslNextUpdate(NEXT_TSL_UPDATE);
            }});
        }});

        TslDiagnosticInfo tslDiagnosticInfo = result.get(TSL_URL);
        assertEquals(LOADING_DIAGNOSTIC_MESSAGE, tslDiagnosticInfo.getLoadingDiagnosticInfo());
        assertEquals(CERTIFICATE_END_DATES,
                tslDiagnosticInfo.getUsedSigningCertificatesValidityEndDates());
        assertEquals(NEXT_TSL_UPDATE, tslDiagnosticInfo.getTslNextUpdate());
    }
}