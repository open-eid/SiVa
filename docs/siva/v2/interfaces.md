<!--# Interface description-->

In this section the SiVa signature validation service's external interfaces that are provided for the service's clients are described. For information of internal components and their interfaces, please refer to [**Structure and activities**](structure_and_activities).

SiVa service provides **REST JSON** and **SOAP** interfaces that enable the service users to:

* request validation of signatures in a digitally signed document (i.e. signature container like BDOC/ASiC-E or PDF); 
* receive a response with the validation result of all the signatures in the document.

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
| filename | Filename | + |  String |File name of the digitally signed document (i.e. sample.bdoc) |
| documentType | DocumentType | + |  String | Format of the digitally signed document. This value determines the validation service type that will be used for validation. <br> **Possible values:** <br> * BDOC - for documents in [BDOC](http://id.ee/wp-content/uploads/2020/06/bdoc-spec212-eng.pdf) (ASiC-E/XAdES) format. Both time-stamp (TS) and time-mark (TM) profiles are supported here. <br> * DDOC - for documents in [DIGIDOC-XML](https://id.ee/wp-content/uploads/2020/08/digidoc_format_1.3.pdf) format, supported versions are DIGIDOC-XML 1.0 (also known as SK-XML 1.0) to DIGIDOC-XML 1.3. In case of DIGIDOC_XML documents, also the hashcode format is supported (see [DigiDocService Specification](http://sertkeskus.github.io/dds-documentation/api/api_docs/#ddoc-format-and-hashcode) for more information). <br> * PDF - for signed PDF document in [PAdES](http://www.etsi.org/deliver/etsi_en/319100_319199/31914201/01.01.01_60/en_31914201v010101p.pdf) format. <br>* XROAD - for documents created in the [X-Road](https://www.ria.ee/en/x-road.html) information system, see also [specification document](https://cyber.ee/uploads/2013/05/T-4-23-Profile-for-High-Performance-Digital-Signatures1.pdf) of the signature format. |
| signaturePolicy | SignaturePolicy | - |  String | Can be used to change the default signature validation policy that is used by the service. <br> See also [SiVa Validation Policy](/siva/appendix/validation_policy) for more information. <br> **Possible values:** <br> * POLv1 - the default policy. Signatures with all legal levels are accepted (i.e. QES, AdES and AdESqc, according to Regulation (EU) No 910/2014.) <br> * POLv2 - only signatures with QES legal level (according to Regulation (EU) No 910/2014) are accepted. |

### Sample JSON request

```json
{
  "filename":"sample.ddoc",
  "documentType":"DDOC",
  "document":"PD94bWwgdmVyc2lvbj0iMS4....",
  "signaturePolicy": "POLv1"
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
        <Filename>sample.ddoc</Filename>
        <DocumentType>DDOC</DocumentType>
        <SignaturePolicy>POLv1</SignaturePolicy>
      </ns2:ValidationRequest>
    </ns2:ValidateDocument>
  </soap:Body>
</soap:Envelope>
```


## Validation response interface

### Validation response parameters (successful scenario)

The signature validation report (i.e. the validation response) for JSON and SOAP interfaces is described in the table below. Data types of SOAP parameters are defined in the [SiVa WSDL document](/siva/appendix/wsdl).

| JSON parameter | SOAP parameter |  JSON data type | Description |
|----------------|----------------|-----------------|-------------|
| policy | Policy |  Object | Object containing information of the SiVa signature validation policy that was used for validation |
| policy.policyName | Policy.PolicyName |  String | Name of the validation policy |
| policy. policyDescription | Policy. PolicyDescription |  String | Short description of the validation policy |
| policy.policyUrl | Policy.PolicyUrl |  String | URL where the signature validation policy document can be downloaded. The validation policy document shall include information about validation of all the document formats, including the different validation policies that are used in case of different file formats and base libraries. |
| signaturesCount | SignaturesCount |  Number | Number of signatures found inside digitally signed file |
| validSignaturesCount | ValidSignaturesCount |  Number | Signatures count that have validated to `TOTAL-PASSED`. See also `Signature.Indication` field. |
| validationTime | ValidationTime |  Date | Time of validating the signature by the service |
| validationWarnings | ValidationWarnings |  Array | Block of SiVa validation warnings that do not affect the overall validation result and applicable only to BDOC/ASiC-E type containers. Known warning situations: container includes unsigned files, manifest validation warnings. |
| filename | DocumentName |  String | Digitally signed document's file name |
| signatureForm | SignatureForm |  String | Format (and optionally version) of the digitally signed document container. <br> In case of documents in [DIGIDOC-XML](https://www.id.ee/wp-content/uploads/2020/08/digidoc_format_1.3.pdf) (DDOC) format, the "hashcode" suffix is used to denote that the container was validated in [hashcode mode](http://sertkeskus.github.io/dds-documentation/api/api_docs/#ddoc-format-and-hashcode), i.e. without original data files. <br> **Possible values:**  <br> * DIGIDOC_XML_1.0 <br> * DIGIDOC_XML_1.0_hashcode <br> * DIGIDOC_XML_1.1 <br> * DIGIDOC_XML_1.1_hashcode <br> * DIGIDOC_XML_1.2 <br> * DIGIDOC_XML_1.2_hashcode <br> * DIGIDOC_XML_1.3 <br> * DIGIDOC_XML_1.3_hashcode <br> * PAdES - used in case of signed PDF document <br> * ASiC_E - used in case of all [BDOC](http://id.ee/wp-content/uploads/2020/06/bdoc-spec212-eng.pdf) documents and X-Road simple containers that don't use batch time-stamping (see [specification document](https://cyber.ee/uploads/2013/05/T-4-23-Profile-for-High-Performance-Digital-Signatures1.pdf))<br> * ASiC_E_batchsignature - used in case of X-Road containers with batch signature (see [specification document](https://cyber.ee/uploads/2013/05/T-4-23-Profile-for-High-Performance-Digital-Signatures1.pdf))|
| signatures | Signatures |  Array | Collection of signatures found in digitally signed document |
| signatures[0] | Signature |  Object | Signature information object |
| signatures[0]. claimedSigningTime | Signature. ClaimedSigningTime |  Date | Claimed signing time, i.e. signer's computer time during signature creation |
| signatures[0].id | Signature.Id | String | Signature ID attribute  |
| signatures[0].indication | Signature.Indication |  String | Overall result of the signature's validation process, according to [ETSI EN 319 102-1](http://www.etsi.org/deliver/etsi_en/319100_319199/31910201/01.01.01_60/en_31910201v010101p.pdf) "Table 5: Status indications of the signature validation process". <br> Note that the validation results of different signatures in one signed document (signature container) may vary. <br> See also `validSignaturesCount` and `SignaturesCount` fields. <br>**Possible values:** <br> * TOTAL-PASSED <br> * TOTAL-FAILED <br> * INDETERMINATE |
| signatures[0]. subIndication | Signature. SubIndication |  String | Additional subindication in case of failed or indeterminate validation result, according to [ETSI EN 319 102-1](http://www.etsi.org/deliver/etsi_en/319100_319199/31910201/01.01.01_60/en_31910201v010101p.pdf) "Table 6: Validation Report Structure and Semantics" |
| signatures[0].errors | Signature.Errors |  Array | Information about validation error(s), array of error messages.  |
| signatures[0].errors[0].  content | Signature.Errors. Error.Content |  String | Error message, as returned by the base library that was used for signature validation. |
| signatures[0].info | Signature.Info |  Object | Object containing trusted signing time information.  |
| signatures[0].info. bestSignatureTime | Signature.Info. BestSignatureTime |  Date | Time value that is regarded as trusted signing time, denoting the earliest time when it can be trusted by the validation application (because proven by some Proof-of-Existence present in the signature) that a signature has existed.<br>The source of the value depends on the signature profile (see also `SignatureFormat` parameter):<br>- Signature with time-mark (LT_TM level) - the producedAt value of the earliest valid time-mark (OCSP confirmation of the signer's certificate) in the signature.<br>- Signature with time-stamp (LT or LTA level) - the genTime value of the earliest valid signature time-stamp token in the signature. <br> - Signature with BES or EPES level - the value is empty, i.e. there is no trusted signing time value available. |
| signatures[0]. signatureFormat | Signature. SignatureFormat |  String | Format and profile (according to Baseline Profile) of the signature. See [XAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103171/02.01.01_60/ts_103171v020101p.pdf) and [PAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf) for detailed description of the Baseline Profile levels. Levels that are accepted in SiVa validation policy are described in [SiVa signature validation policy](/siva/appendix/validation_policy) <br>**Possible values:**  <br> * XAdES_BASELINE_B_BES <br> * XAdES_BASELINE_B_EPES <br> * XAdES_BASELINE_T <br> * XAdES_BASELINE_LT - long-term level XAdES signature where time-stamp is used as a assertion of trusted signing time<br> * XAdES_BASELINE_LT_TM - long-term level XAdES signature where time-mark is used as a assertion of trusted signing time. Used in case of [BDOC](http://id.ee/wp-content/uploads/2020/06/bdoc-spec212-eng.pdf) signatures with time-mark profile and [DIGIDOC-XML](https://www.id.ee/wp-content/uploads/2020/08/digidoc_format_1.3.pdf) (DDOC) signatures.<br> * XAdES_BASELINE_LTA <br> * PAdES_BASELINE_B_BES <br> * PAdES_BASELINE_B_EPES <br> * PAdES_BASELINE_T <br> * PAdES_BASELINE_LT <br> * PAdES_BASELINE_LTA |
| signatures[0]. signatureLevel | Signature. SignatureLevel |  String | Legal level of the signature, according to Regulation (EU) No 910/2014. <br> - In case of BDOC and PAdES formats: indication whether the signature is Advanced electronic Signature (AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES). <br> - In case of DIGIDOC-XML 1.0..1.3 formats, empty value is used as the signature level is not checked by the JDigiDoc base library that is used for validation. However, the signatures can be indirectly regarded as QES level signatures, see also [SiVa Validation Policy](/siva/appendix/validation_policy)|
| signatures[0].signedBy | Signature.SignedBy |  String | Signers name and identification number, i.e. value of the CN field of the signer's certificate |
| signatures[0]. signatureScopes | Signature. SignatureScopes |  Array | Contains information of the original data that is covered by the signature. |
| signatures[0]. signatureScopes[0]. name | Signature. SignatureScopes.  SignatureScope.Name |  String | Name of the signature scope.  <br>- In case of XAdES signature: name of the data file that is signed. <br>- In case of PAdES signature: PDF version that is covered by the signature, e.g. 'PDF previous version #1' |
| signatures[0]. signatureScopes[0]. scope | Signature. SignatureScopes.  SignatureScope. Scope |  String | Type of the signature scope.<br>- In case of XAdES signature: 'FullSignatureScope'<br>- In case of PAdES signature: 'PdfByteRangeSignatureScope' |
| signatures[0]. signatureScopes[0]. content | Signature. SignatureScopes.  SignatureScope. Content |  String | - In case of XAdES signatures: 'Full document', indicating that the whole document is covered by the signature. <br>- In case of PAdES signatures: the byte range that is covered by the signature. |
| signatures[0].warnings | Signature.Warnings |  Array | Block of validation warnings that do not affect the overall validation result. Known warning situations (according to https://id.ee/wp-content/uploads/2021/04/sk-jdd-prg-guide.pdf, chap. 5.2.4.1): <br>- BDOC (ASiC-E) and PAdES: weaker digest method (SHA-1) has been used than recommended when calculating either Reference or Signature element’s digest value; <br> - DIGIDOC-XML 1.0-1.3: DataFile element’s xmlns attribute is missing;<br> - DIGIDOC-XML 1.0-1.3: IssuerSerial/X509IssuerName and/or IssuerSerial/X509IssuerSerial element’s xmlns attribute is missing. |
| signatures[0].warnings[0]. description | Signature.Warnings. Warning.Description |  String | Warning description, as retuned by the base library that was used for validation. |

### Sample JSON response (successful scenario)

```json
{
    "policy": {
        "policyName": "POLv2",
        "policyDescription": "Policy for validating Qualified Electronic Signatures and Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive validation result.",
        "policyUrl": "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv2"
    },
    "signaturesCount": 1,
    "validSignaturesCount": 1,
    "validationTime": "2016-07-28T14:41:45Z",
    "documentName": "sample.bdoc",
    "validationWarnings": [
        {"content": "Manifest file has an entry for file document_3.xml with mimetype application/octet-stream but the signature file for signature S0 does not have an entry for this file"},
        {"content": "Signature SOLOVEI,JULIA,47711040261 has unsigned files: document_3.xml"}
    ],
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

### Sample SOAP response (successful scenario)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:ValidateDocumentResponse xmlns:ns2="http://soap.webapp.siva.openeid.ee/" xmlns:ns3="http://x-road.eu/xsd/identifiers" xmlns:ns4="http://x-road.eu/xsd/xroad.xsd">
      <ns2:ValidationReport>
        <Policy>
          <PolicyDescription>Policy for validating Electronic Signatures and Electronic Seals regardless of the legal type of the signature or seal (according to Regulation (EU) No 910/2014), i.e. the fact that the electronic signature or electronic seal is either Advanced electronic Signature (AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES) does not change the total validation result of the signature.</PolicyDescription>
          <PolicyName>POLv1</PolicyName>
          <PolicyUrl>http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv1</PolicyUrl>
        </Policy>
        <ValidationTime>2016-09-27T06:42:33Z</ValidationTime>
        <ValidationWarnings>
          <ValidationWarning>
            <Content>Manifest file has an entry for file document_3.xml with mimetype application/octet-stream but the signature file for signature S0 does not have an entry for this file</Content>
          </ValidationWarning>
          <ValidationWarning>
            <Content>Signature SOLOVEI,JULIA,47711040261 has unsigned files: document_3.xml</Content>
          </ValidationWarning>
        </ValidationWarnings>
        <DocumentName>Valid_ID_sig.bdoc</DocumentName>
        <SignatureForm>ASiC_E</SignatureForm>
        <Signatures>
          <Signature>
            <Id>S0</Id>
            <SignatureFormat>XAdES_BASELINE_LT_TM</SignatureFormat>
            <SignatureLevel>QES</SignatureLevel>
            <SignedBy>NURM,AARE,38211015222</SignedBy>
            <Indication>TOTAL-PASSED</Indication>
            <SubIndication/>
            <Errors/>
            <SignatureScopes>
              <SignatureScope>
                <Name>Proov.txt</Name>
                <Scope>FullSignatureScope</Scope>
                <Content>Full document</Content>
              </SignatureScope>
            </SignatureScopes>
            <ClaimedSigningTime>2016-05-12T10:09:09Z</ClaimedSigningTime>
            <Warnings/>
            <Info>
              <bestSignatureTime>2016-05-12T10:09:20Z</bestSignatureTime>
            </Info>
          </Signature>
        </Signatures>
        <ValidSignaturesCount>1</ValidSignaturesCount>
        <SignaturesCount>1</SignaturesCount>
      </ns2:ValidationReport>
    </ns2:ValidateDocumentResponse>
  </soap:Body>
</soap:Envelope>
```

### Sample JSON response (error situation)

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
| documentType | DocumentType | + |  String | Format of the digitally signed document. <br> **Possible values:** <br> * DDOC - for documents in [DIGIDOC-XML](https://www.id.ee/wp-content/uploads/2020/08/digidoc_format_1.3.pdf) format, supported versions are DIGIDOC-XML 1.0 (also known as SK-XML 1.0) to DIGIDOC-XML 1.3. Currently only DDOC file format is supported for this operation|

### Sample JSON request

```json
{
  "documentType":"DDOC",
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
        <DocumentType>DDOC</DocumentType>
      </soap:DataFilesRequest>
    </soap:GetDocumentDataFiles>
  </soapenv:Body>
</soapenv:Envelope>
```


## Data files response interface

### Data files response parameters (successful scenario)

The data file extraction report (i.e. the data files response) for JSON and SOAP interfaces is described in the table below. Data types of SOAP parameters are defined in the [SiVa WSDL document](/siva/appendix/wsdl).
SiVa returns all data files as they are extracted by JDigiDoc library in an as is form. No extra operations or validations are done.

| JSON parameter | SOAP parameter | JSON data type | Description |
|----------------|----------------|----------------|-------------|
| dataFiles | DataFiles |  Array | Collection of data files found in digitally signed document |
| dataFiles[0] | DataFile |  Object | Extracted data file object |
| dataFiles[0].fileName | DataFile.FileName |  String | File name of the extracted data file |
| dataFiles[0].size | DataFile.Size |  Long | Extracted data file size in bytes |
| dataFiles[0].base64 | DataFile.Base64 | String | Base64 encoded string of extracted data file |
| dataFiles[0].mimeType | DataFile.MimeType |  String | MIME type of the extracted data file  |

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
