/*
 * Copyright 2019 Riigi Infosüsteemide Amet
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
import ee.openeid.siva.validation.service.ValidationService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class ValidationProxyUnhandledReportTypeTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @InjectMocks
    private ContainerValidationProxy validationProxy;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ValidationService validationService;

    @Test
    @PrepareForTest(ReportType.class)
    public void unhandledReportTypeThrowsIllegalArgumentException() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Failed to determine report type - report of type 'NEW_UNHANDLED_ENUM_VALUE' is unhandled");

        ReportType[] reportTypeRealValues = ReportType.values();

        ReportType mockedReportType = PowerMockito.mock(ReportType.class);
        Whitebox.setInternalState(mockedReportType, "name", "NEW_UNHANDLED_ENUM_VALUE");
        Whitebox.setInternalState(mockedReportType, "ordinal", ReportType.values().length);
        PowerMockito.mockStatic(ReportType.class);

        List<ReportType> reportTypeValues = Arrays.stream(reportTypeRealValues).collect(Collectors.toList());
        reportTypeValues.add(mockedReportType);
        PowerMockito.when(ReportType.values()).thenReturn(reportTypeValues.toArray(new ReportType[reportTypeValues.size()]));

        when(applicationContext.getBean(anyString())).thenReturn(validationService);
        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setReportType(mockedReportType);
        proxyDocument.setName("TEST_FILE_NAME.bdoc");
        validationProxy.validateRequest(proxyDocument);
    }
}
