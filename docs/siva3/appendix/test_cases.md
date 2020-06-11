Test Case Descriptions
==================

## Introduction
This document gives overview of the test cases. This page is partially generated using a script, more info about the script can be found in [SiVa GitHub](https://github.com/open-eid/SiVa/tree/master/test-helpers).

The structure and elements of test case is described in [QA Strategy](http://open-eid.github.io/SiVa/siva3/qa_strategy/#testing) document.
All the files used in the tests can be found in [SiVa GitHub](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/resources).

## AsiceValidationFailIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/AsiceValidationFailIT.java)


**TestCaseID: Asice-ValidationFail-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with single invalid signature
  * Expected Result: The document should fail the validation
  * File: InvalidLiveSignature.asice


**TestCaseID: Asice-ValidationFail-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice with multiple invalid signatures
  * Expected Result: The document should fail the validation
  * File: InvalidMultipleSignatures.bdoc


**TestCaseID: Asice-ValidationFail-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice with multiple signatures both valid and invalid
  * Expected Result: The document should fail the validation
  * File: InvalidAndValidSignatures.asice


**TestCaseID: Asice-ValidationFail-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice with no signatures
  * Expected Result: The document should fail the validation
  * File: AsiceContainerNoSignature.asice


**TestCaseID: Asice-ValidationFail-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Wrong signature timestamp
  * Expected Result: The document should fail the validation
  * File: TS-02_23634_TS_wrong_SignatureValue.asice


**TestCaseID: Asice-ValidationFail-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice No non-repudiation key usage value in the certificate, verification of AdES signature level
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-I-43.asice


**TestCaseID: Asice-ValidationFail-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice signers certificate does not have non-repudiation value in the certificates key usage field and it does not contain the QC and SSCD compliance information.
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-I-26.asice


**TestCaseID: Asice-ValidationFail-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: OCSP certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: TM-01_bdoc21-unknown-resp.bdoc


**TestCaseID: Asice-ValidationFail-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice TSA certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: TS-05_23634_TS_unknown_TSA.asice


**TestCaseID: Asice-ValidationFail-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice OCSP response status is revoked
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-R-25.asice


**TestCaseID: Asice-ValidationFail-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice difference between OCSP and time-stamp issuing times is more than 24 hours
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-V-20.asice


**TestCaseID: Asice-ValidationFail-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice unsigned data files in the container
  * Expected Result: The document should pass the validation with warning
  * File: EE_SER-AEX-B-LT-V-34.asice


**TestCaseID: Asice-ValidationFail-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice SignatureValue does not correspond to the SignedInfo block
  * Expected Result: The document should fail the validation
  * File: REF-19_bdoc21-no-sig-asn1-pref.bdoc


**TestCaseID: Asice-ValidationFail-14**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice Baseline-BES file
  * Expected Result: The document should fail the validation
  * File: signWithIdCard_d4j_1.0.4_BES.asice


**TestCaseID: Asice-ValidationFail-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice Baseline-EPES file
  * Expected Result: The document should fail the validation
  * File: TM-04_kehtivuskinnituset.4.asice


**TestCaseID: Asice-ValidationFail-16**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice signers certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: SS-4_teadmataCA.4.asice


**TestCaseID: Asice-ValidationFail-17**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice OCSP response status is revoked
  * Expected Result: The document should fail the validation
  * File: TM-15_revoked.4.asice


**TestCaseID: Asice-ValidationFail-18**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice OCSP response status is unknown
  * Expected Result: The document should fail the validation
  * File: TM-16_unknown.4.asice


**TestCaseID: Asice-ValidationFail-19**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice signed data file has been removed from the container
  * Expected Result: The document should fail the validation
  * File: KS-21_fileeemaldatud.4.asice


**TestCaseID: Asice-ValidationFail-20**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice no files in container
  * Expected Result: The document should fail the validation
  * File: KS-02_tyhi.bdoc


**TestCaseID: Asice-ValidationFail-21**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice signed data file(s) don't match the hash values in reference elements
  * Expected Result: The document should fail the validation
  * File: REF-14_filesisumuudetud.4.bdoc


**TestCaseID: Asice-ValidationFail-22**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice Baseline-T signature
  * Expected Result: The document should fail the validation
  * File: TS-06_23634_TS_missing_OCSP.asice


**TestCaseID: Asice-ValidationFail-23**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice certificate's validity time is not in the period of OCSP producedAt time
  * Expected Result: The document should fail the validation
  * File:

  **Attention! This test is disabled: test file is needed where certificate expiration end is before the OCSP produced at time


## AsiceValidationPassIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/AsiceValidationPassIT.java)


**TestCaseID: Asice-ValidationPass-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice with single valid signature
  * Expected Result: The document should pass the validation
  * File: ValidLiveSignature.asice


**TestCaseID: Asice-ValidationPass-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice TM with multiple valid signatures
  * Expected Result: The document should pass the validation
  * File: BDOC-TS.asice


**TestCaseID: Asice-ValidationPass-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice One LT signature with certificates from different countries
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LT-V-30.asice


**TestCaseID: Asice-ValidationPass-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice Baseline-LT file
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LT-V-49.asice


**TestCaseID: Asice-ValidationPass-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice Baseline-LTA file
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LTA-V-24.asice


**TestCaseID: Asice-ValidationPass-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice file signed with Mobile-ID, ECC-SHA256 signature with prime256v1 key
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LT-V-2.asice


**TestCaseID: Asice-ValidationPass-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice file with 	ESTEID-SK 2015 certificate chain
  * Expected Result: The document should pass the validation
  * File: IB-4270_TS_ESTEID-SK 2015  SK OCSP RESPONDER 2011.asice


**TestCaseID: Asice-ValidationPass-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice file with KLASS3-SK 2010 (EECCRCA) certificate chain
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LT-V-28.asice


**TestCaseID: Asice-ValidationPass-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: *.sce file with TimeStamp
  * Expected Result: The document should pass the validation
  * File: ASICE_TS_LTA_content_as_sce.sce


**TestCaseID: Asice-ValidationPass-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice-TS with special characters in data file
  * Expected Result: The document should pass the validation with correct signature scope
  * File: Nonconventionalcharacters.asice


**TestCaseID: Asice-ValidationPass-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice unsigned data files in the container
  * Expected Result: The document should pass the validation with warning
  * File: EE_SER-AEX-B-LT-V-34.asice


**TestCaseID: Asice-ValidationPass-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: New Estonian ECC signature
  * Expected Result: The document should pass the validation
  * File: Mac_AS0099904_EsimeneAmetlikSKTestElliptilistega_TS.asice


## AsicsValidationFailIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/AsicsValidationFailIT.java)


**TestCaseID: Asics-ValidationFail-1**

  * TestType: Automated
  * Requirement: []()
  * Title: Only one datafile is allowed in ASIC-s
  * Expected Result: The validation should fail
  * File: TwoDataFilesAsics.asics


**TestCaseID: Asics-ValidationFail-2**

  * TestType: Automated
  * Requirement: []()
  * Title: No data file in ASIC-s
  * Expected Result: The validation should fail
  * File: DataFileMissingAsics.asics


**TestCaseID: Asics-ValidationFail-3**

  * TestType: Automated
  * Requirement: []()
  * Title: more folders that META-INF in ASIC-s
  * Expected Result: The validation should fail
  * File: FoldersInAsics.asics


**TestCaseID: Asics-ValidationFail-4**

  * TestType: Automated
  * Requirement: []()
  * Title: META-INF folder not in root of container
  * Expected Result: The validation should fail
  * File: MetaInfNotInRoot.asics


**TestCaseID: Asics-ValidationFail-5**

  * TestType: Automated
  * Requirement: []()
  * Title: Not allowed files in META-INF folder
  * Expected Result: The validation should fail
  * File: signatureMixedWithTST.asics


**TestCaseID: Asics-ValidationFail-6**

  * TestType: Automated
  * Requirement: []()
  * Title: TST not intact
  * Expected Result: The validation should fail
  * File: AsicsTSTsignatureModified.asics


**TestCaseID: Asics-ValidationFail-7**

  * TestType: Automated
  * Requirement: []()
  * Title: TST has been corrupted
  * Expected Result: The validation should fail
  * File: AsicsTSTsignatureBroken.asics


**TestCaseID: Asics-ValidationFail-8**

  * TestType: Automated
  * Requirement: []()
  * Title: Data file changed
  * Expected Result: The validation should fail
  * File: DatafileAlteredButStillValid.asics


**TestCaseID: Asics-ValidationFail-9**

  * TestType: Automated
  * Requirement: []()
  * Title: Exluding files in META-INF folder together with TST
  * Expected Result: The validation should fail
  * File: evidencerecordMixedWithTST.asics


## AsicsValidationPassIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/AsicsValidationPassIT.java)


**TestCaseID: Asics-ValidationPass-1**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of ASICs with DDOC inside
  * Expected Result: TST and inner DDOC are valid
  * File: ValidDDOCinsideAsics.asics


**TestCaseID: Asics-ValidationPass-2**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of ASICs with DDOC inside SCS extension
  * Expected Result: TST and inner DDOC are valid
  * File: ValidDDOCinsideAsics.scs


**TestCaseID: Asics-ValidationPass-3**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of ASICs with BDOC inside
  * Expected Result: TST and inner BDOC are valid
  * File: ValidBDOCinsideAsics.asics


**TestCaseID: Asics-ValidationPass-4**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of ASICs with text document inside
  * Expected Result: TST is valid
  * File: TXTinsideAsics.asics


**TestCaseID: Asics-ValidationPass-5**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of ASICs with ASICs inside
  * Expected Result: TST is valid, no inner looping of ASICs
  * File: ValidASICSinsideAsics.asics


**TestCaseID: Asics-ValidationPass-6**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of ASICs with DDOC inside ZIP extension
  * Expected Result: TST and inner DDOC are valid
  * File: ValidDDOCinsideAsics.zip


**TestCaseID: Asics-ValidationPass-7**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of ASICs with wrong mimetype with DDOC inside
  * Expected Result: TST and inner DDOC are valid
  * File: ValidDDOCinsideAsicsWrongMime.asics


## BdocValidationFailIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/BdocValidationFailIT.java)


**TestCaseID: Bdoc-ValidationFail-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with single invalid signature
  * Expected Result: The document should fail the validation
  * File: IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc


**TestCaseID: Bdoc-ValidationFail-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with multiple invalid signatures
  * Expected Result: The document should fail the validation
  * File: BdocMultipleSignaturesInvalid.bdoc


**TestCaseID: Bdoc-ValidationFail-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with multiple signatures both valid and invalid
  * Expected Result: The document should fail the validation
  * File: BdocMultipleSignaturesMixedWithValidAndInvalid.bdoc


**TestCaseID: Bdoc-ValidationFail-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with no signatures
  * Expected Result: The document should fail the validation
  * File: BdocContainerNoSignature.bdoc


**TestCaseID: Bdoc-ValidationFail-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Wrong signature timestamp
  * Expected Result: The document should fail the validation in DD4J
  * File: TS-02_23634_TS_wrong_SignatureValue.asice


**TestCaseID: Bdoc-ValidationFail-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice No non-repudiation key usage value in the certificate, verification of AdES signature level
  * Expected Result: The document should fail the validation in DD4J
  * File: EE_SER-AEX-B-LT-I-43.asice


**TestCaseID: Bdoc-ValidationFail-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice signers certificate does not have non-repudiation value in the certificates key usage field and it does not contain the QC and SSCD compliance information.
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-I-26.asice


**TestCaseID: Bdoc-ValidationFail-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc OCSP certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: TM-01_bdoc21-unknown-resp.bdoc


**TestCaseID: Bdoc-ValidationFail-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice TSA certificate is not trusted
  * Expected Result: The document should fail the validation in DD4J
  * File: TS-05_23634_TS_unknown_TSA.asice


**TestCaseID: Bdoc-ValidationFail-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice OCSP response status is revoked
  * Expected Result: The document should fail the validation in DD4J
  * File: EE_SER-AEX-B-LT-R-25.asice


**TestCaseID: Bdoc-ValidationFail-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice difference between OCSP and time-stamp issuing times is more than 24 hours
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-V-20.asice


**TestCaseID: Bdoc-ValidationFail-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc different data file mime-type values in signatures.xml and manifest.xml files
  * Expected Result: The document should return warning regarding the mismatch
  * File: 23613_TM_wrong-manifest-mimetype.bdoc


**TestCaseID: Bdoc-ValidationFail-16**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc SignatureValue does not correspond to the SignedInfo block
  * Expected Result: The document should fail the validation
  * File: REF-19_bdoc21-no-sig-asn1-pref.bdoc


**TestCaseID: Bdoc-ValidationFail-17**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc Baseline-BES file
  * Expected Result: The document should fail the validation in DD4J
  * File: signWithIdCard_d4j_1.0.4_BES.asice


**TestCaseID: Bdoc-ValidationFail-18**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc Baseline-EPES file
  * Expected Result: The document should fail the validation
  * File: TM-04_kehtivuskinnituset.4.asice


**TestCaseID: Bdoc-ValidationFail-19**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc signers certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: SS-4_teadmataCA.4.asice


**TestCaseID: Bdoc-ValidationFail-20**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc OCSP response status is revoked
  * Expected Result: The document should fail the validation
  * File: TM-15_revoked.4.asice


**TestCaseID: Bdoc-ValidationFail-21**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc OCSP response status is unknown
  * Expected Result: The document should fail the validation
  * File: TM-16_unknown.4.asice


**TestCaseID: Bdoc-ValidationFail-22**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc signed data file has been removed from the container
  * Expected Result: The document should fail the validation in DD4J
  * File: KS-21_fileeemaldatud.4.asice


**TestCaseID: Bdoc-ValidationFail-23**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc no files in container
  * Expected Result: The document should fail the validation
  * File: KS-02_tyhi.bdoc


**TestCaseID: Bdoc-ValidationFail-24**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc wrong nonce
  * Expected Result: The document should fail the validation
  * File: TM-10_noncevale.4.asice


**TestCaseID: Bdoc-ValidationFail-25**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc signed data file(s) don't match the hash values in reference elements
  * Expected Result: The document should fail the validation
  * File: REF-14_filesisumuudetud.4.bdoc


**TestCaseID: Bdoc-ValidationFail-26**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice Baseline-T signature
  * Expected Result: The document should fail the validation in DD4J
  * File: TS-06_23634_TS_missing_OCSP.asice


**TestCaseID: Bdoc-ValidationFail-27**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc OCSP response is not the one expected
  * Expected Result: The document should fail the validation
  * File: 23608-bdoc21-TM-ocsp-bad-nonce.bdoc


**TestCaseID: Bdoc-ValidationFail-28**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc certificate's validity time is not in the period of OCSP producedAt time
  * Expected Result: The document should fail the validation
  * File: 23154_test1-old-sig-sigat-OK-prodat-NOK-1.bdoc


**TestCaseID: Bdoc-ValidationFail-29**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc 	BDOC-1.0 version container
  * Expected Result: The document should fail the validation
  * File: BDOC-1.0.bdoc


## BdocValidationPassIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/BdocValidationPassIT.java)


**TestCaseID: Bdoc-ValidationPass-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Bdoc with single valid signature
  * Expected Result: The document should pass the validation
  * File: Valid_ID_sig.bdoc


**TestCaseID: Bdoc-ValidationPass-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Bdoc TM with multiple valid signatures
  * Expected Result: The document should pass the validation
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Bdoc-ValidationPass-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Bdoc with warning on signature
  * Expected Result: The document should pass the validation but warning should be returned
  * File: bdoc_weak_warning_sha1.bdoc

  **Attention! This test is disabled: New file needed. This one has different mimetype value in manifest.xml and signature.xml


**TestCaseID: Bdoc-ValidationPass-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice One LT signature with certificates from different countries
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LT-V-30.asice


**TestCaseID: Bdoc-ValidationPass-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Bdoc signed with Mobile-ID, ECC-SHA256 signature with prime256v1 key
  * Expected Result: The document should pass the validation
  * File: 24050_short_ecdsa_correct_file_mimetype.bdoc


**TestCaseID: Bdoc-ValidationPass-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice Baseline-LT file
  * Expected Result: The document should pass the validation in DD4J
  * File: EE_SER-AEX-B-LT-V-49.asice


**TestCaseID: Bdoc-ValidationPass-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice QES file
  * Expected Result: The document should pass the validation
  * File: ValidLiveSignature.asice


**TestCaseID: Bdoc-ValidationPass-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice Baseline-LTA file
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LTA-V-24.asice


**TestCaseID: Bdoc-ValidationPass-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice file signed with Mobile-ID, ECC-SHA256 signature with prime256v1 key
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LT-V-2.asice


**TestCaseID: Bdoc-ValidationPass-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice file with 	ESTEID-SK 2015 certificate chain
  * Expected Result: The document should pass the validation
  * File: IB-4270_TS_ESTEID-SK 2015  SK OCSP RESPONDER 2011.asice


**TestCaseID: Bdoc-ValidationPass-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Asice file with KLASS3-SK 2010 (EECCRCA) certificate chain
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LT-V-28.asice


**TestCaseID: Bdoc-ValidationPass-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Bdoc with Baseline-LT_TM and QES signature level and ESTEID-SK 2011 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: BDOC2.1.bdoc


**TestCaseID: Bdoc-ValidationPass-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Bdoc TS with multiple valid signatures
  * Expected Result: The document should pass the validation
  * File: Test_id_aa.asice


**TestCaseID: Bdoc-ValidationPass-14**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Bdoc-TM with special characters in data file
  * Expected Result: The document should pass the validation
  * File: Šužlikud sõid ühe õuna ära.bdoc


**TestCaseID: Bdoc-ValidationPass-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: *.sce file with TimeMark
  * Expected Result: The document should pass the validation
  * File: BDOC2.1_content_as_sce.sce


**TestCaseID: Bdoc-ValidationPass-16**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: *.sce file with TimeStamp
  * Expected Result: The document should pass the validation
  * File: ASICE_TS_LTA_content_as_sce.sce


**TestCaseID: Bdoc-ValidationPass-17**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc-TS with special characters in data file
  * Expected Result: The document should pass the validation with correct signature scope
  * File: Nonconventionalcharacters.asice


**TestCaseID: Bdoc-ValidationPass-18**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice unsigned data files in the container
  * Expected Result: The document should pass the validation with warning
  * File: EE_SER-AEX-B-LT-V-34.asice


**TestCaseID: Bdoc-ValidationPass-19**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: ECC signature vith BDOC TM
  * Expected Result: The document should pass the validation
  * File: testECCDemo.bdoc


**TestCaseID: Bdoc-ValidationPass-20**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: ECC signature vith BDOC TS
  * Expected Result: The document should pass the validation
  * File: Mac_AS0099904_EsimeneAmetlikSKTestElliptilistega_TS.asice


**TestCaseID: Bdoc-ValidationPass-21**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Asice with wrong slash character ('\') in data file mime-type value
  * Expected Result: The document should pass
  * File: EE_SER-AEX-B-LT-V-33.bdoc


**TestCaseID: Bdoc-ValidationPass-22**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with invalid mimetype in manifest
  * Expected Result: The document should pass
  * File: 23147_weak-warning-sha1-invalid-mimetype-in-manifest.bdoc

  **Attention! This test is disabled: s://jira.ria.ee/browse/DD4J-161")


## DdocGetDataFilesIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/DdocGetDataFilesIT.java)


**TestCaseID:  Ddoc-Get-Data-Files-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process](http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process)
  * Title: Valid DDOC with data file used
  * Expected Result: The data file should be returned
  * File: 18912.ddoc


**TestCaseID:  Ddoc-Get-Data-Files-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process](http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process)
  * Title: Invalid DDOC with data file used
  * Expected Result: The data file should be returned
  * File: OCSP nonce vale.ddoc


**TestCaseID:  Ddoc-Get-Data-Files-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process](http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process)
  * Title:DDOC with xml v1.1 is  used
  * Expected Result: The data file is returned
  * File: DIGIDOC-XML1.1.ddoc


