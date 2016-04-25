package ee.openeid.siva.proxy.document.typeresolver;

import ee.openeid.siva.proxy.document.ReportType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class ReportTypeResolverTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void whenInputReportTypeStringIsSimpleThenReturnSimpleReportType() {
        ReportType reportType = ReportTypeResolver.reportTypeFromString("SIMPLE");
        assertEquals(reportType, ReportType.SIMPLE);
    }

    @Test
    public void ignoreCaseWithVariousInputReportTypeStrings() {
        ReportType reportType = ReportTypeResolver.reportTypeFromString("sImPlE");
        assertEquals(reportType, ReportType.SIMPLE);
        reportType = ReportTypeResolver.reportTypeFromString("DeTAILed");
        assertEquals(reportType, ReportType.DETAILED);
        reportType = ReportTypeResolver.reportTypeFromString("diagnosticDATA");
        assertEquals(reportType, ReportType.DIAGNOSTICDATA);
    }

    @Test
    public void whenInputReportTypeStringIsDetailedThenReturnDetailedReportType() {
        ReportType reportType = ReportTypeResolver.reportTypeFromString("DETAILED");
        assertEquals(reportType, ReportType.DETAILED);
    }

    @Test
    public void whenInputReportTypeStringIsDiagnosticDataThenReturnSDiagnosticDataReportType() {
        ReportType reportType = ReportTypeResolver.reportTypeFromString("DIAGNOSTICDATA");
        assertEquals(reportType, ReportType.DIAGNOSTICDATA);
    }

    @Test
    public void whenInputReportTypeStringIsInvalidThenThrowUnsupportedTypeException() {
        String reportTypeInput = "RANDOMREPORTTYPE";
        expectedException.expect(UnsupportedTypeException.class);
        expectedException.expectMessage("type = " + reportTypeInput + " is unsupported");

        ReportTypeResolver.reportTypeFromString(reportTypeInput);
    }

}
