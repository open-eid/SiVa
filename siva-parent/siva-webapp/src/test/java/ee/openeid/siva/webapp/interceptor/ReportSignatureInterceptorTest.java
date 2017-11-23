package ee.openeid.siva.webapp.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.signature.SignatureService;
import ee.openeid.siva.validation.document.report.DetailedReport;
import ee.openeid.siva.validation.document.report.ValidatedDocument;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.webapp.response.ValidationResponse;
import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportSignatureInterceptorTest {

    private ReportSignatureInterceptor reportSignatureInterceptor;

    @Mock
    private SignatureService signatureService;

    @Before
    public void setUp() throws IOException {
        when(signatureService.getSignature(any(byte[].class), anyString(), anyString())).thenReturn(getRawSignatureMock());

        reportSignatureInterceptor = new ReportSignatureInterceptor();
        reportSignatureInterceptor.setSignatureService(signatureService);
        reportSignatureInterceptor.setJacksonObjectMapper(new ObjectMapper());
        ReportConfigurationProperties properties = new ReportConfigurationProperties(true);
        reportSignatureInterceptor.setProperties(properties);
    }

    @Test
    public void test() throws IOException {
        ValidationResponse validationResponse = (ValidationResponse) reportSignatureInterceptor.beforeBodyWrite(getValidationResponseMock(), null, null, null, null,null);
        assertArrayEquals(getRawSignatureMock(), Base64.decodeBase64(validationResponse.getValidationReportSignature()));
    }

    private ValidationResponse getValidationResponseMock() {
        ValidationResponse validationResponse = new ValidationResponse();
        DetailedReport detailedReport = new DetailedReport();
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setValidSignaturesCount(0);
        validationConclusion.setSignaturesCount(0);
        validationConclusion.setValidationTime(new Date().toString());
        ValidatedDocument validatedDocument = new ValidatedDocument();
        validatedDocument.setFilename("test.txt");
        validatedDocument.setHashAlgo("SHA256");
        validatedDocument.setFileHashInHex("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08");
        validationConclusion.setValidatedDocument(validatedDocument);
        detailedReport.setValidationConclusion(validationConclusion);
        validationResponse.setValidationReport(detailedReport);
        return validationResponse;
    }

    private byte[] getRawSignatureMock() {
        return "test".getBytes();
    }

} 
