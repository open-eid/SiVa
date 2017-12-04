/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.validation.service.bdoc.configuration;

import ee.openeid.tsl.CustomCertificatesLoader;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.digidoc4j.TSLCertificateSource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TSLUtilsTest {

    private TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();

    @Before
    public void setUp() {
        CustomCertificatesLoader customCertificatesLoader = new CustomCertificatesLoader();
        customCertificatesLoader.setTrustedListsCertificateSource(trustedListsCertificateSource);
        customCertificatesLoader.init();
    }

    @Test
    public void allCertificatesShouldBeCopiedFromTrustedListSourceToTSLSource() throws Exception {
        TSLCertificateSource tslCertificateSource = TSLUtils.createTSLFromTrustedCertSource(trustedListsCertificateSource);
        assertEquals(tslCertificateSource.getCertificates(), trustedListsCertificateSource.getCertificates());
    }

}
