<!--# Interface description-->

In this section the SiVa external interfaces are described. For information of internal components and their interfaces, please refer to [**Structure and activities**](structure_and_activities).

SiVa service provides **REST JSON** and **SOAP** interfaces that enable the service users to:

* Request validation of signatures in a digitally signed document (i.e. signature container like BDOC,ASiC-E/PDF/...);
* Request validation of XAdES signature without datafiles.
* Request datafiles inside of DDOC container

SiVa service SOAP interface supports X-Road v6. However, it is optional whether to integrate SiVa service using X-Road or using "plain" SOAP interface. This document only describes the SiVa service part of the interface, for the X-Road specifics visit Riigi Infosüsteemi Amet [webpage](https://www.ria.ee/ee/x-tee.html).

In the following subsections, the SiVa validation request and response interfaces are described in detail. 

## Validation request interface


** REST JSON Endpoint **

```
POST https://<server url>/validate
```

** SOAP Endpoint **
```
POST https://<server url>/soap/validationWebService/validateDocument
```

** SOAP WSDL **
```
POST https://<server url>/soap/validationWebService/validateDocument?wsdl
```

### Validation request parameters

Validation request parameters for JSON and SOAP interfaces are described in the table below. Data types of SOAP parameters are defined in the [SiVa WSDL document](/siva/appendix/wsdl).

| JSON parameter | SOAP parameter | Mandatory | JSON data type | Description |
|----------------|----------------|-----------|-------------|----------------|
| document | Document | + |  String | Base64 encoded string of digitally signed document to be validated |
| filename | Filename | + |  String | File name of the digitally signed document (i.e. sample.bdoc), max length 255 characters. |
| documentType | DocumentType | - |  String | If not present document type is determined automatically based on the file extension used in the filename. This parameter is necessary to differentiate XROAD ASIC-E containers from standard ASIC-E containers. <br> **Possible values:** <br> XROAD - for documents created in the [X-Road](https://www.ria.ee/en/x-road.html) information system, see also [specification document](https://cyber.ee/uploads/2013/05/T-4-23-Profile-for-High-Performance-Digital-Signatures1.pdf) of the signature format. |
| signaturePolicy | SignaturePolicy | - |  String | Can be used to change the default signature validation policy that is used by the service. <br> See also [SiVa Validation Policy](/siva3/appendix/validation_policy) for more detailed information on given policy constraints.<br>**Possible values:** <br> POLv3 - signatures with all legal levels are accepted (i.e. QES, AdESqc and AdES, according to Regulation (EU) No 910/2014.) <br> POLv4 - the default policy. Accepted signatures depend on their type (i.e. signature, seal or unknown) and legal level (i.e. QES, AdESqc and Ades) |
| reportType | ReportType | - | String | Can be used to change the default returned report type. <br>**Possible values:** <br> Simple - default report type. Returns overall validation result (validationConclusion block)<br> Detailed -  returns detailed information about the signatures and their validation results (validationConclusion, validationProcess and validationReportSignature. Two later ones are optionally present). <br> Diagnostic -  returns diagnostic data about the information contained in the signature itself, it's revocation data and mathematical validity (validationConclusion, diagnosticData block) |

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
  "documentType":"XROAD",
  "signaturePolicy":"POLv3",
  "reportType":"Detailed"
}
```

### Sample SOAP request

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:ValidateDocument xmlns:ns2="http://soap.webapp.siva.openeid.ee/">
      <ns2:ValidationRequest>
        <Document>PD94bWwgdmVyc2lvbj0iMS4....</Document>
        <Filename>sample.asice</Filename>
        <DocumentType>XROAD</DocumentType>
        <SignaturePolicy>POLv3</SignaturePolicy>
        <ReportType>Detailed</ReportType>
      </ns2:ValidationRequest>
    </ns2:ValidateDocument>
  </soap:Body>
</soap:Envelope>
```

## Validation request interface for hashcode

Hashcode XAdES validation is supported for **REST JSON** and **SOAP** interfaces.

** REST JSON Endpoint **

```
POST https://<server url>/validateHashcode
```

** SOAP Endpoint **
```
POST https://<server url>/soap/hashcodeValidationWebService
```

** SOAP WSDL **
```
POST https://<server url>/soap/hashcodeValidationWebService?wsdl
```

### Validation request parameters

Two different use cases are supported for the hashcode validation. 
1) Providing the data file name, hash algorithm and hash on validation.
2) Providing only signature file. **NB! This method requires validation result change on integrator side when the local datafile hashes do not match with hashes returned in validation result!**

Validation request parameters for JSON interface are described in the table below.

