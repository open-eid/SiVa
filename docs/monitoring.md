Starting and configuring monitoring Service
-------------------------------------------

PDF Validator monitoring is a simple web service that allows one to
check if the validation service is running correctly. This monitoring
service tries to periodically validate a simple PDF document using the
validator, and exposes the validation results via a JSON API. This JSON
API also includes information about TSL loading error messages that
occurred on the validation server (if any) and shows when the TSLs
(and/or their signing certificates) are about to expire. The simplest
way to run this service is to issue a command shown below.

**Run PDF validator monitoring Service**

```bash
java -jar pdf-validator-monitoring-1.0.1.RC1.jar
```

Default parameters for monitoring application are:

```
server.port=9000
monitoring.host=http://localhost:8080
monitoring.path=/pdf-validator-webapp/wService/validationService
```

**Default monitoring interval is 5 min**
```
monitoring.requestInterval=300000
```

You can override these properties by placing application.properties file
in same directory where the `pdf-validator-monitoring-1.0.1.RC1.jar` is
located.

Monitoring Service response values
----------------------------------

To see the last monitoring request result You can navigate to URL with
browser where the Service is running
i.e <http://localhost:9000/> default port for the Service is **9000**.

Otherwise if You can not use browser then issue following command with
curl:

```bash
curl -s http://localhost:9000 | python -m json.tool
```

**Sample response of PDF Validator Monitoring Service**

```json
{
	"lastChecked": "2015-09-30 11:33:49 AM GMT",
	"serviceStatus": "OK",
	"statusMessage": "PDF validator service running correctly",
	"tslStatusResponse": {
		"serviceStatus": "WARNING",
		"updateStartTime": "2015-09-30 09:59:07 AM GMT",
		"updateEndTime": "2015-09-30 09:59:33 AM GMT",
		"updateMessage": "Expiration WARNING(s) for: TSL has expired for http://example.com/tsl.xml at: 2015-07-31 10:00:00 AM EEST.",
		"tslCertificateInfo": {
			"https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl-mp.xml": {
				"loadingDiagnosticInfo": "Loaded Wed Sep 30 12:59:08 EEST 2015",
				"signerCertificateValidUntil": "2018-12-19 08:42:39 AM GMT",
				"tslValidUntil": "2016-01-14 11:00:00 AM GMT"
			},
			...
		},
	}
}
```

Possible ServiceStatus values are: **OK**, **WARNING**, **CRITICAL**

Meaning of each of these statuses are:

- **OK** - Validation of sample PDF file went without problems.
  Service is running correctly
- **WARNING** - There was issue validating PDF file but Service
  is running. Example when WARNING is thrown when loading TSL files
  into cache
- **CRITICAL** - Service is not running and is not responding to requests

TSL update status report
------------------------

Service status also provides information about current state of loaded
list of TSL certificates. Following information is provided.

- **ServiceStatus **- possible values are OK, WARNING, CRITICAL see
   explanations below
- **updateStartTime **- date and time when the TSL update started or
  **NULL** if not started
- **updateEndTime **- date and time when TSL update finished
  successfully otherwise **NULL** if not yet finished or failed with error
- **updateMessage **- More detailed explanation why Service status is
  i.e WARNING
- **tslCertificateInfo** - collection of all the countries TSLs that
  have been loaded into PDF Validator
- **loadingDiagnosticInfo** - Additional information when loading or
  validating the TSL failed or loading time if no error was not
  encountered
- **signerCertificateValidUntil** - TSL signing certificate expiration
  date
- **tslValidUntil** - TSL expiration date

Explanation of TSL update Service status values:

- **OK** - TSL certificate list has successfully completed update all
  is working correctly
- **WARNING** - TSL update has not yet started or is currently in
  progress
- **CRITICAL** - TSL update status information could not be queried
  from PDF Validator Service  


