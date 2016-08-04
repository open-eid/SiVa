List of Test Cases
==================

## BdocValidationFail.java

**TestCaseID: Bdoc-ValidationFail-1**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc with single invalid signature
  * Expected Result: The document should fail the validation
  * File: IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc

**TestCaseID: Bdoc-ValidationFail-2**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc with multiple invalid signatures
  * Expected Result: The document should fail the validation
  * File: BdocMultipleSignaturesInvalid.bdoc

**TestCaseID: Bdoc-ValidationFail-3**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc with multiple signatures both valid and invalid
  * Expected Result: The document should fail the validation
  * File: BdocMultipleSignaturesMixedWithValidAndInvalid.bdoc

**TestCaseID: Bdoc-ValidationFail-4**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc with no signatures
  * Expected Result: The document should fail the validation
  * File: BdocContainerNoSignature.bdoc

**TestCaseID: Bdoc-ValidationFail-5**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc with invalid mimetype in manifest
  * Expected Result: document malformed error should be returned
  * File: 23147_weak-warning-sha1-invalid-mimetype-in-manifest.bdoc

**TestCaseID: Bdoc-ValidationFail-6**

  * TestType: Automated
  * RequirementID: []()
  * Title: Asice with wrong slash character ('\') in data file mime-type value
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-V-33.asice

**TestCaseID: Bdoc-ValidationFail-7**

  * TestType: Automated
  * RequirementID: []()
  * Title: Asice time-stamp does not correspond to the SignatureValue element
  * Expected Result: The document should fail the validation
  * File: TS-02_23634_TS_wrong_SignatureValue.asice

**TestCaseID: Bdoc-ValidationFail-8**

  * TestType: Automated
  * RequirementID: []()
  * Title: Asice No non-repudiation key usage value in the certificate
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-I-43.asice

**TestCaseID: Bdoc-ValidationFail-9**

  * TestType: Automated
  * RequirementID: []()
  * Title: Asice signers certificate does not have non-repudiation value in the certificates key usage field and it does not contain the QC and SSCD compliance information.
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-I-26.asice

**TestCaseID: Bdoc-ValidationFail-10**

  * TestType: Automated
  * RequirementID: []()
  * Title: Asice OCSP certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-I-27.asice

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Bdoc-ValidationFail-11**

  * TestType: Automated
  * RequirementID: []()
  * Title: Asice TSA certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: TS-05_23634_TS_unknown_TSA.asice

**TestCaseID: Bdoc-ValidationFail-12**

  * TestType: Automated
  * RequirementID: []()
  * Title: Asice OCSP response status is revoked
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-R-25.asice

**TestCaseID: Bdoc-ValidationFail-13**

  * TestType: Automated
  * RequirementID: []()
  * Title: Asice difference between OCSP and time-stamp issuing times is more than 24 hours
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-V-20.asice

**TestCaseID: Bdoc-ValidationFail-14**

  * TestType: Automated
  * RequirementID: []()
  * Title: Asice unsigned data files in the container
  * Expected Result: The document should fail the validation
  * File: EE_SER-AEX-B-LT-V-34.asice

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Bdoc-ValidationFail-15**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc different data file mime-type values in signatures.xml and manifest.xml files
  * Expected Result: The document should fail the validation
  * File: 23613_TM_wrong-manifest-mimetype.bdoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Bdoc-ValidationFail-16**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc SignatureValue does not correspond to the SignedInfo block
  * Expected Result: The document should fail the validation
  * File: REF-19_bdoc21-no-sig-asn1-pref.bdoc

**TestCaseID: Bdoc-ValidationFail-17**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc Baseline-BES file
  * Expected Result: The document should fail the validation
  * File: TM-05_bdoc21-good-nonce-policy-bes.bdoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Bdoc-ValidationFail-18**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc Baseline-EPES file
  * Expected Result: The document should fail the validation
  * File: TM-04_kehtivuskinnituset.4.asice

**TestCaseID: Bdoc-ValidationFail-19**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc signers certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: SS-4_teadmataCA.4.asice

**TestCaseID: Bdoc-ValidationFail-20**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc OCSP response status is revoked
  * Expected Result: The document should fail the validation
  * File: TM-15_revoked.4.asice

**TestCaseID: Bdoc-ValidationFail-21**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc OCSP response status is unknown
  * Expected Result: The document should fail the validation
  * File: TM-16_unknown.4.asice

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Bdoc-ValidationFail-22**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc signed data file has been removed from the container
  * Expected Result: The document should fail the validation
  * File: TM-16_unknown.4.asice

**TestCaseID: Bdoc-ValidationFail-23**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc no files in container
  * Expected Result: The document should fail the validation
  * File: KS-02_tyhi.bdoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


## BdocValidationPass.java

**TestCaseID: Bdoc-ValidationPass-1**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc with single valid signature
  * Expected Result: The document should pass the validation
  * File: Valid_ID_sig.bdoc

**TestCaseID: Bdoc-ValidationPass-2**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc with multiple valid signatures
  * Expected Result: The document should pass the validation
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: Bdoc-ValidationPass-3**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc with warning on signature
  * Expected Result: The document should pass the validation but warning should be returned
  * File:

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Bdoc-ValidationPass-4**

  * TestType: Automated
  * RequirementID: []()
  * Title: Asice One LT signature with certificates from different countries
  * Expected Result: The document should pass the validation
  * File: EE_SER-AEX-B-LT-V-30.asice

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Bdoc-ValidationPass-5**

  * TestType: Automated
  * RequirementID: []()
  * Title: Bdoc signed with Mobile-ID, ECC-SHA256 signature with prime256v1 key
  * Expected Result: The document should pass the validation
  * File: 24050_short_ecdsa_correct_file_mimetype.bdoc


## DdocValidationFail.java

**TestCaseID: Ddoc-ValidationFail-1**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc with single invalid signature
  * Expected Result: The document should fail the validation
  * File: test1-ddoc-revoked.ddoc

**TestCaseID: Ddoc-ValidationFail-2**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc with multiple invalid signatures
  * Expected Result: The document should fail the validation
  * File: multipleInvalidSignatures.ddoc

**TestCaseID: Ddoc-ValidationFail-3**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc with multiple signatures both valid and invalid
  * Expected Result: The document should fail the validation
  * File: multipleValidAndInvalidSignatures.ddoc

**TestCaseID: Ddoc-ValidationFail-4**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc with no signatures
  * Expected Result: The document should fail the validation
  * File: DdocContainerNoSignature.bdoc

**TestCaseID: Ddoc-ValidationFail-5**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc signature value has been changed (SignatureValue does not correspond to the SignedInfo block)
  * Expected Result: The document should fail the validation
  * File: test-inv-sig-inf.ddoc

**TestCaseID: Ddoc-ValidationFail-6**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc Data file(s) don't match the hash values in Reference elements
  * Expected Result: The document should fail the validation
  * File: AndmefailiAtribuudidMuudetud.ddoc

**TestCaseID: Ddoc-ValidationFail-7**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc Baseline-BES file, no OCSP response
  * Expected Result: The document should fail the validation
  * File: ilma_kehtivuskinnituseta.ddoc

**TestCaseID: Ddoc-ValidationFail-8**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc no non-repudiation key usage value in the certificate
  * Expected Result: The document should fail the validation
  * File: test-non-repu1.ddoc

**TestCaseID: Ddoc-ValidationFail-8**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc Signer's certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: Belgia_kandeavaldus_LIV.ddoc

**TestCaseID: Ddoc-ValidationFail-9**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc OCSP certificate is not trusted
  * Expected Result: The document should fail the validation
  * File: Tundmatu_OCSP_responder.ddoc

**TestCaseID: Ddoc-ValidationFail-10**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc has unsigned data files in the container
  * Expected Result: The document should fail the validation
  * File: lisatud_andmefail.ddoc

**TestCaseID: Ddoc-ValidationFail-11**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc signed data file has been removed from the container
  * Expected Result: The document should fail the validation
  * File: faileemald1.ddoc


## DdocValidationPass.java

**TestCaseID: Ddoc-ValidationPass-1**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc with single valid signature
  * Expected Result: The document should pass the validation
  * File: 23734-ddoc13-13basn1.ddoc

**TestCaseID: Ddoc-ValidationPass-2**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc v1.0 with multiple valid signatures
  * Expected Result: The document should pass the validation
  * File: DigiDoc_1.0_Tartu_ja_Tallinna_koostooleping.ddoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Ddoc-ValidationPass-3**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc v1.1 with multiple valid signatures v1.1
  * Expected Result: The document should pass the validation
  * File: igasugust1.1.ddoc

**TestCaseID: Ddoc-ValidationPass-4**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc v1.2 with multiple valid signatures
  * Expected Result: The document should pass the validation
  * File: igasugust1.2.ddoc

**TestCaseID: Ddoc-ValidationPass-5**

  * TestType: Automated
  * RequirementID: []()
  * Title: Ddoc v1.3 with multiple valid signatures
  * Expected Result: The document should pass the validation
  * File: igasugust1.3.ddoc


## DocumentFormatTests.java

**TestCaseID: DocumentFormat-1**

  * TestType: Automated
  * RequirementID: [(TBD)]((TBD))
  * Title: Validation of pdf document acceptance
  * Expected Result: Pdf is accepted and correct signature validation is given
  * File: hellopades-pades-lt-sha256-sign.pdf

**TestCaseID: DocumentFormat-2**

  * TestType: Automated
  * RequirementID: [(TBD)]((TBD))
  * Title: Validation of bdoc document acceptance
  * Expected Result: Bdoc is accepted and correct signature validation is given
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: DocumentFormat-3**

  * TestType: Automated
  * RequirementID: [(TBD)]((TBD))
  * Title: Validation of txt document rejection
  * Expected Result: Txt document is rejected and proper error message is given
  * File: hellopades-pades-lt-sha256-sign.txt

**TestCaseID: DocumentFormat-4**

  * TestType: Automated
  * RequirementID: [(TBD)]((TBD))
  * Title: Validation of p7s document rejection
  * Expected Result: P7s document is rejected and proper error message is given
  * File: hellocades.p7s

**TestCaseID: DocumentFormat-5**

  * TestType: Automated
  * RequirementID: [(TBD)]((TBD))
  * Title: Validation of zip document rejection
  * Expected Result: Zip document is rejected and proper error message is given
  * File: 42.zip

**TestCaseID: DocumentFormat-6**

  * TestType: Automated
  * RequirementID: [(TBD)]((TBD))
  * Title: Validation of doc document rejection
  * Expected Result: Doc document is rejected and proper error message is given
  * File: hellopades-pades-lt-sha256-sign.doc

**TestCaseID: DocumentFormat-7**

  * TestType: Automated
  * RequirementID: [(TBD)]((TBD))
  * Title: Validation of XML document rejection
  * Expected Result: XML document is rejected and proper error message is given
  * File: XML.xml

**TestCaseID: DocumentFormat-8**

  * TestType: Automated
  * RequirementID: [(TBD)]((TBD))
  * Title: Validation of png document rejection
  * Expected Result: Png document is rejected and proper error message is given
  * File: Picture.png


## LargeFileTests.java

**TestCaseID: PDF-LargeFiles-1**

  * TestType: Automated
  * RequirementID: []()
  * Title: Larger signed PDF files (PAdES Baseline LT).
  * Expected Result: Bigger documents with valid signature should pass
  * File: scout_x4-manual-signed_lt_9mb.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-LargeFiles-2**

  * TestType: Automated
  * RequirementID: []()
  * Title: Larger signed PDF files (PAdES Baseline LT).
  * Expected Result: Bigger documents with valid signature should pass
  * File: scout_x4-manual-signed_lta_9mb.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-LargeFiles-3**

  * TestType: Automated
  * RequirementID: []()
  * Title: Larger signed PDF files (PAdES Baseline LT).
  * Expected Result: Bigger documents with valid signature should pass
  * File: singlesignature_lt_1-2mb.pdf

**TestCaseID: PDF-LargeFiles-4**

  * TestType: Automated
  * RequirementID: []()
  * Title: Larger signed PDF files (PAdES Baseline LT).
  * Expected Result: Bigger documents with valid signature should pass
  * File: digidocservice-signed-lta-1-2mb.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-LargeFiles-5**

  * TestType: Automated
  * RequirementID: []()
  * Title: Larger signed PDF files (PAdES Baseline LT).
  * Expected Result: Bigger documents with valid signature should pass
  * File: singlesignature_lt_3-8mb.pdf

**TestCaseID: PDF-LargeFiles-6**

  * TestType: Automated
  * RequirementID: []()
  * Title: Larger signed PDF files (PAdES Baseline LT).
  * Expected Result: Bigger documents with valid signature should pass
  * File: egovenrment-benchmark-lta-3-8mb.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


## PdfBaselineProfileTests.java

**TestCaseID: PDF-BaselineProfile-1**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF has PAdES-B profile signature
  * Expected Result: Document validation should fail
  * File: hellopades-pades-b-sha256-auth.pdf

**TestCaseID: PDF-BaselineProfile-2**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF has PAdES-T profile signature
  * Expected Result: Document validation should fail
  * File:

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-BaselineProfile-3**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF has PAdES-LT profile signature
  * Expected Result: Document validation should pass
  * File: hellopades-pades-lt-sha256-sign.pdf

**TestCaseID: PDF-BaselineProfile-4**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF has PAdES-LTA profile signature
  * Expected Result: Document validation should pass
  * File:

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-BaselineProfile-5**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF has PAdES-LT and B profile signature
  * Expected Result: Document validation should fail
  * File: hellopades-lt-b.pdf

**TestCaseID: PDF-BaselineProfile-6**

  * TestType: Automated
  * RequirementID: []()
  * Title: PDF document message digest attribute value does not match calculate value
  * Expected Result: Document validation should fail
  * File: hellopades-lt1-lt2-wrongDigestValue.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-BaselineProfile-7**

  * TestType: Automated
  * RequirementID: []()
  * Title: PDF file with a serial signature
  * Expected Result: Document signed with multiple signers with serial signatures should pass
  * File: hellopades-lt1-lt2-Serial.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-BaselineProfile-8**

  * TestType: Automated
  * RequirementID: []()
  * Title: PDF document signed with multiple signers parallel signature
  * Expected Result: Document with parallel signatures should pass
  * File: hellopades-lt1-lt2-parallel3.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-BaselineProfile-9**

  * TestType: Automated
  * RequirementID: []()
  * Title: PDF document signed with multiple signers parallel signature without Sscd
  * Expected Result: Document with no qualified and without SSCD should fail
  * File: hellopades-lt1-lt2-parallel3.pdf


## PdfSignatureCryptographicAlgorithmTests.java

**TestCaseID: PDF-SigCryptoAlg-1**

  * TestType: Automated
  * RequirementID: []()
  * Title: SHA512 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with SHA512 algorithm should pass
  * File: hellopades-lt-sha512.pdf

**TestCaseID: PDF-SigCryptoAlg-2**

  * TestType: Automated
  * RequirementID: []()
  * Title: SHA1 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with SHA1 algorithm should pass
  * File: hellopades-lt-sha1.pdf

**TestCaseID: PDF-SigCryptoAlg-3**

  * TestType: Automated
  * RequirementID: []()
  * Title: ECDSA algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with ECDSA algorithm should pass
  * File: hellopades-ecdsa.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-SigCryptoAlg-4**

  * TestType: Automated
  * RequirementID: []()
  * Title: ECDSA224 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with ECDSA224 algorithm should pass
  * File: hellopades-lt-sha256-ec224.pdf

**TestCaseID: PDF-SigCryptoAlg-5**

  * TestType: Automated
  * RequirementID: []()
  * Title: ECDSA256 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with ECDSA256 algorithm should pass
  * File: hellopades-lt-sha256-ec256.pdf

**TestCaseID: PDF-SigCryptoAlg-6**

  * TestType: Automated
  * RequirementID: []()
  * Title: RSA1024 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with RSA1024 algorithm should pass
  * File: hellopades-lt-sha256-rsa1024.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-SigCryptoAlg-7**

  * TestType: Automated
  * RequirementID: []()
  * Title: RSA1023 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with RSA1023 algorithm should pass
  * File: hellopades-lt-sha256-rsa1023.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-SigCryptoAlg-8**

  * TestType: Automated
  * RequirementID: []()
  * Title: RSA2047 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with RSA2047 algorithm should pass
  * File: hellopades-lt-sha256-rsa2047.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-SigCryptoAlg-9**

  * TestType: Automated
  * RequirementID: []()
  * Title: RSA2048 algorithms (PAdES Baseline LT)
  * Expected Result: Document signed with RSA2048 algorithm should pass
  * File: PdfValidSingleSignature


## PdfValidationFail.java

**TestCaseID: PDF-ValidationFail-1**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has been signed with certificate that is expired before signing (PAdES Baseline LT)
  * Expected Result: Document signed with certificate that expired before signing should fail.
  * File: hellopades-lt-rsa1024-sha1-expired.pdf

**TestCaseID: PDF-ValidationFail-2**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has been signed with expired certificate (PAdES Baseline LT)
  * Expected Result: Document signed with certificate that is expired should fail.
  * File: hellopades-lt-rsa1024-sha1-expired.pdf

**TestCaseID: PDF-ValidationFail-3**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has been signed with revoked certificate (PAdES Baseline LT)
  * Expected Result: Document signed with certificate that is revoked should fail.
  * File: pades_lt_revoked.pdf

**TestCaseID: PDF-ValidationFail-4**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has been signed with certificate that missing signed attribute (PAdES Baseline LT)
  * Expected Result: PDF-file validation should fail
  * File: missing_signing_certificate_attribute.pdf

**TestCaseID: PDF-ValidationFail-5**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has been signed with certificate which has no non repudiation key usage attribute (PAdES Baseline LT)
  * Expected Result: The PDF-file validation should fail with error.
  * File: hellopades-pades-lt-sha256-auth.pdf

**TestCaseID: PDF-ValidationFail-6**

  * TestType: Automated
  * RequirementID: []()
  * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
  * Expected Result: Document signed with expired certificate should fail
  * File: hellopades-lt-sha256-rsa2048-expired.pdf

**TestCaseID: PDF-ValidationFail-7**

  * TestType: Automated
  * RequirementID: []()
  * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
  * Expected Result: Document signed with expired certificate should fail
  * File: hellopades-lt-sha256-rsa1024-expired2.pdf

**TestCaseID: PDF-ValidationFail-8**

  * TestType: Automated
  * RequirementID: []()
  * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
  * Expected Result: Document signed with expired certificate should fail
  * File: hellopades-lt-sha1-rsa1024-expired2.pdf


## PdfValidationPass.java

**TestCaseID: PDF-ValidationPass-1**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has been signed with certificate that is expired after signing (PAdES Baseline LT)
  * Expected Result: Document signed with certificate that expired after signing should pass.
  * File: hellopades-lt-sha256-rsa1024-not-expired.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-ValidationPass-2**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has been signed with certificate that will expire in 7 days after signing (PAdES Baseline LT)
  * Expected Result: Document signed with certificate that expired after signing should pass.
  * File: hellopades-lt-sha256-rsa2048-7d.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-ValidationPass-3**

  * TestType: Automated
  * RequirementID: []()
  * Title: Certificate contents are include in response (PAdES Baseline LT)
  * Expected Result: The PDF-file validation should pass
  * File: hellopades-lt-sha256-ocsp-15min1s.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-ValidationPass-4**

  * TestType: Automated
  * RequirementID: []()
  * Title: Pdf with single valid signature
  * Expected Result: Document should pass.
  * File: PdfValidSingleSignature.pdf


## SignaturePolicyTests.java

**TestCaseID: PDF-SigPol-1**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
  * Expected Result: validation should fail with error when signature policy is set to a non-existing one
  * File: hellopades-lt-sha256-ocsp-28h.pdf

**TestCaseID: PDF-SigPol-2**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
  * Expected Result: Document with over 24h delay should fail when signature policy is set to "EE"
  * File: hellopades-lt-sha256-ocsp-28h.pdf

**TestCaseID: PDF-SigPol-3**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
  * Expected Result: Document with over 24h delay should pass when signature policy is set to "EU"
  * File: hellopades-lt-sha256-ocsp-28h.pdf

**TestCaseID: PDF-SigPol-4**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
  * Expected Result: Document with over 24h delay should fail when signature policy is not set or empty, because it defaults to "EE"
  * File: hellopades-lt-sha256-ocsp-28h.pdf

**TestCaseID: PDF-SigPol-5**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF has PAdES-LT and B profile signature
  * Expected Result: 1 of 2 signatures' should pass when signature policy is set to "EE"
  * File: hellopades-lt-b.pdf

**TestCaseID: PDF-SigPol-6**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF has PAdES-LT and B profile signature
  * Expected Result: 2 of 2 signatures' validation should pass when signature policy is set to "EU"
  * File: hellopades-lt-b.pdf

**TestCaseID: BDOC-SigPol-1**

  * TestType: Automated
  * RequirementID: []()
  * Title: Valid_ID_sig.bdoc
  * Expected Result: validation should fail with error when signature policy is set to a non-existing one
  * File: Valid_ID_sig.bdoc

**TestCaseID: BDOC-SigPol-2**

  * TestType: Automated
  * RequirementID: []()
  * Title: Valid_ID_sig.bdoc
  * Expected Result: Document should pass when signature policy is set to "EE"
  * File: Valid_ID_sig.bdoc


## SignatureRevocationValueTests.java

**TestCaseID: PDF-SigRevocVal-1**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation that is more than 15 minutes later than the signatures Time Stamp.
  * Expected Result: Document with ocsp over 15 min delay should pass but warn
  * File: hellopades-lt-sha256-ocsp-15min1s.pdf

**TestCaseID: PDF-SigRevocVal-2**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation that is more than 15 minutes later than the signatures Time Stamp.
  * Expected Result: Document with ocsp over 15 min delay should pass but warn
  * File: hellopades-lt-sha256-ocsp-15min1s.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-SigRevocVal-3**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
  * Expected Result: Document with over 24h delay should fail
  * File: hellopades-lt-sha256-ocsp-28h.pdf

**TestCaseID: PDF-SigRevocVal-4**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
  * Expected Result: Document with over 24h delay should fail
  * File: hellopades-lt-sha256-ocsp-28h.pdf

**TestCaseID: PDF-SigRevocVal-5**

  * TestType: Automated
  * RequirementID: []()
  * Title: The PDF-file has been signed with PAdES Baseline LTA profile signature, the signature contains CRL.
  * Expected Result: Document with no ocsp or crl in signature should fail
  * File: hellopades-lta-no-ocsp.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: PDF-SigRevocVal-6**

  * TestType: Automated
  * RequirementID: []()
  * Title: PDF signature has OCSP confirmation before Time Stamp
  * Expected Result: Document signed with ocsp time value before best signature time should fail
  * File: hellopades-lt-sha256-rsa2048-ocsp-before-ts.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


## ValidationReportJsonStructureVerification.java

**TestCaseID: Bdoc-ValidationReport-1**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: JSON structure has all elements (Bdoc valid single signature)
  * Expected Result: All required elements are present according to BdocDocSimpleReportSchema.json
  * File: Valid_ID_sig.bdoc

**TestCaseID: Bdoc-ValidationReport-2**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: JSON structure has all elements (Bdoc valid multiple signatures)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: Baltic MoU digital signing_EST_LT_LV.bdoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Bdoc-ValidationReport-3**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: JSON structure has all elements (Bdoc invalid single signature)
  * Expected Result: All required elements are present according to BdocDocSimpleReportSchema.json
  * File: IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc

**TestCaseID: Bdoc-ValidationReport-4**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: JSON structure has all elements (Bdoc indeterminate status)
  * Expected Result: All required elements are present according to BdocDocSimpleReportSchema.json
  * File: test1-bdoc-unknown.bdoc

**TestCaseID: Bdoc-ValidationReport-5**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Check for optional subindication and error elements
  * Expected Result: Error and subindication elements are present
  * File: IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc

**TestCaseID: Bdoc-ValidationReport-6**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Check for optional warning element
  * Expected Result: Warning element is present
  * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc

**TestCaseID: Bdoc-ValidationReport-7**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title:  Bdoc report with no signatures
  * Expected Result: Report is returned with required elements
  * File:BdocContainerNoSignature.bdoc

**TestCaseID: Pdf-ValidationReport-8**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: JSON structure has all elements (Pdf valid single signature)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: hellopades-lt-sha256-ec256.pdf

**TestCaseID: Pdf-ValidationReport-9**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: JSON structure has all elements (Pdf valid Multiple signatures)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File:

**TestCaseID: Pdf-ValidationReport-10**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: JSON structure has all elements (Pdf invalid signature)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: hellopades-lt-b.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Pdf-ValidationReport-11**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: JSON structure has all elements (Pdf indeterminate status)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: hellopades-lt-rsa1024-sha1-expired.pdf

**TestCaseID: Pdf-ValidationReport-12**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title:  Pdf report with no signatures
  * Expected Result: Report is returned with required elements
  * File: PdfNoSignature.pdf

**TestCaseID: Ddoc-ValidationReport-13**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: JSON structure has all elements (ddoc valid single signature)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: 18912.ddoc

**TestCaseID: Ddoc-ValidationReport-14**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: JSON structure has all elements (ddoc valid Multiple signatures)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: igasugust1.1.ddoc

**TestCaseID: Ddoc-ValidationReport-15**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: JSON structure has all elements (ddoc invalid signature)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: test1-ddoc-revoked.ddoc

**TestCaseID: Ddoc-ValidationReport-16**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: JSON structure has all elements (ddoc indeterminate status)
  * Expected Result: All required elements are present according to SimpleReportSchema.json
  * File: test1-ddoc-unknown.ddoc

**TestCaseID: Ddoc-ValidationReport-17**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Check for optional subindication and error elements
  * Expected Result: Error and subindication elements are present
  * File: test1-ddoc-unknown.ddoc

**TestCaseID: Ddoc-ValidationReport-18**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Check for optional warning element
  * Expected Result: Warning element is present
  * File:

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Ddoc-ValidationReport-19**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title:  Ddoc report with no signatures
  * Expected Result: Report is returned with required elements
  * File: DdocContainerNoSignature.ddoc


## ValidationReportValueVerification.java

**TestCaseID: Bdoc-ValidationReportValue-1**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: Valid_ID_sig.bdoc

**TestCaseID: Bdoc-ValidationReportValue-2**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23635_bdoc_ts_OCSP_random_nonce.bdoc

**TestCaseID: Bdoc-ValidationReportValue-3**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT, AdES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc

**TestCaseID: Bdoc-ValidationReportValue-4**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, AdESqc, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23200_weakdigest-wrong-nonce.asice

**TestCaseID: Bdoc-ValidationReportValue-5**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LTA, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: EE_SER-AEX-B-LTA-V-24.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Ddoc-ValidationReportValue-6**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report, xml v1.0, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: DigiDoc_1.0_Tartu_ja_Tallinna_koostooleping.ddoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Ddoc-ValidationReportValue-7**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report, xml v1.1, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.1.ddoc

**TestCaseID: Ddoc-ValidationReportValue-8**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report, xml v1.2, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.2.ddoc

**TestCaseID: Ddoc-ValidationReportValue-9**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report, xml v1.3, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.3.ddoc


## ValidationRequestTests.java

**TestCaseID: ValidationRequest-1**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Input random base64 string as document with bdoc document type
  * Expected Result: Error is returned stating problem in document
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-2**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Input random base64 string as document with pdf document type
  * Expected Result: Error is returned stating problem in document
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-3**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Input random base64 string as document with ddoc document type
  * Expected Result: Error is returned stating problem in document
  * File:

**TestCaseID: ValidationRequest-4**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Expected Result: Errors are returned stating the missing values
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-5**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Request with not base64 string as document
  * Expected Result: Error is returned stating encoding problem
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-6**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of wrong document type as input
  * Expected Result: Correct error code is returned
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-7**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Mismatch in documentType and actual document (bdoc and pdf)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-8**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Mismatch in documentType and actual document (asice and bdoc)
  * Expected Result: Error is returned
  * File: TS-11_23634_TS_2_timestamps.asice

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: ValidationRequest-9**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of case insensitivity in document type
  * Expected Result: Report is returned
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-10**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of filename value (filename do not match the actual file)
  * Expected Result: The same filename is returned as sent in the request
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-11**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Request has invalid character in filename
  * Expected Result: Correct error code is returned
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-12**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Request has invalid key on document position
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-13**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Request has XML as document type (special case, XML is similar to ddoc and was a accepted document type in earlier versions)
  * Expected Result: Error is given
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-14**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Request has long (38784 characters) in filename field
  * Expected Result: Report is returned with the same filename
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-15**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Totally empty request body is sent
  * Expected Result: Error is given
  * File: None

**TestCaseID: ValidationRequest-16**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Request with more parameters than expected is sent
  * Expected Result: Error is given or extra parameters are ignored?
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-17**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Request with special chars is sent
  * Expected Result: Validation report is returned
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-18**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Mismatch in documentType and actual document (ddoc and bdoc)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc

**TestCaseID: ValidationRequest-19**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Mismatch in documentType and actual document (ddoc and pdf)
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf

**TestCaseID: ValidationRequest-19**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Mismatch in documentType and actual document (bdoc and pdf)
  * Expected Result: Error is returned
  * File: PdfValidSingleSignature.pdf

**TestCaseID: ValidationRequest-20**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Mismatch in documentType and actual document (bdoc and ddoc)
  * Expected Result: Error is returned
  * File: igasugust1.3.ddoc

**TestCaseID: ValidationRequest-21**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Mismatch in documentType and actual document (pdf and ddoc)
  * Expected Result: Error is returned
  * File: igasugust1.3.ddoc

**TestCaseID: ValidationRequest-22**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Mismatch in documentType and actual document (pdf and bdoc)
  * Expected Result: Error is returned
  * File: Valid_IDCard_MobID_signatures.bdoc


## SoapValidationReportValueTests.java

**TestCaseID: Soap-ValidationReportValue-1**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: Valid_ID_sig.bdoc

**TestCaseID: Soap-ValidationReportValue-2**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23635_bdoc_ts_OCSP_random_nonce.bdoc

**TestCaseID: Soap-ValidationReportValue-3**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT, AdES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc

**TestCaseID: Soap-ValidationReportValue-4**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, AdESqc, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: 23200_weakdigest-wrong-nonce.asice

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Soap-ValidationReportValue-5**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report XAdES_BASELINE_LTA, QES, FullSignatureScope
  * Expected Result: All required elements are present and meet the expected values.
  * File: EE_SER-AEX-B-LTA-V-24.pdf

**TestCaseID: Soap-ValidationReportValue-6**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report, xml v1.0, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: DigiDoc_1.0_Tartu_ja_Tallinna_koostooleping.ddoc

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 

**TestCaseID: Soap-ValidationReportValue-7**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report, xml v1.1, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.1.ddoc

**TestCaseID: Soap-ValidationReportValue-8**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report, xml v1.2, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.2.ddoc

**TestCaseID: Soap-ValidationReportValue-9**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report, xml v1.3, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: Igasugust1.3.ddoc

**TestCaseID: Soap-ValidationReportValue-10**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report, PAdES_baseline_LT, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: pades_lt_two_valid_sig.pdf

**TestCaseID: Soap-ValidationReportValue-11**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Verification of values in Validation Report, PAdES_baseline_B, checks for missing info
  * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
  * File: hellopades-pades-b-sha256-auth.pdf

  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** 


## SoapValidationRequestTests.java

**TestCaseID: Soap-ValidationRequest-1**

  * TestType: Automated
  * RequirementID: [http://open-eid.github.io/SiVa/siva/interface_description/](http://open-eid.github.io/SiVa/siva/interface_description/)
  * Title: Input random base64 string as document with bdoc document type
  * Expected Result: Error is returned stating problem in document
  * File: Valid_IDCard_MobID_signatures.bdoc

