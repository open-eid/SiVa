<!--# Interface description-->

## REST JSON validation service

### REST JSON Endpoint

```
POST https://<server url>/validate
```

### JSON Request

+-------------------+--------+-----------+------------------------------------------------------------+
| Parameter         | Type   | Mandatory | Description                                                |
+===================+========+===========+============================================================+
| `document`        | String | +         | Base64 encoded string of digitally signed document         |
+-------------------+--------+-----------+------------------------------------------------------------+
| `filename`        | String | +         | File name of the digitally signed document                 |
|                   |        |           | (i.e `sample.bdoc`)                                        |
+-------------------+--------+-----------+------------------------------------------------------------+
| `documentType`    | String | +         | Validation service to use for validation                   |
+-------------------+--------+-----------+------------------------------------------------------------+
| `signaturePolicy` | String | -         | Can be used to change the default signature validation     |
|                   |        |           | policy that is used by the service                         |
+-------------------+--------+-----------+------------------------------------------------------------+

#### Sample request

```json
{
  "filename":"sample.ddoc",
  "documentType":"DDOC",
  "document":"PD94bWwgdmVyc2lvbj0iMS4....",
  "signaturePolicy": "EU"
}
```

### JSON Response

+---------------------------------------+--------+------------------------------------------------------------+
| Parameter                             | Type   |  Description                                               |
+=======================================+========+============================================================+
| `policy`                              | Object | Object containing information of the SiVa signature        |
|                                       |        | validation policy that was used for validation             |
+---------------------------------------+--------+------------------------------------------------------------+
| `policy.policyVersion`                | String | Version of the validation policy                           |
+---------------------------------------+--------+------------------------------------------------------------+
| `policy.policyName`                   | String | Name of the validation policy                              |
+---------------------------------------+--------+------------------------------------------------------------+
| `policy.policyDescription`            | String | Short description of the validation policy                 |
+---------------------------------------+--------+------------------------------------------------------------+
| `policy.policyUrl`                    | String | URL where the signature validation policy                  |
|                                       |        | document can be downloaded.                                |
|                                       |        |                                                            |
|                                       |        | The validation policy document shall include information   |
|                                       |        | about validation of all the document formats, including    |
|                                       |        | the different validation policies that are used in         |
|                                       |        | case of different file formats and base libraries.         |
+---------------------------------------+--------+------------------------------------------------------------+
| `signaturesCount`                     | Number | Number of signatures found inside digitally signed file    |
+---------------------------------------+--------+------------------------------------------------------------+
| `validSignaturesCount`                | Number | Signatures count that have validated to `TOTAL-PASSED`.    |
|                                       |        | See also `validSignaturesCount` field.                     |
+---------------------------------------+--------+------------------------------------------------------------+
| `validationTime`                      | Date   | Time of validating the signature by the service            |
+---------------------------------------+--------+------------------------------------------------------------+
| `documentName`                        | String | Digitally signed document's file name                      |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatureForm`                       | String | Format (and optionally version) of the container           |
|                                       |        | containing the signature                                   |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures`                          | Array  | Collection of signatures found in digitally signed document|
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures[0]`                       | Object | Signature information object                               |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures[0].claimedSigningTime`    | Data   | Claimed signing time, i.e. signer's computer time during   |
|                                       |        | signature creation                                         |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures[0].errors`                | Array  | Information about validation error(s).                     |
|                                       |        |                                                            |
|                                       |        | Array of error messages, as returned by the base library   |
|                                       |        | that was used for signature validation.                    |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures[0].id`                    | String | Signature ID:                                              |
|                                       |        |                                                            |
|                                       |        | * In case of XAdES signatures, the value of <Signature>    |
|                                       |        |   XML element's Id attribute                               |
|                                       |        |                                                            |
|                                       |        | * In case of PAdES signatures, the value of                |
|                                       |        |                                                            |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures[0].indication`            | String | Overall result of the signature's validation               |
|                                       |        | process, according to EN 319 102-1 "Table 5: Status        |
|                                       |        | indications of the signature validation process"           |
|                                       |        |                                                            |
|                                       |        | The validation results of different signatures in one      |
|                                       |        | signed document (signature container) may vary.            |
|                                       |        | See also `validSignaturesCount` and `SignaturesCount`      |
|                                       |        | fields.                                                    |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures[0].info`                  | Object |                                                            |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures[0].info.bestSignatureTime`| Date   | Time value that is regarded as trusted signing time,       |
|                                       |        | denoting the earliest time when it can be trusted by       |
|                                       |        | the validation application (because proven by some POE     |
|                                       |        | present in the signature) that a signature has existed:    |
|                                       |        |                                                            |
|                                       |        | * in case of signature with time-mark - the producedAt     |
|                                       |        |   value of the earliest valid time-mark (OCSP confirmation |
|                                       |        |   of the signer's certificate) in the signature.           |
|                                       |        | * in case of signature with time-stamp - the getTime       |
|                                       |        |   value of the earliest valid signature time-stamp         |
|                                       |        |   token in the signature.                                  |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures[0].signatureFormat`       | String | Format and profile (according to Baseline Profile standard)|
|                                       |        | of the signature                                           |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures[0].signatureLevel`        | String | In case of BDOC and PAdES formats: indication whether      |
|                                       |        | the signature is Advanced electronic Signature (AdES),     |
|                                       |        | AdES supported by a Qualified Certificate (AdES/QC) or a   |
|                                       |        | Qualified electronic Signature (QES).                      |
|                                       |        |                                                            |
|                                       |        | In case of DIGIDOC-XML 1.0..1.3 formats, empty value       |
|                                       |        | is used as the signature level is not checked by the       |
|                                       |        | JDigiDoc base library that is used for validation.         |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures[0].signatureScopes`       | Array  | Contains information of the original data that is          |
|                                       |        | covered by the signature.                                  |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures[0].signedBy`              | String | Signers name and identification number, i.e. value of      |
|                                       |        | the CN field of the signer's certificate                   |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures[0].subIndication`         | String | Additional subindication in case of failed or              |
|                                       |        | indeterminate validation result, according to EN 319 102-1 |
|                                       |        | "Table 6: Validation Report Structure and Semantics"       |
+---------------------------------------+--------+------------------------------------------------------------+
| `signatures[0].warnings`              | Array  | Block of validation warnings that do not affect            |
|                                       |        | the overall validation result.                             |
|                                       |        |                                                            |
|                                       |        | Known warning situations (according to                     |
|                                       |        | http://id.ee/public/SK-JDD-PRG-GUIDE.pdf, chap. 5.2.4.1):  |
|                                       |        |                                                            |
|                                       |        | * BDOC (and PAdES?): Weaker digest method (SHA-1) has      |
|                                       |        |   been used than recommended when calculating either or    |
|                                       |        |   element’s digest value.                                  |
|                                       |        | * DIGIDOC-XML 1.0-1.3: `DataFile` element’s `xmlns`        |
|                                       |        |   attribute is missing.                                    |
|                                       |        | * DIGIDOC-XML 1.0-1.3: `<IssuerSerial><X509IssuerName>`    |
|                                       |        |   and/or `<IssuerSerial><X509IssuerSerial>` element’s      |
|                                       |        |   `xmlns` attribute is missing.                            |
+---------------------------------------+--------+------------------------------------------------------------+

#### Sample response

```json
{
    "policy": {
        "policyVersion": "1.0",
        "policyName": "SiVa validation policy",
        "policyDescription": "SiVa validation policy",
        "policyUrl": "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/"
    },
    "signaturesCount": 1,
    "validSignaturesCount": 1,
    "validationTime": "2016-07-28T14:41:45Z",
    "documentName": "sample.bdoc",
    "signatures": [
        {
            "claimedSigningTime": "2016-05-11T10:17:57Z",
            "errors": [],
            "id": "S0",
            "indication": "TOTAL-PASSED",
            "info": {
                "bestSignatureTime": "2016-05-11T10:18:06Z"
            },
            "signatureFormat": "XAdES_BASELINE_LT_TM",
            "signatureLevel": "QES",
            "signatureScopes": [],
            "signedBy": "NURM,AARE,38211015222",
            "subIndication": "",
            "warnings": []
        }
    ]
}
```

## SOAP validation service

### SOAP Request

+-------------------+--------+-----------+------------------------------------------------------------+
| Parameter         | Type   | Mandatory | Description                                                |
+===================+========+===========+============================================================+
| `Document`        | String | +         | Base64 encoded string of digitally signed document         |
+-------------------+--------+-----------+------------------------------------------------------------+
| `Filename`        | String | +         | Filename of the digitally signed document                  |
|                   |        |           | (i.e `sample.bdoc`)                                        |
+-------------------+--------+-----------+------------------------------------------------------------+
| `DocumentType`    | String | +         | Validation service to use for validation                   |
+-------------------+--------+-----------+------------------------------------------------------------+
| `SignaturePolicy` | String | -         | Can be used to change the default signature validation     |
|                   |        |           | policy that is used by the service                         |
+-------------------+--------+-----------+------------------------------------------------------------+


### SOAP Response

!!! development
    Need to add response description