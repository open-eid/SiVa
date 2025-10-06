/*
 * Copyright 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timestamptoken;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import eu.europa.esig.dss.asic.cades.validation.ASiCContainerWithCAdESValidator;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TimeStampTokenValidationServiceValidationTimeTest extends BaseTimeStampTokenValidationServiceTest {

    @Test
    void validateDocument_ValidationTimeProvided_ValidationTimeSetForValidator() {
        ValidationDocument validationDocument = buildValidationDocument("timestamptoken-ddoc.asics");
        Date validationTime = new Date();
        validationDocument.setValidationTime(validationTime);
        ASiCContainerWithCAdESValidator validatorMock = Mockito.mock(ASiCContainerWithCAdESValidator.class);

        createServiceFake(validatorMock).validateDocument(validationDocument);

        verify(validatorMock).setValidationTime(validationTime);
    }

    @Test
    void validateDocument_ValidationTimeNotProvided_ValidationTimeNotSetForValidator() {
        ValidationDocument validationDocument = buildValidationDocument("timestamptoken-ddoc.asics");
        ASiCContainerWithCAdESValidator validatorMock = Mockito.mock(ASiCContainerWithCAdESValidator.class);

        createServiceFake(validatorMock).validateDocument(validationDocument);

        verify(validatorMock, never()).setValidationTime(any(Date.class));
    }

    private TimeStampTokenValidationServiceFake createServiceFake(ASiCContainerWithCAdESValidator validatorMock) {
        TimeStampTokenValidationServiceFake validationServiceFake = new TimeStampTokenValidationServiceFake();
        when(validatorMock.validateDocument(any(InputStream.class))).thenReturn(
                new eu.europa.esig.dss.validation.reports.Reports(new XmlDiagnosticData(), null, new XmlSimpleReport(), null)
        );
        validationServiceFake.setValidator(validatorMock);

        validationServiceFake.setSignaturePolicyService(new ConstraintLoadingSignaturePolicyService(policyProperties));
        validationServiceFake.setReportConfigurationProperties(new ReportConfigurationProperties(true));

        return validationServiceFake;
    }

    private static class TimeStampTokenValidationServiceFake extends TimeStampTokenValidationService {

        @Setter
        private ASiCContainerWithCAdESValidator validator;

        @Override
        protected ASiCContainerWithCAdESValidator createValidatorFromDocument(final ValidationDocument validationDocument) {
            return validator;
        }
    }
}
