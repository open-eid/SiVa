Appendix 3 - Validation Policy
==============================

In PDF Validator, the Validation Policy is configured in an XML-file called
constraint.xml. For each rule in Validation Policy, the result is OK or NOT OK.
The validation process is stopped when the first rule fails. Rules are grouped
into blocks. Each rule block will have a conclusion in the Detailed Report
section of the Service’s Response. If all rules in a block are met then
the conclusion node indicates VALID. Otherwise INVALID or INDETERMINATE
indication is returned depending on the ETSI standard definition.

Note: The statuses described here are for rule blocks and do not determine the
validity of the whole signature.

PDF-file
--------

* **Name**: PDF version
    * **Description**: PDF-document's version has to be 1.7 or higher
    * **Must / Should**: Must
    * **NFR-ID**: DSS-PDF-VERSION
* **Name**:  PDF standards
    * **Description**: PDF and PDF/A file standards are supported.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-PDF-STANDARD
* **Name**: File Extension
    * **Description**: The file to be checked must have a file extension .pdf
    * **Must / Should**: Must
    * **NFR-ID**: DSS-PDF-EXTENSION

Trust Anchors
-------------

* **Name**: Trust Anchors
    * **Description**: European Commission's Trust List is used as the default
      Trust List (LOTL) for certificate chain validation. The trust anchor is a
      set of certificates in a configuration file that are trusted for signing the
      Trust List.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-TA-EU
* **Name**: Default LOTL URL
    * **Description**: The default LOTL URL can be set via configuration file
    * **Must / Should**: Must
    * **NFR-ID**: DSS-TA-URL
* **Name**: Storing downloaded TSL lists
    * **Description**: The downloaded TSL lists are stored in local cache
    * **Must / Should**: Must
    * **NFR-ID**: DSS-TA-TSL_CACHE
* **Name**: Local TSL cache location
    * **Description**: The local TSL cache location can be set via configuration file
    * **Must / Should**:  Must
    * **NFR-ID**:  DSS-TA-LOCAL_TSL_LOC
* **Name**: Default TSL's signer's certificate
    * **Description**: The default TSL's signer’s certificate must be European Commission’s certificate.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-TA-DEF_TSL_CERT
* **Name**: TSL URL redirection
    * **Description**: TSL URL redirection is supported
    * **Must / Should**: Must
    * **NFR-ID**: DSS-TA-URL_REDIR
* **Name**: Service Information/ Service Type Identifier for each TSP Service
    * **Description**: For each of the TSPService in the TSL,
      the ServiceInformation/ServiceTypeIdentifier value must be one of the
      following (TS 119 612 „D.2.1 Service Type“):  
        * <http://uri.etsi.org/TrstSvc/Svctype/CA/QC>
        * <http://uri.etsi.org/TrstSvc/Svctype/NationalRootCA-QC>
        * <http://uri.etsi.org/TrstSvc/Svctype/TSA>
        * <http://uri.etsi.org/TrstSvc/Svctype/TSA/QTST>
        * <http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP>
        * <http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP/QC>
    * **Must / Should**: Must
    * **NFR-ID**:DSS-TA-TSP_INFO_TYPE_ID
* **Name**: Checking for SSCD
    * **Description**: If there is no SSCD (Secure Signature Creation Device)
      extension present in the signer’s certificate then the respective SSCD value
      must be checked in the TSL list. In this case, for the respective
      TSPService in the TSL, there must be the qualifier
      <http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCWithSSCD> present
      QualificationElement/Qualifier XML block (TS 119 612 v1.2.1 chap. 5.5.9).
      Validation of the qualifier’s criteria must be successful.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-TA-SSCD_CHECK
* **Name**: TSPService for OCSP responder and TSA certificate
    * **Description**:  If there is a TSPService registered in TSL representing
      OCSP/TSA Service then the OCSP/TSA certificate used in the signature should
      be in the list of trusted TSPServices.
      If there is no TSPService registered in TSL representing OCSP/TSA Service
      then  the OCSP/TSA certificate’s issuer certificate should be registered in
      the TSL.
    * **Must / Should**: Should
    * **NFR-ID**: DSS-TA-OCSP_TSA_TSP
