
/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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
