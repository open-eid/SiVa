<!--# SiVa Known Issues-->

## General issues

1.	German, Norwegian and Croatian TSLs are not loaded, signatures containing certificates included in the respective TSL lists can not be validated.
2.	During TSL refreshing/reloading, service could give wrong validation results. Service must be turned off for TSL refreshing time (at  3:00 AM, ca 1 minute). 
3.	X-Road validation service is separated (do not follow general architectuse) because of different BouncyCastle version used in X-Road utility. X-Road validation module should use latest BouncyCastle version.
4.	Container validation errors are not returned as the current DSS-based validation report doesn't include a proper structure for container errors: [https://esig-dss.atlassian.net/browse/DSS-932](https://esig-dss.atlassian.net/browse/DSS-932)

## DIGIDOC-XML related issues

1.	(JDigiDoc) Validating DIGIDOC-XML 1.0..1.3 documents is supported in case of "EMBEDDED_BASE64" content type, i.e. "DETACHED" and "EMBEDDED" content types are not supported. 
2.	(DigiDoc4j, JDigiDoc) Investigate JDD DDOC 1.0 validation issue in DD4j 1.0.4: [https://www.pivotaltracker.com/story/show/125537231](https://www.pivotaltracker.com/story/show/125537231)
3.	(JDigiDoc) DDOC validation is not thread safe, added synchronization block, only one request at time: [https://www.pivotaltracker.com/story/show/86696334](https://www.pivotaltracker.com/story/show/86696334) and [https://github.com/open-eid/digidoc4j/wiki/Questions-&-Answers#using-ddoc-containers-in-multi-threaded-environment](https://github.com/open-eid/digidoc4j/wiki/Questions-&-Answers#using-ddoc-containers-in-multi-threaded-environment)

## BDOC/ASiC-E related issues

1.	(DigiDoc4j) Subindication is missing: [https://www.pivotaltracker.com/story/show/128947235](https://www.pivotaltracker.com/story/show/128947235)
2.	(DigiDoc4j) Improve API to return validation parameters: [https://www.pivotaltracker.com/story/show/123789261](https://www.pivotaltracker.com/story/show/123789261)
3.	(DigiDoc4j) return indeterminate [https://www.pivotaltracker.com/story/show/129437879](https://www.pivotaltracker.com/story/show/129437879) 
4.	(DigiDoc4j) Improve validation report: [https://www.pivotaltracker.com/story/show/118940113](https://www.pivotaltracker.com/story/show/118940113)
5.	(DigiDoc4j, DSS) Fix detecting signature qualification level based on TL information: [https://www.pivotaltracker.com/story/show/128061699](https://www.pivotaltracker.com/story/show/128061699)
6.	(DigiDoc4j, DSS) DSS doesn't properly validate manifest.xml, including not checking that data files in container would be listed in manifest.xml: [https://esig-dss.atlassian.net/browse/DSS-842](https://esig-dss.atlassian.net/browse/DSS-842), [https://esig-dss.atlassian.net/browse/DSS-932](https://esig-dss.atlassian.net/browse/DSS-932)
7.	(DigiDoc4j, DSS) OCSP revocation errors are not included in simple report: [https://esig-dss.atlassian.net/browse/DSS-922](https://esig-dss.atlassian.net/browse/DSS-922), [https://www.pivotaltracker.com/n/projects/1110130/stories/129526189](https://www.pivotaltracker.com/n/projects/1110130/stories/129526189)
8.	(DigiDoc4j, DSS) Validation warning is not returned when SHA-1 is used in the signature [https://www.pivotaltracker.com/story/show/129929643](https://www.pivotaltracker.com/story/show/129929643), [https://esig-dss.atlassian.net/browse/DSS-760](https://esig-dss.atlassian.net/browse/DSS-760)
9.	(DigiDoc4j) In case of BDOC with time-stamp, the validation warning is not returned if time difference between time-stamp and OCSP issuance times exceeds 15 minutes: [https://www.pivotaltracker.com/story/show/130098445](https://www.pivotaltracker.com/story/show/130098445)

## PAdES related issues

1.	(DSS) PAdES validation is not thread safe, added synchronization block, only one request at time: [https://esig-dss.atlassian.net/browse/DSS-923](https://esig-dss.atlassian.net/browse/DSS-923)
2.	(DSS) Validation warning is not returned when SHA-1 is used in the signature: [https://esig-dss.atlassian.net/browse/DSS-760](https://esig-dss.atlassian.net/browse/DSS-760)
3.	(DSS) Documents signed with revoked signature pass the validation: [https://esig-dss.atlassian.net/browse/DSS-922](https://esig-dss.atlassian.net/browse/DSS-922), [https://esig-dss.atlassian.net/browse/DSS-918](https://esig-dss.atlassian.net/browse/DSS-918)

## X-Road signatures' related issues
1.	(X-Road) Values of some validation report fields are not accessible via the X-Road library's API:  
2.	SignatureFormat – Format and profile (according to Baseline Profile) of the signature. Currently, hard coded values are used in the report. 
3.	SignatureLevel – Indication whether the signature is Advanced electronic Signature (AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES). Currently, the value is left empty, as the library doesn't enable determining legal level of the signature then POLv2 validation policy (only QES level signatures are accepted) is not supported for X-Road signatures. 
4.	Indication, Errors and Warnings - in error situations, the verification process results in a CodedException extends RuntimeException. This Exception always contains a fault code String but it’s how should we determine if we should result in INDETERMINATE or TOTAL-FAILED result? Also since the rest of the verification process is not completed after the exception we only ever know the single error that was reached first. Also we can’t get warnings as we do in DSS.
5.	SubIndication – Additional subindication in case of failed or indeterminate validation result, according to EN 319 102-1 "Table 6: Validation Report Structure and Semantics".  
6.	SignatureScope - Contains information of the original data that is covered by the signature. Currently, a hard-coded value is used.  
7.	ClaimedSigningTime - Claimed signing time, i.e. user’s computer time during signature creation.
