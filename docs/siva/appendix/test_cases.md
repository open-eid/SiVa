List of Test Cases
==================

## BdocValidationFailIT.java


**TestCaseID: Bdoc-ValidationFail-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc with single invalid signature
  * Expected Result: The document should fail the validation
  * File: IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc


**TestCaseID: Bdoc-ValidationFail-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc with multiple invalid signatures
  * Expected Result: The document should fail the validation
  * File: BdocMultipleSignaturesInvalid.bdoc


**TestCaseID: Bdoc-ValidationFail-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc with multiple signatures both valid and invalid
  * Expected Result: The document should fail the validation
  * File: BdocMultipleSignaturesMixedWithValidAndInvalid.bdoc


**TestCaseID: Bdoc-ValidationFail-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc with no signatures
  * Expected Result: The document should fail the validation
  * File: BdocContainerNoSignature.bdoc


**TestCaseID: Bdoc-ValidationFail-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc with invalid mimetype in manifest
  * Expected Result: document malformed error should be returned
  * File: 23147_weak-warning-sha1-invalid-mimetype-in-manifest.bdoc


**TestCaseID: Bdoc-ValidationFail-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Asice with wrong slash character ('\') in data file mime-type value
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-V-33.asice


**TestCaseID: Bdoc-ValidationFail-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Wrong signature timestamp
  * Expected Result: The document should fail the validation
  * File: TS-02_23634_TS_wrong_SignatureValue.asice


**TestCaseID: Bdoc-ValidationFail-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Asice No non-repudiation key usage value in the certificate, verification of AdES signature level
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-I-43.asice


**TestCaseID: Bdoc-ValidationFail-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Asice signers certificate does not have non-repudiation value in the certificates key usage field and it does not contain the QC and SSCD compliance information.
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-I-26.asice


**TestCaseID: Bdoc-ValidationFail-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc OCSP certificate is not trusted
  * Expected Result: The document should fail the validation
  * File:  TM-01_bdoc21-unknown-resp.bdoc


**TestCaseID: Bdoc-ValidationFail-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Asice TSA certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: TS-05_23634_TS_unknown_TSA.asice


**TestCaseID: Bdoc-ValidationFail-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Asice OCSP response status is revoked
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-R-25.asice


**TestCaseID: Bdoc-ValidationFail-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Asice difference between OCSP and time-stamp issuing times is more than 24 hours
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-V-20.asice


**TestCaseID: Bdoc-ValidationFail-14**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Asice unsigned data files in the container
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-V-34.asice

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: Bdoc-ValidationFail-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc different data file mime-type values in signatures.xml and manifest.xml files
  * Expected Result: The document should fail the validation
  * File: 23613_TM_wrong-manifest-mimetype.bdoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: Bdoc-ValidationFail-16**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc SignatureValue does not correspond to the SignedInfo block
  * Expected Result: The document should fail the validation
  * File: REF-19_bdoc21-no-sig-asn1-pref.bdoc


**TestCaseID: Bdoc-ValidationFail-17**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc Baseline-BES file
  * Expected Result: The document should fail the validation
  * File: signWithIdCard_d4j_1.0.4_BES.asice


**TestCaseID: Bdoc-ValidationFail-18**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc Baseline-EPES file
  * Expected Result: The document should fail the validation
  * File: TM-04_kehtivuskinnituset.4.asice


**TestCaseID: Bdoc-ValidationFail-19**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc signers certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: SS-4_teadmataCA.4.asice


**TestCaseID: Bdoc-ValidationFail-20**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc OCSP response status is revoked
  * Expected Result: The document should fail the validation
  * File: TM-15_revoked.4.asice


**TestCaseID: Bdoc-ValidationFail-21**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc OCSP response status is unknown
  * Expected Result: The document should fail the validation
  * File: TM-16_unknown.4.asice

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: Bdoc-ValidationFail-22**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc signed data file has been removed from the container
  * Expected Result: The document should fail the validation
  * File: KS-21_fileeemaldatud.4.asice


**TestCaseID: Bdoc-ValidationFail-23**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc no files in container
  * Expected Result: The document should fail the validation
  * File: KS-02_tyhi.bdoc


**TestCaseID: Bdoc-ValidationFail-24**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc wrong nonce
  * Expected Result: The document should fail the validation
  * File: TM-10_noncevale.4.asice


**TestCaseID: Bdoc-ValidationFail-25**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc signed data file(s) don't match the hash values in reference elements
  * Expected Result: The document should fail the validation
  * File: REF-14_filesisumuudetud.4.asice


**TestCaseID: Bdoc-ValidationFail-26**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Asice Baseline-T signature
  * Expected Result: The document should fail the validation
  * File: TS-06_23634_TS_missing_OCSP.asice


**TestCaseID: Bdoc-ValidationFail-27**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc OCSP response is not the one expected
  * Expected Result: The document should fail the validation
  * File: 23608-bdoc21-TM-ocsp-bad-nonce.bdoc


**TestCaseID: Bdoc-ValidationFail-28**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc certificate's validity time is not in the period of OCSP producedAt time
  * Expected Result: The document should fail the validation
  * File: 23154_test1-old-sig-sigat-OK-prodat-NOK-1.bdoc


**TestCaseID: Bdoc-ValidationFail-29**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc 	BDOC-1.0 version container
  * Expected Result: The document should fail the validation
  * File: BDOC-1.0.bdoc


## BdocValidationPassIT.java


**TestCaseID: Bdoc-ValidationPass-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Bdoc with single valid signature
  * Expected Result: The document should pass the validation
  * File: Valid_ID_sig.bdoc


**TestCaseID: Bdoc-ValidationPass-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Bdoc TM with multiple valid signatures
  * Expected Result: The document should pass the validation
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Bdoc-ValidationPass-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Bdoc with warning on signature
  * Expected Result: The document should pass the validation but warning should be returned
  * File: bdoc_weak_warning_sha1.bdoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: Bdoc-ValidationPass-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Asice One LT signature with certificates from different countries
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LT-V-30.asice


**TestCaseID: Bdoc-ValidationPass-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Bdoc signed with Mobile-ID, ECC-SHA256 signature with prime256v1 key
  * Expected Result: The document should pass the validation
  * File: 24050_short_ecdsa_correct_file_mimetype.bdoc


**TestCaseID: Bdoc-ValidationPass-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Asice Baseline-LT file
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LT-V-49.asice


**TestCaseID: Bdoc-ValidationPass-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Asice QES file
  * Expected Result: The document should pass the validation
  * File: ValidLiveSignature.asice


**TestCaseID: Bdoc-ValidationPass-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Asice Baseline-LTA file
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LTA-V-24.asice


**TestCaseID: Bdoc-ValidationPass-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Asice file signed with Mobile-ID, ECC-SHA256 signature with prime256v1 key
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LT-V-2.asice


**TestCaseID: Bdoc-ValidationPass-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Asice file with 	ESTEID-SK 2015 certificate chain
  * Expected Result: The document should pass the validation
  * File: IB-4270_TS_ESTEID-SK 2015  SK OCSP RESPONDER 2011.asice


**TestCaseID: Bdoc-ValidationPass-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Asice file with KLASS3-SK 2010 (EECCRCA) certificate chain
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LT-V-28.asice


**TestCaseID: Bdoc-ValidationPass-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Bdoc with Baseline-LT_TM and QES signature level and ESTEID-SK 2011 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: BDOC2.1.bdoc


**TestCaseID: Bdoc-ValidationPass-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Bdoc TS with multiple valid signatures
  * Expected Result: The document should pass the validation
  * File: Test_id_aa.asice


**TestCaseID: Bdoc-ValidationPass-14**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Bdoc-TM with special characters in data file
  * Expected Result: The document should pass the validation
  * File: Šužlikud sõid ühe õuna ära.bdoc


**TestCaseID: Bdoc-ValidationPass-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: *.sce file with TimeMark
  * Expected Result: The document should pass the validation
  * File: BDOC2.1_content_as_sce.sce


**TestCaseID: Bdoc-ValidationPass-16**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: *.sce file with TimeStamp
  * Expected Result: The document should pass the validation
  * File: ASICE_TS_LTA_content_as_sce.sce


**TestCaseID: Bdoc-ValidationPass-17**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Bdoc-TS with special characters in data file
  * Expected Result: The document should pass the validation with correct signature scope
  * File: Nonconventionalcharacters.asice


## DdocValidationFailIT.java


**TestCaseID: Ddoc-ValidationFail-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc with single invalid signature
  * Expected Result: The document should fail the validation
  * File: test1-ddoc-revoked.ddoc


**TestCaseID: Ddoc-ValidationFail-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc with multiple invalid signatures
  * Expected Result: The document should fail the validation
  * File: multipleInvalidSignatures.ddoc


**TestCaseID: Ddoc-ValidationFail-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc with multiple signatures both valid and invalid
  * Expected Result: The document should fail the validation
  * File: multipleValidAndInvalidSignatures.ddoc


**TestCaseID: Ddoc-ValidationFail-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc with no signatures
  * Expected Result: The document should fail the validation
  * File: DdocContainerNoSignature.bdoc


**TestCaseID: Ddoc-ValidationFail-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc signature value has been changed (SignatureValue does not correspond to the SignedInfo block)
  * Expected Result: The document should fail the validation
  * File: test-inv-sig-inf.ddoc


**TestCaseID: Ddoc-ValidationFail-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc Data file(s) don't match the hash values in Reference elements
  * Expected Result: The document should fail the validation
  * File: AndmefailiAtribuudidMuudetud.ddoc


**TestCaseID: Ddoc-ValidationFail-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc Baseline-BES file, no OCSP response
  * Expected Result: The document should fail the validation
  * File: ilma_kehtivuskinnituseta.ddoc


**TestCaseID: Ddoc-ValidationFail-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc no non-repudiation key usage value in the certificate
  * Expected Result: The document should fail the validation
  * File: test-non-repu1.ddoc


**TestCaseID: Ddoc-ValidationFail-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc Signer's certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: Belgia_kandeavaldus_LIV.ddoc


**TestCaseID: Ddoc-ValidationFail-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc OCSP certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: Tundmatu_OCSP_responder.ddoc


**TestCaseID: Ddoc-ValidationFail-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc has unsigned data files in the container
  * Expected Result: The document should fail the validation
  * File: lisatud_andmefail.ddoc


**TestCaseID: Ddoc-ValidationFail-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc signed data file has been removed from the container
  * Expected Result: The document should fail the validation
  * File: faileemald1.ddoc


**TestCaseID: Ddoc-ValidationFail-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc wrong nonce
  * Expected Result: The document should fail the validation
  * File: OCSP nonce vale.ddoc


**TestCaseID: Ddoc-ValidationFail-13**

  * TestType: Automated
  * Requirement: []()
  * Title: Ddoc with XML Entity expansion attack
  * Expected Result: The document should fail the validation with error
  * File: xml_expansion.ddoc


**TestCaseID: Ddoc-ValidationFail-14**

  * TestType: Automated
  * Requirement: []()
  * Title: Ddoc with XML server side request forgery attack
  * Expected Result: The document should fail the validation with error
  * File: xml_entity.ddoc


**TestCaseID: Ddoc-ValidationFail-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc no files in container
  * Expected Result: The document should fail the validation
  * File: KS-02_tyhi.ddoc


**TestCaseID: Ddoc-ValidationFail-17**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc XML namespace error in container
  * Expected Result: The document should pass with warning
  * File: ns6t3cp7.ddoc


## DdocValidationPassIT.java


**TestCaseID: Ddoc-ValidationPass-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Ddoc v1.0 with valid signatures
  * Expected Result: The document should pass the validation
  * File: SK-XML1.0.ddoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: Ddoc-ValidationPass-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Ddoc v1.1 with valid signature
  * Expected Result: The document should pass the validation
  * File: DIGIDOC-XML1.1.ddoc


**TestCaseID: Ddoc-ValidationPass-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Ddoc v1.2 with valid signature
  * Expected Result: The document should pass the validation
  * File: DIGIDOC-XML1.2.ddoc


**TestCaseID: Ddoc-ValidationPass-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Ddoc v1.3 with valid signature with ESTEID-SK 2011 certificate chain
  * Expected Result: The document should pass the validation
  * File: DIGIDOC-XML1.3.ddoc


**TestCaseID: Ddoc-ValidationPass-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Ddoc v1.3 with valid signature, signed data file name has special characters and ESTEID-SK certificate chain
  * Expected Result: The document should pass the validation
  * File: susisevad1_3.ddoc


**TestCaseID: Ddoc-ValidationPass-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Ddoc v1.3 KLASS3-SK certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: KLASS3-SK _ KLASS3-SK OCSP RESPONDER uus.ddoc


**TestCaseID: Ddoc-ValidationPass-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Ddoc v1.3 KLASS3-SK 2010 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: KLASS3-SK 2010 _ KLASS3-SK 2010 OCSP RESPONDER.ddoc


**TestCaseID: Ddoc-ValidationPass-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Ddoc v1.1 ESTEID-SK 2007 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: vaikesed1.1.ddoc


**TestCaseID: Ddoc-ValidationPass-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Ddoc v1.1 ESTEID-SK 2007 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: IB-4270_ESTEID-SK 2015  SK OCSP RESPONDER 2011.ddoc


**TestCaseID: Ddoc-ValidationPass-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Ddoc v1.1 ESTEID-SK certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: EID-SK _ EID-SK OCSP RESPONDER.ddoc


**TestCaseID: Ddoc-ValidationPass-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Ddoc v1.1 ESTEID-SK 2007 and OCSP 2010 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: EID-SK 2007 _ EID-SK 2007 OCSP RESPONDER 2010.ddoc


**TestCaseID: Ddoc-ValidationPass-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Ddoc v1.3 ESTEID-SK 2007 and OCSP 2007 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: EID-SK 2007 _ EID-SK 2007 OCSP RESPONDER.ddoc


**TestCaseID: Ddoc-ValidationPass-14**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Ddoc v1.3 ESTEID-SK 2011 and OCSP 2011 certificate chain with valid signature
  * Expected Result: The document should pass the validation
  * File: EID-SK 2011 _ SK OCSP RESPONDER 2011.ddoc


**TestCaseID: Ddoc-ValidationPass-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Ddoc with warning should pass
  * Expected Result: Document passes the validation
  * File: 18912.ddoc


**TestCaseID: DdocHashcode-ValidationPass-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Validation of DDOC Hashcode v1.0
  * Expected Result: Document passes the validation
  * File: SK-XML1.0_hashcode.ddoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: DdocHashcode-ValidationPass-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Validation of DDOC Hashcode v1.1
  * Expected Result: Document passes the validation
  * File: DIGIDOC-XML1.1_hashcode.ddoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: DdocHashcode-ValidationPass-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Validation of DDOC Hashcode v1.2
  * Expected Result: Document passes the validation
  * File: DIGIDOC-XML1.2_hashcode.ddoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: DdocHashcode-ValidationPass-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Validation of DDOC Hashcode v1.3
  * Expected Result: Document passes the validation
  * File: DIGIDOC-XML1.3_hashcode.ddoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


## DocumentFormatIT.java


**TestCaseID: DocumentFormat-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Validation of pdf document acceptance
  * Expected Result: Pdf is accepted and correct signature validation is given
  * File: hellopades-pades-lt-sha256-sign.pdf


**TestCaseID: DocumentFormat-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Validation of bdoc document acceptance
  * Expected Result: Bdoc is accepted and correct signature validation is given
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: DocumentFormat-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Validation of txt document rejection
  * Expected Result: Txt document is rejected and proper error message is given
  * File: hellopades-pades-lt-sha256-sign.txt


**TestCaseID: DocumentFormat-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Validation of p7s document rejection
  * Expected Result: P7s document is rejected and proper error message is given
  * File: hellocades.p7s


**TestCaseID: DocumentFormat-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Validation of zip document rejection
  * Expected Result: Zip document is rejected and proper error message is given
  * File: 42.zip


**TestCaseID: DocumentFormat-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Validation of doc document rejection
  * Expected Result: Doc document is rejected and proper error message is given
  * File: hellopades-pades-lt-sha256-sign.doc


**TestCaseID: DocumentFormat-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Validation of XML document rejection
  * Expected Result: XML document is rejected and proper error message is given
  * File: XML.xml


**TestCaseID: DocumentFormat-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Validation of png document rejection
  * Expected Result: Png document is rejected and proper error message is given
  * File: Picture.png


## LargeFileIT.java


**TestCaseID: PDF-LargeFiles-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/overview/#main-features-of-siva-validation-service](http://open-eid.github.io/SiVa/siva/overview/#main-features-of-siva-validation-service)
  * Title: 9MB PDF files (PAdES Baseline LT).
  * Expected Result: Validation report is returned
  * File: 9MB_PDF.pdf


**TestCaseID: Bdoc-LargeFiles-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/overview/#main-features-of-siva-validation-service](http://open-eid.github.io/SiVa/siva/overview/#main-features-of-siva-validation-service)
  * Title: 9MB ASIC-E file
  * Expected Result: Validation report is returned
  * File: 9MB_BDOC-TS.bdoc


**TestCaseID: Bdoc-LargeFiles-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/overview/#main-features-of-siva-validation-service](http://open-eid.github.io/SiVa/siva/overview/#main-features-of-siva-validation-service)
  * Title: 9MB BDOC-TM
  * Expected Result: Validation report is returned
  * File: 9MB_BDOC-TM.bdoc


**TestCaseID: Ddoc-LargeFiles-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/overview/#main-features-of-siva-validation-service](http://open-eid.github.io/SiVa/siva/overview/#main-features-of-siva-validation-service)
  * Title: 9MB DDOC
  * Expected Result: Validation report is returned
  * File: 9MB_DDOC.ddoc


## PdfBaselineProfileIT.java


**TestCaseID: PDF-BaselineProfile-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: The PDF has PAdES-B profile signature POLv1
  * Expected Result: Document validation should fail as the profile is not supported with any policy
  * File: hellopades-pades-b-sha256-auth.pdf


**TestCaseID: PDF-BaselineProfile-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: The PDF has PAdES-B profile signature POLv2
  * Expected Result: Document validation should fail as the profile is supported with any policy
  * File: hellopades-pades-b-sha256-auth.pdf


**TestCaseID: PDF-BaselineProfile-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: The PDF has PAdES-T profile signature POLv1
  * Expected Result: Document validation should fail with any policy
  * File: pades-baseline-t-live-aj.pdf


**TestCaseID: PDF-BaselineProfile-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: The PDF has PAdES-T profile signature POLv2
  * Expected Result: Document validation should fail with any policy
  * File: pades-baseline-t-live-aj.pdf


**TestCaseID: PDF-BaselineProfile-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: The PDF has PAdES-LT profile signature POLv1
  * Expected Result: Document validation should pass with any policy
  * File: hellopades-pades-lt-sha256-sign.pdf


**TestCaseID: PDF-BaselineProfile-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: The PDF has PAdES-LT profile signature POLv2
  * Expected Result: Document validation should pass with any policy
  * File: hellopades-pades-lt-sha256-sign.pdf


**TestCaseID: PDF-BaselineProfile-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: The PDF has PAdES-LTA profile signature POLv1
  * Expected Result: Document validation should pass with any policy
  * File: pades-baseline-lta-live-aj.pdf


**TestCaseID: PDF-BaselineProfile-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: The PDF has PAdES-LTA profile signature POLv2
  * Expected Result: Document validation should pass with any policy
  * File: pades-baseline-lta-live-aj.pdf


**TestCaseID: PDF-BaselineProfile-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: The PDF has PAdES-LT and B profile signature
  * Expected Result: Document validation should fail
  * File: hellopades-lt-b.pdf


**TestCaseID: PDF-BaselineProfile-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: PDF document message digest attribute value does not match calculate value
  * Expected Result: Document validation should fail
  * File: hellopades-lt1-lt2-wrongDigestValue.pdf


**TestCaseID: PDF-BaselineProfile-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: PDF file with a serial signature
  * Expected Result: Document signed with multiple signers with serial signatures should pass
  * File: hellopades-lt1-lt2-Serial.pdf


## PdfSignatureCryptographicAlgorithmIT.java


**TestCaseID: PDF-SigCryptoAlg-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: SHA512 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with SHA512 algorithm should pass
  * File: hellopades-lt-sha512.pdf


**TestCaseID: PDF-SigCryptoAlg-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: SHA1 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with SHA1 algorithm should pass
  * File: hellopades-lt-sha1.pdf


**TestCaseID: PDF-SigCryptoAlg-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: ECDSA224 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with ECDSA224 algorithm should pass
  * File: hellopades-lt-sha256-ec224.pdf


**TestCaseID: PDF-SigCryptoAlg-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: ECDSA256 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with ECDSA256 algorithm should pass
  * File: hellopades-lt-sha256-ec256.pdf


**TestCaseID: PDF-SigCryptoAlg-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: RSA1024 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with RSA1024 algorithm should pass
  * File: hellopades-lt-sha256-rsa1024.pdf


**TestCaseID: PDF-SigCryptoAlg-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: RSA1023 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with RSA1023 algorithm should fail
  * File: hellopades-lt-sha256-rsa1023.pdf


**TestCaseID: PDF-SigCryptoAlg-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: RSA2047 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with RSA2047 algorithm should pass
  * File: hellopades-lt-sha256-rsa2047.pdf


**TestCaseID: PDF-SigCryptoAlg-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: RSA2048 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with RSA2048 algorithm should pass
  * File: PdfValidSingleSignature


## PdfValidationFailIT.java


**TestCaseID: PDF-ValidationFail-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: The PDF-file has been signed with expired certificate (PAdES Baseline LT)
  * Expected Result: Document signed with certificate that is expired should fail.
  * File: hellopades-lt-rsa1024-sha1-expired.pdf


**TestCaseID: PDF-ValidationFail-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: The PDF-file has been signed with revoked certificate (PAdES Baseline LT)
  * Expected Result: Document signed with certificate that is revoked should fail.
  * File: pades_lt_revoked.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: PDF-ValidationFail-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: The PDF-file has been signed with certificate which has no non repudiation key usage attribute (PAdES Baseline LT)
  * Expected Result: The PDF-file validation should fail with error.
  * File: hellopades-pades-lt-sha256-auth.pdf


**TestCaseID: PDF-ValidationFail-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
  * Expected Result: Document signed with expired certificate should fail
  * File: hellopades-lt-sha256-rsa2048-expired.pdf


**TestCaseID: PDF-ValidationFail-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
  * Expected Result: Document signed with expired certificate should fail
  * File: hellopades-lt-sha256-rsa1024-expired2.pdf


**TestCaseID: PDF-ValidationFail-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
  * Expected Result: Document signed with expired certificate should fail
  * File: hellopades-lt-sha1-rsa1024-expired2.pdf


## PdfValidationPassIT.java


**TestCaseID: PDF-ValidationPass-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: The PDF-file has been signed with certificate that is expired after signing (PAdES Baseline LT)
  * Expected Result: Document signed with certificate that expired after signing should pass.
  * File: hellopades-lt-sha256-rsa1024-not-expired.pdf


**TestCaseID: PDF-ValidationPass-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: The PDF-file has been signed with certificate that will expire in 7 days after signing (PAdES Baseline LT)
  * Expected Result: Document signed with certificate that expired after signing should pass.
  * File: hellopades-lt-sha256-rsa2048-7d.pdf


**TestCaseID: PDF-ValidationPass-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2)
  * Title: Pdf with single valid signature
  * Expected Result: Document should pass.
  * File: PdfValidSingleSignature.pdf


