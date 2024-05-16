The following guide is for system integrators who need to set-up, configure, manage and troubleshoot SiVa service.

### System requirements

Following are the minimum requirements to build and deploy SiVa webapps as a service:

* Java 11 or above is supported
* Git version control system version 1.8 or above is recommended
* Minimum 2 GB of RAM. Recommended at least 4 GB of RAM
* Minimum 1 processor core
* Open internet connection
* 2GB of free disk space
* Supported operating system is Ubuntu 16.04 LTS

## Building

### Building SiVa webapps

It is recommended to build the project with Maven Wrapper. Run following command in the projects main directory:

```bash
./mvnw clean install
```

!!! note
    The first time build can take up to **45 minutes** because of downloading the required dependencies, running vulnerability checks and unit tests.

To verify that SiVa project built successfully look for `BUILD SUCCESS` in build compilation output last lines.
The last lines of build output should look very similar to below image:

```text
[INFO] Reactor Summary:
[INFO]
[INFO] SiVa Digitally signed documents validation service X.X.X SUCCESS [  2.089 s]
[INFO] validation-services-parent ......................... SUCCESS [  0.380 s]
[INFO] validation-commons ................................. SUCCESS [ 13.782 s]
[INFO] tsl-loader ......................................... SUCCESS [  9.372 s]
[INFO] Generic Validation Service ......................... SUCCESS [ 41.723 s]
[INFO] TimeStampToken Validation Service .................. SUCCESS [  8.400 s]
[INFO] Time-mark container Validation Service ............. SUCCESS [ 36.508 s]
[INFO] SiVa webapp and other core modules ................. SUCCESS [  0.374 s]
[INFO] siva-monitoring .................................... SUCCESS [ 11.982 s]
[INFO] siva-statistics .................................... SUCCESS [  9.816 s]
[INFO] SiVa validation service proxy ...................... SUCCESS [ 14.861 s]
[INFO] SiVa signature service ............................. SUCCESS [  7.801 s]
[INFO] siva-webapp ........................................ SUCCESS [ 42.451 s]
[INFO] SiVa Sample Web application ........................ SUCCESS [ 42.236 s]
[INFO] SiVa Web Service integration tests ................. SUCCESS [ 18.830 s]
[INFO] siva-distribution X.X.X ............................ SUCCESS [  5.763 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 04:46 min
[INFO] Finished at: 2020-07-03T14:22:02+03:00
[INFO] ------------------------------------------------------------------------

```


## Deploying

### OPTION 1 - starting webapps from command line
SiVa project compiles **2 fat executable JAR** files that you can run after successfully building the
project by issuing below commands:

**First start the Siva webapp**

```bash
./siva-parent/siva-webapp/target/siva-webapp-X.X.X.jar
```

The SiVa webapp by default runs on port **8080**.
Easiest way to test out the deployment is to run SiVa demo application and use it for validation.

**Start the Demo webapp**

```bash
./siva-parent/siva-sample-application/target/siva-sample-application-X.X.X.jar
```

Now point Your browser to URL: <http://localhost:9000>


### OPTION 2 - running webapps as systemd services

Maven build generates executable JAR files. This means web container and all its dependencies are package inside
single JAR file. It makes a lot easier to deploy it into servers.

Easiest option to setup SiVa is as `systemd` service in Ubuntu servers.

For that we first need to create service file:
```bash
vim siva-webapp.service
```

Inside it we need to paste below text. You need to change few things in service setup file.

* First you **must not** run service as `root`. So it's strongly recommended to change line `User=root`
* Second You can change Java JVM options by modifying the `JAVA_OPTS` inside the `siva-webapp.service` file.
* Also You can change the SiVa application configuration options by modifying `RUN_ARGS` section in file