**TestCaseID:  Ddoc-Get-Data-Files-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process](http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process)
  * Title: DDOC with xml v1.2 is  used
  * Expected Result: The data file is returned
  * File: DIGIDOC-XML1.2.ddoc


**TestCaseID:  Ddoc-Get-Data-Files-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process](http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process)
  * Title: DDOC with xml v1.3 is  used
  * Expected Result: The data file is returned
  * File: DIGIDOC-XML1.3.ddoc


**TestCaseID:  Ddoc-Get-Data-Files-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process](http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process)
  * Title: Hashcoded DDOC  is  used
  * Expected Result: Null is returned
  * File: DIGIDOC-XML1.3_hashcode.ddoc

  **Attention! This test is disabled: Run this test manually as it fails in Travis


**TestCaseID:  Ddoc-Get-Data-Files-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process](http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process)
  * Title: DDOC  with 12 different  files of different types  is  used
  * Expected Result: Data file returned for each file correctly
  * File: igasugust1.3.ddoc


**TestCaseID:  Ddoc-Get-Data-Files-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process](http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process)
  * Title: BDOC with data file used
  * Expected Result: The error message  should be returned
  * File: BDOC-TS.bdoc


**TestCaseID:  Ddoc-Get-Data-Files-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process](http://open-eid.github.io/SiVa/siva2/use_cases/#ddoc-data-file-extraction-process)
  * Title: PDF file used
  * Expected Result: The error message  should be returned
  * File: hellopades-lt-b.pdf


## DdocValidationFailIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/DdocValidationFailIT.java)


**TestCaseID: Ddoc-ValidationFail-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc with single invalid signature
  * Expected Result: The document should fail the validation
  * File: AndmefailiAtribuudidMuudetud.ddoc


**TestCaseID: Ddoc-ValidationFail-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc with multiple invalid signatures
  * Expected Result: The document should fail the validation
  * File: multipleInvalidSignatures.ddoc


**TestCaseID: Ddoc-ValidationFail-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc with multiple signatures both valid and invalid
  * Expected Result: The document should fail the validation
  * File: multipleValidAndInvalidSignatures.ddoc


**TestCaseID: Ddoc-ValidationFail-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc with no signatures
  * Expected Result: The document should fail the validation
  * File: DdocContainerNoSignature.bdoc


**TestCaseID: Ddoc-ValidationFail-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc signature value has been changed (SignatureValue does not correspond to the SignedInfo block)
  * Expected Result: The document should fail the validation
  * File: test-inv-sig-inf.ddoc


**TestCaseID: Ddoc-ValidationFail-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc Data file(s) don't match the hash values in Reference elements
  * Expected Result: The document should fail the validation
  * File: AndmefailiAtribuudidMuudetud.ddoc


**TestCaseID: Ddoc-ValidationFail-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc Baseline-BES file, no OCSP response
  * Expected Result: The document should fail the validation
  * File: ilma_kehtivuskinnituseta.ddoc


**TestCaseID: Ddoc-ValidationFail-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc no non-repudiation key usage value in the certificate
  * Expected Result: The document should fail the validation
  * File: test-non-repu1.ddoc


**TestCaseID: Ddoc-ValidationFail-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc Signer's certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: Belgia_kandeavaldus_LIV.ddoc


**TestCaseID: Ddoc-ValidationFail-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc OCSP certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: Tundmatu_OCSP_responder.ddoc


**TestCaseID: Ddoc-ValidationFail-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc has unsigned data files in the container
  * Expected Result: The document should fail the validation
  * File: lisatud_andmefail.ddoc


**TestCaseID: Ddoc-ValidationFail-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc signed data file has been removed from the container
  * Expected Result: The document should fail the validation
  * File: faileemald1.ddoc


**TestCaseID: Ddoc-ValidationFail-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc wrong nonce
  * Expected Result: The document should fail the validation
  * File: OCSP nonce vale.ddoc


**TestCaseID: Ddoc-ValidationFail-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/](http://open-eid.github.io/SiVa/siva2/interfaces/)
  * Title: Ddoc with XML Entity expansion attack
  * Expected Result: The document should fail the validation with error
  * File: xml_expansion.ddoc


**TestCaseID: Ddoc-ValidationFail-14**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/](http://open-eid.github.io/SiVa/siva2/interfaces/)
  * Title: Ddoc with XML server side request forgery attack
  * Expected Result: The document should fail the validation with error
  * File: xml_entity.ddoc


**TestCaseID: Ddoc-ValidationFail-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc no files in container
  * Expected Result: The document should fail the validation
  * File: KS-02_tyhi.ddoc


## DdocValidationPassIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/DdocValidationPassIT.java)


**TestCaseID: Ddoc-ValidationPass-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Ddoc v1.0 with valid signatures
  * Expected Result: The document should pass the validation
  * File: SK-XML1.0.ddoc

  **Attention! This test is disabled: DDOC 1.0 fails in Travis CI. Needs investigation


**TestCaseID: Ddoc-ValidationPass-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Ddoc v1.1 with valid signature
  * Expected Result: The document should pass the validation
  * File: DIGIDOC-XML1.1.ddoc


**TestCaseID: Ddoc-ValidationPass-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Ddoc v1.2 with valid signature
  * Expected Result: The document should pass the validation
  * File: DIGIDOC-XML1.2.ddoc


**TestCaseID: Ddoc-ValidationPass-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Ddoc v1.3 with valid signature with ESTEID-SK 2011 certificate chain
  * Expected Result: The document should pass the validation
  * File: DIGIDOC-XML1.3.ddoc


**TestCaseID: Ddoc-ValidationPass-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Ddoc v1.3 with valid signature, signed data file name has special characters and ESTEID-SK certificate chain
  * Expected Result: The document should pass the validation
  * File: susisevad1_3.ddoc


**TestCaseID: Ddoc-ValidationPass-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Ddoc v1.3 KLASS3-SK certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: KLASS3-SK _ KLASS3-SK OCSP RESPONDER uus.ddoc


**TestCaseID: Ddoc-ValidationPass-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Ddoc v1.3 KLASS3-SK 2010 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: KLASS3-SK 2010 _ KLASS3-SK 2010 OCSP RESPONDER.ddoc


**TestCaseID: Ddoc-ValidationPass-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Ddoc v1.1 ESTEID-SK 2007 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: vaikesed1.1.ddoc


**TestCaseID: Ddoc-ValidationPass-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Ddoc v1.1 ESTEID-SK 2007 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: IB-4270_ESTEID-SK 2015  SK OCSP RESPONDER 2011.ddoc


**TestCaseID: Ddoc-ValidationPass-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Ddoc v1.1 ESTEID-SK certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: EID-SK _ EID-SK OCSP RESPONDER.ddoc


**TestCaseID: Ddoc-ValidationPass-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Ddoc v1.1 ESTEID-SK 2007 and OCSP 2010 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: EID-SK 2007 _ EID-SK 2007 OCSP RESPONDER 2010.ddoc


**TestCaseID: Ddoc-ValidationPass-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Ddoc v1.3 ESTEID-SK 2007 and OCSP 2007 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: EID-SK 2007 _ EID-SK 2007 OCSP RESPONDER.ddoc


**TestCaseID: Ddoc-ValidationPass-14**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Ddoc v1.3 ESTEID-SK 2011 and OCSP 2011 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: EID-SK 2011 _ SK OCSP RESPONDER 2011.ddoc


**TestCaseID: Ddoc-ValidationPass-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Ddoc with warning should pass
  * Expected Result: Document passes the validation
  * File: 18912.ddoc


**TestCaseID: DdocHashcode-ValidationPass-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Validation of DDOC Hashcode v1.0
  * Expected Result: Document passes the validation
  * File: SK-XML1.0_hashcode.ddoc

  **Attention! This test is disabled: 


**TestCaseID: DdocHashcode-ValidationPass-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Validation of DDOC Hashcode v1.1
  * Expected Result: Document passes the validation
  * File: DIGIDOC-XML1.1_hashcode.ddoc

  **Attention! This test is disabled: This tests fails in Travis. Has been executed locally


**TestCaseID: DdocHashcode-ValidationPass-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Validation of DDOC Hashcode v1.2
  * Expected Result: Document passes the validation
  * File: DIGIDOC-XML1.2_hashcode.ddoc

  **Attention! This test is disabled: This tests fails in Travis. Has been executed locally


**TestCaseID: DdocHashcode-ValidationPass-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Validation of DDOC Hashcode v1.3
  * Expected Result: Document passes the validation
  * File: DIGIDOC-XML1.3_hashcode.ddoc

  **Attention! This test is disabled: This tests fails in Travis. Has been executed locally


## DocumentFormatIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/DocumentFormatIT.java)


**TestCaseID: DocumentFormat-1**

  * TestType: Automated
  * Requirement: [ http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4]( http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Validation of pdf document acceptance
  * Expected Result: Pdf is accepted and correct signature validation is given
  * File: hellopades-pades-lt-sha256-sign.pdf


**TestCaseID: DocumentFormat-2**

  * TestType: Automated
  * Requirement: [ http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4]( http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Validation of bdoc document acceptance
  * Expected Result: Bdoc is accepted and correct signature validation is given
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: DocumentFormat-3**

  * TestType: Automated
  * Requirement: [ http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4]( http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Validation of asice document acceptance
  * Expected Result: asice is accepted and correct signature validation is given
  * File: Vbdoc21-TS.asice


**TestCaseID: DocumentFormat-4**

  * TestType: Automated
  * Requirement: [ http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4]( http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Validation of asics document acceptance
  * Expected Result: asics is accepted and correct signature validation is given
  * File: ValidDDOCinsideAsics.asics


**TestCaseID: DocumentFormat-5**

  * TestType: Automated
  * Requirement: [ http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4]( http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Validation of xades acceptance
  * Expected Result: xades is accepted and correct signature validation is given
  * File: signatures0.xml


**TestCaseID: DocumentFormat-6**

  * TestType: Automated
  * Requirement: [ http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4]( http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Validation of cades acceptance
  * Expected Result: cades is accepted and correct signature validation is given
  * File:

  **Attention! This test is disabled: Test file needed


## DocumentValidationIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/DocumentValidationIT.java)


**TestCaseID: Document-Validation-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title:Bdoc with two signatures and one unsigned document.
  * Expected Result: The document should fail the validation and warnings should be displayed.
  * File:3f_2s_1f_unsigned.bdoc


**TestCaseID: Document-Validation-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with two signatures and one document signed by only one signature.
  * Expected Result: The document should fail the validation and warnings should be displayed.
  * File:3f_2s_1partly_signed.bdoc


**TestCaseID: Document-Validation-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with two signatures and two documents signed by only one signature.
  * Expected Result: The document should fail the validation and warnings should be displayed.
  * File:3f_2s_2partly_signed.bdoc


**TestCaseID: Document-Validation-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with two signatures, one unsigned and two partly signed documents.
  * Expected Result: The document should fail the validation and warnings should be displayed.
  * File:4f_2s_all_combinations.bdoc


**TestCaseID: Document-Validation-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with three unsigned documents.
  * Expected Result: The document should pass the validation and warnings should be displayed.
  * File:6f_2s_3unsigned.bdoc


**TestCaseID: Document-Validation-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with deleted document, named in manifest.
  * Expected Result: The document should fail the validation and warning should be displayed.
  * File:2f_2signed_1f_deleted.bdoc


**TestCaseID: Document-Validation-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with deleted document, also removed from manifest.
  * Expected Result: The document should fail the validation without warning.
  * File:2f_2signed_1f_totally_removed.bdoc


**TestCaseID: Document-Validation-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with one unsigned document, named in manifest.
  * Expected Result: The document should fail the validation and warnings should be displayed.
  * File:3f_2signed_1unsigned_all_in_manifest.bdoc


**TestCaseID: Document-Validation-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with one unsigned document, NOT named in manifest.
  * Expected Result: The document should fail the validation and warnings should be displayed.
  * File:3f_2signed_1unsigned_2in_manifest.bdoc


**TestCaseID: Document-Validation-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Bdoc with signed documents.
  * Expected Result: The document should pass the validation without warning.
  * File:2f_all_signed.bdoc

  **Attention! This test is disabled: 


## EuPlugValidationPassIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/EuPlugValidationPassIT.java)


**TestCaseID: EuPlug-ValidationPass-1**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Lithuania adoc-v2.0 signature
  * Expected Result: The document should pass the validation
  * File: Signature-A-LT_MIT-1.asice


**TestCaseID: EuPlug-ValidationPass-2**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation multiple of Lithuania adoc-v2.0 signatures
  * Expected Result: The document should pass the validation
  * File: Signature-A-LT_MIT-2.asice


**TestCaseID: EuPlug-ValidationPass-3**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Lithuania adoc-v2.0 signature with warning
  * Expected Result: The document should pass the validation
  * File: Signature-A-LT_MIT-5.asice"


**TestCaseID: EuPlug-ValidationPass-4**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Latvian edoc-v2.0 signature
  * Expected Result: The document should pass the validation
  * File: Signature-A-LV_EUSO-1.asice


**TestCaseID: EuPlug-ValidationPass-5**

  * TestType: Automated
  * Requirement: []()
  * Title:
  * Expected Result: The document should pass the validation
  * File: Signature-A-LV_EUSO-2.asice


**TestCaseID: EuPlug-ValidationPass-6**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Polish Asic-s with CAdES signature
  * Expected Result: The document should pass the validation
  * File: Signature-A-PL_KIR-1.asics


**TestCaseID: EuPlug-ValidationPass-7**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Polish Asic-s with XAdES signature
  * Expected Result: The document should pass the validation
  * File: Signature-A-PL_KIR-2.asics


**TestCaseID: EuPlug-ValidationPass-8**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Slovakia Asic-e with XAdES signature
  * Expected Result: The document should pass the validation
  * File: Signature-A-SK_DIT-3.asice


**TestCaseID: EuPlug-ValidationPass-10**

  * TestType: Automated
  * Requirement: []()
  * Title:
  * Expected Result: The document should pass the validation
  * File: Signature-C-AT_SIT-1.p7m


**TestCaseID: EuPlug-ValidationPass-11**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of German CAdES signature
  * Expected Result: The document should pass the validation
  * File: Signature-C-DE_SCI-1.p7m


**TestCaseID: EuPlug-ValidationPass-12**

  * TestType: Automated
  * Requirement: []()
  * Title:
  * Expected Result: The document should pass the validation
  * File: Signature-C-ES_MIN-1.p7m


**TestCaseID: EuPlug-ValidationPass-13**

  * TestType: Automated
  * Requirement: []()
  * Title:
  * Expected Result: The document should pass the validation
  * File: Signature-C-ES_MIN-2.p7m


**TestCaseID: EuPlug-ValidationPass-14**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Italian Cades signatures
  * Expected Result: The document should pass the validation
  * File: Signature-C-IT_BIT-5.p7m


**TestCaseID: EuPlug-ValidationPass-15**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Poland CAdES B signature
  * Expected Result: The document should pass the validation
  * File: Signature-C-PL_ADS-4.p7m


**TestCaseID: EuPlug-ValidationPass-16**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Poland CAdES T signature
  * Expected Result: The document should pass the validation
  * File: Signature-C-PL_ADS-7.p7m


**TestCaseID: EuPlug-ValidationPass-17**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Belgium PAdES B signature
  * Expected Result: The document should pass the validation
  * File: Signature-P-BE_CONN-1.pdf


**TestCaseID: EuPlug-ValidationPass-18**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Belgian PAdES LTA signature
  * Expected Result: The document should pass the validation
  * File: Signature-P-BE_CONN-7.pdf


**TestCaseID: EuPlug-ValidationPass-19**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of German PAdES signature
  * Expected Result: The document should pass the validation
  * File: Signature-P-DE_SCI-2.pdf


**TestCaseID: EuPlug-ValidationPass-20**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Italian PAdES signature
  * Expected Result: The document should pass the validation
  * File: Signature-P-IT_MID-1.pdf


**TestCaseID: EuPlug-ValidationPass-21**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Lithuanian PAdES signature
  * Expected Result: The document should pass the validation
  * File: Signature-P-LT_MIT-1.pdf


**TestCaseID: EuPlug-ValidationPass-22**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Lithuanian PAdES signature 2
  * Expected Result: The document should pass the validation
  * File: Signature-P-LT_MIT-2.pdf


**TestCaseID: EuPlug-ValidationPass-23**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Latvian PAdES signature
  * Expected Result: The document should pass the validation
  * File: Signature-P-LV_EUSO-1.pdf


**TestCaseID: EuPlug-ValidationPass-24**

  * TestType: Automated
  * Requirement: []()
  * Title:
  * Expected Result: The document should pass the validation
  * File: Signature-P-LV_EUSO-2.pdf


**TestCaseID: EuPlug-ValidationPass-25**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Polish PAdES signature
  * Expected Result: The document should pass the validation
  * File: Signature-P-PL_ADS-6.pdf


**TestCaseID: EuPlug-ValidationPass-26**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Polish PAdES QES signature
  * Expected Result: The document should pass the validation
  * File: Signature-P-PL_ADS-8.pdf


**TestCaseID: EuPlug-ValidationPass-27**

  * TestType: Automated
  * Requirement: []()
  * Title:
  * Expected Result: The document should pass the validation
  * File: Signature-A-LT_MIT-1.asice


**TestCaseID: EuPlug-ValidationPass-28**

  * TestType: Automated
  * Requirement: []()
  * Title:
  * Expected Result: The document should pass the validation
  * File: Signature-A-LT_MIT-1.asice


**TestCaseID: EuPlug-ValidationPass-29**

  * TestType: Automated
  * Requirement: []()
  * Title: Validation of Belgian XAdES signature
  * Expected Result: The document should pass the validation
  * File: Signature-X-BE_CONN-1.xml


**TestCaseID: EuPlug-ValidationPass-30**

  * TestType: Automated
  * Requirement: []()
  * Title:
  * Expected Result: The document should pass the validation
  * File: Signature-X-BE_CONN-21.xml


## LargeFileIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/LargeFileIT.java)


**TestCaseID: PDF-LargeFiles-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/overview/#main-features-of-siva-validation-service](http://open-eid.github.io/SiVa/siva2/overview/#main-features-of-siva-validation-service)
  * Title: 9MB PDF files (PAdES Baseline LT).
  * Expected Result: Validation report is returned
  * File: 9MB_PDF.pdf


**TestCaseID: Bdoc-LargeFiles-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/overview/#main-features-of-siva-validation-service](http://open-eid.github.io/SiVa/siva2/overview/#main-features-of-siva-validation-service)
  * Title: 9MB ASIC-E file
  * Expected Result: Validation report is returned
  * File: 9MB_BDOC-TS.bdoc


**TestCaseID: Bdoc-LargeFiles-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/overview/#main-features-of-siva-validation-service](http://open-eid.github.io/SiVa/siva2/overview/#main-features-of-siva-validation-service)
  * Title: 9MB BDOC-TM
  * Expected Result: Validation report is returned
  * File: 9MB_BDOC-TM.bdoc


**TestCaseID: Ddoc-LargeFiles-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/overview/#main-features-of-siva-validation-service](http://open-eid.github.io/SiVa/siva2/overview/#main-features-of-siva-validation-service)
  * Title: 9MB DDOC
  * Expected Result: Validation report is returned
  * File: 9MB_DDOC.ddoc


## MonitoringIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/MonitoringIT.java)


**TestCaseID: WebApp-Monitoring-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#service-health-monitoring](http://open-eid.github.io/SiVa/siva2/interfaces/#service-health-monitoring)
  * Title: Health monitor response structure
  * Expected Result: response matches the expected structure of JSON
  * File: not relevant


## PdfBaselineProfileIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/PdfBaselineProfileIT.java)


**TestCaseID: PDF-BaselineProfile-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: The PDF has PAdES-B profile signature polv3
  * Expected Result: Document validation should fail as the profile is not supported with any policy
  * File: hellopades-pades-b-sha256-auth.pdf


**TestCaseID: PDF-BaselineProfile-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: The PDF has PAdES-T profile signature polv3
  * Expected Result: Document validation should fail with any policy
  * File: pades-baseline-t-live-aj.pdf


**TestCaseID: PDF-BaselineProfile-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: The PDF has PAdES-LT profile signature polv3
  * Expected Result: Document validation should pass with any policy
  * File: hellopades-pades-lt-sha256-sign.pdf


**TestCaseID: PDF-BaselineProfile-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: The PDF has PAdES-LT profile signature polv4
  * Expected Result: Document validation should pass with any policy
  * File: hellopades-pades-lt-sha256-sign.pdf


**TestCaseID: PDF-BaselineProfile-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: The PDF has PAdES-LTA profile signature polv3
  * Expected Result: Document validation should pass with any policy
  * File: pades-baseline-lta-live-aj.pdf


**TestCaseID: PDF-BaselineProfile-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: The PDF has PAdES-LTA profile signature polv4
  * Expected Result: Document validation should pass with any policy
  * File: pades-baseline-lta-live-aj.pdf


**TestCaseID: PDF-BaselineProfile-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: The PDF has PAdES-LT and B profile signature
  * Expected Result: Document validation should fail
  * File: hellopades-lt-b.pdf


**TestCaseID: PDF-BaselineProfile-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: PDF document message digest attribute value does not match calculate value
  * Expected Result: Document validation should fail
  * File: hellopades-lt1-lt2-wrongDigestValue.pdf


**TestCaseID: PDF-BaselineProfile-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: PDF file with a serial signature
  * Expected Result: Document signed with multiple signers with serial signatures should pass
  * File: hellopades-lt1-lt2-Serial.pdf


## PdfSignatureCryptographicAlgorithmIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/PdfSignatureCryptographicAlgorithmIT.java)


**TestCaseID: PDF-SigCryptoAlg-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: SHA512 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with SHA512 algorithm should pass
  * File: hellopades-lt-sha512.pdf


**TestCaseID: PDF-SigCryptoAlg-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: SHA1 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with SHA1 algorithm should pass
  * File: hellopades-lt-sha1.pdf


**TestCaseID: PDF-SigCryptoAlg-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: ECDSA224 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with ECDSA224 algorithm should pass
  * File: hellopades-lt-sha256-ec224.pdf


**TestCaseID: PDF-SigCryptoAlg-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: ECDSA256 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with ECDSA256 algorithm should pass
  * File: hellopades-lt-sha256-ec256.pdf


**TestCaseID: PDF-SigCryptoAlg-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: RSA1024 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with RSA1024 algorithm should pass
  * File: hellopades-lt-sha256-rsa1024.pdf


**TestCaseID: PDF-SigCryptoAlg-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: RSA1023 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with RSA1023 algorithm should fail
  * File: hellopades-lt-sha256-rsa1023.pdf


**TestCaseID: PDF-SigCryptoAlg-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: RSA2047 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with RSA2047 algorithm should pass
  * File: hellopades-lt-sha256-rsa2047.pdf


**TestCaseID: PDF-SigCryptoAlg-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: RSA2048 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with RSA2048 algorithm should pass
  * File: PdfValidSingleSignature


## PdfValidationFailIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/PdfValidationFailIT.java)


**TestCaseID: PDF-ValidationFail-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: The PDF-file has been signed with expired certificate (PAdES Baseline T)
  * Expected Result: Document signed with certificate that is expired should fail.
  * File: hellopades-lt-rsa1024-sha1-expired.pdf


**TestCaseID: PDF-ValidationFail-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: The PDF-file has been signed with revoked certificate (PAdES Baseline LT)
  * Expected Result: Document signed with certificate that is revoked should fail.
  * File: pades_lt_revoked.pdf


**TestCaseID: PDF-ValidationFail-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: The PDF-file has been signed with certificate which has no non repudiation key usage attribute (PAdES Baseline LT)
  * Expected Result: The PDF-file validation should fail with error.
  * File: hellopades-pades-lt-sha256-auth.pdf


**TestCaseID: PDF-ValidationFail-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
  * Expected Result: Document signed with expired certificate should fail
  * File: hellopades-lt-sha256-rsa2048-expired.pdf

  **Attention! This test is disabled: Needs new test file


**TestCaseID: PDF-ValidationFail-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
  * Expected Result: Document signed with expired certificate should fail
  * File: hellopades-lt-sha256-rsa1024-expired2.pdf

  **Attention! This test is disabled: le needed


**TestCaseID: PDF-ValidationFail-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: OCSP is taken more than 24h after TS
  * Expected Result: Document signed with expired certificate should fail
  * File: hellopades-lt-sha256-rsa1024-expired2.pdf


**TestCaseID: PDF-ValidationFail-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: CRL is out of thisUpdate and nextUpdate range
  * Expected Result: Validation should fail
  * File: pades-lt-CRL-taken-days-later.pdf


## PdfValidationPassIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/PdfValidationPassIT.java)


**TestCaseID: PDF-ValidationPass-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: The PDF-file has been signed with certificate that is expired after signing (PAdES Baseline LT)
  * Expected Result: Document signed with certificate that expired after signing should pass.
  * File: hellopades-lt-sha256-rsa1024-not-expired.pdf


**TestCaseID: PDF-ValidationPass-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: The PDF-file has been signed with certificate that will expire in 7 days after signing (PAdES Baseline LT)
  * Expected Result: Document signed with certificate that expired after signing should pass.
  * File: hellopades-lt-sha256-rsa2048-7d.pdf


**TestCaseID: PDF-ValidationPass-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Pdf with single valid signature
  * Expected Result: Document should pass.
  * File: PdfValidSingleSignature.pdf


**TestCaseID: PDF-ValidationPass-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: The PDF-file has OCSP more than 15 minutes after TS but earlier than 24h
  * Expected Result: Warning should be returned but validation should pass
  * File: hellopades-lt-sha256-ocsp-15min1s.pdf


**TestCaseID: PDF-ValidationPass-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: The PDF-file has OCSP almost 24h before TS
  * Expected Result: Warning should be returned but validation should pass
  * File: hellopades-lt-sha256-rsa2048-ocsp-before-ts.pdf


## ReportSignatureIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/ReportSignatureIT.java)


**TestCaseID: Detailed-Report-Signature-1**

  * TestType: Auto
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Simple report signature should not be in response
  * Expected Result: Simple report response should not contain signature
  * File: hellopades-pades-lt-sha256-sign.pdf


**TestCaseID: Detailed-Report-Signature-2**

  * TestType: Auto
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Detailed report signature should be in response
  * Expected Result: Detailed report response should contain signature
  * File: hellopades-pades-lt-sha256-sign.pdf


**TestCaseID: Detailed-Report-Signature-3**

  * TestType: Auto
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature
  * Expected Result: Signed report is successfully validated
  * File: hellopades-lt-sha256-rsa2048.pdf


**TestCaseID: Detailed-Report-Signature-4**

  * TestType: Auto
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: In simple report reportSignatureEnabled parameter value true
  * Expected Result: File hash in hex in report
  * File: hellopades-lt-sha256-rsa2048.pdf


**TestCaseID: Detailed-Report-Signature-5**

  * TestType: Auto
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: In simple report reportSignatureEnabled parameter value false
  * Expected Result: File hash in hex not in report
  * File: hellopades-lt-sha256-rsa2048.pdf

  **Attention! This test is disabled: est should be ran manually after configuring the report signature feature


## SignaturePolicyIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/SignaturePolicyIT.java)


**TestCaseID: POLv4-Signature-Policy-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The PDF-file is Ades level
  * Expected Result: Signatures are invalid according to policy
  * File: soft-cert-signature.pdf


**TestCaseID: POLv4-Signature-Policy-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The bdoc is Ades level
  * Expected Result: Signatures are invalid according to policy
  * File: allkiri_ades.asice


**TestCaseID: POLv4-Signature-Policy-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The bdoc is Ades signature
  * Expected Result: Signatures are invalid according to policy
  * File:

  **Attention! This test is disabled: test file needed


**TestCaseID: POLv4-Signature-Policy-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The asice is Ades signature
  * Expected Result: Signatures are invalid according to policy
  * File:

  **Attention! This test is disabled: test file needed


**TestCaseID: POLv4-Signature-Policy-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The bdoc is Ades seal
  * Expected Result: Signatures are invalid according to policy
  * File:

  **Attention! This test is disabled: test file needed


**TestCaseID: POLv4-Signature-Policy-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The asice is ades seal
  * Expected Result: Signatures are valid according to policy
  * File:

  **Attention! This test is disabled: test file needed


**TestCaseID: POLv4-Signature-Policy-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The certificate is QC level signature, but do not have SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy, warning is returned about signature level
  * File: testAdesQC.asice


**TestCaseID: POLv4-Signature-Policy-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The certificate is QC level signature, but do not have SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy, warning is returned about signature level
  * File: testAdesQC.asice


**TestCaseID: POLv4-Signature-Policy-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The certificate is QC level seal, but do not have SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy, no warning about the signature level
  * File: IB-4828_tempel_not_qscd_TS.asice


**TestCaseID: POLv4-Signature-Policy-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The certificate is QC level seal, but do not have SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy, no warning about the signature level
  * File: IB-4828_tempel_not_qscd_TM.bdoc


**TestCaseID: POLv4-Signature-Policy-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The certificate is QC level, but do not have SSCD/QSCD compliance and type identifier
  * Expected Result: Signatures are valid according to policy, no warning about the signature level
  * File:

  **Attention! This test is disabled: file needed


**TestCaseID: POLv4-Signature-Policy-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The certificate is QC level, but do not have SSCD/QSCD compliance and type identifier
  * Expected Result: Signatures are valid according to policy, no warning about the signature level
  * File:

  **Attention! This test is disabled: file needed


**TestCaseID: POLv4-Signature-Policy-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The bdoc is QES level signature
  * Expected Result: Signatures are valid according to policy
  * File: Valid_ID_sig.bdoc


**TestCaseID: POLv4-Signature-Policy-14**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The bdoc is QES level signature
  * Expected Result: Signatures are valid according to policy
  * File: ValidLiveSignature.asice


**TestCaseID: POLv4-Signature-Policy-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The bdoc is QES level seal
  * Expected Result: Signatures are valid according to policy
  * File: IB-4828_tempel_qscd_TM.bdoc


**TestCaseID: POLv4-Signature-Policy-16**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The asice is QES level seal
  * Expected Result: Signatures are valid according to policy
  * File: IB-4828_tempel_qscd_TS.asice


**TestCaseID: POLv4-Signature-Policy-17**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The bdoc is QES level
  * Expected Result: Signatures are valid according to policy
  * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc


**TestCaseID: POLv4-Signature-Policy-18**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The asice is QES level
  * Expected Result: Signatures are valid according to policy
  * File: EE_SER-AEX-B-LT-V-28.asice


**TestCaseID: POLv3-Signature-Policy-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3)
  * Title: The PDF-file is Ades level
  * Expected Result: Signatures are not valid according to policy
  * File: soft-cert-signature.pdf


**TestCaseID: POLv3-Signature-Policy-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3)
  * Title: The bdoc is Ades level
  * Expected Result: Signatures are valid according to policy
  * File: allkiri_ades.asice


**TestCaseID: POLv3-Signature-Policy-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3)
  * Title: The bdoc is ADES level signature
  * Expected Result: Signatures are valid according to policy
  * File: allkiri_ades.asice

  **Attention! This test is disabled: test file needed


**TestCaseID: POLv3-Signature-Policy-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3)
  * Title: The asice is Ades level signature
  * Expected Result: Signatures are valid according to policy
  * File:

  **Attention! This test is disabled: test file needed


**TestCaseID: POLv3-Signature-Policy-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3)
  * Title: The bdoc is Ades seal
  * Expected Result: Signatures are valid according to policy
  * File:

  **Attention! This test is disabled: test file needed


**TestCaseID: POLv3-Signature-Policy-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3)
  * Title: The asice is Ades level seal
  * Expected Result: Signatures are valid according to policy
  * File:

  **Attention! This test is disabled: test file needed


**TestCaseID: POLv3-Signature-Policy-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The certificate is QC level signature, but do not have SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy, warning is returned about signature level
  * File: testAdesQC.asice


**TestCaseID: POLv3-Signature-Policy-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The certificate is QC level signature, but do not have SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy, warning is returned about signature level
  * File: testAdesQC.asice


**TestCaseID: POLv3-Signature-Policy-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The certificate is QC level seal, but do not have SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy, no warning about the signature level
  * File: IB-4828_tempel_not_qscd_TS.asice


**TestCaseID: POLv3-Signature-Policy-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The certificate is QC level seal, but do not have SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy, no warning about the signature level
  * File: IB-4828_tempel_not_qscd_TM.bdoc


**TestCaseID: POLv3-Signature-Policy-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The certificate is QC level, but do not have SSCD/QSCD compliance and type identifier
  * Expected Result: Signatures are valid according to policy, no warning about the signature level
  * File:

  **Attention! This test is disabled: file needed


**TestCaseID: POLv3-Signature-Policy-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The certificate is QC level, but do not have SSCD/QSCD compliance and type identifier
  * Expected Result: Signatures are valid according to policy, no warning about the signature level
  * File:

  **Attention! This test is disabled: file needed


**TestCaseID: POLv3-Signature-Policy-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The bdoc is QES level signature
  * Expected Result: Signatures are valid according to policy
  * File: Valid_ID_sig.bdoc


**TestCaseID: POLv3-Signature-Policy-14**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The bdoc is QES level signature
  * Expected Result: Signatures are valid according to policy
  * File: ValidLiveSignature.asice


**TestCaseID: POLv3-Signature-Policy-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The bdoc is QES level seal
  * Expected Result: Signatures are valid according to policy
  * File: IB-4828_tempel_qscd_TM.bdoc


**TestCaseID: POLv3-Signature-Policy-16**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The asice is QES level seal
  * Expected Result: Signatures are valid according to policy
  * File: IB-4828_tempel_qscd_TS.asice


**TestCaseID: POLv3-Signature-Policy-17**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The bdoc is QES level
  * Expected Result: Signatures are valid according to policy
  * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc


**TestCaseID: POLv3-Signature-Policy-18**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The asice is QES level
  * Expected Result: Signatures are valid according to policy
  * File: EE_SER-AEX-B-LT-V-28.asice


**TestCaseID: Revocation-Signature-Policy-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The PDF-file is missing an OCSP or CRL
  * Expected Result: Signatures are invalid according to policy
  * File: PadesProfileT.pdf


**TestCaseID: Revocation-Signature-Policy-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4](http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4)
  * Title: The PDF-file with included CRL
  * Expected Result: Signatures are valid according to policy
  * File: PadesProfileLtWithCrl.pdf


## XadesHashcodeValidationFailIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/XadesHashcodeValidationFailIT.java)


**TestCaseID: Xades-Hashcode-Validation-Fail-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Data file hash algorithm do not match signature hash algorithm
  * Expected Result: Validation fails
  * File: Valid_XAdES_LT_TM.xml


**TestCaseID: Xades-Hashcode-Validation-Fail-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Hashes do not match
  * Expected Result: Validation fails
  * File: Valid_XAdES_LT_TM.xml


**TestCaseID: Xades-Hashcode-Validation-Fail-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Wrong data file name is used
  * Expected Result: Validation report is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Xades-Hashcode-Validation-Fail-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Invalid signature in XAdES
  * Expected Result: Validation report is returned
  * File: Valid_XAdES_LT_TM.xml


## XadesHashcodeValidationPassIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/XadesHashcodeValidationPassIT.java)


**TestCaseID: Xades-Hashcode-Validation-Pass-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: XAdES extracted from ASICE
  * Expected Result: The document should pass the validation
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Xades-Hashcode-Validation-Pass-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: XAdES extracted from BDOC
  * Expected Result: The document should pass the validation
  * File: Valid_XAdES_LT_TM.xml


**TestCaseID: Xades-Hashcode-Validation-Pass-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: XAdES extracted from BDOC
  * Expected Result: The document should pass the validation
  * File: Valid_XADES_LT_TS_multiple_datafiles.xml


