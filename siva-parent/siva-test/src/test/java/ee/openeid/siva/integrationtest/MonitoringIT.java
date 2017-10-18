package ee.openeid.siva.integrationtest;


import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class MonitoringIT extends SiVaRestTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "large_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: WebApp-Monitoring-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#service-health-monitoring
     *
     * Title: Health monitor response structure
     *
     * Expected Result: response matches the expected structure of JSON
     *
     * File: not relevant
     */
    @Test
    public void requestingWebAppMonitoringStatusShouldReturnProperStructure() {
        getMonitoring()
                .then()
                .body(matchesJsonSchemaInClasspath("MonitorStatusSchema.json"))
                .body("status", Matchers.is("DOWN"))
                .body("health.status", Matchers.is("UP"))
                .body("link1.status", Matchers.is("DOWN"))
                .body("link1.name", Matchers.is("xRoadService"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