## SignaturePolicyIT.java


**TestCaseID: Pdf-Signature-Policy-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1)
  * Title: The PDF-file is not QES level and misses SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy
  * File: soft-cert-signature.pdf


**TestCaseID: Pdf-Signature-Policy-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1)
  * Title: The PDF-file is not QES level and misses SSCD/QSCD compliance
  * Expected Result: Signatures are not valid according to policy
  * File: soft-cert-signature.pdf


**TestCaseID: Pdf-Signature-Policy-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1)
  * Title: The PDF-file is QES level and has SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy ("weaker" policy is used)
  * File: pades_lt_two_valid_sig.pdf


**TestCaseID: Pdf-Signature-Policy-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1)
  * Title: The PDF-file is QES level and has SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy
  * File: pades_lt_two_valid_sig.pdf


**TestCaseID: Bdoc-Signature-Policy-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1)
  * Title: The bdoc is not QES level and misses SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy
  * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc


**TestCaseID: Bdoc-Signature-Policy-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1)
  * Title: The bdoc is not QES level and misses SSCD/QSCD compliance
  * Expected Result: Signatures are not valid according to policy
  * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc


**TestCaseID: Bdoc-Signature-Policy-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1)
  * Title: The bdoc is QES level and has SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy ("non-strict" policy is used)
  * File: Valid_ID_sig.bdoc


