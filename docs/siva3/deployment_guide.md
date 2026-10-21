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

# Building

## Building SiVa webapps

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


# Deploying

## OPTION 1 - starting webapps from command line
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


## OPTION 2 - running webapps as systemd services

Maven build generates executable JAR files. This means web container and all its dependencies are package inside
single JAR file. It makes a lot easier to deploy it into servers.

Easiest option to setup SiVa is as `systemd` service in Ubuntu servers.

For that we first need to create service file:
```bash
vim siva-webapp.service
```

Inside it, we need to paste below text. You need to change few things in service setup file.

* First you **must not** run service as `root`. So it's strongly recommended to change line `User=root`
* Second You can change Java JVM options by modifying the `JAVA_OPTS` inside the `siva-webapp.service` file.
* Also, You can change the SiVa application configuration options by modifying `RUN_ARGS` section in file

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

## OPTION 3  - deploy webapps as war files (Tomcat setup for legacy systems)

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

> **IMPORTANT** siva-webapp on startup creates `etc` directory where it copies the TSL validation certificates
> `siva-keystore.jks`. Default location for this directory is application root or `$CATALINA_HOME`. To change
> this default behavior you should set environment variable `DSS_DATA_FOLDER`. 

> **IMPORTANT** When updating the siva-keystore.jks it is needed to delete the "temp" keystore from default/specified location. 
> Deleting the "temp" keystore is also needed when upgrading your deployment to newer SIVA version!

## How-to set WAR deployed SiVa `application.properties`

SiVa override properties can be set using `application.properties` file. The file can locate anywhere in the host system.
To make properties file accessible for SiVa you need to create or edit `setenv.sh` placed inside `bin` directory.

Contents of the `setenv.sh` file should look like:

```bash
export CATALINA_OPTS="-Dspring.config.location=file:/path/to/application.properties"
```


## OPTION 4 - Kubernetes (Helm)

A reference Helm chart ships with the sources in `helm/siva-webapp/`. Like the docker-compose
file it is a reference, not a product: build the image yourself, override what your cluster
needs, no support promise. Its README (`helm/siva-webapp/README.md`) documents every value.

1. Build the image with the Spring Boot Maven plugin (Paketo buildpacks) and push it to a
   registry your cluster can pull from:

   ```bash
   ./mvnw spring-boot:build-image -pl siva-parent/siva-webapp -DskipTests -Dspring-boot.build-image.imageName=<registry>/siva-webapp:<version>
   docker push <registry>/siva-webapp:<version>
   ```

