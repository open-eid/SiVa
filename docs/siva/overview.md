# SiVa overview

SiVa (Signature Validation) web service (hereinafter – the Service) 
is continued development of PDF Validation web service. Service provides 
a JSON and SOAP based API web interface which purpose is to validate signatures 
in digitally signed BDOC, DDOC, PDF and X-Road ASiCE files according to 
laws of the Republic of Estonia 
(described in the [Validation Policy](appendix/validation_policy) section).

The Service uses following Java libraries and command line utilities:

* EU DSS (Digital Signature Service) library is chosen for digitally signed 
  PDF file validation 
* Customized Validation Policy constraints that will verify the signed
  PDF-file's conformity to laws of Republic of Estonia.
* DigiDoc4J Java library to validate BDOC containers. Supported signature  
  types are `TimeStamp` and `TimeMark`
* JDigiDoc Java library is used to validate DDOC files starting from version
  1.3
* X-Road ASiCE containers are validated using X-Road security server project
  provided command line utility

## Validation libraries

### EU DSS

EU DSS library for PDF files was chosen as it has already been used in DigiDoc4J
library (where it was chosen for having the most complete functionality
compared to other Java libraries). For more information on EU DSS, see
<https://joinup.ec.europa.eu/asset/sd-dss/description>.

The Service will use the following functionality of EU DSS library:

* PaDES Validation Functionality

## Main features of SiVa validation service:

- SiVa SOAP ETSI compliant API to validate all supported file types.
- SiVa REST ETSI compliant API to validate all supported digitally signed
  document types
- The Service handles files in PDF-format version 1.7 and later,
  signed with PadES-profile signatures.
- Service handles DDOC files starting from version 1.3 or later
- Service supports BDOC files starting from version 2.1 or later
- Service supports X-Road 6 security server ASiCE containers
- Multiple Signatures are supported.
- The Service uses European Commission’s TSL (Trusted Service
  Status List) for certificate chain validation.
	- European Commission’s TSL contains references to TSLs of
	  European Union’s member states and members of the European
	  Economic Area. This allows the PDF Validator to validate
	  signature that has been signed with certificates issued in any
	  of European Union’s member states.
	- During the Validation Process, a Certificate Chain is created
	  from Signer’s Certificate up to the Trust Anchor (national Trust
	  List referenced by the central European Commission's Trust List)
	  for all certificates included in the signature (i.e. the
	  signer's certificate, OCSP Service's certificate, time-stamping
	  Service's certificate).
- Signatures with PadES-LT and PadES-LTA profile are supported.
- BDOC signatures with type BDOC-TM and BDOC-TS are supported 
- The Signature must contain OCSP confirmation that meets the
  Service’s requirements.

At the time of creating the current documentation, it is expected that
the Service will be used by the following applications:

- DigiDoc3 Client application
- Third party document management applications