* **Name**: Certificates' validity times compared to time when Service was added to TSL
    * **Description**: The signer certificate’s, OCSP responder certificate’s and
      TSA certificate’s “valid from” field’s value must be equal to or later
      than the respective TSPService’s earliest StatusStartingTime value (either
      current status or historical status), i.e. the time when the Service was
      added to TSL
    * **Must / Should**: Must
    * **NFR-ID**: DSS-TA-CERT_VALIDITY_TIMES
* **Name**: TSL Updating Interval
    * **Description**: Local TSL cache's updating interval is once per 24 hours.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-TA-TSL_UPDATE_INTERVAL

Signature Standards
-------------------

* **Name**: PAdES Signature Standards
    * **Description**: The signature must conform to PAdES Standards
      PAdES-LTV (ETSI TS 102 778-4 v1.1.2), PAdES Baseline-LT
      Profile (ETSI TS 103 172, v2.2.2) according to the signature’s level.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-SIG-STANDARD
* **Name**: PAdES Signature Standards (eIDAS)
    * **Description**: The signature should conform to
      PAdES Standard PAdES-E-LTV (eIDAS) according to the signature’s level.
      (Note: this requirement is a Must after eIDAS - 2016)
    * **Must / Should**: Should
    * **NFR-ID**: DSS-SIG_STANDARD_ELTV
* **Name**: Placement of signature
    * **Description**: The signature must be an enveloped signature (not
      detached and not enveloping).
    * **Must / Should**: Must
    * **NFR-ID**: DSS-SIG-ENVELOPE
* **Name**: Multiple signatures
    * **Description**: If PDF-document is signed by mutliple signers then each signature must be in separate signature dictionary (i.e. serial signature instead of parallel signature)
    * **Must / Should**: Must
    * **NFR-ID**: DSS-SIG-MULTISIG
* **Name**: Supported Signature Profiles
    * **Description**: PAdES-LT and PAdES-LTA signature profiles are supported
    * **Must / Should**: Must
    * **NFR-ID**: DSS-SIG-PROFILE

Time Stamps and Validity Confirmation
-------------------------------------

* **Name**: Signature without OCSP Validity Confirmation
    * **Description**: The signer certificate's revocation information must be
      added to the Signature in the form of an OCSP Validity Confirmation,
      i.e. CRLs are not supported.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-SIG-NO-OCSP
* **Name**: Time-Stamp in Signature
    * **Description**: The Signature must contain a signature Time-Stamp
      (signature-time-stamp attribute).
    * **Must / Should**: Must
    * **NFR-ID**: DSS-SIG-TIMESTAMP
* **Name**: All Certificates valid during Signature's Time-Stamp
    * **Description**: All Certificates that are included in Signature must be
      valid at the time of Time-Stamp's issuance time (i.e. every certificate
      must meet the condtion: Certificate.validFrom <= Time-Stamp's genTime
      value <= Certificate.validTo). If this requirement is not met then the
      validation of the signature fails.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-SIG-VALID_CERTS
* **Name**: OCSP Validity Confirmation After Time-Stamp's time
    * **Description**: OCSP Validity Confirmation's producedAt field must
      contain a time that is later than signature Time-Stamp's time.
      If this requirement is not met then the validation of the signature fails. 
      Note: this requirement is still being analyzed. Possible other
      implementation - OCSP producedAt may be earlier than signature's
      Time-Stamp's time - by some pre-determined period.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-SIG-VALID-OCSP-TIME
* **Name**: Warning - OCSP Validity Confirmation later than Time-Stamp's time
    * **Description**: If the OCSP Validity Confirmation's time is
      later than signature Time-Stamp's time by more than 15 minutes
      then a warning is returned
    * **Must / Should**: Must
    * **NFR-ID**: DSS-SIG-OCSP-TS-WARN
