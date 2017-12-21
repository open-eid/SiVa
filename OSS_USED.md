# We use open source software

SiVa is built using only open source software. Below are all SiVa applications and open source
Java libraries used by each applications. Libraries are grouped by license.

Libraries license information was obtained using below command:

```bash
./mvnw project-info-reports:dependencies
```

## Siva Webapp

* **The Apache Software License, Version 2.0:** JBoss Logging 3, Undertow Core, Undertow Servlet, Undertow WebSockets JSR356 implementations,
Cryptacular Library, Objenesis, Javassist, Apache Commons CLI, Apache Commons Codec, Apache Commons Collections, Apache Commons Compress,
Apache Commons Lang, Apache FontBox, Apache HttpClient, Apache HttpCore, Apache PDFBox, AssertJ fluent assertions, Hibernate Validator Engine,
SnakeYAML, Spring AOP, Spring Beans, Spring Boot, Spring Boot Actuator, Spring Boot Actuator Starter, Spring Boot AutoConfigure, Spring Boot
Logging Starter, Spring Boot Starter, Spring Boot Test, Spring Boot Test Auto-Configure, Spring Boot Test Starter, Spring Boot Undertow Starter,
Spring Boot Web Starter, Spring Context, Spring Core, Spring Expression Language (SpEL), Spring TestContext Framework, Spring Web,
Spring Web MVC, ASM based accessors helper used by json-smart, Apache CXF Core, Apache CXF Runtime Core for WSDL, Apache CXF Runtime HTTP Transport,
Apache CXF Runtime JAX-WS Frontend, Apache CXF Runtime JAXB DataBinding, Apache CXF Runtime SOAP Binding, Apache CXF Runtime Simple Frontend, Apache
CXF Runtime WS Addressing, Apache CXF Runtime WS Policy, Apache CXF Runtime XML Binding, Apache Commons Logging, Apache Log4j, Apache Neethi, Apache
XML Security for Java, Bean Validation API, ClassMate, Commons IO, Guava: Google Core Libraries for Java, IntelliJ IDEA Annotations, JSON Small
and Fast Parser, JSONassert, Jackson-annotations, Jackson-core, Json Path, PowerMock, Woodstox, XML Commons Resolver Component, Xalan Java, Xalan
Java Serializer, XmlSchema Core, jackson-databind
* **Eclipse Public License 1.0:** JUnit, Logback Classic Module, Logback Core Module
* **The BSD License:** Stax2 API, ASM Core, jcabi-log, jcabi-manifests
* **MIT License:** JCL 1.2 implemented over SLF4J, JUL to SLF4J bridge, SLF4J API Module, Mockito, Project Lombok
* **New BSD License:** Hamcrest Core, Hamcrest library
* **Apache Software Licenses:** Log4j Implemented Over SLF4J
* **GNU General Public License, Version 2 with the Classpath Exception:** Java(TM) API for WebSocket
* **MPL 1.1:** Javassist
* **GNU Lesser General Public License, Version 2.1:** DigiDoc4j, Java DigiDoc library
* **Common Development and Distribution License:** Java(TM) API for WebSocket
* **The JSON License:** JSON in Java
* **Public Domain:** XNIO API, XNIO NIO Implementation
* **CDDL or GPLv2 with exceptions:** Common Annotations 1.2 API
* **GNU Lesser General Public License:** Cryptacular Library, DSS ASiC Common, DSS ASiC with CAdES signature(s), DSS ASiC with XAdES signature(s),
DSS CAdES, DSS Document, DSS Model, DSS PAdES, DSS Reports, DSS Service, DSS Service Provider Interface, DSS TSL Validation, DSS Token, DSS Utils
API, DSS Utils implementation with Apache Commons, DSS Utils implementation with Google Guava, DSS XAdES, JAXB Detailed Report Data Model, JAXB
Diagnostic Data Model, JAXB Simple Report Model, JAXB TSL Model, JAXB Validation Policy Data Model, Logback Classic Module, Logback Core Module,
dss-common-validation-jaxb, validation-policy
* **European Union Public License 1.1:** BDOC Validation Service, DDOC Validation Service, Generic Validation Service, SiVa signature service, SiVa validation service proxy, TimeStampToken Validation Service, siva-monitoring, siva-statistics, siva-webapp, tsl-loader, validation-commons
* **Bouncy Castle Licence:** Bouncy Castle PKIX, CMS, EAC, TSP, PKCS, OCSP, CMP, and CRMF APIs, Bouncy Castle Provider, Bouncy Castle S/MIME API
* **CDDL + GPLv2 with classpath exception:** Expression Language 3.0, Java Servlet API
* **CPL:** WSDL4J
* **CDDL+GPL License:** Old JAXB Core, Old JAXB Runtime
* **LGPL 2.1:** Javassist

## SiVa Sample Application

