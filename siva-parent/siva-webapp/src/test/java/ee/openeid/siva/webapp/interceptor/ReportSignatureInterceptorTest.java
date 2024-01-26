/*
 * Copyright 2017 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.webapp.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.signature.SignatureService;
import ee.openeid.siva.validation.document.report.DetailedReport;
import ee.openeid.siva.validation.document.report.ValidatedDocument;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.webapp.response.ValidationResponse;
import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportSignatureInterceptorTest {

    private ReportSignatureInterceptor reportSignatureInterceptor;

    @Mock
    private SignatureService signatureService;

    @BeforeEach
    public void setUp() throws IOException {
        when(signatureService.getSignature(any(byte[].class), anyString(), anyString())).thenReturn(getRawSignatureMock());

        reportSignatureInterceptor = new ReportSignatureInterceptor();
        reportSignatureInterceptor.setSignatureService(signatureService);
        reportSignatureInterceptor.setJacksonObjectMapper(new ObjectMapper());
        ReportConfigurationProperties properties = new ReportConfigurationProperties(true);
        reportSignatureInterceptor.setProperties(properties);
    }

    @Test
    void test() throws IOException {
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
        validatedDocument.setFileHash("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08");
        validationConclusion.setValidatedDocument(validatedDocument);
        detailedReport.setValidationConclusion(validationConclusion);
        validationResponse.setValidationReport(detailedReport);
        return validationResponse;
    }

    private byte[] getRawSignatureMock() {
        return "test".getBytes();
    }

} 
