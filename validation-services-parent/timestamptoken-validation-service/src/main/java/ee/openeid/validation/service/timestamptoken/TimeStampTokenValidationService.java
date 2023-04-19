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

package ee.openeid.validation.service.timestamptoken;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.exception.DocumentRequirementsException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.validation.service.timestamptoken.validator.report.TimeStampTokenValidationReportBuilder;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.spi.DSSUtils;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Selector;
import org.digidoc4j.utils.ZipEntryInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;
import static eu.europa.esig.dss.asic.common.ASiCUtils.META_INF_FOLDER;
import static eu.europa.esig.dss.asic.common.ASiCUtils.MIME_TYPE;

@Service
public class TimeStampTokenValidationService implements ValidationService {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String TIMESTAMP_FILE = "TIMESTAMP.TST";
    private static final String SIGNATURE_FILE_EXTENSION_P7S = "SIGNATURE.P7S";
    private static final String EVIDENCE_RECORD_FILE_EXTENSION_ERS = "EVIDENCERECORD.ERS";
    private static final String EVIDENCE_RECORD_FILE_EXTENSION_XML = "EVIDENCERECORD.XML";
    private static final String SIGNATURE_FILE_EXTENSION_XML = "SIGNATURES.XML";
    private SignaturePolicyService<ValidationPolicy> signaturePolicyService;
    private ReportConfigurationProperties reportConfigurationProperties;


    @Override
    public Reports validateDocument(ValidationDocument validationDocument) {

        List<InMemoryDocument> documents = getFilesFromContainer(validationDocument);
        validateContainer(documents);
        TimeStampToken timeStampToken = getTimeStamp(documents);
        List<Error> errors = validateTimeStamp(documents, timeStampToken);

        TimeStampTokenValidationReportBuilder reportBuilder = new TimeStampTokenValidationReportBuilder(validationDocument, timeStampToken, signaturePolicyService.getPolicy(validationDocument.getSignaturePolicy()), errors, reportConfigurationProperties.isReportSignatureEnabled());
        return reportBuilder.build();
    }

    private void validateContainer(List<InMemoryDocument> documents) {
        documents.removeIf(d -> d.getName().equals(META_INF_FOLDER));
        long dataFileCount = documents.stream()
                .filter(d -> !d.getName().startsWith(META_INF_FOLDER))
                .filter(d -> !d.getName().endsWith(MIME_TYPE)).count();

        long timeStampCount = documents.stream()
                .filter(d -> d.getName().startsWith(META_INF_FOLDER))
                .filter(d -> d.getName().toUpperCase().endsWith(TIMESTAMP_FILE)).count();

        long signatureFileCount = documents.stream()
                .filter(d -> d.getName().startsWith(META_INF_FOLDER))
                .filter(d -> getFileFromFullPath(d.getName().toUpperCase()).equals(SIGNATURE_FILE_EXTENSION_P7S)
                        || getFileFromFullPath(d.getName().toUpperCase()).equals(SIGNATURE_FILE_EXTENSION_XML)
                        || getFileFromFullPath(d.getName().toUpperCase()).equals(EVIDENCE_RECORD_FILE_EXTENSION_ERS)
                        || getFileFromFullPath(d.getName().toUpperCase()).equals(EVIDENCE_RECORD_FILE_EXTENSION_XML)).count();

        if (dataFileCount != 1 || timeStampCount != 1 || signatureFileCount > 0) {
            throw new DocumentRequirementsException();
        }
    }

    private String getFileFromFullPath(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    private TimeStampToken getTimeStamp(List<InMemoryDocument> documents) {

        try {
            CMSSignedData cms = new CMSSignedData(documents.stream()
                    .filter(d -> d.getName().toUpperCase().endsWith(TIMESTAMP_FILE)).findAny().orElseThrow(IllegalArgumentException::new).getBytes());
            return new TimeStampToken(cms);
        } catch (CMSException | TSPException | IOException e) {
            throw new MalformedDocumentException(e);
        }
    }

    private List<Error> validateTimeStamp(List<InMemoryDocument> documents, TimeStampToken timeStampToken) {
        List<Error> errors = new ArrayList<>();
        boolean isSignatureValid = isSignatureValid(timeStampToken);
        if (!isSignatureValid) {
            errors.add(mapError("Signature not intact"));
        }
        byte[] dataFile = documents.stream()
                .filter(d -> !d.getName().startsWith(META_INF_FOLDER))
                .filter(d -> !d.getName().endsWith(MIME_TYPE)).findAny().orElseThrow(IllegalArgumentException::new).getBytes();
        boolean isMessageImprintsValid = isMessageImprintsValid(dataFile, timeStampToken);
        if (isSignatureValid && !isMessageImprintsValid) {
            errors.add(mapError("Signature not intact"));
        }
        boolean isVersionValid = isVersionValid(timeStampToken);
        if (!isVersionValid) {
            errors.add(mapError("TST version not supported"));
        }
        return errors;

    }

    private boolean isSignatureValid(TimeStampToken timeStampToken) {
        try {
            JcaSimpleSignerInfoVerifierBuilder sigVerifierBuilder = new JcaSimpleSignerInfoVerifierBuilder();
            Collection certCollection = timeStampToken.getCertificates().getMatches(timeStampToken.getSID());
            Iterator certIt = certCollection.iterator();
            X509CertificateHolder cert = (X509CertificateHolder) certIt.next();
            Certificate x509Cert = CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(cert.getEncoded()));

            SignerInformationVerifier signerInfoVerifier = sigVerifierBuilder.setProvider(BouncyCastleProvider.PROVIDER_NAME).build(x509Cert.getPublicKey());
            return timeStampToken.isSignatureValid(signerInfoVerifier);
        } catch (Exception e) {
            throw new MalformedDocumentException(e);
        }
    }

    private boolean isVersionValid(TimeStampToken timeStampToken) {
        return timeStampToken.getTimeStampInfo().toASN1Structure().getVersion().getValue().longValue() == 1;
    }

    private Error mapError(String content) {
        Error error = new Error();
        error.setContent(emptyWhenNull(content));
        return error;
    }

    private boolean isMessageImprintsValid(byte[] dataFile, TimeStampToken timeStampToken) {
        final byte[] digestValue = DSSUtils.digest(DigestAlgorithm.SHA256, dataFile);
        return Arrays.equals(timeStampToken.getTimeStampInfo().getMessageImprintDigest(), digestValue);
    }

    private List<InMemoryDocument> getFilesFromContainer(ValidationDocument validationDocument) {
        List<InMemoryDocument> documents = new ArrayList<>();
        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(validationDocument.getBytes()))) {

            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                try (ZipEntryInputStream zipEntryInputStream = new ZipEntryInputStream(zipStream, null)) {
                    documents.add(new InMemoryDocument(zipEntryInputStream, entry.getName()));
                }
            }
        } catch (IOException e) {
            throw new MalformedDocumentException(e);
        }
        return documents;
    }

    @Autowired
    @Qualifier("timestampPolicyService")
    public void setSignaturePolicyService(SignaturePolicyService<ValidationPolicy> signaturePolicyService) {
        this.signaturePolicyService = signaturePolicyService;
    }

    @Autowired
    public void setReportConfigurationProperties(ReportConfigurationProperties reportConfigurationProperties) {
        this.reportConfigurationProperties = reportConfigurationProperties;
    }

}
