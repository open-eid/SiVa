package ee.openeid.siva.validation.service.bdoc.report;

import ee.openeid.siva.validation.service.bdoc.report.digidoc4j.DigiDoc4JReportModel;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class DigiDoc4JReportModelTest {

    private static final String VALID_REPORT_2_SIGNATURES = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ValidationReport><SignatureValidation ID=\"S0\"><Policy><PolicyName>QES AdESQC TL based</PolicyName><PolicyDescription>RIA customized validation policy</PolicyDescription></Policy><ValidationTime>2016-05-04T14:18:52Z</ValidationTime><DocumentName>META-INF/signatures0.xml</DocumentName><Signature Id=\"S0\" SignatureFormat=\"XAdES_BASELINE_LT\"><SigningTime>2016-05-04T08:43:55Z</SigningTime><SignedBy>JUHANSON,ALLAN,38608014910</SignedBy><Indication>VALID</Indication><SignatureLevel>QES</SignatureLevel><SignatureScopes><SignatureScope name=\"chrome-signing.log\" scope=\"FullSignatureScope\">Full document</SignatureScope></SignatureScopes><AdditionalValidation/></Signature><ValidSignaturesCount>1</ValidSignaturesCount><SignaturesCount>1</SignaturesCount></SignatureValidation><SignatureValidation ID=\"S1\"><Policy><PolicyName>QES AdESQC TL based</PolicyName><PolicyDescription>RIA customized validation policy</PolicyDescription></Policy><ValidationTime>2016-05-04T14:18:52Z</ValidationTime><DocumentName>META-INF/signatures1.xml</DocumentName><Signature Id=\"S1\" SignatureFormat=\"XAdES_BASELINE_LT\"><SigningTime>2016-05-04T11:14:23Z</SigningTime><SignedBy>VOLL,ANDRES,39004170346</SignedBy><Indication>VALID</Indication><SignatureLevel>QES</SignatureLevel><SignatureScopes><SignatureScope name=\"chrome-signing.log\" scope=\"FullSignatureScope\">Full document</SignatureScope></SignatureScopes><AdditionalValidation/></Signature><ValidSignaturesCount>1</ValidSignaturesCount><SignaturesCount>1</SignaturesCount></SignatureValidation><ManifestValidation/></ValidationReport>";

    @Test
    public void digiDoc4JReportIsGeneratedFromXML() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(DigiDoc4JReportModel.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        DigiDoc4JReportModel digiDoc4JReportModel = (DigiDoc4JReportModel) unmarshaller.unmarshal(new StringReader(VALID_REPORT_2_SIGNATURES));

        System.out.println(digiDoc4JReportModel);
    }
}
