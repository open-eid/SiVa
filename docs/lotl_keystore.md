# Generating keystore for LOTL validation (optional)

PDF validator contains a Java keystore that holds certificates for TSL
validation (more precisely, certificates for validating the signature of
the LOTL, or List of the Lists). The file name is `keystore.jks`
(usually contained in the `etc` folder under Tomcat). As of this writing
the keystore contains 3 certificates. In other words, these 3
certificates will be used by default for validating the LOTL signatures.
If necessary, one can configure these accepted certificates.

When PDF validator is deployed to Tomcat and started, it tries to load
the certificates contained in `etc/keystore.jks` (the path is relative
to the path of the directory where Tomcat was started from). When the
file does not exist, the application tries to copy the `keystore.jks`
that it has under its classpath (usually inside its installation
WAR-file) to `etc/keystore.jks`, and then load it.

To override the default keystore, following steps should be taken:

1.  Make sure PDF Validator service is not running
2.  Make sure that the certificates that will be added to keystore are
    present in the `keystore_certificates` directory, which is relative
    to the executable jar:

    	ls keystore_certificates
    	ec.europa.eu.2.crt ec.europa.eu.3.crt ec.europa.eu.crt

3.  Run PDF Validator Keystore Generator to generate keystore:

		java -jar pdf-validator-keystore-generator-4.5.RC1.jar\

    keystore.jks will be created inside the etc/ folder, which is
    relative to the given executable jar.

4.  Copy the newly create `etc` directory to Tomcat's `bin` directory.
5.  Start PDF validator:

	    ./bin/startup.sh


