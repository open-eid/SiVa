### Scheduled update of the EU LOTL signer certificates in April 2026

A new notice was scheduled for publication in the Official Journal of the European Union on **April 14, 2026**, updating 
the set of signer certificates for LOTL and resetting pivot LOTL chain.

!!! warning

    SiVa users who have pivot LOTL mechanism enabled through *siva.tsl.loader.LotlPivotSupportEnabled* property
    (enabled by default since SiVa `3.6.0`) **need to update their LOTL truststore and the ojUrl property**!

    Failure to update before **April 29, 2026**, will make it impossible to load the European
    LOTL and the trusted lists of the member states!

#### How to update LOTL truststore and ojUrl property if pivot mechanism is enabled

With pivot LOTL mechanism, only the initial set of LOTL signing certificates need to be trusted.
In order to update the truststore for the initial set of LOTL signing certificates, do as follows:

1. Find the currently valid set of trusted EU LOTL signing certificate candidates.
    * The new set of LOTL signing certificates can be found from the annex of the
      [Information related to data on Member States’ trusted lists as notified under Commission Implementing Decision (EU) 2015/1505 as amended by Commission Implementing Decision (EU) 2025/2164](https://eur-lex.europa.eu/eli/C/2026/1944/oj).
2. Use [Java keytool](https://docs.oracle.com/en/java/javase/21/docs/specs/man/keytool.html)
   (or any other keystore editor of your choice) to generate a new truststore with the new certificates.
3. Use the newly generated truststore:
    * **before version 3.10.0**: the default truststore can be overwritten with following properties:
        - [siva.keystore.filename](https://github.com/open-eid/SiVa/blob/release-3.9.0/validation-services-parent/tsl-loader/src/main/java/ee/openeid/tsl/configuration/TSLValidationKeystoreProperties.java#L26).
          Note that the truststore file location can be only overridden when using environment variable `DSS_DATA_FOLDER`.
          By default, the truststore file location is expected to be on local filesystem in `etc` directory which is at
          the same level with the fat jar file (one is created, if no such directory exists).
          It is also needed to delete the "temp" truststore from default/specified location.
        - [siva.keystore.password](https://github.com/open-eid/SiVa/blob/release-3.9.0/validation-services-parent/tsl-loader/src/main/java/ee/openeid/tsl/configuration/TSLValidationKeystoreProperties.java#L28).
        - [siva.keystore.type](https://github.com/open-eid/SiVa/blob/release-3.9.0/validation-services-parent/tsl-loader/src/main/java/ee/openeid/tsl/configuration/TSLValidationKeystoreProperties.java#L25).
    * **since version 3.10.0**: the default truststore can be overwritten with following properties:
        - `siva.tsl.loader.lotlTruststorePath` using either classpath or file resource path.
        - `siva.tsl.loader.lotlTruststorePassword`.
        - `siva.tsl.loader.lotlTruststoreType`.

Set the new Official Journal of the European Union URL to *https://eur-lex.europa.eu/eli/C/2026/1944/oj* through the 
following property: [siva.tsl.loader.ojUrl](https://github.com/open-eid/SiVa/blob/release-3.9.0/validation-services-parent/tsl-loader/src/main/java/ee/openeid/tsl/configuration/TSLLoaderConfigurationProperties.java#L32).
