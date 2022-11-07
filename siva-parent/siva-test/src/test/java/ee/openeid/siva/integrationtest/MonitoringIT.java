
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
package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Category(IntegrationTest.class)
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#service-health-monitoring
     *
     * Title: Health monitor response structure
     *
     * Expected Result: response matches the expected structure of JSON
     *
     * File: not relevant
     */
    @Test
    public void requestingWebAppMonitoringHealthStatusShouldReturnProperStructure() {
        getMonitoringHealth()
                .then()
                .body(matchesJsonSchemaInClasspath("MonitorHealthSchema.json"))
                .body("status", Matchers.is("UP"))
                .body("components.health.status", Matchers.is("UP"));
    }

    /**
     * TestCaseID: WebApp-Monitoring-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#simplified-health-monitoring
     *
     * Title: Heartbeat monitor response structure
     *
     * Expected Result: response matches the expected structure of JSON
     *
     * File: not relevant
     */
    @Test
    public void requestingWebAppMonitoringHeartbeatStatusShouldReturnProperStructure() {
        getMonitoringHeartbeat()
                .then()
                .body(matchesJsonSchemaInClasspath("MonitorHeartbeatSchema.json"))
                .body("status", Matchers.is("UP"));
    }

    /**
     * TestCaseID: WebApp-Monitoring-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#version-information
     *
     * Title: Version monitor response structure
     *
     * Expected Result: response matches the expected structure of JSON
     *
     * File: not relevant
     */
    @Test
    public void requestingWebAppMonitoringVersionInfoShouldReturnProperStructure() {
        getMonitoringVersion()
                .then()
                .body(matchesJsonSchemaInClasspath("MonitorVersionSchema.json"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
