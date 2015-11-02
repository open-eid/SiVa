# Deploying PDF Validation Web Service

### System requirements:

-  At least **2GB of RAM** on machine where the build is executed
-  Minimum required Java version is **Java 7u80** but Java 8 is
   recommended
-  **Maven 3** to build project manually. (Any minor version update to
   Maven 3 should work.)
-  **Tomcat 7** to run PDF validation web service. (Any minor version
   update to should work.)

> **NOTE**: There are known PowerMock issues when building test JAR files
> with Java 7. Therefore, it is required to use the latest Java 7 update.
> Currently Java SDK version **1.7.0\_80**.

Building project manually
-------------------------

The easiest way to get all required files is to just download the ZIP
file with all the required apps and services. But if You prefer to build
the project manually, then just issue following Maven command:

### Basic build process

If `git`, `mvn` and `javac` have been installed correctly then You can just issue these commands:

```bash
git clone https://github.com/open-eid/pdf-validator.git --recursive
cd pdf-validator
mvn clean install -Dmaven.test.skip.exec -DargLine="-Xmx512m"
```

Next step is to deploy the build artifacts to Tomcat.


### Build process on Ubuntu 15.04

First we need to install all required software dependencies for that we need 
to issue following commands:

```bash
sudo apt-get update
sudo apt-get install -y git 
sudo apt-get install -y openjdk-7-jdk
sudo apt-get install -y maven
sudo apt-get install -y tomcat7
```

> **NOTE**: Recommended way of using Tomcat is to download it directly from Apache Tomcat homepage.
> Reason for that is to get more platform independent deployment process.

Next we need to clone the `pdf-validator` project I chose `$HOME` directory

```bash
cd $HOME
git clone https://github.com/open-eid/pdf-validator.git --recursive
cd pdf-validator
```

Now are ready to build the project by issuing following Maven command

```bash
mvn clean install -Dmaven.test.skip.exec -DargLine="-Xmx512m"
```

We can skip test execution when building because most of the tests are integration tests
and may fail in some cases.

> **NOTE**: The computer where maven install command is issued must be
> open to the internet. Maven pulls dependencies from several different
> repositories.

