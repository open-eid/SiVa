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

package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.CertificateType;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.helper.TestLog;
import ee.openeid.validation.service.generic.validator.ocsp.LoggingOSCPSourceWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;

import static ee.openeid.siva.validation.helper.matcher.CommonMatchers.base64String;
import static ee.openeid.siva.validation.helper.matcher.CommonMatchers.stringAsInstantInRangeOfNotBeforeAndNowWithClockSkew;
import static ee.openeid.siva.validation.helper.matcher.IsCertificate.isCertificateWith;
import static ee.openeid.siva.validation.helper.matcher.IsCertificate.isCertificateWithoutIssuerNorType;
import static ee.openeid.siva.validation.helper.matcher.IsWarning.warning;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

@ActiveProfiles("test")
@TestPropertySource(properties = {
        "t-level-signature-filter.filter-type=ALLOWED_COUNTRIES",
        "t-level-signature-filter.countries=EE"
})
public class HashcodeGenericValidationServiceTLevelTest extends HashcodeGenericValidationServiceTestBase {

    @Test
    void validateDocument_WhenSignatureLevelIsT_FreshOcspIsTaken() {
        TestLog testLog = new TestLog(LoggingOSCPSourceWrapper.class);
        ValidationDocument validationDocument = buildValidationDocument("xades-t-level-signature.xml", buildDefaultDatafile());
        Instant notBefore = Instant.now();

        Reports reports = validationService.validateDocument(validationDocument);

        assertThat(reports.getSimpleReport().getValidationConclusion(), notNullValue());
        ValidationConclusion validationConclusion = reports.getSimpleReport().getValidationConclusion();
        assertThat(validationConclusion.getSignatureForm(), nullValue());
        assertThat(validationConclusion.getSignaturesCount(), equalTo(1));
        assertThat(validationConclusion.getValidSignaturesCount(), equalTo(1));
        assertThat(validationConclusion.getSignatures(), hasSize(1));
        SignatureValidationData signature = validationConclusion.getSignatures().get(0);
        assertThat(signature.getSignatureFormat(), equalTo("XAdES_BASELINE_T"));
        assertThat(signature.getIndication(), equalTo("TOTAL-PASSED"));
        assertThat(signature.getSubIndication(), emptyString());
        assertThat(signature.getErrors(), empty());
        assertThat(signature.getWarnings(), contains(
                warning(containsString("OCSP response not 'fresh'"))
        ));
        assertThat(signature.getInfo(), notNullValue());
        assertThat(signature.getInfo().getOcspResponseCreationTime(), stringAsInstantInRangeOfNotBeforeAndNowWithClockSkew(notBefore));
        assertThat(signature.getCertificates(), contains(
                isCertificateWith(
                        equalTo("DEMO SK TIMESTAMPING UNIT 2025E"),
                        base64String(),
                        isCertificateWithoutIssuerNorType(
                                equalTo("TEST of SK TSA CA 2023E"),
                                base64String()
                        ),
                        sameInstance(CertificateType.SIGNATURE_TIMESTAMP)
                ),
                isCertificateWith(
                        equalTo("JÕEORG,JAAK-KRISTJAN,38001085718"),
                        base64String(),
                        isCertificateWithoutIssuerNorType(
                                equalTo("TEST of ESTEID2018"),
                                base64String()
                        ),
                        sameInstance(CertificateType.SIGNING)
                ),
                isCertificateWith(
                        equalTo("DEMO of ESTEID-SK 2018 AIA OCSP RESPONDER 2018"),
                        base64String(),
                        isCertificateWithoutIssuerNorType(
                                equalTo("TEST of ESTEID2018"),
                                base64String()
                        ),
                        sameInstance(CertificateType.REVOCATION)
                )
        ));
        testLog.verifyLogInOrder(allOf(
                containsString("Performed OCSP request"),
                containsString("http://aia.demo.sk.ee/esteid2018")
        ));
    }

    private static Datafile buildDefaultDatafile() {
        Datafile datafile = new Datafile();
        datafile.setFilename("test.txt");
        datafile.setHashAlgo("SHA512");
        datafile.setHash("sd8ha1sF45ZcRpSSdEpd4MlF4LEDxC6x5XR2++2PHUifXK6beS2zfF2CO8DGx9BrBWF21qvlzgdu6trtQU4Xow==");
        return datafile;
    }

}