**TestCaseID: Xades-Hashcode-Validation-Pass-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Datafile has + in name
  * Expected Result: The document should pass the validation
  * File: test+document.xml


**TestCaseID: Xades-Hashcode-Validation-Pass-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Datafile has space in name
  * Expected Result: The document should pass the validation
  * File: spacesInDatafile.xml


**TestCaseID: Xades-Hashcode-Validation-Algorithms-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Datafile digest in SHA1
  * Expected Result: The document should pass the validation
  * File: sha224_TS.xml


**TestCaseID: Xades-Hashcode-Validation-Algorithms-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Datafile digest in SHA224
  * Expected Result: The document should pass the validation
  * File: sha224_TS.xml


**TestCaseID: Xades-Hashcode-Validation-Algorithms-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Datafile digest in SHA256
  * Expected Result: The document should pass the validation
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Xades-Hashcode-Validation-Algorithms-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Datafile digest in SHA384
  * Expected Result: The document should pass the validation
  * File: sha384_TS.xml


**TestCaseID: Xades-Hashcode-Validation-Algorithms-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Datafile digest in SHA512
  * Expected Result: The document should pass the validation
  * File: sha512_TS.xml


## XRoadValidationFailIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/XRoadValidationFailIT.java)


**TestCaseID: Xroad-ValidationFail-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Simple xroad document with container fault
  * Expected Result: Document should fail validation
  * File: invalid-digest.asice


## XRoadValidationPassIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/integrationtest/XRoadValidationPassIT.java)


**TestCaseID: Xroad-ValidationPass-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Simple xroad document
  * Expected Result: Document should pass
  * File: xroad-simple.asice


**TestCaseID: Xroad-ValidationPass-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Batchsignature xroad document
  * Expected Result: Document should pass
  * File: xroad-batchsignature.asice


**TestCaseID: Xroad-ValidationPass-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4)
  * Title: Attachment xroad document
  * Expected Result: Document should pass
  * File: xroad-attachment.asice


## DetailedReportValidationManualIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/manualtest/DetailedReportValidationManualIT.java)


**TestCaseID: Detailed-Report-Validation-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: ValidationConclusion element
  * Expected Result: Detailed report includes validationConclusion element
  * File: ValidLiveSignature.asice


**TestCaseID: Detailed-Report-Validation-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: TL analysis element
  * Expected Result: Detailed report includes tlanalysis element and its values
  * File: pades-baseline-lta-live-aj.pdf


**TestCaseID: Detailed-Report-Validation-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Signatures element
  * Expected Result: Detailed report includes signatures element and its sub-elements and its values
  * File: hellopades-lt-sha256-ec256.pdf


**TestCaseID: Detailed-Report-Validation-4**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: basicBuildingBlocks element
  * Expected Result: Detailed report includes basicBuildingBlocks element and its sub-elements and its values
  * File: pades-baseline-lta-live-aj.pdf


**TestCaseID: Detailed-Report-Validation-5**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: BasicBuildingBlocks element
  * Expected Result: Detailed report includes basicBuildingBlocks element and its sub-elements and its values
  * File: pades-baseline-lta-live-aj.pdf


**TestCaseID: Detailed-Report-Validation-6**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: BasicBuildingBlocks element
  * Expected Result: Detailed report includes basicBuildingBlocks element and its sub-elements and its values
  * File: pades-baseline-lta-live-aj.pdf

  **Attention! This test is disabled: The sequence of blocks changes


**TestCaseID: Detailed-Report-Validation-7**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Wrong signature value
  * Expected Result: Detailed report includes wrong signature value
  * File: TS-02_23634_TS_wrong_SignatureValue.asice


**TestCaseID: Detailed-Report-Validation-8**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Wrong data file in manifest
  * Expected Result:
  * File: WrongDataFileInManifestAsics.asics


**TestCaseID: Detailed-Report-Validation-9**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report file hash if ReportSignatureEnabled value true
  * Expected Result: fileHash calculated
  * File: hellopades-lt-sha256-rsa2048.pdf


**TestCaseID: Detailed-Report-Validation-10**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report file hash if ReportSignatureEnabled value false
  * Expected Result: fileHash no calculated
  * File: hellopades-lt-sha256-rsa2048.pdf

  **Attention! This test is disabled: Needs possibility to configure report signing in tests


## DiagnosticReportValidationManualIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/manualtest/DiagnosticReportValidationManualIT.java)


**TestCaseID: Diagnostic-Report-Validation-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: ValidationConclusion element
  * Expected Result: Diagnostic report includes validationConclusion element
  * File: ValidLiveSignature.asice


**TestCaseID: Diagnostic-Report-Validation-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Trusted List element
  * Expected Result: Diagnostic report includes Trust List element and its values
  * File: pades-baseline-lta-live-aj.pdf


**TestCaseID: Diagnostic-Report-Validation-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: List Of Trusted List element
  * Expected Result: Diagnostic report includes lotl element and its values
  * File: pades-baseline-lta-live-aj.pdf


**TestCaseID: Diagnostic-Report-Validation-4**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Signatures element
  * Expected Result: Diagnostic report includes tlanalysis element and its values
  * File: pades-baseline-lta-live-aj.pdf


**TestCaseID: Diagnostic-Report-Validation-5**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Used Certificates
  * Expected Result: Diagnostic report includes used certificates element and its values
  * File: pades-baseline-lta-live-aj.pdf

  **Attention! This test is disabled: 


**TestCaseID: Diagnostic-Report-Validation-6**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Wrong signature value
  * Expected Result: Diagnostic report includes wrong signature value
  * File: TS-02_23634_TS_wrong_SignatureValue.asice


## ReportSignatureManuallT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/manualtest/ReportSignatureManuallT.java)


**TestCaseID: Detailed-Report-Signatures-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_LTA signed with RSA key.
  * Expected Result: validationReportSignature exists in report
  * File: hellopades-lt-sha256-rsa2048.pdf

  **Attention! This test is disabled: 


**TestCaseID: Detailed-Report-Signatures-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_LT signed with RSA key.
  * Expected Result: validationReportSignature exists in report
  * File: hellopades-lt-sha256-rsa2048.pdf

  **Attention! This test is disabled: 


**TestCaseID: Detailed-Report-Signatures-4**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_T signed with RSA key.
  * Expected Result: validationReportSignature exists in report
  * File: hellopades-lt-sha256-rsa2048.pdf

  **Attention! This test is disabled: 


**TestCaseID: Detailed-Report-Signatures-5**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_B signed with RSA key.
  * Expected Result: validationReportSignature exists in report
  * File: hellopades-lt-sha256-rsa2048.pdf

  **Attention! This test is disabled: 


**TestCaseID: Detailed-Report-Signatures-6**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_LTA and signed with ECC key.
  * Expected Result: validationReportSignature exists in report
  * File: hellopades-lt-sha256-rsa2048.pdf

  **Attention! This test is disabled: 


**TestCaseID: Detailed-Report-Signatures-7**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_LT and signed with ECC key.
  * Expected Result: validationReportSignature exists in report
  * File: hellopades-lt-sha256-rsa2048.pdf

  **Attention! This test is disabled: 


**TestCaseID: Detailed-Report-Signatures-8**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_T and signed with ECC key.
  * Expected Result: validationReportSignature exists in report
  * File: hellopades-lt-sha256-rsa2048.pdf

  **Attention! This test is disabled: 


**TestCaseID: Detailed-Report-Signatures-9**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_B and signed with ECC key.
  * Expected Result: validationReportSignature exists in report
  * File: hellopades-lt-sha256-rsa2048.pdf

  **Attention! This test is disabled: 