| JSON parameter | SOAP parameter | Mandatory | JSON data type | Description |
|----------------|----------------|-----------|----------------|-------------|
| signatureFiles | SignatureFiles | + |  Array | Array containing the signature objects. |
| signatureFiles[0] | SignatureFiles.SignatureFile | + |  Object | Object containing one signature file. |
| signatureFiles[0].signature | SignatureFiles.SignatureFile.Signature | + |  String | Base64 encoded string of signature file |
| signatureFiles[0].datafiles | SignatureFiles.SignatureFile.Signature.DataFiles | - |  Array | Array containing the information for datafiles that signature is covering |
| signatureFiles[0].datafiles[0] | SignatureFiles.SignatureFile.Signature.DataFiles.DataFile | + | Object | Object containing data file information |
| signatureFiles[0].datafiles[0].filename | SignatureFiles.SignatureFile.Signature.DataFiles.DataFile.Filename | + |  String | File name of the hashed data file, max length 255 characters. |
| signatureFiles[0].datafiles[0].hashAlgo | SignatureFiles.SignatureFile.Signature.DataFiles.DataFile.HashAlgo | + |  String | Hash algorithm used for hashing the data file (must match with algorithm in signature file). Accepted values are dependant of validation policy |
| signatureFiles[0].datafiles[0].hash | SignatureFiles.SignatureFile.Signature.DataFiles.DataFile.Hash | + |  String | Data file hash in Base64 encoded format. |
| signaturePolicy | SignaturePolicy | - |  String | Can be used to change the default signature validation policy that is used by the service. <br> See also [SiVa Validation Policy](/siva3/appendix/validation_policy) for more detailed information on given policy constraints.<br>**Possible values:** <br> POLv3 - signatures with all legal levels are accepted (i.e. QES, AdESqc and AdES, according to Regulation (EU) No 910/2014.) <br> POLv4 - the default policy. Accepted signatures depend on their type (i.e. signature, seal or unknown) and legal level (i.e. QES, AdESqc and Ades) |
| reportType | ReportType | - | String | <br>**Possible values:** <br> Simple - default report type. Returns overall validation result (validationConclusion block)<br> Detailed -  returns detailed information about the signatures and their validation results (validationConclusion, validationProcess and validationReportSignature. Two later ones are not supported for hashcode). <br> Diagnostic -  returns diagnostic data about the information contained in the signature itself, it's revocation data and mathematical validity (validationConclusion, diagnosticData block. Last one is not support for hashcode) |

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

### Sample SOAP request with mandatory parameters

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://soap.webapp.siva.openeid.ee/">
   <soapenv:Body>
      <soap:HashcodeValidationDocument>
         <soap:HashcodeValidationRequest>
			<SignatureFiles>
				<SignatureFile>
					<Signature>PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGlu...</Signature>
				</SignatureFile>
				<SignatureFile>
					<Signature>PD94bW2lvbj0wgdmVyciMS4wIvZG2lvbj0lu...</Signature>
				</SignatureFile>
			</SignatureFiles>
         </soap:HashcodeValidationRequest>
      </soap:HashcodeValidationDocument>
   </soapenv:Body>
</soapenv:Envelope>
```
### Sample SOAP request with all parameters and multiple datafiles

```xml

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://soap.webapp.siva.openeid.ee/">
   <soapenv:Body>
      <soap:HashcodeValidationDocument>
         <soap:HashcodeValidationRequest>
			<SignatureFiles>
				<SignatureFile>
					<Signature>PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGlu...</Signature>
					<DataFiles>
						<DataFile>
						  <Filename>test.pdf</Filename>
						  <HashAlgo>SHA256</HashAlgo>
						  <Hash>IucjUcbRo9Rke0bZLiHcwiIiplP9pSrSPr7LKln1EiI=</Hash>
					   </DataFile>
					   <DataFile>
						  <Filename>test2.pdf</Filename>
						  <HashAlgo>SHA256</HashAlgo>
						  <Hash>IucjUcbRo9Rke0bZLiHcwiIiplP9pSrSPr7LKln1EiI=</Hash>
					   </DataFile>
					</DataFiles>
  				</SignatureFile>
				<SignatureFile>
					<Signature>PiBlbmNvZD94bWwgdmVyc2lvbj0PD94bWwGlu...</Signature>
					<DataFiles>
						<DataFile>
						  <Filename>test.pdf</Filename>
						  <HashAlgo>SHA256</HashAlgo>
						  <Hash>IucjUcbRo9Rke0bZLiHcwiIiplP9pSrSPr7LKln1EiI=</Hash>
					   </DataFile>
					   <DataFile>
						  <Filename>test2.pdf</Filename>
						  <HashAlgo>SHA256</HashAlgo>
						  <Hash>IucjUcbRo9Rke0bZLiHcwiIiplP9pSrSPr7LKln1EiI=</Hash>
					   </DataFile>
					</DataFiles>
  				</SignatureFile>
			</SignatureFiles>
         </soap:HashcodeValidationRequest>
      </soap:HashcodeValidationDocument>
   </soapenv:Body>
