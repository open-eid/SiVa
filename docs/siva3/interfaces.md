<!--# Interface description-->

In this section the SiVa external interfaces are described. For information of internal components and their interfaces, please refer to [**Structure and activities**](../siva3/structure_and_activities.md).

SiVa service provides **REST JSON** interface that enable the service users to:

* Request validation of signatures in a digitally signed document (i.e. signature container like BDOC,ASiC-E/PDF/...);
* Request validation of XAdES signature without datafiles.
* Request datafiles inside of DDOC container

SiVa service REST JSON interface supports X-Road v6. However, it is optional whether to integrate SiVa service using X-Road or using "plain" REST interface. This document only describes the SiVa service part of the interface, for the X-Road specifics visit Riigi Infosüsteemi Amet [webpage](https://www.ria.ee/en/state-information-system/data-exchange-platforms/data-exchange-layer-x-tee).

In the following subsections, the SiVa validation request and response interfaces are described in detail. 

## Validation request interface


** REST JSON Endpoint **

```
POST https://<server url>/validate
```

### Validation request parameters

Validation request parameters for JSON interface are described in the table below.

| JSON parameter | Mandatory | JSON data type | Description |
|----------------|-----------|-------------|----------------|
| document | + |  String | Base64 encoded string of digitally signed document to be validated |
| filename | + |  String | File name of the digitally signed document (i.e. sample.bdoc), max length 255 characters. |
| signaturePolicy | - |  String | Can be used to change the default signature validation policy that is used by the service. <br> See also [SiVa Validation Policy](../siva3/appendix/validation_policy.md) for more detailed information on given policy constraints.<br>**Possible values:** <br> POLv3 - signatures with all legal levels are accepted (i.e. QES, AdESqc and AdES, according to Regulation (EU) No 910/2014.) <br> POLv4 - the default policy. Accepted signatures depend on their type (i.e. signature, seal or unknown) and legal level (i.e. QES, AdESqc and Ades) |
| reportType | - | String | Can be used to change the default returned report type. <br>**Possible values:** <br> Simple - default report type. Returns overall validation result (validationConclusion block)<br> Detailed -  returns detailed information about the signatures and their validation results (validationConclusion, validationProcess and validationReportSignature. Two later ones are optionally present). <br> Diagnostic -  returns diagnostic data about the information contained in the signature itself, it's revocation data and mathematical validity (validationConclusion, diagnosticData block) |

### Sample JSON request with mandatory parameters

```json
{
  "filename":"sample.bdoc",
  "document":"PD94bWwgdmVyc2lvbj0iMS4...."
}
```
### Sample JSON request with all parameters

```json
{
  "filename":"sample.asice",
  "document":"PD94bWwgdmVyc2lvbj0iMS4....",
  "signaturePolicy":"POLv3",
  "reportType":"Detailed"
}
```

## Validation request interface for hashcode

Hashcode XAdES validation is supported for **REST JSON** interface.

** REST JSON Endpoint **

```
POST https://<server url>/validateHashcode
```

### Validation request parameters

Two different use cases are supported for the hashcode validation. 
1) Providing the data file name, hash algorithm and hash on validation.
2) Providing only signature file. **NB! This method requires validation result change on integrator side when the local datafile hashes do not match with hashes returned in validation result!**

Validation request parameters for JSON interface are described in the table below.