**TestCaseID: Bdoc-Signature-Policy-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1)
  * Title: The bdoc is QES level and has SSCD/QSCD compliance
  * Expected Result: Signatures are valid according to policy
  * File: Valid_ID_sig.bdoc


## XRoadValidationFailIT.java


**TestCaseID: Xroad-ValidationFail-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Simple xroad document with container fault
  * Expected Result: Document should fail validation
  * File: invalid-digest.asice


## XRoadValidationPassIT.java


**TestCaseID: Xroad-ValidationPass-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Simple xroad document
  * Expected Result: Document should pass
  * File: xroad-simple.asice


**TestCaseID: Xroad-ValidationPass-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Batchsignature xroad document
  * Expected Result: Document should pass
  * File: xroad-batchsignature.asice


**TestCaseID: Xroad-ValidationPass-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2](http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2)
  * Title: Attachment xroad document
  * Expected Result: Document should pass
  * File: xroad-attachment.asice


## StatisticsToGAManualIT.java

!!! Note

    All tests in this class expect manual configuration of Google Analytics before executing the tests.
    The test are made to prepare test data and ease the test execution, all results must be checked manually in GA!
    Check all the fields presence and correctness even though tests prepare data for specific cases.

**TestCaseID: Xauth-Statistics-GA-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Pdf valid container is validated with x-authenticated-user set in header
  * Expected Result: x-authenticated-user value is shown in GA
  * File: pades_lt_two_valid_sig.pdf


