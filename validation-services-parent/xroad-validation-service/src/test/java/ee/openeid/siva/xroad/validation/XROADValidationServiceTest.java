package ee.openeid.siva.xroad.validation;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.xroad.configuration.XROADValidationServiceProperties;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class XROADValidationServiceTest {

    private static final String TEST_FILES_LOCATION = "test-files/";
    private static final String XROAD_SIMPLE = "xroad-simple.asice";
    private static final String XROAD_BATCHSIGNATURE = "xroad-batchsignature.asice";
    private static final String BDOC_TS_VALID = "BDOC-TS.bdoc";
    private static final String DDOC_VALID = "ddoc_valid_2_signatures.ddoc";
    private static final String PDF_VALID = "hellopades-pades-lt-sha256-sign.pdf";

    private XROADValidationServiceProperties properties;
    private XROADValidationService validationService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        properties = new XROADValidationServiceProperties();
        properties.setDefaultPath();
        validationService = new XROADValidationService();
        validationService.setProperties(properties);
        validationService.loadXroadConfigurationDirectory();
    }

    @Test
    public void ValidatingXRoadSimpleContainerShouldHaveOnlyTheCNFieldOfTheSingersCerificateAsSignedByFieldInQualifiedReport() throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(XROAD_SIMPLE);
        QualifiedReport report = validationService.validateDocument(validationDocument);
        assertEquals("Riigi Infosüsteemi Amet", report.getSignatures().get(0).getSignedBy());
    }

    @Test
    public void validationReportForXroadBatchSignatureShouldHaveCorrectSignatureForm() throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(XROAD_BATCHSIGNATURE);
        QualifiedReport report = validationService.validateDocument(validationDocument);
        assertEquals("ASiC_E_batchsignature", report.getSignatureForm());
    }

    @Test
    public void validatingInvalidBase64EncodedDocumentResultsInMalformedDocumentException() throws Exception {
        ValidationDocument doc = new ValidationDocument();
        doc.setDataBase64Encoded("ASDASDAFGOAGMRASGMASPÖGLMOP");
        expectedException.expect(MalformedDocumentException.class);
        expectedException.expectMessage("the document is malformed");
        validationService.validateDocument(doc);
    }

    @Test
    public void validatingValidBdocTSWithXroadValidatorResultsInMalformedDocumentException() throws Exception {
        ValidationDocument doc = buildValidationDocument(BDOC_TS_VALID);
        expectedException.expect(MalformedDocumentException.class);
        expectedException.expectMessage("the document is malformed");
        validationService.validateDocument(doc);
    }

    @Test
    public void validatingValidDDocWithXroadValidatorResultsInMalformedDocumentException() throws Exception {
        ValidationDocument doc = buildValidationDocument(DDOC_VALID);
        expectedException.expect(MalformedDocumentException.class);
        expectedException.expectMessage("the document is malformed");
        validationService.validateDocument(doc);
    }

    @Test
    public void validatingValidPdfWithXroadValidatorResultsInMalformedDocumentException() throws Exception {
        ValidationDocument doc = buildValidationDocument(PDF_VALID);
        expectedException.expect(MalformedDocumentException.class);
        expectedException.expectMessage("the document is malformed");
        validationService.validateDocument(doc);
    }

    private ValidationDocument buildValidationDocument(String testFile) throws Exception {
        String fileLocation = TEST_FILES_LOCATION + testFile;
        ValidationDocument document = DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(fileLocation)
                .withName(testFile)
                .build();
        document.setDataBase64Encoded(Base64.encodeBase64String(IOUtils.toByteArray(getFileStream(fileLocation))));
        return document;
    }

    private InputStream getFileStream(String filename) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(filename);
    }
}
