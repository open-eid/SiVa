# SiVa overview

SiVa (Signature Validation) web service (hereinafter – the Service) 
is a JSON and SOAP based service which purpose is to validate signatures 
in digitally signed BDOC, DDOC, PDF and X-Road ASiCE files according to 
laws of the Republic of Estonia 
(described in the [Validation Policy](appendix/validation_policy) section).

The Service uses

* EU DSS (Digital Signature Service) library as Core Library
* Customized Validation Policy constraints that will verify the signed
  PDF-file's conformity to laws of Republic of Estonia.

EU DSS library was chosen as it has already been used in DigiDoc4J
library (where it was chosen for having the most complete functionality
compared to other Java libraries). For more information on EU DSS, see
<https://joinup.ec.europa.eu/asset/sd-dss/description>.

The Service will use the following functionalities of EU DSS library:

* Validation Functionality

Main features of the Service:

- EU DSS SOAP API with some additional information (Signer’s
  certificate etc).
- The Service handles files in PDF-format version 1.7 and later,
  signed with PadES-profile signatures.
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
- The Signature must contain OCSP confirmation that meets the
  Service’s requirements.

At the time of creating the current documentation, it is expected that
the Service will be used by the following applications:

- DigiDoc3 Client application
- Third party document management applications