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

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.exception.ContainerMimetypeFileException;
import ee.openeid.siva.proxy.validation.ZipMimetypeValidator;
import ee.openeid.siva.statistics.StatisticsService;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.ValidationWarning;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.validation.service.timemark.report.DDOCContainerValidationReportBuilder;
import ee.openeid.validation.service.timestamptoken.TimeStampTokenValidationService;
import eu.europa.esig.dss.asic.common.ZipUtils;
import eu.europa.esig.dss.exception.IllegalInputException;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static ee.openeid.siva.validation.constant.AsicContainerConstants.ASICS_MIME_TYPE;
import static eu.europa.esig.dss.asic.common.ASiCUtils.META_INF_FOLDER;
import static eu.europa.esig.dss.asic.common.ASiCUtils.MIME_TYPE;

@Service
public class ContainerValidationProxy extends ValidationProxy {

    private static final String GENERIC_SERVICE = "generic";
    private static final String ASICS_EXTENSION = "ASICS";
    private static final String SCS_FILE_TYPE = "SCS";
    private static final String ZIP_FILE_TYPE = "ZIP";
    private static final String BDOC_FILE_TYPE = "BDOC";
    private static final String DDOC_FILE_TYPE = "DDOC";
    private static final String TIMESTAMP_EXTENSION = ".TST";
    private static final String TIMEMARK_CONTAINER_SERVICE = "timemarkContainer";
    private static final String TIMESTAMP_TOKEN_SERVICE = "timeStampToken";
    private static final String DOCUMENT_FORMAT_NOT_RECOGNIZED = "Document format not recognized/handled";

    private final ZipMimetypeValidator zipMimetypeValidator;

    @Autowired
    public ContainerValidationProxy(StatisticsService statisticsService,
                                    ApplicationContext applicationContext,
                                    Environment environment,
                                    ZipMimetypeValidator zipMimetypeValidator) {
        super(statisticsService, applicationContext, environment);
        this.zipMimetypeValidator = zipMimetypeValidator;
    }

    @Override
    public SimpleReport validateRequest(ProxyRequest proxyRequest) {
        Reports reports;
        SimpleReport report;

        ValidationService validationService = getServiceForType(proxyRequest);
        reports = validate(validationService, proxyRequest);
        report = chooseReport(reports, proxyRequest.getReportType());
        if (validationService instanceof TimeStampTokenValidationService
                && report.getValidationConclusion().getTimeStampTokens().stream()
                .allMatch(token -> token.getIndication() == TimeStampTokenValidationData.Indication.TOTAL_PASSED)) {
            report = generateDataFileReport(proxyRequest, report);
        }
        try {
            zipMimetypeValidator.validateZipContainerMimetype((ProxyDocument) proxyRequest);
        } catch (ContainerMimetypeFileException e) {
            addWarningToReport(report, e.getMessage());
        }

        return report;
    }

    SimpleReport generateDataFileReport(ProxyRequest proxyRequest, SimpleReport report) {
        ProxyDocument proxyDocument = (ProxyDocument) proxyRequest;
        ProxyDocument dataFileProxyDocument = generateDataFileProxyDocument(proxyDocument);
        ValidationService dataFileValidationService = getServiceForType(dataFileProxyDocument);
        SimpleReport dataFileReport = null;
        try {
            dataFileReport = chooseReport(dataFileValidationService.validateDocument(createValidationDocument(dataFileProxyDocument)), proxyDocument.getReportType());
            removeUnnecessaryWarning(dataFileReport.getValidationConclusion());
        } catch (MalformedDocumentException e) {
            if (e.getCause() == null || !DOCUMENT_FORMAT_NOT_RECOGNIZED.equalsIgnoreCase(e.getCause().getMessage())) {
                throw e;
            }
        }
        return mergeReports(report, dataFileReport);
    }

    @Override
    String constructValidatorName(ProxyRequest proxyRequest) {
        ProxyDocument proxyDocument = (ProxyDocument) proxyRequest;
        String filename = proxyDocument.getName();
        String extension = FilenameUtils.getExtension(filename).toUpperCase();
        if (!StringUtils.isNotBlank(extension)) {
            throw new IllegalArgumentException("Invalid file format:" + filename);
        }
        if (DDOC_FILE_TYPE.equals(extension) || BDOC_FILE_TYPE.equals(extension)) {
            return TIMEMARK_CONTAINER_SERVICE + SERVICE_BEAN_NAME_POSTFIX;
        } else if (extension.equals(ASICS_EXTENSION) || extension.equals(SCS_FILE_TYPE) || extension.equals(ZIP_FILE_TYPE)) {
            return decideAsicsValidatorService(proxyDocument.getBytes(), extension);
        }
        return GENERIC_SERVICE + SERVICE_BEAN_NAME_POSTFIX;
    }

