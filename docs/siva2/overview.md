<!--# SiVa overview-->

## What is SiVa?

SiVa (Signature Validation) web service
is continued development of PDF Validation web service. Service provides
a JSON and SOAP based API web interface which purpose is to validate signatures
in digitally signed BDOC, DDOC, PDF and X-Road ASiCE files according to
validation policy (described in the [Validation Policy](appendix/validation_policy)
section).

SiVa uses following Java libraries and command line utilities:

* EU DSS (Digital Signature Service) library is chosen for digitally signed
  PDF file validation
* DigiDoc4J Java library to validate BDOC containers. Supported signature
  types are `TimeStamp` and `TimeMark`
* JDigiDoc Java library is used to validate DDOC files starting from version
* X-Road ASiCE containers are validated using X-Road security server project
  provided command line utility

## Validation libraries

### DigiDoc4j EU DSS fork

DigiDoc4J EU DSS fork library for PDF files was chosen because it all the main
validation constrains already provided and all new constraints can be added easily.
For more information on EU DSS, see:
<https://joinup.ec.europa.eu/asset/sd-dss/description>.

**SiVa will use the following functionality of EU DSS library:**

* PaDES Validation Functionality
* TSL loading functionality

### DigiDoc4J

DigiDoc4J will be used to validate both `TimeMark` and `TimeStamp` based BDOC containers.
DigiDoc4J was chosen because it's only Java library that can validate Estonian BDOC files
according to SiVa validation policy.
For more information on DigiDoc4J:
<https://github.com/open-eid/digidoc4j>

SiVa will use the following functionality of DigiDoc4J:

* BDOC validation functionality

### JDigiDoc

JDigiDoc provides support for DDOC files the library was chosen because it provides most
complete support for all required DDOC versions.
Read more about JDigiDoc:
<https://github.com/open-eid/jdigidoc>

SiVa will use the following functionality of JDigiDoc:

* DDOC validation functionality

### X-Road signature validation utility

X-Road signature validation utility is command line tool to validate X-Road Security server
generated ASiCe files. The utility was chosen because it's only available packaged to tool
to validate X-Road signature files.

## Main features of SiVa validation service:

- SiVa SOAP ETSI compliant API to validate all supported signatures.
- SiVa REST ETSI compliant API to validate all supported signatures.
- SiVa handles files in PDF-format version 1.7 and later,
  signed with PadES-profile signatures.
- Service handles DDOC files starting from version 1.0 or later
- Service supports BDOC files starting from version 2.1 or later
- Service supports X-Road 6 security server ASiCE containers
- Service supports up to 10MB file size upload
- SiVa uses European Commission’s TSL (Trusted Service
  Status List) for certificate chain validation for PDF and BDOC files.
	- European Commission’s TSL contains references to TSLs of
	  European Union’s member states and members of the European
	  Economic Area. This allows the PDF Validator to validate
	  signature that has been signed with certificates issued in any
	  of European Union’s member states.
	- During the validation process, a certificate chain is created
	  from signer’s certificate up to the trust anchor (national trust
	  list referenced by the central European Commission's trust list)
	  for all certificates included in the signature (i.e. the
	  signer's certificate, OCSP service's certificate, time-stamping
	  Service's certificate).
- SiVas for DDOC and X-Road signature containers will use configured
  list certificates.
- Signatures with PadES-LT and PadES-LTA profile are supported.
- BDOC signatures with type BDOC-TM and BDOC-TS are supported
- SiVa extracts data files from DDOC containers.

At the time of creating the current documentation, it is expected that
SiVa will be used by the following applications:

- DigiDoc3 Client application
- Third party document management applications