</soapenv:Envelope>
```

## Validation response interface
The signature validation report (i.e. the validation response) for JSON and SOAP interfaces depends on what type of validation report was requested.  Data types of SOAP parameters are defined in the [SiVa WSDL document](/siva3/appendix/wsdl).

### Validation response parameters Simple Report (successful scenario)

General structure of validation response.

| JSON parameter | SOAP parameter | Mandatory |  JSON data type | Description |
|----------------|----------------|-----------|-----------------|-------------|
| validationReport | ValidationReport |  + | Object | Object containing SIVA validation report |
| validationReport. validationConclusion | ValidationReport. ValidationConclusion |  + | Object | Object containing information of the validation conclusion |

Structure of validationConclusion block

| JSON parameter | SOAP parameter | Mandatory |  JSON data type | Description |
|----------------|----------------|-----------|-----------------|-------------|
| policy | Policy | + |  Object | Object containing information of the SiVa signature validation policy that was used for validation. |
| policy.policyName | Policy.PolicyName | + | String | Name of the validation policy |
| policy. policyDescription | Policy. PolicyDescription | + | String | Short description of the validation policy. |
| policy.policyUrl | Policy.PolicyUrl | + | String | URL where the signature validation policy document can be downloaded. The validation policy document shall include information about validation of all the document formats, including the different validation policies that are used in case of different file formats and base libraries. |
| signaturesCount | SignaturesCount | + | Number | Number of signatures found inside digitally signed file. |
| validSignaturesCount | ValidSignaturesCount | + | Number | Signatures count that have validated to `TOTAL-PASSED`. See also `Signature.Indication` field. |
| validationLevel | ValidationLevel | - | Date | Validation process against what the document is validated, only applicable on DSS based validations. <br>**Possible values:** <br> ARCHIVAL_DATA|
| validationTime | ValidationTime | + | Date | Time of validating the signature by the service. |
| validationWarnings | ValidationWarnings | - | Array | Array of SiVa validation warnings that do not affect the overall validation result. See also `signatures.warnings` parameter. |
| validationWarnings[0] | ValidationWarning | + | Object | Object containing the warning. |
| validationWarnings[0]. content | ValidationWarning. Content| + | String | Description of the warning. |
| validatedDocument | ValidatedDocument | - | Object | Object containing information about validated document. |
| validatedDocument. filename | ValidatedDocument. Filename | - | String | Digitally signed document's file name. Not present for hashcode validation. |
| validatedDocument. fileHash | ValidatedDocument. FileHash | - | String | Calculated hash for validated document in Base64. Present when report signing is enabled. |
| validatedDocument. hashAlgo | ValidatedDocument. HashAlgo | - | String | Hash algorithm used. Present when report signing is enabled. |
| signatureForm | SignatureForm | - | String | Format (and optionally version) of the digitally signed document container. <br> In case of documents in [DIGIDOC-XML](http://id.ee/public/DigiDoc_format_1.3.pdf) (DDOC) format, the "hashcode" suffix is used to denote that the container was validated in [hashcode mode](http://sertkeskus.github.io/dds-documentation/api/api_docs/#ddoc-format-and-hashcode), i.e. without original data files. <br> **Possible values:**  <br> DIGIDOC_XML_1.0 <br> DIGIDOC_XML_1.0_hashcode <br> DIGIDOC_XML_1.1 <br> DIGIDOC_XML_1.1_hashcode <br> DIGIDOC_XML_1.2 <br> DIGIDOC_XML_1.2_hashcode <br> DIGIDOC_XML_1.3 <br> DIGIDOC_XML_1.3_hashcode <br> ASiC_E - used in case of all ASIC-E ([BDOC](http://id.ee/public/bdoc-spec212-eng.pdf)) documents and X-Road simple containers that don't use batch time-stamping (see [specification document](https://cyber.ee/uploads/2013/05/T-4-23-Profile-for-High-Performance-Digital-Signatures1.pdf))<br> ASiC_E_batchsignature - used in case of X-Road containers with batch signature (see [specification document](https://cyber.ee/uploads/2013/05/T-4-23-Profile-for-High-Performance-Digital-Signatures1.pdf)) <br> ASiC_S - used in case of all ASIC-S documents |
| signatures | Signatures | - | Array | Collection of signatures found in digitally signed document |
| signatures[0] | Signature | + | Object | Signature information object |
| signatures[0]. claimedSigningTime | Signature. ClaimedSigningTime | + | Date | Claimed signing time, i.e. signer's computer time during signature creation |
| signatures[0].id | Signature.Id | + | String | Signature ID attribute  |
| signatures[0].indication | Signature.Indication | + | String | Overall result of the signature's validation process, according to [ETSI EN 319 102-1](http://www.etsi.org/deliver/etsi_en/319100_319199/31910201/01.01.01_60/en_31910201v010101p.pdf) "Table 5: Status indications of the signature validation process". <br> Note that the validation results of different signatures in one signed document (signature container) may vary. <br> See also `validSignaturesCount` and `SignaturesCount` fields. <br>**Possible values:** <br> TOTAL-PASSED <br> TOTAL-FAILED <br> INDETERMINATE |
| signatures[0]. subIndication | Signature. SubIndication | - | String | Additional subindication in case of failed or indeterminate validation result, according to [ETSI EN 319 102-1](http://www.etsi.org/deliver/etsi_en/319100_319199/31910201/01.01.01_60/en_31910201v010101p.pdf) "Table 6: Validation Report Structure and Semantics" |
| signatures[0].errors | Signature.Errors | - | Array | Information about validation error(s), array of error messages.  |
| signatures[0].errors[0] | Signature.Errors. Error | + | Object | Object containing the error |
| signatures[0].errors[0].  content | Signature.Errors. Error.Content | + | String | Error message, as returned by the base library that was used for signature validation. |
| signatures[0].info | Signature.Info | - | Object | Object containing trusted signing time information and user added additional signing info. |
| signatures[0].info. bestSignatureTime | Signature.Info. BestSignatureTime | + | Date | Time value that is regarded as trusted signing time, denoting the earliest time when it can be trusted by the validation application (because proven by some Proof-of-Existence present in the signature) that a signature has existed.<br>The source of the value depends on the signature profile (see also `SignatureFormat` parameter):<br>- Signature with time-mark (LT_TM level) - the producedAt value of the earliest valid time-mark (OCSP confirmation of the signer's certificate) in the signature.<br>- Signature with time-stamp (LT or LTA level) - the genTime value of the earliest valid signature time-stamp token in the signature. <br> - Signature with BES or EPES level - the value is empty, i.e. there is no trusted signing time value available. |
| signatures[0].info. timeAssertionMessageImprint | Signature.Info. TimeAssertionMessageImprint | - | String | Base64 encoded value of message imprint retrieved from time assertion. In case of LT_TM (TimeMark) signatures, OCSP nonce value is returned. In case of T, LT or LTA (TimeStamp) signatures, TimeStamp message imprint is returned. |
| signatures[0].info. signerRole | Signature.Info. SignerRole | - | Array | Array of roles attached to the signature. |
| signatures[0].info. signerRole[0] | Signature.Info. SignerRole[0] | + | Object | Object containing claimed roles. |
| signatures[0].info. signerRole[0]. claimedRole | Signature.Info. SignerRole[0].ClaimedRole | + | String | Role stated by signer on signing. |
| signatures[0].info. signatureProductionPlace | Signature.Info. SignatureProductionPlace | - | Object | Object containing stated signing location info. |
| signatures[0].info. signatureProductionPlace.countryName | Signature.Info. SignatureProductionPlace.CountryName | - | String | Stated signing country. |
| signatures[0].info. signatureProductionPlace.stateOrProvince | Signature.Info. SignatureProductionPlace.StateOrProvince | - | String | Stated state or province. |
| signatures[0].info. signatureProductionPlace.city | Signature.Info. SignatureProductionPlace.City | - | String | Stated city. |
| signatures[0].info. signatureProductionPlace.postalCode | Signature.Info. SignatureProductionPlace.PostalCode | - | String | Stated postal code. |
| signatures[0]. signatureFormat | Signature. SignatureFormat | + | String | Format and profile (according to Baseline Profile) of the signature. See [XAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103171/02.01.01_60/ts_103171v020101p.pdf), [CAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103173/02.02.01_60/ts_103173v020201p.pdf) and [PAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf) for detailed description of the Baseline Profile levels. Levels that are accepted in SiVa validation policy are described in [SiVa signature validation policy](/siva/appendix/validation_policy) <br>**Possible values:**  <br> XAdES_BASELINE_B <br> XAdES_BASELINE_B_BES <br> XAdES_BASELINE_B_EPES <br> XAdES_BASELINE_T <br> XAdES_BASELINE_LT - long-term level XAdES signature where time-stamp is used as a assertion of trusted signing time<br> XAdES_BASELINE_LT_TM - long-term level XAdES signature where time-mark is used as a assertion of trusted signing time. Used in case of [BDOC](http://id.ee/public/bdoc-spec212-eng.pdf) signatures with time-mark profile and [DIGIDOC-XML](http://id.ee/public/DigiDoc_format_1.3.pdf) (DDOC) signatures.<br>  XAdES_BASELINE_LTA <br> CAdES_BASELINE_B <br> CAdES_BASELINE_T <br> CAdES_BASELINE_LT <br> CAdES_BASELINE_LTA<br> PAdES_BASELINE_B <br> PAdES_BASELINE_T <br> PAdES_BASELINE_LT <br> PAdES_BASELINE_LTA |
| signatures[0]. signatureMethod | Signature. SignatureMethod | + | String | Signature method specification URI used in signature creation. |
| signatures[0]. signatureLevel | Signature. SignatureLevel | - |String | Legal level of the signature, according to Regulation (EU) No 910/2014. <br> - **Possible values on positive validation result:**<br> QESIG <br> QESEAL <br> QES <br> ADESIG_QC <br> ADESEAL_QC <br> ADES_QC <br> ADESIG <br> ADESEAL <br> ADES <br> - **Possible values on indeterminate validation result:**<br> prefix INDETERMINATE is added to the level described in positive result. For example  INDETERMINATE_QESIG <br> - **Possible values on negative validation result:**<br>In addition to abovementioned<br> NOT_ADES_QC_QSCD <br> NOT_ADES_QC <br> NOT_ADES <br> NA <br> - In case of DIGIDOC-XML 1.0..1.3 formats, value is missing as the signature level is not checked by the JDigiDoc base library that is used for validation. However, the signatures can be indirectly regarded as QES level signatures, see also [SiVa Validation Policy](/siva3/appendix/validation_policy)<br> - In case of XROAD ASICE containers the value is missing as the asicverifier base library do not check the signature level.|
| signatures[0].signedBy | Signature.SignedBy | + | String | Signers name and identification number, i.e. value of the CN field of the signer's certificate |
| signatures[0].subjectDistinguishedName.serialNumber | Signature.SubjectDistinguishedName.SerialNumber | - | String | SERIALNUMBER value portion in signer's certificate's subject distinguished name |
| signatures[0].subjectDistinguishedName.commonName | Signature.SubjectDistinguishedName.CommonName | - | String | CN (common name) value portion in signer's certificate's subject distinguished name |
| signatures[0]. signatureScopes | Signature. SignatureScopes | - | Array | Contains information of the original data that is covered by the signature. |
| signatures[0]. signatureScopes[0]. name | Signature. SignatureScopes.  SignatureScope.Name | + | String | Name of the signature scope. |
| signatures[0]. signatureScopes[0]. scope | Signature. SignatureScopes.  SignatureScope. Scope | + | String | Type of the signature scope. |
| signatures[0]. signatureScopes[0]. content | Signature. SignatureScopes.  SignatureScope. Content | + | String | Description of the scope. |
| signatures[0]. signatureScopes[0]. hashAlgo | Signature. SignatureScopes.  SignatureScope. HashAlgo | - | String | Hash algorithm used for datafile hash calculation. Present for hashcode validation. |
| signatures[0]. signatureScopes[0]. hash | Signature. SignatureScopes.  SignatureScope. Hash | - | String | Hash of data file encoded in Base64. Present for hashcode validation. |
| signatures[0]. warnings | Signature.Warnings | - | Array | Block of validation warnings that do not affect the overall validation result. |
| signatures[0]. warnings[0] | Signature.Warnings. Warning | + | Object | Object containing the warning |
| signatures[0]. warnings[0]. content | Signature.Warnings. Warning.Description | + | String | Warning description, as retuned by the base library that was used for validation. |
| signatures[0].certificates | Signature.Certificates | - | Array | Array containing certificates that are present in the signature or can be fetched from TSL. |
| signatures[0].certificates[0] | Signature.Certificates.Certificate | + | Object | Object containinig certificate type, common name and certificate. Minimal object is signer certificate. If present contains certificates for TimeStamps and OCSP as well. |
| signatures[0].certificates[0].commonName | Signature.Certificates.Certificate.CommonName | + | String | CN (common name) value in certificate. |
| signatures[0].certificates[0].type | Signature.Certificates.Certificate.Type | + | String | Type of the certificate. Can be SIGNING, REVOCATION, SIGNATURE_TIMESTAMP, ARCHIVE_TIMESTAMP or CONTENT_TIMESTAMP. |
| signatures[0].certificates[0].content | Signature.Certificates.Certificate.Content | + | String | DER encoded X.509 certificate in Base64. |
| signatures[0].certificates[0].issuer | Signature.Certificates.Certificate.Issuer | + | String | Object containing issuer certificate information. Can create chain til the trust anchor. |
| timeStampTokens | TimeStampTokens | - | Array | Array containing the time stamp tokens |
| timeStampTokens[0]. | TimeStampToken | + | Object | Object containing the time stamp token (TST) |
| timeStampTokens[0]. indication | TimeStampToken. Indication | + | String | Result of the time stamp token validation. <br>**Possible values:** <br> TOTAL-PASSED <br> TOTAL-FAILED |
| timeStampTokens[0]. signedBy | TimeStampToken. SignedBy | + | String | Signer of the time stamp token. |
| timeStampTokens[0]. signedTime | TimeStampToken. SignedTime | + | String | Time when the time stamp token was given. |
| timeStampTokens[0]. error | TimeStampToken. Errors| - | Array | Errors returned in time stamp token validation. |
| timeStampTokens[0]. error[0] | Errors. Error | + | Object | Object containing the error. |
| timeStampTokens[0]. error[0]. content | Error. Content | + | String | Error description. |

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

#### Sample SOAP response Simple Report (successful scenario)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:ValidateDocumentResponse xmlns:ns2="http://soap.webapp.siva.openeid.ee/" xmlns:ns3="http://soap.webapp.siva.openeid.ee/response/" xmlns:ns4="http://dss.esig.europa.eu/validation/detailed-report" xmlns:ns5="http://dss.esig.europa.eu/validation/diagnostic" xmlns:ns6="http://x-road.eu/xsd/identifiers" xmlns:ns7="http://x-road.eu/xsd/xroad.xsd">
      <ns3:ValidationReport>
        <ns3:ValidationConclusion>
          <ns3:Policy>
            <ns3:PolicyDescription>Policy according most common requirements of Estonian Public Administration, to validate Qualified Electronic Signatures and Electronic Seals with Qualified Certificates (according to Regulation (EU) No 910/2014, aka eIDAS). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive validation result, with exception for seals, where AdES/QC and above will produce positive result. Signatures and Seals which are not compliant with ETSI standards (referred by eIDAS) may produce unknown or invalid validation result. Validation process is based on eIDAS Article 32 and referred ETSI standards.</ns3:PolicyDescription>
            <ns3:PolicyName>POLv4</ns3:PolicyName>
            <ns3:PolicyUrl>http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4</ns3:PolicyUrl>
          </ns3:Policy>
          <ns3:ValidationTime>2020-06-15T11:45:52Z</ns3:ValidationTime>
          <ns3:ValidatedDocument>
            <ns3:Filename>singleValidSignatureTS.asice</ns3:Filename>
          </ns3:ValidatedDocument>
          <ns3:ValidationLevel>ARCHIVAL_DATA</ns3:ValidationLevel>
          <ns3:ValidationWarnings/>
          <ns3:SignatureForm>ASiC-E</ns3:SignatureForm>
          <ns3:Signatures>
            <ns3:Signature>
              <ns3:Id>id-a9ce7f66cff1d17ddaab37c46a88f5f4</ns3:Id>
              <ns3:SignatureFormat>XAdES_BASELINE_LT</ns3:SignatureFormat>
              <ns3:SignatureMethod>http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256</ns3:SignatureMethod>
              <ns3:SignatureLevel>QESIG</ns3:SignatureLevel>
              <ns3:SignedBy>JÕEORG,JAAK-KRISTJAN,38001085718</ns3:SignedBy>
              <ns3:SubjectDistinguishedName>
                <ns3:SerialNumber>PNOEE-38001085718</ns3:SerialNumber>
                <ns3:CommonName>JÕEORG,JAAK-KRISTJAN,38001085718</ns3:CommonName>
              </ns3:SubjectDistinguishedName>
              <ns3:Certificates>
                <ns3:Certificate>
                  <ns3:Content>MIIE...</ns3:Content>
                  <ns3:CommonName>DEMO of SK TSA 2014</ns3:CommonName>
                  <ns3:Type>SIGNATURE_TIMESTAMP</ns3:Type>
                </ns3:Certificate>
                <ns3:Certificate>
                  <ns3:Content>MIIE...</ns3:Content>
                  <ns3:CommonName>JÕEORG,JAAK-KRISTJAN,38001085718</ns3:CommonName>
                  <ns3:Issuer>
                    <ns3:Content>MIIE...</ns3:Content>
                    <ns3:CommonName>TEST of ESTEID2018</ns3:CommonName>
                  </ns3:Issuer>
                  <ns3:Type>SIGNING</ns3:Type>
                </ns3:Certificate>
                <ns3:Certificate>
                  <ns3:Content>MIIE...</ns3:Content>
                  <ns3:CommonName>TEST of SK OCSP RESPONDER 2011</ns3:CommonName>
                  <ns3:Type>REVOCATION</ns3:Type>
                </ns3:Certificate>
              </ns3:Certificates>
              <ns3:Indication>TOTAL-PASSED</ns3:Indication>
              <ns3:SubIndication/>
              <ns3:Errors/>
              <ns3:SignatureScopes>
                <ns3:SignatureScope>
                  <ns3:Name>test.txt</ns3:Name>
                  <ns3:Scope>FULL</ns3:Scope>
                  <ns3:Content>Full document</ns3:Content>
                </ns3:SignatureScope>
              </ns3:SignatureScopes>
              <ns3:ClaimedSigningTime>2020-05-21T13:56:52Z</ns3:ClaimedSigningTime>
              <ns3:Warnings/>
              <ns3:Info>
                <ns3:OcspResponseCreationTime>2020-05-21T13:56:49Z</ns3:OcspResponseCreationTime>
                <ns3:TimestampCreationTime>2020-05-21T13:56:48Z</ns3:TimestampCreationTime>
                <ns3:BestSignatureTime>2020-05-21T13:56:48Z</ns3:BestSignatureTime>
                <ns3:TimeAssertionMessageImprint>MDEwDQYJYIZIAWUDBAIBBQAEID3j1ceryQp4ZNP8iVfd50l/0JXvpry+XS+ajiAUA+Su</ns3:TimeAssertionMessageImprint>
              </ns3:Info>
            </ns3:Signature>
          </ns3:Signatures>
          <ns3:ValidSignaturesCount>1</ns3:ValidSignaturesCount>
          <ns3:SignaturesCount>1</ns3:SignaturesCount>
        </ns3:ValidationConclusion>
      </ns3:ValidationReport>
    </ns2:ValidateDocumentResponse>
  </soap:Body>
</soap:Envelope>
```