| JSON parameter | Mandatory | JSON data type | Description |
|----------------|-----------|----------------|-------------|
| signatureFiles | + |  Array | Array containing the signature objects. |
| signatureFiles[0] | + |  Object | Object containing one signature file. |
| signatureFiles[0].signature | + |  String | Base64 encoded string of signature file |
| signatureFiles[0].datafiles | - |  Array | Array containing the information for datafiles that signature is covering |
| signatureFiles[0].datafiles[0] | + | Object | Object containing data file information |
| signatureFiles[0].datafiles[0].filename | + |  String | File name of the hashed data file, max length 255 characters. |
| signatureFiles[0].datafiles[0].hashAlgo | + |  String | Hash algorithm used for hashing the data file (must match with algorithm in signature file). Accepted values are dependant of validation policy |
| signatureFiles[0].datafiles[0].hash | + |  String | Data file hash in Base64 encoded format. |
| signaturePolicy | - |  String | Can be used to change the default signature validation policy that is used by the service. <br> See also [SiVa Validation Policy](https://open-eid.github.io/SiVa/siva3/appendix/validation_policy) for more detailed information on given policy constraints.<br>**Possible values:** <br> POLv3 - signatures with all legal levels are accepted (i.e. QES, AdESqc and AdES, according to Regulation (EU) No 910/2014.) <br> POLv4 - the default policy. Accepted signatures depend on their type (i.e. signature, seal or unknown) and legal level (i.e. QES, AdESqc and Ades) |
| reportType | - | String | <br>**Possible values:** <br> Simple - default report type. Returns overall validation result (validationConclusion block)<br> Detailed -  returns detailed information about the signatures and their validation results (validationConclusion, validationProcess and validationReportSignature. Two later ones are not supported for hashcode). <br> Diagnostic -  returns diagnostic data about the information contained in the signature itself, it's revocation data and mathematical validity (validationConclusion, diagnosticData block. Last one is not support for hashcode) |

### Sample JSON request with mandatory parameters (datafile hashcode match verification done on integrators side)

```json
{
  "signatureFiles": [
    {
	  "signature": "PD9094wskjd..."
	},
	{
	  "signature": "AD9sa4wsfsd..."
	}
  ]
}
```
### Sample JSON request with all parameters (datafile hashcode match verification done on SIVA side)

```json
{
	"signatureFiles": [
		{
			"signature": "PD94bWwgdmVyc2lvbj...",
			"datafiles": [
				{
					"filename": "leping.pdf",
					"hashAlgo": "SHA256",
					"hash": "WRlczpSZXZvY2F0aW9uVmFsd..."
				},
								{
					"filename": "leping2.pdf",
					"hashAlgo": "SHA256",
					"hash": "WRlpzaF0F0sda2vaW9uVmFsd..."
				}
			]
		},
		{
			"signature": "PDadw4mVyc2lvbj...",
			"datafiles": [
				{
					"filename": "leping.pdf",
					"hashAlgo": "SHA256",
					"hash": "WRlczpSZXZvY2F0aW9uVmFsd..."
				},
								{
					"filename": "leping2.pdf",
					"hashAlgo": "SHA256",
					"hash": "WRlpzaF0F0sda2vaW9uVmFsd..."
				}
			]
		}
	],
	"reportType": "Simple",
	"signaturePolicy": "POLv4"
}
```

## Validation response interface
The signature validation report (i.e. the validation response) for JSON interface depends on what type of validation report was requested.

### Validation response parameters Simple Report (successful scenario)

General structure of validation response.

| JSON parameter | Mandatory |  JSON data type | Description |
|----------------|-----------|-----------------|-------------|
| validationReport |  + | Object | Object containing SIVA validation report |
| validationReport. validationConclusion |  + | Object | Object containing information of the validation conclusion |

Structure of validationConclusion block

| JSON parameter | Mandatory |  JSON data type | Description |
|----------------|-----------|-----------------|-------------|
| policy | + |  Object | Object containing information of the SiVa signature validation policy that was used for validation. |
| policy.policyName | + | String | Name of the validation policy |
| policy. policyDescription | + | String | Short description of the validation policy. |
| policy.policyUrl | + | String | URL where the signature validation policy document can be downloaded. The validation policy document shall include information about validation of all the document formats, including the different validation policies that are used in case of different file formats and base libraries. |
| signaturesCount | + | Number | Number of signatures found inside digitally signed file. |
| validSignaturesCount | + | Number | Signatures count that have validated to `TOTAL-PASSED`. See also `Signature.Indication` field. |
| validationLevel | - | Date | Validation process against what the document is validated, only applicable on DSS based validations. <br>**Possible values:** <br> ARCHIVAL_DATA|
| validationTime | + | Date | Time of validating the signature by the service. |
| validationWarnings | - | Array | Array of SiVa validation warnings that do not affect the overall validation result. See also `signatures.warnings` parameter. |
| validationWarnings[0] | + | Object | Object containing the warning. |
| validationWarnings[0]. content | + | String | Description of the warning. |
| validatedDocument | - | Object | Object containing information about validated document. |
| validatedDocument. filename | - | String | Digitally signed document's file name. Not present for hashcode validation. |
| validatedDocument. fileHash | - | String | Calculated hash for validated document in Base64. Present when report signing is enabled. |
| validatedDocument. hashAlgo | - | String | Hash algorithm used. Present when report signing is enabled. |
| signatureForm | - | String | Format (and optionally version) of the digitally signed document container. <br> In case of documents in [DIGIDOC-XML](https://www.id.ee/wp-content/uploads/2020/08/digidoc_format_1.3.pdf) (DDOC) format, the "hashcode" suffix is used to denote that the container was validated in [hashcode mode](https://open-eid.github.io/allkirjastamisteenus/json-technical-description/#hashcode-container-form), i.e. without original data files. <br> **Possible values:**  <br> DIGIDOC_XML_1.0 <br> DIGIDOC_XML_1.0_hashcode <br> DIGIDOC_XML_1.1 <br> DIGIDOC_XML_1.1_hashcode <br> DIGIDOC_XML_1.2 <br> DIGIDOC_XML_1.2_hashcode <br> DIGIDOC_XML_1.3 <br> DIGIDOC_XML_1.3_hashcode <br> ASiC-E - used in case of all ASiC-E (and [BDOC](https://www.id.ee/wp-content/uploads/2021/06/bdoc-spec212-eng.pdf)) documents <br> ASiC-S - used in case of all ASiC-S documents |
| signatures | - | Array | Collection of signatures found in digitally signed document |
| signatures[0] | + | Object | Signature information object |
| signatures[0]. claimedSigningTime | + | Date | Claimed signing time, i.e. signer's computer time during signature creation |
| signatures[0].id | + | String | Signature ID attribute  |
| signatures[0].indication | + | String | Overall result of the signature's validation process, according to [ETSI EN 319 102-1](http://www.etsi.org/deliver/etsi_en/319100_319199/31910201/01.01.01_60/en_31910201v010101p.pdf) "Table 5: Status indications of the signature validation process". <br> Note that the validation results of different signatures in one signed document (signature container) may vary. <br> See also `validSignaturesCount` and `SignaturesCount` fields. <br>**Possible values:** <br> TOTAL-PASSED <br> TOTAL-FAILED <br> INDETERMINATE |
| signatures[0]. subIndication | - | String | Additional subindication in case of failed or indeterminate validation result, according to [ETSI EN 319 102-1](http://www.etsi.org/deliver/etsi_en/319100_319199/31910201/01.01.01_60/en_31910201v010101p.pdf) "Table 6: Validation Report Structure and Semantics" |
| signatures[0].errors | - | Array | Information about validation error(s), array of error messages.  |
| signatures[0].errors[0] | + | Object | Object containing the error |
| signatures[0].errors[0].  content | + | String | Error message, as returned by the base library that was used for signature validation. |
| signatures[0].info | - | Object | Object containing trusted signing time information and user added additional signing info. |
| signatures[0].info. bestSignatureTime | + | Date | Time value that is regarded as trusted signing time, denoting the earliest time when it can be trusted by the validation application (because proven by some Proof-of-Existence present in the signature) that a signature has existed.<br>The source of the value depends on the signature profile (see also `SignatureFormat` parameter):<br>- Signature with time-mark (LT_TM level) - the producedAt value of the earliest valid time-mark (OCSP confirmation of the signer's certificate) in the signature.<br>- Signature with time-stamp (LT or LTA level) - the genTime value of the earliest valid signature time-stamp token in the signature. <br> - Signature with BES or EPES level - the value is empty, i.e. there is no trusted signing time value available. |
| signatures[0].info. timeAssertionMessageImprint | - | String | Base64 encoded value of message imprint retrieved from time assertion. In case of LT_TM (TimeMark) signatures, OCSP nonce value is returned. In case of T, LT or LTA (TimeStamp) signatures, TimeStamp message imprint is returned. |
| signatures[0].info. ocspResponseCreationTime | - | Date | Time value that is regarded as the original OCSP response creation time. |
| signatures[0].info. timestampCreationTime | - | Date | Time value of the timestamp creation |
| signatures[0].info. signerRole | - | Array | Array of roles attached to the signature. |
| signatures[0].info. signerRole[0] | + | Object | Object containing claimed roles. |
| signatures[0].info. signerRole[0]. claimedRole | + | String | Role stated by signer on signing. |
| signatures[0].info. signatureProductionPlace | - | Object | Object containing stated signing location info. |
| signatures[0].info. signatureProductionPlace.countryName | - | String | Stated signing country. |
| signatures[0].info. signatureProductionPlace.stateOrProvince | - | String | Stated state or province. |
| signatures[0].info. signatureProductionPlace.city | - | String | Stated city. |
| signatures[0].info. signatureProductionPlace.postalCode | - | String | Stated postal code. |
| signatures[0].info. signingReason | - | String | Free text field for PAdES type signatures for stating the signing reason |
| signatures[0]. signatureFormat | + | String | Format and profile (according to Baseline Profile) of the signature. See [XAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103171/02.01.01_60/ts_103171v020101p.pdf), [CAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103173/02.02.01_60/ts_103173v020201p.pdf) and [PAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf) for detailed description of the Baseline Profile levels. Levels that are accepted in SiVa validation policy are described in [SiVa signature validation policy](../siva3/appendix/validation_policy.md) <br>**Possible values:**  <br> XAdES_BASELINE_B <br> XAdES_BASELINE_B_BES <br> XAdES_BASELINE_B_EPES <br> XAdES_BASELINE_T <br> XAdES_BASELINE_LT - long-term level XAdES signature where time-stamp is used as a assertion of trusted signing time<br> XAdES_BASELINE_LT_TM - long-term level XAdES signature where time-mark is used as a assertion of trusted signing time. Used in case of [BDOC](https://www.id.ee/wp-content/uploads/2021/06/bdoc-spec212-eng.pdf) signatures with time-mark profile and [DIGIDOC-XML](https://www.id.ee/wp-content/uploads/2020/08/digidoc_format_1.3.pdf) (DDOC) signatures.<br>  XAdES_BASELINE_LTA <br> CAdES_BASELINE_B <br> CAdES_BASELINE_T <br> CAdES_BASELINE_LT <br> CAdES_BASELINE_LTA<br> PAdES_BASELINE_B <br> PAdES_BASELINE_T <br> PAdES_BASELINE_LT <br> PAdES_BASELINE_LTA |
| signatures[0]. signatureMethod | + | String | Signature method specification URI used in signature creation. |
| signatures[0]. signatureLevel | - |String | Legal level of the signature, according to Regulation (EU) No 910/2014. <br> - **Possible values on positive validation result:**<br> QESIG <br> QESEAL <br> QES <br> ADESIG_QC <br> ADESEAL_QC <br> ADES_QC <br> ADESIG <br> ADESEAL <br> ADES <br> - **Possible values on indeterminate validation result:**<br> prefix INDETERMINATE is added to the level described in positive result. For example  INDETERMINATE_QESIG <br> - **Possible values on negative validation result:**<br>In addition to abovementioned<br> NOT_ADES_QC_QSCD <br> NOT_ADES_QC <br> NOT_ADES <br> NA <br> - In case of DIGIDOC-XML 1.0..1.3 formats, value is missing as the signature level is not checked by the JDigiDoc base library that is used for validation. However, the signatures can be indirectly regarded as QES level signatures, see also [SiVa Validation Policy](/siva3/appendix/validation_policy.md)<br>|
| signatures[0].signedBy | + | String | In format of "surname, givenName, serialNumber" if  these fields are present in subject distinguished name field. In other cases, value of common name field. |
| signatures[0]. subjectDistinguishedName | - | Object | Object containing subject's distinguished name information. |
| signatures[0]. subjectDistinguishedName .serialNumber | - | String | SERIALNUMBER value portion in signer's certificate's subject distinguished name |
| signatures[0]. subjectDistinguishedName .commonName | - | String | CN (common name) value portion in signer's certificate's subject distinguished name |
| signatures[0]. subjectDistinguishedName .givenName | - | String | Given name value portion in signer's certificate's subject distinguished name |
| signatures[0]. subjectDistinguishedName .surname | - | String | Surname value portion in signer's certificate's subject distinguished name |
| signatures[0]. signatureScopes | - | Array | Contains information of the original data that is covered by the signature. |
| signatures[0]. signatureScopes[0]. name | + | String | Name of the signature scope. |
| signatures[0]. signatureScopes[0]. scope | + | String | Type of the signature scope. |
| signatures[0]. signatureScopes[0]. content | + | String | Description of the scope. |
| signatures[0]. signatureScopes[0]. hashAlgo | - | String | Hash algorithm used for datafile hash calculation. Present for hashcode validation. |
| signatures[0]. signatureScopes[0]. hash | - | String | Hash of data file encoded in Base64. Present for hashcode validation. |
| signatures[0]. warnings | - | Array | Block of validation warnings that do not affect the overall validation result. |
| signatures[0]. warnings[0] | + | Object | Object containing the warning |
| signatures[0]. warnings[0]. content | + | String | Warning description, as returned by the base library that was used for validation. |
| signatures[0]. certificates | - | Array | Array containing certificates that are present in the signature or can be fetched from TSL. |
| signatures[0]. certificates[0] | + | Object | Object containing certificate type, common name and certificate. Minimal object is signer certificate. If present contains certificates for TimeStamps and OCSP as well. |
| signatures[0]. certificates[0].commonName | + | String | CN (common name) value in certificate. |
| signatures[0]. certificates[0].type | + | String | Type of the certificate. Can be SIGNING, REVOCATION, SIGNATURE_TIMESTAMP, ARCHIVE_TIMESTAMP or CONTENT_TIMESTAMP. |
| signatures[0]. certificates[0].content | + | String | DER encoded X.509 certificate in Base64. |
| signatures[0]. certificates[0].issuer | + | String | Object containing issuer certificate information. Can create chain til the trust anchor. |
| timeStampTokens | - | Array | Array containing the time stamp tokens |
| timeStampTokens[0] | + | Object | Object containing the time stamp token (TST) |
| timeStampTokens[0]. indication | + | String | Result of the time stamp token validation. <br>**Possible values:** <br> TOTAL-PASSED <br> TOTAL-FAILED |
| timeStampTokens[0]. signedBy | + | String | Signer of the time stamp token. |
| timeStampTokens[0]. signedTime | + | String | Time when the time stamp token was given. |
| timeStampTokens[0]. error | - | Array | Errors returned in time stamp token validation. |
| timeStampTokens[0]. error[0] | + | Object | Object containing the error. |
| timeStampTokens[0]. error[0]. content | + | String | Error description. |

#### Sample JSON response Simple Report (successful scenario)

```json
{"validationReport": {"validationConclusion": {
    "validationTime": "2020-06-15T11:45:52Z",
    "signaturesCount": 1,
    "validationLevel": "ARCHIVAL_DATA",
    "validatedDocument": {"filename": "singleValidSignatureTS.asice"},
    "validSignaturesCount": 1,
    "signatures": [{
        "signatureFormat": "XAdES_BASELINE_LT",
        "subjectDistinguishedName": {
            "commonName": "JÕEORG,JAAK-KRISTJAN,38001085718",
            "serialNumber": "PNOEE-38001085718"
        },
        "certificates": [
            {
                "commonName": "DEMO of SK TSA 2014",
                "type": "SIGNATURE_TIMESTAMP",
                "content": "MII..."
            },
            {
                "commonName": "JÕEORG,JAAK-KRISTJAN,38001085718",
                "type": "SIGNING",
                "content": "MII...",
                "issuer": {
                    "commonName": "TEST of ESTEID2018",
                    "content": "MII..."
                }
            },
            {
                "commonName": "TEST of SK OCSP RESPONDER 2011",
                "type": "REVOCATION",
                "content": "MII..."
            }
        ],
        "signedBy": "JÕEORG,JAAK-KRISTJAN,38001085718",
        "claimedSigningTime": "2020-05-21T13:56:52Z",
        "signatureLevel": "QESIG",
        "signatureScopes": [{
            "scope": "FULL",
            "name": "test.txt",
            "content": "Full document"
        }],
        "signatureMethod": "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256",
        "id": "id-a9ce7f66cff1d17ddaab37c46a88f5f4",
        "indication": "TOTAL-PASSED",
        "info": {
            "timestampCreationTime": "2020-05-21T13:56:48Z",
            "timeAssertionMessageImprint": "MDEwDQYJYIZIAWUDBAIBBQAEID3j1ceryQp4ZNP8iVfd50l/0JXvpry+XS+ajiAUA+Su",
            "bestSignatureTime": "2020-05-21T13:56:48Z",
            "ocspResponseCreationTime": "2020-05-21T13:56:49Z"
        }
    }],
    "policy": {
        "policyDescription": "Policy according most common requirements of Estonian Public Administration, to validate Qualified Electronic Signatures and Electronic Seals with Qualified Certificates (according to Regulation (EU) No 910/2014, aka eIDAS). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive validation result, with exception for seals, where AdES/QC and above will produce positive result. Signatures and Seals which are not compliant with ETSI standards (referred by eIDAS) may produce unknown or invalid validation result. Validation process is based on eIDAS Article 32 and referred ETSI standards.",
        "policyUrl": "http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4",
        "policyName": "POLv4"
    },
    "signatureForm": "ASiC-E"
}}}
```

#### Warning/error messages filtered out in Simple Report

From Simple Report following messages, that are considered false-positive in Estonian context, are filtered out:
* Warning: _The organization name is missing in the trusted certificate!_
* Warning: _The trusted certificate does not match the trust service!_
* Error: _The certificate is not related to a granted status at time-stamp lowest POE time!_

Above messages are filtered out only in Simple Report.

### Validation response parameters Detailed Report (successful scenario)

General structure of validation response.

| JSON parameter | Mandatory |  JSON data type | Description |
|----------------|-----------|-----------------|-------------|
| validationReport |  + | Object | Object containing SIVA validation report. |
| validationReport. validationConclusion |  + | Object | Object containing information of the validation conclusion. The same object that is present in Simple Report. |
| validationReport. validationProcess | - | Object | Object containing information of the validation process. This block is present only on DSS library based validations and is built on DSS detailed report. For more information visit [DSS documentation](https://github.com/esig/dss/blob/develop/dss-cookbook/src/main/asciidoc/_chapters/signature-validation.adoc).  |
| validationReportSignature |  - | String | Base64 string of ASIC-E container that includes the detailed report and is signed by the validation service provider |

### Validation response parameters for Diagnostic Data Report (successful scenario)

General structure of validation response.

| JSON parameter | Mandatory |  JSON data type | Description |
|----------------|-----------|-----------------|-------------|
| validationReport |  + | Object | Object containing SIVA validation report. |
| validationReport. validationConclusion |  + | Object | Object containing information of the validation conclusion. The same object that is present in Simple Report and Detailed Report. |
| validationReport. diagnosticData | - | Object | Object containing diagnostic data about the information contained in the signature itself, it's revocation data and mathematical validity. This block is present only on DSS library based validations (excluding hashcode validation) and is built on DSS diagnostic data. For more information visit [DSS documentation](https://github.com/esig/dss/blob/develop/dss-cookbook/src/main/asciidoc/dss-documentation.adoc#validation-process).  |

### Sample JSON response (error situation)
In case of error (when validation report is not returned) status code 400 is returned together with following message body:

```json
{"requestErrors": [{
    "message": "Document malformed or not matching documentType",
    "key": "document"
}]}
```

## Data files request interface


** REST JSON Endpoint **

```
POST https://<server url>/getDataFiles
```

### Data files request parameters

Data files request parameters for JSON interface are described in the table below.

| JSON parameter | Mandatory | JSON data type | Description |
|----------------|-----------|----------------|-------------|
| document | + |  String | Base64 encoded string of digitally signed DDOC document |
| filename | + |  String | File name of the digitally signed document (i.e. sample.ddoc), max length 255 characters. Currently only DDOC file format is supported for this operation|

### Sample JSON request

```json
{
  "filename":"sample.ddoc",
  "document":"PD94bWwgdmVyc2lvbj0iMS4...."
}
```

## Data files response interface

### Data files response parameters (successful scenario)

The data file extraction report (i.e. the data files response) for JSON interface is described in the table below.
SiVa returns all data files as they are extracted by JDigiDoc library in an as is form. No extra operations or validations are done.

| JSON parameter | Mandatory | JSON data type | Description |
|----------------|-----------|----------------|-------------|
| dataFiles | - |  Array | Collection of data files found in digitally signed document |
| dataFiles[0] | + | Object | Extracted data file object |
| dataFiles[0].fileName | - |  String | File name of the extracted data file |
| dataFiles[0].size | - | Long | Extracted data file size in bytes |
| dataFiles[0].base64 | - |String | Base64 encoded string of extracted data file |
| dataFiles[0].mimeType |  - | String | MIME type of the extracted data file  |

### Sample JSON response (successful scenario)

```json
{
"dataFiles": [{
 "fileName": "Glitter-rock-4_gallery.jpg",
 "size": 41114,
 "base64": "/9j/4AAQSkZJ...",
 "mimeType": "application/octet-stream" }]
}
```

### Sample JSON response (error situation)
In case of error (when datafiles are not returned) status code 400 is returned together with following message body:

```json
{"requestErrors": [{
    "message": "Invalid document type. Can only return data files for DDOC type containers.",
    "key": "documentType"
}]}
```



## Service health monitoring

SiVa webapps provide an interface for external monitoring tools (to periodically check the generic service health status).

### The request
The monitoring endpoint is accessible via HTTP GET at **/monitoring/health** or **/monitoring/health.json** url.

Sample request:
```
GET https://<server url>/monitoring/health
```

### The response

As a response, a JSON object is returned with the following information:

| Field | Description |
| ---------| --------------- |
| status | General status of the webapp. <br/>Possible values: <ul><li>**DOWN** - when some of the dependent indicators status are down (ie when `link{number}.status` is DOWN, the overall service status is DOWN)</li><li>**UP** - the default value. </li></ul> |
| components.health.status | Status of current webapp - constant value **UP** |
| components.health.details.webappName | The artifact name of the webapp. Taken from the MANIFEST.MF file (inside the jar/war file). |
| components.health.details.version | The release version fo the webapp. Taken from the MANIFEST.MF (inside the jar/war file).  |
| components.health.details.buildTime | Build date and time (format yyyy-MM-dd'T'HH:mm:ss'Z') of the webapp. Taken from the MANIFEST.MF (inside the jar/war file).  |
| components.health.details.startTime | Webapp startup date and time (format yyyy-MM-dd'T'HH:mm:ss'Z')|
| components.health.details.currentTime | Current server date and time (format yyyy-MM-dd'T'HH:mm:ss'Z') |
| components.link{number}.status | (OPTIONAL) Represents the status of a link to the external system that the webapp depends on. <ul><li>**DOWN** when the webapp does not respond (within a specified timeout limit - default 10 seconds) or the response is in invalid format (default Spring boot actuator /health endpoint format is expected).</li><li>**UP** if the service responds with HTTP status code 200 and returns a valid JSON object with status "UP"</li></ul> |) |
| components.link{number}.details.name | (OPTIONAL) Descriptive name for the link to the external system |

Sample response:

```json
{
    "status": "UP",
    "components": {
        "health": {
            "details": {
               "webappName":"siva-sample-application",
                "version":"3.3.0",
                "buildTime":"2016-10-21T15:56:21Z",
                "startTime":"2016-10-21T15:57:48Z",
                "currentTime":"2016-10-21T15:58:39Z"
            },
            "status": "UP"
        },
        "link1": {
            "details": {
                "name": "sivaService"
            },
            "status": "UP"
        }
    }
}
```

## Simplified health monitoring

SiVa webapps provide a simple interface for external monitoring tools (to periodically check the generic service health status).

### The request
The simplified monitoring endpoint is accessible via HTTP GET at **/monitoring/heartbeat** url.

Sample request:
```
GET https://<server url>/monitoring/heartbeat
```

### The response

As a response, a JSON object is returned with the following information:

| Field | Description |
| ---------| --------------- |
| status | General status of the webapp. <br/>Possible values: <ul><li>**DOWN** - when the webapp or any of its dependencies down.</li><li>**UP** - the default value. </li></ul> |

Sample response:

```json
{
    "status": "UP"
}
```

## Version information

SiVa webapps provide a simple interface for querying the application version information.

### The request
The version information endpoint is accessible via HTTP GET at **/monitoring/version** url.

Sample request:
```
GET https://<server url>/monitoring/version
```

### The response

As a response, a JSON object is returned with the following information:

| Field | Description |
| ---------| --------------- |
| version | Version string of the webapp |

Sample response:

```json
{
    "version": "3.5.0"
}
```

## Changes in API compared to V3 v3.4.0

Changes are described using notation from REST endpoint.

### New endpoints

| Endpoint | HTTP Method | Link | Comment |
|----------|-----------|------|---------|
| /monitoring/heartbeat | GET |  [Link](../interfaces/#simplified-health-monitoring) | New monitoring endpoint |
| /monitoring/version | GET |  [Link](../interfaces/#version-information) | New monitoring endpoint |


## Changes in API compared to V3 v3.3.0 

Changes are described using notation from REST endpoint.

### Changes in response

| Report | Parameter | Change | Link | Comment |
|---------------|-----------|--------|------|---------|
| validationConclusion | signatures[0].info.timestampCreationTime | Parameter added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Date containing timestamp creation time added |
| validationConclusion | signatures[0].info.ocspResponseCreationTime | Parameter added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Date containing OCSP response creation time added |
| validationConclusion | signatures[0].info.signingReason | Parameter added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | String containing signing reason for PAdES added |

## Changes in API compared to V3 v3.2.0 (non breaking additions to protocol)

Changes are described using notation from REST endpoint.

### Changes in response

| Report | Parameter | Change | Link | Comment |
|---------------|-----------|--------|------|---------|
| validationConclusion | signatures[0].signatureMethod | Parameter added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Used signature method is now returned |
| validationConclusion | signatures[0].info.timeAssertionMessageImprint | Parameter added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Base64 encoded value of message imprint retrieved from time assertion. In case of LT_TM (TimeMark) signatures, OCSP nonce value is returned. In case of T, LT or LTA (TimeStamp) signatures, TimeStamp message imprint is returned. |
| validationConclusion | signatures[0].info.signatureProductionPlace | Parameter added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Object containing optional signing location info added |
| validationConclusion | signatures[0].info.signerRole | Parameter added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Array containing optional signer roles added |
| validationConclusion | signatures[0].certificates | Parameter added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Array containing certificates that are present in the signature or in TSL. |

## Changes in API compared to V2

Changes are described using notation from REST endpoint.

### Changes in request

| Endpoint | Parameter | Change | Link | Comment |
|----------|-----------|--------|------|---------|
| /validateDocument | reportType | parameter added |  [Link](../interfaces/#validation-request-parameters) | Diagnostic report type added |
| /validateHashcode | whole request | request changed |  [Link](../interfaces/#validation-request-parameters_1) | Request changed to support validation of multiple signatures with one request |

### Changes in response

| Response type | Parameter | Change | Link | Comment                                                               |
|---------------|-----------|--------|------|-----------------------------------------------------------------------|
| Simple | validatedDocument | now optional |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Object now optional                                                   |
| Simple | validatedDocument.filename | now optional |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | String now optional                                                   |
| Simple | validatedDocument.fileHash | changed |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Previously validatedDocument.fileHashInHex. Now contains Base64 value |
| Simple | signatures[0].subjectDistinguishedName.serialNumber | added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Added signers serial number field                                     |
| Simple | signatures[0].subjectDistinguishedName.commonName | added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Added signers common name field                                       |
| Simple | signatures[0].subjectDistinguishedName.givenName | added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Added signers given name from SubjectDistinguishedName field          |
| Simple | signatures[0].subjectDistinguishedName.surname | added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Added signers surname from SubjectDistinguishedName field                          |
| Simple | signatures[0].signatureScopes[0].hashAlgo | added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Added datafile hash algo field for hashcode validation                |
| Simple | signatures[0].signatureScopes[0].hash | added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Added datafile hash field for hashcode validation                     |
| Diagnostic | whole response | added |  [Link](../interfaces/#validation-response-parameters-for-diagnostic-data-report-successful-scenario) | New report type added                                                 |