> **NOTE:** Joinup Maven repository (http://joinup.ec.europa.eu/site) is
> very slow sometimes, so please be patient when building project the
> first time.

Installing the PDF Validation on Ubuntu 15.04
--------------------------------------------

After build has completed successfully we can continue installing the build artifacts
into our Tomcat web container. 

First make sure You have installed the Tomcat.

```bash
sudo apt-get update && sudo apt-get install -y tomcat7
```

First we need to create directory where our certificate Java keystore reside in by issuing following 
commands:

```bash
cd /tmp
sudo mkdir cert-store
sudo chown tomcat7:root
sudo chmod 755 cert-store
```

Now we need to configure some environment variables to make PDF Validation work correctly.
Make `bin` directory in `$CATALINA_HOME` and add `setenv.sh` file into it.

> **NOTE:** Paths below are based on default paths of package manger 
installed version of **Tomcat 7 of Ubuntu 15.04**

```bash
cd /var/lib/tomcat7
sudo mkdir bin 
sudo touch bin/setenv.sh 
```

Add following content into this file.

```
export DSS_DATA_FOLDER=/tmp/cert-store
```

Next we need to install the build artifacts into `webapps` directory:

```bash
cp $HOME/pdf-validator/pdf-validator-parent/pdf-validator-webapp/target/pdf-validator-webapp-*.war /var/lib/tomcat7/webapps
```

> **NOTE** It would be good to rename the `war` file to make more usable URL otherwise the version number will be visible in 
> URL path

Now we can start the Tomcat service:

```bash
sudo service tomcat7 restart
```

Installing the PDF Validator webapp with downloaded Apache Tomcat for development 
---------------------------------------------------------------------------------

1.  Navigate the directory that holds the ZIP file (if you built the ZIP
    file using Maven, navigate to
    directory `PROJECT_BASE_DIRECTORY/pdf-validator-distribution/target`).
2.  Copy `pdf-validator-distribution-1.0.1.RC1-distribution.zip` file
    outside `PROJECT_BASE_DIRECTORY`. ZIP file contents will look like
    the following:

	    pdf-validator-distribution-1.0.1.RC1-distribution\
	    └── bin
	    ├── pdf-validator-monitoring-1.0.1.RC1.jar # Monitoring web service (fat JAR)\
	    ├── pdf-validator-tsl-downloader-1.0.1.RC1.jar   # TSL File Downloader when (fat JAR)\
     	    └── pdf-validator-webapp-1.0.1.RC1.war # PDF Validator webap

3.  Unzip the ZIP-file in directory for example into directory `~/pdfvalidator/`
4.  Install Tomcat 7. For a quick manual install, one can use the
    following commands:

	    curl --remote-name http://www.eu.apache.org/dist/tomcat/tomcat-7/v7.0.64/bin/apache-tomcat-7.0.64.zip
	    unzip apache-tomcat-7.0.64.zip

     After that the directory should look like so:

	    pdfvalidator
	    ├── apache-tomcat-7.0.64 - Tomcat 7 Application server
	    │ ├── bin
	    │ ├── conf
	    │ ├── lib
	    │ ├── LICENSE
	    │ ├── logs
	    │ ├── NOTICE
	    │ ├── RELEASE-NOTES
	    │ ├── RUNNING.txt
	    │ ├── temp
	    │ ├── webapps
	    │ └── work
	    └── bin - Unzipped PDF Validator contents
	    ├── pdf-validator-monitoring-1.0.1.RC1.jar
	    ├── pdf-validator-tsl-downloader-1.0.1.RC1.jar
	    └── pdf-validator-webapp-1.0.1.RC1.war


5.  Create context by issuing following commands relative to
    `~/pdf-validator` directory:

		mkdir -p apache-tomcat-7.0.64/conf/Catalina/localhost\
		touch apache-tomcat-7.0.64/conf/Catalina/localhost/pdf-validator-webapp.xml6.

6.  Copy below contents to `pdf-validator-webapp.xml`

	    <xml version="1.0" encoding="UTF-8"\>    	
	    <Context path="/pdf-validator-webapp" docBase="/home/vagrant/pdfvalidator/bin/pdf-validator-webapp-1.0.1.RC1.war" />

7.  Start the web service by issuing following command relative to
`~/pdf-validator`

	    ./apache-tomcat-7.0.64/bin/startup.sh start

8.  Check that service is running by navigating to URL: `http://localhost:8080/pdf-validator-webapp/wservice` You should see
list of WSDL endpoints.

System checks after installation
--------------------------------

After checking logs for errors You can navigate to `http://<server-ip>:8080/pdf-validator-webapp-1.0.1.RC1/wservice` with browser to 
check if web service has started correctly and see similar web page as shown below.

![WSDL Endpoints after service has started](img/working_web_service.png)

Configuring Java certificate keystore location
----------------------------------------------

When PDF Validator web application starts it creates `etc` directory relative to path of Tomcat startup script execution 
location. In most of Linux distribution the default solution won't work because of lack of write permissions in startup forlder.

To fix this You need to set environment variable `DSS_DATA_FOLDER` and make it accessible to Tomcat Web container user.

<!---
Using Nginx reverse proxy in front of PDF Validator web service
---------------------------------------------------------------
-->

Validation request maximum size limit
-------------------------------------

PDF Validator by default can validate files around **10MB** in size. If there
is need to validate larger files then web service needs to be recompiled. 

Steps to change upload limit:

1.  Open file `PROJECT_BASE_DIRECTORY/pdf-validator-parent/pdf-validator-webservice/src/main/resources/pdf-validator-webservice.xml`
2.  Change this below shown section. File size limit is given in bytes.\

    		<cxf:bus>
    			<cxf:properties>
    				<entry key="org.apache.cxf.stax.maxTextLength" value="10000000" /> 
    			</cxf:properties>
    		</cxf:bus>

3.  Save file and recompile the project in the root using `./build.sh` script

To test file size limit You can submit large files with the following
command (which submits "demo.xml", presumably a large file):

```bash
curl -s -X POST -d "@demo.xml"
http://localhost:8080/pdf-validator-webapp/wservice/validationService\\?wsdl
| xmllint --format - | pygmentize -l xml
```

> **NOTE**: `xmllint` and `pygmentize` maybe required to be installed
> separately

Produced output should look similar to this:

```xml
<?xml version="1.0" ?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope">
	<soap:Body>
		<soap:Fault>
			<faultcode>soap:Client</faultcode>
			 <faultstring>Unmarshalling Error: Text size limit (10000000) exceeded </faultstring>
		 </soap:Fault>
	</soap:Body>
</soap:Envelope>
```
