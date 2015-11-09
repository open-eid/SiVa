Appendix - Sample Response Data Blocks - positive response
==========================================================

The Simple Report
-----------------

```xml
<xmlSimpleReport><?xml version="1.0" encoding="UTF-8"?>
<SimpleReport xmlns="http://dss.esig.europa.eu/validation/diagnostic">
	<Policy>
		<PolicyName>QES AdESQC TL based</PolicyName>
<PolicyDescription>Validate electronic signatures and indicates whether they are Advanced electronic Signatures (AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES). All certificates and their related chains supporting the signatures are validated against the EU Member State Trusted Lists (this includes signer's certificate and certificates used to validate certificate validity status Services - CRLs, OCSP, and time-stamps).
</PolicyDescription>
	</Policy>
	<ValidationTime>2015-09-01T15:13:00Z</ValidationTime>
	<DocumentName>/fixedpath/file.pdf</DocumentName>
	<Signature Id="id-027bac117bcd86670c6eb8292925083b"
		SignatureFormat="PAdES_BASELINE_LT">
		<SigningTime>2015-07-09T07:00:48Z</SigningTime>
		<SignedBy>SURNAME,GIVENNAME,37101010101</SignedBy>
		<Indication>VALID</Indication>
		<Info BestSignatureTime="2015-07-09T07:00:55Z" NameId="EMPTY" />
		<SignatureLevel>QES</SignatureLevel>
		<SignatureScopes>
			<SignatureScope name="PDF previous version #1"
scope="PdfByteRangeSignatureScope">The document byte range: [0, 14153, 52047, 491]
</SignatureScope>
		</SignatureScopes>
	</Signature>
	<ValidSignaturesCount>1</ValidSignaturesCount>
	<SignaturesCount>1</SignaturesCount>
</SimpleReport>
</xmlSimpleReport>
```

The Detailed Report
-------------------

