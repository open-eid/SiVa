# Component diagram

![SiVa component diagram](/img/siva/siva_module_diagram.png)

* Web API is standard Spring MVC module it will take in JSON or SOAP requests sent by
  systems that integrate with SIVA web service API
* Validation service proxy or validation service selector is Spring module that will choose
  the appropriate validation service for user request
* TSL loader loads in contents of TSL file from given URL in online mode or from directory when
  using offline mode in predefined interval.
* PüPKI proxy converts PüPKI DB SQL results to Java objects that validation services,
  validation service proxy and Web API will use
* Validation services (listed below) validate different digitally signed documents
     * PDF validation service for PDF files will use **DigiDoc4J DSS forked** library
     * BDOC for ASiC compliant containers both TM and TS will latest Maven released **DigiDoc4J** library
     * DDOC for previous generation digitally signed files will use latest Maven release of **JDigiDoc**
     * X-Road Signature validation service for X-Road web service signature files will use X-Road Security server project
       provided X-Road signature validation command line utility.
* PüPKI DB relational database providing client information and client request counting
* PüPKI UI a separate Python application that will provide CRUD operations for clients
