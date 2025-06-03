/*
 * Copyright 2019 - 2025 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.validation.ZipMimetypeValidator;
import ee.openeid.siva.validation.document.report.DetailedReport;
import ee.openeid.siva.validation.document.report.DiagnosticReport;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.service.ValidationService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationProxyReportTypeHandlingTest {

    @InjectMocks
    private ContainerValidationProxy validationProxy;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ValidationService validationService;

    @Mock
    private ZipMimetypeValidator zipMimetypeValidator;

    @Mock
    private SimpleReport simpleReport;

    @Mock
    private DetailedReport detailedReport;

    @Mock
    private DiagnosticReport diagnosticReport;

    @ParameterizedTest
    @EnumSource(ReportType.class)
    void validateRequest_WhenReportTypeIsValid_NoExceptionsThrown(ReportType reportType) {
        when(applicationContext.getBean(anyString())).thenReturn(validationService);
        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setReportType(reportType);
        proxyDocument.setName("TEST_FILE_NAME.bdoc");
        Reports reports = new Reports(simpleReport, detailedReport, diagnosticReport);
        when(validationService.validateDocument(any())).thenReturn(reports);

        SimpleReport result = validationProxy.validateRequest(proxyDocument);

        assertThat(result, anyOf(
                sameInstance(simpleReport),
                sameInstance((SimpleReport) detailedReport),
                sameInstance((SimpleReport) diagnosticReport)
        ));
        verify(zipMimetypeValidator).validateZipContainerMimetype(proxyDocument);
        verifyNoMoreInteractions(validationService, applicationContext, zipMimetypeValidator);
        verifyNoInteractions(simpleReport, detailedReport, diagnosticReport);
    }
}
