<!--# QA Strategy-->

## Introduction

The goal of this document is to give general overview of the used infrastructure, processes, schedule and actions to ensure good quality delivery. The document describes activities in the whole software development process. Analysis, development and testing are separated for the sake of structure and transparency although they are integral parts of the development cycle.
This is living document and will be constantly updated as the project evolves.

## Environments and infrastructure

![Enviroment](../img/siva/qa_strategy/Env.png)

There are different test environments for quality assurance, depending on the nature and aim.

  1. TravisCI environment for public CI - Platform: Linux
  2. Test environment for local test and load test - Platform: Linux

Instructions how to set up test enviroment and run tests together with more info can be found in [SiVa GitHub page](https://github.com/open-eid/SiVa)

System requirements:

  * At least 2GB of RAM on machine where the build is executed
  * Minimum required Java version is Java 8
  * SiVa project provided Maven Wrapper (./mvnw)

Tools used:

  * TravisCI – is a continuous integration service used to build and test software projects hosted at GitHub
  * Jenkins – is an open source continuous integration tool written in Java.
  * JMeter – tool for creating and running load tests.
  * IntelliJ IDEA – is a Java integrated development environment(IDE) for developing and executing automated tests locally
  * Apache Tomcat - is an open source servlet container developed by the Apache Software Foundation.
  * Docker – is an open-source project that automates the deployment of applications inside software containers, by providing an additional layer of abstraction and automation of operating-system-level virtualization on Linux.
  * Rest-Assured - is a Java DSL(Domain-specific language) for simplifying testing of REST based Services built on top of HTTP Builder.

## Analysis

Analysis will be tagged with identificators to enable cross-reference between requirements and corresponding tests. This includes both functional and non-functional requirements. Analysis of specific tasks must be done before the sprint planning meeting. This will ensure that upcoming sprint is planned in common grounds.

See documents[(2) and (3) in References](/siva/references/)

## Development

### Development process

Customized iterative process similar to SCRUM is used in the development. The process consists of following elements:

  * Product backlog is maintained
  * Sprints are two weeks long
  * Sprint planning meetings are held
  * Tasks are maintained through JIRA SCRUM board
  * Daily team stand-ups are held
  * Tasks marked done are developed, tested and ready to be shipped
  * Bi-weekly meetings are held to give status on progress and discuss open questions
  * Retrospectives are held at least with two month intervals

![Development process](../img/siva/qa_strategy/DevProcess.png)

### Issue lifecycle

Each ticket (defect, task, ...) in JIRA SCRUM board goes through the following states that correspond the development procedure described in previous chapter.

![Issue lifecycle](../img/siva/qa_strategy/IssueLifecycle.png)

Description of states:

  * ToDo - tickets to be dealt with in current sprint (Sprint backlog).
  * Analysis -ticket is being analysed. During this stage requirements are refined.
  * Development - ticket is being handled (feature coding, document writing, ...).
  * In Review - the work done in development stage is being reviewed.
  * Test - ticket is being tested.
  * Done - ticket has been tested and deemed sufficient for release (Ready features)
  * Closed - ticket has been released.

### QA activities and quality criterias in the development

**Process improvement**

The development process is constantly monitored and adjusted to the changing situations. Retrospective meetings for process feedback are held.

**Unit tests**

It is responsibility of developer to write, maintain and execute the unit tests on developed features. The code must compile and pass unit tests before it is allowed to be submitted to the code repository.  The code of the unit tests is integral part of the source code and is maintained on the same principles.

Unit tests are also automatically executed on each build, if the unit tests do not pass further test execution is stopped.

**Static testing/code reviews**

All changes (including changes in unit test code) are reviewed by another development team member using GitHub. The code must pass review before it is submitted to testing.

SonarLint is used to validate code automatically. It integrates both suggested tools mentioned in reference document [(3) References](/siva/references/)

## Testing

### Approach and schedule

Testing follows the principles described in reference document [(1) in References](/siva/references/)

The goal is to automate as much of the testing process as possible, however some aspects of the testing will be carried out manually.

As the development is carried out validation module by validation module, testing will follow the same principle. The tests will be developed in iterations for each validation module.

After each iteration test cases, test automation code and test results will be available through GitHub.

The main focus on testing is on different type of input data, the tests themselves will be based on the same principle for all the modules.

The schedule of testing can be seen on the image below.

![Testing schedule](../img/siva/qa_strategy/Schedule.png)

### Testing process

All automatic tests, except load tests will follow the same execution process. The tests are ran automatically during the project build process by Travis CI after each code change push in GitHub.

![Testing process](../img/siva/qa_strategy/TestProcess.png)

### Test case management

Test cases are handled as integral part of test automation code. The same applies on manual tests, in manual test cases some portion of automation may be used to execute the tests but the results are verified manually. All the test cases and test code will be maintained in the GitHub.

Test cases are developed and maintained together with test automation code by the QA specialist for Integration and System Testing.

Following elements will be present in test cases:

  * TestCaseID: unique ID that makes possible to identify specific test case
  * TestType: Automated, Automated SoapUI or Manual
  * Requirement: Reference to the requirement that is tested
  * Title: Description of the test
  * Expected Result: expected outcome of the test (pass criteria for the test)
  * File: input test file that is used

**Test case sample**

Automatic and manual test cases will have the same description principles (shown below).

```bash
    /**
     * TestCaseID: Bdoc-ValidationFail-27
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc 	OCSP response doesn't correspond to the signers certificate
     *
     * Expected Result: The document should fail the validation
     *
     * File: NS28_WrongSignerCertInOCSPResp.bdoc
     */
    @Test
    public void bdocWrongSignersCertInOcspResponse() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("NS28_WrongSignerCertInOCSPResp.bdoc"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("TRY_LATER"))
                .body("signatures[0].errors.content", Matchers.hasItem("No revocation data for the certificate"))
                .body("validSignaturesCount", Matchers.is(0));
    }
```

### Defect management

All found defects will be reported and handled in Jira. The defect report will follow similar lifecycle in JIRA SCRUM board as tasks.

The report will have following elements:

  * Title: Short, precise description of the problem
  * Details: Type, Priority, Sprint, Epic Link, Fix Version/s
  * Description:
	* **Steps to reproduce bug** - sequence for reproduction, link to test case if applicable.
	* **Expected behavior** - expected outcome of the test sequence.
	* **Actual behavior** - actual result of the test sequence. The description should be thorough enough to enable the debugging of the problem and to give objective base for severity assessment.
	* **File attachments** - Test files, logs, images, ...

### Test levels

**Integration testing**

The scope of the tests is illustrated on the image below. The goal is to test the SiVA application API (both X-Road and REST/JSON) and to test the independent module capability for validation of specific type of file (DDOC, BDOC, PDF or X-Road signature). Both valid and invalid inputs are tested. More info about testing specifics can be found in Test Plan [Integration testing](/siva/test_plan/#integration-test-introduction) section.

![Integration testing](../img/siva/qa_strategy/IntegrationTest.png)

**System testing**

The scope of the tests is illustrated on the image below. The goal of the test is to test the entire length of signature validation process and to test supportive functions. In addition Demo application is tested. More info about testing specifics can be found in Test Plan [System testing](/siva/test_plan/#system-test-introduction) section.

![System testing](../img/siva/qa_strategy/SystemTest.png)

**Regression testing**

Regression testing will consist of two parts:
Running all automated tests (unit, integration and system tests)
Manual testing of the areas that are not covered by automatic tests based on the regression test checklist

