### System requirements

Following are the minimum requirements to build and deploy a SiVa webapps as a service:

* Java 8 or above Oracle JVM is supported
* Git version control system version 1.8 or above is recommended
* Minimum 2 GB of RAM. Recommended at least 4 GB of RAM
* Minimum 1 processor core
* Open internet connection
* 1GB of free disk space
* Supported operating system is Ubuntu 14.04 LTS

## Building

### Building SiVa webapps on Ubuntu 16.04

First we need to install Git and Java SDK 8 by issuing below commands:

```bash
sudo apt-get update
sudo apt-get install git -y
sudo apt-get install default-jdk -y
```

Next we need to clone the SiVa Github repository:

```bash
git clone https://github.com/open-eid/SiVa.git --branch develop
```

Final step is building the SiVa project using Maven Wrapper

```bash
cd SiVa
./mvnw install
```

!!! note
    The build can take up to **30 minutes** because there are lot of tests that will be run through and downloading of the
    required dependencies

To verify that SiVa project built successfully look for `BUILD SUCCESS` in build compilation output last lines.
The last lines of build output should look very similar to below image:

```text
[INFO] Reactor Summary:
[INFO]
[INFO] SiVa Digitally signed documents validation service . SUCCESS [ 25.258 s]
[INFO] validation-services-parent ......................... SUCCESS [  0.479 s]
[INFO] validation-commons ................................. SUCCESS [01:45 min]
[INFO] tsl-loader ......................................... SUCCESS [ 16.507 s]
[INFO] PDF Validation Service ............................. SUCCESS [ 42.263 s]
[INFO] BDOC Validation Service ............................ SUCCESS [ 58.864 s]
[INFO] DDOC Validation Service ............................ SUCCESS [  9.929 s]
[INFO] xroad-validation-service ........................... SUCCESS [  5.664 s]
[INFO] SIVa webapp and other core modules ................. SUCCESS [  0.315 s]
[INFO] SiVa validation service proxy ...................... SUCCESS [ 43.098 s]
[INFO] siva-webapp ........................................ SUCCESS [04:06 min]
[INFO] SiVa Sample Web application ........................ SUCCESS [04:31 min]
[INFO] SiVa Web Service integration tests ................. SUCCESS [03:41 min]
[INFO] siva-distribution .................................. SUCCESS [ 56.941 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 18:30 min
[INFO] Finished at: 2016-07-22T13:40:31+00:00
[INFO] Final Memory: 80M/250M
[INFO] ------------------------------------------------------------------------
```


## Deploying

### OPTION 1 - starting webapps from command line
SiVa project compiles **3 fat executable JAR** files that You can run after successfully building the
project by issuing below commands:

**First start SiVa REST and SOAP web service**

```bash
./siva-parent/siva-webapp/target/siva-webapp-2.0.2-SNAPSHOT.jar
```

**Second we need to start SiVa XRoad validation service**

```bash
./validation-services-parent/xroad-validation-service/target/xroad-validation-service-2.0.2-SNAPSHOT.jar
```

The SiVa webapp by default runs on port **8080** and XRoad validation service starts up on port **8081**.
Easiest way to test out validation is run SiVa demo application.

**Start SiVa Demo Application**

```bash
./siva-parent/siva-sample-application/target/siva-sample-application-2.0.2-SNAPSHOT.jar
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
Next we need to move `siva-webapp-2.0.2-SNAPSHOT.jar` into newly created `/var/apps` directory and rename to
JAR file to `siva-webapp.jar`. match

!!! note
    The copied JAR filename must match option `ExecStart` in  `siva-webapp.service` file

```bash
sudo mkdir /var/apps
sudo cp siva-parent/siva-webapp/target/executable/siva-webapp-2.0.2-SNAPSHOT.jar /var/apps/siva-webapp.jar
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

> **NOTE 1**: We do not recommend using WAR deployment option because lack of testing done on different servlet
> containers also possible container application libraries conflicts

> **NOTE 2**: Each SiVa service **must** be deployed to separate instance of Tomcat to avoid Java JAR library version
> conflicts.