**TestCaseID: Detailed-Report-Signatures-10**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature when configuration parameter OcspUrl empty
  * Expected Result: No signature
  * File: hellopades-lt-sha256-rsa2048.pdf


**TestCaseID: Detailed-Report-Signatures-11**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature when configuration parameter TspUrl empty
  * Expected Result: No signature
  * File: hellopades-lt-sha256-rsa2048.pdf


**TestCaseID: Detailed-Report-Signatures-12**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature when configuration parameter Pkcs11 wrong value
  * Expected Result: Error message
  * File: hellopades-lt-sha256-rsa2048.pdf

  **Attention! This test is disabled: 


**TestCaseID: Detailed-Report-Signatures-13**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature when configuration parameter SignatureLevel empty
  * Expected Result: No Signature
  * File: hellopades-lt-sha256-rsa2048.pdf


**TestCaseID: Detailed-Report-Signatures-14**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Validate detailed report signature when using pks11 to sign
  * Expected Result: Signature exists
  * File: hellopades-lt-sha256-rsa2048.pdf

  **Attention! This test is disabled: 


## StatisticsToLogsManualIT.java

!!! Note

    All tests in this class expect manual verification of responses in log! The tests are made to prepare test data and ease the test execution.

**TestCaseID: Bdoc-Statistics-Log-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Bdoc valid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Bdoc-Statistics-Log-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Bdoc invalid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: SS-4_teadmataCA.4.asice


**TestCaseID: Bdoc-Statistics-Log-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Bdoc not supported file is inserted
  * Expected Result: No message in statistics as the container is not validated
  * File: xroad-simple.asice


**TestCaseID: Bdoc-Statistics-Log-4**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Bdoc with certificates from different countries.
  * Expected Result: Correct data is shown in the log with correct structure
  * File: Baltic MoU digital signing_EST_LT_LV.bdoc


**TestCaseID: Ddoc-Statistics-Log-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Ddoc valid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: igasugust1.3.ddoc


**TestCaseID: Ddoc-Statistics-Log-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Ddoc invalid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: ilma_kehtivuskinnituseta.ddoc


**TestCaseID: Ddoc-Statistics-Log-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Ddoc not supported file is inserted
  * Expected Result: No message in statistics as the container is not validated
  * File: xroad-simple.asice


**TestCaseID: Ddoc-Statistics-Log-4**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Ddoc with certificates from different countries.
  * Expected Result: Correct data is shown in the log with correct structure
  * File: Belgia_kandeavaldus_LIV.ddoc

  **Attention! This test is disabled: 2-126")


**TestCaseID: Pdf-Statistics-Log-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Pdf valid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: pades_lt_two_valid_sig.pdf


**TestCaseID: Pdf-Statistics-Log-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Pdf invalid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: hellopades-lt1-lt2-wrongDigestValue.pdf


**TestCaseID: Pdf-Statistics-Log-4**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Pdf with certificates from non Estonian countries.
  * Expected Result: Correct data is shown in the log with correct structure
  * File: Regulatione-signedbyco-legislators.pdf


**TestCaseID: Xroad-Statistics-Log-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Xroad valid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: xroad-batchsignature.asice


**TestCaseID: Xroad-Statistics-Log-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Xroad invalid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: invalid-digest.asice


**TestCaseID: Xroad-Statistics-Log-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: Xroad not supported file is inserted
  * Expected Result: No message in statistics as the container is not validated
  * File: BDOC-TS.bdoc


**TestCaseID: Asics-Statistics-Log-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: ASiCs valid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: ValidBDOCinsideAsics.asics


**TestCaseID: Asics-Statistics-Log-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics_v3.pdf)
  * Title: asics invalid container is validated
  * Expected Result: No message in statistics as the container is not validated
  * File: TwoDataFilesAsics.asics


## GetDataFileRequestIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/resttest/GetDataFileRequestIT.java)


**TestCaseID: Get-Data-Files-Request-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Input empty values
  * Expected Result: Errors are returned stating the missing values
  * File: not relevant


**TestCaseID: Get-Data-Files-Request-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Totally empty request body is sent
  * Expected Result: Errors are given
  * File: not relevant


**TestCaseID: Get-Data-Files-Request-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Order of elements is changed in request
  * Expected Result: Order of  elements is ignored, no error messages in response
  * File: susisevad1_3.ddoc


**TestCaseID: Get-Data-Files-Request-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Additional elements are added
  * Expected Result: Added elements ignored, no error messages and  additional elements in response
  * File: susisevad1_3.ddoc


**TestCaseID: Get-Data-Files-Request-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Mandatory element 'documentType' is duplicated, duplicated element with bdoc value
  * Expected Result: Error message is returned
  * File: susisevad1_3.ddoc


**TestCaseID: Get-Data-Files-Request-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Mandatory element 'document' is deleted
  * Expected Result: Errors returned stating the missing element
  * File: susisevad1_3.ddoc


**TestCaseID: Get-Data-Files-Request-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Low case for document type is used
  * Expected Result: Data file is returned, no error messages
  * File: susisevad1_3.ddoc


**TestCaseID: Get-Data-Files-Request-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Document Type is changed in request to BDOC, actual is DDOC
  * Expected Result: Error is returned
  * File: susisevad1_3.ddoc


**TestCaseID: Get-Data-Files-Request-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Document Type is changed in request to PDF, actual is DDOC
  * Expected Result: Error is returned
  * File: susisevad1_3.ddoc


**TestCaseID: Get-Data-Files-Request-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Document Type is changed in request to unsupported format (JPG), actual is DDOC
  * Expected Result: Error is returned
  * File: susisevad1_3.ddoc


**TestCaseID: Get-Data-Files-Request-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Document Type is changed in request to XROAD format, actual is DDOC
  * Expected Result: Error is returned
  * File: susisevad1_3.ddoc


**TestCaseID: Get-Data-Files-Request-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Document Type is changed in request to DDOC format, actual is BDOC
  * Expected Result: Error is returned
  * File: BDOC-TS.bdoc


**TestCaseID: Get-Data-Files-Request-14**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Document Type is changed in request to DDOC format, actual is PDF
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: Get-Data-Files-Request-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: For Unsupported format Document Type is changed in request to DDOC format, actual is PNG
  * Expected Result: Error is returned
  * File: Picture.png


## HashcodeValidationRequestIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/resttest/HashcodeValidationRequestIT.java)


**TestCaseID: Hascode-Validation-Request-Report-Type-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Input correct values for simple report
  * Expected Result: Simple report is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Report-Type-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Input correct values for detailed report
  * Expected Result: Simple report is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Report-Type-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Report type missing
  * Expected Result: Default is used
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Report-Type-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Report type case sensitivity
  * Expected Result: Report type is case insensitive
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Report-Type-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Report type is invalid
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Report-Type-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Input correct values for detailed report
  * Expected Result: Simple report is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Policy-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Signature policy POLv3
  * Expected Result: Correct policy is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Policy-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Signature policy POLv4
  * Expected Result: Correct policy is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Policy-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Signature policy missing
  * Expected Result: Default is used
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Policy-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Signature policy is invalid
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Policy-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Incorrect signature policy format
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Policy-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Signature policy is empty
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Policy-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Signature policy too long
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Policy-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Double signature policy
  * Expected Result: Last value is used
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Signature-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Input missing signature files
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Signature-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Input missing signature file
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Signature-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Input incorrect signature
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Signature-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Input file without signature
  * Expected Result: Validation report is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Signature-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Not correct file type
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Datafiles-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Data files not in request
  * Expected Result: Simple report is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Datafiles-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Empty data files list
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Datafiles-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Data file filename missing
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Datafiles-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Data file filename empty
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Datafiles-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Data file filename too long
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Datafiles-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Data file invalid hash algorithm
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Datafiles-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Data file hash algorithm case sensitivity
  * Expected Result: Validation report is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Datafiles-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Data file hash missing
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Datafiles-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Data file hash empty
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Datafiles-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Data file hash wrong format
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Datafiles-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Hash too long
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Policy-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Double fields in datafile object
  * Expected Result: Last value is used
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Datafiles-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Excess data files are ignored
  * Expected Result: Validation report is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Hascode-Validation-Request-Signatures-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Several signatures validated
  * Expected Result: Validation report is returned
  * File: multiple files


**TestCaseID: Hascode-Validation-Request-Signatures-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Several signatures validated with datafile info
  * Expected Result: Validation report is returned
  * File: multiple files


**TestCaseID: Hascode-Validation-Request-Signatures-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Several signatures validated one signature not valid
  * Expected Result: Validation report is returned
  * File: multiple files


## ValidationReportValueVerificationIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/resttest/ValidationReportValueVerificationIT.java)


**TestCaseID: Bdoc-ValidationReportVerification-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface#validation-request-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: Valid_ID_sig.bdoc


**TestCaseID: Bdoc-ValidationReportVerification-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23635_bdoc_ts_OCSP_random_nonce.bdoc


**TestCaseID: Bdoc-ValidationReportVerification-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc


**TestCaseID: Bdoc-ValidationReportVerification-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT-TM, AdESqc
  * Expected Result: All required elements are present and meet the expected values.
  * File: testAdesQCInvalid.asice


**TestCaseID: Bdoc-ValidationReportVerification-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (Bdoc valid multiple signatures)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: Baltic MoU digital signing_EST_LT_LV.bdoc


**TestCaseID: Bdoc-ValidationReportVerification-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (Bdoc indeterminate status)
  * Expected Result: All required elements are present according to BdocDocSimpleReportSchema.json
  * File: SS-4_teadmataCA.4.asice

  **Attention! This test is disabled: needs investigation why the signature is determined as XAdES_BASELINE_T not as XAdES_BASELINE_LT_TM


**TestCaseID: Bdoc-ValidationReportVerification-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title:  Bdoc report with no signatures
  * Expected Result: Report is returned with required elements
  * File:BdocContainerNoSignature.bdoc


**TestCaseID: Pdf-ValidationReportVerification-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (Pdf valid single signature)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: hellopades-lt-sha256-ec256.pdf


**TestCaseID: Pdf-ValidationReportVerification-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (Pdf valid Multiple signatures)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: pades_lt_two_valid_sig.pdf


**TestCaseID: Pdf-ValidationReportVerification-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (Pdf invalid signature)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: hellopades-lt-b.pdf


**TestCaseID: Pdf-ValidationReportVerification-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (Pdf indeterminate status)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: hellopades-lt-rsa1024-sha1-expired.pdf


**TestCaseID: Pdf-ValidationReportVerification-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title:  Pdf report with no signatures
  * Expected Result: Report is returned with required elements
  * File: PdfNoSignature.pdf


**TestCaseID: Ddoc-ValidationReportVerification-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (ddoc valid single signature)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: DIGIDOC-XML1.3.ddoc


**TestCaseID: Ddoc-ValidationReportVerification-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (ddoc invalid signature)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: multipleInvalidSignatures.ddoc


**TestCaseID: Ddoc-ValidationReportVerification-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Check for optional warning element
  * Expected Result: Warning element is present
  * File: 18912.ddoc


**TestCaseID: Ddoc-ValidationReportVerification-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title:  Ddoc report with no signatures
  * Expected Result: Report is returned with required elements
  * File: DdocContainerNoSignature.ddoc


**TestCaseID: Ddoc-ValidationReportVerification-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.0, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: SK-XML1.0.ddoc

  **Attention! This test is disabled: DDOC 1.0 fails in Travis CI. Needs investigation


**TestCaseID: Ddoc-ValidationReportVerification-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.1, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.1.ddoc


**TestCaseID: Ddoc-ValidationReportVerification-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.2, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.2.ddoc


**TestCaseID: Ddoc-ValidationReportVerification-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.3, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.3.ddoc


**TestCaseID: Xroad-ValidationReportVerification-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xroad-simple container
  * Expected Result: Report is returned with required elements
  * File: xroad-simple.asice


**TestCaseID: Xroad-ValidationReportVerification-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xroad-batchsignature container
  * Expected Result: Report is returned with required elements
  * File: xroad-batchsignature.asice


**TestCaseID: Xroad-ValidationReportVerification-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xroad-attachment container
  * Expected Result: Report is returned with required elements
  * File: xroad-attachment.asice


**TestCaseID: Xroad-ValidationReportVerification-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report with invalid xroad container
  * Expected Result: Report is returned with required elements
  * File: xroad-attachment.asice


## ValidationRequestIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/resttest/ValidationRequestIT.java)


**TestCaseID: ValidationRequest-Parameters-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Happy path valid mandatory input test
  * Expected Result: Validation report is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Happy path valid all input test
  * Expected Result: Validation report is returned
  * File: xroad-simple.asice


**TestCaseID: ValidationRequest-Parameters-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Totally empty request body is sent
  * Expected Result: Error is given
  * File: not relevant


