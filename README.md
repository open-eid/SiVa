# Signature Verification Service 

[![Build Status](https://travis-ci.org/open-eid/SiVa.svg?branch=develop)](https://travis-ci.org/open-eid/SiVa)
[![Coverage Status](https://coveralls.io/repos/github/open-eid/SiVa/badge.svg?branch=develop)](https://coveralls.io/github/open-eid/SiVa?branch=develop)

SiVa is digitally signed documents validations web service with REST JSON API
built with [Spring Boot](http://projects.spring.io/spring-boot/). Supported digitally
signed document formats are: BDOC, DDOC and PDF files with at least signature level Long Term.

## Main features

* SiVa REST ETSI compliant API to validate all supported signatures.
* SiVa handles files in PDF-format version 1.7 and later, signed with PadES-profile signatures.
* Service handles DDOC files starting from version 1.0 or later
* Service supports BDOC files starting from version 2.1 or later
* Signatures with PadES-LT and PadES-LTA profile are supported.
* BDOC signatures with type BDOC-TM and BDOC-TS are supported

### Libraries used in validation services

Below is list of Java libraries we use and for which digital signed file we use it for:

* [JDigiDoc](https://github.com/open-eid/jdigidoc) - is used to validate Estonian older digital 
  signature format called DDOC
* [DigiDoc4J](https://github.com/open-eid/digidoc4j) - is used to validate BDOC digital signature container 
  that are compliant with ASiCE standard  
* [DigiDoc4J DSS fork](https://github.com/open-eid/sd-dss) - to validate digitally signed PDF files that 
  comply with Estonian laws

## Requirements

These are minimum requirements to build and develop SiVa project:

* **git** - to easily download and update code. You can [download git here](https://git-scm.com/)
* **Oracle Java** - to compile and run SiVa applications. Download link for [Oracle Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* **IDE** - to develop SiVa. We recommend to use [JetBrains IntelliJ](https://www.jetbrains.com/idea/)
* **2 GB of RAM** the RAM requirement is here because when building the project the integration tests take up a lot of memory
* Optionally You can also install **Maven** but it is not needed because SiVa project uses Maven wrapper to install maven

## How to build

### Using Maven Wrapper

Recommended way of building this project is using [Maven Wrapper](https://github.com/takari/maven-wrapper) to build it.
Run following command:

```bash
./mvnw clean install 
```

## How to run

SiVa project compiles **fat executable JAR** files that You can run after successfully building the
project by issuing below commands:

**Start SiVa REST web service**

```bash
./siva-parent/siva-webapp/target/executable/siva-webapp-2.0.2-SNAPSHOT.jar
```

The service by default runs on port **8080**. Easiest way to test out validation is run SiVa demo
application.

**Start SiVa Demo Application**

```bash
./siva-parent/siva-sample-application/target/siva-sample-application-2.0.2-SNAPSHOT.jar
```

Now point Your browser to URL: [http://localhost:9000]

![Sample of validation result](https://raw.githubusercontent.com/open-eid/SiVa/develop/docs/img/siva_demo_validation.png)

## How to run tests

Unit and integration tests are integral part of the SiVa code base. The tests are automatically executed every time the application is built. The build will fail if any of the tests fail.

To execute the tests from command line after application is built use:

```bash
./mvnw verify
```

### How to run performance tests

Performance tests are disabled by default, but can be enabled with maven parameter `-Drun.load.tests=true`. All unit 
and integration tests will be executed prior the performance tests. When executing the performance tests, SiVa 
Web application has to be started before the tests are executed.

> **Note**: PDF load test files contain test certificates. In order for PDF load tests to succeed 
> SiVa application should be started with test certificates preloaded.

To load trusted test certificates in addition to LOTL add spring profile parameter to the command.

```bash
java -Dspring.profiles.active=test -jar siva-webapp-2.0.2-SNAPSHOT.jar
```

To run the performance tests:

```bash
./mvnw verify -Drun.load.tests=true
```

It is possible to configure following parameters in performance test:

  * `jmeter.host.name` - target webapp host against what the tests are executed, default is localhost
  * `jmeter.host.port` - target port of the webapp host , default is 8080
  * `jmeter.host.endpoint` - name of endpoint, default is /validate
  * `jmeter.host.timeoutInMillis` - response waiting timeout, default is 60000
  * `jmeter.testfiles.dir` - directory of the test files, default is ${project.basedir}/src/test/jmeter/test-files
  * `jmeter.thread.init.count` - start thread count, default is 5
  * `jmeter.thread.max.count` - max thread count, default is 50
  * `jmeter.thread.increment.count` - step of thread count incrementation, dedault is 5
  * `jmeter.thread.increment.intervalInSecs` - time between incrementations, default is 180

The default values can be changed `siva-test/pom.xml` file. It is also possible to run the tests with modifying the parameters on execution.

To run the tests with modified parameters:

```bash
./mvnw verify -Drun.load.tests=true -Djmeter.host.port=9090
```

Test results will be available at `/siva-parent/siva-test/target/jmeter/results/reports/`` folder

## Documentation

Read [SiVa documentation](http://open-eid.github.io/SiVa/)
