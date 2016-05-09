# Use cases

## Digitally signed document validation process

Digitally signed document validation process shows how SiVa chooses
validation service and possible output of validation process.

![BDOC validation process](/img/siva/siva_bdoc_validation_process.png)

User of SiVa system provides digitally signed document file in form of 
Base64 encoded string. The validation of file and validation policy 
is handled by validation services underlying libraries. 

* In case of PDF file it will be DSS
* For BDOC and DDOC files we will use DigiDoc4J or when required jDigiDoc
* And for X-Road signatures we will use X-road signature validation utility 

We will log following failure cases:
When file upload fails (request started but was not completed successfully)
When request validation (JSON or SOAP) fails
When user authentication fails - **not shown in diagram above**
When signature validation fails – **not shown in diagram above**
When increasing of request count fails – **not shown in diagram above**

## CRL loading process

All validation services require certificates to validate digitally signed
documents. Below process shows how certificates are loaded into 
validation service. Loading process is done separably for each validation
service.

![CRL Loading process](/img/siva/siva_validator_crl_loading.png)

CRL loading process is scheduled cron job inside each validation 
service to update currently in memory loaded CRLs.

This process should run after TSL loader has completed updating 
SiVa local copy of CRLs.

## X-Road 6 security server SOAP request process

X-Road validation process is brought out because we skip authentication 
process for X-Road security server interface and and use XML SOAP 
as input source.

![X-Road SOAP validation request](/img/siva/siva_x_road_server_diagram.png)

Validation of SOAP request XML is done in the SiVa web application module.  
Document validation process is described in detail in [Digitally signed document validation process](#digitally-signed-document-validation-process)    
Validation report output id described in [Interface description](/siva/interface_description)

## Authenticate JSON REST API user

![JSON REST validation request](/img/siva/siva_remote_client_flowchart.png)

Validation of JSON request is done in  SiVA web application module 
Document validation process is described in detail in [Digitally signed document validation process](#digitally-signed-document-validation-process)    
Validation report output id described in [Interface description](/siva/interface_description)

## TSL loading use case

![TSL loading process](/img/siva/siva_tsl_loading_process.png)
