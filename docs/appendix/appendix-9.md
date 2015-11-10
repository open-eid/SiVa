Appendix 9 - Sample Response Data Blocks – negative response
============================================================

The Simple Report
-----------------

```xml
<xmlSimpleReport><?xml version="1.0" encoding="UTF-8"?><SimpleReport xmlns="http://dss.esig.europa.eu/validation/diagnostic">
	<Policy>
		<PolicyName>QES AdESQC TL based</PolicyName>
		<PolicyDescription>Validate electronic signatures and indicates whether they are Advanced electronic Signatures (AdES), AdES supported by a
		Qualified Certificate (AdES/QC) or a Qualified electronic
		Signature (QES). All certificates and their related chains supporting the signatures are validated against the EU Member State
		Trusted Lists (this includes signer's certificate and certificates used
		to validate certificate validity status services - CRLs, OCSP, and time-stamps).</PolicyDescription>
	</Policy>
	<ValidationTime>2015-08-24T14:39:09Z</ValidationTime>
	<DocumentName>/fixedpath/file.pdf</DocumentName>
	<Signature Id="id-9505f60eeee910adeb6091e18f9a831f780e552845d04b66868301a5cf0ed8ba" SignatureFormat="PAdES_BASELINE_T">
		<SigningTime>2014-11-19T09:59:19Z</SigningTime>
		<SignedBy>SINIVEE,VEIKO,36706020210</SignedBy>
		<Indication>INDETERMINATE</Indication>
		<SubIndication>TRY_LATER</SubIndication>
		<Error CertificateId="0446cb4cc1dd99d0839a202b6be0f743d146cf03807f484ba4ec588e645d7da0" NameId="BBB_XCV_IRDPFC_ANS">No revocation data for the certificate</Error>
		<SignatureLevel>QES</SignatureLevel>
		<SignatureScopes>
			<SignatureScope name="Full PDF" scope="FullSignatureScope">Full document</SignatureScope>
		</SignatureScopes>
	</Signature>
	<ValidSignaturesCount>0</ValidSignaturesCount>
	<SignaturesCount>1</SignaturesCount>
</SimpleReport>
</xmlSimpleReport>
```

The Detailed Report
-------------------

