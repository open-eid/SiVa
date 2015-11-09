Appendix 10 - Summary of modifications made to the DSS library
==============================================================

There were requirements that required forking of the EU DSS library (at least 
for the time being). Here is a summary of such changes that have now been made to 
the DSS fork (these changes largely overlap with Estonian validation constraints).

* Signature level must be at “LT” or “LTA”. The signature must already contain an OCSP response 
  and a signature timestamp (the service will not attempt to request new OCSP or timestamp tokens during validation).
* The provided OCSP response must be after the signature timestamp, but not more than 24 hours later. 
  A warning is given if the time difference is more than 15 minutes but less than 24 hours.
* Certificate Revocation Lists (CRL) are not supported; OCSP protocol is mandated.
* Turned off unnecessary functionality (the service only allows only PDF files, not ASiC). This can later be turned back on as needed.
* Validation output now includes the certificates used in the signing process.
* Monitoring support also required small changes to be made in the library to provide more diagnostic info about TSL loading process.
