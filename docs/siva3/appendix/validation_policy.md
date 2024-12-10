# SiVa Signature Validation Policy

## Introduction
This signature validation policy document specifies signature validation rules used for validating signatures in **SiVa digital signature validation service** (hereinafter: Service).

### Versioning

Different policy versions may be used by the service in the following conditions:

* different validation policies may be in use simultaneously, enabling the Service's user to choose the most suitable policy for a specific business context;
* validation policies are subject to change, i.e. there may be an update to a policy which causes the previous version to become no longer used (obsolete);
* for later reference, the validation report returned by the Service must indicate the specific version of validation policy that was used during validation process.
* for later reference, previous versions of validation policy documents should remain available for the Service's users.

The following validation policy versions are supported in SiVa 2.0 and 3.0 service:

1. [**SiVA Signature Validation Policy - Version 3 (POLv3)**](#POLv3)
2. [**SiVA Signature Validation Policy - Version 4 (POLv4)**](#POLv4)

The following validation policy versions are marked as obsolete in SiVa 2.0 service:

1. [**SiVA Signature Validation Policy - Version 1 (POLv1)**](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv1)
2. [**SiVA Signature Validation Policy - Version 2 (POLv2)**](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv2)

### General principles of SiVa validation policies

1. The validation policy documents describe validation rules for all digital signature formats that are supported in SiVa.
* All rules described for electronic signatures also apply for electronic seals and digital stamps if not explicitly stated otherwise.
* The set of signature validation constraints that are used by the Service are a combination of constraints defined in the Service itself and constraints that are implicitly defined in base components of the service, including:
    * Validation rules defined by the standard or specification documents of the digital signature formats supported in SiVa (described in [Signature format constraints](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_format) section).
    * Validation rules defined by base libraries used in SiVa that implement the supported digital signature formats, i.e. the validation constraints that are imposed by the source code implementation or configuration of the base libraries (described in [Base libraries' constraints](#common_libraries) section).

!!! note
	When no specific validation rule is set in the present document, the requirements and rules from the aforementioned implicit sources for validation requirements shall apply in their entirety. When specific requirements and rules are set in the present validation policy document, they shall prevail over the corresponding requirements set in the implicit resources.


## SiVA Signature Validation Policy - Version 3 (POLv3)
<a name="POLv3"></a>

### Description
Policy for validating Electronic Signatures and Electronic Seals regardless of the legal type of the signature or seal (according to Regulation (EU) No 910/2014), i.e. the fact that the electronic signature or electronic seal is either Advanced electronic Signature (AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES) does not change the total validation result of the signature. 

### URL

```
http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv3
```

### POLv3 validation constraints
1. The signature may be a Qualified Electronic Signature (QES), Advanced electronic Signature (AdES) or AdES supported by a Qualified Certificate (AdES/QC) taking account of the following requirements:
	* Qualified Certificate (QC) requirement
		* The signer’s certificate may be a qualified or non-qualified certificate, as meant by the eIDAS regulation.
		* The signer's certificate is considered acceptable by the validation process even if it is not possible to determine the certificate's QC compliance.
	* SSCD/QSCD (Secure/Qualified Signature Creation Device) requirement
		* Signer certificate may or may not comply with SSCD/QSCD criteria. 
		* The signer's certificate is considered acceptable by the validation process even if it is not possible to determine the certificate's SSCD/QSCD compliance.
* Constraints defined in the [Common validation constraints (POLv3, POLv4)](#common_POLv3_POLv4) section


## SiVA Signature Validation Policy - Version 4 (POLv4)
<a name="POLv4"></a>

### Description
Policy for validating Qualified Electronic Signatures and Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive validation result.

### URL

```
http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
```

### POLv4 validation constraints

1. The requirements for the signature level depend on signature type.
	* Qualified Certificate (QC) requirement
		* The signer’s certificate must be a qualified certificate, as meant by the eIDAS regulation.
		* If Trusted Lists are used during signature validation then also the signer certificate’s qualification information in the Trusted List is taken into account.
	* SSCD/QSCD (Secure/Qualified Signature Creation Device) requirement
		* In case of Signatures, signer certificate should comply with SSCD/QSCD criteria, otherwise warning is returned.
		* In case of Seals, there is no requirement for SSCD/QSCD criteria.
		* If it is not possible to determine the signature type, it must comply with SSCD/QSCD criteria.
		* If Trusted Lists are used during signature validation then the also signer certificate’s SSCD/QSCD qualification information in the Trusted List is taken into account. 
	* The signer's certificate is not considered acceptable by the validation process if it is not possible to determine the certificate's QC and SSCD/QSCD compliance, with the following exception:
		* In case of DIGIDOC-XML 1.0...1.3 and the respective hashcode formats, it is assumed that the signer's certificate complies with QC and SSCD/QSCD requirements, if the certificate is issued by [SK](https://www.skidsolutions.eu/en/repository/certs/) and if the nonRepudiation bit has been set in the certificate's Key Usage field. See also [Certificate Profile](https://www.skidsolutions.eu/en/repository/profiles/) documents of certificates issued by SK, [ETSI EN 319 412-2](http://www.etsi.org/deliver/etsi_en/319400_319499/31941202/02.01.01_60/en_31941202v020101p.pdf) and [ETSI EN 319 412-3](http://www.etsi.org/deliver/etsi_en/319400_319499/31941203/01.01.01_60/en_31941203v010101p.pdf).
* Constraints defined in the [Common validation constraints (POLv3, POLv4)](#common_POLv3_POLv4) section


## Common validation constraints (POLv3, POLv4)
<a name="common_POLv3_POLv4"></a>

### General constraints

1. The validation result returned by SiVa determines whether a signature is technically valid and depending of a container type also conforms to a set of validation constraints that are specific to Estonian legislation and local practices of digital signing. **The policy may not be suitable for signatures created in other territories.**
2. The validation result returned by SiVa comprises validation results of all the signatures in a single signature container (in case of detached signatures) or all signatures in a signed document (in case of enveloped or enveloping signatures). I.e. in case of multiple detached/enveloped/enveloping signatures, overall validation result corresponding to the collection of signatures is not determined.

### Signature format constraints
<a name="common_format"></a>

1. SiVa implicitly implements constraints that are specified in the specification documents of the signature formats supported by the Service:

	* [BDOC 2.1](https://www.id.ee/wp-content/uploads/2021/06/bdoc-spec212-eng.pdf) ASiC-E/XAdES signatures
	* [PAdES](http://www.etsi.org/deliver/etsi_en/319100_319199/31914201/01.01.01_60/en_31914201v010101p.pdf) signatures
	* [XAdES](http://www.etsi.org/deliver/etsi_en/319100_319199/31913201/01.01.01_60/en_31913201v010101p.pdf) signatures
	* [CAdES](http://www.etsi.org/deliver/etsi_en/319100_319199/31912201/01.01.01_60/en_31912201v010101p.pdf) signatures
	* [DIGIDOC-XML](https://www.id.ee/wp-content/uploads/2020/08/digidoc_format_1.3.pdf)  1.0, 1.1, 1.2, 1.3 signatures
	* DIGIDOC-XML 1.0, 1.1, 1.2 and 1.3 signatures in [hashcode format](https://open-eid.github.io/allkirjastamisteenus/json-technical-description/#hashcode-container-form)

### Base libraries' constraints
<a name="common_libraries"></a>

1. SiVa implicitly implements constraints that are imposed by the base software libraries that are used by the service. For more information, see the documentation and source code of the base libraries:

	* [DigiDoc4J](https://github.com/open-eid/digidoc4j) - is used to validate signatures in BDOC 2.1 and DDOC format
	* [Open-eID DSS fork](https://github.com/open-eid/sd-dss) - is used to validate all other signature formats than mentioned above

### Baseline Profile constraints
1. The signature must comply with Baseline Profile of the respective signature format:
	* [XAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103171/02.01.01_60/ts_103171v020101p.pdf)
	* [PAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf)
	* [CAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103173/02.02.01_60/ts_103173v020201p.pdf)
2. In case of Baseline LT-level signature with time-mark, the notation BASELINE_LT_TM is used.
3. The following table describes supported Baseline Profile levels, according to signature formats:

| Signature format | BASELINE_B | BASELINE_T | BASELINE_LT | BASELINE_LT_TM | BASELINE_LTA |
|--|--|--|--|--|--|
|**BDOC** | NOK | NOK| NOK | **OK** | NOK |
|**PAdES** | NOK | NOK | **OK** | NOK | **OK** |
|**XAdES** | NOK | NOK | **OK** | NOK | **OK** |
|**CAdES** | NOK | NOK | **OK** | NOK | **OK** |
|**DIGIDOC-XML 1.0...1.3 **| NOK | NOK | NOK | **OK** | NOK |
|**DIGIDOC-XML 1.0...1.3 hashcode **| NOK | NOK | NOK | **OK** | NOK |

Legend:

* OK - the respective Baseline Profile level is supported and acceptable.
* NOK - the respective Baseline Profile level is not supported or is considered insufficient for the signature format.


### X.509 validation constraints

1. The signer certificate’s Key Usage field must have nonRepudiation bit set (also referred to as contentCommitment).


### Cryptographic algorithm constraints
1. Hash algorithm constraints:
	* In case of BDOC format: when validating a signature where SHA-1 function has been used then a validation warning about weak digest method is returned.
2. Asymmetric cryptographic algorithm constraints:
	* RSA and ECC cryptographic algorithms are supported
	* In case of PAdES/XAdES(also BDOC)/CAdES formats, the RSA key length must be at least 1024 bits and ECC key length at least 192 bits.

### Trust anchor constraints
1. The signature must contain the certificate of the trust anchor and all certificates necessary for the signature validator to build a certification path up to the trust anchor. This applies to the signer’s certificate and the certificates of trust service providers that have issued the time-stamp token and revocation data that are incorporated in the signature.
2. Trust Anchors are:
	* In case of XAdES/CAdES/PAdES formats: [EU Member State Trusted Lists](https://ec.europa.eu/tools/lotl/eu-lotl.xml).
	* In case of DIGIDOC-XML 1.0...1.3 and respective hashcode formats: Estonian CA certificates issued by [SK](https://www.skidsolutions.eu/en/repository/certs/), defined in local configuration file.


### Revocation data constraints
1. The signature must contain evidence record to confirm that certificate was valid at the time of signing.
2.  The evidence record of signer certificate must be in the form of an [OCSP confirmation](https://tools.ietf.org/html/rfc6960) or as a Certificate Revocation List.
3. No additional revocation data other than the data that was originally incorporated in the signature shall be requested during validation time.
4. Checking revocation of certificates regarded as Trust Anchors:
	* In case of DIGIDOC-XML 1.0...1.3 format: revocation of Trust Anchor certificates is not checked.
	* In case of XAdES/CAdES/PAdES formats: revocation of Trust Anchor certificates is checked on the basis of the data in Trusted Lists.


### Signer certificate's revocation freshness constraints
1. In case of BDOC and DIGIDOC-XML 1.0...1.3 BASELINE_LT_TM signatures with time-mark: revocation data is always considered fresh as the revocation data is issued at the trusted signing time.
2. In case of XAdES/CAdES/PAdES BASELINE_LT and BASELINE_LTA signatures with signature time-stamp: revocation data freshness is checked according to the following rules:
	* In case of Estonian signature's OCSP response, if the difference between signature's time-stamp's production time (genTime field) and signer certificate's OCSP confirmation’s production time (producedAt field) is more than 24 hours, then the signature is considered invalid. If the difference is more than 15 minutes and less than 24h, then a validation warning is returned.
	* In case of Certificate Revocation List the signature time-stamp's production time (genTime field) must be within validity range of the CRL (between thisUpdate and nextUpdate)


### Trusted signing time constraints
1. Trusted signing time, denoting the earliest time when it can be trusted (because proven by some Proof-of-Existence present in the signature) that a signature has existed, is determined as follows:
	* In case of signature with time-mark (BASELINE_LT_TM level) - the producedAt value of the earliest valid time-mark (OCSP confirmation of the signer's certificate) in the signature.
	* In case of signature with time-stamp (BASELINE_T, BASELINE_LT or BASELINE_LTA level) - the genTime value of the earliest valid signature time-stamp token in the signature.
	* In case of basic signature (BASELINE_B) - the trusted signing time value cannot be determined as there is no Proof-of-Existence of the signature.
2. In case of QES signature with (archive) time-stamp or ASiC-S with time stamp token, only qualified time-stamps are allowed (TSA/QTST services only).


### BDOC container specific requirements
The BDOC container must conform with [BDOC 2.1](https://www.id.ee/wp-content/uploads/2021/06/bdoc-spec212-eng.pdf) standard.

1. File extension `.bdoc` is supported during signature validation.
2. Only one signature shall be stored in one `signatures.xml` file.
3. All signatures in the container must sign all of the data files.
4. All data files in the container must be signed, i.e. all files in the container, except of `mimetype` file and the files in `META-INF/` folder, must be signed.
5. Two data files with the same name and same path shall not be allowed in the container as the signed data file must be uniquely identifiable in the container. To avoid conflicts in some operating system environments, file names shall be case insensitive.
6. Only relative file paths are supported, for example `META-INF/signatures.xml` and `document.txt` instead of `/META-INF/signatures.xml` and `/document.txt`.
7. `META-INF/manifest.xml` file shall be conformant to OASIS Open Document Format version [1.0](http://docs.oasis-open.org/office/v1.0/OpenDocument-v1.0-os.pdf) or [1.2](http://docs.oasis-open.org/office/v1.2/OpenDocument-v1.2-part3.pdf).

### ASICE container specific requirements
The ASICE container must conform with [ETSI EN 319 162-1](http://www.etsi.org/deliver/etsi_en/319100_319199/31916201/01.01.01_60/en_31916201v010101p.pdf) standard.

1. Warning is returned when signatures in the container do not sign all of the data files.
2. Manifest file must be present.

### ASICS container specific requirements
The service supports both signature and time-stamp token (TST) based ASiC-S containers. Evidence record based containers are not supported. The ASiC-S container must conform with [ETSI EN 319 162-1](http://www.etsi.org/deliver/etsi_en/319100_319199/31916201/01.01.01_60/en_31916201v010101p.pdf) and [ETSI EN 319 162-2](http://www.etsi.org/deliver/etsi_en/319100_319199/31916202/01.01.01_60/en_31916202v010101p.pdf) standards.

1. Manifest file can not be present in case of signature-based ASiC-S containers.
2. Only one data file is present in an ASiC-S container.
3. One or more time-stamp tokens per container is supported:
	- Only one `META-INF/timestamp.tst` can be present.
	- In case of multiple timestamps, each following time-stamp token must have a unique name (e.g., `timestamp001.tst`).
4. There must not be any of the following files in the ASiC-S container:
	- `META-INF/signature.p7s`
	- `META-INF/signatures.xml`
	- `META-INF/evidencerecord.ers`
	- `META-INF/evidencerecord.xml`
5. Time-stamp tokens of ASiC-S containers are validated via DSS (TSL verification is performed).