2. Install, pointing the chart at the image:

   ```bash
   helm install siva ./helm/siva-webapp --namespace siva --create-namespace \
     --set image.repository=<registry>/siva-webapp --set-string image.tag=<version>
   kubectl -n siva rollout status deployment/siva-siva-webapp --timeout=15m
   ```

   The first start downloads the EU trust lists, which takes minutes; the startup probe budget
   covers it. With `tsl.mode: snapshot` a CronJob downloads them instead and the pods load the
   published snapshot in seconds (see [Trust-list snapshots](#trust-list-snapshots) and the
   chart README). Pods run under the Pod Security "restricted" profile with a read-only root
   filesystem. `helm install` prints how to reach the service for the chosen Service type
   (in-cluster name, NodePort or LoadBalancer); the smoke tests below then apply to that
   address instead of `localhost:8080`.

3. Configure through values: `config.spring` takes the `siva.*` keys from this guide in
   application.yml syntax (nested, kebab-case) as a Spring configuration override; the chart's
   values schema rejects a key SiVa or Spring Boot does not declare. `config.files` mounts further
   files beside it (a Logback configuration, a digidoc4j override), `env` takes environment
   variables (relaxed binding), `secrets.existingSecret` mounts a Secret whose keys are read as
   properties (passwords, the signing keystore), and `extraVolumes`, `extraVolumeMounts` and
   `extraContainers` pass pod spec through for what the chart does not model (a sync sidecar for
   a snapshot bucket, for example). Ingress objects come from
   `network.ingresses` (set your controller's body-size limit to at least 27 MB). Health with
   details, heartbeat, version and Prometheus metrics are served on the management port (8081)
   through the in-cluster Service `<release>-management`, not through the traffic port or the
   Ingress; the traffic port answers only the status-only probes `/readyz` and `/livez`, which
   the kubelet and an external load balancer's health check use.

Optional objects, all off by default: HorizontalPodAutoscaler, NetworkPolicy, ServiceMonitor,
PrometheusRule and a Grafana dashboard (`scaling.*`, `network.networkPolicy.*`,
`monitoring.*`), and in snapshot mode the trust-list fetch CronJob, its bootstrap Job and the
store claim (`tsl.*`). A PodDisruptionBudget is created once there is more than one replica.
The trusted and mandatory countries are chart values (`tsl.territories.*`), so SiVa and the
PrometheusRule use the same list; in snapshot mode `tsl.fetcher.period` sets the fetch schedule,
the snapshot age limit and the alert thresholds together (see
[Choosing the fetch period](#choosing-the-fetch-period)).

### Test instance

A test instance (SiVa-Test, the demo application) needs what the docker-compose file gives the
webapp: SiVa's `test` Spring profile (the open-eid test trust list and truststore shipped in the
jar), `TZ=Europe/Tallinn` and the Estonian T-level signature filter, and the monitoring
endpoints on the API port because the system tests address both through one host:port. The
chart's `values-kind.yaml` is that profile for a local kind cluster:

```bash
helm install siva ./helm/siva-webapp -f helm/siva-webapp/values-kind.yaml \
  --set image.repository=<registry>/siva-webapp --set-string image.tag=<version>
```

Reach it at `<kind node>:30080`. A number of SiVa-Test specifications need a trust list that
mixes production and test anchors and is not published; they fail against this instance and
against a production one alike, which is expected.

## Smoke testing your deployed system

**Step 1**. Install HTTPIE
`httpie` is more user-friendly version of `curl` and we will use to verify that SiVa was installed
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

# Logging

By default, logging works on the INFO level and logs are directed to the system console only. Logging functionality is handled by the SLF4J logging facade and on top of the Logback framework. As a result, logging can be configured via the standard Logback configuration file through Spring boot. Additional logging appenders can be added. Consult [logback documentation](http://logback.qos.ch/documentation.html) for more details on log file structure.

For example, adding application.properties to classpath with the **logging.config** property
```bash
logging.config=/path/to/logback.xml
```

# Statistics

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


# Monitoring

SiVa webapps provide endpoints for external monitoring tools to periodically check the generic service health status.

## Health Endpoint

!!! note
    Note that this endpoint is exposed by default.

The url for accessing JSON formatted health information with HTTP GET is `/monitoring/health` . See the [Interfaces section](../siva3/interfaces.md#service-health-monitoring) for response structure and details.

* **Exposing the health monitoring endpoint**

This endpoint is controlled by the following configuration parameter:
```bash
management.endpoints.web.exposure.include=health
```

* **External service health indicators**

The endpoint is implemented as a customized Spring boot [health endpoint](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html#production-ready-health), which allows to add custom health indicators.

Demo webapp and Siva webapp include additional information about the health of their dependent services.
These links to dependent web services have been preconfigured. For example, the Demo webapp is preset to check whether the Siva webapp is accessible from the following url (parameter `siva.service.serviceHost` value)/monitoring/health.

* **Trust lists**

The `tsl` component reports, per trusted-list loader, the number of trust anchors, when the last refresh finished, the anchors per territory and, after an online refresh that had to serve a list from its last copy, `lastCopy` (territory, `EU` for the list of trusted lists, to the reason the download failed) (what the certificate source holds after DSS synchronized the lists it accepts, so a list whose re-download failed keeps counting as long as DSS keeps its anchors). It is DOWN until each loader has refreshed once and holds every mandatory territory (`siva.tsl.loader.mandatoryTerritories`, `EE` by default) with at least one anchor, naming the missing ones in `missingMandatoryTerritories`; other territories never decide the status, so one member state's list being unreachable does not take the service out of rotation. In snapshot mode (see [Trust-list snapshots](#trust-list-snapshots)) each loader also reports the `snapshot` id it loaded. The overall health and the heartbeat follow it. It is part of the `readiness` probe group (`/monitoring/health/readiness`), so a Kubernetes pod in that state receives no traffic.

* **Probe paths on the application port**

When the actuator runs on its own port (`management.server.port`), the `liveness` and `readiness` groups are also served on the application port as `/livez` and `/readyz` (`management.endpoint.health.probes.add-additional-paths`, on by default). They return the status only, nothing else, which makes them the right target for a load balancer's health check and for Kubernetes probes. Without a separate management port the groups stay under `/monitoring/health/liveness` and `/monitoring/health/readiness`.

## Trust-list metrics

The Prometheus endpoint (`/monitoring/prometheus`) carries the trust-list state of each loader (`generic`, `timemark`), refreshed whenever the loader reloads its lists:

| Metric | Labels | Meaning |
| ------ | ------ | ------- |
| `siva_tsl_trust_anchors` | `loader`, `territory`, `mandatory` (`true`, `false`) | Trust anchors the loader holds for that country after DSS accepted its list, for every configured country (`siva.tsl.loader.trustedTerritories` and `mandatoryTerritories`). Zero means signatures from that country cannot be validated as trusted. |
| `siva_tsl_next_update_timestamp_seconds` | `loader`, `kind` (`lotl`, `tl`), `territory` | The list's `NextUpdate` as Unix time: after it, the list counts as expired. For the LOTL every country depends on it. |
| `siva_tsl_snapshot_created_timestamp_seconds` | `loader` | Snapshot mode only: when the loaded snapshot was published (`manifest.json` `createdAt`), so its age as the instance sees it, after any copy between the fetcher and the instance. |
| `siva_tsl_list_downloaded_timestamp_seconds` | `loader`, `kind` (`lotl`, `tl`), `territory` | When the list was last really downloaded from its publisher (online: the cache file's modification time; snapshot mode: the manifest's `downloadedAt`). A list served from its last copy after a failed download keeps the earlier time, so the age of this value is how long the publisher has been out of reach. Not a number for a plain cache directory or a snapshot published by an older fetcher. |
| `siva_tsl_refresh_period_seconds` | `loader` | The refresh period the instance runs on: `siva.tsl.loader.refreshPeriod` online, the fetcher's `siva.tsl.fetcher.period` from the manifest in snapshot mode. The thresholds below multiply it, so the rules repeat no period either. Not a number while unknown. |

Because the series exist exactly for the configured countries and say which are mandatory, alerts need no list of their own: zero anchors with `mandatory="true"` is critical, with `mandatory="false"` a warning. Thresholds for list expiry, a list served from its last copy and snapshot age derive from the period the instance reports, as described under [Choosing the fetch period](#choosing-the-fetch-period); the Helm chart's PrometheusRule does exactly that, joining each threshold to the list series by instance and loader, so the same rules hold for a plain-cron deployment that sets the period properties.

## Heartbeat endpoint

!!! note
    Note that this endpoint is enabled and exposed by default.

!!! note
    Note that this endpoint requires the health endpoint to be enabled and exposed in order to function.

The url for accessing JSON formatted heartbeat information with HTTP GET is `/monitoring/heartbeat`. See the [Interfaces section](../siva3/interfaces.md#simplified-health-monitoring) for response structure and details.

* **Enabling and exposing the heartbeat monitoring endpoint**

This endpoint is controlled by the following configuration parameters:
```bash
management.endpoints.web.exposure.include=health,heartbeat
management.endpoint.heartbeat.enabled=true
```

* **Simplified service health indicator**

The endpoint is implemented by polling the health information directly from the underlying health endpoint implementation, but exposing just the aggregated overall service status, hiding everything else.

## Version information endpoint

!!! note
    Note that this endpoint is enabled and exposed by default.

The url for accessing JSON formatted version information with HTTP GET is `/monitoring/version`. See the [Interfaces section](../siva3/interfaces.md#version-information) for response structure and details.

* **Enabling and exposing the version information endpoint**

This endpoint is controlled by the following configuration parameters:
```bash
management.endpoints.web.exposure.include=version
management.endpoint.version.enabled=true
```

## Prometheus metrics endpoint

!!! note
    Note that this endpoint is exposed by default.

The url for accessing Prometheus metrics with HTTP GET is `/monitoring/prometheus`. 
See the [Interfaces section](../siva3/interfaces.md#prometheus-metrics) for response structure and details.

* **Exposing the Prometheus metrics endpoint**

This endpoint is controlled by the following configuration parameter:
```bash
management.endpoints.web.exposure.include=prometheus
```

# Validation Report Signature

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

|Property | Description |
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
# Configuration parameters

All SiVa webapps have been designed to run with predetermined defaults after building and without additional configuration.
However, all the properties can be overridden on the service or embedded web server level, if necessary.

By default, the service loads it's global configuration from the `application.yml` file that is packaged inside the jar file.
Default configuration parameters can be overridden by providing custom `application.yml` in the [following locations](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files), or using command line parameters or by using other [externalized configuration methods](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) methods.

For example, to configure the embedded Tomcat web server inside a fat jar to run on different port (default is 8080), change the **server.port** following property:
```bash
server.port=8080
```

Or to increase or modify the default http request limit, override the **siva.http.request.max-request-size-limit** property:
```bash
siva.http.request.max-request-size-limit: 15MB
```

See the reference list of all common [application properties](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html) provided by Spring boot

Since 3.11.0 a key under a `siva.*` prefix that SiVa does not declare fails the start, naming the
key (`The elements [siva.tsl.loader.trusted-territoriez[0]] were left unbound`), so a typo cannot
pass silently as a default; `siva.bdoc.*` is the exception. Spring's relaxed binding still
accepts the kebab-case (`trusted-territories`), camelCase (`trustedTerritories`) and
environment-variable (`SIVA_TSL_LOADER_TRUSTEDTERRITORIES`) spellings of every key, and the
build ships the keys as Spring configuration metadata, so IDEs complete them.

## Siva webapp parameters

### Updating TSL

| Property | Description |
| -------- | ----------- |
| **siva.tsl.loader.loadFromCache** | A boolean value that determines whether the TSL is loaded from disk cache instead of by downloading a new TSL in a predetermined interval.<br/><br/>Note that the cache files are by default stored in a system temporary folder (can be set with system property `java.io.tmpdir`; make sure the application has both read and write permissions in that folder).<ul><li>When set to **false**, the cache is refreshed periodically by SiVa in a predetermined interval specified by `siva.tsl.loader.schedulerCron` using `siva.tsl.loader.url`.</li><li>When set to **true**, SiVa uses existing cache as it's TSL. No direct polling for updates is performed, so all the files (LOTL and TSL files) must be present in system temporary directory. The cache files must have the same filenames as their URLs, but special characters need to be replaced with underscores. For example, the file downloaded from https://sr.riik.ee/tsl/estonian-tsl.xml must be named `https___sr_riik_ee_tsl_estonian_tsl_xml`</li><li>Default: **false**</li></ul> |
| **siva.tsl.loader.cacheDirectory** | Directory of the disk cache the trusted lists are downloaded into, or read from with `loadFromCache`. <ul><li>Default: the JVM's temporary directory (`java.io.tmpdir`), as before</li></ul> |
| **siva.tsl.loader.snapshotDirectory** | With `loadFromCache=true`: root of a snapshot store written by the trust-list fetcher (see [Trust-list snapshots](#trust-list-snapshots)). The file `current` in it names the snapshot under `snapshots/` to load; the loader reads that snapshot instead of `cacheDirectory`, checks `current` every `snapshotPollInterval` and loads the new snapshot when it changes. The `tsl` health component then shows the snapshot id per loader. <ul><li>Default: not set (plain cache directory)</li></ul> |
| **siva.tsl.loader.snapshotMaxAge** | ISO-8601 duration. In snapshot mode, a snapshot whose `manifest.json` is older than this (or missing) is refused and the loaded state is kept, so a store that stopped being refreshed, or was pointed at an old snapshot by whoever can write it, cannot feed a fresh instance lists older than the operator allows; an instance with nothing loaded stays not ready. <ul><li>Default: not set (no age limit)</li></ul> |
| **siva.tsl.loader.snapshotPollInterval** | ISO-8601 duration between checks of `current` in snapshot mode; the scheduled refresh (`schedulerCron`) stays as the safety net. Zero disables the checks. <ul><li>Default: **PT1M**</li></ul> |
| **siva.tsl.loader.onlineCacheExpirationTime** | A string value in a [format based on ISO-8601 duration format PnDTnHnMn.nS](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Duration.html#parse(java.lang.CharSequence)) that determines the expiration time of TSL disk cache in case `siva.tsl.loader.loadFromCache` is set to `false`. The default is 1 hour.<br/><br/>Note that the expiration time only determines, for each cached file, the minimum time that must have been passed since their last update before that file is considered expired and is susceptible to an update. The actual update is performed periodically by SiVa (specified by `siva.tsl.loader.schedulerCron`) or when the application is (re)started. <ul><li>Default: **PT1H**</li></ul> |
| **siva.tsl.loader.url** | A url value that points to the external TSL <ul><li>Default: **https://ec.europa.eu/tools/lotl/eu-lotl.xml**</li></ul> |
| **siva.tsl.loader.ojUrl** | A url value that points to the legal act in Official Journal of the European Union <ul><li>Default: **https://eur-lex.europa.eu/eli/C/2026/1944/oj**</li></ul> |
| **siva.tsl.loader.lotlRootSchemeInfoUri** | A url value that points to the European Unions' disclaimer regarding LOTL <ul><li>Default: **https://ec.europa.eu/tools/lotl/eu-lotl-legalnotice.html**</li></ul> |
| **siva.tsl.loader.code** | Sets the LOTL code in DSS <ul><li>Default: **EU**</li></ul> |
| **siva.tsl.loader.trustedTerritories** | Sets the trusted territories by countries <ul><li>Default: **"AT", "BE", "BG", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "EL", "HU", "HR", "IE", "IS", "IT", "LT", "LU", "LV", "LI", "MT", "NO", "NL", "PL", "PT", "RO", "SE", "SI", "SK", "UK"**</li><li>If the property is not specified or has an empty value, the default trusted territories will be used</li><li>Values are case-sensitive. Country codes must be specified in uppercase format</li></ul> |
| **siva.tsl.loader.mandatoryTerritories** | Territories (same codes as above) whose trusted list must be loaded with at least one trust anchor before the service reports itself ready (the `tsl` health component, see [Health Endpoint](#health-endpoint)). Any other trusted territory may be missing or stale without affecting readiness. Codes are case-sensitive and must be among `siva.tsl.loader.trustedTerritories` when that list is set (the loader logs a warning otherwise). <ul><li>Default: **"EE"** (the `test` profile uses **"EE_T"**, the scheme territory of the test trusted list)</li><li>Empty: any loaded trusted list is enough</li></ul> |
| **siva.tsl.loader.schedulerCron** | A string in a [Crontab expression format](http://www.manpagez.com/man/5/crontab/) that defines the interval at which the TSL renewal process is started. The default is 03:00 every day (local time) <ul><li>Default: **0 0 3 \* * ?**</li></ul> |
| **siva.tsl.loader.refreshPeriod** | ISO-8601 duration. In online mode, how often the lists are refreshed as `schedulerCron` means it, published as `siva_tsl_refresh_period_seconds` so that staleness alerts take the period from the instance; keep it in step with `schedulerCron`. Unused in snapshot mode, where the snapshot's manifest carries the fetcher's period. <ul><li>Default: **P1D**</li></ul> |
| **siva.tsl.loader.sslTruststorePath** | Path to truststore containing trusted CA certificates used in HTTPS connection to retrieve member states TSLs. Requires *classpath:* prefix to load truststore from application classpath or *file:* prefix to load truststore from file system. <ul><li>Default: **classpath:tsl-ssl-truststore.p12**</li></ul> |
| **siva.tsl.loader.sslBundle** | Name of a [Spring SSL bundle](https://docs.spring.io/spring-boot/reference/features/ssl.html) (`spring.ssl.bundle.pem.<name>.truststore.certificate=file:...`) whose trust store is used instead of `sslTruststorePath` for the HTTPS connections to the trust-list hosts: PEM files, no password, and with `spring.ssl.bundle.pem.<name>.reload-on-update=true` a changed file is picked up by the next download without a restart. Like `sslTruststorePath` it replaces the default trust, so the bundle must hold every CA the list hosts chain to. <ul><li>Default: not set (`sslTruststorePath` is used)</li></ul> |
| **siva.tsl.loader.sslTruststoreType** | Truststore type <ul><li>Default: **PKCS12**</li></ul> |
| **siva.tsl.loader.sslTruststorePassword** | Truststore password <ul><li>Default: **digidoc4j-password**</li></ul>  |
| **siva.tsl.loader.LotlPivotSupportEnabled** | A boolean value that determines, whether LOTL pivot mode should be used or not <ul><li>Default: **true**</li></ul> |
| **siva.tsl.loader.lotlTruststoreType** | Truststore type <ul><li>Default: **PKCS12**</li></ul> |
| **siva.tsl.loader.lotlTruststorePath** | Truststore that contains public keys of trusted LOTL signers for LOTL signature validation. Requires *classpath:* prefix to load truststore from application classpath or *file:* prefix to load truststore from file system.<ul><li>Default: **classpath:lotl-truststore.p12**</li></ul> |
| **siva.tsl.loader.lotlTruststorePassword** | Truststore password <ul><li>Default: **lotl-truststore-password**</li></ul> |
| **siva.tsl.loader.lotlBundle** | Name of a Spring SSL bundle whose trust store holds the LOTL signer certificates, used instead of `lotlTruststorePath`; the certificates are read again before every refresh, so with `reload-on-update` a changed PEM file counts from the next refresh on. <ul><li>Default: not set (`lotlTruststorePath` is used)</li></ul> |

What a refresh does (since 3.11.0): every refresh, the scheduled one and the one a new snapshot triggers, parses
the lists into a fresh in-memory trust source and swaps it in when it is complete. Requests resolve the current
source per call, so a validation never reads a source that is being refilled; a changed list takes effect for
all requests at the swap. Earlier releases refilled the one source in place, and a request in flight during a
refresh that changed a list could miss trust anchors (INDETERMINATE or TOTAL-FAILED for a valid signature) or fail
with an internal error (open-eid/SiVa#6). Because every refresh parses all lists again, it takes seconds for the EU
LOTL set, on the scheduler thread, with requests served from the previous source meanwhile; two sources exist in
memory for that time.

!!! note
    When updating the siva-keystore.jks it is needed to delete the "temp" keystore from default/specified location. Deleting the "temp" keystore is also needed when upgrading your deployment to newer SIVA version!

!!! note
    TSL is currently used only by Generic and BDOC validators


### Trust-list snapshots

Instead of every instance downloading the trusted lists itself, one process can download them and publish a snapshot that all instances load from disk: a Kubernetes CronJob with several replicas, or a cron entry next to a systemd service. The fetcher is the webapp itself in the `tsl-fetch` Spring profile: it starts without a listening port, refreshes the trusted lists online into `siva.tsl.loader.cacheDirectory` (keep that directory between runs: a list whose download fails keeps its last copy there), publishes the cache as `snapshots/<id>/` with a `manifest.json` under `siva.tsl.fetcher.outputDirectory`, points the file `current` at it and exits. When the lists end up below the readiness threshold (`siva.tsl.loader.mandatoryTerritories`), nothing is published, `current` keeps naming the last good snapshot and the exit code is non-zero. The exit code is the run's result for whatever schedules it: 0 every list downloaded and published; 1 below the threshold, nothing published; 2 the store could not be written; 3 published, but one or more lists came from their last copy because the download failed (the log's `using the last copy on disk` line names the list and the reason; the `tsl` health and the manifest list them under `lastCopy`). A copy is good until the list's own `NextUpdate`, but a fetch that did not fetch should fail as a job, so that the job's own monitoring sees it.

```bash
java -jar siva-webapp-X.Y.Z-exec.jar --spring.profiles.include=tsl-fetch \
  --siva.tsl.loader.cacheDirectory=/var/siva/tsl/work \
  --siva.tsl.loader.onlineCacheExpirationTime=PT0S \
  --siva.tsl.fetcher.outputDirectory=/var/siva/tsl \
  --siva.tsl.fetcher.period=PT6H
```

Set `onlineCacheExpirationTime` to zero for the fetcher: a fetch is for downloading, and with the default of one hour a run that starts within an hour of the previous one would take the cached files without trying the publishers, which also leaves the download times it records an hour behind. `period` is what you scheduled the cron at.

The instances run with `siva.tsl.loader.loadFromCache=true` and `siva.tsl.loader.snapshotDirectory=/var/siva/tsl` (read-only is enough), load the snapshot `current` names in seconds and follow `current` when it changes. Whoever can write the store can point the instances at any snapshot whose lists are still within their validity, so give write access to the fetcher only, mount it read-only everywhere else and set `siva.tsl.loader.snapshotMaxAge` to a few fetch intervals. The fetcher and the instances must share the rest of the `siva.tsl.loader.*` settings (profile, `url`, `trustedTerritories`), or the instances look for lists the fetcher never downloaded. The manifest records the fetcher's `tsl` health at publication: per loader the anchor count, the territories, any missing mandatory ones and any list served from its last copy, exactly as an instance reports them after loading the same files; plus `period` (`siva.tsl.fetcher.period`, how often the fetcher runs) and `downloadedAt` (per loader and territory, `EU` for the list of trusted lists, when each list was last really downloaded: the working cache file's modification time, which a copy served in place of a failed download leaves as it was). The instances publish both as metrics, so staleness alerts can be sized from the instance (see [Trust-list metrics](#trust-list-metrics)).

| Property | Description |
| -------- | ----------- |
| **siva.tsl.fetcher.outputDirectory** | Root of the snapshot store (`current`, `snapshots/<id>/`). Required in the `tsl-fetch` profile. |
| **siva.tsl.fetcher.keepSnapshots** | Snapshots kept besides the current one; older ones are deleted after each successful publish. <ul><li>Default: **3**</li></ul> |
| **siva.tsl.fetcher.period** | ISO-8601 duration: how often the fetcher is scheduled to run. Written into the manifest and published by the instances as `siva_tsl_refresh_period_seconds`, the period the trust-list alerts size themselves from. Set it to your cron's period. <ul><li>Default: none (the instances report no period)</li></ul> |

#### Choosing the fetch period

How often the fetcher runs is the one choice the other staleness settings follow. What finally ends validation is a list's own `NextUpdate`: the EU LOTL and most member-state lists are valid for about six months from issue, some for three, and a list is reissued only when its content changes, so a production list can be weeks from expiry at any time. Once a list's `NextUpdate` has passed, SiVa stops treating that country's signatures as qualified (under POLv4 they fail) and, at the next reload, drops its trust anchors altogether. The fetch period has to be short against that, and the alerts have to watch it.

Run the fetcher at least weekly; every six hours is the default. A monthly fetch is not supported: with lists running out within three months of issue it leaves too little time to notice and react. For a fetch period P, set the rest as follows (the Helm chart derives the schedule and the age limit from `tsl.fetcher.period` and passes it to the fetcher; with plain cron, set the schedule and `siva.tsl.loader.snapshotMaxAge` by hand and `siva.tsl.fetcher.period` to the cron's period). The alert thresholds are not settings: the instances publish the period as `siva_tsl_refresh_period_seconds` and the rules multiply it, so they follow a changed period on their own. A fetch run whose Job failed is the orchestrator's own alert (Kubernetes: a Failed Job; cron: a non-zero exit), see the exit codes above.

| Setting | Rule | P = 6 h | P = 1 day | P = 7 days |
| ------- | ---- | ------- | --------- | ---------- |
| Fetch schedule | every P | `0 */6 * * *` | `0 3 * * *` | `0 3 * * 1` |
| `siva.tsl.loader.snapshotMaxAge` | max(4P, 48 h) | PT48H | P4D | P28D |
| Alert: a list served from its last copy (not downloaded) for | 2P + grace | 13 h | 49 h | 14 days 1 h |
| Alert: snapshot loaded by an instance older than | 2P + grace | 13 h | 49 h | 14 days 1 h |
| Warning: a configured country's list expires within | 2P + lead time | 7.5 days | 9 days | 21 days |
| Critical: a configured country's list expires within | P + 1 day | 1.25 days | 2 days | 8 days |
| Warning / critical: a configured country has no trust anchors | 15 minutes | | | |

Grace defaults to one hour; when a sync tool copies the store between the fetcher and the instances (for example through object storage), add its interval. Online mode has no fetcher: there P is `siva.tsl.loader.refreshPeriod` (one day by default, the nightly reload) and only the list rows apply. Lead time defaults to seven days, the time you give yourself to react before a list expires. `snapshotMaxAge` is lenient on purpose: it only stops instances from loading an old snapshot, so a tight value turns a late fetch into instances that never become ready; the alerts are what catch staleness early. The metrics behind the alerts are described under [Trust-list metrics](#trust-list-metrics).

### TimeMark validation - customizing policies

| Property | Description |
| -------- | ----------- |
| **siva.bdoc.digidoc4JConfigurationFile** | Path to Digidoc4j configuration override. Requires *classpath:* prefix to load configuration from application classpath or *file:* prefix to load configuration from file system. <ul><li>Default: **classpath:/siva-digidoc4j.yaml**</li></ul> |

| Property | Description |
| -------- | ----------- |
|**siva.bdoc.signaturePolicy.defaultPolicy**| Selected default policy name <ul><li>Default: **N/A**</li></ul>|
|**siva.bdoc.signaturePolicy.policies[index].name**| Policy name <ul><li>Default: **N/A**</li></ul>|
|**siva.bdoc.signaturePolicy.policies[index].description**| Policy description <ul><li>Default: **N/A**</li></ul>|
|**siva.bdoc.signaturePolicy.policies[index].constraintPath**| Constraint XML file path for the policy. Requires *classpath:* prefix to load constraint from application classpath or *file:* prefix to load constraint from file system.<ul><li>Default: **N/A**</li></ul>|
|**siva.bdoc.signaturePolicy.policies[index].url**| Policy URL <ul><li>Default: **N/A**</li></ul>|

By default, the following configuration is used
```text
siva.bdoc.signaturePolicy.policies[0].name=POLv3
siva.bdoc.signaturePolicy.policies[0].description=Policy for validating Electronic Signatures and Electronic Seals regardless of the legal type of the signature or seal (according to Regulation (EU) No 910/2014), i.e. the fact that the electronic signature or electronic seal is either Advanced electronic Signature (AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES) does not change the total validation result of the signature.
siva.bdoc.signaturePolicy.policies[0].url=http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv3
siva.bdoc.signaturePolicy.policies[0].constraintPath=classpath:bdoc_constraint_ades.xml

siva.bdoc.signaturePolicy.policies[1].name=POLv4
siva.bdoc.signaturePolicy.policies[1].description=Policy for validating Qualified Electronic Signatures and Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive validation result.
siva.bdoc.signaturePolicy.policies[1].url=http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
siva.bdoc.signaturePolicy.policies[1].constraintPath=classpath:bdoc_constraint_qes.xml
```

!!! note
    Default policy configuration is lost when policy detail properties (name, description, url or constraintPath) are overridden or new custom policies added in custom configuration files (in this case, the existing default policies must be redefined in configuration files explicitly)

### Generic validation - customize validation policies

| Property | Description |
| -------- | ----------- |
|**siva.europe.signaturePolicy.defaultPolicy**| Selected default policy name <ul><li>Default: **N/A**</li></ul>|
|**siva.europe.signaturePolicy.policies[index].name**| Policy name <ul><li>Default: **N/A**</li></ul>|
|**siva.europe.signaturePolicy.policies[index].description**| Policy description <ul><li>Default: **N/A**</li></ul>|
|**siva.europe.signaturePolicy.policies[index].constraintPath**| Constraint XML file path for the policy. Requires *classpath:* prefix to load constraint from application classpath or *file:* prefix to load constraint from file system.<ul><li>Default: **N/A**</li></ul>|
|**siva.europe.signaturePolicy.policies[index].url**| Policy URL <ul><li>Default: **N/A**</li></ul>|

By default, the following configuration is used
```text
siva.europe.signaturePolicy.policies[0].name=POLv3
siva.europe.signaturePolicy.policies[0].description=Policy for validating Electronic Signatures and Electronic Seals regardless of the legal type of the signature or seal (according to Regulation (EU) No 910/2014), i.e. the fact that the electronic signature or electronic seal is either Advanced electronic Signature (AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES) does not change the total validation result of the signature.
siva.europe.signaturePolicy.policies[0].url=http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv3
siva.europe.signaturePolicy.policies[0].constraintPath=classpath:generic_constraint_ades.xml

siva.europe.signaturePolicy.policies[1].name=POLv4
siva.europe.signaturePolicy.policies[1].description=Policy for validating Qualified Electronic Signatures and Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive validation result.
siva.europe.signaturePolicy.policies[1].url=http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
siva.europe.signaturePolicy.policies[1].constraintPath=classpath:generic_constraint_qes.xml
```

!!! note
    Default policy configuration is lost when policy detail properties (name, description, url or constraintPath) are overridden or new custom policies added in custom configuration files (in this case, the existing default policies must be redefined in configuration files explicitly)

### Configure SiVa to request revocation status for T level signatures

By default, T level signatures do not contain revocation data.
It is possible to configure SiVa to use OnlineOCSPSource in order to request revocation status during the validation process for T level signatures.

An example of configuring SiVa to request an OCSP response only for certificates issued by Latvian and Lithuanian service providers:
```text
t-level-signature-filter.filter-type = ALLOWED_COUNTRIES
t-level-signature-filter.countries[0] = LV
t-level-signature-filter.countries[1] = LT
```

| Property | Description |
| -------- |-------------|
|**t-level-signature-filter.filter-type**| A string value that determines, which filter is being used. There are two available options: ALLOWED_COUNTRIES or NOT_ALLOWED_COUNTRIES <ul><li>When set to **ALLOWED_COUNTRIES** SiVa uses OnlineOCSPSource to request revocation status for the list of provided countries. If the list of countries is left empty, no OCSP requests are made</li><li>When set to **NOT_ALLOWED_COUNTRIES** SiVa uses OnlineOCSPSource to request revocation status for all the countries that are not on the list. If the list of countries is left empty, it makes an OCSP request for every country</li><li>Default: **N/A**</li></ul> |
|**t-level-signature-filter.countries**| A list of countries to be provided to the filter. For example: EE, LV, BE <ul><li>Default: **N/A**</li></ul>|

!!! note
    If enabled, the revocation request is made for every signature level for given countries, not only T level signatures.

!!! note
    Revocation requests are only executed when using the Generic validator and are not applied to BDOC or DDOC validation,
    where revocation handling follows container-specific rules.
   
## Demo webapp parameters

* Linking to SiVa webapp

| Property | Description |
| -------- | ----------- |
|**siva.service.serviceHost**| An HTTP URL link to the Siva webapp <ul><li>Default: **http://localhost:8080**</li></ul> |
|**siva.service.jsonServicePath**| Service path in Siva webapp to access the REST/JSON API<ul><li>Default: **/validate**</li></ul> |
|**siva.service.jsonDataFilesServicePath**| Data file service path in Siva webapp to access the REST/JSON API<ul><li>Default: **/getDataFiles**</li></ul> |
|**siva.service.trustStore**| Path to Siva webapp truststore on classpath <ul><li>Default: **siva_server_truststore.p12**</li></ul> |
|**siva.service.trustStorePassword**| Siva webapp truststore password <ul><li>Default: **password**</li></ul> |
