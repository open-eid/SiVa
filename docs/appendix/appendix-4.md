Appendix 4 - Validation Constraint Configuration
================================================

* `ConstraintsParameters`
    * `Description` - String containing the description of Validation Policy
    * `MainSignature`
        * `AcceptablePolicies Level="FAIL"` - The list of ID’s of acceptable signature policies.
          Level – Indicates return message if check fails: FAIL, WARNING
            * `Id` - [*] character can be used to indicate that any policy is acceptable.
              It is also possible to define an exhaustive list of acceptable policies, i.e. ANY_POLICY, NO_POLICY etc.
              It’s also possible to define an URN: urn:oid:1.2.250.190.50.1.2.1
        	* `AcceptableSignatureFormats Level="FAIL` - List of ID’s of acceptable Signature formats.
               * `Id` - ID of acceptale Signature Format, i.e. PAdES_BASELINE_LT, PAdES_BASELINE_LTA
            * `OcspDelayToBestSignatureTime Unit="MINUTES"` - Determines the period of time (in minutes) between Signing Time and OCSP Time Stamp. Different levels of return value, based on the period difference between Signing Time and OCSP Time Stamp
               * `Warn` - Determines conditions when a warning is returned for OCSP Delay check
                 * `MinimalDelay` - Determines a period (in minutes) during which an OCSP Time Stamp
               * `ail` - Determines conditions when OCSP Delay check fails
                 * `MinimalDelay` - Determines a period (in minutes) during which an OCSP Time Stamp
            * `ReferenceDataExistence Level="FAIL"` - True/false
            * `ReferenceDataIntact Level="FAIL"	` - True/false
            * `SignatureIntact Level="FAIL"` - True/false
            * `SigningCertificate` - Container of rules for checking the Signing Certificate
                * `Recognition Level="FAIL"` - Level – FAIL/WARNING/INFORM
                * `AttributePresent Level="FAIL"` - Level – FAIL/WARNING/INFORM
                * `DigestValuePresent Level="FAIL"` - Level – FAIL/WARNING/INFORM
                * `DigestValueMatch Level="FAIL"` - True/false Level – FAIL/WARNING/INFORM
                * `IssuerSerialMatch Level="WARN"` - True/false
                * `Signed Level="FAIL"`
                * `Signature Level="FAIL"`
                * `Expiration Level="IGNORE"`
                * `RevocationDataAvailable Level="FAIL"`
                * `RevocationDataIsTrusted Level="FAIL"`
                * `RevocationDataFreshness Level="WARN"`
                * `ProspectiveCertificateChain Level="FAIL"`
                * `KeyUsage Level="FAIL"` - List of Key Usage extensions that have to be present in Certificate
                    * `Identifier` - Identifier of Key Usage which has to exist in the certificate. For example „nonrepudiation“
                * `Revoked Level="FAIL"` - Checks if the Certificate has been Revoked
                * `OnHold Level="FAIL"`- Checks if the Certificate has been Suspended (put On Hold)
                * `TSLValidity Level="WARN"`
                * `TSLStatus Level="WARN"`
                * `TSLStatusAndValidity Level="FAIL"`
                * `Qualification Level="WARN"`
                * `SupportedBySSCD Level="WARN"`
                * `IssuedToLegalPerson Level="INFORM"`
                * `Cryptographic Level="FAIL"`- Set of cryptographic constraints applied to certain type of Certificate (Signing, CA etc)
                    * `AcceptableEncryptionAlgo` - List of acceptable cryptographic algorithms for Main Signature
                        * `Algo` - Algotithm identifier, i.e. „RSA“, „ECDSA“ etc.
                    * `MiniPublicKeySize` - List of minimal lengths of public keys, for acceptable encryption algorithms
                        * `Algo Size="1024"` - Defines algorithm and its minimal key length
                    * `cceptableDigestAlgo` - List of acceptable message digest (hash) algorithms
                        * `Algo` - Defines acceptable message digest, i.e. SHA1, SHA224 etc.
            * `CACertificate` - Container for constraints of specific cryptographic element. Same elements as described for SigningCertificate container.
            * `TimestampCertificate` - Container for constraints of specific cryptographic element. Same elements as described for SigningCertificate container.
            * `OCSPCertificate` - Container for constraints of specific cryptographic element. Same elements as described for SigningCertificate container.
            * `CRLCertificate` - Container for constraints of specific cryptographic element. Same elements as described for SigningCertificate container.
            * `Cryptographic Level="FAIL“` - Set of cryptographic constraints which can be applied on different cryptographic material. Same elements as in SigningCertificate -> Cryptographic
            * `MandatedSignedQProperties` - Indicates the signed qualifying properties that are mandated to be present in the signature.
                * `SigningTime Level="FAIL"` - Checks if SigningTime is present in Signature
                * `ContentTimeStamp`
                    * `essageImprintDataFound Level="FAIL"`
                    * `MessageImprintDataIntact Level="FAIL"`
                    * `MandatedUnsignedQProperties`
                        * `CounterSignature`
                            * `ReferenceDataExistence Level="FAIL"`
                            * `ReferenceDataIntact Level="FAIL"`
                            * `SignatureIntact Level="FAIL"`
            * `Timestamp` - Container for Timestamp rules
                * `TimestampDelay Unit="DAYS"` - Specifies a maximum acceptable time between the signing time and the time at which the signature time- stamp is created for the verifier. If the signature time-stamp is later that the time in the signing-time attribute by more than the value given in signatureTimestampDelay, the signature must be considered invalid. Specified in days.
                * `MessageImprintDataFound Level="FAIL"`
                * `MessageImprintDataIntact Level="FAIL"`
                * `RevocationTimeAgainstBestSignatureTime Level="FAIL"`
                * `BestSignatureTimeBeforeIssuanceDateOfSigningCertificate Level="FAIL"`
                * `igningCertificateValidityAtBestSignatureTime Level="FAIL"`
                * `AlgorithmReliableAtBestSignatureTime Level="FAIL"`
                * `Coherence Level="WARN"`
                * `SigningCertificate` - Container of rules for checking the Signing Certificate. Same elements as described in MainSignature.SigningCertificate
                * `CACertificate` - Container for constraints of specific cryptographic element. Same elements as described in MainSignature.SigningCertificate.
            * `Revocation` - Container for Revocation Data rules.
                * `RevocationFreshness Unit="DAYS"` - The freshness of the revocation status information is the maximum  accepted difference between the issuance date of the revocation status information and the current time.In general, revocation status information is said "fresh" if its issuance date is after the current time minus the considered freshness. 
                  If present indicates the revocation freshness for the certificates validation. If its value is 0 then the revocation freshness is not checked. This parameter applies to all certificates.
                * `SigningCertificate` - Container of rules for checking the Signing Certificate. Same elements as described in MainSignature.SigningCertificate
                * `CACertificate` - Container for constraints of specific cryptographic element. Same elements as described in MainSignature.SigningCertificate.
            * `Cryptographic` - Container for Cryptographic rules
                * `AlgoExpirationDate Format="yyyy-MM-dd"` - List of expiration dates for each Cryptographic Algorithm mentioned in the list.
                * `Algo Date="2017-02-24"` - Specifies the date until when the given algorithm/key size or algorithm/hash length combination (mentioned in element value) is accepted as being strong enough.