**TestCaseID: Xauth-Statistics-GA-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Pdf valid container is validated with x-authenticated-user set in header
  * Expected Result: x-authenticated-user value is shown as N/A in GA
  * File: pades_lt_two_valid_sig.pdf


**TestCaseID: Xauth-Statistics-GA-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Pdf valid container is validated with x-authenticated-user set in header
  * Expected Result: x-authenticated-user value is shown as N/A in GA
  * File: pades_lt_two_valid_sig.pdf


**TestCaseID: Bdoc-Statistics-GA-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Bdoc with certificates from different countries.
  * Expected Result: LT, EE and LV country codes are present in GA, Signature container type is ASIC-E TM
  * File: Baltic MoU digital signing_EST_LT_LV.bdoc


**TestCaseID: Bdoc-Statistics-GA-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Asice-E TS is validated
  * Expected Result: GA shows Signature container type as ASIC-E TS
  * File: BDOC-TS.bdoc


**TestCaseID: Xroad-Statistics-GA-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Xroad valid container is validated
  * Expected Result: Country code XX is sent to GA (GA shows ZZ as the XX is unknown code), Signature container type is ASiC-E (BatchSignature)
  * File: xroad-batchsignature.asice


**TestCaseID: Ddoc-Statistics-GA-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Ddoc not supported file is inserted
  * Expected Result: Not shown in GA statistics as the container is not validated
  * File: xroad-simple.asice


