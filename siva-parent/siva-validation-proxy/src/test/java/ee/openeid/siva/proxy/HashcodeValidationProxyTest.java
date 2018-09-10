package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.Datafile;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.exception.ValidatonServiceNotFoundException;
import ee.openeid.siva.proxy.http.RESTProxyService;
import ee.openeid.siva.statistics.StatisticsService;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.Info;
import ee.openeid.siva.validation.document.report.Policy;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.ValidatedDocument;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.service.ValidationService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class HashcodeValidationProxyTest {
    private static final String DEFAULT_DOCUMENT_NAME = "document.xml";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private HashcodeValidationProxy hashcodeValidationProxy;

    private ApplicationContext applicationContext;

    private ValidationServiceSpy validationServiceSpy;

    @Before
    public void setUp() {
        hashcodeValidationProxy = new HashcodeValidationProxy();

        applicationContext = mock(ApplicationContext.class);
        hashcodeValidationProxy.setApplicationContext(applicationContext);

        RESTProxyService restProxyService = mock(RESTProxyService.class);
        hashcodeValidationProxy.setRestProxyService(restProxyService);

        StatisticsService statisticsService = mock(StatisticsService.class);
        hashcodeValidationProxy.setStatisticsService(statisticsService);

        validationServiceSpy = new ValidationServiceSpy();
    }

    @Test
    public void applicationContextHasNoBeanWithGivenNameThrowsException() {
        BDDMockito.given(applicationContext.getBean(anyString())).willThrow(new NoSuchBeanDefinitionException("Bean not loaded"));

        exception.expect(ValidatonServiceNotFoundException.class);
        exception.expectMessage("hashcodeGenericValidationService not found");

        ProxyDocument proxyDocument = mockProxyDocument();
        hashcodeValidationProxy.validate(proxyDocument);

        verify(applicationContext).getBean(anyString());
    }

    @Test
    public void proxyDocumentShouldReturnValidationReport() {
        when(applicationContext.getBean("hashcodeGenericValidationService")).thenReturn(validationServiceSpy);

        ProxyDocument proxyDocument = mockProxyDocument();
        SimpleReport report = hashcodeValidationProxy.validate(proxyDocument);
        assertEquals(validationServiceSpy.reports.getSimpleReport(), report);
    }

    private ProxyDocument mockProxyDocument() {
        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setName(DEFAULT_DOCUMENT_NAME);
        proxyDocument.setDatafiles(createDatafiles(createDatafile("test", "test-hash-1", "SHA256")));
        return proxyDocument;
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

    private class ValidationServiceSpy implements ValidationService {

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
            return new Reports(simpleReport, null);
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
