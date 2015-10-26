# TSL file loading without internet

The Service by default tries to refresh TSL files in cache once every
day, at 3:00 AM. In some cases when PDF Validator is behind the firewall
and part of the Internet is not accessible to PDF Validation Service
then there is a way to manually update TSL files cache directory.

Steps to manually update TSL files temp directory:

1.  First, if you haven’t done so already, turn off downloading TSLs
    from the internet (only needs to be done once):
	-  Make sure PDF Validator Service is not running
	-  Create or copy `application-override.properties` to
	    `$CATALINA\_HOME/conf` directory with following contents:  `trustedListSource.tslRefreshPolicy=NEVER`

	This property only reloads cache contents and it never checks for
	updated TSL file list.

2.  Start the PDF Validation Service.

Using TSL downloader Java application
-------------------------------------

1.  Download the fresh TSLs from the internet and copy them to the
    temp-folder of the server.
	-  Run PDF Validator TSL Downloader in machine that is open to
           Internet:

        	java -jar pdf-validator-tsl-downloader-4.5.RC1.jar\

> After completing it creates `tmp` directory in same directory where the
> JAR file is.

2.  Copy tmp directory contents to PDF validator tmp directory. Default
    location when using Tomcat is `$CATALINA\_HOME/temp`.
    This copying operation should be done during a time when the server
    is not loading from that folder (for example, at 2:30 AM). It is
    recommended to do it as an atomic operation (i.e., a filesystem
    “move” operation).

Changing the location for TSL files and certificates
----------------------------------------------------

By default, validator service will look for downloaded certificates from
default temp directory. In case of Tomcat, it is `$CATALINA\_HOME/temp`,
but in some cases you may want to change it.

Follow these steps:

1. Open or create `application-override.properties` under
   `$CATALINA\_HOME/conf` directory
2. Add property: `fileCacheDataLoader.fileCacheDirectory=/path/to/certificate/directory`
3. Save file and restart PDF validator web service
