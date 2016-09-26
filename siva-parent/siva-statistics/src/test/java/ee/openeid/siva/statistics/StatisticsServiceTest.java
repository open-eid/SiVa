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

    private static final String LINE_SEPARATOR = System.lineSeparator();

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
        String signatureForm = "PAdES";
        int validSignaturesCount = 1;
        int totalSignatureCount = 1;
        SignatureValidationData.Indication indication = SignatureValidationData.Indication.TOTAL_PASSED;
        String subindication = "";
        String countryCode = "EE";

        QualifiedReport report = createDummyQualifiedReport(signatureForm, validSignaturesCount, totalSignatureCount);
        addSignatureValidationData(report, indication, subindication, countryCode);

        statisticsService.publishValidationStatistic(TimeUnit.MILLISECONDS.toNanos(validationDurationInMillis), report);
        verify(loggerMock).info("{" + LINE_SEPARATOR  +
                "  \"stats\" : {" + LINE_SEPARATOR  +
                "    \"type\" : \"" + signatureForm + "\"," + LINE_SEPARATOR  +
                "    \"dur\" : "+ validationDurationInMillis + "," + LINE_SEPARATOR  +
                "    \"sigCt\" : "+ totalSignatureCount + "," + LINE_SEPARATOR  +
                "    \"vSigCt\" : "+ validSignaturesCount + "," + LINE_SEPARATOR  +
                "    \"sigRslt\" : [ {" + LINE_SEPARATOR  +
                "      \"i\" : \"" + indication + "\"," + LINE_SEPARATOR  +
                "      \"cc\" : \"" + countryCode + "\"" + LINE_SEPARATOR  +
                "    } ]" + LINE_SEPARATOR  +
                "  }" + LINE_SEPARATOR  +
                "}"
        );
    }

    @Test
    public void testValidationStatisticsLoggingWhereOneSignatureInQualifiedReportIsValid() {
        long validationDurationInMillis = 2000L;
        String signatureForm = "PAdES";
        int validSignaturesCount = 1;
        int totalSignatureCount = 2;
        SignatureValidationData.Indication firstSignatureIndication = SignatureValidationData.Indication.TOTAL_PASSED;
        String firstSignatureSubindication = "";
        String firstSignatureCountryCode = "EE";
        SignatureValidationData.Indication secondSignatureIndication = SignatureValidationData.Indication.TOTAL_FAILED;
        String secondSignatureSubindication = "CERTIFICATE_CHAIN_NOT_FOUND";
        String secondSignatureCountryCode = "US";

        QualifiedReport report = createDummyQualifiedReport(signatureForm, validSignaturesCount, totalSignatureCount);
        addSignatureValidationData(report, firstSignatureIndication, firstSignatureSubindication, firstSignatureCountryCode);
        addSignatureValidationData(report, secondSignatureIndication, secondSignatureSubindication, secondSignatureCountryCode);

        statisticsService.publishValidationStatistic(TimeUnit.MILLISECONDS.toNanos(validationDurationInMillis), report);
        verify(loggerMock).info("{" + LINE_SEPARATOR  +
                "  \"stats\" : {" + LINE_SEPARATOR  +
                "    \"type\" : \"" + signatureForm + "\"," + LINE_SEPARATOR  +
                "    \"dur\" : "+ validationDurationInMillis + "," + LINE_SEPARATOR  +
                "    \"sigCt\" : "+ totalSignatureCount + "," + LINE_SEPARATOR  +
                "    \"vSigCt\" : "+ validSignaturesCount + "," + LINE_SEPARATOR  +
                "    \"sigRslt\" : [ {" + LINE_SEPARATOR  +
                "      \"i\" : \"" + firstSignatureIndication + "\"," + LINE_SEPARATOR  +
                "      \"cc\" : \"" + firstSignatureCountryCode + "\"" + LINE_SEPARATOR  +
                "    }, {" + LINE_SEPARATOR  +
                "      \"i\" : \"" + secondSignatureIndication + "\"," + LINE_SEPARATOR  +
                "      \"si\" : \"" + secondSignatureSubindication + "\"," + LINE_SEPARATOR  +
                "      \"cc\" : \"" + secondSignatureCountryCode + "\"" + LINE_SEPARATOR  +
                "    } ]" + LINE_SEPARATOR  +
                "  }" + LINE_SEPARATOR  +
                "}"
        );
    }

    private QualifiedReport createDummyQualifiedReport(String signatureForm, int validSignaturesCount, int totalSignaturesCount) {
        QualifiedReport report = new QualifiedReport();
        report.setSignaturesCount(totalSignaturesCount);
        report.setValidSignaturesCount(validSignaturesCount);
        report.setSignatureForm(signatureForm);
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
