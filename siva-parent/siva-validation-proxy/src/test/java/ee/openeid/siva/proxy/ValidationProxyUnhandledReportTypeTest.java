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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidationProxyUnhandledReportTypeTest {

    @InjectMocks
    private ContainerValidationProxy validationProxy;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ValidationService validationService;

    @Test
    @Disabled("SIVA-413")
    public void unhandledReportTypeThrowsIllegalArgumentException() {
        ReportType[] reportTypeRealValues = ReportType.values();

        try (MockedStatic<ReportType> reportType = mockStatic(ReportType.class)) {
            ReportType mockedReportType = mock(ReportType.class);
            when(mockedReportType.name()).thenReturn("NEW_UNHANDLED_ENUM_VALUE");
            when(mockedReportType.ordinal()).thenReturn(reportTypeRealValues.length);

            List<ReportType> reportTypeValues = Arrays.stream(reportTypeRealValues).collect(Collectors.toList());
            reportTypeValues.add(mockedReportType);
            reportType.when(ReportType::values).thenReturn(reportTypeValues.toArray(new ReportType[reportTypeValues.size()]));

            when(applicationContext.getBean(anyString())).thenReturn(validationService);
            ProxyDocument proxyDocument = new ProxyDocument();
            proxyDocument.setReportType(mockedReportType);
            proxyDocument.setName("TEST_FILE_NAME.bdoc");
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> validationProxy.validateRequest(proxyDocument))
                    .withMessage("Failed to determine report type - report of type 'NEW_UNHANDLED_ENUM_VALUE' is unhandled");
        }
    }
}
