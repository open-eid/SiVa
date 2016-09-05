package ee.openeid.siva.statistics;

import ee.openeid.siva.statistics.googleanalytics.GoogleAnalyticsMeasurementProtocolClient;
import ee.openeid.siva.statistics.model.SimpleValidationReport;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LoggerFactory.class})
public class StatisticsServiceTest {

    private static StatisticsService statisticsService;

    private static Logger loggerMock;

    @BeforeClass
    public static void setUp() {
        mockStatic(LoggerFactory.class);
        loggerMock = mock(Logger.class);
        when(LoggerFactory.getLogger(StatisticsService.class)).thenReturn(loggerMock);

        statisticsService = new StatisticsService();
        GoogleAnalyticsMeasurementProtocolClient ga = mock(GoogleAnalyticsMeasurementProtocolClient.class);
        statisticsService.setGoogleAnalyticsMeasurementClient(ga);
        doNothing().when(ga).sendStatisticalData(any(SimpleValidationReport.class));
    }

    @Test
    public void testValidationStatisticsLoggingWhereAllSignaturesInQualifiedReportAreValid() {
        long validationDurationInMillis = 1000L;
        int validSignaturesCount = 1;
        int totalSignatureCount = 1;
        SignatureValidationData.Indication indication = SignatureValidationData.Indication.TOTAL_PASSED;
        String subindication = "";
        String countryCode = "EE";

        QualifiedReport report = createDummyQualifiedReport(validSignaturesCount, totalSignatureCount);
        addSignatureValidationData(report, indication, subindication, countryCode);

        statisticsService.publishValidationStatistic(TimeUnit.MILLISECONDS.toNanos(validationDurationInMillis), report);
        verify(loggerMock).info("{\n" +
                "  \"dur\" : "+ validationDurationInMillis + ",\n" +
                "  \"sigCt\" : "+ totalSignatureCount + ",\n" +
                "  \"vSigCt\" : "+ validSignaturesCount + ",\n" +
                "  \"sigRslt\" : [ {\n" +
                "    \"i\" : \"" + indication + "\",\n" +
                "    \"cc\" : \"" + countryCode + "\"\n" +
                "  } ]\n" +
                "}"
        );
    }

    @Test
    public void testValidationStatisticsLoggingWhereOneSignatureInQualifiedReportIsValid() {
        long validationDurationInMillis = 2000L;
        int validSignaturesCount = 1;
        int totalSignatureCount = 2;
        SignatureValidationData.Indication firstSignatureIndication = SignatureValidationData.Indication.TOTAL_PASSED;
        String firstSignatureSubindication = "";
        String firstSignatureCountryCode = "EE";
        SignatureValidationData.Indication secondSignatureIndication = SignatureValidationData.Indication.TOTAL_FAILED;
        String secondSignatureSubindication = "CERTIFICATE_CHAIN_NOT_FOUND";
        String secondSignatureCountryCode = "US";

        QualifiedReport report = createDummyQualifiedReport(validSignaturesCount, totalSignatureCount);
        addSignatureValidationData(report, firstSignatureIndication, firstSignatureSubindication, firstSignatureCountryCode);
        addSignatureValidationData(report, secondSignatureIndication, secondSignatureSubindication, secondSignatureCountryCode);

        statisticsService.publishValidationStatistic(TimeUnit.MILLISECONDS.toNanos(validationDurationInMillis), report);
        verify(loggerMock).info("{\n" +
                "  \"dur\" : "+ validationDurationInMillis + ",\n" +
                "  \"sigCt\" : "+ totalSignatureCount + ",\n" +
                "  \"vSigCt\" : "+ validSignaturesCount + ",\n" +
                "  \"sigRslt\" : [ {\n" +
                "    \"i\" : \"" + firstSignatureIndication + "\",\n" +
                "    \"cc\" : \"" + firstSignatureCountryCode + "\"\n" +
                "  }, {\n" +
                "    \"i\" : \"" + secondSignatureIndication + "\",\n" +
                "    \"si\" : \"" + secondSignatureSubindication + "\",\n" +
                "    \"cc\" : \"" + secondSignatureCountryCode + "\"\n" +
                "  } ]\n" +
                "}"
        );
    }

    private QualifiedReport createDummyQualifiedReport(int validSignaturesCount, int totalSignaturesCount) {
        QualifiedReport report = new QualifiedReport();
        report.setSignaturesCount(totalSignaturesCount);
        report.setValidSignaturesCount(validSignaturesCount);
        return report;
    }

    private void addSignatureValidationData(QualifiedReport report, SignatureValidationData.Indication indication, String subindication, String country) {
        if (report.getSignatures() == null) {
            report.setSignatures(new ArrayList<>());
        }
        SignatureValidationData sigData = new SignatureValidationData();
        sigData.setIndication(indication);
        sigData.setSubIndication(subindication);
        sigData.setCountryCode(country);
        report.getSignatures().add(sigData);
    }

}