**TestCaseID: ValidationRequest-Parameters-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Input empty values
  * Expected Result: Errors are returned stating the missing values
  * File: not relevant


**TestCaseID: ValidationRequest-Parameters-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with more parameters than expected is sent
  * Expected Result: Error is given or extra parameters are ignored?
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request has invalid keys (capital letters)
  * Expected Result: Error is returned stating wrong values
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Document parameter is missing
  * Expected Result: Errors are returned stating missing Document values
  * File: not relevant


**TestCaseID: ValidationRequest-Parameters-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Filename parameter is missing
  * Expected Result: Errors are returned stating the missing values
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Happy path valid file encoding format
  * Expected Result: Validation report is returned
  * File: xroad-simple.asice


**TestCaseID: ValidationRequest-Parameters-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Happy path valid file encoding format
  * Expected Result: Validation report is returned
  * File: xroad-simple.asice


**TestCaseID: ValidationRequest-Parameters-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with not base64 string as document
  * Expected Result: Error is returned stating encoding problem
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Filename field should be case insensitive
  * Expected Result: Validation report is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Filename field should ignore spaces
  * Expected Result: Validation report is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Filename is in allowed length
  * Expected Result: Report is returned with the same filename
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-16**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Filename is over allowed length
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-17**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: DocumentType parameter is missing
  * Expected Result: Document type is selected automatically and validation report is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-18**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: DocumentType parameter is XROAD
  * Expected Result: Xroad validatior is used for validation and report is returned
  * File: xroad-simple.asice


**TestCaseID: ValidationRequest-Parameters-19**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: DocumentType parameter is XROAD
  * Expected Result: Xroad validatior is used for validation and report is returned
  * File: xroad-simple.asice


**TestCaseID: ValidationRequest-Parameters-20**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: SignaturePolicy parameter is missing, default is used
  * Expected Result: Validation report is returned with POLv4
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-21**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: SignaturePolicy is set as POLv3
  * Expected Result: Validation report is returned with POLv3
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-22**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: SignaturePolicy is set as POLv4
  * Expected Result: Validation report is returned with POLv4
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-23**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: SignaturePolicy has not allowed value
  * Expected Result: error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-24**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: ReportType parameter is missing
  * Expected Result: Simple report is returned as default
  * File: TS-11_23634_TS_2_timestamps.asice


**TestCaseID: ValidationRequest-Parameters-25**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: ReportType parameter Simple
  * Expected Result: Simple report is returned
  * File: TS-11_23634_TS_2_timestamps.asice


**TestCaseID: ValidationRequest-Parameters-26**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: ReportType parameter Detailed
  * Expected Result: Detailed report is returned
  * File: TS-11_23634_TS_2_timestamps.asice


**TestCaseID: ValidationRequest-Parameters-27**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: ReportType parameter is invalid
  * Expected Result: Error is returned
  * File: xroad-simple.asice


**TestCaseID: ValidationRequest-Parameters-28**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Filename is under allowed length
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-29**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: SignaturePolicy is under allowed length
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-30**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Parameters-31**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: ReportType parameter Diagnostic
  * Expected Result: Diagnostic report is returned
  * File: TS-11_23634_TS_2_timestamps.asice


**TestCaseID: ValidationRequest-Validator-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Mismatch in stated and actual document (bdoc and pdf)
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: ValidationRequest-Validator-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as bdoc
  * Expected Result: Error is returned stating problem in document
  * File: not relevant


**TestCaseID: ValidationRequest-Validator-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Acceptance of ASICE as BDOC document type
  * Expected Result: Asice files are handled the same as bdoc
  * File: bdoc21-TS.asice


**TestCaseID: ValidationRequest-Validator-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as pdf
  * Expected Result: Error is returned stating problem in document
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Validator-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Mismatch in stated and actual document (ddoc and bdoc)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-Validator-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as ddoc
  * Expected Result: Error is returned stating problem in document
  * File: not relevant


**TestCaseID: ValidationRequest-Validator-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Mismatch in stated and actual document (xroad and ddoc)
  * Expected Result: Error is returned
  * File: igasugust1.3.ddoc


**TestCaseID: ValidationRequest-Validator-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as document with xroad document type
  * Expected Result: Error is returned stating problem in document
  * File: not relevant


## SoapGetDataFileReportIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/soaptest/SoapGetDataFileReportIT.java)


**TestCaseID: SoapGetDataFile-Report-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-response-interface)
  * Title: Verification of values in Validation Report, checks for  File name, Base64, Mimetype and Size
  * Expected Result: All required elements are present and meet the expected values
  * File: ddoc_1_3.xml.ddoc


**TestCaseID: SoapGetDataFile-Report-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-response-interface)
  * Title: Verification of values in Validation Report xml v1.1, checks for  File name, Base64, Mimetype and Size
  * Expected Result: All required elements are present and meet the expected values
  * File: DIGIDOC-XML1.1.ddoc


**TestCaseID: SoapGetDataFile-Report-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-response-interface)
  * Title: Verification of values in Validation Report xml v1.2, checks for  File name, Base64, Mimetype and Size
  * Expected Result: All required elements are present and meet the expected values
  * File: DIGIDOC-XML1.2.ddoc


**TestCaseID: SoapGetDataFile-Report-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-response-interface)
  * Title: Verification of values in Validation Report for container with a number of files, checks for  File name, Base64, Mimetype and Size for each file
  * Expected Result: All required elements are present and meet the expected values
  * File: igasugust1.3.ddoc

  **Attention! This test is disabled: Run this test manually as it fails in Travis because of big response data


## SoapGetDataFileRequestIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/soaptest/SoapGetDataFileRequestIT.java)


**TestCaseID: Soap-Validation-Request-For-Data-Files-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Input empty values
  * Expected Result: Error is returned
  * File: not relevant


**TestCaseID: Soap-Validation-Request-For-Data-Files-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Input empty body
  * Expected Result: Error is returned
  * File: not relevant


**TestCaseID: Soap-Validation-Request-For-Data-Files-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Element DocumentType is removed from the body
  * Expected Result: Error is returned
  * File: ddoc_1_3.xml.ddoc


**TestCaseID: Soap-Validation-Request-For-Data-Files-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Element Document is removed from the body
  * Expected Result: Error is returned
  * File: ddoc_1_3.xml.ddoc


**TestCaseID: Soap-Validation-Request-For-Data-Files-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Element DocumentType is duplicated in the body with different value
  * Expected Result: Error is returned
  * File: ddoc_1_3.xml.ddoc


**TestCaseID: Soap-Validation-Request-For-Data-Files-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Additional element Filename is added to  the body
  * Expected Result: Error is returned
  * File: ddoc_1_3.xml.ddoc


**TestCaseID: Soap-Validation-Request-For-Data-Files-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Value for element Document was changed
  * Expected Result: Error is returned
  * File: ddoc_1_3.xml.ddoc


**TestCaseID: Soap-Validation-Request-For-Data-Files-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title:  DocumentType was changed to BDOC when actual is DDOC
  * Expected Result: Error is returned
  * File: 18912.ddoc


**TestCaseID: Soap-Validation-Request-For-Data-Files-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title:  DocumentType was changed to PDF when actual is DDOC
  * Expected Result: Error is returned
  * File: 18912.ddoc


**TestCaseID: Soap-Validation-Request-For-Data-Files-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title:  DocumentType was changed to XROAD when actual is DDOC
  * Expected Result: Error is returned
  * File: 18912.ddoc


**TestCaseID: Soap-Validation-Request-For-Data-Files-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title:  DocumentType was changed to unsupported format JPG when actual is DDOC
  * Expected Result: Error is returned
  * File: 18912.ddoc


**TestCaseID: Soap-Validation-Request-For-Data-Files-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title:  DocumentType was changed to DDOC when actual is PDF
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: Soap-Validation-Request-For-Data-Files-14**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title:  DocumentType was changed to DDOC when actual is BDOC
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


## SoapHashcodeValidationRequestIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/soaptest/SoapHashcodeValidationRequestIT.java)


**TestCaseID: Soap-Hashcode-Validation-Report-Type-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Valid request
  * Expected Result: Simple report is returned with valid signatures
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-Validation-Report-Type-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Valid request with detailed report
  * Expected Result: Simple report is returned with valid signatures
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-Validation-Report-Type-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Report type missing
  * Expected Result: Default is used
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-Validation-Report-Type-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Report type case sensitivity
  * Expected Result: Report type is case insensitive
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-Validation-Report-Type-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Report type is invalid
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-Validation-Report-Type-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Valid request with report Diagnostic
  * Expected Result: Simple report is returned with valid signatures
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-Validation-Policy-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Signature policy POLv3
  * Expected Result: Correct policy is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-Validation-Policy-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Signature policy POLv4
  * Expected Result: Correct policy is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-Validation-Policy-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Default policy
  * Expected Result: POLv4 is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-Validation-Policy-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Invalid policy asked
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-Validation-Policy-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Invalid policy format
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-Validation-Policy-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode)
  * Title: Too long policy
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Signature-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Signature file missing
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Signature-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Signature file empty
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Signature-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Signature file not Base64
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Signature-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Signature file without signature
  * Expected Result: Report without signature is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Signature-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Signature file not XML
  * Expected Result: Report without signature is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Datafile-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request without datafiles
  * Expected Result: Validation report is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Datafile-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with empty data files
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Datafile-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request without datafile filename
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Datafile-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with empty filename
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Datafile-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request without datafile filename
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Datafile-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with too long datafile name
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Datafile-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with invalid hash algorithm
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Datafile-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Hash algo small case
  * Expected Result: Report is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Datafile-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Hash algo mismatch
  * Expected Result: Report with invalid signature
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Datafile-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Datafile hash missing
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Datafile-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Data file hash empty
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-Datafile-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Datafile hash not Base64
  * Expected Result: Error is returned
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Multiple data files
  * Expected Result: Report is returned with signature statuses
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Wrong hash
  * Expected Result: Report is returned with invalid signature
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Wrong data file name
  * Expected Result: Report is returned with signature statuses
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Soap-Hashcode-ValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Empty request body
  * Expected Result: Report is returned with signature statuses
  * File: Valid_XAdES_LT_TS.xml


**TestCaseID: Xades-Hashcode-Validation-Special-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Datafile has + in name
  * Expected Result: The document should pass the validation
  * File: test+document.xml


**TestCaseID: Xades-Hashcode-Validation-Special-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Datafile has space in name
  * Expected Result: The document should pass the validation
  * File: spacesInDatafile.xml


**TestCaseID: Xades-Hashcode-Validation-Special-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Datafile has + in name with full API request
  * Expected Result: The document should pass the validation
  * File: test+document.xml


**TestCaseID: Xades-Hashcode-Validation-Special-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4](http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4)
  * Title: Datafile has space in name with full API request
  * Expected Result: The document should pass the validation
  * File: spacesInDatafile.xml


## SoapValidationReportValueIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/soaptest/SoapValidationReportValueIT.java)


**TestCaseID: Bdoc-SoapValidationReportValue-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: Valid_ID_sig.bdoc


**TestCaseID: Bdoc-SoapValidationReportValue-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23635_bdoc_ts_OCSP_random_nonce.bdoc


**TestCaseID: Bdoc-SoapValidationReportValue-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT, AdES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc

  **Attention! This test is disabled: New testfile needed


**TestCaseID: Bdoc-SoapValidationReportValue-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, AdESqc, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: testAdesQC.bdoc

  **Attention! This test is disabled: Testfile needed


**TestCaseID: Bdoc-SoapValidationReportValue-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LTA, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: EE_SER-AEX-B-LTA-V-24.bdoc


**TestCaseID: Ddoc-SoapValidationReportValue-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.0, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: SK-XML1.0.ddoc

  **Attention! This test is disabled: DDOC 1.0 fails in Travis CI. Needs investigation


**TestCaseID: Ddoc-SoapValidationReportValue-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.1, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.1.ddoc


**TestCaseID: Ddoc-SoapValidationReportValue-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.2, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.2.ddoc


**TestCaseID: Ddoc-SoapValidationReportValue-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.3, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.3.ddoc


**TestCaseID: Pdf-SoapValidationReportValue-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, PAdES_baseline_LT, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: pades_lt_two_valid_sig.pdf


**TestCaseID: Pdf-SoapValidationReportValue-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, PAdES_baseline_B, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: hellopades-pades-b-sha256-auth.pdf


**TestCaseID: Xroad-SoapValidationReportValue-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xroad-simple container
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: xroad-simple.asice


**TestCaseID: Xroad-SoapValidationReportValue-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xroad-simple invalid container
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: invalid-digest.asice


## SoapValidationRequestIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/soaptest/SoapValidationRequestIT.java)


**TestCaseID: Soap-ValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Empty request body
  * Expected Result: Error is returned stating mismatch with required elements
  * File: not relevant