```xml
<ValidationData xmlns="http://dss.esig.europa.eu/validation/diagnostic">
	<BasicBuildingBlocks>
		<Signature Id="id-9505f60eeee910adeb6091e18f9a831f780e552845d04b66868301a5cf0ed8ba">
			<ISC>
				<Constraint>
					<Name NameId="BBB_ICS_ISCI">Is there an identified candidate for the signing certificate?</Name>
					<Status>OK</Status>
					<Info CertificateId="0446cb4cc1dd99d0839a202b6be0f743d146cf03807f484ba4ec588e645d7da0"/>
				</Constraint>
				<Constraint>
					<Name NameId="BBB_ICS_ISASCP">Is the signed attribute: 'signing-certificate' present?</Name>
					<Status>OK</Status>
				</Constraint>
				<Constraint>
					<Name NameId="BBB_ICS_ISACDP">Is the signed attribute: 'cert-digest' of the certificate present?</Name>
					<Status>OK</Status>
				</Constraint>
				<Constraint>
					<Name NameId="BBB_ICS_ICDVV">Is the certificate's digest value valid?</Name>
					<Status>OK</Status>
				</Constraint>
				<Constraint>
					<Name NameId="BBB_ICS_AIDNASNE">Are the issuer distinguished name and the serial number equal?</Name>
					<Status>OK</Status>
				</Constraint>
				<Conclusion>
					<Indication>VALID</Indication>
				</Conclusion>
			</ISC>
			<VCI>
				<Constraint>
					<Name NameId="BBB_VCI_ISPK">Is the signature policy known?</Name>
					<Status>OK</Status>
					<Info Identifier="NO_POLICY"/>
				</Constraint>
				<Conclusion>
					<Indication>VALID</Indication>
				</Conclusion>
			</VCI>
			<CV>
				<Constraint>
					<Name NameId="BBB_CV_IRDOF">Is the reference data object(s) found?</Name>
					<Status>OK</Status>
				</Constraint>
				<Constraint>
					<Name NameId="BBB_CV_IRDOI">Is the reference data object(s) intact?</Name>
					<Status>OK</Status>
				</Constraint>
				<Constraint>
					<Name NameId="BBB_CV_ISI">Is the signature intact?</Name>
					<Status>OK</Status>
				</Constraint>
				<Conclusion>
					<Indication>VALID</Indication>
				</Conclusion>
			</CV>
			<SAV>
				<Constraint>
					<Name NameId="BBB_SAV_ISQPSTP">Is signed qualifying property: 'signing-time' present?</Name>
					<Status>OK</Status>
				</Constraint>
				<Constraint>
					<Name NameId="ASCCM">Are signature cryptographic constraints met?</Name>
					<Status>OK</Status>
				</Constraint>
				<Conclusion>
					<Indication>VALID</Indication>
				</Conclusion>
			</SAV>
			<XCV>
				<Constraint>
					<Name NameId="BBB_XCV_ICTIVRSC">Is the current time in the validity range of the signer's certificate?</Name>
					<Status>IGNORED</Status>
				</Constraint>
				<Constraint>
					<Name NameId="BBB_XCV_CCCBB">Can the certificate chain be built till the trust anchor?</Name>
					<Status>OK</Status>
				</Constraint>
				<Constraint>
					<Name NameId="BBB_XCV_ISCGKU">Has the signer's certificate given key-usage?</Name>
					<Status>OK</Status>
					<Info CertificateId="0446cb4cc1dd99d0839a202b6be0f743d146cf03807f484ba4ec588e645d7da0"/>
				</Constraint>
				<Constraint>
					<Name NameId="BBB_XCV_ICSI">Is the certificate's signature intact?</Name>
					<Status>OK</Status>
					<Info CertificateId="0446cb4cc1dd99d0839a202b6be0f743d146cf03807f484ba4ec588e645d7da0"/>
				</Constraint>
				<Constraint>
					<Name NameId="BBB_XCV_IRDPFC">Is the revocation data present for the certificate?</Name>
					<Status>NOT OK</Status>
				</Constraint>
				<Conclusion>
					<Indication>INDETERMINATE</Indication>
					<SubIndication>TRY_LATER</SubIndication>
					<Error CertificateId="0446cb4cc1dd99d0839a202b6be0f743d146cf03807f484ba4ec588e645d7da0" NameId="BBB_XCV_IRDPFC_ANS">No revocation data for the certificate</Error>
				</Conclusion>
			</XCV>
			<Conclusion>
				<Indication>INDETERMINATE</Indication>
				<SubIndication>TRY_LATER</SubIndication>
				<Error CertificateId="0446cb4cc1dd99d0839a202b6be0f743d146cf03807f484ba4ec588e645d7da0" NameId="BBB_XCV_IRDPFC_ANS">No revocation data for the certificate</Error>
			</Conclusion>
		</Signature>
	</BasicBuildingBlocks>
	<BasicValidationData>
		<Signature Id="id-9505f60eeee910adeb6091e18f9a831f780e552845d04b66868301a5cf0ed8ba">
			<Conclusion>
				<Indication>INDETERMINATE</Indication>
				<SubIndication>TRY_LATER</SubIndication>
				<Error CertificateId="0446cb4cc1dd99d0839a202b6be0f743d146cf03807f484ba4ec588e645d7da0" NameId="BBB_XCV_IRDPFC_ANS">No revocation data for the certificate</Error>
			</Conclusion>
		</Signature>
	</BasicValidationData>
	<TimestampValidationData>
		<Signature Id="id-9505f60eeee910adeb6091e18f9a831f780e552845d04b66868301a5cf0ed8ba">
			<Timestamp Id="68fabd59056e697ede41e42e53014cc61b5e2c1fb7a9b26cc5ac357f30d8c52e" Type="SIGNATURE_TIMESTAMP">
				<BasicBuildingBlocks>
					<ISC>
						<Constraint>
							<Name NameId="BBB_ICS_ISCI">Is there an identified candidate for the signing certificate?</Name>
							<Status>OK</Status>
							<Info CertificateId="1e49f497d89d430aad534b622d82bd9b9d0d4afdb7b7d36986c5df0981d9067d"/>
						</Constraint>
						<Conclusion>
							<Indication>VALID</Indication>
						</Conclusion>
					</ISC>
					<CV>
						<Constraint>
							<Name NameId="BBB_CV_IRDOF">Is the reference data object(s) found?</Name>
							<Status>OK</Status>
						</Constraint>
						<Constraint>
							<Name NameId="BBB_CV_IRDOI">Is the reference data object(s) intact?</Name>
							<Status>OK</Status>
						</Constraint>
						<Constraint>
							<Name NameId="BBB_CV_ISI">Is the signature intact?</Name>
							<Status>OK</Status>
						</Constraint>
						<Conclusion>
							<Indication>VALID</Indication>
						</Conclusion>
					</CV>
					<SAV>
						<Constraint>
							<Name NameId="ASCCM">Are signature cryptographic constraints met?</Name>
							<Status>OK</Status>
						</Constraint>
						<Conclusion>
							<Indication>VALID</Indication>
						</Conclusion>
					</SAV>
					<XCV>
						<Constraint>
							<Name NameId="BBB_XCV_ICTIVRSC">Is the current time in the validity range of the signer's certificate?</Name>
							<Status>OK</Status>
						</Constraint>
						<Constraint>
							<Name NameId="BBB_XCV_CCCBB">Can the certificate chain be built till the trust anchor?</Name>
							<Status>OK</Status>
						</Constraint>
						<Conclusion>
							<Indication>VALID</Indication>
						</Conclusion>
					</XCV>
					<Conclusion>
						<Indication>VALID</Indication>
					</Conclusion>
				</BasicBuildingBlocks>
			</Timestamp>
		</Signature>
	</TimestampValidationData>
	<AdESTValidationData>
		<Signature Id="id-9505f60eeee910adeb6091e18f9a831f780e552845d04b66868301a5cf0ed8ba">
			<Constraint>
				<Name NameId="ADEST_ROBVPIIC">Is the result of the Basic Validation Process conclusive?</Name>
				<Status>NOT OK</Status>
			</Constraint>
			<Conclusion>
				<Indication>INDETERMINATE</Indication>
				<SubIndication>TRY_LATER</SubIndication>
				<Error CertificateId="0446cb4cc1dd99d0839a202b6be0f743d146cf03807f484ba4ec588e645d7da0" NameId="BBB_XCV_IRDPFC_ANS">No revocation data for the certificate</Error>
			</Conclusion>
		</Signature>
	</AdESTValidationData>
	<LongTermValidationData>
		<Signature Id="id-9505f60eeee910adeb6091e18f9a831f780e552845d04b66868301a5cf0ed8ba">
			<Constraint>
				<Name NameId="PSV_IATVC">Is AdES-T validation conclusive?</Name>
				<Status>NOT OK</Status>
				<Info Field="Indication">INDETERMINATE</Info>
				<Info Field="SubIndication">TRY_LATER</Info>
			</Constraint>
			<Conclusion>
				<Indication>INDETERMINATE</Indication>
				<SubIndication>TRY_LATER</SubIndication>
				<Error CertificateId="0446cb4cc1dd99d0839a202b6be0f743d146cf03807f484ba4ec588e645d7da0" NameId="BBB_XCV_IRDPFC_ANS">No revocation data for the certificate</Error>
			</Conclusion>
		</Signature>
	</LongTermValidationData>
</ValidationData>
```

