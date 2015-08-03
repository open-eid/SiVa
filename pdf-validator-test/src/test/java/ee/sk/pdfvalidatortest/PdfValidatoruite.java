package ee.sk.pdfvalidatortest;

import ee.sk.testserver.TestServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        InvalidDocumentFormatTests.class,
        InvalidSignaturesTest.class
})
public class PdfValidatoruite {
    private static TestServer server = new TestServer();

    @BeforeClass
    public static void startTestServer() throws Exception {
        server.startServer();
    }

    @AfterClass
    public static void stopServer() {
        server.stopServer();
    }
}
