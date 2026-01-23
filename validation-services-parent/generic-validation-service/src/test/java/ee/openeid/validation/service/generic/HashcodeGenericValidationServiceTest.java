/*
 * Copyright 2019 - 2026 Riigi Infosüsteemi Amet
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
import ee.openeid.siva.validation.document.report.Scope;
import ee.openeid.siva.validation.document.report.SignatureProductionPlace;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SignerRole;
import ee.openeid.siva.validation.util.CertUtil;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class HashcodeGenericValidationServiceTest extends HashcodeGenericValidationServiceTestBase {

    @Test
    void validHashcodeRequest() {
        Reports response = validationService.validate(getValidationDocumentSingletonList());
        Scope signatureScope = response.getSimpleReport().getValidationConclusion().getSignatures().get(0).getSignatureScopes().get(0);
        assertEquals("LvhnsrgBZBK9kTQ8asbPtcsjuEhBo9s3QDdCcIxlMmo=", signatureScope.getHash());
        assertEquals("SHA256", signatureScope.getHashAlgo());
        assertEquals("test.pdf", signatureScope.getName());
        assertEquals((Integer) 1, response.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        assertEquals(1L, response.getSimpleReport().getValidationConclusion().getSignatures().size());
    }

    @Test
    void validMultipleSignatures() {
        List<ValidationDocument> validationDocuments = getValidationDocumentSingletonList();
        validationDocuments.addAll(getValidationDocumentSingletonList());
        Reports response = validationService.validate(validationDocuments);
        assertEquals((Integer) 2, response.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        assertEquals((Integer) 2, response.getSimpleReport().getValidationConclusion().getSignaturesCount());
        assertEquals(2L, response.getSimpleReport().getValidationConclusion().getSignatures().size());
    }

    @Test
    void validDataFromSignatureFile() {
        List<ValidationDocument> validationDocuments = getValidationDocumentSingletonList();
        validationDocuments.get(0).setDatafiles(null);
        Reports response = validationService.validate(validationDocuments);
        Scope signatureScope = response.getSimpleReport().getValidationConclusion().getSignatures().get(0).getSignatureScopes().get(0);
        assertEquals("LvhnsrgBZBK9kTQ8asbPtcsjuEhBo9s3QDdCcIxlMmo=", signatureScope.getHash());
        assertEquals("SHA256", signatureScope.getHashAlgo());
        assertEquals("test.pdf", signatureScope.getName());
    }

    @Test
    void hashcodeValidationCertificateCorrectlyPresent() throws Exception {
        Reports response = validationService.validate(getValidationDocumentSingletonList());
        SignatureValidationData signatureValidationData = response.getSimpleReport().getValidationConclusion().getSignatures().get(0);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        assertEquals(3, signatureValidationData.getCertificates().size());

        ee.openeid.siva.validation.document.report.Certificate signerCertificate = signatureValidationData.getCertificatesByType(CertificateType.SIGNING).get(0);
        Certificate signerX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(signerCertificate.getContent().getBytes())));
        assertEquals("PAULIUS PODOLSKIS", CertUtil.getCommonName((X509Certificate) signerX509Certificate));
        assertEquals("PAULIUS PODOLSKIS", signerCertificate.getCommonName());

        ee.openeid.siva.validation.document.report.Certificate timestampCertificate = signatureValidationData.getCertificatesByType((CertificateType.SIGNATURE_TIMESTAMP)).get(0);
        Certificate timestampX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(timestampCertificate.getContent().getBytes())));
        assertEquals("SK TIMESTAMPING AUTHORITY", CertUtil.getCommonName((X509Certificate) timestampX509Certificate));
        assertEquals("SK TIMESTAMPING AUTHORITY", timestampCertificate.getCommonName());


        ee.openeid.siva.validation.document.report.Certificate revocationCertificate = signatureValidationData.getCertificatesByType((CertificateType.REVOCATION)).get(0);
        Certificate revocationX509Certificate = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(revocationCertificate.getContent().getBytes())));
        assertEquals("VI Registru Centras OCSP (IssuingCA-A)", CertUtil.getCommonName((X509Certificate) revocationX509Certificate));
        assertEquals("VI Registru Centras OCSP (IssuingCA-A)", revocationCertificate.getCommonName());

    }

    @Test
    void hashcodeValidationSubjectDNCorrectlyPresent() {
        Reports reports = validationService.validate(getValidationDocumentSingletonList());

        assertSame(1, reports.getSimpleReport().getValidationConclusion().getSignatures().size());
        SignatureValidationData signature = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0);
        assertNotNull(signature.getSubjectDistinguishedName());
        assertEquals("38605170596", signature.getSubjectDistinguishedName().getSerialNumber());
        assertEquals("PAULIUS PODOLSKIS", signature.getSubjectDistinguishedName().getCommonName());
    }

    @Test
    void populatesSignerRole() {
        Reports reports = validationService.validate(getValidationDocumentSingletonList());
        List<SignerRole> signerRole = reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getSignerRole();
        assertEquals(1, signerRole.size());
        assertEquals("Direktorius", signerRole.get(0).getClaimedRole());
    }

    @Test
    void populatesSignatureProductionPlace() {
        Reports reports = validationService.validate(getValidationDocumentSingletonList("signatures_with_sig_production_place.xml"));
        SignatureProductionPlace signatureProductionPlace = reports.getSimpleReport().getValidationConclusion()
                .getSignatures().get(0).getInfo().getSignatureProductionPlace();

        assertEquals("Tallinn", signatureProductionPlace.getCity());
        assertEquals("Harjumaa", signatureProductionPlace.getStateOrProvince());
        assertEquals("12345", signatureProductionPlace.getPostalCode());
        assertEquals("Eesti", signatureProductionPlace.getCountryName());
    }

    @Test
    void populatesSignatureMethod() {
        Reports reports = validationService.validate(getValidationDocumentSingletonList("signatures_with_sig_production_place.xml"));
        assertEquals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256",
                reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getSignatureMethod());
    }

    @Test
    void populatesTimeAssertionMessageImprint() {
        Reports reports = validationService.validate(getValidationDocumentSingletonList());
        assertEquals("MDEwDQYJYIZIAWUDBAIBBQAEIBf8So+lfR/lrfzu5i+SZwguJGakhr/W+eHwrAQJ0acJ",
                reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getInfo().getTimeAssertionMessageImprint());
    }

    private static List<ValidationDocument> getValidationDocumentSingletonList() {
        return getValidationDocumentSingletonList("signatures.xml");
    }

    private static List<ValidationDocument> getValidationDocumentSingletonList(String signatureTestFile) {
        List<ValidationDocument> validationDocuments = new ArrayList<>();
        validationDocuments.add(buildValidationDocument(signatureTestFile, getDataFile()));
        return validationDocuments;
    }

    private static Datafile getDataFile() {
        Datafile datafile = new Datafile();
        datafile.setFilename("test.pdf");
        datafile.setHash("LvhnsrgBZBK9kTQ8asbPtcsjuEhBo9s3QDdCcIxlMmo=");
        datafile.setHashAlgo("SHA256");
        return datafile;
    }
}
