!!! development
    ** Unclear items that are not yet present in the plan **

    * Testing of logs
    * Testing of user statistics
    * Configuration/administration of the service
    * SOAP API testing
    * System Testing of X-Road service

# SiVa Test Plan

This is living document and will be constantly updated as the project evolves. The aim of the document is to describe what is tested during SiVa Web Apllication development.

## Integration Test introduction

Overview of the SiVa (Signature Validation) web service can be found in the [Overview](/siva/overview/) section of the document.

This section of the document gives overview of Integration Testing carried out on SiVa web service.

Integration testing is using RestAssured library v2.5.0 to implement automatic checks for REST/SOAP based tests.

The testing of the SiVa web service is divided into sections based on the software architecture and functionalities provided to the users. The sections are:

  * REST API
  * SOAP API
  * DDOC signature validation
  * BDOC signature validation
  * PDF signature validation
  * X-Road ASICE signature validation

The goal is to focus testing on functionalities implemented in SiVa application. Functionalities provided by [Validation libraries](/siva/overview/#validation-libraries) are not explicitly tested.

## Testing of REST API

The goal of the REST API testing is to check that the API is accepting the requests based on the specification and the output result (Validation Report) is in correct format and has all the required elements.

### Validation REST API tests

Following areas are tested on input:

  * Wrong (not accepted) values in input parameters
  * Empty values in input paramters
  * Too many parameters
  * Too few parameters
  * Inconsistencies on stated parameters and actual data (wrong document type)
  * Case insensitivity on parameter names
  * Empty request
  * Simultaneous validation requests


In all of the negative cases correctness of returned error message is checked.

Specific test cases and input files can be found in:

  * [Appendix 5 - ValidationRequestTests.java](/siva/appendix/test_cases/#validationrequesttestsjava)
  * [Appendix 5 - DocumentFormatTests.java](/siva/appendix/test_cases/#documentformattestsjava)



### Validation Report tests

SiVa web service returns uniform Validation Report on all the supported document types. This also includes correct document types without actual signature (for example PDF document without signature).

Following areas are tested on output (Validation Report):

  * JSON structure on DDOC, BDOC, PDF and ASICE document types
  * Presence of the mandatory elements on DDOC, BDOC, PDF and ASICE document types
  * Presence of optional elements on DDOC, BDOC, PDF and ASICE document types
  * JSON structure on containers without signatures

**What is not tested:**

  * Correctness of the values in the report is not in scope on these tests

Specific test cases and input files can be found in:

  * [Appendix 5 - ValidationReportJsonStructureVerification.java](/siva/appendix/test_cases/#validationreportjsonstructureverificationjava)

## Testing of SOAP API

!!! development
    Will be covered when SOAP API is implemented


## Testing of DDOC signature validation

The goal of the DDOC signature validation testing is to check that the validation results given by JDigiDoc library are properly presented in validation report.

The testing of DDOC signatures consists of following main cases:

  * Containers with valid signature(s) are validated.
  * Containers with invalid signature(s) or no signature are validated
  * Containers sizes near maximum are validated
  * Containers with DDOC v1.0 - 1.3 are validated

Specific test cases and input files can be found in:

  * [Appendix 5 - DdocValidationFail.java](/siva/appendix/test_cases/#ddocvalidationfailjava)
  * [Appendix 5 - DdocValidationPass.java](/siva/appendix/test_cases/#ddocvalidationpassjava)
  * [Appendix 5 - LargeFileTests.java](/siva/appendix/test_cases/#largefiletestsjava)

**What is not tested:**

  * Verification of different causes in container for invalid result is out of scope.

!!! development
    Insert links to JDOC repos where these were tested.


## Testing of BDOC signature validation

The goal of the BDOC signature validation testing is to check that the validation results given by DigiDoc4J library are properly presented in validation report.

The testing of BDOC signatures consists of following main cases:

  * Containers with valid signature(s) are validated (how many signatures are acceptable?)
  * Containers with invalid signature(s) or no signature are validated
  * Containers sizes near maximum are validated

Specific test cases and input files can be found in:

  * [Appendix 5 - BdocValidationFail.java](/siva/appendix/test_cases/#bdocvalidationfailjava)
  * [Appendix 5 - BdocValidationPass.java](/siva/appendix/test_cases/#bdocvalidationpassjava)
  * [Appendix 5 - LargeFileTests.java](/siva/appendix/test_cases/#largefiletestsjava)

**What is not tested:**

  * Verification of different causes in container for invalid result is out of scope.

!!! development
    Insert links where Digidoc4J repos where these were tested.

## Testing of PDF signature validation

Portion of the validation rules for PDF documents are implemented in SiVa web apllication itself. Therefor different test area selection is used for PDF compared to other containers.

The testing of PDF signatures consists of following main cases:

  * Containers with invalid signature(s) (different reasons for failure) are validated
  * Containers with no signature are validated
  * Containers sizes near maximum are validated
  * Containers with different baseline profiles are validated
  * Containers with serial and parallel signatures are validated
  * Containers with different signature cryptocaphic algorithms are validated
  * Containers with OCSP values inside and outside bounds are validated

Specific test cases and input files can be found in:

  * [Appendix 5 - PdfBaselineProfileTests.java](/siva/appendix/test_cases/#pdfbaselineprofiletestsjava)
  * [Appendix 5 - PdfSignatureCryptographicAlgorithmTests.java](/siva/appendix/test_cases/#pdfsignaturecryptographicalgorithmtestsjava)
  * [Appendix 5 - PdfValidationFail.java](/siva/appendix/test_cases/#pdfvalidationfailjava)
  * [Appendix 5 - PdfValidationPass.java](/siva/appendix/test_cases/#pdfvalidationpassjava)
  * [Appendix 5 - SignatureRevocationValueTests.java](/siva/appendix/test_cases/#signaturerevocationvaluetestsjava)
  * [Appendix 5 - LargeFileTests.java](/siva/appendix/test_cases/#largefiletestsjava)

## Testing of X-Road ASICE signature validation

The goal of the ASICE signature validation testing is to check that the validation results given by X-Road signature validation utility are properly presented in validation report.

It is possible to validate ASICE containers both via SOAP and REST interface. The same test cases are used for both interfaces.

The testing of ASICE signatures consists of following main cases:

  * Containers with valid signature(s) are validated
  * Containers with invalid signature(s) and no signature are validated
  * Containers sizes near maximum are validated

Specific test cases and input files can be found in [Appendix 5 - Test Case Descriptions](/siva/appendix/test_cases/)

**What is not tested:**

  * Verification of different causes in container for invalid result is out of scope.


## System Test introduction

!!! development
    Will be covered before System Test start

While Integration Tests were carried out automatically then System Testing will be done manually.

System testing is carried out using two access points:

  * Testing through SiVa Sample Application
  * Testing through X-Road Security Server

The goal is to test the whole validation process.

## SiVa Sample Application tests

In addition to testing the service as such SiVa Sample Application itself is tested. The main cases are:

  * Cross browser usage (IE, Edge, Chrome, Firefox and Safari)
  * File upload (different sizes, suported and unsupported file types)
  * Displayment of Validation Report

## X-Road Application tests


!!! development
    Will be covered on System Test phase

## Performance Test introduction

Performance testing will be carried out on following environments:

  * Local machine (processor: memory: )
  * Amazon cloud? (processor: memory: )
  * RIA test enviroment? (processor: memory: )

Jmeter v2.13 is used to carry out the testing.

The goal is to measure throughput-latency of the service with different file types and sizes. The performance testing is carried out on REST interface. SOAP interface is used only for one testrun for comparison between SOAP and REST interface. It is assessed that the interface itself does not have considerable impact on throughput or latency compared to the validation process.

Following cases will be covered on all supported file types (BDOC, DDOC, ASICE, PDF) with steps of 5, 10, 20, 30, 50 and 60 requests per second:

  * The service is loaded with valid under 1MB containers
  * The service is loaded with valid around 5MB containers
  * The service is loaded with valid near 10MB containers
  * The service is loaded with valid containers with many (how many?) signatures
  * The service is loaded with invalid signature under 1MB containers

In addition, following case will be covered to simulate the real life enviroment with steps of 5, 10, 20, 30, 50 and 60 requests per second:

  * The service is loaded with mixture of different file types and valid/invalid signatures

These tests run for a X? hour/minutes each. The results are presented as throughput-latency graphs for each run.