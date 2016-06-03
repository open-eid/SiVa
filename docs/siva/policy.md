Validation Policy
=================

The validation of a Digital Signature in PDF is driven by a set of rules
called a Validation Policy - a set of constraints that are applied to
cryptographic data during the execution of the Signature Validation
Process. For the most part, validation proceeds according to EU
standards and norms using the underlying DSS library. However, the
Service adds some additional constraints to comply with Estonian law and
procedures. The main differences from the rest of the Europe are
following:

- The service requires a reasonable guarantee that the signing
  certificates were not temporarily stopped during the time
  of signing. Two different checks facilitate ensuring this
  requirement:
	- Signature level must be at “LT” or “LTA”. The signature must
	  already contain an OCSP response and a signature timestamp (the
	  service will not attempt to request new OCSP or timestamp tokens
	  during validation).
	- The provided OCSP response must be after the signature
	  timestamp, but not more than 24 hours later. A warning is given
	  if the time difference is more than 15 minutes but less than 24 hours.
- Certificate Revocation Lists (CRL) are not supported; OCSP protocol is mandated.
- The cryptographic algorithms DSA and RIPEMD are not supported;
  allowed key lengths of other algorithms have been changed in
  accordance with Estonian requirements.
- Signing certificates must explicitly state that they were meant to
  be used for digital signatures via the use of the non-repudiation flag.

Validation Policy is described in more detail in „Appendix 3 -
Validation Policy“. Validation Policy configuration is shown in
„Appendix 4 - Validation Constraint Configuration“. Validation Process’s
result example is shown Detailed Report section in „Appendix 8 - Sample
Response Data Blocks - positive response“.


