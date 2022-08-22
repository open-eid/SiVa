<!--# Introduction-->

<div class="eu-logo">
    <img src="img/siva/eu_logo.svg" />
</div>

SiVa is digital signature validation web service that provides SOAP and JSON
API to validate following file types:

 * Estonian DDOC containers
 * Estonian BDOC containers with `TimeMark` and `TimeStamp` signatures
 * Estonian ASiCS containers with time stamp tokens
 * ETSI standard based ASiCE and ASiCS containers
 * ETSI standard based XAdES, CAdES and PAdES signatures
 * ETSI standard based XAdES signatures with datafiles in hashcode form

Main purpose of this documentation is to give overview what SiVa is, how it is built and provide information for deploying the service and integrating with the service.

## SiVa architecture document sections overview

Below list will give You an overview of what each section of the
SiVa architecture document will cover:

* [**Definitions**](siva3/definitions) - defines and explines most common concepts used in SiVa documentation
* [**Overview**](siva3/overview) - gives overview what SiVa is and
  it's main features.
* [**Structure and activities**](siva3/structure_and_activities) - gives overview of
  main SiVa subsystems and and and base validation Java libraries
  used for different validation services
* [**Interfaces**](siva3/interfaces) - Description of SiVa
   SOAP and JSON API request and response
* [**Deployment**](siva3/deployment) - gives general overview of
  servers required when deploying SiVa validation web service
  into production
* [**Quality Assurance**](siva3/qa_strategy) - overview of quality assurance strategy and testing
* [**Roadmap**](siva3/roadmap) - info about planned releases

