package ee.openeid.siva.common;

public class DssMessages {
    public static final DssMessage BSV_IFCRC = new DssMessage("BSV_IFCRC", "Is the result of the 'Format Checking' building block conclusive?");
    public static final DssMessage BSV_IISCRC = new DssMessage("BSV_IISCRC", "Is the result of the 'Identification of Signing Certificate' building block conclusive?");
    public static final DssMessage BSV_IVCIRC = new DssMessage("BSV_IVCIRC", "Is the result of the 'Validation Context Initialization' building block conclusive?");
    public static final DssMessage BSV_IXCVRC = new DssMessage("BSV_IXCVRC", "Is the result of the 'X.509 Certificate Validation' building block conclusive?");
    public static final DssMessage BSV_ISCRAVTC = new DssMessage("BSV_ISCRAVTC", "Is the signing certificate not revoked at validation time?");
    public static final DssMessage BSV_IVTAVRSC = new DssMessage("BSV_IVTAVRSC", "Is the validation time in the validity range of the signing certificate?");
    public static final DssMessage BSV_ICVRC = new DssMessage("BSV_ICVRC", "Is the result of the 'Cryptographic Verification' building block conclusive?");
    public static final DssMessage BSV_ISAVRC = new DssMessage("BSV_ISAVRC", "Is the result of the 'Signature Acceptance Validation' building block conclusive?");

    public static final DssMessage BBB_XCV_CCCBB = new DssMessage("BBB_XCV_CCCBB", "Can the certificate chain be built till a trust anchor?");
    public static final DssMessage BBB_XCV_SUB = new DssMessage("BBB_XCV_SUB", "Is the certificate validation conclusive?");
    public static final DssMessage BBB_XCV_SUB_ANS = new DssMessage("BBB_XCV_SUB_ANS", "The certificate validation is not conclusive!");
    public static final DssMessage BBB_XCV_ICTIVRSC_ANS = new DssMessage("BBB_XCV_ICTIVRSC_ANS", "The current time is not in the validity range of the signer's certificate!");
    public static final DssMessage BBB_CV_ISI_ANS = new DssMessage("BBB_CV_ISI_ANS", "The signature is not intact!");
    public static final DssMessage BBB_CV_TSP_IRDOI_ANS = new DssMessage("BBB_CV_TSP_IRDOI_ANS", "The time-stamp message imprint is not intact!");
    public static final DssMessage BBB_CV_TSP_IRDOF = new DssMessage("BBB_CV_TSP_IRDOF", "Has the message imprint data been found?");
    public static final DssMessage BBB_CV_TSP_IRDOI = new DssMessage("BBB_CV_TSP_IRDOI", "Is the message imprint data intact?");
    public static final DssMessage BBB_CV_ISIT = new DssMessage("BBB_CV_ISIT", "Is time-stamp's signature intact?");
    public static final DssMessage BBB_CV_ISIR = new DssMessage("BBB_CV_ISIR", "Is revocation's signature intact?");
    public static final DssMessage BBB_ICS_ISCI = new DssMessage("BBB_ICS_ISCI", "Is there an identified candidate for the signing certificate?");
    public static final DssMessage BBB_CV_IRDOF = new DssMessage("BBB_CV_IRDOF", "Has the reference data object been found?");
    public static final DssMessage BBB_SAV_ISSV = new DssMessage("BBB_SAV_ISSV", "Is the structure of the signature valid?");
    public static final DssMessage BBB_SAV_ISQPSTP = new DssMessage("BBB_SAV_ISQPSTP", "Is the signed qualifying property: 'signing-time' present?");
    public static final DssMessage BBB_SAV_ISQPMDOSPP = new DssMessage("BBB_SAV_ISQPMDOSPP", "Is the signed qualifying property: 'message-digest' or 'SignedProperties' present?");
    public static final DssMessage BBB_SAV_DMICTSTMCMI = new DssMessage("BBB_SAV_DMICTSTMCMI", "Does the message-imprint match the computed value?");
    public static final DssMessage BBB_SAV_ISVA = new DssMessage("BBB_SAV_ISVA", "Is the signature acceptable?");
    public static final DssMessage BBB_XCV_IRDPFC = new DssMessage("BBB_XCV_IRDPFC", "Is the revocation data present for the certificate?");
    public static final DssMessage BBB_XCV_RAC = new DssMessage("BBB_XCV_RAC", "Is the revocation acceptance check conclusive?");
    public static final DssMessage BBB_XCV_IARDPFC = new DssMessage("BBB_XCV_IARDPFC", "Is an acceptable revocation data present for the certificate?");

    public static final DssMessage LTV_ABSV = new DssMessage("LTV_ABSV", "Is the result of the Basic Validation Process acceptable?");
    public static final DssMessage LTV_ABSV_ANS = new DssMessage("LTV_ABSV_ANS", "The result of the Basic validation process is not acceptable to continue the process!");

    public static final DssMessage ACCM = new DssMessage("ACCM", "Are cryptographic constraints met for the {0}?");

    public static final DssMessage ADEST_RORPIIC = new DssMessage("ADEST_RORPIIC", "Is the result of the revocation data basic validation process acceptable?");
    public static final DssMessage ADEST_IBSVPTC = new DssMessage("ADEST_IBSVPTC", "Is the result of basic time-stamp validation process conclusive?");
    public static final DssMessage ADEST_ISTPTDABST = new DssMessage("ADEST_ISTPTDABST", "Is the signing-time plus the time-stamp delay after best-signature-time?");

    public static final DssMessage ARCH_LTVV = new DssMessage("ARCH_LTVV", "Is the result of the LTV validation process acceptable?");

    public static final DssMessage TSV_ASTPTCT = new DssMessage("TSV_ASTPTCT", "Are the time-stamps in the right order?");
    public static final DssMessage TSV_IBSTAIDOSC = new DssMessage("TSV_IBSTAIDOSC", "Is the best-signature-time not before the issuance date of the signing certificate?");

}