### Validation response parameters Detailed Report (successful scenario)

General structure of validation response.

| JSON parameter | SOAP parameter | Mandatory |  JSON data type | Description |
|----------------|----------------|-----------|-----------------|-------------|
| validationReport | ValidationReport |  + | Object | Object containing SIVA validation report. |
| validationReport. validationConclusion | ValidationReport. ValidationConclusion |  + | Object | Object containing information of the validation conclusion. The same object that is present in Simple Report. |
| validationReport. validationProcess | ValidationReport. ValidationProcess | - | Object | Object containing information of the validation process. This block is present only on DSS library based validations and is built on DSS detailed report. For more information visit [DSS documentation](https://github.com/esig/dss/blob/develop/dss-cookbook/src/main/asciidoc/dss-documentation.adoc#validation-process).  |
| validationReportSignature | ValidationReportSignature |  - | String | Base64 string of ASIC-E container that includes the detailed report and is signed by the validation service provider |

### Validation response parameters for Diagnostic Data Report (successful scenario)

General structure of validation response.

| JSON parameter | SOAP parameter | Mandatory |  JSON data type | Description |
|----------------|----------------|-----------|-----------------|-------------|
| validationReport | ValidationReport |  + | Object | Object containing SIVA validation report. |
| validationReport. validationConclusion | ValidationReport. ValidationConclusion |  + | Object | Object containing information of the validation conclusion. The same object that is present in Simple Report and Detailed Report. |
| validationReport. diagnosticData | ValidationReport. DiagnosticData | - | Object | Object containing diagnostic data about the information contained in the signature itself, it's revocation data and mathematical validity. This block is present only on DSS library based validations (excluding hashcode validation) and is built on DSS diagnostic data. For more information visit [DSS documentation](https://github.com/esig/dss/blob/develop/dss-cookbook/src/main/asciidoc/dss-documentation.adoc#validation-process).  |

### Sample JSON response (error situation)
In case of error (when validation report is not returned) status code 400 is returned together with following message body:

```json
{"requestErrors": [{
    "message": "Document malformed or not matching documentType",
    "key": "document"
}]}
```

### Sample SOAP response (error situation)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <soap:Fault>
      <faultcode>soap:Server</faultcode>
      <faultstring>Document malformed or not matching documentType</faultstring>
    </soap:Fault>
  </soap:Body>
</soap:Envelope>
```

## Data files request interface


** REST JSON Endpoint **

```
POST https://<server url>/getDataFiles
```

** SOAP Endpoint **
```
POST https://<server url>/soap/dataFilesWebService/getDocumentDataFiles
```

** SOAP WSDL **
```
POST https://<server url>/soap/dataFilesWebService/getDocumentDataFiles?wsdl
```

### Data files request parameters

Data files request parameters for JSON and SOAP interfaces are described in the table below. Data types of SOAP parameters are defined in the [SiVa WSDL document](/siva/appendix/wsdl).

| JSON parameter | SOAP parameter | Mandatory | JSON data type | Description |
|----------------|----------------|-----------|----------------|-------------|
| document | Document | + |  String | Base64 encoded string of digitally signed DDOC document |
| filename | Filename | + |  String | File name of the digitally signed document (i.e. sample.ddoc), max length 255 characters. Currently only DDOC file format is supported for this operation|

### Sample JSON request

```json
{
  "filename":"sample.ddoc",
  "document":"PD94bWwgdmVyc2lvbj0iMS4...."
}
```


### Sample SOAP request

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://soap.webapp.siva.openeid.ee/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:GetDocumentDataFiles>
      <soap:DataFilesRequest>
        <Document>PD94bWwgdmVyc2lvbj0iMS4wI...</Document>
        <Filename>sample.ddoc</Filename>
      </soap:DataFilesRequest>
    </soap:GetDocumentDataFiles>
  </soapenv:Body>
</soapenv:Envelope>
```


## Data files response interface

### Data files response parameters (successful scenario)

The data file extraction report (i.e. the data files response) for JSON and SOAP interfaces is described in the table below. Data types of SOAP parameters are defined in the [SiVa WSDL document](/siva/appendix/wsdl).
SiVa returns all data files as they are extracted by JDigiDoc library in an as is form. No extra operations or validations are done.

| JSON parameter | SOAP parameter | Mandatory | JSON data type | Description |
|----------------|----------------|-----------|----------------|-------------|
| dataFiles | DataFiles | - |  Array | Collection of data files found in digitally signed document |
| dataFiles[0] | DataFile | + | Object | Extracted data file object |
| dataFiles[0].fileName | DataFile.FileName | - |  String | File name of the extracted data file |
| dataFiles[0].size | DataFile.Size | - | Long | Extracted data file size in bytes |
| dataFiles[0].base64 | DataFile.Base64 | - |String | Base64 encoded string of extracted data file |
| dataFiles[0].mimeType | DataFile.MimeType |  - | String | MIME type of the extracted data file  |

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

### Sample SOAP response (successful scenario)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:GetDocumentDataFilesResponse xmlns:ns2="http://soap.webapp.siva.openeid.ee/" xmlns:ns3="http://x-road.eu/xsd/identifiers" xmlns:ns4="http://x-road.eu/xsd/xroad.xsd">
      <ns2:DataFilesReport>
       <DataFiles>
         <DataFile>
           <Base64>UCgUCgUCgUCgUCgUCgUCgUCgUCgUCgUCgUH...</Base64>
           <FileName>Glitter-rock-4_gallery.jpg</FileName>
           <MimeType>application/octet-stream</MimeType>
           <Size>41114</Size>
         </DataFile>
       </DataFiles>
     </ns2:DataFilesReport>
   </ns2:GetDocumentDataFilesResponse>
  </soap:Body>
</soap:Envelope>
```

### Sample JSON response (error situation)
In case of error (when datafiles are not returned) status code 400 is returned together with following message body:

```json
{"requestErrors": [{
    "message": "Invalid document type. Can only return data files for DDOC type containers.",
    "key": "documentType"
}]}
```

### Sample SOAP response (error situation)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <soap:Fault>
      <faultcode>soap:Client</faultcode>
      <faultstring>Invalid document type. Can only return data files for DDOC type containers.</faultstring>
    </soap:Fault>
  </soap:Body>
</soap:Envelope>
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

| Response type | Parameter | Change | Link | Comment |
|---------------|-----------|--------|------|---------|
| Simple | validatedDocument | now optional |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Object now optional |
| Simple | validatedDocument.filename | now optional |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | String now optional |
| Simple | validatedDocument.fileHash | changed |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Previously validatedDocument.fileHashInHex. Now contains Base64 value |
| Simple | signatures[0].subjectDistinguishedName.serialNumber | added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Added signers serial number field |
| Simple | signatures[0].subjectDistinguishedName.commonName | added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Added signers common name field |
| Simple | signatures[0].signatureScopes[0].hashAlgo | added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Added datafile hash algo field for hashcode validation |
| Simple | signatures[0].signatureScopes[0].hash | added |  [Link](../interfaces/#validation-response-parameters-simple-report-successful-scenario) | Added datafile hash field for hashcode validation |
| Diagnostic | whole response | added |  [Link](../interfaces/#validation-response-parameters-for-diagnostic-data-report-successful-scenario) | New report type added |

