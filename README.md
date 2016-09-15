# Signature Verification Service

[![Build Status](https://travis-ci.org/open-eid/SiVa.svg?branch=develop)](https://travis-ci.org/open-eid/SiVa)
[![Coverage Status](https://coveralls.io/repos/github/open-eid/SiVa/badge.svg?branch=develop)](https://coveralls.io/github/open-eid/SiVa?branch=develop)
[![GitHub license](https://img.shields.io/badge/license-GPLv2-blue.svg)](https://raw.githubusercontent.com/open-eid/SiVa/develop/LICENSE.md)

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

Below is list of Java libraries we use and for which digitally signed document format we use it for:

* [JDigiDoc](https://github.com/open-eid/jdigidoc) - is used to validate Estonian older digital
  signature format called DDOC
* [DigiDoc4J](https://github.com/open-eid/digidoc4j) - is used to validate BDOC digital signature container
  that are compliant with ASiCE standard
* [DigiDoc4J DSS fork](https://github.com/open-eid/sd-dss) - to validate digitally signed PDF files that
  comply with Estonian laws
* [asicverifier](https://github.com/vrk-kpa/xroad-public/tree/master/src/asicverifier) is used to validate 
  XRoad signature containers

## Requirements

These are minimum requirements to build and develop SiVa project:

* **git** - to easily download and update code. You can [download git here](https://git-scm.com/)
* **Oracle Java JDK** - to compile and run SiVa applications. Download link for [Oracle Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
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

## How-to run

SiVa project compiles **3 fat executable JAR** files that You can run after successfully building the
project by issuing below commands:

**First start SiVa REST and SOAP web service**

```bash
./siva-parent/siva-webapp/target/siva-webapp-2.0.2-SNAPSHOT.jar
```

**Second we need to start SiVa XRoad validation service**

```bash
./validation-services-parent/xroad-validation-service/target/xroad-validation-service-2.0.2-SNAPSHOT.jar
```

The SiVa webapp by default runs on port **8080** and XRoad validation service starts up on port **8081**.
Easiest way to test out validation is run SiVa demo application.

**Start SiVa Demo Application**

```bash
./siva-parent/siva-sample-application/target/siva-sample-application-2.0.2-SNAPSHOT.jar
```

Now point Your browser to URL: <http://localhost:9000>

![Sample of validation result](https://raw.githubusercontent.com/open-eid/SiVa/develop/docs/img/siva-responsive.png)

## Quick start using Docker

There are unofficial Docker images You can use to test out SiVa. These images are created only for testing and demo
purposes.

> **NOTE** We use these images in SiVa project to run our performance tests

There two ways to run SiVa docker images. First option is use `docker-compose` and SiVa project provided
`docker-compose.yml` file:

```yaml
version: "2"
services:
  siva-xroad-validation:
    image: mihkels/xroad-validation-service
    ports:
      - "8081:8081"
  siva-webapp:
    image: mihkels/siva-webapp
    links:
      - siva-xroad-validation
    ports:
      - "8080:8080"
    environment:
      SIVA_PROXY_XROAD_URL: http://siva-xroad-validation:8081
  siva-sample-application:
    image: mihkels/siva-sample-application
    links:
      - siva-webapp
    ports:
      - "9000:9000"
    environment:
      SIVA_SERVICE_SERVICE_HOST: http://siva-webapp:8080
```

Issue below command in same directory where You saved `docker-compose.yml` file:

```bash
docker-compose rm -f && docker-compose pull && docker-compose up
```

> NOTE: You can use `-d` flag start in daemon mode instead of interactive when doing `docker-compose up`

Second option is to run containers directly using [Docker](https://www.docker.com/products/docker). Running directly
is good option when You want test out each service on its own.

Below is example of starting SiVa REST/SOAP web service:

```bash
docker run -it -p 8080:8080 mihkels/siva-webapp
```

> NOTE: You can stop the service by pressing `Ctrl+C`

## How-to run tests

Unit and integration tests are integral part of the SiVa code base. The tests are automatically executed every time the application is built. The build will fail if any of the tests fail.

To execute the tests from command line after application is built use:

```bash
./mvnw verify
```

### How to run load tests

Load tests are disabled by default, but can be enabled with maven parameter `-DrunLoadTests=true`. By default all unit
and integration tests will be executed prior the load tests, but it is possible to skip them. When executing the load tests, SiVa
Web application has to be started before the tests are executed.

> **Note**: PDF load test files contain test certificates. In order for PDF load tests to succeed
> SiVa application should be started with test certificates preloaded.

To load trusted test certificates in addition to TSL, "test" spring profile should be activated at startup, for example:

```bash
java -Dspring.profiles.active=test -jar siva-webapp-2.0.2-SNAPSHOT.jar
```

To run load tests after unit and integration tests in non GUI mode:

```bash
./mvnw verify -DrunLoadTests=true
```

To run load tests only:

```bash
./mvnw verify -DskipTests=true -DrunLoadTests=true
```

To run load tests with JMeter GUI execute the command in `Siva/siva-parent/siva-test/` folder:

```bash
mvn jmeter:gui  -DrunLoadTests=true
```

It is possible to configure following parameters in load test (given defaults are based on `../siva-test/pom.xml`):

  * `jmeter.host.name` - target webapp host against what the tests are executed, default is localhost
  * `jmeter.host.port` - target port of the webapp host , default is 8080
  * `jmeter.host.timeout` - response waiting timeout, default is 60000 (in milliseconds)
  * `jmeter.testfiles.dir` - directory of the test files, default is ${project.basedir}/src/test/jmeter/test-files
  * `jmeter.load.step.duration` - time how long the load is kept on each throuput level, default is 60 (in seconds)

These values can be set in three different ways:
  * In JMeter test plan - these settings will be used when JMeter GUI is used to run the tests
  * In `../siva-test/pom.xml` file - these settings will be used when the tests are run in non GUI mode and will overwrite the default values in test plans.
  * As parameters when executing the tests - These values have highest priority and will overwrite other default values.

To run the tests with modified parameters:

```bash
./mvnw verify -Drun.load.tests=true -Djmeter.host.port=9090
```

Test results will be available at `/siva-parent/siva-test/target/jmeter/results/reports/` folder

## Documentation

Read [SiVa documentation](http://open-eid.github.io/SiVa/)
