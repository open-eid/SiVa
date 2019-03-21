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
			"dataFiles": [
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
			"dataFiles": [
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
| signatures[0].info | Signature.Info | - | Object | Object containing trusted signing time information.  |
| signatures[0].info. bestSignatureTime | Signature.Info. BestSignatureTime | + | Date | Time value that is regarded as trusted signing time, denoting the earliest time when it can be trusted by the validation application (because proven by some Proof-of-Existence present in the signature) that a signature has existed.<br>The source of the value depends on the signature profile (see also `SignatureFormat` parameter):<br>- Signature with time-mark (LT_TM level) - the producedAt value of the earliest valid time-mark (OCSP confirmation of the signer's certificate) in the signature.<br>- Signature with time-stamp (LT or LTA level) - the genTime value of the earliest valid signature time-stamp token in the signature. <br> - Signature with BES or EPES level - the value is empty, i.e. there is no trusted signing time value available. |
| signatures[0]. signatureFormat | Signature. SignatureFormat | + | String | Format and profile (according to Baseline Profile) of the signature. See [XAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103171/02.01.01_60/ts_103171v020101p.pdf), [CAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103173/02.02.01_60/ts_103173v020201p.pdf) and [PAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf) for detailed description of the Baseline Profile levels. Levels that are accepted in SiVa validation policy are described in [SiVa signature validation policy](/siva/appendix/validation_policy) <br>**Possible values:**  <br> XAdES_BASELINE_B <br> XAdES_BASELINE_B_BES <br> XAdES_BASELINE_B_EPES <br> XAdES_BASELINE_T <br> XAdES_BASELINE_LT - long-term level XAdES signature where time-stamp is used as a assertion of trusted signing time<br> XAdES_BASELINE_LT_TM - long-term level XAdES signature where time-mark is used as a assertion of trusted signing time. Used in case of [BDOC](http://id.ee/public/bdoc-spec212-eng.pdf) signatures with time-mark profile and [DIGIDOC-XML](http://id.ee/public/DigiDoc_format_1.3.pdf) (DDOC) signatures.<br>  XAdES_BASELINE_LTA <br> CAdES_BASELINE_B <br> CAdES_BASELINE_T <br> CAdES_BASELINE_LT <br> CAdES_BASELINE_LTA<br> PAdES_BASELINE_B <br> PAdES_BASELINE_T <br> PAdES_BASELINE_LT <br> PAdES_BASELINE_LTA |
| signatures[0]. signatureLevel | Signature. SignatureLevel | - |String | Legal level of the signature, according to Regulation (EU) No 910/2014. <br> - **Possible values on positive validation result:**<br> QESIG <br> QESEAL <br> QES <br> ADESIG_QC <br> ADESEAL_QC <br> ADES_QC <br> ADESIG <br> ADESEAL <br> ADES <br> - **Possible values on indeterminate validation result:**<br> prefix INDETERMINATE is added to the level described in positive result. For example  INDETERMINATE_QESIG <br> - **Possible values on negative validation result:**<br>In addition to abovementioned<br> NOT_ADES_QC_QSCD <br> NOT_ADES_QC <br> NOT_ADES <br> NA <br> - In case of DIGIDOC-XML 1.0..1.3 formats, value is missing as the signature level is not checked by the JDigiDoc base library that is used for validation. However, the signatures can be indirectly regarded as QES level signatures, see also [SiVa Validation Policy](/siva3/appendix/validation_policy)<br> - In case of XROAD ASICE containers the value is missing as the asicverifier base library do not check the signature level.|
| signatures[0].signedBy | Signature.SignedBy | + | String | Signers name and identification number, i.e. value of the CN field of the signer's certificate |
| signatures[0]. signatureScopes | Signature. SignatureScopes | - | Array | Contains information of the original data that is covered by the signature. |
| signatures[0]. signatureScopes[0]. name | Signature. SignatureScopes.  SignatureScope.Name | + | String | Name of the signature scope. |
| signatures[0]. signatureScopes[0]. scope | Signature. SignatureScopes.  SignatureScope. Scope | + | String | Type of the signature scope. |
| signatures[0]. signatureScopes[0]. content | Signature. SignatureScopes.  SignatureScope. Content | + | String | Description of the scope. |
| signatures[0]. signatureScopes[0]. hashAlgo | Signature. SignatureScopes.  SignatureScope. HashAlgo | - | String | Hash algorithm used for datafile hash calculation. Present for hashcode validation. |
| signatures[0]. signatureScopes[0]. hash | Signature. SignatureScopes.  SignatureScope. Hash | - | String | Hash of data file encoded in Base64. Present for hashcode validation. |
| signatures[0]. warnings | Signature.Warnings | - | Array | Block of validation warnings that do not affect the overall validation result. |
| signatures[0]. warnings[0] | Signature.Warnings. Warning | + | Object | Object containing the warning |
| signatures[0]. warnings[0]. content | Signature.Warnings. Warning.Description | + | String | Warning description, as retuned by the base library that was used for validation. |
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
{"validationReport":
{"validationConclusion": {
    "validationTime": "2017-11-07T08:14:07Z",
    "signaturesCount": 1,
    "validationLevel": "ARCHIVAL_DATA",
    "validatedDocument": {
        "filename": "ValidLiveSignature.asice",
        "fileHashInHex": "0A805C920603750E0B427C3F25D7B22DCEC183DEF3CA14BE9A2D4488887DD501",
        "hashAlgo": "SHA-256"
    },
    "validSignaturesCount": 1,
    "signatures": [{
        "signatureFormat": "XAdES_BASELINE_LT",
        "signedBy": "NURM,AARE,38211015222",
        "claimedSigningTime": "2016-10-11T09:35:48Z",
        "signatureLevel": "QESIG",
        "warnings": [{"content": "The trusted list is not fresh!"}],
        "signatureScopes": [{
            "scope": "FullSignatureScope",
            "name": "Tresting.txt",
            "content": "Full document"
        }],
        "id": "S0",
        "indication": "TOTAL-PASSED",
        "info": {"bestSignatureTime": "2016-10-11T09:36:10Z"}
    }],
    "policy": {
        "policyDescription": "Policy for validating Qualified Electronic Signatures and Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive validation result.",
        "policyUrl": "http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4",
        "policyName": "POLv4"
    },
    "signatureForm": "ASiC_E"
}}}
```

#### Sample SOAP response Simple Report (successful scenario)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:ValidateDocumentResponse xmlns:ns2="http://soap.webapp.siva.openeid.ee/" xmlns:ns3="http://dss.esig.europa.eu/validation/detailed-report" xmlns:ns4="http://x-road.eu/xsd/identifiers" xmlns:ns5="http://x-road.eu/xsd/xroad.xsd">
      <ns2:ValidationReport>
        <ns2:ValidationConclusion>
          <Policy>
            <PolicyDescription>Policy for validating Qualified Electronic Signatures and Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive validation result.</PolicyDescription>
            <PolicyName>POLv4</PolicyName>
            <PolicyUrl>http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv4</PolicyUrl>
          </Policy>
          <ValidationTime>2017-11-07T08:14:07Z</ValidationTime>
          <ValidatedDocument>
            <Filename>ValidLiveSignature.asice</Filename>
            <FileHashInHex>0A805C920603750E0B427C3F25D7B22DCEC183DEF3CA14BE9A2D4488887DD501</FileHashInHex>
            <HashAlgo>SHA-256</HashAlgo>
          </ValidatedDocument>
          <ValidationLevel>ARCHIVAL_DATA</ValidationLevel>
          <ValidationWarnings/>
          <SignatureForm>ASiC_E</SignatureForm>
          <Signatures>
            <Signature>
              <Id>S0</Id>
              <SignatureFormat>XAdES_BASELINE_LT</SignatureFormat>
              <SignatureLevel>QESIG</SignatureLevel>
              <SignedBy>NURM,AARE,38211015222</SignedBy>
              <Indication>TOTAL-PASSED</Indication>
              <SubIndication/>
              <Errors/>
              <SignatureScopes>
                <SignatureScope>
                  <Name>Tresting.txt</Name>
                  <Scope>FullSignatureScope</Scope>
                  <Content>Full document</Content>
                </SignatureScope>
              </SignatureScopes>
              <ClaimedSigningTime>2016-10-11T09:35:48Z</ClaimedSigningTime>
              <Warnings>
                <Warning>
                  <Content>The trusted list is not fresh!</Content>
                </Warning>
              </Warnings>
              <Info>
                <bestSignatureTime>2016-10-11T09:36:10Z</bestSignatureTime>
              </Info>
            </Signature>
          </Signatures>
          <ValidSignaturesCount>1</ValidSignaturesCount>
          <SignaturesCount>1</SignaturesCount>
        </ns2:ValidationConclusion>
      </ns2:ValidationReport>
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

#### Sample JSON response Detailed Report (successful scenario). The report is shortened but gives general overview of structure.

```json
{
    "validationReport": {
        "validationProcess": {
            "qmatrixBlock": {
                "tlanalysis": [{
                        "conclusion": {"indication": "PASSED"},
                        "countryCode": "EU",
                        "constraint": [
                            {
                                "name": {
                                    "nameId": "QUAL_TL_FRESH",
                                    "value": "Is the trusted list fresh ?"
                                },
                                "status": "OK"
                            },
                         ...
                        ]
                    },
                    {
                        "conclusion": {"indication": "PASSED"},
                        "countryCode": "EE",
                        "constraint": [{
                                "name": {
                                    "nameId": "QUAL_TL_FRESH",
                                    "value": "Is the trusted list fresh ?"
                                },
                                "status": "OK"
                            },
                           ...
                        ]
                    }
                ],
                "signatureAnalysis": [{
                    "conclusion": {"indication": "PASSED"},
                    "signatureQualification": "QESIG",
                    "constraint": [{
                            "name": {
                                "nameId": "QUAL_IS_ADES",
                                "value": "Is the signature/seal an acceptable AdES (ETSI EN 319 102-1) ?"
                            },
                            "status": "OK"
                        },
                      ...
                    ],
                    "id": "S0"
                }]
            },
            "basicBuildingBlocks": [{
                    "conclusion": {"indication": "PASSED"},
                    "cv": {
                        "conclusion": {"indication": "PASSED"},
                        "constraint": [{
                                "name": {
                                    "nameId": "BBB_CV_IRDOF",
                                    "value": "Is the reference data object(s) found?"
                                },
                                "status": "OK"
                            },
                            ...
                        ]},
                    ...
                    "id": "1561CD6BEA97B0A72664067021330509894BE1EBA586D3057D77787E5F4180A4",
                    "type": "TIMESTAMP"
                },
                ...
            "signatures": [{
                "validationProcessArchivalData": {
                    "conclusion": {"indication": "PASSED"},
                    "constraint": [{
                        "name": {
                            "nameId": "ARCH_LTVV",
                            "value": "Is the result of the LTV validation process acceptable?"
                        },
                        "status": "OK"
                    }]},
                ...
                },
                "id": "S0",
                "validationProcessLongTermData": {
                    "conclusion": {"indication": "PASSED"},
                    "constraint": [{
                            "name": {
                                "nameId": "LTV_ABSV",
                                "value": "Is the result of the Basic Validation Process acceptable?"},
                            "status": "OK"
                            },
                        ...
                        }]}}]
        },
        "validationConclusion": {
            "validationTime": "2017-11-07T09:20:18Z",
            "signaturesCount": 1,
            "validationLevel": "ARCHIVAL_DATA",
            "validatedDocument": {
                "filename": "ValidLiveSignature.asice",
                "fileHashInHex": "0A805C920603750E0B427C3F25D7B22DCEC183DEF3CA14BE9A2D4488887DD501",
                "hashAlgo": "SHA-256"
            },
            "validSignaturesCount": 1,
            "signatures": [{
                "signatureFormat": "XAdES_BASELINE_LT",
                "signedBy": "NURM,AARE,38211015222",
                "claimedSigningTime": "2016-10-11T09:35:48Z",
                "signatureLevel": "QESIG",
                "signatureScopes": [{
                    "scope": "FullSignatureScope",
                    "name": "Tresting.txt",
                    "content": "Full document"
                }],
                "id": "S0",
                "indication": "TOTAL-PASSED",
                "info": {"bestSignatureTime": "2016-10-11T09:36:10Z"}
            }],
            "policy": {
                "policyDescription": "Policy for validating Qualified Electronic Signatures and Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive validation result.",
                "policyUrl": "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv4",
                "policyName": "POLv4"
            },
            "signatureForm": "ASiC_E"
        }
    },
    "validationReportSignature": "UEsDBBQACAgIAIlaZ0sAA..."
}

```

#### Sample SOAP response Detailed Report (successful scenario)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:ValidateDocumentResponse xmlns:ns2="http://soap.webapp.siva.openeid.ee/" xmlns:ns3="http://dss.esig.europa.eu/validation/detailed-report" xmlns:ns4="http://x-road.eu/xsd/identifiers" xmlns:ns5="http://x-road.eu/xsd/xroad.xsd">
      <ns2:ValidationReport>
        <ns2:ValidationConclusion>
          <Policy>
            <PolicyDescription>Policy for validating Qualified Electronic Signatures and Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive validation result.</PolicyDescription>
            <PolicyName>POLv4</PolicyName>
            <PolicyUrl>http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv4</PolicyUrl>
          </Policy>
          <ValidationTime>2017-11-07T09:20:18Z</ValidationTime>
          <ValidatedDocument>
            <Filename>ValidLiveSignature.asice</Filename>
            <FileHashInHex>0A805C920603750E0B427C3F25D7B22DCEC183DEF3CA14BE9A2D4488887DD501</FileHashInHex>
            <HashAlgo>SHA-256</HashAlgo>
          </ValidatedDocument>
          <ValidationLevel>ARCHIVAL_DATA</ValidationLevel>
          <ValidationWarnings/>
          <SignatureForm>ASiC-E</SignatureForm>
          <Signatures>
            <Signature>
              <Id>S0</Id>
              <SignatureFormat>XAdES_BASELINE_LT</SignatureFormat>
              <SignatureLevel>QESIG</SignatureLevel>
              <SignedBy>NURM,AARE,38211015222</SignedBy>
              <Indication>TOTAL-PASSED</Indication>
              <SubIndication/>
              <Errors/>
              <SignatureScopes>
                <SignatureScope>
                  <Name>Tresting.txt</Name>
                  <Scope>FullSignatureScope</Scope>
                  <Content>Full document</Content>
                </SignatureScope>
              </SignatureScopes>
              <ClaimedSigningTime>2016-10-11T09:35:48Z</ClaimedSigningTime>
              <Warnings/>
              <Info>
                <bestSignatureTime>2016-10-11T09:36:10Z</bestSignatureTime>
              </Info>
            </Signature>
          </Signatures>
          <ValidSignaturesCount>1</ValidSignaturesCount>
          <SignaturesCount>1</SignaturesCount>
        </ns2:ValidationConclusion>
        <ns2:ValidationProcess>
          <ns3:Signatures Id="S0">
            <ns3:ValidationProcessBasicSignatures>
              <ns3:Constraint Id="S0">
                <ns3:Name NameId="ADEST_ROBVPIIC">Is the result of the Basic Validation Process conclusive?</ns3:Name>
                <ns3:Status>OK</ns3:Status>
              </ns3:Constraint>
              <ns3:Conclusion>
                <ns3:Indication>PASSED</ns3:Indication>
              </ns3:Conclusion>
            </ns3:ValidationProcessBasicSignatures>
            ...
            <ns3:ValidationProcessArchivalData>
              <ns3:Constraint>
                <ns3:Name NameId="ARCH_LTVV">Is the result of the LTV validation process acceptable?</ns3:Name>
                <ns3:Status>OK</ns3:Status>
              </ns3:Constraint>
              <ns3:Conclusion>
                <ns3:Indication>PASSED</ns3:Indication>
              </ns3:Conclusion>
            </ns3:ValidationProcessArchivalData>
          </ns3:Signatures>
          ...
          <ns3:BasicBuildingBlocks Id="S0" Type="SIGNATURE">
            <ns3:FC>
              <ns3:Constraint>
                <ns3:Name NameId="BBB_FC_IEFF">Is the expected format found?</ns3:Name>
                <ns3:Status>OK</ns3:Status>
              </ns3:Constraint>
            ...
            <ns3:XCV>
              <ns3:Constraint>
                <ns3:Name NameId="BBB_XCV_CCCBB">Can the certificate chain be built till the trust anchor?</ns3:Name>
                <ns3:Status>OK</ns3:Status>
              </ns3:Constraint>
		  </ns3:BasicBuildingBlocks>
          <ns3:QMatrixBlock>
            <ns3:TLAnalysis CountryCode="EU">
              ...
              <ns3:Constraint>
                <ns3:Name NameId="QUAL_TL_WS">Is the trusted list well signed ?</ns3:Name>
                <ns3:Status>OK</ns3:Status>
              </ns3:Constraint>
              <ns3:Conclusion>
                <ns3:Indication>PASSED</ns3:Indication>
              </ns3:Conclusion>
            </ns3:TLAnalysis>
            ...
            <ns3:SignatureAnalysis Id="S0" SignatureQualification="QESig">
              <ns3:Constraint>
                <ns3:Name NameId="QUAL_IS_ADES">Is the signature/seal an acceptable AdES (ETSI EN 319 102-1) ?</ns3:Name>
                <ns3:Status>OK</ns3:Status>
              </ns3:Constraint>
              ...
              <ns3:Conclusion>
                <ns3:Indication>PASSED</ns3:Indication>
              </ns3:Conclusion>
            </ns3:SignatureAnalysis>
          </ns3:QMatrixBlock>
        </ns2:ValidationProcess>
      </ns2:ValidationReport>
      <ValidationReportSignature>UEsDBBQACAgIAIlaZ0s...</ValidationReportSignature>
    </ns2:ValidateDocumentResponse>
  </soap:Body>
</soap:Envelope>

```

### Validation response parameters for Diagnostic Data Report (successful scenario)

General structure of validation response.

| JSON parameter | SOAP parameter | Mandatory |  JSON data type | Description |
|----------------|----------------|-----------|-----------------|-------------|
| validationReport | ValidationReport |  + | Object | Object containing SIVA validation report. |
| validationReport. validationConclusion | ValidationReport. ValidationConclusion |  + | Object | Object containing information of the validation conclusion. The same object that is present in Simple Report and Detailed Report. |
| validationReport. diagnosticData | ValidationReport. DiagnosticData | - | Object | Object containing diagnostic data about the information contained in the signature itself, it's revocation data and mathematical validity. This block is present only on DSS library based validations (excluding hashcode validation) and is built on DSS diagnostic data. For more information visit [DSS documentation](https://github.com/esig/dss/blob/develop/dss-cookbook/src/main/asciidoc/dss-documentation.adoc#validation-process).  |

#### Sample Diagnostic Data Report JSON response (successful scenario). The report is shortened but gives general overview of structure.

```json
{
  "validationReport": {
    "diagnosticData": {
        "listOfTrustedLists": {
            "sequenceNumber": 237,
            "countryCode": "EU",
            "wellSigned": true,
            "lastLoading": 1553160947541,
            "issueDate": 1553151600000,
            "version": 5,
            "url": "https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl-mp.xml",
            "nextUpdate": 1569024000000
        },
        "usedCertificates": [
            ...,
            {
                "commonName": "NURM,AARE,38211015222",
                "notAfter": 1633381199000,
                "subjectDistinguishedName": [
                    {
                        "format": "CANONICAL",
                        "value": "2.5.4.5=#130b3338323131303135323232,2.5.4.42=#0c0441415245,2.5.4.4=#0c044e55524d,cn=nurm\\,aare\\,38211015222,ou=digital signature,o=esteid (mobiil-id),c=ee"
                    },
                    ...
                ],
                "certificatePolicyIds": [{"value": "1.3.6.1.4.1.10015.1.3"}],
                "issuerDistinguishedName": [
                    {
                        "format": "CANONICAL",
                        "value": "cn=esteid-sk 2015,2.5.4.97=#0c0e4e545245452d3130373437303133,o=as sertifitseerimiskeskus,c=ee"
                    },
                    ...
                ],
                "publicKeyEncryptionAlgo": "ECDSA",
                "qcstatementIds": [
                    ...
                ],
                "notBefore": 1475561770000,
                "crldistributionPoints": ["http://www.sk.ee/crls/esteid/esteid2015.crl"],
                "surname": "NURM",
                "keyUsageBits": ["nonRepudiation"],
                "basicSignature": {
                    ...
                },
                "publicKeySize": 256,
                "selfSigned": false,
                "id": "82905213FDD6FC4129F4C8853F45F8FBA8F3C1C558421DBCCF0AEFE85CA5147F",
                "trustedServiceProviders": [{
                    ...
                }],
                "organizationalUnit": "digital signature",
                "info": [{
                    "id": 0,
                    "value": "No CRL info found !"
                }],
                "signingCertificate": {"id": "74D992D3910BCF7E34B8B5CD28F91EAEB4F41F3DA6394D78B8C43672D43F4F0F"},
                "serialNumber": "99414911336318064438106396713165831388",
                "organizationName": "ESTEID (MOBIIL-ID)",
                "digestAlgoAndValues": [{
                    "digestValue": "gpBSE/3W/EEp9MiFP0X4+6jzwcVYQh28zwrv6FylFH8=",
                    "digestMethod": "SHA256"
                }],
                "certificateChain": [{
                    "source": "TRUSTED_LIST",
                    "id": "74D992D3910BCF7E34B8B5CD28F91EAEB4F41F3DA6394D78B8C43672D43F4F0F"
                }],
                "givenName": "AARE",
                "idPkixOcspNoCheck": false,
                "idKpOCSPSigning": false,
                "revocations": [{
                    "signingCertificate": {"id": "50D62F373742287B5F18319E513C7BCA89BD78788B23DA356124CB90F9A636AF"},
                    "productionDate": 1476178570000,
                    "digestAlgoAndValues": [{
                        "digestValue": "2BQ3gSOylsY/Fc1vrOMMTg8BKj3lo1pN8WtMwO0IJ1M=",
                        "digestMethod": "SHA256"
                    }],
                    "certificateChain": [{
                        "source": "TRUSTED_LIST",
                        "id": "50D62F373742287B5F18319E513C7BCA89BD78788B23DA356124CB90F9A636AF"
                    }],
                    "thisUpdate": 1476178570000,
                    "origin": "SIGNATURE",
                    "basicSignature": {
                        ...
                    },
                    "source": "OCSPToken",
                    "id": "82905213fdd6fc4129f4c8853f45f8fba8f3c1c558421dbccf0aefe85ca5147fd814378123b296c63f15cd6face30c4e0f012a3de5a35a4df16b4cc0ed082753",
                    "status": true
                }],
                "trusted": false,
                "countryName": "EE",
                "base64Encoded": "MIIE3DCCAsSgAwIBAgIQSsqdjzAQgvpX80krgJy83DANBgkqhkiG9w0BAQsFADBjMQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEXMBUGA1UEYQwOTlRSRUUtMTA3NDcwMTMxFzAVBgNVBAMMDkVTVEVJRC1TSyAyMDE1MB4XDTE2MTAwNDA2MTYxMFoXDTIxMTAwNDIwNTk1OVowgZoxCzAJBgNVBAYTAkVFMRswGQYDVQQKDBJFU1RFSUQgKE1PQklJTC1JRCkxGjAYBgNVBAsMEWRpZ2l0YWwgc2lnbmF0dXJlMR4wHAYDVQQDDBVOVVJNLEFBUkUsMzgyMTEwMTUyMjIxDTALBgNVBAQMBE5VUk0xDTALBgNVBCoMBEFBUkUxFDASBgNVBAUTCzM4MjExMDE1MjIyMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEEmQMc8aNpHPBQCV3J2I/VZ1AAIQBilg/wxfNwR6jQNJSua5sYQiM8mZj7thf7HwYrDRrrdw/XbMmaWtkj4ZQJaOCAR0wggEZMAkGA1UdEwQCMAAwDgYDVR0PAQH/BAQDAgZAMFoGA1UdIARTMFEwTwYJKwYBBAHOHwEDMEIwHQYIKwYBBQUHAgIwEQwPQ29udHJhY3QgMS4xMS05MCEGCCsGAQUFBwIBFhVodHRwczovL3d3dy5zay5lZS9jcHMwHQYDVR0OBBYEFBO7ncBMwgpRL5JLrVZ+/6zzcpmEMCIGCCsGAQUFBwEDBBYwFDAIBgYEAI5GAQEwCAYGBACORgEEMB8GA1UdIwQYMBaAFLOriLyZ1WKkhSoIzbQdcjuDckdRMDwGA1UdHwQ1MDMwMaAvoC2GK2h0dHA6Ly93d3cuc2suZWUvY3Jscy9lc3RlaWQvZXN0ZWlkMjAxNS5jcmwwDQYJKoZIhvcNAQELBQADggIBALITavvjDaKHB2tl2AYJmJDvlOCcNpfDiVxbEYkGnWddk7dK2R7Dy25xh2GXeLB6kkDFixgyGRYbn1BUSviFn8hgLp9x10VaL1xP+ziZ0fjZFw0z4jpZk91zqJOKfkliVgbOH/9PoSlB1lFSeijl2jRKCehlYVBJqF9hj7wOKbr8zw+b+o41IiZ+TKJTpO9Il4sKI0l/xCD1lSPMwxnhRZxAOb9gXeTYG8Opdh4VLFYU2+KJG/o84T/nsmbFm/VPNSnr/20aK2L00OpFmUHfXJ0Q4VaqZ/Euf6aXtPKCwF6pINJcMa2vk+nABsDD8L89C33S50ZRdwQvHn1oiJ+X0X3Zi7QNUoQbIfnz3Qza7fwqAE3hzPpKOMnJv0HKsdzLsS3LkrBuo5HZ990QHvy3x99qchxqfu4phPKgmzPDgZBTB2akgXH6bf2tzC5fi78gJzyWa5OT/EkDM6DmXHYGj9sLc4Et203t67XBNYCQG0Uyo+/0IapcEozQcUhf+CjGiGLQDbbho6YBs4j1oRyQNapahdCoUE60zGQ041F+W2qBW9fsnHr8OInRbr6XNkQPnsvBsCEuCb7/B5tuEnHcLQfuJXcuoo7XMl5VfBlpXA5HXSLHkAhOd/I24aR3zuyI9IxetyF/bKLNZy5fGrk9EFmFOTAhhC23tGCeNpB6bwGc"
            }
        ],
        "containerInfo": {
            "contentFiles": ["Tresting.txt"],
            "containerType": "ASiC-E",
            "mimeTypeContent": "application/vnd.etsi.asic-e+zip",
            "mimeTypeFilePresent": true,
            "manifestFiles": [{
                "entries": ["Tresting.txt"],
                "filename": "META-INF/manifest.xml",
                "signatureFilename": "META-INF/signatures0.xml"
            }]
        },
        "trustedLists": [{
            "sequenceNumber": 46,
            "countryCode": "EE",
            "wellSigned": true,
            "lastLoading": 1553160956504,
            "issueDate": 1552266000000,
            "version": 5,
            "url": "https://sr.riik.ee/tsl/estonian-tsl.xml",
            "nextUpdate": 1568160000000
        }],
        "documentName": "ValidLiveSignature.asice",
        "validationDate": 1553161484029,
        "signatures": [{
            "dateTime": 1476178548000,
            "signingCertificate": {
                "attributePresent": true,
                "digestValueMatch": true,
                "digestValuePresent": true,
                "id": "82905213FDD6FC4129F4C8853F45F8FBA8F3C1C558421DBCCF0AEFE85CA5147F",
                "issuerSerialMatch": true
            },
            "signatureFormat": "XAdES-BASELINE-LT",
            "certificateChain": [
                ...
            ],
            "timestamps": [{
                ...
            }],
            "basicSignature": {
                ...
            },
            "signatureScopes": [{
                "scope": "FullSignatureScope",
                "name": "Tresting.txt",
                "value": "Full document"
            }],
            "structuralValidation": {"valid": true},
            "signatureFilename": "META-INF/signatures0.xml",
            "id": "S0",
            "contentType": "text/xml"
        }]
    },
    "validationConclusion": {
        "validationTime": "2019-03-21T09:44:44Z",
        "signaturesCount": 1,
        "validationLevel": "ARCHIVAL_DATA",
        "validatedDocument": {"filename": "ValidLiveSignature.asice"},
        "validSignaturesCount": 1,
        "signatures": [{
            "signatureFormat": "XAdES_BASELINE_LT",
            "signedBy": "NURM,AARE,38211015222",
            "claimedSigningTime": "2016-10-11T09:35:48Z",
            "signatureLevel": "QESIG",
            "signatureScopes": [{
                "scope": "FullSignatureScope",
                "name": "Tresting.txt",
                "content": "Full document"
            }],
            "id": "S0",
            "indication": "TOTAL-PASSED",
            "info": {"bestSignatureTime": "2016-10-11T09:36:10Z"}
        }],
        "policy": {
            "policyDescription": "Policy according most common requirements of Estonian Public Administration, to validate Qualified Electronic Signatures and Electronic Seals with Qualified Certificates (according to Regulation (EU) No 910/2014, aka eIDAS). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive validation result, with exception for seals, where AdES/QC and above will produce positive result. Signatures and Seals which are not compliant with ETSI standards (referred by eIDAS) may produce unknown or invalid validation result. Validation process is based on eIDAS Article 32 and referred ETSI standards.",
            "policyUrl": "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv4",
            "policyName": "POLv4"
        },
        "signatureForm": "ASiC-E"
    }
}}
```

#### Sample Diagnostic Data Report SOAP response (successful scenario)

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
            <ns3:PolicyUrl>http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv4</ns3:PolicyUrl>
          </ns3:Policy>
          <ns3:ValidationTime>2019-03-21T09:44:44Z</ns3:ValidationTime>
          <ns3:ValidatedDocument>
            <ns3:Filename>ValidLiveSignature.asice</ns3:Filename>
          </ns3:ValidatedDocument>
          <ns3:ValidationLevel>ARCHIVAL_DATA</ns3:ValidationLevel>
          <ns3:ValidationWarnings/>
          <ns3:SignatureForm>ASiC-E</ns3:SignatureForm>
          <ns3:Signatures>
            <ns3:Signature>
              <ns3:Id>S0</ns3:Id>
              <ns3:SignatureFormat>XAdES_BASELINE_LT</ns3:SignatureFormat>
              <ns3:SignatureLevel>QESIG</ns3:SignatureLevel>
              <ns3:SignedBy>NURM,AARE,38211015222</ns3:SignedBy>
              <ns3:Indication>TOTAL-PASSED</ns3:Indication>
              <ns3:SubIndication/>
              <ns3:Errors/>
              <ns3:SignatureScopes>
                <ns3:SignatureScope>
                  <ns3:Name>Tresting.txt</ns3:Name>
                  <ns3:Scope>FullSignatureScope</ns3:Scope>
                  <ns3:Content>Full document</ns3:Content>
                </ns3:SignatureScope>
              </ns3:SignatureScopes>
              <ns3:ClaimedSigningTime>2016-10-11T09:35:48Z</ns3:ClaimedSigningTime>
              <ns3:Warnings/>
              <ns3:Info>
                <ns3:BestSignatureTime>2016-10-11T09:36:10Z</ns3:BestSignatureTime>
              </ns3:Info>
            </ns3:Signature>
          </ns3:Signatures>
          <ns3:ValidSignaturesCount>1</ns3:ValidSignaturesCount>
          <ns3:SignaturesCount>1</ns3:SignaturesCount>
        </ns3:ValidationConclusion>
        <ns3:DiagnosticData>
          <ns5:DocumentName>ValidLiveSignature.asice</ns5:DocumentName>
          <ns5:ValidationDate>2019-03-21T09:44:44</ns5:ValidationDate>
          <ns5:ContainerInfo>
            <ns5:ContainerType>ASiC-E</ns5:ContainerType>
            <ns5:MimeTypeFilePresent>true</ns5:MimeTypeFilePresent>
            <ns5:MimeTypeContent>application/vnd.etsi.asic-e+zip</ns5:MimeTypeContent>
            <ns5:ManifestFiles>
              <ns5:ManifestFile>
                <ns5:Filename>META-INF/manifest.xml</ns5:Filename>
                <ns5:SignatureFilename>META-INF/signatures0.xml</ns5:SignatureFilename>
                <ns5:Entries>
                  <ns5:Entry>Tresting.txt</ns5:Entry>
                </ns5:Entries>
              </ns5:ManifestFile>
            </ns5:ManifestFiles>
            <ns5:ContentFiles>
              <ns5:ContentFile>Tresting.txt</ns5:ContentFile>
            </ns5:ContentFiles>
          </ns5:ContainerInfo>
          <ns5:Signatures>
            <ns5:Signature Id="S0">
              <ns5:SignatureFilename>META-INF/signatures0.xml</ns5:SignatureFilename>
              <ns5:DateTime>2016-10-11T09:35:48</ns5:DateTime>
              <ns5:SignatureFormat>XAdES-BASELINE-LT</ns5:SignatureFormat>
              <ns5:StructuralValidation>
                <ns5:Valid>true</ns5:Valid>
              </ns5:StructuralValidation>
              <ns5:BasicSignature>
                ...
              </ns5:BasicSignature>
              <ns5:SigningCertificate Id="82905213FDD6FC4129F4C8853F45F8FBA8F3C1C558421DBCCF0AEFE85CA5147F">
                ...
              </ns5:SigningCertificate>
              <ns5:CertificateChain>
                ...
              </ns5:CertificateChain>
              <ns5:ContentType>text/xml</ns5:ContentType>
              <ns5:CommitmentTypeIndication/>
              <ns5:ClaimedRoles/>
              <ns5:Timestamps>
                ...
              </ns5:Timestamps>
              <ns5:SignatureScopes>
                <ns5:SignatureScope name="Tresting.txt" scope="FullSignatureScope">Full document</ns5:SignatureScope>
              </ns5:SignatureScopes>
            </ns5:Signature>
          </ns5:Signatures>
          <ns5:UsedCertificates>
            ...
            <ns5:Certificate Id="82905213FDD6FC4129F4C8853F45F8FBA8F3C1C558421DBCCF0AEFE85CA5147F">
              ...
              <ns5:SubjectDistinguishedName Format="CANONICAL">2.5.4.5=#130b3338323131303135323232,2.5.4.42=#0c0441415245,2.5.4.4=#0c044e55524d,cn=nurm\,aare\,38211015222,ou=digital signature,o=esteid (mobiil-id),c=ee</ns5:SubjectDistinguishedName>
              <ns5:IssuerDistinguishedName Format="CANONICAL">cn=esteid-sk 2015,2.5.4.97=#0c0e4e545245452d3130373437303133,o=as sertifitseerimiskeskus,c=ee</ns5:IssuerDistinguishedName>
              <ns5:SerialNumber>99414911336318064438106396713165831388</ns5:SerialNumber>
              <ns5:CommonName>NURM,AARE,38211015222</ns5:CommonName>
              <ns5:CountryName>EE</ns5:CountryName>
              <ns5:OrganizationName>ESTEID (MOBIIL-ID)</ns5:OrganizationName>
              <ns5:GivenName>AARE</ns5:GivenName>
              <ns5:OrganizationalUnit>digital signature</ns5:OrganizationalUnit>
              <ns5:Surname>NURM</ns5:Surname>
              <ns5:AuthorityInformationAccessUrls/>
              <ns5:CRLDistributionPoints>
                <ns5:Url>http://www.sk.ee/crls/esteid/esteid2015.crl</ns5:Url>
              </ns5:CRLDistributionPoints>
              <ns5:OCSPAccessUrls/>
              <ns5:DigestAlgoAndValues>
                <ns5:DigestAlgoAndValue>
                  <ns5:DigestMethod>SHA256</ns5:DigestMethod>
                  <ns5:DigestValue>gpBSE/3W/EEp9MiFP0X4+6jzwcVYQh28zwrv6FylFH8=</ns5:DigestValue>
                </ns5:DigestAlgoAndValue>
              </ns5:DigestAlgoAndValues>
              <ns5:NotAfter>2021-10-04T20:59:59</ns5:NotAfter>
              <ns5:NotBefore>2016-10-04T06:16:10</ns5:NotBefore>
              <ns5:PublicKeySize>256</ns5:PublicKeySize>
              <ns5:PublicKeyEncryptionAlgo>ECDSA</ns5:PublicKeyEncryptionAlgo>
              <ns5:KeyUsageBits>
                <ns5:KeyUsage>nonRepudiation</ns5:KeyUsage>
              </ns5:KeyUsageBits>
              <ns5:IdKpOCSPSigning>false</ns5:IdKpOCSPSigning>
              <ns5:IdPkixOcspNoCheck>false</ns5:IdPkixOcspNoCheck>
              <ns5:BasicSignature>
                ...
              </ns5:BasicSignature>
              <ns5:SigningCertificate Id="74D992D3910BCF7E34B8B5CD28F91EAEB4F41F3DA6394D78B8C43672D43F4F0F"/>
              <ns5:CertificateChain>
                <ns5:ChainItem Id="74D992D3910BCF7E34B8B5CD28F91EAEB4F41F3DA6394D78B8C43672D43F4F0F">
                  <ns5:Source>TRUSTED_LIST</ns5:Source>
                </ns5:ChainItem>
              </ns5:CertificateChain>
              <ns5:Trusted>false</ns5:Trusted>
              <ns5:SelfSigned>false</ns5:SelfSigned>
              <ns5:CertificatePolicyIds>
                <ns5:oid>1.3.6.1.4.1.10015.1.3</ns5:oid>
              </ns5:CertificatePolicyIds>
              <ns5:QCStatementIds>
                ...
              </ns5:QCStatementIds>
              <ns5:QCTypes/>
              <ns5:TrustedServiceProviders>
                ...
              </ns5:TrustedServiceProviders>
              <ns5:Revocations>
                <ns5:Revocation Id="82905213fdd6fc4129f4c8853f45f8fba8f3c1c558421dbccf0aefe85ca5147fd814378123b296c63f15cd6face30c4e0f012a3de5a35a4df16b4cc0ed082753">
                  <ns5:Origin>SIGNATURE</ns5:Origin>
                  <ns5:Source>OCSPToken</ns5:Source>
                  <ns5:Status>true</ns5:Status>
                  <ns5:ProductionDate>2016-10-11T09:36:10</ns5:ProductionDate>
                  <ns5:ThisUpdate>2016-10-11T09:36:10</ns5:ThisUpdate>
                  <ns5:DigestAlgoAndValues>
                    <ns5:DigestAlgoAndValue>
                      <ns5:DigestMethod>SHA256</ns5:DigestMethod>
                      <ns5:DigestValue>2BQ3gSOylsY/Fc1vrOMMTg8BKj3lo1pN8WtMwO0IJ1M=</ns5:DigestValue>
                    </ns5:DigestAlgoAndValue>
                  </ns5:DigestAlgoAndValues>
                  <ns5:BasicSignature>
                    ...
                  </ns5:BasicSignature>
                  <ns5:SigningCertificate Id="50D62F373742287B5F18319E513C7BCA89BD78788B23DA356124CB90F9A636AF"/>
                  <ns5:CertificateChain>
                    <ns5:ChainItem Id="50D62F373742287B5F18319E513C7BCA89BD78788B23DA356124CB90F9A636AF">
                      <ns5:Source>TRUSTED_LIST</ns5:Source>
                    </ns5:ChainItem>
                  </ns5:CertificateChain>
                  <ns5:Info/>
                </ns5:Revocation>
              </ns5:Revocations>
              <ns5:Info>
                <ns5:Message Id="0">No CRL info found !</ns5:Message>
              </ns5:Info>
              <ns5:Base64Encoded>MIIE3DCCAsSgAwIBAgIQSsqdjzAQgvpX80krgJy83DANBgkqhkiG9w0BAQsFADBjMQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEXMBUGA1UEYQwOTlRSRUUtMTA3NDcwMTMxFzAVBgNVBAMMDkVTVEVJRC1TSyAyMDE1MB4XDTE2MTAwNDA2MTYxMFoXDTIxMTAwNDIwNTk1OVowgZoxCzAJBgNVBAYTAkVFMRswGQYDVQQKDBJFU1RFSUQgKE1PQklJTC1JRCkxGjAYBgNVBAsMEWRpZ2l0YWwgc2lnbmF0dXJlMR4wHAYDVQQDDBVOVVJNLEFBUkUsMzgyMTEwMTUyMjIxDTALBgNVBAQMBE5VUk0xDTALBgNVBCoMBEFBUkUxFDASBgNVBAUTCzM4MjExMDE1MjIyMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEEmQMc8aNpHPBQCV3J2I/VZ1AAIQBilg/wxfNwR6jQNJSua5sYQiM8mZj7thf7HwYrDRrrdw/XbMmaWtkj4ZQJaOCAR0wggEZMAkGA1UdEwQCMAAwDgYDVR0PAQH/BAQDAgZAMFoGA1UdIARTMFEwTwYJKwYBBAHOHwEDMEIwHQYIKwYBBQUHAgIwEQwPQ29udHJhY3QgMS4xMS05MCEGCCsGAQUFBwIBFhVodHRwczovL3d3dy5zay5lZS9jcHMwHQYDVR0OBBYEFBO7ncBMwgpRL5JLrVZ+/6zzcpmEMCIGCCsGAQUFBwEDBBYwFDAIBgYEAI5GAQEwCAYGBACORgEEMB8GA1UdIwQYMBaAFLOriLyZ1WKkhSoIzbQdcjuDckdRMDwGA1UdHwQ1MDMwMaAvoC2GK2h0dHA6Ly93d3cuc2suZWUvY3Jscy9lc3RlaWQvZXN0ZWlkMjAxNS5jcmwwDQYJKoZIhvcNAQELBQADggIBALITavvjDaKHB2tl2AYJmJDvlOCcNpfDiVxbEYkGnWddk7dK2R7Dy25xh2GXeLB6kkDFixgyGRYbn1BUSviFn8hgLp9x10VaL1xP+ziZ0fjZFw0z4jpZk91zqJOKfkliVgbOH/9PoSlB1lFSeijl2jRKCehlYVBJqF9hj7wOKbr8zw+b+o41IiZ+TKJTpO9Il4sKI0l/xCD1lSPMwxnhRZxAOb9gXeTYG8Opdh4VLFYU2+KJG/o84T/nsmbFm/VPNSnr/20aK2L00OpFmUHfXJ0Q4VaqZ/Euf6aXtPKCwF6pINJcMa2vk+nABsDD8L89C33S50ZRdwQvHn1oiJ+X0X3Zi7QNUoQbIfnz3Qza7fwqAE3hzPpKOMnJv0HKsdzLsS3LkrBuo5HZ990QHvy3x99qchxqfu4phPKgmzPDgZBTB2akgXH6bf2tzC5fi78gJzyWa5OT/EkDM6DmXHYGj9sLc4Et203t67XBNYCQG0Uyo+/0IapcEozQcUhf+CjGiGLQDbbho6YBs4j1oRyQNapahdCoUE60zGQ041F+W2qBW9fsnHr8OInRbr6XNkQPnsvBsCEuCb7/B5tuEnHcLQfuJXcuoo7XMl5VfBlpXA5HXSLHkAhOd/I24aR3zuyI9IxetyF/bKLNZy5fGrk9EFmFOTAhhC23tGCeNpB6bwGc</ns5:Base64Encoded>
            </ns5:Certificate>
          </ns5:UsedCertificates>
          <ns5:TrustedLists>
            <ns5:TrustedList>
              <ns5:CountryCode>EE</ns5:CountryCode>
              <ns5:Url>https://sr.riik.ee/tsl/estonian-tsl.xml</ns5:Url>
              <ns5:SequenceNumber>46</ns5:SequenceNumber>
              <ns5:Version>5</ns5:Version>
              <ns5:LastLoading>2019-03-21T09:35:56</ns5:LastLoading>
              <ns5:IssueDate>2019-03-11T01:00:00</ns5:IssueDate>
              <ns5:NextUpdate>2019-09-11T00:00:00</ns5:NextUpdate>
              <ns5:WellSigned>true</ns5:WellSigned>
            </ns5:TrustedList>
          </ns5:TrustedLists>
          <ns5:ListOfTrustedLists>
            <ns5:CountryCode>EU</ns5:CountryCode>
            <ns5:Url>https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl-mp.xml</ns5:Url>
            <ns5:SequenceNumber>237</ns5:SequenceNumber>
            <ns5:Version>5</ns5:Version>
            <ns5:LastLoading>2019-03-21T09:35:47</ns5:LastLoading>
            <ns5:IssueDate>2019-03-21T07:00:00</ns5:IssueDate>
            <ns5:NextUpdate>2019-09-21T00:00:00</ns5:NextUpdate>
            <ns5:WellSigned>true</ns5:WellSigned>
          </ns5:ListOfTrustedLists>
        </ns3:DiagnosticData>
      </ns3:ValidationReport>
    </ns2:ValidateDocumentResponse>
  </soap:Body>
</soap:Envelope>
```

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
| health.status | Status of current webapp - constant value **UP** |
| health.webappName | The artifact name of the webapp. Taken from the MANIFEST.MF file (inside the jar/war file). |
| health.version | The release version fo the webapp. Taken from the MANIFEST.MF (inside the jar/war file).  |
| health.buildTime | Build date and time (format yyyy-MM-dd'T'HH:mm:ss'Z') of the webapp. Taken from the MANIFEST.MF (inside the jar/war file).  |
| health.startTime | Webapp startup date and time (format yyyy-MM-dd'T'HH:mm:ss'Z')|
| health.currentTime | Current server date and time (format yyyy-MM-dd'T'HH:mm:ss'Z') |
| link{number}.status | (OPTIONAL) Represents the status of a link to the external system that the webapp depends on. <ul><li>**DOWN** when the webapp does not respond (within a specified timeout limit - default 10 seconds) or the response is in invalid format (default Spring boot actuator /health endpoint format is expected).</li><li>**UP** if the service responds with HTTP status code 200 and returns a valid JSON object with status "UP"</li></ul> |) |
| link{number}.name | (OPTIONAL) Descriptive name for the link to the external system |

Sample response:

```json
{
  "status":"UP",
    "health":{
      "status":"UP",
      "webappName":"siva-sample-application",
      "version":"3.1.0",
      "buildTime":"2016-10-21T15:56:21Z",
      "startTime":"2016-10-21T15:57:48Z",
      "currentTime":"2016-10-21T15:58:39Z"
    },
    "link1":{
      "status":"UP",
      "name":"sivaService"
    }
}
```