The Diagnostic Data
-------------------

```xml
<DiagnosticData xmlns="http://dss.esig.europa.eu/validation/diagnostic">
   <DocumentName>/fixedpath/file.pdf</DocumentName>
   <Signature Id="id-9505f60eeee910adeb6091e18f9a831f780e552845d04b66868301a5cf0ed8ba">
      <DateTime>2014-11-19T09:59:19Z</DateTime>
      <SignatureFormat>PAdES_BASELINE_T</SignatureFormat>
      <BasicSignature>
         <EncryptionAlgoUsedToSignThisToken>RSA</EncryptionAlgoUsedToSignThisToken>
         <KeyLengthUsedToSignThisToken>2048</KeyLengthUsedToSignThisToken>
         <DigestAlgoUsedToSignThisToken>SHA256</DigestAlgoUsedToSignThisToken>
         <ReferenceDataFound>true</ReferenceDataFound>
         <ReferenceDataIntact>true</ReferenceDataIntact>
         <SignatureIntact>true</SignatureIntact>
         <SignatureValid>true</SignatureValid>
      </BasicSignature>
      <SigningCertificate Id="0446cb4cc1dd99d0839a202b6be0f743d146cf03807f484ba4ec588e645d7da0">
         <AttributePresent>true</AttributePresent>
         <DigestValuePresent>true</DigestValuePresent>
         <DigestValueMatch>true</DigestValueMatch>
         <IssuerSerialMatch>true</IssuerSerialMatch>
      </SigningCertificate>
      <CertificateChain>
         <ChainCertificate Id="0446cb4cc1dd99d0839a202b6be0f743d146cf03807f484ba4ec588e645d7da0">
            <Source>SIGNATURE</Source>
         </ChainCertificate>
         <ChainCertificate Id="41ec808e33cca8659eaea81670d6c7dc01446636e1f227561b6307b80ba63862">
            <Source>TRUSTED_LIST</Source>
         </ChainCertificate>
      </CertificateChain>
      <ContentType>application/pdf</ContentType>
      <Timestamps>
         <Timestamp Id="68fabd59056e697ede41e42e53014cc61b5e2c1fb7a9b26cc5ac357f30d8c52e" Type="SIGNATURE_TIMESTAMP">
            <ProductionTime>2014-11-19T09:59:28Z</ProductionTime>
            <SignedDataDigestAlgo>SHA256</SignedDataDigestAlgo>
            <EncodedSignedDataDigestValue>dNX6ublwUduBBLiHmnDxKXMyuLJs6f2cxS0gP8VkzrU=</EncodedSignedDataDigestValue>
            <MessageImprintDataFound>true</MessageImprintDataFound>
            <MessageImprintDataIntact>true</MessageImprintDataIntact>
            <BasicSignature>
               <EncryptionAlgoUsedToSignThisToken>RSA</EncryptionAlgoUsedToSignThisToken>
               <KeyLengthUsedToSignThisToken>2048</KeyLengthUsedToSignThisToken>
               <DigestAlgoUsedToSignThisToken>SHA256</DigestAlgoUsedToSignThisToken>
               <ReferenceDataFound>true</ReferenceDataFound>
               <ReferenceDataIntact>true</ReferenceDataIntact>
               <SignatureIntact>true</SignatureIntact>
               <SignatureValid>true</SignatureValid>
            </BasicSignature>
            <SigningCertificate Id="1e49f497d89d430aad534b622d82bd9b9d0d4afdb7b7d36986c5df0981d9067d"/>
            <CertificateChain>
               <ChainCertificate Id="1e49f497d89d430aad534b622d82bd9b9d0d4afdb7b7d36986c5df0981d9067d">
                  <Source>TRUSTED_LIST</Source>
               </ChainCertificate>
            </CertificateChain>
            <SignedObjects>
               <SignedSignature Id="id-9505f60eeee910adeb6091e18f9a831f"/>
               <DigestAlgAndValue Category="CERTIFICATE">
                  <DigestMethod>http://www.w3.org/2001/04/xmlenc#sha256</DigestMethod>
                  <DigestValue>BEbLTMHdmdCDmiAra+D3Q9FGzwOAf0hLpOxYjmRdfaA=</DigestValue>
               </DigestAlgAndValue>
            </SignedObjects>
         </Timestamp>
      </Timestamps>
      <SignatureScopes>
         <SignatureScope name="Full PDF" scope="FullSignatureScope">Full document</SignatureScope>
      </SignatureScopes>
   </Signature>
   <UsedCertificates>
      <Certificate Id="1e49f497d89d430aad534b622d82bd9b9d0d4afdb7b7d36986c5df0981d9067d">
         <SubjectDistinguishedName Format="CANONICAL">cn=sk timestamping authority,ou=tsa,o=as sertifitseerimiskeskus,c=ee</SubjectDistinguishedName>
         <SubjectDistinguishedName Format="RFC2253">CN=SK TIMESTAMPING AUTHORITY,OU=TSA,O=AS Sertifitseerimiskeskus,C=EE</SubjectDistinguishedName>
         <IssuerDistinguishedName Format="CANONICAL">1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=ee certification centre root ca,o=as sertifitseerimiskeskus,c=ee</IssuerDistinguishedName>
         <IssuerDistinguishedName Format="RFC2253">1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=EE Certification Centre Root CA,O=AS Sertifitseerimiskeskus,C=EE</IssuerDistinguishedName>
         <SerialNumber>48765665071482659663074918749208248665</SerialNumber>
         <DigestAlgAndValue>
            <DigestMethod>SHA1</DigestMethod>
            <DigestValue>stAhgvC5biocaH7OMjQII5gZMLY=</DigestValue>
         </DigestAlgAndValue>
         <NotAfter>2019-09-16T08:40:38Z</NotAfter>
         <NotBefore>2014-09-16T08:40:38Z</NotBefore>
         <PublicKeySize>2048</PublicKeySize>
         <PublicKeyEncryptionAlgo>RSA</PublicKeyEncryptionAlgo>
         <KeyUsageBits>
            <KeyUsage>digitalSignature</KeyUsage>
            <KeyUsage>nonRepudiation</KeyUsage>
         </KeyUsageBits>
         <X509Data>MIIEDTCCAvWgAwIBAgIQJK/s6xJo0AJUF/eG7W8BWTANBgkqhkiG9w0BAQsFADB1MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEoMCYGA1UEAwwfRUUgQ2VydGlmaWNhdGlvbiBDZW50cmUgUm9vdCBDQTEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVlMB4XDTE0MDkxNjA4NDAzOFoXDTE5MDkxNjA4NDAzOFowYzELMAkGA1UEBhMCRUUxIjAgBgNVBAoMGUFTIFNlcnRpZml0c2VlcmltaXNrZXNrdXMxDDAKBgNVBAsMA1RTQTEiMCAGA1UEAwwZU0sgVElNRVNUQU1QSU5HIEFVVEhPUklUWTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAJPa/dQKemSKCNSwlMUp9YKQY6zQOfs9vgUnbzTRHCRBRdsabZYknxTI4DqQ5+JPqw8MTkDvb6nfDZGd15t4oY4tHXXoCfRrbMjJ9+DV+M7bd+vrBI8vi7DBCM59/VAjxBAuZ9P7Tsg8o8BrVqqB9c0ezlSCtFg8X0x2ET3ZBtZ49UARh/XP07I7eRk/DtSLYauxJDPzXVEZmSJCIybclox93u8F5/o8GySbD5GYMhffOJgXmul/Vz7eR0d5SxCMvJIRrP7WfiJYaUjLYqL2wjFQe/nUltcGCn2KtqGCyH7vl+Xzefea6Xjc8ebTgan2FJ0UH0mHv98lWADKuTI2fXcCAwEAAaOBqjCBpzAOBgNVHQ8BAf8EBAMCBsAwFgYDVR0lAQH/BAwwCgYIKwYBBQUHAwgwHQYDVR0OBBYEFLGwvffmoGkWbCDlUftc9DBic1cnMB8GA1UdIwQYMBaAFBLyWj7qVhy/zQas8fElyalL1BSZMD0GA1UdHwQ2MDQwMqAwoC6GLGh0dHA6Ly93d3cuc2suZWUvcmVwb3NpdG9yeS9jcmxzL2VlY2NyY2EuY3JsMA0GCSqGSIb3DQEBCwUAA4IBAQCopcU932wVPD6eed+sDBht4zt+kMPPFXv1pIX0RgbizaKvHWU4oHpRH8zcgo/gpotRLlLhZbHtu94pLFN6enpiyHNwevkmUyvrBWylONR1Yhwb4dLS8pBGGFR6eRdhGzoKAUF4B4dIoXOj4p26q1yYULF5ZkZHxhQFNi5uxak9tgCFlGtzXumjL5jBmtWeDTGE4YSa34pzDXjz8VAjPJ9sVuOmK2E0gyWxUTLXF9YevrWzRLzVFqw+qewBV2I4of/6miZOOT2wlA/meL7zr3hnfo7KSJQmMNUjZ6lh6RBIVvYI0t+A/fpTKiZfviz/Xn2e4PC6i57wmH5EgOOav0UK</X509Data>
         <BasicSignature>
            <EncryptionAlgoUsedToSignThisToken>RSA</EncryptionAlgoUsedToSignThisToken>
            <KeyLengthUsedToSignThisToken>?</KeyLengthUsedToSignThisToken>
            <DigestAlgoUsedToSignThisToken>SHA256</DigestAlgoUsedToSignThisToken>
            <ReferenceDataFound>true</ReferenceDataFound>
            <ReferenceDataIntact>true</ReferenceDataIntact>
            <SignatureIntact>true</SignatureIntact>
            <SignatureValid>true</SignatureValid>
         </BasicSignature>
         <Trusted>true</Trusted>
         <SelfSigned>false</SelfSigned>
      </Certificate>
      <Certificate Id="3e84ba4342908516e77573c0992f0979ca084e4685681ff195ccba8a229b8a76">
         <SubjectDistinguishedName Format="CANONICAL">1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=ee certification centre root ca,o=as sertifitseerimiskeskus,c=ee</SubjectDistinguishedName>
         <SubjectDistinguishedName Format="RFC2253">1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=EE Certification Centre Root CA,O=AS Sertifitseerimiskeskus,C=EE</SubjectDistinguishedName>
         <IssuerDistinguishedName Format="CANONICAL">1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=ee certification centre root ca,o=as sertifitseerimiskeskus,c=ee</IssuerDistinguishedName>
         <IssuerDistinguishedName Format="RFC2253">1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=EE Certification Centre Root CA,O=AS Sertifitseerimiskeskus,C=EE</IssuerDistinguishedName>
         <SerialNumber>112324828676200291871926431888494945866</SerialNumber>
         <DigestAlgAndValue>
            <DigestMethod>SHA1</DigestMethod>
            <DigestValue>yai551WAXljjU3enJeuvw3snzNc=</DigestValue>
         </DigestAlgAndValue>
         <NotAfter>2030-12-17T23:59:59Z</NotAfter>
         <NotBefore>2010-10-30T10:10:30Z</NotBefore>
         <PublicKeySize>2048</PublicKeySize>
         <PublicKeyEncryptionAlgo>RSA</PublicKeyEncryptionAlgo>
         <KeyUsageBits>
            <KeyUsage>keyCertSign</KeyUsage>
            <KeyUsage>cRLSign</KeyUsage>
         </KeyUsageBits>
         <IdKpOCSPSigning>true</IdKpOCSPSigning>
         <X509Data>MIIEAzCCAuugAwIBAgIQVID5oHPtPwBMyonY43HmSjANBgkqhkiG9w0BAQUFADB1MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEoMCYGA1UEAwwfRUUgQ2VydGlmaWNhdGlvbiBDZW50cmUgUm9vdCBDQTEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVlMCIYDzIwMTAxMDMwMTAxMDMwWhgPMjAzMDEyMTcyMzU5NTlaMHUxCzAJBgNVBAYTAkVFMSIwIAYDVQQKDBlBUyBTZXJ0aWZpdHNlZXJpbWlza2Vza3VzMSgwJgYDVQQDDB9FRSBDZXJ0aWZpY2F0aW9uIENlbnRyZSBSb290IENBMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDIIMDs4MVLqwd4lfNE7vsLDP90jmG7sWLqI9iroWUyeuuOF0+W2Ap7kaJjbMeMTC55v6kF/GlclY1i+blw7cNRfdCT5mzrMEvhvH2/UpvObntl8jixwKIy72KyaOBhU8E2lf/slLo2rpwcpzIP5Xy0xm90/XsY6KxX7QYgSzIwWFv9zajmofxwvI6Sc9uXp3whrj3B9UiHbCe9nyV0gVWw93X2PaRka9ZP585ArQ/dMtO8ihJTmMmJ+xAdTX7Nfh9WDSFwhfYggx/2uh8Ej+p3iDXE/+pOoYtNP2MbRMNE1CV2yreN1x5KZmTNXMWcg+HCCIia7E6j8T4cLNlsHaFLAgMBAAGjgYowgYcwDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8EBAMCAQYwHQYDVR0OBBYEFBLyWj7qVhy/zQas8fElyalL1BSZMEUGA1UdJQQ+MDwGCCsGAQUFBwMCBggrBgEFBQcDAQYIKwYBBQUHAwMGCCsGAQUFBwMEBggrBgEFBQcDCAYIKwYBBQUHAwkwDQYJKoZIhvcNAQEFBQADggEBAHv25MANqhlHt01Xo/6tu7Fq1Q+e2+RjxY6hUFaTlrg4wCQiZrxTFGGVv9DHKpY5P30osxBAIWrEr7BSdxjhlthWXePdNl4dp1BUoMUq5KqMlIpPnTX/dqQGE5Gion0ARD9V04I8GtVbvFZMIi5GQ4okQC3zErg7cBqklrkar4dBGmoYDQZPxz5uuSlNDUmJEYcyW+ZLBMjkXOZ0c5RdFpgTlf7727FE5TpwrDdr5rMzcijJs1eg9gIWiAYLtqZLICjU3j2LrTcFU3T+bsy8QxdxXvnFzBqpYe73dgzzcvRyrc9yAjYHR8/vGVCJYMzpJJUPwssd8m92kMfMdcGWxZ0=</X509Data>
         <BasicSignature>
            <EncryptionAlgoUsedToSignThisToken>RSA</EncryptionAlgoUsedToSignThisToken>
            <KeyLengthUsedToSignThisToken>2048</KeyLengthUsedToSignThisToken>
            <DigestAlgoUsedToSignThisToken>SHA1</DigestAlgoUsedToSignThisToken>
            <ReferenceDataFound>true</ReferenceDataFound>
            <ReferenceDataIntact>true</ReferenceDataIntact>
            <SignatureIntact>true</SignatureIntact>
            <SignatureValid>true</SignatureValid>
         </BasicSignature>
         <Trusted>false</Trusted>
         <SelfSigned>true</SelfSigned>
         <QCStatement>
            <QCP>false</QCP>
            <QCPPlus>false</QCPPlus>
            <QCC>false</QCC>
            <QCSSCD>false</QCSSCD>
         </QCStatement>
      </Certificate>
      <Certificate Id="0446cb4cc1dd99d0839a202b6be0f743d146cf03807f484ba4ec588e645d7da0">
         <SubjectDistinguishedName Format="CANONICAL">2.5.4.5=#130b3336373036303230323130,2.5.4.42=#13055645494b4f,2.5.4.4=#130753494e49564545,cn=sinivee\,veiko\,36706020210,ou=digital signature,o=esteid,c=ee</SubjectDistinguishedName>
         <SubjectDistinguishedName Format="RFC2253">2.5.4.5=#130b3336373036303230323130,2.5.4.42=#13055645494b4f,2.5.4.4=#130753494e49564545,CN=SINIVEE\,VEIKO\,36706020210,OU=digital signature,O=ESTEID,C=EE</SubjectDistinguishedName>
         <IssuerDistinguishedName Format="CANONICAL">1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=esteid-sk 2011,o=as sertifitseerimiskeskus,c=ee</IssuerDistinguishedName>
         <IssuerDistinguishedName Format="RFC2253">1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=ESTEID-SK 2011,O=AS Sertifitseerimiskeskus,C=EE</IssuerDistinguishedName>
         <IssuerCertificate>41ec808e33cca8659eaea81670d6c7dc01446636e1f227561b6307b80ba63862</IssuerCertificate>
         <SerialNumber>138113704091925806741842900129975503991</SerialNumber>
         <DigestAlgAndValue>
            <DigestMethod>SHA1</DigestMethod>
            <DigestValue>nYwXkMUJv7kPb+eDgNWu2r4ZJPc=</DigestValue>
         </DigestAlgAndValue>
         <NotAfter>2017-01-23T21:59:59Z</NotAfter>
         <NotBefore>2012-01-25T09:09:05Z</NotBefore>
         <PublicKeySize>2048</PublicKeySize>
         <PublicKeyEncryptionAlgo>RSA</PublicKeyEncryptionAlgo>
         <KeyUsageBits>
            <KeyUsage>nonRepudiation</KeyUsage>
         </KeyUsageBits>
         <X509Data>MIIEuTCCA6GgAwIBAgIQZ+e7WiJWyzFPH8axcYYMdzANBgkqhkiG9w0BAQUFADBkMQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEXMBUGA1UEAwwORVNURUlELVNLIDIwMTExGDAWBgkqhkiG9w0BCQEWCXBraUBzay5lZTAeFw0xMjAxMjUwOTA5MDVaFw0xNzAxMjMyMTU5NTlaMIGWMQswCQYDVQQGEwJFRTEPMA0GA1UECgwGRVNURUlEMRowGAYDVQQLDBFkaWdpdGFsIHNpZ25hdHVyZTEiMCAGA1UEAwwZU0lOSVZFRSxWRUlLTywzNjcwNjAyMDIxMDEQMA4GA1UEBAwHU0lOSVZFRTEOMAwGA1UEKgwFVkVJS08xFDASBgNVBAUTCzM2NzA2MDIwMjEwMIIBIzANBgkqhkiG9w0BAQEFAAOCARAAMIIBCwKCAQEAoP1UqXQG7xzE3USl9cGSzJmSYCyd8hKAUU/omzjblMDn+IQPcnLIj5bGgFXom1rdlfGWHQja9uglvHTN6jgZihdPikLZ9gkfYFdtxuB3uToBMpHxR6suywwuUQlRxd2dHZEsoCj0Ov6eIOLrYkepAsxysGfa3bsBd6/erAyLp3ym72kz/UWhivG9j+N37vHSpu5Jncfxe2iQmNCBLAhhw+QS7MnMLToscPk02Fd3V7DZ0REWJzkRoiIua2iArw779N3j0FfmfJcpH4dLoooukucxEScVKoSdm6cjUSDzUnLYR7sOKKWQUvWD7ZWQTLlk2wwVSp8Vzh8gTxgUe0xl3wIEBg/ZJaOCATEwggEtMAkGA1UdEwQCMAAwDgYDVR0PAQH/BAQDAgZAMFEGA1UdIARKMEgwRgYLKwYBBAHOHwEBAwIwNzASBggrBgEFBQcCAjAGGgRub25lMCEGCCsGAQUFBwIBFhVodHRwOi8vd3d3LnNrLmVlL2Nwcy8wIQYDVR0RBBowGIEWdmVpa28uc2luaXZlZUBlZXN0aS5lZTAdBgNVHQ4EFgQUEpLZ3fAMV49Q4+teYDMjR6IvcBAwGAYIKwYBBQUHAQMEDDAKMAgGBgQAjkYBATAfBgNVHSMEGDAWgBR7avJVUFy42XoIh0Gu+qIrPVtXdjBABgNVHR8EOTA3MDWgM6Axhi9odHRwOi8vd3d3LnNrLmVlL3JlcG9zaXRvcnkvY3Jscy9lc3RlaWQyMDExLmNybDANBgkqhkiG9w0BAQUFAAOCAQEABrCwlBJ51yuDtNBAlgh8+3s3Ps4KGbX9uH8J3Wvs3Ln7WGuDhNX18IV/QBICVBDsNrc0HNVRK9VMpQzvzmVgsSFnvC+osBkjt7nxjbiyJVM5xDsGGZAd69R8RlQ/5Ypb/HM5zc/zUTjHEio0e+NkLTn8dX/8RShrPhl+H71QnRyi8EStXLTJQG4beATBOhCa1wTSbWDGIzxBdk1a7fhz/4FH1jf7/V27Ag1eG2LDck7H0QTn/y3CBQn2vwBP10k52gSgjqTfQGultuIHD3ttUXw8DEWjKjhfQKgYR/0O7Wu0EUVhHPy4+t1hOj0DK20S/Auju05ZkFNgBtMePklzFg==</X509Data>
         <BasicSignature>
            <EncryptionAlgoUsedToSignThisToken>RSA</EncryptionAlgoUsedToSignThisToken>
            <KeyLengthUsedToSignThisToken>2048</KeyLengthUsedToSignThisToken>
            <DigestAlgoUsedToSignThisToken>SHA1</DigestAlgoUsedToSignThisToken>
            <ReferenceDataFound>true</ReferenceDataFound>
            <ReferenceDataIntact>true</ReferenceDataIntact>
            <SignatureIntact>true</SignatureIntact>
            <SignatureValid>true</SignatureValid>
         </BasicSignature>
         <SigningCertificate Id="41ec808e33cca8659eaea81670d6c7dc01446636e1f227561b6307b80ba63862"/>
         <CertificateChain>
            <ChainCertificate Id="41ec808e33cca8659eaea81670d6c7dc01446636e1f227561b6307b80ba63862">
               <Source>TRUSTED_LIST</Source>
            </ChainCertificate>
         </CertificateChain>
         <Trusted>false</Trusted>
         <SelfSigned>false</SelfSigned>
         <QCStatement>
            <QCP>false</QCP>
            <QCPPlus>false</QCPPlus>
            <QCC>true</QCC>
            <QCSSCD>false</QCSSCD>
         </QCStatement>
         <TrustedServiceProvider>
            <TSPName>AS Sertifitseerimiskeskus</TSPName>
            <TSPServiceName>ESTEID-SK 2011: Qualified certificates for Estonian ID-card, the residence permit card, the digital identity card, the digital identity card in form of the Mobile-ID</TSPServiceName>
            <TSPServiceType>http://uri.etsi.org/TrstSvc/Svctype/CA/QC</TSPServiceType>
            <Status>http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision</Status>
            <StartDate>2011-03-18T11:14:59Z</StartDate>
            <Qualifiers>
               <Qualifier>http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCWithSSCD</Qualifier>
            </Qualifiers>
            <WellSigned>true</WellSigned>
         </TrustedServiceProvider>
      </Certificate>
      <Certificate Id="41ec808e33cca8659eaea81670d6c7dc01446636e1f227561b6307b80ba63862">
         <SubjectDistinguishedName Format="CANONICAL">1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=esteid-sk 2011,o=as sertifitseerimiskeskus,c=ee</SubjectDistinguishedName>
         <SubjectDistinguishedName Format="RFC2253">1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=ESTEID-SK 2011,O=AS Sertifitseerimiskeskus,C=EE</SubjectDistinguishedName>
         <IssuerDistinguishedName Format="CANONICAL">1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=ee certification centre root ca,o=as sertifitseerimiskeskus,c=ee</IssuerDistinguishedName>
         <IssuerDistinguishedName Format="RFC2253">1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=EE Certification Centre Root CA,O=AS Sertifitseerimiskeskus,C=EE</IssuerDistinguishedName>
         <SerialNumber>54927111231050666919490365049675206925</SerialNumber>
         <DigestAlgAndValue>
            <DigestMethod>SHA1</DigestMethod>
            <DigestValue>RiZ0FvdTsxKAYiMPnB+wq30+7Bo=</DigestValue>
         </DigestAlgAndValue>
         <NotAfter>2024-03-18T10:14:59Z</NotAfter>
         <NotBefore>2011-03-18T10:14:59Z</NotBefore>
         <PublicKeySize>2048</PublicKeySize>
         <PublicKeyEncryptionAlgo>RSA</PublicKeyEncryptionAlgo>
         <KeyUsageBits>
            <KeyUsage>keyCertSign</KeyUsage>
            <KeyUsage>cRLSign</KeyUsage>
         </KeyUsageBits>
         <X509Data>MIIFBTCCA+2gAwIBAgIQKVKTqv2MxtRNgzCjwmRRDTANBgkqhkiG9w0BAQUFADB1MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEoMCYGA1UEAwwfRUUgQ2VydGlmaWNhdGlvbiBDZW50cmUgUm9vdCBDQTEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVlMB4XDTExMDMxODEwMTQ1OVoXDTI0MDMxODEwMTQ1OVowZDELMAkGA1UEBhMCRUUxIjAgBgNVBAoMGUFTIFNlcnRpZml0c2VlcmltaXNrZXNrdXMxFzAVBgNVBAMMDkVTVEVJRC1TSyAyMDExMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCz6XxsZh6r/aXcNe3kSpNMOqmQoAXUpzzcr4ZSaGZh/7JHIiplvNi6tbW/lK7sAiRsb65KzMWROEauld66ggbDPga6kU97C+AXGu7+DROXstjUOv6VlrHZVAnLmIOkycpWaxjM+EfQPZuDxEbkw96B3/fG69Zbp3s9y6WEhwU5Y9IiQl8YTkGnNUxidQbON1BGQm+HVEsgTf22J6r6G3FsE07rnMNskNC3DjuLSCUKF4kH0rVGVK9BdiCdFaZjHEykjwjIGzqnyxyRKe4YbJ6B9ABm95eSFgMBHtZEYU+q0VUIQGhAGAurOTXjWi1TssA42mnLGQZEI5GXMXtabp51AgMBAAGjggGgMIIBnDASBgNVHRMBAf8ECDAGAQH/AgEAMA4GA1UdDwEB/wQEAwIBBjCB9gYDVR0gBIHuMIHrMIHoBgsrBgEEAc4fZAEBATCB2DCBsgYIKwYBBQUHAgIwgaUegaIASwBhAHMAdQB0AGEAdABhAGsAcwBlACAAaQBzAGkAawB1AHQAdAD1AGUAbgBkAGEAdgBhAGwAZQAgAGQAbwBrAHUAbQBlAG4AZABpAGwAZQAgAGsAYQBuAHQAYQB2AGEAdABlACAAcwBlAHIAdABpAGYAaQBrAGEAYQB0AGkAZABlACAAdgDkAGwAagBhAHMAdABhAG0AaQBzAGUAawBzAC4wIQYIKwYBBQUHAgEWFWh0dHBzOi8vd3d3LnNrLmVlL0NQUzAdBgNVHQ4EFgQUe2ryVVBcuNl6CIdBrvqiKz1bV3YwHwYDVR0jBBgwFoAUEvJaPupWHL/NBqzx8SXJqUvUFJkwPQYDVR0fBDYwNDAyoDCgLoYsaHR0cDovL3d3dy5zay5lZS9yZXBvc2l0b3J5L2NybHMvZWVjY3JjYS5jcmwwDQYJKoZIhvcNAQEFBQADggEBAKC4IN3FC2gVDIH05TNMgFrQOCGSnXhzoJclRLoQ81BCOXTZI4qn7N74FHEnrAy6uNG7SS5qANqSaPIL8dp63jg/L4qn4iWaB5q5GGJOV07SnTHS7gUrqChGClnUeHxiZbL13PkP37Lnc+TKl1SKfgtn5FbH5cqrhvbA/VF3Yzlimu+L7EVohW9HKxZ//z8kDn6ieiPFfZdTOov/0eXVLlxqklybUuS6LYRRDiqQupgBKQBTwNbC8x0UHX00HokW+dCVcQvsUbv4xLhRq/MvyTthE+RdbkrV0JuzbfZvADfj75nA3+ZAzFYS5ZpMOjZ9p4rQVKpzQTklrF0m6mkdcEo=</X509Data>
         <BasicSignature>
            <EncryptionAlgoUsedToSignThisToken>RSA</EncryptionAlgoUsedToSignThisToken>
            <KeyLengthUsedToSignThisToken>?</KeyLengthUsedToSignThisToken>
            <DigestAlgoUsedToSignThisToken>SHA1</DigestAlgoUsedToSignThisToken>
            <ReferenceDataFound>true</ReferenceDataFound>
            <ReferenceDataIntact>true</ReferenceDataIntact>
            <SignatureIntact>true</SignatureIntact>
            <SignatureValid>true</SignatureValid>
         </BasicSignature>
         <Trusted>true</Trusted>
         <SelfSigned>false</SelfSigned>
      </Certificate>
   </UsedCertificates>
</DiagnosticData>
```
