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
* Optionally You can also install **Maven** but it is not needed because SiVaa project uses Maven wrapper to install maven

## How to build

### Using Maven Wrapper

Recommended way of building this project is using [Maven Wrapper](https://github.com/takari/maven-wrapper) to build it.
Run following command:

```bash
./mvnw clean install 
```

## How to run

SiVa project compiles **fat executable JAR** files that You cna run after successfully building the
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

Now point Your browser to URL: http://localhost:9000

![Sample of validation result](https://raw.githubusercontent.com/open-eid/SiVa/develop/docs/img/siva_sample_validation.png)

## Documentation

Read [SiVa documentation](http://open-eid.github.io/SiVa/)