**TestCaseID: Ddoc-Statistics-Log-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Ddoc invalid container is validated
  * Expected Result: GA shows Signature container type as XAdES, TOTAL-FAILED indication
  * File: ilma_kehtivuskinnituseta.ddoc


## StatisticsToLogsManualIT.java

!!! Note

    All tests in this class expect manual verification of responses in log! The tests are made to prepare test data and ease the test execution.

**TestCaseID: Bdoc-Statistics-Log-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Bdoc valid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Bdoc-Statistics-Log-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Bdoc invalid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: SS-4_teadmataCA.4.asice


**TestCaseID: Bdoc-Statistics-Log-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Bdoc not supported file is inserted
  * Expected Result: No message in statistics as the container is not validated
  * File: xroad-simple.asice


**TestCaseID: Bdoc-Statistics-Log-4**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Bdoc with certificates from different countries.
  * Expected Result: Correct data is shown in the log with correct structure
  * File: Baltic MoU digital signing_EST_LT_LV.bdoc


**TestCaseID: Ddoc-Statistics-Log-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Ddoc valid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: igasugust1.3.ddoc


**TestCaseID: Ddoc-Statistics-Log-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Ddoc invalid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: ilma_kehtivuskinnituseta.ddoc


**TestCaseID: Ddoc-Statistics-Log-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Ddoc not supported file is inserted
  * Expected Result: No message in statistics as the container is not validated
  * File: xroad-simple.asice


**TestCaseID: Ddoc-Statistics-Log-4**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Ddoc with certificates from different countries.
  * Expected Result: Correct data is shown in the log with correct structure
  * File: Belgia_kandeavaldus_LIV.ddoc


**TestCaseID: Pdf-Statistics-Log-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Pdf valid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: pades_lt_two_valid_sig.pdf


**TestCaseID: Pdf-Statistics-Log-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Pdf invalid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: hellopades-lt1-lt2-wrongDigestValue.pdf


**TestCaseID: Pdf-Statistics-Log-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Pdf not supported file is inserted
  * Expected Result: No message in statistics as the container is not validated
  * File: xroad-simple.asice


**TestCaseID: Pdf-Statistics-Log-4**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Pdf with certificates from non Estonian countries.
  * Expected Result: Correct data is shown in the log with correct structure
  * File: Regulatione-signedbyco-legislators.pdf


**TestCaseID: Xroad-Statistics-Log-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Xroad valid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: xroad-batchsignature.asice


**TestCaseID: Xroad-Statistics-Log-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Xroad invalid container is validated
  * Expected Result: Correct data is shown in the log with correct structure
  * File: invalid-digest.asice


**TestCaseID: Xroad-Statistics-Log-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf](http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf)
  * Title: Xroad not supported file is inserted
  * Expected Result: No message in statistics as the container is not validated
  * File: BDOC-TS.bdoc


## ValidationReportValueVerificationIT.java


**TestCaseID: Bdoc-ValidationReportVerification-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface#validation-request-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: Valid_ID_sig.bdoc


**TestCaseID: Bdoc-ValidationReportVerification-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23635_bdoc_ts_OCSP_random_nonce.bdoc


