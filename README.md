<img src="docs/img/siva/Co-funded_by_the_European_Union.jpg" width="350" height="200" alt="Co-funded by the European Union">

# Signature Verification Service

[![GitHub license](https://img.shields.io/badge/license-EUPLv1.1-blue.svg)](https://raw.githubusercontent.com/open-eid/SiVa/develop/LICENSE.md)

SiVa is digital signature validation web service that provides JSON API to validate following file types:

* Estonian DDOC containers
* Estonian BDOC containers with TimeMark and TimeStamp signatures
* Estonian ASiC-S containers with time stamp tokens
* ETSI standard based ASiC-E and ASiC-S containers
* ETSI standard based XAdES, CAdES and PAdES signatures
* ETSI standard based XAdES signatures with datafiles in hashcode form

### Libraries used in validation services

Below is list of Java libraries used for validation:

* [DigiDoc4J](https://github.com/open-eid/digidoc4j) - is used to validate DDOC and BDOC digital signature containers.
* [DigiDoc4J DSS fork](https://github.com/open-eid/sd-dss) - to validate all other digitally signed files.

## Requirements

These are minimum requirements to build and develop SiVa project:

* **git** - to easily download and update code. You can [download git here](https://git-scm.com/)
* **Java JDK 17** - to compile and run SiVa applications.
* **IDE** - to develop SiVa. We recommend to use [JetBrains IntelliJ](https://www.jetbrains.com/idea/)
* **2 GB of RAM** the RAM requirement is here because when building the project the integration tests take up a lot of memory
* Optionally You can also install **Maven** but it is not needed because SiVa project uses Maven wrapper to install maven

## How to build

### Using Maven Wrapper

Recommended way of building this project is using [Maven Wrapper](https://github.com/takari/maven-wrapper).
Run following command:

```bash
./mvnw clean install
```

After that, you can optionally create an image for Docker:

```bash
./mvnw spring-boot:build-image -pl siva-parent/siva-webapp -DskipTests
```

## How-to run

### With docker (for testing only)

Before continuing, the [siva-demo-application](https://github.com/open-eid/SiVa-demo-application) docker image must be built and available on Docker as `siva-demo-application:latest`.

> **Configuration note:** For configuration changes create an `application.yml` file inside the `docker` directory 
> and define all required configuration overrides there.

The following command will run siva-webapp along with siva-demo-application:

```
docker compose up
```

To remove all containers run:

```
docker compose down -v --remove-orphans
```

Now SiVa itself is accessible http://siva.localhost:8080/ and siva-demo-application http://siva-demo.localhost:9000/.
Logs for all running containers can be viewed at http://localhost:11080.

### Without docker

SiVa project compiles **1 fat executable JAR** file that You can run after successfully building the
project by issuing below command:

**Starting the SiVa REST webservice. NB! X.X.X denotes the version you are running.**

```bash
java -jar siva-parent/siva-webapp/target/siva-webapp-X.X.X-exec.jar
```

The SiVa webapp by default runs on port **8080**.

Easiest way to test out validation is to [start SiVa Demo Application without docker](https://github.com/open-eid/SiVa-demo-application#without-docker).

## WAR for an external servlet container

Not covered. The default build produces no WAR; the `war` Maven profile (`./mvnw -Pwar package`)
packages the webapp as one for a servlet container that provides the Servlet API of this Spring Boot
version. Everything else here and in the deployment guide assumes the executable JAR.

## How-to run tests

Unit test are integral part of the SiVa code base. The tests are automatically executed every
time the application is built. The build will fail if any of the tests fail.

To execute the tests from command line after application is built use:

```bash
./mvnw verify
```

### How to run system tests
System tests can be found [here](https://github.com/open-eid/SiVa-Test), and executing them requires running SiVa Web application instance.

PS! Some tests are expected to fail as they need a special test LOTL.

### How to run load tests

Load tests are available in the [SiVa-perftests](https://github.com/open-eid/SiVa-perftests) repository.

## Open source software used to build SiVa

Full list of open source Java libraries used to build SiVa can be found in our
[Open Source Software used page](OSS_USED.md)

## Documentation

Read [SiVa documentation](http://open-eid.github.io/SiVa/)
