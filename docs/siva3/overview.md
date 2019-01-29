<!--# SiVa overview-->

## What is SiVa?

SiVa (Signature Validation) web service provides JSON and SOAP based API web interface to validate digital signatures.
Please take a look in [Validation Policy](appendix/validation_policy) section for supported formats and applied constraints.

SiVa uses following Java libraries and command line utilities:

* DigiDoc4J Java library to validate BDOC (supported signature
  types are `TimeStamp` and `TimeMark`) and DDOC containers.
* X-Road ASiCE containers are validated using X-Road security server project
  provided command line utility
* EU DSS (Digital Signature Service) library is used to validate all other types of digital signatures that are not covered above.

## Validation libraries

### DigiDoc4j EU DSS fork

[DigiDoc4J EU DSS fork](https://github.com/open-eid/sd-dss) is used as the main validation library. The fork includes [Estonian specific changes](https://github.com/open-eid/sd-dss/wiki/BDoc-specific-modifications) and may not be suitable for all signatures.

**SiVa will use the following functionality of EU DSS library:**

* XAdES/CAdES/PAdES Validation Functionality
* ASIC-E and ASIC-S container validation
* TSL loading functionality

### DigiDoc4J

DigiDoc4J is used to validate both `TimeMark` and `TimeStamp` based BDOC and DDOC containers. For more information on DigiDoc4J visit [Github](https://github.com/open-eid/digidoc4j)

SiVa will use the following functionality of DigiDoc4J:

* BDOC validation functionality
* DDOC validation functionality

### X-Road signature validation utility

X-Road signature validation utility is command line tool to validate X-Road Security server
generated ASiCe files. For more information on this utility visit [GitHub](https://github.com/ria-ee/X-Road)

## Main features of SiVa validation service:

- SOAP and REST/JSON API to validate signatures.
- SOAP and REST/JSON API to retrieve data files from DDOC containers.
- SOAP API is compadible with X-Road v6.
- Signing of validation report.