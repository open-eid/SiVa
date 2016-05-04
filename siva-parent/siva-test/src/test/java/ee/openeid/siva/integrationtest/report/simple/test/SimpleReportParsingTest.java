package ee.openeid.siva.integrationtest.report.simple.test;


import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.integrationtest.report.simple.Signature;
import ee.openeid.siva.integrationtest.report.simple.SimpleReport;
import ee.openeid.siva.integrationtest.report.simple.SimpleReportWrapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleReportParsingTest {

    private static final String SIMPLE_REPORT_2_SIGNATURES = "{\"SimpleReport\":{\"Policy\":{\"PolicyDescription\":\"Validate electronic signatures and indicates whether they are Advanced electronic Signatures (AdES), AdES supported by a\\n\\t\\tQualified Certificate (AdES/QC) or a Qualified electronic\\n\\t\\tSignature (QES). All certificates and their related chains supporting the signatures are validated against the EU Member State\\n\\t\\tTrusted Lists (this includes signer's certificate and certificates used\\n\\t\\tto validate certificate validity status services - CRLs, OCSP, and time-stamps).\",\"PolicyName\":\"QES AdESQC TL based\"},\"ValidSignaturesCount\":0,\"Signature\":[{\"SignatureFormat\":\"PAdES_BASELINE_LTA\",\"SignedBy\":\"Veiko Sinivee\",\"Indication\":\"INVALID\",\"SignatureScopes\":{\"SignatureScope\":{\"scope\":\"PdfByteRangeSignatureScope\",\"name\":\"PDF previous version #3\",\"content\":\"The document byte range: [0, 14153, 52047, 491]\"}},\"SigningTime\":\"2015-10-01T10:16:21Z\",\"Error\":{\"NameId\":\"BBB_XCV_CMDCIQC_ANS\",\"content\":\"The certificate is not qualified!\"},\"Id\":\"id-45e849080fdfa4206273bae20513d457\",\"SubIndication\":\"CHAIN_CONSTRAINTS_FAILURE\",\"SignatureLevel\":\"AdES\"},{\"SignatureFormat\":\"PAdES_BASELINE_LTA\",\"SignedBy\":\"Veiko Sinivee\",\"Indication\":\"INVALID\",\"SignatureScopes\":{\"SignatureScope\":{\"scope\":\"PdfByteRangeSignatureScope\",\"name\":\"PDF previous version #1\",\"content\":\"The document byte range: [0, 93034, 130928, 492]\"}},\"SigningTime\":\"2015-10-01T10:18:43Z\",\"Error\":{\"NameId\":\"BBB_XCV_CMDCIQC_ANS\",\"content\":\"The certificate is not qualified!\"},\"Id\":\"id-2f2ace7a24e12cc1aa9987fd96c1e8cd\",\"SubIndication\":\"CHAIN_CONSTRAINTS_FAILURE\",\"SignatureLevel\":\"AdES\"}],\"ValidationTime\":\"2016-05-02T13:37:53Z\",\"SignaturesCount\":2,\"DocumentName\":\"WSDocument\"}}";
    private static final String SIMPLE_REPORT_1_SIGNATURE = "{\"SimpleReport\":{\"Policy\":{\"PolicyDescription\":\"Validate electronic signatures and indicates whether they are Advanced electronic Signatures (AdES), AdES supported by a\\n\\t\\tQualified Certificate (AdES/QC) or a Qualified electronic\\n\\t\\tSignature (QES). All certificates and their related chains supporting the signatures are validated against the EU Member State\\n\\t\\tTrusted Lists (this includes signer's certificate and certificates used\\n\\t\\tto validate certificate validity status services - CRLs, OCSP, and time-stamps).\",\"PolicyName\":\"QES AdESQC TL based\"},\"ValidSignaturesCount\":0,\"Signature\":{\"SignatureFormat\":\"PAdES_BASELINE_B\",\"SignedBy\":\"SINIVEE,VEIKO,36706020210\",\"Indication\":\"INVALID\",\"SignatureScopes\":{\"SignatureScope\":{\"scope\":\"FullSignatureScope\",\"name\":\"Full PDF\",\"content\":\"Full document\"}},\"SigningTime\":\"2015-07-09T06:15:51Z\",\"Error\":{\"NameId\":\"BBB_VCI_ISFC_ANS_1\",\"ExpectedValue\":\"[PAdES_BASELINE_LT, PAdES_BASELINE_LTA]\",\"content\":\"The signature format is not allowed by the validation policy constraint!\"},\"Id\":\"id-472859c4c635b0bdac6dbc25be155b4a\",\"SignatureLevel\":\"QES\"},\"ValidationTime\":\"2016-05-02T17:55:23Z\",\"SignaturesCount\":1,\"DocumentName\":\"WSDocument\"}}";

    @Test
    public void parse2SignatureReport() throws IOException {
        SimpleReport report = new ObjectMapper().readValue(SIMPLE_REPORT_2_SIGNATURES, SimpleReportWrapper.class).getSimpleReport();

        assertTrue(2 == report.getSignaturesCount());
        assertTrue(0 == report.getValidSignaturesCount());

        for (Signature signature : report.getSignatures()) {
            assertEquals("PAdES_BASELINE_LTA", signature.getSignatureFormat());
            assertEquals("Veiko Sinivee", signature.getSignedBy());
        }
    }

    @Test
    public void parse1SignatureReport() throws IOException {
        SimpleReport report = new ObjectMapper().readValue(SIMPLE_REPORT_1_SIGNATURE, SimpleReportWrapper.class).getSimpleReport();

        assertTrue(1 == report.getSignaturesCount());
        assertTrue(0 == report.getValidSignaturesCount());

        Signature signature = report.getSignatures().get(0);

        assertEquals("PAdES_BASELINE_B", signature.getSignatureFormat());
        assertEquals("SINIVEE,VEIKO,36706020210", signature.getSignedBy());
    }

}