```ini
[Unit]
Description=siva-webapp
After=syslog.target

[Service]
User=root
ExecStart=/var/apps/siva-webapp.jar
Environment=JAVA_OPTS=-Xmx320m RUN_ARGS=--server.port=80
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

Save and close the `siva-webapp.service` file.
Next we need to move `siva-webapp-X.X.X.jar` into newly created `/var/apps` directory and rename to
JAR file to `siva-webapp.jar`. match

!!! note
    The copied JAR filename must match option `ExecStart` in  `siva-webapp.service` file

```bash
sudo mkdir /var/apps
sudo cp siva-parent/siva-webapp/target/executable/siva-webapp-X.X.X.jar /var/apps/siva-webapp.jar
```

Next we need to copy the `siva-webapp.service` file into `/lib/systemd/system` directory.
Then we are ready to start the `siva-webapp` service.

```bash
sudo cp siva-webapp.service /lib/systemd/system
sudo systemctl start siva-webapp
```

Final step of setting up the `siva-webapp` service is to verify that service started correctly by issuing below
command.

```bash
systemctl status siva-webapp
```

It should print out similar to below picture:

```
● siva-webapp.service - siva-webapp
   Loaded: loaded (/lib/systemd/system/siva-webapp.service; disabled; vendor preset: enabled)
   Active: active (running) since Thu 2016-07-21 08:48:14 EDT; 1 day 2h ago
 Main PID: 15965 (siva-webapp.jar)
    Tasks: 34
   Memory: 429.6M
      CPU: 2min 5.721s
   CGroup: /system.slice/siva-webapp.service
           ├─15965 /bin/bash /var/apps/stage/siva-webapp.jar
           └─15982 /usr/bin/java -Dsun.misc.URLClassPath.disableJarChecking=true -Xmx320m -jar /var/apps/stage/siva-webapp.jar

