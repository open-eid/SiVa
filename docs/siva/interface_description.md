# Interface description

## REST JSON validation service

### JSON Request

+-------------------+--------+-----------+------------------------------------------------------------+
| Parameter         | Type   | Mandatory | Description                                                |
+===================+========+===========+============================================================+
| `document`        | String | +         | Base64 encoded string of digitally signed file             |
+-------------------+--------+-----------+------------------------------------------------------------+
| `filename`        | String | +         | Filename of the digitally signed file (i.e `sample.bdoc`   |
+-------------------+--------+-----------+------------------------------------------------------------+
| `documentType`    | String | +         | Validation service to use for validation                   |
+-------------------+--------+-----------+------------------------------------------------------------+
| `signaturePolicy` | String | -         | If You want to use alternative validation policy. Then You |
|                   |        |           | `signaturePolicy` for that.                                |
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
| `Document`        | String | +         | Base64 encoded string of digitally signed file             |
+-------------------+--------+-----------+------------------------------------------------------------+
| `Filename`        | String | +         | Filename of the digitally signed file (i.e `sample.bdoc`   |
+-------------------+--------+-----------+------------------------------------------------------------+
| `DocumentType`    | String | +         | Validation service to use for validation                   |
+-------------------+--------+-----------+------------------------------------------------------------+
| `SignaturePolicy` | String | -         | If You want to use alternative validation policy. Then You |
|                   |        |           | `signaturePolicy` for that.                                |
+-------------------+--------+-----------+------------------------------------------------------------+


### SOAP Response
