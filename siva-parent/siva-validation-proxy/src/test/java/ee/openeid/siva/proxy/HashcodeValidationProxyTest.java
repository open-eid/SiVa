/*
 * Copyright 2019 - 2023 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.proxy;


import ee.openeid.siva.proxy.document.ProxyHashcodeDataSet;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.exception.ValidatonServiceNotFoundException;
import ee.openeid.siva.statistics.StatisticsService;
import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.SignatureFile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.validation.service.generic.HashcodeGenericValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class HashcodeValidationProxyTest {

    private HashcodeValidationProxy hashcodeValidationProxy;

    @Mock
    private StatisticsService statisticsService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Environment environment;

    private ValidationServiceSpy validationServiceSpy;

    @BeforeEach
    public void setUp() {
        hashcodeValidationProxy = new HashcodeValidationProxy(statisticsService, applicationContext, environment);

        validationServiceSpy = new ValidationServiceSpy();
    }

    @Test
    void applicationContextHasNoBeanWithGivenNameThrowsException() {
        BDDMockito.given(applicationContext.getBean(anyString())).willThrow(new NoSuchBeanDefinitionException("Bean not loaded"));
        ProxyHashcodeDataSet proxyDocument = mockHashCodeDataSet();

        ValidatonServiceNotFoundException caughtException = assertThrows(
                ValidatonServiceNotFoundException.class, () -> {
                    hashcodeValidationProxy.validate(proxyDocument);
                }
        );
        assertEquals("hashcodeGenericValidationService not found", caughtException.getMessage());
        verify(applicationContext).getBean(anyString());
    }

    @Test
    void proxyDocumentShouldReturnValidationReport() {
        when(applicationContext.getBean("hashcodeGenericValidationService")).thenReturn(validationServiceSpy);

        ProxyHashcodeDataSet proxyDocument = mockHashCodeDataSet();
        SimpleReport report = hashcodeValidationProxy.validate(proxyDocument);
        assertEquals(validationServiceSpy.reports.getSimpleReport(), report);
    }

    @Test
    void hashcodeValidationAlwaysReturnsSimpleReport() {
        when(applicationContext.getBean("hashcodeGenericValidationService")).thenReturn(validationServiceSpy);
        ProxyHashcodeDataSet proxyDocument = mockHashCodeDataSet();

        for (ReportType reportType : ReportType.values()) {
            proxyDocument.setReportType(reportType);
            SimpleReport report = hashcodeValidationProxy.validate(proxyDocument);
            assertTrue(report instanceof SimpleReport);
            assertFalse(report instanceof DetailedReport);
            assertFalse(report instanceof DiagnosticReport);
        }
    }

    private ProxyHashcodeDataSet mockHashCodeDataSet() {
        ProxyHashcodeDataSet proxyHashcodeDataSet = new ProxyHashcodeDataSet();
        SignatureFile signatureFile = new SignatureFile();
        signatureFile.setSignature("hash".getBytes());
        signatureFile.setDatafiles(createDatafiles(createDatafile("test", "test-hash-1", "SHA256")));
        proxyHashcodeDataSet.setSignatureFiles(Collections.singletonList(signatureFile));

        return proxyHashcodeDataSet;
    }

    private List<Datafile> createDatafiles(Datafile... datafiles) {
        return Arrays.asList(datafiles);
    }

    private Datafile createDatafile(String filename, String hash, String hashAlgo) {
        Datafile datafile = new Datafile();
        datafile.setHash(hash);
        datafile.setFilename(filename);
        datafile.setHashAlgo(hashAlgo);
        return datafile;
    }

    private class ValidationServiceSpy extends HashcodeGenericValidationService {

        Reports reports;

        @Override
        public Reports validateDocument(ValidationDocument validationDocument) {
            reports = createDummyReports();
            return reports;
        }

        private Reports createDummyReports() {
            ValidationConclusion validationConclusion = new ValidationConclusion();
            validationConclusion.setValidSignaturesCount(0);
            validationConclusion.setSignaturesCount(1);
            validationConclusion.setValidationTime("ValidationTime");
            validationConclusion.setValidatedDocument(createDummyValidatedDocument());
            validationConclusion.setPolicy(createDummyPolicy());
            validationConclusion.setSignatures(createDummySignatures());
            SimpleReport simpleReport = new SimpleReport(validationConclusion);
            return new Reports(simpleReport, null, null);
        }

        private ValidatedDocument createDummyValidatedDocument() {
            ValidatedDocument validatedDocument = new ValidatedDocument();
            validatedDocument.setFilename("DocumentName");
            return validatedDocument;
        }

        private List<SignatureValidationData> createDummySignatures() {
            SignatureValidationData signature = new SignatureValidationData();
            signature.setSignatureLevel("SignatureLevel");
            signature.setClaimedSigningTime("ClaimedSigningTime");
            signature.setInfo(createDummySignatureInfo());
            signature.setSignatureFormat("SingatureFormat");
            signature.setId("id1");
            signature.setSignedBy("Some Name 123456789");
            signature.setIndication(SignatureValidationData.Indication.TOTAL_FAILED);
            signature.setWarnings(Collections.emptyList());
            signature.setErrors(createDummyErrors());
            return Collections.singletonList(signature);
        }

        private List<Error> createDummyErrors() {
            Error error = new Error();
            error.setContent("ErrorContent");
            return Collections.singletonList(error);
        }

        private Info createDummySignatureInfo() {
            Info info = new Info();
            info.setBestSignatureTime("BestSignatureTime");
            return info;
        }

        private Policy createDummyPolicy() {
            Policy policy = new Policy();
            policy.setPolicyDescription("PolicyDescription");
            policy.setPolicyName("PolicyName");
            policy.setPolicyUrl("http://policyUrl.com");
            return policy;
        }
    }

}
