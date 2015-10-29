PDF Valdiator
=============

Used for validating PDF digital signature container files.
This project depends on Digital Signature Service (DSS), Github: https://github.com/esig/dss, Project: https://joinup.ec.europa.eu/asset/sd-dss/description

How to build
------------

```bash
mvn clean install -Dmaven.test.skip.exec -DargLine="-Xmx512m"
```

Above command will have quite long running time and may run into complications due to DSS tests, to skip these tests run:
```bash
 mvn clean install -Dmaven.test.skip=true
```

Introducing new modules in addition to existing DSS modules:

PDF Validator Webapp
--------------------

Used to deploy to servlet container to offer the validation service.

PDF Valdiator Tests
-------------------

Run tests:
```bash
 cd pdf-validator-test
 mvn test
```
Verify that the service_url property in src/main/config/test.properties points to the correct path, where the service is running.
Some of the test files are signed with test certificate (chain), so in order for all the tests to pass run the servlet container (where the validation service resides) with development profile:
```bash
 -Dspring.profiles.active=development
```

PDF Valdiator TSL Downloader
----------------------------

Used to load/update TSL in cache, when the server itself is behind a firewall and cannot access the TSL over the internet.
Steps to manually update TSL files temp directory:

1.  `java -jar pdf-validator-tsl-downloader-4.5.RC1.jar` -> It creates tmp directory in same directory where the JAR file is.
2.  Copy tmp directory contents to Tomcat tmp directory.

Make sure there is application-override.properties file inside Tomcat's conf directory with following contents:
```bash
trustedListSource.tslRefreshPolicy=NEVER
```
When updating TSL the service should be shut down to avoid unwanted behaviour.

PDF Valdiator Monitoring
------------------------

PDF Validator monitoring is a web service to check that the PDF validator service is running correctly. Run with following command:
```bash
java -jar pdf-validator-monitoring-4.5.RC1.jar
```

Default parameters for monitoring application are:
```bash
server.port=9000
monitoring.host=http://localhost:8080
monitoring.path=/pdf-validator-webapp/wservice/validationService
# Default monitoring interval is 5 min
monitoring.requestInterval=300000
```
You can override these properties by placing application.properties file in same directory where the pdf-validator-monitoring-4.5.RC1.jar is located

To get the monitoring report, just request the monitoring service on the configured port, for example:
```bash
curl -s http://localhost:9000
```

Documentation
-------------

You can read more detailed documentation about PDF Validator here [in our GitHub Pages](http://open-eid.github.io/pdf-validator/).

To update and edit documentation follow these steps:

> **NOTE** Python must be installed before You continue

1.  Install MkDocs: `pip install mkdocs`
2.  Edit markdown files inside the `docs` directory
3.  Preview Your changes by issuing `mkdocs serve` and navigating to `http://localhost:8000`
4.  Commit Your changes to `git`
5.  Generate GitHub Pages: `mkdocs gh-deploy` 
6.  You are done


