The following chapter depicts SiVa software on a component diagram.
Note that not all of the external base libraries are included in the component model to avoid duplicity with other documentation. Only relevant base libraries and dependencies are listed and described in the documentation. Information about additional and transitive dependencies of the respective validation service components can be found via the references provided.

![SiVa component diagram](../img/siva/siva3_component_diagram.png)

## System components
### Siva webapp

All validation services use different Java library to validate given document in request. The used validation
library is described in each of the validation service section.

| Component | Description |
|------------------------------|--------------------------------------------------------|
| **Web gateway service** | Web gateway service is a single access point to the whole SiVa webapp application. Web service is implemented as a standard Spring Boot Web application module, that accepts valid JSON requests (see the [Interfaces section](../siva3/interfaces.md)) sent by the client systems. Web service module is responsible for basic request and response handling. This includes basic validation of incoming requests (existence of required fields, permitted values, etc) and unmarshaling the request to a Java object before passing it to the next component – the Proxy service. Response from the proxy service is marshalled and sent back to the client according to respective API. |
| **Report signing service**   | Provides signing services for report. Supported interfaces for signature creation: PKCS11, PKCS12.|
| **Validation proxy service** | Acts as a router for the request and response. It is responsible for selecting the appropriate validation service for incoming document.<br>- Proxy service accepts an in memory representation of a document and finds a matching validation service based on document type. The incoming request is converted to validation request and forwarded to the specific validation service.<br>- The report returned by the specific validation service is passed to the statistics service before returning it to Web service. |
| **Hashcode validation proxy service** | Acts as a router for the request and response. It is responsible for selecting the appropriate hashcode validation service for incoming document.<br>- The incoming request is converted to validation request and forwarded to the specific validation service.<br>- The report returned by the specific validation service is passed to the statistics service before returning it to Web service. |
| **Data files proxy service** | Acts as a router for the request and response. It is responsible for selecting the appropriate data files extraction service for incoming document.<br>- Proxy service accepts an in memory representation of a document and finds a matching validation service based on document type. |
| **Statistics service** | A component for collecting statistics data to log. It is Spring Boot module and main purpose for it is to collect data about: incoming request, validation reports and errors that have been reported during validation process.<br>- When HTTP authentication header have been set the reporting service will also add this info to statistics report.<br>- After the report object has been created the data will be logged. |
| **Generic validation service** | Provides validation services for detached signatures and ASIC containers along with PAdES, CAdES and XAdES signatures. Uses Maven released *DigiDoc4J EU DSS fork* library|
| **Timemark container validation service** | Provides validation services for BDOC and DDOC containers. BDOC for ASiC compliant containers both TM and TS is supported. Uses Maven released **DigiDoc4J** library |
| **TST validation service** | Provides validation service for Time Stamp Token based ASIC-S containers with Estonian specific validation policy |
| **DDOC data files service** | Provides files extraction services for DigiDoc containers. DDOC for previous generation digitally signed files. Uses latest Maven release of **JDigiDoc** |
| **Dss** | Dss library implementations are used for ASICE, XAdES, CAdES, PAdES and TST validation and TSL loading. SiVa uses [Digidoc4J DSS fork Java library](https://github.com/open-eid/sd-dss). |
| **TSL loader** | TSL loader loads the contents of TSL file from given URL in online mode or from directory when using offline mode in predefined interval.|
| **Logger** | Logging functionality is handled by the **SLF4J** logging facade and on top of the **Logback** framework. As a result, logging can be configured via the standard Logback configuration file. By default, logging works on the `INFO` level and logs are directed to the system console. Additional logging appenders can be added (consult logback documentation for more [details](http://logback.qos.ch/documentation.html))  |
| **Configuration** | Configuration is a global component used throughout the webapp. Responsible for reading and handling the application configuration management |
| **Validation commons** | Common interfaces and utilities for all validation services (ie utilities for default and additional policies and constraints) |
| **Monitoring service** | Service that provides generic information about the webapp - it's name, version, uptime and link status. (The service is disabled by default) |

### Siva webapp interfaces

Provided:

* Validation service REST interface - interface for handling validation queries.
    * JSON formatted POST requests
* Hashcode validation service REST interface - interface for handling hashcode validation queries.
    * JSON formatted POST requests
* Data file service REST interface - interface for handling data file extraction queries.
    * JSON formatted POST requests
* Monitoring service REST interface - interface for retrieving the webapp's health information
    * HTTP GET that returns JSON formatted respónses

Required:

* EU TSL provider interface - interface for loading the TSL

### Demo webapp

| Component | Description |
|------------------------------|--------------------------------------------------------|
| **Demo webapp** | A Spring boot based web application that provides a web interface to the SiVa system for initial access and testing purposes. Offers a simple and user friendly presentation layer for the whole SiVa webapp. Contains a simple form, that can be used to send signed documents to be validated in SiVa. |

### Demo webapp interfaces

Provided:

* Web interface - interface for handling signed requests.
    * JSON formatted POST queries

Required:

* Siva webapp REST interface

### External configuration

| Component | Description |
|------------------------------|--------------------------------------------------------|
| **Policies and constraints** | (Optional) By default, two policies are supported and integrated into SiVa - see [SiVa Validation Policy](../siva3/appendix/validation_policy.md). However, it is possible to customize the validation policies and their constraints. Configuration is validation service specific. See the [overriding configuration](../siva3/deployment_guide.md/#configuration-parameters) section for details. |
| **Externalized configuration** | Application specific configuration (property files, command line variables, etc) that allow overriding the default values set within the webapp.                                                                                                                                                                                                                                               |

## External subsystems

| Component | Description |
|------------------------------|--------------------------------------------------------|
| **DigiDoc4 client** | DigiDoc4 client is a program that can be used to sign digitally with ID-card and Mobile-ID, check the validity of digital signatures and open and save documents inside the signature container. See the github [project](https://github.com/open-eid/DigiDoc4-Client) for further details. |
| **XROAD security server** | A client subsystem that, once connected, allows SiVa to be integrated into the [X-Road data exchange layer](https://www.ria.ee/en/state-information-system/data-exchange-platforms/data-exchange-layer-x-tee)                                                                     |
| **EU TSL provider** | A subsystem that provides SiVa with a current TSL. An HTTPS endpoint, that provides an XML formatted file that lists all trusted service providers along with their certificates.                                                                                                 |
| **Monitoring** | (Optional) A subsystem that tracks the health and uptime of SiVa webapps</li></ul>                                                                                                                                                                                                |