```xml
<xmlDetailedReport><?xml version="1.0" encoding="UTF-8"?>
	<ValidationData xmlns="http://dss.esig.europa.eu/validation/diagnostic">
		<BasicBuildingBlocks>
			<Signature Id="id-027bac117bcd86670c6eb8292925083b">
				<ISC>
					<Constraint>
						<Name NameId="BBB_ICS_ISCI">Is there an identified candidate for the signing certificate?</Name>
						<Status>OK</Status>
						<Info CertificateId="8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842" />
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
						<Info Identifier="NO_POLICY" />
					</Constraint>
					<Constraint>
						<Name NameId="BBB_VCI_ISFC">Is the signature format correct?</Name>
						<Status>OK</Status>
						<Info Identifier="PAdES_BASELINE_LT" />
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
						<Status>OK</Status>
					</Constraint>
					<Constraint>
						<Name NameId="BBB_XCV_CCCBB">Can the certificate chain be built till the trust anchor?</Name>
						<Status>OK</Status>
					</Constraint>
					<Constraint>
						<Name NameId="BBB_XCV_ISCGKU">Has the signer's certificate given key-usage?</Name>
						<Status>OK</Status>
						<Info CertificateId="8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842" />
					</Constraint>
					<Constraint>
						<Name NameId="BBB_XCV_ICSI">Is the certificate's signature intact?</Name>
						<Status>OK</Status>
						<Info CertificateId="8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842" />
					</Constraint>
					<Constraint>
						<Name NameId="BBB_XCV_IRDPFC">Is the revocation data present for the certificate?</Name>
						<Status>OK</Status>
						<Info CertificateId="8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842" />
					</Constraint>
					<Constraint>
						<Name NameId="BBB_XCV_IRDTFC">Is the revocation data trusted for the certificate?</Name>
						<Status>OK</Status>
						<Info CertificateId="8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842" CertificateSource="TRUSTED_LIST" />
					</Constraint>
					<Constraint>
						<Name NameId="BBB_XCV_IRIF">Is the revocation information fresh for the certificate?</Name>
						<Status>OK</Status>
						<Info CertificateId="8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842" MaximumRevocationFreshness="0 DAYS" RevocationIssuingTime="2015-07-09T07:00:56Z" RevocationNextUpdate="" />
					</Constraint>
					<Constraint>
						<Name NameId="BBB_XCV_ISCR">Is the signer's certificate not revoked?</Name>
						<Status>OK</Status>
						<Info CertificateId="8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842" />
					</Constraint>
					<Constraint>
						<Name NameId="BBB_XCV_ISCOH">Is the signer's certificate on hold?</Name>
						<Status>OK</Status>
						<Info CertificateId="8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842" />
					</Constraint>
					<Constraint>
						<Name NameId="CTS_IIDOCWVPOTS">Is the issuance date of the certificate within the validity period of trusted Service?</Name>
						<Status>OK</Status>
						<Info CertificateId="8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842" />
					</Constraint>
					<Constraint>
						<Name NameId="CTS_WITSS">What is the trusted Service status?</Name>
						<Status>OK</Status>
						<Info CertificateId="8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842" TrustedServiceStatus= "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision" />
					</Constraint>
					<Constraint>
						<Name NameId="CTS_ITACBT">Is there a concordance between the trusted Service and the certificate?</Name>
						<Status>OK</Status>
						<Info CertificateId="8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842" />
					</Constraint>
					<Constraint>
						<Name NameId="ASCCM">Are signature cryptographic constraints met?</Name>
						<Status>OK</Status>
					</Constraint>
					<Constraint>
						<Name NameId="BBB_XCV_CMDCIQC">Certificate meta-data constraints: Is the signer's certificate qualified?</Name>
						<Status>OK</Status>
					</Constraint>
					<Constraint>
						<Name NameId="BBB_XCV_CMDCISSCD"> Certificate meta-data constraints: Is the SSCD?</Name>
						<Status>OK</Status>
					</Constraint>
					<Constraint>
						<Name NameId="BBB_XCV_CMDCIITLP"> Certificate meta-data constraints: Is issued to a legal person?</Name>
						<Status>INFORMATION</Status>
						<Info ConstraintValue="false" ExpectedValue="true" />
					</Constraint>
					<Constraint>
						<Name NameId="ASCCM">Are signature cryptographic constraints met?</Name>
						<Status>OK</Status>
					</Constraint>
					<Conclusion>
						<Indication>VALID</Indication>
					</Conclusion>
				</XCV>
				<Conclusion>
					<Indication>VALID</Indication>
				</Conclusion>
			</Signature>
		</BasicBuildingBlocks>
		<BasicValidationData>
			<Signature Id="id-027bac117bcd86670c6eb8292925083b">
				<Conclusion>
					<Indication>VALID</Indication>
				</Conclusion>
			</Signature>
		</BasicValidationData>
		<TimestampValidationData>
			<Signature Id="id-027bac117bcd86670c6eb8292925083b">
				<Timestamp Id="60163a7729ba71741248e54d7bd4ac2b4b59d313c050098b02a8b8f77827d761" Type="SIGNATURE_TIMESTAMP">
					<BasicBuildingBlocks>
						<ISC>
							<Constraint>
								<Name NameId="BBB_ICS_ISCI">Is there an identified candidate for the signing certificate?</Name>
								<Status>OK</Status>
								<Info CertificateId="1e49f497d89d430aad534b622d82bd9b9d0d4afdb7b7d36986c5df0981d9067d" />
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
								<Name NameId="BBB_XCV_ICTIVRSC"> Is the current time in the validity range of the signer's certificate?</Name>
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
				<Timestamp Id="02e5836474938844669a75b52e693d0a44630077a495b82cfcd97663b3852a13" Type="SIGNATURE_TIMESTAMP">
					<BasicBuildingBlocks>
						<ISC>
							<Constraint>
								<Name NameId="BBB_ICS_ISCI">Is there an identified candidate for the signing certificate?</Name>
								<Status>OK</Status>
								<Info CertificateId="1e49f497d89d430aad534b622d82bd9b9d0d4afdb7b7d36986c5df0981d9067d" />
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
			<Signature Id="id-027bac117bcd86670c6eb8292925083b">
				<Constraint>
					<Name NameId="ADEST_ROBVPIIC">Is the result of the Basic Validation Process conclusive?</Name>
					<Status>OK</Status>
				</Constraint>
				<Timestamp GenerationTime="2015-07-09T07:00:55Z" Id="60163a7729ba71741248e54d7bd4ac2b4b59d313c050098b02a8b8f77827d761" Type="SIGNATURE_TIMESTAMP">
					<Constraint>
						<Name NameId="ADEST_IMIDF">Is message imprint data found?</Name>
						<Status>OK</Status>
					</Constraint>
					<Constraint>
						<Name NameId="ADEST_IMIVC">Is message imprint verification conclusive?</Name>
						<Status>OK</Status>
					</Constraint>
					<Constraint>
						<Name NameId="ADEST_ITVPC">Is timestamp validation process conclusive?</Name>
						<Status>OK</Status>
						<Info NameId="ADEST_ITVPC_INFO_1">The best-signature-time was set to the generation time of the timestamp.</Info>
					</Constraint>
				</Timestamp>
				<Timestamp GenerationTime="2015-07-09T07:00:56Z" Id="02e5836474938844669a75b52e693d0a44630077a495b82cfcd97663b3852a13" Type="SIGNATURE_TIMESTAMP">
					<Constraint>
						<Name NameId="ADEST_IMIDF">Is message imprint data found?</Name>
						<Status>OK</Status>
					</Constraint>
					<Constraint>
						<Name NameId="ADEST_IMIVC">Is message imprint verification conclusive?</Name>
						<Status>OK</Status>
					</Constraint>
					<Constraint>
						<Name NameId="ADEST_ITVPC">Is timestamp validation process conclusive?</Name>
						<Status>NOT OK</Status>
						<Warning NameId="ADEST_ITVPC_ANS_1">The timestamp is rejected, its generation time is before the best-signature-time!</Warning>
					</Constraint>
				</Timestamp>
				<Constraint>
					<Name NameId="ADEST_ROTVPIIC">Is the result of the timestamps validation process conclusive?</Name>
					<Status>OK</Status>
					<Info BestSignatureTime="2015-07-09T07:00:55Z"/>
				</Constraint>
				<Constraint>
					<Name NameId="TSV_ASTPTCT">Is the order of the timestamps correct?</Name>
					<Status>OK</Status>
					<Info EarliestSignatureTimestampProductionDate="2015-07-09T07:00:55Z" LatestContentTimestampProductionDate="" />
				</Constraint>
				<Conclusion>
					<Indication>VALID</Indication>
					<Info BestSignatureTime="2015-07-09T07:00:55Z" NameId="EMPTY" />
				</Conclusion>
			</Signature>
		</AdESTValidationData>
		<LongTermValidationData>
			<Signature Id="id-027bac117bcd86670c6eb8292925083b">
				<Constraint>
					<Name NameId="PSV_IATVC">Is AdES-T validation conclusive?</Name>
					<Status>OK</Status>
					<Info BestSignatureTime="2015-07-09T07:00:55Z" NameId="EMPTY" />
				</Constraint>
				<Conclusion>
					<Indication>VALID</Indication>
					<Info BestSignatureTime="2015-07-09T07:00:55Z" NameId="EMPTY" />
				</Conclusion>
			</Signature>
		</LongTermValidationData>
	</ValidationData>
</xmlDetailedReport>
```

The Diagnostic Data
-------------------