**TestCaseID: Bdoc-ValidationReportVerification-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT, AdES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc


**TestCaseID: Bdoc-ValidationReportVerification-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT-TM, AdESqc
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23200_weakdigest-wrong-nonce.asice

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: Bdoc-ValidationReportVerification-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (Bdoc valid multiple signatures)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: Baltic MoU digital signing_EST_LT_LV.bdoc


**TestCaseID: Bdoc-ValidationReportVerification-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (Bdoc indeterminate status)
  * Expected Result: All required elements are present according to BdocDocSimpleReportSchema.json
  * File: SS-4_teadmataCA.4.asice


**TestCaseID: Bdoc-ValidationReportVerification-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title:  Bdoc report with no signatures
  * Expected Result: Report is returned with required elements
  * File:BdocContainerNoSignature.bdoc


**TestCaseID: Pdf-ValidationReportVerification-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (Pdf valid single signature)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: hellopades-lt-sha256-ec256.pdf


**TestCaseID: Pdf-ValidationReportVerification-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (Pdf valid Multiple signatures)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File:


**TestCaseID: Pdf-ValidationReportVerification-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (Pdf invalid signature)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: hellopades-lt-b.pdf


**TestCaseID: Pdf-ValidationReportVerification-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (Pdf indeterminate status)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: hellopades-lt-rsa1024-sha1-expired.pdf


**TestCaseID: Pdf-ValidationReportVerification-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title:  Pdf report with no signatures
  * Expected Result: Report is returned with required elements
  * File: PdfNoSignature.pdf


**TestCaseID: Ddoc-ValidationReportVerification-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (ddoc valid single signature)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: DIGIDOC-XML1.3.ddoc


**TestCaseID: Ddoc-ValidationReportVerification-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (ddoc invalid signature)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: test1-ddoc-revoked.ddoc


**TestCaseID: Ddoc-ValidationReportVerification-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: JSON structure has all elements (ddoc indeterminate status)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: test1-ddoc-unknown.ddoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: Ddoc-ValidationReportVerification-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Check for optional warning element
  * Expected Result: Warning element is present
  * File: ns6t3cp7.ddoc


**TestCaseID: Ddoc-ValidationReportVerification-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title:  Ddoc report with no signatures
  * Expected Result: Report is returned with required elements
  * File: DdocContainerNoSignature.ddoc


**TestCaseID: Ddoc-ValidationReportVerification-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.0, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: SK-XML1.0.ddoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: Ddoc-ValidationReportVerification-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.1, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.1.ddoc


**TestCaseID: Ddoc-ValidationReportVerification-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.2, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.2.ddoc


**TestCaseID: Ddoc-ValidationReportVerification-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.3, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.3.ddoc


**TestCaseID: Xroad-ValidationReportVerification-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xroad-simple container
  * Expected Result: Report is returned with required elements
  * File: xroad-simple.asice


**TestCaseID: Xroad-ValidationReportVerification-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xroad-batchsignature container
  * Expected Result: Report is returned with required elements
  * File: xroad-batchsignature.asice


**TestCaseID: Xroad-ValidationReportVerification-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xroad-attachment container
  * Expected Result: Report is returned with required elements
  * File: xroad-attachment.asice


**TestCaseID: Xroad-ValidationReportVerification-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report with invalid xroad container
  * Expected Result: Report is returned with required elements
  * File: xroad-attachment.asice


## ValidationRequestIT.java


**TestCaseID: ValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Input empty values
  * Expected Result: Errors are returned stating the missing values
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Verification of wrong document type as input
  * Expected Result: Correct error code is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request has invalid key on document position
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request has XML as document type (special case, XML is similar to ddoc and was a accepted document type in earlier versions)
  * Expected Result: Error is given
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request has long (38784 characters) in filename field
  * Expected Result: Report is returned with the same filename
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Totally empty request body is sent
  * Expected Result: Error is given
  * File: None


**TestCaseID: ValidationRequest-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request with more parameters than expected is sent
  * Expected Result: Error is given or extra parameters are ignored?
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request with special chars is sent
  * Expected Result: Validation report is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: ValidationRequest-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request with not base64 string as document
  * Expected Result: Error is returned stating encoding problem
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Bdoc-ValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (bdoc and pdf)
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: Bdoc-ValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (bdoc and ddoc)
  * Expected Result: Error is returned
  * File: igasugust1.3.ddoc


**TestCaseID: Bdoc-ValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (bdoc and xroad)
  * Expected Result: Error is returned
  * File: xroad-simple.asice


**TestCaseID: Bdoc-ValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Bdoc file, not existing value in signaturePolicy
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Bdoc-ValidationRequest-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Bdoc file, policy fiels should be case insensitive
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Bdoc-ValidationRequest-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request without optional signaturePolicy field
  * Expected Result: Validation report is returned with default policy
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Bdoc-ValidationRequest-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as document with bdoc document type
  * Expected Result: Error is returned stating problem in document
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Bdoc-ValidationRequest-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Acceptance of ASICE as BDOC document type
  * Expected Result: Asice files are handled the same as bdoc
  * File: bdoc21-TS.asice


**TestCaseID: Bdoc-ValidationRequest-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Verification of case insensitivity in document type
  * Expected Result: Report is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Bdoc-ValidationRequest-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Verification of filename value (filename do not match the actual file)
  * Expected Result: The same filename is returned as sent in the request
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Bdoc-ValidationRequest-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request has invalid character in filename
  * Expected Result: Correct error code is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Bdoc-ValidationRequest-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (bdoc and xroad)
  * Expected Result: Error is returned
  * File: xroad-batchsignature


**TestCaseID: Bdoc-ValidationRequest-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (bdoc and xroad)
  * Expected Result: Error is returned
  * File: xroad-attachment.asice


**TestCaseID: Pdf-ValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (pdf and ddoc)
  * Expected Result: Error is returned
  * File: igasugust1.3.ddoc


**TestCaseID: Pdf-ValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (pdf and bdoc)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Pdf-ValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (pdf and xroad)
  * Expected Result: Error is returned
  * File: xroad-simple.asice