* **Name**: Exception - OCSP Validity Confirmation later than Time-Stamp's time
    * **Description**: If the OCSP Validity Confirmation's time is later than
      signature Time-Stamp's time by more than 24 hours then validation of
      the signature fails.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-SIG-OCSP-TS-ERROR
* **Name**: No Checking of Freshness of Revocation Status
    * **Description**: For all certificates, there will be no checking of Freshness
      of Revocation Status (the corresponding configuration parameter
      will be set to 0 in constraint file)
    * **Must / Should**: Must
    * **NFR-ID**: DSS-SIG-NOREVOFRESH
* **Name**: No new OCSP/CRL data acquiring
    * **Description**: Validation is done based on existing revocation data
      present in the signature - i.e. no additional revocation data is acquired
      from external sources during validation time.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-SIG-NONEWREVDATA

Certificates
------------

* **Name**: Certificate Chain
    * **Description**: Certificate chain must be traceable up to the trust anchor (national Trust List referenced by the central European Commission's Trust List) for all certificates included in the signature (i.e. the signer's certificate, OCSP Service's certificate, time-stamping Service's certificate).
    * **Must / Should**: Must
    * **NFR-ID**: DSS-CRT-CHAIN
* **Name**: Signer's Certificate - QC
    * **Description**: The Signer's Certificate must be a qualified certificate.
    * **Must / Should**:Must
    * **NFR-ID**: DSS-CRT-SIGNER_QC
* **Name**: Signature created with SSCD
    * **Description**: The signature must be created with a secure signature creation device (SSCD), the SSCD criteria must verifiable based on the data in the Signer's Certificate or in the TSL.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-CRT-SSCD
* **Name**: Non-Repudiation
    * **Description**: The signer's certificates must have Non-Repudiation bit set in "Key Usage"-field (for all supported countries and certificates).
    * **Must / Should**: Must
    * **NFR-ID**: DSS-CRT-NONREPU

Cryptography
------------

* **Name**: Forbidden Encryption Algorithms
    * **Description**: DSA Encryption Algorithm is not allowed in Signature
    * **Must / Should**: Must
    * **NFR-ID**: DSS-CRY-NA-ENCR
* **Name**: Forbidden Hash Algorithms
    * **Description**: RIPEMD Hash Algorithm is not allowed in Signature
    * **Must / Should**: Must
    * **NFR-ID**: DSS-CRY-NA-HASH
* **Name**: Allowed Hash algorithms
    * **Description**: Hash algorithms SHA-1 or better are supported - SHA-1, SHA-224, SHA-256, SHA-384, SHA-512.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-CRY-HASH
* **Name**: Weak Hash Algorithm Warning
    * **Description**: In case of hash algorithm SHA-1 a warning is included in Validation Service's Response.
    * **Must / Should**: Must
* **Name**: RSA Key Length
    * **Description**: RSA key length must be at least 1024 bits.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-CRY-RSAKEY
* **Name**: ECDSA Key Length
    * **Description**: ECDSA length must be at least 224 bits.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-CRY-ECDSAKEY
* **Name**: Cryptographic Algorithm Expiration Dates
    * **Description**: Supported cryptographic algorithms have the following expiration dates:
        * Algorithms under 112 bit strength:
            * SHA1 - 2017-02-24
            * RSA1024 - 2017-02-24
            * RSA1536 - 2017-02-24
        * Algorithms with ~ 112 bit strength:
            * SHA224 - 2025-02-24
            * RSA2048 - 2025-02-24
            * ECDSA224 - 2025-02-24
        * Algorithms with ~ 128 bit strength:
            * SHA256 - 2030-02-24
            * RSA3072 - 2030-02-24
            * RSA4096 - 2030-02-24
            * ECDSA256 - 2030-02-24
        * Algorithms with ~ 192 bit strength:
            * SHA384 - 2035-02-24
        * Algorithms with ~ 256 bit strength:
            * SHA512 - 2035-02-24
    * **Must / Should**: Must
    * **NFR-ID**: DSS-CRY-ALGOEXP


 