    ValidationDocument createValidationDocument(ProxyRequest proxyRequest) {
        ValidationDocument validationDocument = new ValidationDocument();
        ProxyDocument proxyDocument = (ProxyDocument) proxyRequest;
        validationDocument.setName(proxyDocument.getName());
        validationDocument.setBytes(proxyDocument.getBytes());
        validationDocument.setSignaturePolicy(proxyDocument.getSignaturePolicy());
        return validationDocument;
    }

    private SimpleReport mergeReports(SimpleReport timeStampTokenReport, SimpleReport dataFileReport) {
        if (dataFileReport != null) {
            dataFileReport.getValidationConclusion().setTimeStampTokens(timeStampTokenReport.getValidationConclusion().getTimeStampTokens());
            dataFileReport.getValidationConclusion().setSignatureForm(timeStampTokenReport.getValidationConclusion().getSignatureForm());
            dataFileReport.getValidationConclusion().setValidatedDocument(timeStampTokenReport.getValidationConclusion().getValidatedDocument());
            return dataFileReport;
        }
        return timeStampTokenReport;
    }

    private ProxyDocument generateDataFileProxyDocument(ProxyDocument proxyDocument) {
        ProxyDocument dataFileDocument = new ProxyDocument();
        InMemoryDocument inMemoryDocument = getDataFile(proxyDocument.getBytes());
        dataFileDocument.setName(inMemoryDocument.getName());
        dataFileDocument.setSignaturePolicy(proxyDocument.getSignaturePolicy());
        dataFileDocument.setBytes(inMemoryDocument.getBytes());
        return dataFileDocument;
    }

    private Reports validate(ValidationService validationService, ProxyRequest proxyRequest) {
        return validationService.validateDocument(createValidationDocument(proxyRequest));
    }

    private InMemoryDocument getDataFile(byte[] proxyDocument) {
        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(proxyDocument))) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (!entry.getName().startsWith(META_INF_FOLDER) && !entry.getName().equalsIgnoreCase(MIME_TYPE)) {
                    return new InMemoryDocument(zipStream, entry.getName());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("Invalid document");
    }

    private String decideAsicsValidatorService(byte[] document, String extension) {
        DSSDocument dssDocument = new InMemoryDocument(document);
        List<DSSDocument> containerContent;
        try {
            containerContent = ZipUtils.getInstance().extractContainerContent(dssDocument);
        } catch (DSSException | IllegalInputException e) {
            throw new MalformedDocumentException(e);
        }

        boolean isAsicsMimeType = false;
        boolean isTimeStampExtension = false;

        for (DSSDocument containerEntry : containerContent) {
            if (isAsicsMimeType(containerEntry)) {
                isAsicsMimeType = true;
            }
            if (isTimeStamp(containerEntry)) {
                isTimeStampExtension = true;
            }
        }

        if (extension.equals(ZIP_FILE_TYPE)) {
            if (isAsicsMimeType && isTimeStampExtension) {
                return TIMESTAMP_TOKEN_SERVICE + SERVICE_BEAN_NAME_POSTFIX;
            }
        } else {
            if (isTimeStampExtension) {
                return TIMESTAMP_TOKEN_SERVICE + SERVICE_BEAN_NAME_POSTFIX;
            }
        }

        return GENERIC_SERVICE + SERVICE_BEAN_NAME_POSTFIX;
    }

    void removeUnnecessaryWarning(ValidationConclusion validationConclusion) {
        List<ValidationWarning> warnings = validationConclusion.getValidationWarnings();
        if (warnings == null || warnings.isEmpty())
            return;
        List<ValidationWarning> newList = new ArrayList<>(warnings);
        newList.removeIf(s -> DDOCContainerValidationReportBuilder.DDOC_TIMESTAMP_WARNING.equals(s.getContent()));
        validationConclusion.setValidationWarnings(newList);
    }

    private static void addWarningToReport(SimpleReport report, String message) {
        List<ValidationWarning> warnings = report.getValidationConclusion().getValidationWarnings();
        List<ValidationWarning> newList = new ArrayList<>(warnings);
        ValidationWarning validationWarning = new ValidationWarning();
        validationWarning.setContent(message);
        newList.add(validationWarning);
        report.getValidationConclusion().setValidationWarnings(newList);
    }

    private static boolean isAsicsMimeType(DSSDocument entry) {
        if (!entry.getName().equals(MIME_TYPE)) {
            return false;
        }
        try (InputStream inputStream = entry.openStream()) {
            byte[] expectedBytes = ASICS_MIME_TYPE.getBytes(StandardCharsets.US_ASCII);
            byte[] readBuffer = new byte[expectedBytes.length];

            return inputStream.read(readBuffer) == expectedBytes.length
                    && Arrays.equals(readBuffer, expectedBytes)
                    && inputStream.read() < 0;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read entry", e);
        }
    }

    private static boolean isTimeStamp(DSSDocument entry) {
        return entry.getName().toUpperCase().endsWith(TIMESTAMP_EXTENSION);
    }

}