Jul 20 03:00:01 siva siva-webapp.jar[15965]:         at eu.europa.esig.dss.tsl.service.TSLParser.getTslModel(TSLParser.java:143)
Jul 20 03:00:01 siva siva-webapp.jar[15965]:         at eu.europa.esig.dss.tsl.service.TSLParser.call(TSLParser.java:129)
Jul 20 03:00:01 siva siva-webapp.jar[15965]:         ... 5 common frames omitted
Jul 20 03:00:01 siva siva-webapp.jar[15965]: 20.07.2016 03:00:01.450 INFO  [pool-3-thread-1] [e.e.e.dss.tsl.service.TSLRepository.sync
Jul 20 03:00:01 siva siva-webapp.jar[15965]: 20.07.2016 03:00:01.450 INFO  [pool-3-thread-1] [e.e.e.dss.tsl.service.TSLRepository.sync
```

### OPTION 3  - deploy webapps as war files (Tomcat setup for legacy systems)

> **NOTE 1**: Each SiVa service **must** be deployed to separate instance of Tomcat to avoid Java JAR library version
> conflicts.

> **NOTE 2**: To limit your webapp request size (this is set automatically when deploying service as jar) one needs to configure the container manually. For example, when using [Tomcat 8](http://tomcat.apache.org/tomcat-8.0-doc/config/http.html) -
the http connector parameter `maxPostSize` should be configured with the desired limit.

> **NOTE 3**: The war file must be deployed to Tomcat ROOT.

First we need to download Tomcat web servlet container as of the writing latest version available in version 8 branch is 8.5.24. We will download it with `wget`

```bash
wget http://www-eu.apache.org/dist/tomcat/tomcat-8/v8.5.24/bin/apache-tomcat-8.5.24.tar.gz
```

Unpack it somewhere:

```bash
tar xf apache-tomcat-8.5.24.tar.gz
```

Now we should build the WAR file. We have created helper script with all the correct Maven parameters.

```bash
./war-build.sh
```

Final steps would be copying built WAR file into Tomcat `webapps` directory and starting the servlet container.

```bash
cp siva-parent/siva-webapp/target/siva-webapp-X.X.X.war apache-tomcat-8.5.24/webapps
./apache-tomcat-7.0.77/bin/catalina.sh run
```

> **IMPORTANT** siva-webapp on startup creates `etc` directory where it copies the TSL validaiton certificates
> `siva-keystore.jks`. Default location for this directory is application root or `$CATALINA_HOME`. To change
> this default behavior you should set environment variable `DSS_DATA_FOLDER`. 

> **IMPORTANT** When updating the siva-keystore.jks it is needed to delete the "temp" keystore from default/specified location. 
> Deleting the "temp" keystore is also needed when upgrading your deployment to newer SIVA version!

### How-to set WAR deployed SiVa `application.properties`

SiVa override properties can be set using `application.properties` file. The file can locate anywhare in the host system.
To make properties file accessible for SiVa you need to create or edit `setenv.sh` placed inside `bin` directory.

Contents of the `setenv.sh` file should look like:

```bash
export CATALINA_OPTS="-Dspring.config.location=file:/path/to/application.properties"
```


### Smoke testing your deployed system

**Step 1**. Install HTTPIE
`httpie` is more user friendly version of `curl` and we will use to verify that SiVa was installed
and started correctly on our server.

If you have Python and its package manager `pip` installed. Then You can issue below command:

```bash
pip install httpie
```

**Step 2**. Download a sample JSON request file.

```bash
http --download https://raw.githubusercontent.com/open-eid/SiVa/develop/build-helpers/sample-requests/bdocPass.json
```

**Step 3**. After successful download issue below command in same directory where you downloaded the file using
the command below.

```bash
http POST http://localhost:8080/validate < bdocPass.json
```
**Step 4**. Verify the output. Look for `signatureCount` and `validSignatureCount`, they **must** be equal.

## Logging

By default, logging works on the INFO level and logs are directed to the system console only. Logging functionality is handled by the SLF4J logging facade and on top of the Logback framework. As a result, logging can be configured via the standard Logback configuration file through Spring boot. Additional logging appenders can be added. Consult [logback documentation](http://logback.qos.ch/documentation.html) for more details on log file structure.

For example, adding application.properties to classpath with the **logging.config** property
```bash
logging.config=/path/to/logback.xml
```

## Statistics

For every validation a statistical report is composed that contains the following data:

| Property | Type | Description |
|----------|------|-------------|
| stats | Object | Object containing statistic info |
| stats.type | String | Container type ( text value that identifies the container type) of the validated document: ASiC-E, ASIC-S, PAdES, DIGIDOC_XML, N/A |
| stats.sigType | String | Signature type in validated document: XAdES, CAdES, PAdES, N/A |
| stats.usrId | String | (Text data that contains the SiVa user identifier for reports (from the HTTP x-authenticated-user header) or N/A) |
| stats.dur | Number | The time it takes to process an incoming request - measured in milliseconds |
| stats.sigCt | Number | The value of the "signaturesCount" element in the validation report |
| stats.vSigCt | Number | The value of the "validSignaturesCount" element in the validation report |
| stats.sigRslt  | Array | Array of signature statistic objects |
| stats.sigRslt[0] | Object | Object containing signature statistic info |
| stats.sigRslt[0].i | String | Value of signature indication field from the validation report |
| stats.sigRslt[0].si | String | Value of signature subindication field from the validation report. Element not present if not in validation report |
| stats.sigRslt[0].cc | String | Country code extracted from the signer cert subject field. The ISO-3166-1 alpha-2 country code that is associated with signature (the signing certificate or XX if the country cannot be determined. |
| stats.sigRslt[0].sf | String | Values of signatureFormat field from the validation report |

Example of statistic
```
{
   "stats": {
      "type": "PAdES",
      "sigType": "PAdES",
      "usrId": "sample_user1",
      "dur": 4021,
      "sigCt": 2,
      "vSigCt": 1,
      "sigRslt": [
         {"i":"TOTAL-PASSED", "cc":"EE", "sf":"PAdES_BASELINE_LT"},
         {"i":"INDETERMINATE", "si":"NO_CERTIFICATE_CHAIN_FOUND", "cc":"EE", "sf":"PAdES_BASELINE_LT"}
      ]
   }
}
```

This information is sent to log feeds (at INFO level) which can be redirected to files or to a syslog feed.


## Monitoring

SiVa webapps provide endpoints for external monitoring tools to periodically check the generic service health status.

### Health Endpoint

!!! note
    Note that this endpoint is not exposed by default.

The url for accessing JSON formatted health information with HTTP GET is `/monitoring/health` . See the [Interfaces section](../siva3/interfaces.md#service-health-monitoring) for response structure and details.

* **Exposing the health monitoring endpoint**

To expose the endpoint, use the following configuration parameter:
```bash
management.endpoints.web.exposure.include=health
```

* **External service health indicators**

The endpoint is implemented as a customized Spring boot [health endpoint](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html#production-ready-health), which allows to add custom health indicators.

Demo webapp and Siva webapp include additional information about the health of their dependent services.
These links to dependent web services have been preconfigured. For example, the Demo webapp is preset to check whether the Siva webapp is accessible from the following url (parameter `siva.service.serviceHost` value)/monitoring/health.

### Heartbeat endpoint

!!! note
    Note that this endpoint is not enabled nor exposed by default.

!!! note
    Note that this endpoint requires the health endpoint to be enabled and exposed in order to function.

The url for accessing JSON formatted heartbeat information with HTTP GET is `/monitoring/heartbeat`. See the [Interfaces section](../siva3/interfaces.md#simplified-health-monitoring) for response structure and details.

* **Enabling and exposing the heartbeat monitoring endpoint**

To enable and expose the endpoint, use the following configuration parameters:
```bash
management.endpoints.web.exposure.include=health,heartbeat
management.endpoint.heartbeat.enabled=true
```

* **Simplified service health indicator**

The endpoint is implemented by polling the health information directly from the underlying health endpoint implementation, but exposing just the aggregated overall service status, hiding everything else.

### Version information endpoint

!!! note
    Note that this endpoint is not enabled nor exposed by default.

The url for accessing JSON formatted version information with HTTP GET is `/monitoring/version`. See the [Interfaces section](../siva3/interfaces.md#version-information) for response structure and details.

* **Enabling and exposing the version information endpoint**

To enable and expose the endpoint, use the following configuration parameters:
```bash
management.endpoints.web.exposure.include=version
management.endpoint.version.enabled=true
```


## Validation Report Signature

SiVa provides the ability to sign the validation report. The idea of supplementing the validation report with a validation report signature is to prove the authority's authenticity and integrity over the validation.

!!! note
    Signing of validation report is disabled by default

To enable it, use the following configuration parameter:
```bash
siva.report.reportSignatureEnabled=true
```

When validation report signature is enabled, only detailed validation reports will be signed, simple reports will not be signed.
The validation report's digital signature is composed out of response's `validationReport` object. The target format of the signature is ASiC-E (signature level is configurable). The ASiC-E container contents are encoded into Base64 and put on the same level int the response as the validation report itself.

!!! note
    Enabling the validation report signing will affect the performance of the service.

Example structure of the response containing report signature:

```json
{
  "validationReport": {
  ...
  },
  "validationReportSignature": "ZHNmYmhkZmdoZGcgZmRmMTM0NTM..."
}
```

Supported interfaces for signature creation:

* **PKCS#11** - a platform-independent API for cryptographic tokens, such as hardware security modules (HSM) and smart cards
* **PKCS#12** - for files bundled with private key and certificate

Report signature configuration parameters:

Property | Description |
| -------- | ----------- |
|**siva.report.reportSignatureEnabled**| Enables signing of the validation report. Validation report will only be signed when requesting detailed report.  <ul><li>Default: **false**</li></ul> |
|**siva.signatureService.signatureLevel**| The level of the validation report signature. <br> **Example values:** <br> * XAdES_BASELINE_B <br> * XAdES_BASELINE_T <br> * XAdES_BASELINE_LT <br> * XAdES_BASELINE_LTA |
|**siva.signatureService.tspUrl**| URL of the timestamp provider. <br> Only needed when the configured signature level is at least XAdES_BASELINE_T |
|**siva.signatureService.ocspUrl**| URL of the OCSP provider. <br> Only needed when the configured signature level is at least XAdES_BASELINE_LT |
|**siva.signatureService.pkcs11.path**| path to PKCS#11 module (depends on your installed smart card or hardware token library, for example: /usr/local/lib/opensc-pkcs11.so) |
|**siva.signatureService.pkcs11.password**| pin/password of the smart card or hardware token |
|**siva.signatureService.pkcs11.slotIndex**| depends on the hardware token. E.g. Estonian Smart Card uses 2, USB eToken uses 0. <ul><li>Default: **0**</li></ul> |
|**siva.signatureService.pkcs12.path**| path to keystore file containing certificate and private key |
|**siva.signatureService.pkcs12.password**| password of the keystore file containing certificate and private key |

!!! note
    When configuring report signature, either PKCS#11 or PKCS#12 should be configured, no need to configure both.

--------------------------------------------------------------------------------------
## Configuration parameters

All SiVa webapps have been designed to run with predetermined defaults after building and without additional configuration.
However, all the properties can be overridden on the service or embedded web server level, if necessary.

By default, the service loads it's global configuration from the application.yml file that is packaged inside the jar file.
Default configuration parameters can be overridden by providing custom application.yml in the [following locations](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files), or using command line parameters or by using other [externalized configuration methods](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) methods.

For example, to configure the embedded Tomcat web server inside a fat jar to run on different port (default is 8080), change the **server.port** following property:
```bash
server.port=8080
```

Or to increase or modify the default http request limit, override the **siva.http.request.max-request-size-limit** property:
```bash
siva.http.request.max-request-size-limit: 15MB
```

See the reference list of all common [application properties](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html) provided by Spring boot

### Siva webapp parameters

* Updating TSL

| Property | Description |
| -------- | ----------- |
| **siva.tsl.loader.loadFromCache** | A boolean value that determines whether the TSL is loaded from disk cache instead of by downloading a new TSL in a predetermined interval.<br/><br/>Note that the cache files are by default stored in a system temporary folder (can be set with system property `java.io.tmpdir`; make sure the application has both read and write permissions in that folder).<ul><li>When set to **false**, the cache is refreshed periodically by SiVa in a predetermined interval specified by `siva.tsl.loader.schedulerCron` using `siva.tsl.loader.url`.</li><li>When set to **true**, SiVa uses existing cache as it's TSL. No direct polling for updates is performed, so all the files (LOTL and TSL files) must be present in system temporary directory. The cache files must have the same filenames as their URLs, but special characters need to be replaced with underscores. For example, the file downloaded from https://sr.riik.ee/tsl/estonian-tsl.xml must be named `https___sr_riik_ee_tsl_estonian_tsl_xml`</li><li>Default: **false**</li></ul> |
| **siva.tsl.loader.onlineCacheExpirationTime** | A string value in a [format based on ISO-8601 duration format PnDTnHnMn.nS](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Duration.html#parse(java.lang.CharSequence)) that determines the expiration time of TSL disk cache in case `siva.tsl.loader.loadFromCache` is set to `false`. The default is 1 hour.<br/><br/>Note that the expiration time only determines, for each cached file, the minimum time that must have been passed since their last update before that file is considered expired and is susceptible to an update. The actual update is performed periodically by SiVa (specified by `siva.tsl.loader.schedulerCron`) or when the application is (re)started. <ul><li>Default: **PT1H**</li></ul> |
| **siva.tsl.loader.url** | A url value that points to the external TSL <ul><li>Default: **https://ec.europa.eu/tools/lotl/eu-lotl.xml**</li></ul> |
| **siva.tsl.loader.ojUrl** | A url value that points to the legal act in Official Journal of the European Union <ul><li>Default: **https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2019.276.01.0001.01.ENG**</li></ul> |
| **siva.tsl.loader.lotlRootSchemeInfoUri** | A url value that points to the European Unions' disclaimer regarding LOTL <ul><li>Default: **https://ec.europa.eu/tools/lotl/eu-lotl-legalnotice.html**</li></ul> |
| **siva.tsl.loader.code** | Sets the LOTL code in DSS <ul><li>Default: **EU**</li></ul> |
| **siva.tsl.loader.trustedTerritories** | Sets the trusted territories by countries <ul><li>Default: **"AT", "BE", "BG", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "GR", "HU", "HR", "IE", "IS", "IT", "LT", "LU", "LV", "LI", "MT", "NO", "NL", "PL", "PT", "RO", "SE", "SI", "SK", "UK"**</li></ul> |
| **siva.tsl.loader.schedulerCron** | A string in a [Crontab expression format](http://www.manpagez.com/man/5/crontab/) that defines the interval at which the TSL renewal process is started. The default is 03:00 every day (local time) <ul><li>Default: **0 0 3 \* * ?**</li></ul> |
| **siva.tsl.loader.sslTruststorePath** | Path to truststore containing trusted CA certificates used in HTTPS connection to retrieve member states TSLs <ul><li>Default: **classpath:tsl-ssl-truststore.p12**</li></ul> |
| **siva.tsl.loader.sslTruststoreType** | Truststore type <ul><li>Default: **PKCS12**</li></ul> |
| **siva.tsl.loader.sslTruststorePassword** | Truststore password <ul><li>Default: **digidoc4j-password**</li></ul>  |
| **siva.tsl.loader.LotlPivotSupportEnabled** | A boolean value that determines, whether LOTL pivot mode should be used or not <ul><li>Default: **true**</li></ul> |
| **siva.keystore.type** | Keystore type <ul><li>Default: **JKS**</li></ul> |
| **siva.keystore.filename** | Keystore that contains public keys of trusted LOTL signers for LOTL signature validation<ul><li>Default: **siva-keystore.jks**</li></ul> |
| **siva.keystore.password** | Keystore password <ul><li>Default: **siva-keystore-password**</li></ul> |

!!! note
    Note that the keystore file location can be overriden using environment variable `DSS_DATA_FOLDER`. By default the keystore file location, is expected to be on local filesystem in `etc` directory which is at the same level with the fat jar file (one is created, if no such directory exists).

!!! note
    When updating the siva-keystore.jks it is needed to delete the "temp" keystore from default/specified location. Deleting the "temp" keystore is also needed when upgrading your deployment to newer SIVA version!

!!! note
    TSL is currently used only by Generic and BDOC validators


* Configure SOAP services endpoint URL-s displayed in WSDL

| Property | Description |
| ------ | ----------- |
| **siva.wsdl.endpoint-url** | SOAP services endpoint URL to what specific service name is added within the application. Must contain only scheme, host and/or port and optional path. Service name must not be added to the url.  |

* TimeMark validation - customizing policies

| Property | Description |
| -------- | ----------- |
| **siva.bdoc.digidoc4JConfigurationFile** | Path to Digidoc4j configuration override <ul><li>Default: **N/A**</li></ul> |

| Property | Description |
| -------- | ----------- |
|**siva.bdoc.signaturePolicy.defaultPolicy**| Selected default policy name <ul><li>Default: **N/A**</li></ul>|
|**siva.bdoc.signaturePolicy.policies[index].name**| Policy name <ul><li>Default: **N/A**</li></ul>|
|**siva.bdoc.signaturePolicy.policies[index].description**| Policy description <ul><li>Default: **N/A**</li></ul>|
|**siva.bdoc.signaturePolicy.policies[index].constraintPath**| Constraint XML file path for the policy. An absolute path or a reference to a resource on the classpath<ul><li>Default: **N/A**</li></ul>|
|**siva.bdoc.signaturePolicy.policies[index].url**| Policy URL <ul><li>Default: **N/A**</li></ul>|

By default, the following configuration is used
```text
siva.bdoc.signaturePolicy.policies[0].name=POLv3
siva.bdoc.signaturePolicy.policies[0].description=Policy for validating Electronic Signatures and Electronic Seals regardless of the legal type of the signature or seal (according to Regulation (EU) No 910/2014), i.e. the fact that the electronic signature or electronic seal is either Advanced electronic Signature (AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES) does not change the total validation result of the signature.
siva.bdoc.signaturePolicy.policies[0].url=http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv3
siva.bdoc.signaturePolicy.policies[0].constraintPath=bdoc_constraint_ades.xml

siva.bdoc.signaturePolicy.policies[1].name=POLv4
siva.bdoc.signaturePolicy.policies[1].description=Policy for validating Qualified Electronic Signatures and Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive validation result.
siva.bdoc.signaturePolicy.policies[1].url=http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
siva.bdoc.signaturePolicy.policies[1].constraintPath=bdoc_constraint_qes.xml
```

!!! note
    Default policy configuration is lost when policy detail properties (name, description, url or constraintPath) are overridden or new custom policies added in custom configuration files (in this case, the existing default policies must be redefined in configuration files explicitly)

* Generic validation - customize validation policies

| Property | Description |
| -------- | ----------- |
|**siva.europe.signaturePolicy.defaultPolicy**| Selected default policy name <ul><li>Default: **N/A**</li></ul>|
|**siva.europe.signaturePolicy.policies[index].name**| Policy name <ul><li>Default: **N/A**</li></ul>|
|**siva.europe.signaturePolicy.policies[index].description**| Policy description <ul><li>Default: **N/A**</li></ul>|
|**siva.europe.signaturePolicy.policies[index].constraintPath**| Constraint XML file path for the policy. An absolute path or a reference to a resource on the classpath<ul><li>Default: **N/A**</li></ul>|
|**siva.europe.signaturePolicy.policies[index].url**| Policy URL <ul><li>Default: **N/A**</li></ul>|

By default, the following configuration is used
```text
siva.europe.signaturePolicy.policies[0].name=POLv3
siva.europe.signaturePolicy.policies[0].description=Policy for validating Electronic Signatures and Electronic Seals regardless of the legal type of the signature or seal (according to Regulation (EU) No 910/2014), i.e. the fact that the electronic signature or electronic seal is either Advanced electronic Signature (AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES) does not change the total validation result of the signature.
siva.europe.signaturePolicy.policies[0].url=http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv3
siva.europe.signaturePolicy.policies[0].constraintPath=generic_constraint_ades.xml

siva.europe.signaturePolicy.policies[1].name=POLv4
siva.europe.signaturePolicy.policies[1].description=Policy for validating Qualified Electronic Signatures and Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive validation result.
siva.europe.signaturePolicy.policies[1].url=http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
siva.europe.signaturePolicy.policies[1].constraintPath=generic_constraint_qes.xml
```

!!! note
    Default policy configuration is lost when policy detail properties (name, description, url or constraintPath) are overridden or new custom policies added in custom configuration files (in this case, the existing default policies must be redefined in configuration files explicitly)

* Configure SiVa to request revocation status for T level signatures

By default, T level signatures do not contain revocation data.
It is possible to configure SiVa to use OnlineOCSPSource in order to request revocation status during the validation process for T level signatures.

An example of configuring SiVa to request an OCSP response only for certificates issued by Latvian and Lithuanian service providers:
```text
t-level-signature-filter.filter-type = ALLOWED_COUNTRIES
t-level-signature-filter.countries[0] = LV
t-level-signature-filter.countries[1] = LT
```

!!! note
    If enabled, the revocation request is made for every signature level for given countries, not only T level signatures.

| Property | Description |
| -------- |-------------|
|**t-level-signature-filter.filter-type**| A string value that determines, which filter is being used. There are two available options: ALLOWED_COUNTRIES or NOT_ALLOWED_COUNTRIES <ul><li>When set to **ALLOWED_COUNTRIES** SiVa uses OnlineOCSPSource to request revocation status for the list of provided countries. If the list of countries is left empty, no OCSP requests are made</li><li>When set to **NOT_ALLOWED_COUNTRIES** SiVa uses OnlineOCSPSource to request revocation status for all the countries that are not on the list. If the list of countries is left empty, it makes an OCSP request for every country</li><li>Default: **N/A**</li></ul> |
|**t-level-signature-filter.countries**| A list of countries to be provided to the filter. For example: EE, LV, BE <ul><li>Default: **N/A**</li></ul>|
   
### Demo webapp parameters

* Linking to SiVa webapp

| Property | Description |
| -------- | ----------- |
|**siva.service.serviceHost**| An HTTP URL link to the Siva webapp <ul><li>Default: **http://localhost:8080**</li></ul> |
|**siva.service.jsonServicePath**| Service path in Siva webapp to access the REST/JSON API<ul><li>Default: **/validate**</li></ul> |
|**siva.service.soapServicePath**| Service path in Siva webapp to access the SOAP API <ul><li>Default: **/soap/validationWebService/validateDocument**</li></ul> |
|**siva.service.jsonDataFilesServicePath**| Data file service path in Siva webapp to access the REST/JSON API<ul><li>Default: **/getDataFiles**</li></ul> |
|**siva.service.soapDataFilesServicePath**| Data file service path in Siva webapp to access the SOAP API <ul><li>Default: **/soap/dataFilesWebService/getDocumentDataFiles**</li></ul> |
|**siva.service.trustStore**| Path to Siva webapp truststore on classpath <ul><li>Default: **siva_server_truststore.p12**</li></ul> |
|**siva.service.trustStorePassword**| Siva webapp truststore password <ul><li>Default: **password**</li></ul> |
