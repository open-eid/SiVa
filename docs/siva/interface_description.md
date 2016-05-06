# Interface description

!!! warning
    The REST and SOAP API will be revised when final report is has been agreed upon

Currently the Service provides only one service – PDF validation.
WSDL – see „Appendix 6 - ValidationService WSDL“ for details.
Target Namespace - http://dss.esig.europa.eu/validation/diagnostic.
Protocol – SOAP over HTTP/HTTPS, style – document, encoding – literal wrapped.

## Functionality

### Submitting a Request

The calling system will call the service with the required parameters. The result will be synchronously 
returned in the form of an XML. This Request is used to Validate the Signature in PDF against predetermined constraints.

### Handling Errors

Errors occurring during the Validation Process are returned in the response data. Unexpected errors will be returned as SOAP faults.

### Request

Request contains the actual PDF-file which Signature(s) need to be validated and additional information. For example of 
Service Request, see „Appendix 7 - Sample PDF Validation Service Request“.

- **document** - Container for the signed document to be validated and additional information about the document     
  **Type**: wsDocument     
  **Example**: `<document>.... </document>`
	- **bytes** - Contents of the signed PDF document  
  	  **Type**: base64Binary	
	  **Example**: `<bytes>JVBERi0xLjQKJcOkw7zDtsOfC....</bytes>`
	- **name** - File Name of the document    
	  **Type**: string    
	  **Example**: `<name>/fixedpath/file.pdf</name>`  
	- **absolutePath** - Complete File Name of the document with Directory Path   
	  **Type**: string    
	  **Example**: `<absolutePath>/fixedpath/file.pdf </absolutePath>`
	- **mimeType** - MIME-type of the document (PDF)      
	  **Type**: mimeType      
	  **Example**: `<mimeType>PDF</mimeType>`
	- **nextDocument** - Determines if Diagnostic Data element is returned in Validation Service's Response.   
	  **Type**: wsDocument
- **diagnosticDataToBeReturned** - Determines if Diagnostic Data element is returned in Validation Service's Response.    
  **Type**: boolean    
  **Example**: `<diagnosticDataToBeReturned>true</diagnosticDataToBeReturned>`

### Response

Response object validateDocumentResponse is an XML containing the results of validating PDF Signature against constraints. The Service’s Response must conform to ETSI TS 102 853 standard.

The Response consists of three data blocks:

- **The Simple Report** -  is always returned in Response. Contains information about the validity of Signature according 
  to laws of the Republic of Estonia - validation time, status of validity, number of signatures and number of valid signatures 
  in document etc. See „1Appendix 8 - „The Simple Report“ for details. The Simple Report contains the following information:
	- `SimpleReport`
		- `Policy` - Container for information about the Validation policy that was used to validate the PDF-file
			- `PolicyName`- Validation Policy name. Defined in constraint configuration file constraint.xml   
i			  **Example**: `<PolicyName> QES AdESQC TL based </PolicyName>`
			- `PolicyDescription` - Description of Validation Policy    
			  **Example**: `<PolicyDescription> Validates  electronic signatures and indicates whether they are ......
</PolicyDescription>`
		- `ValidationTime` - Indicates the time when PDF-file was validated.    
	   	  **Example**: `<ValidationTime>2015-09-01T15:13:00Z </ValidationTime>`
		- `DocumentName` - Indicates the PDF-file that was validated     
		  **Example**:  `<DocumentName>/fixedpath/file.pdf</DocumentName>`
		- `Signature Id=“...“ SignatureFormat=“...“` - For every Signature in PDF-file, indicates the Signature ID and format    
		  **Example**: `<Signature Id="id-027bac117bcd86670c6eb8292925083b" SignatureFormat= "PAdES_BASELINE_LT">` 
			- `SigningTime` - Signing time of Signature     
			  **Example**: `<SigningTime>2015-07-09T07:00:48Z</SigningTime>`
			- `SignedBy` - Signer’s Common Name
			  **Example**: `<SignedBy>SURNAME,GIVENNAME,37101010101</SignedBy>`
			- `Indication` - Indicates the validity of Signature: VALID, INVALID, INDETERMINATE (see „5.1.45.1.6 Validity of Document“ for details)    
			  **Example**: `<Indication>VALID</Indication>`
			- `Info BestSignatureTime="DATE" NameId="..."`    
			  **Example**: `<Info BestSignatureTime="2015-07-09T07:00:55Z" NameId="EMPTY" />`
			- `SignatureLevel` - Indicates Signature Level    
			  **Example**: `<SignatureLevel>QES </SignatureLevel>`    
			- `SignatureScopes`
				- `SignatureScope name=“...“ scope=“...“`    
				  **Example**:    
				
					<SignatureScope name="PDF previous version #1" scope= "PdfByteRangeSignatureScope"> 
						The document byte range: [0, 14153, 52047, 491] 
					</SignatureScope>

		- `ValidSignaturesCount` - Indicates total number of valid Signatures in PDF-file    
		  **Example**: `<ValidSignaturesCount>1 </ValidSignaturesCount>`
		- `SignaturesCount` - Indicates total number of Signatures in PDF-file   
		  **Example**: `<SignaturesCount>1 </SignaturesCount>`

### Validity of Document

iThe Simple Report in the Service’s Response contains information about the validity of each Signature in PDF-file (according to passing of rules in Validation Policy):

- **VALID** - indicates that the signature has passed verification and it complies with the signature validation policy;
- **INVALID** - indicates that either the signature format is incorrect or that the digital signature value fails verification;
- **INDETERMINATE** - indicates that the format and digital signature verifications have not failed but there is insufficient information to determine if the electronic signature is valid.