**TestCaseID: Pdf-ValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: PDF file, not existing value in signaturePolicy
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: Pdf-ValidationRequest-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as document with pdf document type
  * Expected Result: Error is returned stating problem in document
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Pdf-ValidationRequest-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Verification of filename value (filename do not match the actual file)
  * Expected Result: The same filename is returned as sent in the request
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Pdf-ValidationRequest-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (pdf and xroad)
  * Expected Result: Error is returned
  * File: xroad-batchsignature.asice


**TestCaseID: Pdf-ValidationRequest-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (pdf and xroad)
  * Expected Result: Error is returned
  * File: xroad-attachment.asice


**TestCaseID: Ddoc-ValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (ddoc and bdoc)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Ddoc-ValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (ddoc and pdf)
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: Ddoc-ValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (ddoc and xroad)
  * Expected Result: Error is returned
  * File: xroad-simple.asice


**TestCaseID: Ddoc-ValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Ddoc file, not existing value in signaturePolicy
  * Expected Result: Error is returned
  * File: igasugust1.3.ddoc


**TestCaseID: Ddoc-ValidationRequest-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as document with ddoc document type
  * Expected Result: Error is returned stating problem in document
  * File:


**TestCaseID: Ddoc-ValidationRequest-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Verification of filename value (filename do not match the actual file)
  * Expected Result: The same filename is returned as sent in the request
  * File: igasugust1.3.ddoc


**TestCaseID: Ddoc-ValidationRequest-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (ddoc and xroad)
  * Expected Result: Error is returned
  * File: xroad-simple.asice


**TestCaseID: Ddoc-ValidationRequest-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (ddoc and xroad)
  * Expected Result: Error is returned
  * File: xroad-simple.asice


**TestCaseID: Xroad-ValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (xroad and pdf)
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: Xroad-ValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (xroad and ddoc)
  * Expected Result: Error is returned
  * File: igasugust1.3.ddoc


**TestCaseID: Xroad-ValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (xroad and bdoc)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Xroad-ValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: X-road file, not existing value in signaturePolicy
  * Expected Result: Error is returned
  * File: xroad-simple.asice


**TestCaseID: Xroad-ValidationRequest-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as document with xroad document type
  * Expected Result: Error is returned stating problem in document
  * File:


**TestCaseID: Xroad-ValidationRequest-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Verification of filename value (filename do not match the actual file)
  * Expected Result: The same filename is returned as sent in the request
  * File: xroad-simple.asice


## SoapValidationReportValueIT.java


**TestCaseID: Bdoc-SoapValidationReportValue-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: Valid_ID_sig.bdoc


**TestCaseID: Bdoc-SoapValidationReportValue-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23635_bdoc_ts_OCSP_random_nonce.bdoc


**TestCaseID: Bdoc-SoapValidationReportValue-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT, AdES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc


**TestCaseID: Bdoc-SoapValidationReportValue-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, AdESqc, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23200_weakdigest-wrong-nonce.asice


**TestCaseID: Bdoc-SoapValidationReportValue-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LTA, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: EE_SER-AEX-B-LTA-V-24.bdoc


**TestCaseID: Ddoc-SoapValidationReportValue-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.0, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: SK-XML1.0.ddoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: Ddoc-SoapValidationReportValue-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.1, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.1.ddoc


**TestCaseID: Ddoc-SoapValidationReportValue-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.2, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.2.ddoc


**TestCaseID: Ddoc-SoapValidationReportValue-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xml v1.3, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.3.ddoc


**TestCaseID: Pdf-SoapValidationReportValue-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, PAdES_baseline_LT, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: pades_lt_two_valid_sig.pdf


**TestCaseID: Pdf-SoapValidationReportValue-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, PAdES_baseline_B, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: hellopades-pades-b-sha256-auth.pdf


**TestCaseID: Xroad-SoapValidationReportValue-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xroad-simple container
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: xroad-simple.asice

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


**TestCaseID: Xroad-SoapValidationReportValue-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface)
  * Title: Verification of values in Validation Report, xroad-simple invalid container
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: invalid-digest.asice


## SoapValidationRequestIT.java


**TestCaseID: Soap-ValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Empty request body
  * Expected Result: Error is returned stating mismatch with required elements
  * File:


**TestCaseID: Soap-ValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request with not base64 string as document
  * Expected Result: Error is returned stating encoding problem
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Verification of wrong document type as input
  * Expected Result: Correct error code is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Verification of case sensitivity in document type
  * Expected Result: Error is returned as WSDL defines the allowed values
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request has XML as document type (special case, XML is similar to ddoc and was a accepted document type in earlier versions)
  * Expected Result: Error is given
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request has long filename field
  * Expected Result: Report is returned with the same filename
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Totally empty request body is sent
  * Expected Result: Error is given
  * File: None


**TestCaseID: Soap-ValidationRequest-8**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request with more parameters than expected is sent
  * Expected Result: Error is given or extra parameters are ignored?
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-9**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request with more parameters than expected is sent
  * Expected Result: Error is given or extra parameters are ignored?
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-10**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request with special chars is sent
  * Expected Result: Validation report is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-11**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request with no optional SignaturePolicy field
  * Expected Result: Validation report is returned using default policy
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-12**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request with XML expansion
  * Expected Result: Error is returned and Entity is not handled
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-13**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request XML external entity attack
  * Expected Result: Error message is returned and Doctype field is not handled
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-14**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request with empty document
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-15**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request with empty filename
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-ValidationRequest-16**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Request with not allowed signature policy characters
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-BdocValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as document with bdoc document type
  * Expected Result: Error is returned stating problem in document
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-BdocValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Verification of filename value (filename do not match the actual file)
  * Expected Result: The same filename is returned as sent in the request
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-BdocValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (bdoc and pdf)
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: Soap-BdocValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (bdoc and ddoc)
  * Expected Result: Error is returned
  * File: igasugust1.3.ddoc


**TestCaseID: Soap-BdocValidationRequest-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Bdoc file, not existing value in signaturePolicy
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-BdocValidationRequest-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Bdoc file, policy fiels should be case insensitive
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-DdocValidationRequest-7**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (bdoc and xroad)
  * Expected Result: Error is returned
  * File: xroad-simple.asice


**TestCaseID: Soap-DdocValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as document with ddoc document type
  * Expected Result: Error is returned stating problem in document
  * File:


**TestCaseID: Soap-DdocValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (ddoc and bdoc)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-DdocValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (ddoc and bdoc)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-DdocValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (ddoc and pdf)
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: Soap-DdocValidationRequest-5**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Ddoc file, not existing value in signaturePolicy
  * Expected Result: Error is returned
  * File: igasugust1.3.ddoc


**TestCaseID: Soap-DdocValidationRequest-6**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (ddoc and xroad)
  * Expected Result: Error is returned
  * File: xroad-attachment.asice


**TestCaseID: Soap-PdfValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Input random base64 string as document with pdf document type
  * Expected Result: Error is returned stating problem in document
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-PdfValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (pdf and bdoc)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-PdfValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (pdf and ddoc)
  * Expected Result: Error is returned
  * File: igasugust1.3.ddoc


**TestCaseID: Soap-PdfValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (pdf and xroad)
  * Expected Result: Error is returned
  * File: xroad-batchsignature.asice


**TestCaseID: Soap-PdfValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: PDF file, not existing value in signaturePolicy
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


**TestCaseID: Soap-XroadValidationRequest-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: X-road file, not existing value in signaturePolicy
  * Expected Result: Error is returned
  * File: xroad-simple.asice


**TestCaseID: Soap-XroadValidationRequest-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (xroad and ddoc)
  * Expected Result: Error is returned
  * File: igasugust1.3.ddoc


**TestCaseID: Soap-XroadValidationRequest-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (pdf and bdoc)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: Soap-XroadValidationRequest-4**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface](http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface)
  * Title: Mismatch in documentType and actual document (bdoc and pdf)
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf


## SoapXRoadRequestHeaderIT.java


**TestCaseID: XroadSoap-RequestVerification-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/interface_description/#validation-request-interface](http://open-eid.github.io/SiVa/siva/interface_description/#validation-request-interface)
  * Title: Soap headers are returned in response
  * Expected Result: Same headers are in response as in request
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: XroadSoap-RequestVerification-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/interface_description/#validation-request-interface](http://open-eid.github.io/SiVa/siva/interface_description/#validation-request-interface)
  * Title: Soap headers are returned in correct order
  * Expected Result: Same headers are in response as in request
  * File: Valid_IDCard_MobID_signatures.bdoc


**TestCaseID: XroadSoap-RequestVerification-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/interface_description/#validation-request-interface](http://open-eid.github.io/SiVa/siva/interface_description/#validation-request-interface)
  * Title: Soap headers are returned in error response
  * Expected Result: Same headers are in response as in request
  * File:

  
## X-Road Soap System Test


**TestCaseID: Xroad-Validate-Ddoc-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate valid Ddoc
  * Expected Result: The document should return validation report with one valid signature
  * File: DIGIDOC-XML1.3.ddoc

**TestCaseID: Xroad-Validate-Ddoc-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate invalid Ddoc
  * Expected Result: The document should return validation report with no valid signature
  * File: test-non-repu1.ddoc

**TestCaseID: Xroad-Validate-Ddoc-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Invalid Request
  * Expected Result: Soap error should be returned
  * File:
  
**TestCaseID: Xroad-Validate-Bdoc-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate valid Bdoc
  * Expected Result: The document should return validation report with one valid signature
  * File: Šužlikud sõid ühe õuna ära.bdoc

**TestCaseID: Xroad-Validate-Bdoc-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate invalid Bdoc
  * Expected Result: The document should return validation report with no valid signature
  * File: TM-15_revoked.4.asice

**TestCaseID: Xroad-Validate-Bdoc-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Invalid Request
  * Expected Result: Soap error should be returned
  * File:
  
**TestCaseID: Xroad-Validate-Pdf-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate valid Pdf
  * Expected Result: The document should return validation report with one valid signature
  * File: pades-baseline-lta-live-aj.pdf

**TestCaseID: Xroad-Validate-Pdf-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate invalid Pdf
  * Expected Result: The document should return validation report with no valid signature
  * File: pades-baseline-t-live-aj.pdf

**TestCaseID: Xroad-Validate-Pdf-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Invalid Request
  * Expected Result: Soap error should be returned
  * File:
  
**TestCaseID: Xroad-Validate-Xroad-1**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate valid Xroad
  * Expected Result: The document should return validation report with one valid signature
  * File: xroad-batchsignature.asice

**TestCaseID: Xroad-Validate-Xroad-2**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Validate invalid Xroad
  * Expected Result: The document should return validation report with no valid signature
  * File: invalid-digest.asice

**TestCaseID: Xroad-Validate-Xroad-3**

  * TestType: Automated
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/interfaces/](http://open-eid.github.io/SiVa/siva/v2/interfaces/)
  * Title: Invalid Request
  * Expected Result: Soap error should be returned
  * File:

## Configuration System Test

**TestCaseID: Configuration-webapp-TSL-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/)
  * Title: TSL update over network
  * Expected Result: TSL is updated from configured path
  * File: -

**TestCaseID: Configuration-webapp-TSL-3**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/)
  * Title: TSL update disabled
  * Expected Result: Local cashe is used
  * File: -
  
**TestCaseID: Configuration-webapp-keystore-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/)
  * Title: Keystore change
  * Expected Result: configured keystore is used
  * File: -
  
**TestCaseID: Configuration-webapp-scheduler-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/)
  * Title: Update time is configured
  * Expected Result: configured time is used
  * File: -
  
**TestCaseID: Configuration-webapp-xroad-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/)
  * Title: X-Road validator service address is changed
  * Expected Result: correct address is used
  * File: -
  
**TestCaseID: Configuration-webapp-GA-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/)
  * Title: Google Analytics is configured
  * Expected Result: Data is transfered to GA
  * File: -
  
**TestCaseID: Configuration-webapp-policy-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/)
  * Title: Configure policy for bdoc
  * Expected Result: Configuration is used
  * File: -

**TestCaseID: Configuration-webapp-policy-2**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/)
  * Title: Configure policy for pdf
  * Expected Result: Configuration is used
  * File: -
  
**TestCaseID: Configuration-sample-application-1**

  * TestType: Manual
  * Requirement: [http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/](http://open-eid.github.io/SiVa/siva/v2/systemintegrators_guide/)
  * Title: Configuration of SiVa Web Service access
  * Expected Result: Correct web service is used
  * File: -