/*
 * Copyright 2018 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.integrationtest;

public class TestData {

    public static final String SIGNATURE_POLICY_1 = "POLv3";
    public static final String SIGNATURE_POLICY_2 = "POLv4";

    public static final String REPORT_TYPE_SIMPLE = "Simple";
    public static final String REPORT_TYPE_DETAILED = "Detailed";
    public static final String REPORT_TYPE_DIAGNOSTIC = "Diagnostic";

    public static final String HASH_ALGO_SHA224 = "SHA224";
    public static final String HASH_ALGO_SHA256 = "SHA256";
    public static final String HASH_ALGO_SHA384 = "SHA384";
    public static final String HASH_ALGO_SHA512 = "SHA512";

    public static final String SIGNATURE_FORMAT_PADES_LT = "PAdES_BASELINE_LT";
    public static final String SIGNATURE_FORMAT_PADES_LTA = "PAdES_BASELINE_LTA";
    public static final String SIGNATURE_FORMAT_PADES_B = "PAdES_BASELINE_B";
    public static final String SIGNATURE_FORMAT_XADES_B = "XAdES_BASELINE_B";
    public static final String SIGNATURE_FORMAT_XADES_T = "XAdES_BASELINE_T";
    public static final String SIGNATURE_FORMAT_XADES_LT_TM = "XAdES_BASELINE_LT_TM";
    public static final String SIGNATURE_FORMAT_XADES_LT = "XAdES_BASELINE_LT";
    public static final String SIGNATURE_FORMAT_XADES_LTA = "XAdES_BASELINE_LTA";
    public static final String SIGNATURE_FORMAT_SK_XML = "SK_XML_1.0";
    public static final String SIGNATURE_FORMAT_DIGIDOC_XML_11 = "DIGIDOC_XML_1.1";
    public static final String SIGNATURE_FORMAT_DIGIDOC_XML_12 = "DIGIDOC_XML_1.2";
    public static final String SIGNATURE_FORMAT_DIGIDOC_XML_13 = "DIGIDOC_XML_1.3";

    public static final String SIGNATURE_FORM_ASICE = "ASiC-E";
    public static final String SIGNATURE_FORM_ASICS = "ASiC-S";
    public static final String SIGNATURE_FORM_DDOC_10 = "DIGIDOC_XML_1.0";
    public static final String SIGNATURE_FORM_DDOC_11 = "DIGIDOC_XML_1.1";
    public static final String SIGNATURE_FORM_DDOC_12 = "DIGIDOC_XML_1.2";
    public static final String SIGNATURE_FORM_DDOC_13 = "DIGIDOC_XML_1.3";
    public static final String SIGNATURE_FORM_DDOC_10_HASHCODE = "DIGIDOC_XML_1.0_hashcode";
    public static final String SIGNATURE_FORM_DDOC_11_HASHCODE = "DIGIDOC_XML_1.1_hashcode";
    public static final String SIGNATURE_FORM_DDOC_12_HASHCODE = "DIGIDOC_XML_1.2_hashcode";
    public static final String SIGNATURE_FORM_DDOC_13_HASHCODE = "DIGIDOC_XML_1.3_hashcode";

    public static final String SIGNATURE_LEVEL_QESIG = "QESIG";
    public static final String SIGNATURE_LEVEL_NOT_ADES_QC_QSCD = "NOT_ADES_QC_QSCD";
    public static final String SIGNATURE_LEVEL_INDETERMINATE_QESIG = "INDETERMINATE_QESIG";
    public static final String SIGNATURE_LEVEL_NOT_ADES = "NOT_ADES";
    public static final String SIGNATURE_LEVEL_NA = "NA";

    public static final String VALID_INDICATION_VALUE_PASSED = "PASSED";
    public static final String VALID_INDICATION_VALUE_FAILED = "FAILED";
    public static final String INDETERMINATE= "INDETERMINATE";
    public static final String TOTAL_FAILED = "TOTAL-FAILED";
    public static final String TOTAL_PASSED = "TOTAL-PASSED";

    // Subindications based on TS 119 102-1 v1.2.1
    public static final String SUB_INDICATION_FORMAT_FAILURE = "FORMAT_FAILURE";
    public static final String SUB_INDICATION_HASH_FAILURE = "HASH_FAILURE";
    public static final String SUB_INDICATION_SIG_CRYPTO_FAILURE = "SIG_CRYPTO_FAILURE";
    public static final String SUB_INDICATION_REVOKED = "REVOKED";
    public static final String SUB_INDICATION_EXPIRED = "EXPIRED";
    public static final String SUB_INDICATION_NOT_YET_VALID = "NOT_YET_VALID";
    public static final String SUB_INDICATION_SIG_CONSTRAINTS_FAILURE = "SIG_CONSTRAINTS_FAILURE";
    public static final String SUB_INDICATION_CHAIN_CONSTRAINTS_FAILURE = "CHAIN_CONSTRAINTS_FAILURE";
    public static final String SUB_INDICATION_CERTIFICATE_CHAIN_GENERAL_FAILURE = "CERTIFICATE_CHAIN_GENERAL_FAILURE";
    public static final String SUB_INDICATION_CRYPTO_CONSTRAINTS_FAILURE = "CRYPTO_CONSTRAINTS_FAILURE";
    public static final String SUB_INDICATION_POLICY_PROCESSING_ERROR = "POLICY_PROCESSING_ERROR";
    public static final String SUB_INDICATION_SIGNATURE_POLICY_NOT_AVAILABLE = "SIGNATURE_POLICY_NOT_AVAILABLE";
    public static final String SUB_INDICATION_TIMESTAMP_ORDER_FAILURE = "TIMESTAMP_ORDER_FAILURE";
    public static final String SUB_INDICATION_NO_SIGNING_CERTIFICATE_FOUND = "NO_SIGNING_CERTIFICATE_FOUND";
    public static final String SUB_INDICATION_NO_CERTIFICATE_CHAIN_FOUND = "NO_CERTIFICATE_CHAIN_FOUND";
    public static final String SUB_INDICATION_REVOKED_NO_POE = "REVOKED_NO_POE";
    public static final String SUB_INDICATION_REVOKED_CA_NO_POE = "REVOKED_CA_NO_POE ";
    public static final String SUB_INDICATION_OUT_OF_BOUNDS_NOT_REVOKED = "OUT_OF_BOUNDS_NOT_REVOKED";
    public static final String SUB_INDICATION_OUT_OF_BOUNDS_NO_POE = "OUT_OF_BOUNDS_NO_POE";
    public static final String SUB_INDICATION_CRYPTO_CONSTRAINTS_FAILURE_NO_POE = "CRYPTO_CONSTRAINTS_FAILURE_NO_POE";
    public static final String SUB_INDICATION_NO_POE = "NO_POE";
    public static final String SUB_INDICATION_TRY_LATER = "TRY_LATER";
    public static final String SUB_INDICATION_SIGNED_DATA_NOT_FOUND = "SIGNED_DATA_NOT_FOUND";

    public static final String VALID_SIGNATURE_SCOPE_CONTENT_FULL = "Full document";
    public static final String VALID_SIGNATURE_SCOPE_CONTENT_DIGEST = "Digest of the document content";
    public static final String VALID_SIGNATURE_SCOPE_CONTENT_2 = "The document byte range: [0, 14153, 52047, 491]";
    public static final String SIGNATURE_SCOPE_FULL = "FULL";
    public static final String SIGNATURE_SCOPE_DIGEST = "DIGEST";
    public static final String VALID_SIGNATURE_SCOPE_VALUE_2 = "PdfByteRangeSignatureScope";

    public static final String VALID_VALIDATION_PROCESS_NAMEID_1 = "QUAL_TL_FRESH";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_2 = "QUAL_TL_EXP";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_3 = "QUAL_TL_VERSION";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_4 = "QUAL_TL_WS";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_5 = "ADEST_ROBVPIIC";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_6 = "LTV_ABSV";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_7 = "ARCH_LTVV";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_8 = "BBB_FC_IEFF";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_9 = "BBB_ICS_ISCI";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_10 = "BBB_ICS_ISCS";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_11 = "BBB_ICS_ISASCP";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_12 = "BBB_ICS_ISACDP";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_13 = "BBB_ICS_ICDVV";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_14 = "BBB_ICS_AIDNASNE";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_15 = "BBB_VCI_ISPK";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_16 = "BBB_CV_IRDOF";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_17 = "BBB_CV_IRDOI";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_18 = "BBB_CV_ISI";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_19 = "BBB_SAV_ISQPSTP";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_20 = "ASCCM";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_21 = "BBB_XCV_CCCBB";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_22 = "XCV_TSL_ETIP";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_23 = "XCV_TSL_ESP";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_24 = "BBB_XCV_SUB";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_25 = "BBB_XCV_SUB_ANS";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_26= "BBB_XCV_ICSI";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_27= "BBB_SAV_TSP_IMIVC";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_28= "BBB_XCV_ISCGKU";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_29= "BBB_XCV_ISCR";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_30= "BBB_XCV_ISCOH";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_31= "BBB_XCV_ICTIVRSC";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_32= "BBB_XCV_ICTIVRSC_ANS";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_33 = "BBB_XCV_IRDPFC";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_34 = "QUAL_IS_ADES";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_35 = "QUAL_IS_ADES_INV";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_36 = "QUAL_TRUSTED_CERT_PATH";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_37 = "QUAL_TL_SERV_CONS";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_38 = "QUAL_TL_CERT_CONS";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_39 = "QUAL_QC_AT_ST";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_40 = "QUAL_FOR_SIGN_AT_ST";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_41 = "QUAL_QC_AT_CC";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_42 = "QUAL_QSCD_AT_ST";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_43 = "BBB_XCV_CMDCIQC";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_44 = "BBB_SAV_TSP_IMIDF";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_45 = "BBB_RFC_NUP";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_46 = "BBB_RFC_NUP_ANS";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_47 = "BBB_RFC_IRIF";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_48 = "QUAL_TRUSTED_LIST_ACCEPT";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_49 = "ADEST_ROTVPIIC";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_50 = "ADEST_RORPIIC";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_51 = "TSV_ASTPTCT";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_52 = "ADEST_ISTPTDABST";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_53 = "QUAL_UNIQUE_CERT";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_54 = "ATCCM";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_55 = "ARCCM";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_56 = "BBB_XCV_IRDC";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_57 = "BBB_XCV_IARDPFC";
    public static final String VALID_VALIDATION_PROCESS_NAMEID_59 = "BBB_SAV_ISQPMDOSPP";




    public static final String VALID_VALIDATION_PROCESS_VALUE_1 = "Is the trusted list fresh ?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_2 = "Is the trusted list not expired ?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_3 = "Is the trusted list has the expected version ?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_4 = "Is the trusted list well signed ?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_5 = "Is the result of the Basic Validation Process conclusive?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_6 = "Is the result of the Basic Validation Process acceptable?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_7 = "Is the result of the LTV validation process acceptable?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_8 = "Is the expected format found?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_9 = "Is there an identified candidate for the signing certificate?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_10 = "Is the signing certificate signed?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_11 = "Is the signed attribute: 'signing-certificate' present?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_12 = "Is the signed attribute: 'cert-digest' of the certificate present?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_13 = "Is the certificate's digest value valid?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_14 = "Are the issuer distinguished name and the serial number equal?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_15 = "Is the signature policy known?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_16 = "Has the reference data object been found?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_17 = "Is the reference data object intact?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_18 = "Is the signature intact?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_19 = "Is the signed qualifying property: 'signing-time' present?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_20 = "Are signature cryptographic constraints met?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_21 = "Can the certificate chain be built till a trust anchor?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_22 = "Has a trust service with an expected type identifier been found?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_23 = "Is the expected trust service status present ?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_24 = "Is the certificate validation conclusive?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_25 = "The certificate validation is not concluant!";
    public static final String VALID_VALIDATION_PROCESS_VALUE_26 = "Is the certificate's signature intact?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_27 = "Is message imprint verification conclusive?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_28 = "Has the signer's certificate given key-usage?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_29 = "Is the certificate not revoked?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_30 = "Is the certificate on hold?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_31 = "Is the current time in the validity range of the signer's certificate?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_32 = "Certificate meta-data constraints: Is the signer's certificate qualified?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_33 = "Is the revocation data present for the certificate?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_34 = "Is the signature/seal an acceptable AdES (ETSI EN 319 102-1) ?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_35 = "The signature/seal is not a valid AdES!";
    public static final String VALID_VALIDATION_PROCESS_VALUE_36 = "Is the certificate path trusted?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_37 = "Are trust services consistent ?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_38 = "Is the certificate consistent with the trusted list ?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_39 = "Is the certificate qualifed at signing time?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_40 = "Is the certificate for eSig at signing time?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_41 = "Is the certificate qualifed at issuance time?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_42 = "Is the signature/seal created by a QSCD?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_43 = "Is message imprint data found?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_44 = "Is there a Next Update defined for the revocation data?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_45 = "There is no Next Update defined for the revocation data!";
    public static final String VALID_VALIDATION_PROCESS_VALUE_46 = "Is the revocation information fresh for the certificate?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_47 = "Is the trusted list acceptable?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_48 = "Is the result of the timestamps validation process conclusive?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_49 = "Is the result of the revocation data validation process acceptable?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_50 = "Are the timestamps in the right order?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_51 = "Is the signing-time plus the timestamp delay after best-signature-time?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_52 = "Is the certificate unique ?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_53 = "Are timestamp cryptographic constraints met?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_54 = "Are revocation cryptographic constraints met?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_55 = "Is timestamp's signature intact?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_56 = "Is revocation's signature intact?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_57 = "Is the revocation data consistent?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_58 = "Is an acceptable revocation data present for the certificate?";
    public static final String VALID_VALIDATION_PROCESS_VALUE_59 = "Is the signed qualifying property: 'message-digest' or 'SignedProperties' present?";


    public static final String VALID_VALIDATION_PROCESS_ERROR_VALUE_1 = "The result of the Basic validation process is not conclusive!";
    public static final String VALID_VALIDATION_PROCESS_ERROR_VALUE_2 = "The expected format is not found!";
    public static final String VALID_VALIDATION_PROCESS_ERROR_VALUE_3 = "The result of the Basic validation process is not acceptable to continue the process!";
    public static final String VALID_VALIDATION_PROCESS_ERROR_VALUE_4 = "The expected format is not found!";
    public static final String LTV_PROCESS_NOT_ACCEPTABLE = "The result of the LTV validation process is not acceptable to continue the process!";
    public static final String VALID_VALIDATION_PROCESS_ERROR_VALUE_6 = "The expected format is not found!";
    public static final String VALID_VALIDATION_PROCESS_ERROR_VALUE_7 = "The certificate is not qualified!";
    public static final String VALID_VALIDATION_PROCESS_ERROR_VALUE_8 = "No revocation data for the certificate";
    public static final String VALID_VALIDATION_PROCESS_ERROR_VALUE_9 = "The signature is not intact!";
    public static final String VALID_VALIDATION_PROCESS_ERROR_VALUE_10 = "The current time is not in the validity range of the signer's certificate.";
    public static final String TS_PROCESS_NOT_CONCLUSIVE = "The result of the timestamps validation process is not conclusive!";
    public static final String REFERENCE_DATA_NOT_INTACT = "The reference data object is not intact!";
    public static final String CERTIFICATE_DO_NOT_MATCH_TRUST_SERVICE = "The trusted certificate doesn't match the trust service";
    public static final String PAST_SIG_VALIDATION_NOT_CONCLUSIVE = "The past signature validation is not conclusive!";
    public static final String SIG_CREATED_WITH_EXP_CERT = "Signature has been created with expired certificate";
    public static final String CERT_PATH_NOT_TRUSTED = "Unable to build a certificate chain until a trusted list!";
    public static final String REVOCATION_NOT_FRESH = "The revocation information is not considered as 'fresh'.";
    public static final String ALL_FILES_NOT_SIGNED = "Not all files are signed!";
    public static final String NOT_EXPECTED_KEY_USAGE = "The signer's certificate does not have an expected key-usage!";


    public static final String VALID_VALIDATION_PROCESS_ERROR_NAMEID_1 = "ADEST_ROBVPIIC_ANS";
    public static final String VALID_VALIDATION_PROCESS_ERROR_NAMEID_2 = "BBB_FC_IEFF_ANS";
    public static final String VALID_VALIDATION_PROCESS_ERROR_NAMEID_3 = "LTV_ABSV_ANS";
    public static final String VALID_VALIDATION_PROCESS_ERROR_NAMEID_4 = "BBB_FC_IEFF_ANS";
    public static final String VALID_VALIDATION_PROCESS_ERROR_NAMEID_5 = "ARCH_LTVV_ANS";
    public static final String VALID_VALIDATION_PROCESS_ERROR_NAMEID_6 = "BBB_XCV_CMDCIQC_ANS";
    public static final String VALID_VALIDATION_PROCESS_ERROR_NAMEID_7 = "BBB_XCV_IRDPFC_ANS";
    public static final String VALID_VALIDATION_PROCESS_ERROR_NAMEID_8 = "BBB_CV_ISI_ANS";
    public static final String VALID_VALIDATION_PROCESS_ERROR_NAMEID_9 = "ADEST_ROTVPIIC_ANS";
    public static final String VALID_VALIDATION_PROCESS_ERROR_NAMEID_10 = "BBB_CV_IRDOI_ANS";


    public static final String VALID_VALIDATION_PROCESS_STATUS_1 = "NOT_OK";
    public static final String VALID_VALIDATION_PROCESS_STATUS_2 = "OK";
    public static final String VALID_VALIDATION_PROCESS_STATUS_3 = "WARNING";
    public static final String VALID_VALIDATION_PROCESS_STATUS_4 = "IGNORED";

    public static final String VALIDATION_LEVEL_ARCHIVAL_DATA = "ARCHIVAL_DATA";

    public static final String CA_QC = "http://uri.etsi.org/TrstSvc/Svctype/CA/QC";
    public static final String OCSP_QC = "http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP/QC";
    public static final String UNDER_SUPERVISION = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision";
    public static final String TSA_QTST = "http://uri.etsi.org/TrstSvc/Svctype/TSA/QTST";
    public static final String GRANTED = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/granted";

    public static final String MOCK_XADES_SIGNATURE_FILE = "Valid_XAdES_LT_TS.xml";
    public static final String MOCK_XADES_SIGNATURE_FILE_NAME = "VALID_FILENAME+#$!}[]()-_.,;€ˇ~^'äöõü§@.xml";
    public static final String MOCK_XADES_SIGNATURE_ID = "id-3adeffc353d287db270f5fb98bbcda02";
    public static final String MOCK_XADES_SIGNATURE_SIGNER = "MÄNNIK,MARI-LIIS,47101010033";
    public static final String MOCK_XADES_SIGNATURE_CLAIMED_SIGNING_TIME = "2019-02-05T13:27:22Z";
    public static final String MOCK_XADES_SIGNATURE_BEST_SIGNATURE_TIME = "2019-02-05T13:27:24Z";
    public static final String MOCK_XADES_DATAFILE_HASH = "RnKZobNWVy8u92sDL4S2j1BUzMT5qTgt6hm90TfAGRo=";
    public static final String MOCK_XADES_DATAFILE_HASH2 = "47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=";
    public static final String MOCK_XADES_DATAFILE_HASH_ALGO = "SHA256";
    public static final String MOCK_XADES_DATAFILE_FILENAME = "test.txt";
    public static final String MOCK_XADES_DATAFILE_FILENAME2 = "Proov (2).txt";

    public static final String VALIDATION_CONCLUSION_PREFIX = "validationReport.validationConclusion.";
    public static final String VALIDATION_PROCESS_PREFIX = "validationReport.validationProcess.";
    public static final String DIAGNOSTIC_DATA_PREFIX = "validationReport.diagnosticData.";

    public static final String SOAP_VALIDATION_CONCLUSION_PREFIX = "Envelope.Body.ValidateDocumentResponse.ValidationReport.ValidationConclusion";
    public static final String SOAP_DETAILED_DATA_PREFIX = "Envelope.Body.ValidateDocumentResponse.ValidationReport.ValidationProcess";
    public static final String SOAP_DIAGNOSTIC_DATA_PREFIX = "Envelope.Body.ValidateDocumentResponse.ValidationReport.DiagnosticData";
    public static final String SOAP_ERROR_RESPONSE_PREFIX = "Envelope.Body.Fault";

}