> **NOTE 3**: To limit your webapp request size (this is set automatically when deploying service as jar) one needs to configure the container manually. For example, when using [Tomcat 7](http://tomcat.apache.org/tomcat-8.0-doc/config/http.html) or [Tomcat 8](http://tomcat.apache.org/tomcat-8.0-doc/config/http.html) -
the http connector parameter `maxPostSize` should be configured with the desired limit.

First we need to download Tomcat web servlet container as of the writing latest version available in version 7 branch is 7.0.77. We will download it with `wget`

```bash
wget http://www-eu.apache.org/dist/tomcat/tomcat-7/v7.0.70/bin/apache-tomcat-7.0.70.tar.gz
```

Unpack it somewhere:

```bash
tar xf apache-tomcat-7.0.70.tar.gz
```

Now we should build the WAR file. We have created helper script with all the correct Maven parameters.

```bash
./war-build.sh
```

> **NOTE** The script will skip running the integration tests when building WAR files

Final steps would be copying built WAR file into Tomcat `webapps` directory and starting the servlet container.

```bash
cp siva-parent/siva-webapp/target/siva-webapp-2.0.2-SNAPSHOT.war apache-tomcat-7.0.70/webapps
./apache-tomcat-7.0.77/bin/catalina.sh run
```

> **IMPORTANT** siva-webapp on startup creates `etc` directory where it copies the TSL validaiton certificates
> `siva-keystore.jks`. Default location for this directory is application root or `$CATALINA_HOME`. To change
> this default behavior you should set environment variable `DSS_DATA_FOLDER`

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
http --download https://raw.githubusercontent.com/open-eid/SiVa/develop/build-helpers/sample-requests/bdoc_pass.json
```

**Step 3**. After successful download issue below command in same directory where you downloaded the file using
the command below.

```bash
http POST http://10.211.55.9:8080/validate < bdoc_pass.json
```
**Step 4**. Verify the output. The output of previous command should look like below screenshot. Look for `signatureCount` and
`validSignatureCount` they **must** be equal.


![HTTPIE output validation](../../img/siva/siva-output.png)


--------------------------------------------------------------------------------------
## Custom configuration

All SiVa webapps have been designed to run with predetermined defaults after building and without additional configuration.
However, all the properties can be overridden on the service or embedded web server level, if necessary.

By default, the service loads it's global configuration from the application.yml file that is packaged inside the jar file.
Default configuration parameters can be overridden by providing custom application.yml in the [following locations](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files), or using command line parameters or by using other [externalized configuration methods](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) methods.

See the reference list of common application properties [provided by Spring boot](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)

For example, to configure the embedded Undertow web server inside a fat jar to run on different port (default is 8080), change the **server.port** following property:
```bash
server.port=8080
```

### Logging

Logging functionality is handled by the SLF4J logging facade and on top of the Logback framework. As a result, logging can be configured via the standard Logback configuration file through Spring boot.

```bash
logging.config=/path/to/logback.xml
```

By default, logging works on the INFO level and logs are directed to the system console. Additional logging appenders can be added (consult logback documentation for more details)

See the reference list of other common logging properties [provided by Spring boot](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)

### Updating TSL

| Property | Description |
| -------- | ----------- |
| **siva.tsl.loader.loadFromCache** | A boolean value that determines, whether the disk cache is used to load TLS <ul><li>Default: **false**</li></ul> |
| **siva.tsl.loader.url** | A url value that points to the external TSL <ul><li>Default: **https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl-mp.xml**</li></ul> |
| **siva.tsl.loader.code** | Sets the LOTL code in DSS <ul><li>Default: **EU**</li></ul> |
| **siva.tsl.loader.schedulerCron** | A string in a [Crontab expression format](http://www.manpagez.com/man/5/crontab/) that defines the interval at which the TSL renewal process is started. The default is 03:00 every day (local time) <ul><li>Default: **0 0 3 * * ?**</li></ul> |
| **siva.keystore.type** | Keystore type. Keystore that contains public keys to verify the signed TSL <ul><li>Default: **JKS**</li></ul> |
| **siva.keystore.filename** | Keystore filename. Keystore that contains public keys to verify the signed TSL <ul><li>Default: **siva-keystore.jks**</li></ul> |
| **siva.keystore.password** | Keystore password. Keystore that contains public keys to verify the signed TSL <ul><li>Default: **siva-keystore-password**</li></ul> |

### Forward to custom X-road webapp instance
| Property | Description |
| ------ | ----------- |
| **siva.proxy.xroadUrl** | A URL where the X-Road validation requests are forwarded <ul><li>Default: **http://localhost:8081**</li></ul>|

### Collecting statistics with Google Analytics
| Property | Description |
| -------- | ----------- |
| **siva.statistics.google-analytics.enabled** | Enables/disables the service <ul><li>Default: **false**</li></ul> |
| **siva.statistics.google-analytics.url** | Statistics endpoint URL <ul><li>Default: **http://www.google-analytics.com/batch**</li></ul> |
| **siva.statistics.google-analytics.trackingId** | The Google Analytics tracking ID <ul><li>Default: **UA-83206619-1**</li></ul> |
| **siva.statistics.google-analytics.dataSourceName** | Descriptive text of the system <ul><li>Default: **SiVa**</li></ul> |

### BDOC validation
| Property | Description |
| -------- | ----------- |
| **siva.bdoc.digidoc4JConfigurationFile** | Path to Digidoc4j configuration override <ul><li>Default: **N/A**</li></ul> |
| **siva.bdoc.signaturePolicy.defaultPolicy** | <ul><li>Default: **policy_name**</li></ul> |
| **siva.bdoc.signaturePolicy.policies.pol_v1** | <ul><li>Default: **/bdoc_constraint_no_type.xml**</li></ul> |
| **siva.bdoc.signaturePolicy.policies.pol_v2** | <ul><li>Default: **/bdoc_constraint_qes.xml**</li></ul> |

### PadES validation
| Property | Description |
| -------- | ----------- |
|**siva.pdf.signaturePolicy.defaultPolicy**| <ul><li>Default: **pol_v1**</li></ul>|
|**siva.pdf.signaturePolicy.policies.pol_v1**| <ul><li>Default: **/pdf_constraint_no_type.xml**</li></ul>|
|**siva.pdf.signaturePolicy.policies.pol_v2**| <ul><li>Default: **/pdf_constraint_qes.xml**</li></ul>|

### X-road validation
| Property | Description |
| -------- | ----------- |
|**siva.xroad.validation.service.configurationDirectoryPath**| Directory that contains the certs of approved CA's, TSA's and list of members <ul><li>Default: **/verificationconf**</li></ul> |
> **NOTE** Currently supports only POL_V1 as a default policy

### DDOC validation
| Property | Description |
| -------- | ----------- |
|**siva.ddoc.jdigidocConfigurationFile**| Path to JDigidoc configuration file. Determines the Jdigidoc configuration parameters (see [JDigidoc manual](https://github.com/open-eid/jdigidoc/blob/master/doc/SK-JDD-PRG-GUIDE.pdf) for details.<ul><li>Default: **/siva-jdigidoc.cfg**</li></ul>|