* **Apache License, Version 2.0:** JBoss Logging 3, rxjava-spring-boot-starter, Apache Commons Codec, Apache Commons Collections, Apache Commons Lang,
Apache HttpClient, Apache HttpClient Mime, Apache HttpCore, AssertJ fluent assertions, Hibernate Validator Engine, HtmlUnit, HtmlUnit NekoHtml, SnakeYAML,
Spring AOP, Spring Beans, Spring Boot, Spring Boot Actuator, Spring Boot Actuator Docs, Spring Boot Actuator Starter, Spring Boot AutoConfigure, Spring Boot
Cache Starter, Spring Boot Developer Tools, Spring Boot Logging Starter, Spring Boot Security Starter, Spring Boot Starter, Spring Boot Test, Spring Boot
Test Auto-Configure, Spring Boot Test Starter, Spring Boot Tomcat Starter, Spring Boot Web Starter, Spring Context, Spring Context Support, Spring Core,
Spring Expression Language (SpEL), Spring HATEOAS, Spring TestContext Framework, Spring Web, Spring Web MVC, Wro4j Spring Boot Starter, tomcat-annotations-api,
tomcat-embed-core, tomcat-embed-el, tomcat-embed-websocket, ASM based accessors helper used by json-smart, Apache Commons Logging, Bean Validation API, Caffeine
cache, ClassMate, Commons IO, Commons JEXL, ConcurrentLinkedHashMap, JSON Small and Fast Parser, JSONassert, Jackson-annotations, Jackson-core,
Jackson-dataformat-YAML, Json Path, Spring Boot jade4j Starter, XML Commons External Components XML APIs, Xalan Java, Xalan Java Serializer, Xerces2-j, ZT Zip,
jackson-databind, rxjava, spring-security-config, spring-security-core, spring-security-web, wro4j core
* **Eclipse Public License 1.0:** JUnit
* **Mozilla Public License version 2.0:** HtmlUnit Core JS
* **MIT License:** JCL 1.2 implemented over SLF4J, JUL to SLF4J bridge, SLF4J API Module, dropzone, jade4j, spring-jade4j, Mockito, Project Lombok, bootstrap, jquery, js-cookie, tether
* **New BSD License:** Hamcrest Core, Hamcrest library
* **Apache 2:** Objenesis, parboiled-core, parboiled-java, pegdown
* **BSD:** ASM Analysis, ASM Core, ASM Tree, ASM Util, jcabi-log, jcabi-manifests
* **Apache Software Licenses:** Log4j Implemented Over SLF4J
* **GNU General Public License, Version 2 with the Classpath Exception:**
* **BSD 3-Clause:** highlightjs
* **Apache Software License - Version 2.0:** Jetty :: Asynchronous HTTP Client, Jetty :: Http Utility, Jetty :: IO Utility, Jetty :: Utilities, Jetty :: Websocket :: API, Jetty :: Websocket :: Client, Jetty :: Websocket :: Common, Jetty :: XML utilities
* **The SAX License:** XML Commons External Components XML APIs
* **The W3C License:** XML Commons External Components XML APIs
* **The JSON License:** JSON in Java
* **European Union Public License 1.1:** SiVa Sample Web application, siva-monitoring
* **GNU Lesser General Public License:** CSS Parser, Logback Classic Module, Logback Core Module
* **Eclipse Public License - Version 1.0:** Jetty :: Asynchronous HTTP Client, Jetty :: Http Utility, Jetty :: IO Utility, Jetty :: Utilities, Jetty :: Websocket :: API, Jetty :: Websocket :: Client, Jetty :: Websocket :: Common, Jetty :: XML utilities
* **Eclipse Public License - v 1.0:** Logback Classic Module, Logback Core Module
* **The W3C Software License:** Simple API for CSS

## SiVa XRoad Validation Service

* **The Apache Software License, Version 2.0:** JBoss Logging 3, Undertow Core, Undertow Servlet, Undertow WebSockets JSR356 implementations, Objenesis, Apache Commons IO,
Apache Commons Lang, AssertJ fluent assertions, Hibernate Validator Engine, SnakeYAML, Spring AOP, Spring Beans, Spring Boot, Spring Boot Actuator, Spring Boot Actuator Starter,
Spring Boot AutoConfigure, Spring Boot Logging Starter, Spring Boot Starter, Spring Boot Test, Spring Boot Test Auto-Configure, Spring Boot Test Starter, Spring Boot Undertow
Starter, Spring Boot Web Starter, Spring Context, Spring Core, Spring Expression Language (SpEL), Spring TestContext Framework, Spring Web, Spring Web MVC, ASM based accessors
helper used by json-smart, Apache XML Security for Java, Bean Validation API, ClassMate, Commons Logging, IntelliJ IDEA Annotations, JSON Small and Fast Parser, JSONassert,
Jackson-annotations, Jackson-core, Json Path, ZT Zip, jackson-databind
* **Common Development and Distribution License:** Java(TM) API for WebSocket
* **Eclipse Public License 1.0:** JUnit, Logback Classic Module, Logback Core Module
* **MIT License:** JCL 1.2 implemented over SLF4J, JUL to SLF4J bridge, SLF4J API Module, Mockito, Project Lombok
* **The JSON License:** JSON in Java
* **Public Domain:** XNIO API, XNIO NIO Implementation
* **CDDL or GPLv2 with exceptions:** Common Annotations 1.2 API
* **GNU Lesser General Public License:** DSS Model, DSS Service Provider Interface, DSS Utils API, JAXB Detailed Report Data Model, Logback Classic Module, Logback Core Module, dss-common-validation-jaxb
* **New BSD License:** Hamcrest Core, Hamcrest library
* **Bouncy Castle Licence:** Bouncy Castle PKIX, CMS, EAC, TSP, PKCS, OCSP, CMP, and CRMF APIs, Bouncy Castle Provider
* **BSD:** ASM Core, ASM Core, jcabi-log, jcabi-manifests
* **European Union Public License 1.1:** siva-monitoring, validation-commons, xroad-validation-service
* **Apache Software Licenses:** Log4j Implemented Over SLF4J
* **CDDL + GPLv2 with classpath exception:** Expression Language 3.0, Java Servlet API
* **GNU General Public License, Version 2 with the Classpath Exception:** Java(TM) API for WebSocket
