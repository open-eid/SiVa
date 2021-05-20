<!--# Interface description-->

In this section the SiVa external interfaces are described. For information of internal components and their interfaces, please refer to [**Structure and activities**](structure_and_activities).

SiVa service provides **REST JSON** and **SOAP** interfaces that enable the service users to:

* Request validation of signatures in a digitally signed document (i.e. signature container like BDOC,ASiC-E/PDF/...);
* Request validation of signature with providing data file hashes.
* Receive a response with the validation result of all the signatures in the document.
* Request datafiles inside of DDOC container
* Receive datafiles from DDOC container

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
| documentType | DocumentType | - |  String | If not present document type is determined automatically based on the file extension used in the filename. This parameter is necessary to differentiate XROAD ASIC-E containers from standard ASIC-E containers. <br> **Possible values:** <br> XROAD - for documents created in the [X-Road](https://www.ria.ee/en/x-road.html) information system, see also [specification document](https://cyber.ee/research/reports/T-4-23-Profile-for-High-Performance-Digital-Signatures.pdf) of the signature format. |
| signaturePolicy | SignaturePolicy | - |  String | Can be used to change the default signature validation policy that is used by the service. <br> See also [SiVa Validation Policy](/siva2/appendix/validation_policy) for more detailed information on given policy constraints.<br>**Possible values:** <br> POLv3 - signatures with all legal levels are accepted (i.e. QES, AdESqc and AdES, according to Regulation (EU) No 910/2014.) <br> POLv4 - the default policy. Accepted signatures depend on their type (i.e. signature, seal or unknown) and legal level (i.e. QES, AdESqc and Ades) |
| reportType | ReportType | - | String | Can be used to change the default returned report type. <br>**Possible values:** <br> Simple - default report type. Returns overall validation result (validationConclusion block)<br> Detailed -  returns detailed information about the signatures and their validation results (validationConclusion, validationProcess and validationReportSignature. Two later ones are optionally present). |

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

Validation request parameters for JSON interface are described in the table below.

| JSON parameter | SOAP parameter | Mandatory | JSON data type | Description |
|----------------|----------------|-----------|----------------|-------------|
| signatureFile | SignatureFile | + |  String | Base64 encoded string of XAdES document to be validated |
| filename | Filename | + |  String | File name of the XAdES document (i.e. signature0.xml). Only XML files supported. |
| signaturePolicy | SignaturePolicy | - |  String | Can be used to change the default signature validation policy that is used by the service. <br> See also [SiVa Validation Policy](/siva2/appendix/validation_policy) for more detailed information on given policy constraints.<br>**Possible values:** <br> POLv3 - signatures with all legal levels are accepted (i.e. QES, AdESqc and AdES, according to Regulation (EU) No 910/2014.) <br> POLv4 - the default policy. Accepted signatures depend on their type (i.e. signature, seal or unknown) and legal level (i.e. QES, AdESqc and Ades) |
| reportType | ReportType | - | String | Can be used to change the default returned report type. <br>**Possible values:** <br> Simple - default report type. Returns overall validation result (validationConclusion block)<br> Detailed -  returns detailed information about the signatures and their validation results (validationConclusion, validationProcess and validationReportSignature. Two later ones are optionally present). |
| datafiles | DataFiles | + |  Array | Array containing the information for datafiles that signature is covering |
| datafiles[0] | DataFile | + | Object | Object containing data file information |
| datafiles.filename | Filename | + |  String | File name of the hashed data file, max length 255 characters. |
| datafiles.hashAlgo | HashAlgo | + |  String | Hash algorithm used for hashing the data file (must match with algorithm in signature file). Accepted values are dependant of validation policy |
| datafiles.hash | Hash | + |  String | Data file hash in Base64 encoded format. |

### Sample JSON request with mandatory parameters

```json
{
  "signatureFile": "PD94bWwgdmVyc2lvbj0iMS4...."
  "filename": "signature0.xml",
  "datafiles": [{
    "filename": "test.pdf",
    "hashAlgo": "SHA256",
    "hash": "IucjUcbRo9Rke0bZLiHcwiIiplP9pSrSPr7LKln1EiI="
  }]
}
```
### Sample JSON request with all parameters and multiple datafiles

```json
{
  "signatureFile":"sample.asice",
  "filename":"PD94bWwgdmVyc2lvbj0iMS4....",
  "signaturePolicy":"POLv3",
  "reportType":"Detailed",
  "datafiles": [{
      "filename": "test.pdf",
      "hashAlgo": "SHA256",
      "hash": "IucjUcbRo9Rke0bZLiHcwiIiplP9pSrSPr7LKln1EiI="
      },
      {
      "filename": "test2.pdf",
      "hashAlgo": "SHA256",
      "hash": "IucjUcbRo9Rke0bZLiHc23SSasw9pSrSPr7LKln1EiI="
  }]
}
```

### Sample SOAP request with mandatory parameters

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://soap.webapp.siva.openeid.ee/">
   <soapenv:Body>
      <soap:HashcodeValidationDocument>
         <soap:HashcodeValidationRequest>
            <SignatureFile>PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGlu...</SignatureFile>
            <Filename>signature.xml</Filename>
            <DataFiles>
               <DataFile>
                  <Filename>test.pdf</Filename>
                  <HashAlgo>SHA256</HashAlgo>
                  <Hash>IucjUcbRo9Rke0bZLiHcwiIiplP9pSrSPr7LKln1EiI=</Hash>
               </DataFile>
            </DataFiles>
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
            <SignatureFile>PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGlu...</SignatureFile>
            <Filename>signature.xml</Filename>
            <ReportType>Simple</ReportType>
            <SignaturePolicy>POLv4</SignaturePolicy>
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
               <DataFile>
                  <Filename>test3.pdf</Filename>
                  <HashAlgo>SHA256</HashAlgo>
                  <Hash>IucjUcbRo9Rke0bZLiHcwiIiplP9pSrSPr7LKln1EiI=</Hash>
               </DataFile>
            </DataFiles>
         </soap:HashcodeValidationRequest>
      </soap:HashcodeValidationDocument>
   </soapenv:Body>
</soapenv:Envelope>
```

## Validation response interface
The signature validation report (i.e. the validation response) for JSON and SOAP interfaces depends on what type of validation report was requested.  Data types of SOAP parameters are defined in the [SiVa WSDL document](/siva2/appendix/wsdl).

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
| validatedDocument | ValidatedDocument | + | Object | Object containing information about validated document. |
| validatedDocument. filename | ValidatedDocument. Filename | + | String | Digitally signed document's file name. |
| validatedDocument. fileHashInHex | ValidatedDocument. FileHashInHex | - | String | Calculated hash in hex of validated document. |
| validatedDocument. hashAlgo | ValidatedDocument. HashAlgo | - | String | Hash algorithm used. |
| signatureForm | SignatureForm | - | String | Format (and optionally version) of the digitally signed document container. <br> In case of documents in [DIGIDOC-XML](https://www.id.ee/wp-content/uploads/2020/08/digidoc_format_1.3.pdf) (DDOC) format, the "hashcode" suffix is used to denote that the container was validated in [hashcode mode](http://sertkeskus.github.io/dds-documentation/api/api_docs/#ddoc-format-and-hashcode), i.e. without original data files. <br> **Possible values:**  <br> DIGIDOC_XML_1.0 <br> DIGIDOC_XML_1.0_hashcode <br> DIGIDOC_XML_1.1 <br> DIGIDOC_XML_1.1_hashcode <br> DIGIDOC_XML_1.2 <br> DIGIDOC_XML_1.2_hashcode <br> DIGIDOC_XML_1.3 <br> DIGIDOC_XML_1.3_hashcode <br> ASiC_E - used in case of all ASIC-E ([BDOC](http://id.ee/wp-content/uploads/2020/06/bdoc-spec212-eng.pdf)) documents and X-Road simple containers that don't use batch time-stamping (see [specification document](https://cyber.ee/research/reports/T-4-23-Profile-for-High-Performance-Digital-Signatures.pdf))<br> ASiC_E_batchsignature - used in case of X-Road containers with batch signature (see [specification document](https://cyber.ee/research/reports/T-4-23-Profile-for-High-Performance-Digital-Signatures.pdf)) <br> ASiC_S - used in case of all ASIC-S documents |
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
| signatures[0]. signatureFormat | Signature. SignatureFormat | + | String | Format and profile (according to Baseline Profile) of the signature. See [XAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103171/02.01.01_60/ts_103171v020101p.pdf), [CAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103173/02.02.01_60/ts_103173v020201p.pdf) and [PAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf) for detailed description of the Baseline Profile levels. Levels that are accepted in SiVa validation policy are described in [SiVa signature validation policy](/siva/appendix/validation_policy) <br>**Possible values:**  <br> XAdES_BASELINE_B <br> XAdES_BASELINE_B_BES <br> XAdES_BASELINE_B_EPES <br> XAdES_BASELINE_T <br> XAdES_BASELINE_LT - long-term level XAdES signature where time-stamp is used as a assertion of trusted signing time<br> XAdES_BASELINE_LT_TM - long-term level XAdES signature where time-mark is used as a assertion of trusted signing time. Used in case of [BDOC](http://id.ee/wp-content/uploads/2020/06/bdoc-spec212-eng.pdf) signatures with time-mark profile and [DIGIDOC-XML](https://www.id.ee/wp-content/uploads/2020/08/digidoc_format_1.3.pdf) (DDOC) signatures.<br>  XAdES_BASELINE_LTA <br> CAdES_BASELINE_B <br> CAdES_BASELINE_T <br> CAdES_BASELINE_LT <br> CAdES_BASELINE_LTA<br> PAdES_BASELINE_B <br> PAdES_BASELINE_T <br> PAdES_BASELINE_LT <br> PAdES_BASELINE_LTA |
| signatures[0]. signatureLevel | Signature. SignatureLevel | - |String | Legal level of the signature, according to Regulation (EU) No 910/2014. <br> - **Possible values on positive validation result:**<br> QESIG <br> QESEAL <br> QES <br> ADESIG_QC <br> ADESEAL_QC <br> ADES_QC <br> ADESIG <br> ADESEAL <br> ADES <br> - **Possible values on indeterminate validation result:**<br> prefix INDETERMINATE is added to the level described in positive result. For example  INDETERMINATE_QESIG <br> - **Possible values on negative validation result:**<br>In addition to abovementioned<br> NOT_ADES_QC_QSCD <br> NOT_ADES_QC <br> NOT_ADES <br> NA <br> - In case of DIGIDOC-XML 1.0..1.3 formats, value is missing as the signature level is not checked by the JDigiDoc base library that is used for validation. However, the signatures can be indirectly regarded as QES level signatures, see also [SiVa Validation Policy](/siva2/appendix/validation_policy)<br> - In case of XROAD ASICE containers the value is missing as the asicverifier base library do not check the signature level.|
| signatures[0].signedBy | Signature.SignedBy | + | String | Signers name and identification number, i.e. value of the CN field of the signer's certificate |
| signatures[0]. signatureScopes | Signature. SignatureScopes | - | Array | Contains information of the original data that is covered by the signature. |
| signatures[0]. signatureScopes[0]. name | Signature. SignatureScopes.  SignatureScope.Name | + | String | Name of the signature scope. |
| signatures[0]. signatureScopes[0]. scope | Signature. SignatureScopes.  SignatureScope. Scope | + | String | Type of the signature scope. |
| signatures[0]. signatureScopes[0]. content | Signature. SignatureScopes.  SignatureScope. Content | + | String | Description of the scope. |
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
        "policyUrl": "http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#POLv4",
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