**TestCaseID: Soap-ValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with not base64 string as document
  * Expected Result: Error is returned stating encoding problem
  * File: not relevant


**TestCaseID: Soap-ValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Verification of wrong document type as input
  * Expected Result: Correct error code is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Verification of case sensitivity in document type
  * Expected Result: Error is returned as WSDL defines the allowed values
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request has XML as document type (special case, XML is similar to ddoc and was a accepted document type in earlier versions)
  * Expected Result: Error is given
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request has long filename field
  * Expected Result: Report is returned with the same filename
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Totally empty request body is sent
  * Expected Result: Error is given
  * File: not relevant


**TestCaseID: Soap-ValidationRequest-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with more parameters than expected is sent
  * Expected Result: Error is given or extra parameters are ignored?
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with more parameters than expected is sent
  * Expected Result: Error is given or extra parameters are ignored?
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with special chars is sent
  * Expected Result: Validation report is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with no optional SignaturePolicy field
  * Expected Result: Validation report is returned using default policy
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with XML expansion
  * Expected Result: Error is returned and Entity is not handled
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request XML external entity attack
  * Expected Result: Error message is returned and Doctype field is not handled
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-14**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with empty document
  * Expected Result: Error is returned
  * File: not relevant


**TestCaseID: Soap-ValidationRequest-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with empty filename
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-16**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Request with not allowed signature policy characters
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-17**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: SignaturePolicy is over allowed length
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-18**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: SignaturePolicy is under allowed length
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-19**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Filename is under allowed length
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-20**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Filename is over allowed length
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-BdocValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as document with bdoc document type
  * Expected Result: Error is returned stating problem in document
  * File: not relevant


**TestCaseID: Soap-BdocValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Verification of filename value (filename do not match the actual file)
  * Expected Result: The same filename is returned as sent in the request
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-BdocValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (bdoc and pdf)
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: Soap-BdocValidationRequest-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Bdoc file, not existing value in signaturePolicy
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-BdocValidationRequest-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Bdoc file, policy fiels should be case insensitive
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-DdocValidationRequest-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (bdoc and xroad)
  * Expected Result: Error is returned
  * File: xroad-simple.asice


**TestCaseID: Soap-DdocValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as document with ddoc document type
  * Expected Result: Error is returned stating problem in document
  * File:


**TestCaseID: Soap-DdocValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (ddoc and bdoc)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-DdocValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (ddoc and bdoc)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-DdocValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (ddoc and pdf)
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: Soap-DdocValidationRequest-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Ddoc file, not existing value in signaturePolicy
  * Expected Result: Error is returned
  * File: igasugust1.3.ddoc


**TestCaseID: Soap-DdocValidationRequest-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (ddoc and xroad)
  * Expected Result: Error is returned
  * File: xroad-attachment.asice


**TestCaseID: Soap-PdfValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as document with pdf document type
  * Expected Result: Error is returned stating problem in document
  * File: not relevant


**TestCaseID: Soap-PdfValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: PDF file, not existing value in signaturePolicy
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: Soap-XroadValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: X-road file, not existing value in signaturePolicy
  * Expected Result: Error is returned
  * File: xroad-simple.asice


**TestCaseID: Soap-XroadValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (xroad and ddoc)
  * Expected Result: Error is returned
  * File: igasugust1.3.ddoc


**TestCaseID: Soap-XroadValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (pdf and bdoc)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-XroadValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (bdoc and pdf)
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: Soap-ValidationRequest-ReportType-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Simple report is requested
  * Expected Result: Simple report is returned
  * File:PdfValidSingleSignature.pdf


**TestCaseID: Soap-ValidationRequest-ReportType-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Detailed report is requested
  * Expected Result: Detailed report is returned
  * File:PdfValidSingleSignature.pdf


**TestCaseID: Soap-ValidationRequest-ReportType-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Diagnostic report is requested
  * Expected Result: Diagnostic report is returned
  * File:PdfValidSingleSignature.pdf


## SoapXRoadRequestHeaderIT.java
[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/soaptest/SoapXRoadRequestHeaderIT.java)


**TestCaseID: XroadSoap-RequestVerification-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Soap headers are returned in response
  * Expected Result: Same headers are in response as in request
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: XroadSoap-RequestVerification-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Soap headers are returned in correct order
  * Expected Result: Same headers are in response as in request
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: XroadSoap-RequestVerification-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface)
  * Title: Soap headers are returned in error response
  * Expected Result: Same headers are in response as in request
  * File: not relevant


**TestCaseID: XroadSoap-GetDataFiles-RequestVerification-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Soap headers are returned in response for Get Data Files
  * Expected Result: Same headers are in response as in request
  * File: susisevad1_3.ddoc


**TestCaseID: XroadSoap-GetDataFiles-RequestVerification-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Soap headers are returned in correct order for Get Data Files
  * Expected Result: Same headers are in response as in request
  * File: susisevad1_3.ddoc


**TestCaseID: XroadSoap-GetDataFiles-RequestVerification-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface](http://open-eid.github.io/SiVa/siva2/interfaces/#data-files-request-interface)
  * Title: Soap headers are returned in error response for Get Data Files
  * Expected Result: Same headers are in response as in request
  * File: not relevant


[//]: # (Manually generated test cases start here. Do NOT overwrite with autogenerated test cases!)


## Sample Application Integration Test

**TestCaseID: Sample-Application-Elements-1**

  * TestType: Manual
  * Requirement:
  * Title: Upload of correct file type
  * Expected Result: Its possible to upload the file by dropping in download area or by browsing a file
  * File: Šužlikud sõid ühe õuna ära.bdoc

**TestCaseID: Sample-Application-Elements-2**

  * TestType: Manual
  * Requirement:
  * Title: Upload of invalid file type
  * Expected Result: File is not validated, corresponding errors are returned
  * File: XML.xml

**TestCaseID: Sample-Application-Elements-3**

  * TestType: Manual
  * Requirement:
  * Title: Upload file larger than 10MB
  * Expected Result: File is not validated
  * File: hellopades-lta-no-ocsp.pdf

**TestCaseID: Sample-Application-Elements-4**

  * TestType: Manual
  * Requirement:
  * Title: Page layout
  * Expected Result: All elements are properly displayed and can be used with different window sizes
  * File: not relevant

**TestCaseID: Sample-Application-Validation-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Validate container with valid signatures
  * Expected Result: Validation report is displayed correctly both for JSON and SOAP
  * File: Šužlikud sõid ühe õuna ära.bdoc

**TestCaseID: Sample-Application-Validation-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Validate container with valid signatures
  * Expected Result: Validation report is displayed correctly both for JSON and SOAP
  * File: Šužlikud sõid ühe õuna ära.bdoc

**TestCaseID: Sample-Application-Validation-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Validate container with invalid signatures
  * Expected Result: Validation report is displayed correctly both for JSON and SOAP
  * File: IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc

**TestCaseID: Sample-Application-Validation-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Validate container with not passing policy (POLv2)
  * Expected Result: Validation report is displayed correctly (correct policy is returned) both for JSON and SOAP
  * File: soft-cert-signature.pdf

 
## X-Road Soap System Test


**TestCaseID: Xroad-Validate-Ddoc-1**

  * TestType: Automated SoapUI
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate valid Ddoc
  * Expected Result: The document should return validation report with one valid signature
  * File: DIGIDOC-XML1.3.ddoc

**TestCaseID: Xroad-Validate-Ddoc-2**

  * TestType: Automated SoapUI
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate invalid Ddoc
  * Expected Result: The document should return validation report with no valid signature
  * File: test-non-repu1.ddoc

**TestCaseID: Xroad-Validate-Ddoc-3**

  * TestType: Automated SoapUI
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Invalid Request
  * Expected Result: Soap error should be returned
  * File: not relevant
  
**TestCaseID: Xroad-Validate-Bdoc-1**

  * TestType: Automated SoapUI
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate valid Bdoc
  * Expected Result: The document should return validation report with one valid signature
  * File: Šužlikud sõid ühe õuna ära.bdoc

**TestCaseID: Xroad-Validate-Bdoc-2**

  * TestType: Automated SoapUI
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate invalid Bdoc
  * Expected Result: The document should return validation report with no valid signature
  * File: TM-15_revoked.4.asice

**TestCaseID: Xroad-Validate-Bdoc-3**

  * TestType: Automated SoapUI
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Invalid Request
  * Expected Result: Soap error should be returned
  * File: not relevant
  
**TestCaseID: Xroad-Validate-Pdf-1**

  * TestType: Automated SoapUI 
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate valid Pdf
  * Expected Result: The document should return validation report with one valid signature
  * File: pades-baseline-lta-live-aj.pdf

**TestCaseID: Xroad-Validate-Pdf-2**

  * TestType: Automated SoapUI
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate invalid Pdf
  * Expected Result: The document should return validation report with no valid signature
  * File: pades-baseline-t-live-aj.pdf

**TestCaseID: Xroad-Validate-Pdf-3**

  * TestType: Automated SoapUI
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Invalid Request
  * Expected Result: Soap error should be returned
  * File: not relevant
  
**TestCaseID: Xroad-Validate-Xroad-1**

  * TestType: Automated SoapUI
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate valid Xroad
  * Expected Result: The document should return validation report with one valid signature
  * File: xroad-batchsignature.asice

**TestCaseID: Xroad-Validate-Xroad-2**

  * TestType: Automated SoapUI
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate invalid Xroad
  * Expected Result: The document should return validation report with no valid signature
  * File: invalid-digest.asice

**TestCaseID: Xroad-Validate-Xroad-3**

  * TestType: Automated SoapUI
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Invalid Request
  * Expected Result: Soap error should be returned
  * File: not relevant
  
**TestCaseID: Xroad-GetDataFiles-DDOC-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Get data files from valid DDOC
  * Expected Result: Data files should be returned
  * File: DIGIDOC-XML1.3.ddoc
  
**TestCaseID: Xroad-GetDataFiles-DDOC-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Get data files from invalid DDOC
  * Expected Result: Data files should be returned even if the signatures are not valid
  * File: test-non-repu1.ddoc
  
**TestCaseID: Xroad-GetDataFiles-DDOC-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Invalid request
  * Expected Result: Error is returned as the document type is not supported
  * File: test-non-repu1.ddoc  

## Configuration System Test

**TestCaseID: Configuration-Webapp-TSL-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: TSL update over network
  * Expected Result: TSL is updated from configured path
  * File: not relevant

**TestCaseID: Configuration-Webapp-TSL-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: TSL update disabled
  * Expected Result: Local cashe is used
  * File: not relevant
  
**TestCaseID: Configuration-Webapp-TSL-4**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: TSL trusted territories
  * Expected Result: only listed countries are allowed
  * File: not relevant
  
**TestCaseID: Configuration-Webapp-TSL-5**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: TSL loader scheduler cron
  * Expected Result: TSL renewal process is started
  * File: not relevant

**TestCaseID: Configuration-Webapp-Keystore-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: Keystore change
  * Expected Result: configured keystore is used
  * File: not relevant
  
**TestCaseID: Configuration-Webapp-Scheduler-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: Update time is configured
  * Expected Result: configured time is used
  * File: not relevant
  
**TestCaseID: Configuration-Webapp-Xroad-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: X-Road validator service address is changed
  * Expected Result: correct address is used
  * File: not relevant
  
**TestCaseID: Configuration-Webapp-Policy-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: Configure policy for bdoc
  * Expected Result: Configuration is used
  * File: not relevant

**TestCaseID: Configuration-Webapp-Policy-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: Configure policy for pdf
  * Expected Result: Configuration is used
  * File: not relevant
  
**TestCaseID: Configuration-Webapp-Monitoring-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: Monitoring is enabled for webapp only
  * Expected Result: Its possible to get "heartbeat" status with correct info
  * File: not relevant

**TestCaseID: Configuration-Webapp-Monitoring-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: Monitoring is enabled for webapp and Xroad Service
  * Expected Result: Its possible to get "heartbeat" status with correct info
  * File: not relevant

**TestCaseID: Configuration-Webapp-Monitoring-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: Monitoring is enabled and custom links are configured
  * Expected Result: Its possible to get "heartbeat" status with correct info
  * File: not relevant    

**TestCaseID: Configuration-XroadService-Monitoring-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: Monitoring is enabled
  * Expected Result: Its possible to get "heartbeat" status with correct info for Xroad Service
  * File: not relevant
  
**TestCaseID: Configuration-SampleApplication-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: Configuration of SiVa Web Service access
  * Expected Result: Correct web service is used
  * File: not relevant
  
**TestCaseID: Configuration-SampleApplication-Monitoring-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva2/systemintegrators_guide/)
  * Title: Monitoring is enabled
  * Expected Result: Its possible to get "heartbeat" status with correct info using basic authentication
  * File: not relevant