```xml
 <xmlDiagnosticData><?xml version="1.0" encoding="UTF-8"?>
	<DiagnosticData xmlns="http://dss.esig.europa.eu/validation/diagnostic">
		<DocumentName>/fixedpath/file.pdf</DocumentName>
		<Signature Id="id-027bac117bcd86670c6eb8292925083b">
			<DateTime>2015-07-09T07:00:48Z</DateTime>
			<SignatureFormat>PAdES_BASELINE_LT</SignatureFormat>
			<BasicSignature>
				<EncryptionAlgoUsedToSignThisToken>RSA</EncryptionAlgoUsedToSignThisToken>
				<KeyLengthUsedToSignThisToken>2048</KeyLengthUsedToSignThisToken>
				<DigestAlgoUsedToSignThisToken>SHA256</DigestAlgoUsedToSignThisToken>
				<ReferenceDataFound>true</ReferenceDataFound>
				<ReferenceDataIntact>true</ReferenceDataIntact>
				<SignatureIntact>true</SignatureIntact>
				<SignatureValid>true</SignatureValid>
			</BasicSignature>
			<SigningCertificate
				Id="8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842">
				<AttributePresent>true</AttributePresent>
				<DigestValuePresent>true</DigestValuePresent>
				<DigestValueMatch>true</DigestValueMatch>
				<IssuerSerialMatch>true</IssuerSerialMatch>
			</SigningCertificate>
			<CertificateChain>
				<ChainCertificate
					Id="8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842">
					<Source>SIGNATURE</Source>
				</ChainCertificate>
				<ChainCertificate
					Id="41ec808e33cca8659eaea81670d6c7dc01446636e1f227561b6307b80ba63862">
					<Source>TRUSTED_LIST</Source>
				</ChainCertificate>
			</CertificateChain>
			<ContentType>application/pdf</ContentType>
			<Timestamps>
				<Timestamp
					Id="60163a7729ba71741248e54d7bd4ac2b4b59d313c050098b02a8b8f77827d761"
					Type="SIGNATURE_TIMESTAMP">
					<ProductionTime>2015-07-09T07:00:55Z</ProductionTime>
					<SignedDataDigestAlgo>SHA256</SignedDataDigestAlgo>
					<EncodedSignedDataDigestValue> Ry3xmETVLNy2RtP39ER/1ApgpmAJesj92BuzJE9SzEw=</EncodedSignedDataDigestValue>
					<MessageImprintDataFound>true</MessageImprintDataFound>
					<MessageImprintDataIntact>true</MessageImprintDataIntact>
					<BasicSignature>
						<EncryptionAlgoUsedToSignThisToken>
							RSA
</EncryptionAlgoUsedToSignThisToken>
						<KeyLengthUsedToSignThisToken>2048</KeyLengthUsedToSignThisToken>
						<DigestAlgoUsedToSignThisToken> 
SHA256
</DigestAlgoUsedToSignThisToken>
						<ReferenceDataFound>true</ReferenceDataFound>
						<ReferenceDataIntact>true</ReferenceDataIntact>
						<SignatureIntact>true</SignatureIntact>
						<SignatureValid>true</SignatureValid>
					</BasicSignature>
					<SigningCertificate Id="1e49f497d89d430aad534b622d82bd9b9d0d4afdb7b7d36986c5df0981d9067d" />
					<CertificateChain>
						<ChainCertificate Id="1e49f497d89d430aad534b622d82bd9b9d0d4afdb7b7d36986c5df0981d9067d">
							<Source>TRUSTED_LIST</Source>
						</ChainCertificate>
					</CertificateChain>
					<SignedObjects>
						<SignedSignature Id="id-027bac117bcd86670c6eb8292925083b" />
						<DigestAlgAndValue Category="CERTIFICATE">
<DigestMethod>
http://www.w3.org/2001/04/xmlenc#sha256
</DigestMethod>
							<DigestValue>
								iDVmcxX9z5aBIi1rSuqmnNGrVpP/OqGlmkxCiOSseEI=
</DigestValue>
						</DigestAlgAndValue>
					</SignedObjects>
				</Timestamp>
				<Timestamp Id="02e5836474938844669a75b52e693d0a44630077a495b82cfcd97663b3852a13" Type="SIGNATURE_TIMESTAMP">
					<ProductionTime>2015-07-09T07:00:56Z</ProductionTime>
					<SignedDataDigestAlgo>SHA256</SignedDataDigestAlgo>
					<EncodedSignedDataDigestValue>
BjpNdoaOuZlQVqKzjQjl3otJpmPluHCsLnu6ZpDfQBQ=
</EncodedSignedDataDigestValue>
					<MessageImprintDataFound>true</MessageImprintDataFound>
					<MessageImprintDataIntact>true</MessageImprintDataIntact>
					<BasicSignature>
						<EncryptionAlgoUsedToSignThisToken>
RSA
</EncryptionAlgoUsedToSignThisToken>
						<KeyLengthUsedToSignThisToken>2048</KeyLengthUsedToSignThisToken>
						<DigestAlgoUsedToSignThisToken>
SHA256
</DigestAlgoUsedToSignThisToken>
						<ReferenceDataFound>true</ReferenceDataFound>
						<ReferenceDataIntact>true</ReferenceDataIntact>
						<SignatureIntact>true</SignatureIntact>
						<SignatureValid>true</SignatureValid>
					</BasicSignature>
					<SigningCertificate Id="1e49f497d89d430aad534b622d82bd9b9d0d4afdb7b7d36986c5df0981d9067d" />
					<CertificateChain>
						<ChainCertificate Id="1e49f497d89d430aad534b622d82bd9b9d0d4afdb7b7d36986c5df0981d9067d">
							<Source>TRUSTED_LIST</Source>
						</ChainCertificate>
					</CertificateChain>
					<SignedObjects>
						<SignedSignature Id="id-027bac117bcd86670c6eb8292925083b" />
						<DigestAlgAndValue Category="CERTIFICATE">
							<DigestMethod>
http://www.w3.org/2001/04/xmlenc#sha256
</DigestMethod>
							<DigestValue>
iDVmcxX9z5aBIi1rSuqmnNGrVpP/OqGlmkxCiOSseEI=
</DigestValue>
						</DigestAlgAndValue>
					</SignedObjects>
				</Timestamp>
			</Timestamps>
			<SignatureScopes>
				<SignatureScope name="PDF previous version #1" 
scope="PdfByteRangeSignatureScope">The document byte range: [0, 14153, 52047, 491]</SignatureScope>
			</SignatureScopes>
		</Signature>
		<UsedCertificates>
			<Certificate
				Id="50d62f373742287b5f18319e513c7bca89bd78788b23da356124cb90f9a636af">
				<SubjectDistinguishedName Format="CANONICAL">
1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=sk ocsp responder 2011,ou=ocsp,o=as sertifitseerimiskeskus,l=tallinn,st=harju,c=ee
</SubjectDistinguishedName>
				<SubjectDistinguishedName Format="RFC2253"> 
1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=SK OCSP RESPONDER 2011,OU=OCSP,O=AS Sertifitseerimiskeskus,L=Tallinn,ST=Harju,C=EE
</SubjectDistinguishedName>
				<IssuerDistinguishedName Format="CANONICAL">
1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=ee certification centre root ca,o=as sertifitseerimiskeskus,c=ee
</IssuerDistinguishedName>
				<IssuerDistinguishedName Format="RFC2253">
1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=EE Certification Centre Root CA,O=AS Sertifitseerimiskeskus,C=EE
</IssuerDistinguishedName>
				<SerialNumber>152345024098107435434516224939977300289</SerialNumber>
				<DigestAlgAndValue>
					<DigestMethod>SHA256</DigestMethod>
					<DigestValue>
UNYvNzdCKHtfGDGeUTx7yom9eHiLI9o1YSTLkPmmNq8=
</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA512</DigestMethod>
					<DigestValue>
6MWkU0EE6X904rhKozk+dDZIdGyoKPInz0qIjAWppLn+/I2IOh2t2Os0bqvN6gcRfZgErMzJyOgtSkSZY3rYEg==
</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA224</DigestMethod>
					<DigestValue>P7RxYnVHl0qwkq/LmpsG/BC1Fc3Dw/K7HxbI9g==</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA1</DigestMethod>
					<DigestValue>dTlhPA/nn5BnjjBZsz2Ob/QwDpw=</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA384</DigestMethod>
					<DigestValue>
X3oLvDvbsHyVHvRdJYO/Nu1TaSLqEQfuqqlZQV2YFPCIoIFFW5nnsr9raAwLo5IO
</DigestValue>
				</DigestAlgAndValue>
				<NotAfter>2024-03-18T10:21:43Z</NotAfter>
				<NotBefore>2011-03-18T10:21:43Z</NotBefore>
				<PublicKeySize>2048</PublicKeySize>
				<PublicKeyEncryptionAlgo>RSA</PublicKeyEncryptionAlgo>
				<IdKpOCSPSigning>true</IdKpOCSPSigning>
				<X509Data>
MIIEvDCCA6SgAwIBAgIQcpyVmdruRVxNgzI3N/NZQTANBgkqhkiG9w0BAQUFADB1MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEoMCYGA1UEAwwfRUUgQ2VydGlmaWNhdGlvbiBDZW50cmUgUm9vdCBDQTEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVlMB4XDTExMDMxODEwMjE0M1oXDTI0MDMxODEwMjE0M1owgZ0xCzAJBgNVBAYTAkVFMQ4wDAYDVQQIEwVIYXJqdTEQMA4GA1UEBxMHVGFsbGlubjEiMCAGA1UEChMZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czENMAsGA1UECxMET0NTUDEfMB0GA1UEAxMWU0sgT0NTUCBSRVNQT05ERVIgMjAxMTEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVlMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAihvGyhMVrgReHluKln1za6gvCE/mlSREmWjJFpL9llvuEUZoPFIypYA8g5u1VfgkeW5gDq25jAOq4FyXeDGIa+pJn2h0o2Wc2aeppVG/emfGm/jA8jjeyMrwH8fAJrqVQ7c9X2xSwJEch/P2d8CfMZt5YF6gqLtPvG1b+n6otBZA5wjIFfJ/inJBMUvqHSz3+PLfxO2/T3Wyk/c8M9HIMqTelqyiMGRgWehiU1OsL9armv3dQrHs1wm6vHaxfpfWB9YAFpeo9aYqhPCxVt/zo2NQB6vxyZS0hsOrXL7SxRToOJaqsnvlbf0erPPFtRHUvbojYYgl+fzlz0Jt6QJoNwIDAQABo4IBHTCCARkwEwYDVR0lBAwwCgYIKwYBBQUHAwkwHQYDVR0OBBYEFKWhSGFt537NmJ50nCm7vYrecgxZMIGCBgNVHSAEezB5MHcGCisGAQQBzh8EAQIwaTA+BggrBgEFBQcCAjAyHjAAUwBLACAAdABpAG0AZQAgAHMAdABhAG0AcABpAG4AZwAgAHAAbwBsAGkAYwB5AC4wJwYIKwYBBQUHAgEWG2h0dHBzOi8vd3d3LnNrLmVlL2FqYXRlbXBlbDAfBgNVHSMEGDAWgBQS8lo+6lYcv80GrPHxJcmpS9QUmTA9BgNVHR8ENjA0MDKgMKAuhixodHRwOi8vd3d3LnNrLmVlL3JlcG9zaXRvcnkvY3Jscy9lZWNjcmNhLmNybDANBgkqhkiG9w0BAQUFAAOCAQEAw2sKwvTHtYGtD8Jw9mNUuj/mWiBSBEBeY2LhW8V6tjBPAPp3s6iWOh0FbVR2LUyrqRwgT3fyWiGsiDm/6cIqM+IblLp/8ztfRQjquhW6XCD9SK02OQ9ZSdBwcmoAApZLGXQC34wdgmV/hLTTNxONnDACBKz9U+Dy9a4ZT4tpNkbH8jq/BMne8FzbvRt1bjpXBP7gjLX+zdx8/hp0Wq4tD+f9NVX0+vm9ahEKuzx4QzPnSB7hhWM9OnLZT7noRQa+KWk5c+e5VoR5R2t7MjVl8Cd+2llxiSxqMSbU5/23BzAKgN+NQdrBZAzpZ7lfaAuLFaICP+bAm6uW2JUrM6abOw==
</X509Data>
				<BasicSignature>
					<EncryptionAlgoUsedToSignThisToken>
RSA
</EncryptionAlgoUsedToSignThisToken>
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
			<Certificate Id=
"8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842">
				<SubjectDistinguishedName Format="CANONICAL">
2.5.4.5=#130b3336373036303230323130,2.5.4.42=#13055645494b4f,2.5.4.4=#130753494e49564545,cn=SURNAME\,GIEVENNAME\,37101010101,ou=digital signature,o=esteid,c=ee
</SubjectDistinguishedName>
				<SubjectDistinguishedName Format="RFC2253">
2.5.4.5=#130b3336373036303230323130,2.5.4.42=#13055645494b4f,2.5.4.4=#130753494e49564545,CN=SURNAME\,GIVENNAME\,37101010101,OU=digital signature,O=ESTEID,C=EE
</SubjectDistinguishedName>
				<IssuerDistinguishedName Format="CANONICAL">
1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=esteid-sk 2011,o=as sertifitseerimiskeskus,c=ee
</IssuerDistinguishedName>
				<IssuerDistinguishedName Format="RFC2253">
1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=ESTEID-SK 2011,O=AS Sertifitseerimiskeskus,C=EE
</IssuerDistinguishedName>
				<IssuerCertificate>
41ec808e33cca8659eaea81670d6c7dc01446636e1f227561b6307b80ba63862
</IssuerCertificate>
				<SerialNumber>107809537776493927672953823382756361737</SerialNumber>
				<DigestAlgAndValue>
					<DigestMethod>SHA256</DigestMethod>
					<DigestValue>
iDVmcxX9z5aBIi1rSuqmnNGrVpP/OqGlmkxCiOSseEI=
</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA512</DigestMethod>
					<DigestValue>
cSN1zBhzmtGRVh8kNUSULcVz7cXcSC71Twawa8inrojOCv+dIVWeZMhdcofoOwiXocv5lV4jMkIj5AI3J6abiQ==
</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA224</DigestMethod>
					<DigestValue>NZjZa7hg1KahCv8SJz8sA8xaiaDczDgMkKNJ/Q==</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA1</DigestMethod>
					<DigestValue>/oiHHmkdQIeq6Y0LteKtitUKJa8=</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA384</DigestMethod>
					<DigestValue>
rb5mVn5uOKx14K6+hz+BBIYKSO6Wf6Fe8SNGfcEItPX6CR5p4pOedndVMQk41EsB
</DigestValue>
				</DigestAlgAndValue>
				<NotAfter>2019-12-22T21:59:59Z</NotAfter>
				<NotBefore>2014-12-23T07:44:17Z</NotBefore>
				<PublicKeySize>2048</PublicKeySize>
				<PublicKeyEncryptionAlgo>RSA</PublicKeyEncryptionAlgo>
				<KeyUsageBits>
					<KeyUsage>nonRepudiation</KeyUsage>
				</KeyUsageBits>
				<X509Data>
MIIEnTCCA4WgAwIBAgIQURtcmP07BjlUmR1RPIeGCTANBgkqhkiG9w0BAQUFADBkMQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEXMBUGA1UEAwwORVNURUlELVNLIDIwMTExGDAWBgkqhkiG9w0BCQEWCXBraUBzay5lZTAeFw0xNDEyMjMwNzQ0MTdaFw0xOTEyMjIyMTU5NTlaMIGWMQswCQYDVQQGEwJFRTEPMA0GA1UECgwGRVNURUlEMRowGAYDVQQLDBFkaWdpdGFsIHNpZ25hdHVyZTEiMCAGA1UEAwwZU0lOSVZFRSxWRUlLTywzNjcwNjAyMDIxMDEQMA4GA1UEBAwHU0lOSVZFRTEOMAwGA1UEKgwFVkVJS08xFDASBgNVBAUTCzM2NzA2MDIwMjEwMIIBITANBgkqhkiG9w0BAQEFAAOCAQ4AMIIBCQKCAQCXenu4kxXVFaVpxUfs7PGBC37WgP1oCpFOujGBiWFQiPgSWX8BtcxUIQaGpXMG31BtotSUVhalNDNszjU+ANRMOfeHKZploOV5R+Pm09B/XwRF1D+mK1lG3q+hz0aSt0DWXxFw4UAieTd5tVCDM/WhPFUD7ZinQayejNdRDo4Q7WS0wqp4YBNm3VCg1YPp/1Y86T28nxGKSewquVs089VOU92O0UUmNYy8AHu7Sod+DCNO5eVz6uSpJBRJRyvMbMxxIDfwtQI5YuttKN26IYXtjgOZeNKTV9eW0neO+T5P351odNSKulWzaAYKaI+E/9lfnY6fhygXgd7tmBqBIrOhAgMBAAGjggEXMIIBEzAJBgNVHRMEAjAAMA4GA1UdDwEB/wQEAwIGQDBQBgNVHSAESTBHMEUGCisGAQQBzh8BAQQwNzASBggrBgEFBQcCAjAGGgRub25lMCEGCCsGAQUFBwIBFhVodHRwOi8vd3d3LnNrLmVlL2Nwcy8wHQYDVR0OBBYEFCcVGdX3X/moNIYusvO958F/gbj0MCIGCCsGAQUFBwEDBBYwFDAIBgYEAI5GAQEwCAYGBACORgEEMB8GA1UdIwQYMBaAFHtq8lVQXLjZegiHQa76ois9W1d2MEAGA1UdHwQ5MDcwNaAzoDGGL2h0dHA6Ly93d3cuc2suZWUvcmVwb3NpdG9yeS9jcmxzL2VzdGVpZDIwMTEuY3JsMA0GCSqGSIb3DQEBBQUAA4IBAQCJSoo6h+4Dgu2+0C2ehtqYYEvMBIyLldWP88uWKgxw6HujsF5HRRk/zWAU8jGDN/LNzDNYDz0jg2212mn+neVBgo+U8W1Urkw9zgTsSwqnP7CoGw0nG65gnybrT4K+eX1ykyVmj1RAzfShVgwuOrMCDmguq6jFRj9V1oOmiMDpmzQ7Qo22le7qkkKoQ+PTLRfi5vpN+CQOg6kleeXaVwtdlP0ETfJIrdDBKKBKi8bn5b60300V1dMmsQAxdwXsKcuKPtG1YKO5Rf+OIUdAuOayYboeShGTlXlAswoxcfGZajxF8MCe9B4y0Rse8X1Q9C+F2rgloa5W6+JXeGrY8sUL
</X509Data>
				<BasicSignature>
					<EncryptionAlgoUsedToSignThisToken>
RSA
</EncryptionAlgoUsedToSignThisToken>
					<KeyLengthUsedToSignThisToken>2048</KeyLengthUsedToSignThisToken>
					<DigestAlgoUsedToSignThisToken>SHA1</DigestAlgoUsedToSignThisToken>
					<ReferenceDataFound>true</ReferenceDataFound>
					<ReferenceDataIntact>true</ReferenceDataIntact>
					<SignatureIntact>true</SignatureIntact>
					<SignatureValid>true</SignatureValid>
				</BasicSignature>
				<SigningCertificate Id=
"41ec808e33cca8659eaea81670d6c7dc01446636e1f227561b6307b80ba63862" />
				<CertificateChain>
					<ChainCertificate Id=
"41ec808e33cca8659eaea81670d6c7dc01446636e1f227561b6307b80ba63862">
						<Source>TRUSTED_LIST</Source>
					</ChainCertificate>
				</CertificateChain>
				<Trusted>false</Trusted>
				<SelfSigned>false</SelfSigned>
				<QCStatement>
					<QCP>false</QCP>
					<QCPPlus>false</QCPPlus>
					<QCC>true</QCC>
					<QCSSCD>true</QCSSCD>
				</QCStatement>
				<TrustedServiceProvider>
					<TSPName>AS Sertifitseerimiskeskus</TSPName>
					<TSPServiceName>ESTEID-SK 2011: Qualified certificates for Estonian 
ID-card, the residence permit card, the digital identity card, the digital identity card in form of the Mobile-ID
</TSPServiceName>
					<TSPServiceType>
http://uri.etsi.org/TrstSvc/Svctype/CA/QC
</TSPServiceType>
					<Status>
http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision
</Status>
					<StartDate>2011-03-18T11:14:59Z</StartDate>
					<Qualifiers>
						<Qualifier>
http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCWithSSCD
</Qualifier>
					</Qualifiers>
					<WellSigned>true</WellSigned>
				</TrustedServiceProvider>
				<Revocation>
					<Source>OCSPToken</Source>
					<Status>true</Status>
					<IssuingTime>2015-07-09T07:00:56Z</IssuingTime>
					<BasicSignature>
						<EncryptionAlgoUsedToSignThisToken>
RSA
</EncryptionAlgoUsedToSignThisToken>
						<KeyLengthUsedToSignThisToken>2048</KeyLengthUsedToSignThisToken>
						<DigestAlgoUsedToSignThisToken>SHA1</DigestAlgoUsedToSignThisToken>
						<ReferenceDataFound>true</ReferenceDataFound>
						<ReferenceDataIntact>true</ReferenceDataIntact>
						<SignatureIntact>true</SignatureIntact>
						<SignatureValid>true</SignatureValid>
					</BasicSignature>
					<SigningCertificate Id="
50d62f373742287b5f18319e513c7bca89bd78788b23da356124cb90f9a636af" />
					<CertificateChain>
						<ChainCertificate Id=
"50d62f373742287b5f18319e513c7bca89bd78788b23da356124cb90f9a636af">
							<Source>TRUSTED_LIST</Source>
						</ChainCertificate>
					</CertificateChain>
				</Revocation>
			</Certificate>
			<Certificate Id=
"41ec808e33cca8659eaea81670d6c7dc01446636e1f227561b6307b80ba63862">
				<SubjectDistinguishedName Format="CANONICAL">
1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=esteid-sk 2011,o=as sertifitseerimiskeskus,c=ee
</SubjectDistinguishedName>
				<SubjectDistinguishedName Format="RFC2253">
1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=ESTEID-SK 2011,O=AS Sertifitseerimiskeskus,C=EE
</SubjectDistinguishedName>
				<IssuerDistinguishedName Format="CANONICAL">
1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=ee certification centre root ca,o=as sertifitseerimiskeskus,c=ee
</IssuerDistinguishedName>
				<IssuerDistinguishedName Format="RFC2253">
1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=EE Certification Centre Root CA,O=AS Sertifitseerimiskeskus,C=EE
</IssuerDistinguishedName>
				<SerialNumber>54927111231050666919490365049675206925</SerialNumber>
				<DigestAlgAndValue>
					<DigestMethod>SHA256</DigestMethod>
					<DigestValue>
QeyAjjPMqGWerqgWcNbH3AFEZjbh8idWG2MHuAumOGI=
</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA512</DigestMethod>
					<DigestValue>
N48N8Fi02z3xOLKHKereOtCwbo4RNmvT28kXeD3L1NOQ24hQW3mw8L9JId60gLk6GXeG448AgzAf5IbiuY4caw==
</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA224</DigestMethod>
					<DigestValue>w21gYyCL7M7Yv9RuLXCjPrRnuBwAljRlZSr5mQ==</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA1</DigestMethod>
					<DigestValue>RiZ0FvdTsxKAYiMPnB+wq30+7Bo=</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA384</DigestMethod>
					<DigestValue>
SQITmL3GhgLkXRfsk4CucXBPZtMfrXFGI/ZwIjMVi9R1tRglrR9g3PU09Y57U3OK
</DigestValue>
				</DigestAlgAndValue>
				<NotAfter>2024-03-18T10:14:59Z</NotAfter>
				<NotBefore>2011-03-18T10:14:59Z</NotBefore>
				<PublicKeySize>2048</PublicKeySize>
				<PublicKeyEncryptionAlgo>RSA</PublicKeyEncryptionAlgo>
				<KeyUsageBits>
					<KeyUsage>keyCertSign</KeyUsage>
					<KeyUsage>cRLSign</KeyUsage>
				</KeyUsageBits>
				<X509Data>
MIIFBTCCA+2gAwIBAgIQKVKTqv2MxtRNgzCjwmRRDTANBgkqhkiG9w0BAQUFADB1MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEoMCYGA1UEAwwfRUUgQ2VydGlmaWNhdGlvbiBDZW50cmUgUm9vdCBDQTEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVlMB4XDTExMDMxODEwMTQ1OVoXDTI0MDMxODEwMTQ1OVowZDELMAkGA1UEBhMCRUUxIjAgBgNVBAoMGUFTIFNlcnRpZml0c2VlcmltaXNrZXNrdXMxFzAVBgNVBAMMDkVTVEVJRC1TSyAyMDExMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCz6XxsZh6r/aXcNe3kSpNMOqmQoAXUpzzcr4ZSaGZh/7JHIiplvNi6tbW/lK7sAiRsb65KzMWROEauld66ggbDPga6kU97C+AXGu7+DROXstjUOv6VlrHZVAnLmIOkycpWaxjM+EfQPZuDxEbkw96B3/fG69Zbp3s9y6WEhwU5Y9IiQl8YTkGnNUxidQbON1BGQm+HVEsgTf22J6r6G3FsE07rnMNskNC3DjuLSCUKF4kH0rVGVK9BdiCdFaZjHEykjwjIGzqnyxyRKe4YbJ6B9ABm95eSFgMBHtZEYU+q0VUIQGhAGAurOTXjWi1TssA42mnLGQZEI5GXMXtabp51AgMBAAGjggGgMIIBnDASBgNVHRMBAf8ECDAGAQH/AgEAMA4GA1UdDwEB/wQEAwIBBjCB9gYDVR0gBIHuMIHrMIHoBgsrBgEEAc4fZAEBATCB2DCBsgYIKwYBBQUHAgIwgaUegaIASwBhAHMAdQB0AGEAdABhAGsAcwBlACAAaQBzAGkAawB1AHQAdAD1AGUAbgBkAGEAdgBhAGwAZQAgAGQAbwBrAHUAbQBlAG4AZABpAGwAZQAgAGsAYQBuAHQAYQB2AGEAdABlACAAcwBlAHIAdABpAGYAaQBrAGEAYQB0AGkAZABlACAAdgDkAGwAagBhAHMAdABhAG0AaQBzAGUAawBzAC4wIQYIKwYBBQUHAgEWFWh0dHBzOi8vd3d3LnNrLmVlL0NQUzAdBgNVHQ4EFgQUe2ryVVBcuNl6CIdBrvqiKz1bV3YwHwYDVR0jBBgwFoAUEvJaPupWHL/NBqzx8SXJqUvUFJkwPQYDVR0fBDYwNDAyoDCgLoYsaHR0cDovL3d3dy5zay5lZS9yZXBvc2l0b3J5L2NybHMvZWVjY3JjYS5jcmwwDQYJKoZIhvcNAQEFBQADggEBAKC4IN3FC2gVDIH05TNMgFrQOCGSnXhzoJclRLoQ81BCOXTZI4qn7N74FHEnrAy6uNG7SS5qANqSaPIL8dp63jg/L4qn4iWaB5q5GGJOV07SnTHS7gUrqChGClnUeHxiZbL13PkP37Lnc+TKl1SKfgtn5FbH5cqrhvbA/VF3Yzlimu+L7EVohW9HKxZ//z8kDn6ieiPFfZdTOov/0eXVLlxqklybUuS6LYRRDiqQupgBKQBTwNbC8x0UHX00HokW+dCVcQvsUbv4xLhRq/MvyTthE+RdbkrV0JuzbfZvADfj75nA3+ZAzFYS5ZpMOjZ9p4rQVKpzQTklrF0m6mkdcEo=
</X509Data>
				<BasicSignature>
					<EncryptionAlgoUsedToSignThisToken>
RSA
</EncryptionAlgoUsedToSignThisToken>
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
			<Certificate Id=
"1e49f497d89d430aad534b622d82bd9b9d0d4afdb7b7d36986c5df0981d9067d">
				<SubjectDistinguishedName Format="CANONICAL">
cn=sk timestamping authority,ou=tsa,o=as sertifitseerimiskeskus,c=ee
</SubjectDistinguishedName>
				<SubjectDistinguishedName Format="RFC2253">
CN=SK TIMESTAMPING AUTHORITY,OU=TSA,O=AS Sertifitseerimiskeskus,C=EE
</SubjectDistinguishedName>
				<IssuerDistinguishedName Format="CANONICAL">
1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=ee certification centre root ca,o=as sertifitseerimiskeskus,c=ee
</IssuerDistinguishedName>
				<IssuerDistinguishedName Format="RFC2253">
1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=EE Certification Centre Root CA,O=AS Sertifitseerimiskeskus,C=EE
</IssuerDistinguishedName>
				<SerialNumber>48765665071482659663074918749208248665</SerialNumber>
				<DigestAlgAndValue>
					<DigestMethod>SHA256</DigestMethod>
					<DigestValue>
Hkn0l9idQwqtU0tiLYK9m50NSv23t9NphsXfCYHZBn0=
</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA512</DigestMethod>
					<DigestValue>
nJNPvfzm80s0kdBe8yFDw1fhIeCXN7cnfDG3hIODEDfQg8DbYh51M6Vd0DjXAvuZheS1q6VkDqzatItt5gpvmw==
</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA224</DigestMethod>
					<DigestValue>haMJJONbRc9tS2Ky2ymeV2mn0q6K2o/YF/2YsA==</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA1</DigestMethod>
					<DigestValue>stAhgvC5biocaH7OMjQII5gZMLY=</DigestValue>
				</DigestAlgAndValue>
				<DigestAlgAndValue>
					<DigestMethod>SHA384</DigestMethod>
					<DigestValue>
w1aer0uo8xes7xH8hKBd242IzS0snQK5LimK+n6pTtiIK1A9bJ6xi3DxwnFhQtcC
</DigestValue>
				</DigestAlgAndValue>
				<NotAfter>2019-09-16T08:40:38Z</NotAfter>
				<NotBefore>2014-09-16T08:40:38Z</NotBefore>
				<PublicKeySize>2048</PublicKeySize>
				<PublicKeyEncryptionAlgo>RSA</PublicKeyEncryptionAlgo>
				<KeyUsageBits>
					<KeyUsage>digitalSignature</KeyUsage>
					<KeyUsage>nonRepudiation</KeyUsage>
				</KeyUsageBits>
				<X509Data>
MIIEDTCCAvWgAwIBAgIQJK/s6xJo0AJUF/eG7W8BWTANBgkqhkiG9w0BAQsFADB1MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEoMCYGA1UEAwwfRUUgQ2VydGlmaWNhdGlvbiBDZW50cmUgUm9vdCBDQTEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVlMB4XDTE0MDkxNjA4NDAzOFoXDTE5MDkxNjA4NDAzOFowYzELMAkGA1UEBhMCRUUxIjAgBgNVBAoMGUFTIFNlcnRpZml0c2VlcmltaXNrZXNrdXMxDDAKBgNVBAsMA1RTQTEiMCAGA1UEAwwZU0sgVElNRVNUQU1QSU5HIEFVVEhPUklUWTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAJPa/dQKemSKCNSwlMUp9YKQY6zQOfs9vgUnbzTRHCRBRdsabZYknxTI4DqQ5+JPqw8MTkDvb6nfDZGd15t4oY4tHXXoCfRrbMjJ9+DV+M7bd+vrBI8vi7DBCM59/VAjxBAuZ9P7Tsg8o8BrVqqB9c0ezlSCtFg8X0x2ET3ZBtZ49UARh/XP07I7eRk/DtSLYauxJDPzXVEZmSJCIybclox93u8F5/o8GySbD5GYMhffOJgXmul/Vz7eR0d5SxCMvJIRrP7WfiJYaUjLYqL2wjFQe/nUltcGCn2KtqGCyH7vl+Xzefea6Xjc8ebTgan2FJ0UH0mHv98lWADKuTI2fXcCAwEAAaOBqjCBpzAOBgNVHQ8BAf8EBAMCBsAwFgYDVR0lAQH/BAwwCgYIKwYBBQUHAwgwHQYDVR0OBBYEFLGwvffmoGkWbCDlUftc9DBic1cnMB8GA1UdIwQYMBaAFBLyWj7qVhy/zQas8fElyalL1BSZMD0GA1UdHwQ2MDQwMqAwoC6GLGh0dHA6Ly93d3cuc2suZWUvcmVwb3NpdG9yeS9jcmxzL2VlY2NyY2EuY3JsMA0GCSqGSIb3DQEBCwUAA4IBAQCopcU932wVPD6eed+sDBht4zt+kMPPFXv1pIX0RgbizaKvHWU4oHpRH8zcgo/gpotRLlLhZbHtu94pLFN6enpiyHNwevkmUyvrBWylONR1Yhwb4dLS8pBGGFR6eRdhGzoKAUF4B4dIoXOj4p26q1yYULF5ZkZHxhQFNi5uxak9tgCFlGtzXumjL5jBmtWeDTGE4YSa34pzDXjz8VAjPJ9sVuOmK2E0gyWxUTLXF9YevrWzRLzVFqw+qewBV2I4of/6miZOOT2wlA/meL7zr3hnfo7KSJQmMNUjZ6lh6RBIVvYI0t+A/fpTKiZfviz/Xn2e4PC6i57wmH5EgOOav0UK
</X509Data>
				<BasicSignature>
					<EncryptionAlgoUsedToSignThisToken>
RSA
</EncryptionAlgoUsedToSignThisToken>
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
		</UsedCertificates>
	</DiagnosticData>
</xmlDiagnosticData>
```