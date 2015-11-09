Appendix 2 - Functional Requirements
=======================================

* **Name**: OCSP Checking Disabled
    * **Description**: Checking of Certificate's revocation information in
      OCSP is disabled in PDF Validation Service
    * **Must / Should**: Must
    * **FR-ID**: DSS-FR-OCSP
* **Name**: CRL Checking Disabled
    * **Description**: Checking of Certificate's revocation information in CRL is
      disabled in PDF Validation Service
    * **Must / Should**: Must
    * **FR-ID**: DSS-FR-CRL
* **Name**: Multiple signature validation
    * **Description**: IF PDF-document's signature dictionary contains multiple
      signatures, then the Validation Service validate all signatures in document.
    * **Must / Should**: Must
    * **FR-ID**: DSS-FR-MULTISIGN
* **Name**: Signature Not Conforming to Policy / NFR
    * **Description**: In case the Signature does not meet the Rules in Validation
      Policy of NFR, an error is returned in response, containing:
        * error message
        * information about possible cause of error.
    * **Must / Should**: Must
    * **FR-ID**: DSS-FR-SIG_ERROR
* **Name**: PDF Not Conforming to Policy / NFR
    * **Description**: In case the PDF-file does not meet the Rules in Validation
      Policy of NFR, an error is returned in response, containing:
        * error message
        * information about possible cause of error.
    * **Must / Should**: Must
    * **FR-ID**: DSS-FR-PDF_ERROR
* **Name**: Online TSL Renewal
    * **Description**: The Service has to be able to renew TSL information
      from Online Sources
    * **Must / Should**: Should
    * **FR-ID**: DSS-FR-ONLINE_TSL
* **Name**: Cached TSL
    * **Description**: The Service has to be able to use Cached TSL data
      when checking for Certificate Validities.
    * **Must / Should**: Should
    * **FR-ID**: DSS-FR-CACHED_TSL
* **Name**: Automatic TSL list’s validity check
    * **Description**: The Service must check the Validity of TSL at preconfigured
      intervals. Initial TSL checking interval: 24 hours.
    * **Must / Should**: Must
    * **FR-ID**: DSS-FR-TSL_AUTOCHECK
* **Name**: TSL Certificate Checking
    * **Description**: TSL's certificate's must be validated - one of 3 predefined
      certificates must be used for signing TSL.
    * **Must / Should**: Must
    * **FR-ID**: DSS-FR-TSL_CERT_VALIDATE
* **Name**: Verification Process Cancelling
    * **Description**: Verification process can be cancelled only in
      case of fatal errors (it is not possible to parse the file)
    * **Must / Should**: Should
    * **FR-ID**: DSS-FR-CANCEL

