Appendix 1 - Non-Functional Requirements
========================================

Expandability
-------------

* **Name**: Expandability
  * **Description**: The Core Service has to be expandable to support the following AdES formats:
    * XAdES
    * CAdES
    * ASiC
  * **Must / Should**: Should
  * **NFR-ID**: DSS-EXPAND

Protocol and Data Format
------------------------

* **Name**: SOAP Protocol
    * **Description**: The Service uses SOAP-Protocol.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-PROT-SOAP
* **Name**: XML Data Format
    * **Description**: The  Service uses XML Data Format.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-PROT-XML
* **Name**: REST Protocol
    * **Description**: The Service uses REST-protocol.
      **Update**: In the beginning, we were planning to create our own RESTful
      JSON API; however, the developers of the European DSS Service (that our project
      is based on) have also started to develop their own JSON-based API; therefore,
      it would make more sense for us to simply adopt their new API, instead of
      creating a duplicating API of our own. However, as the development of the said
      JSON API has been postponed to the next version of DSS, at the moment we will
      have to use the existing official API, which SOAP-based and uses XML.
    * **Must / Should**: Should
    * **NFR-ID**: DSS-PROT-REST
* **Name**: JSON Data Format
    * **Description**: The Service uses JSON Data Format
    * **Must / Should**: Should
    * **NFR-ID**: DSS-PROT-JSON
* **Name**: Validation Data Structure in Response
    * **Description**: The Service must provide a Response that contains
      Validation Information conforming to ETSI TS 102 853.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-VAL-STANDARD

Signature Validation
--------------------

* **Name**: Signature Validation Standard
    * **Description**: The Signature Validation Process is based on ETSI TS 102 853 standard, additional Service's Signature Validation Policy constraints are applied.
    * **Must / Should**: Must
    * **NFR-ID**: DSS-SIG-VALIDATION

Deployment Environment
----------------------

* **Name**: Deployment to Tomcat
    * **Description**: The Service must be deployable as a standard Java WAR file into
      the Tomcat container version 8.0.
    * **Must / Should**: Must
    * **NFR-ID**: DEPLOY-WAR-TOMCAT
* **Name**: Deployment to other standard containers
    * **Description**: The Service should also be deployable to other well known
      containers in a similar manner as with Tomcat (for example, it should work
      in a straightforward way with Jetty and WebLogic).
    * **Must / Should**: Should
    * **NFR-ID**: DEPLOY-WAR-GENERAL
* **Name**: Required Java versions
    * **Description**: The Service must work with Java versions 7 and 8.
    * **Must / Should**: Must
    * **NFR-ID**: DEPLOY-JAVA
* **Name**: Configuration files
    * **Description**: If there is a need for user-specific configuration, the configuration must be in a form of a file on the server's file system.
    * **Must / Should**: Must
    * **NFR-ID**: DEPLOY-CONFIG
* **Name**: Firewall requirements
    * **Description**: If the Service needs to download the EU Trusted Lists, the Service can assume that the server firewalls are configured to support direct downloads from all of the URLs of the EU Trusted Lists.
    * **Must / Should**: Must
    * **NFR-ID**: DEPLOY-FIREWALL



