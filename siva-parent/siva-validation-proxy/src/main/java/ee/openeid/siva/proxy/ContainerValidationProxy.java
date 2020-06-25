package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.http.RESTProxyService;
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
import eu.europa.esig.dss.model.InMemoryDocument;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.utils.ZipEntryInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ContainerValidationProxy extends ValidationProxy {

    private static final String GENERIC_SERVICE = "generic";
    private static final String ASICS_EXTENSION = "ASICS";
    private static final String SCS_FILE_TYPE = "SCS";
    private static final String ZIP_FILE_TYPE = "ZIP";
    private static final String TIMESTAMP_EXTENSION = ".TST";
    private static final String TIMEMARK_CONTAINER_SERVICE = "timemarkContainer";
    private static final String TIMESTAMP_TOKEN_SERVICE = "timeStampToken";
    private static final String MIME_TYPE_FILE_NAME = "mimetype";
    private static final String ASICS_MIME_TYPE = "application/vnd.etsi.asic-s+zip";
    private static final String META_INF_FOLDER = "META-INF/";
    private static final String DOCUMENT_FORMAT_NOT_RECOGNIZED = "Document format not recognized/handled";

    private RESTProxyService restProxyService;

    @Override
    public SimpleReport validateRequest(ProxyRequest proxyRequest) {
        Reports reports;
        SimpleReport report;
        if (isDocumentTypeXRoad(proxyRequest)) {
            reports = restProxyService.validate(createValidationDocument(proxyRequest));
            report = chooseReport(reports, proxyRequest.getReportType());
        } else {
            ValidationService validationService = getServiceForType(proxyRequest);
            reports = validate(validationService, proxyRequest);
            report = chooseReport(reports, proxyRequest.getReportType());
            if (validationService instanceof TimeStampTokenValidationService && TimeStampTokenValidationData.Indication.TOTAL_PASSED == report.getValidationConclusion().getTimeStampTokens().get(0).getIndication()) {
                report = generateDataFileReport(proxyRequest, report);
            }
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
        if (DocumentType.DDOC.name().equals(extension) || DocumentType.BDOC.name().equals(extension)) {
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
                if (!entry.getName().startsWith(META_INF_FOLDER) && !entry.getName().equalsIgnoreCase(MIME_TYPE_FILE_NAME)) {
                    return new InMemoryDocument(zipStream, entry.getName());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("Invalid document");
    }

    private String decideAsicsValidatorService(byte[] document, String extension) {
        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(document))) {
            ZipEntry entry;
            boolean isAsicsMimeType = false;
            boolean isTimeStampExtension = false;
            while ((entry = zipStream.getNextEntry()) != null) {
                try (ZipEntryInputStream zipEntryInputStream = new ZipEntryInputStream(zipStream)) {
                    if (isAsicsMimeType(entry, zipEntryInputStream)) {
                        isAsicsMimeType = true;
                    } else if (entry.getName().toUpperCase().endsWith(TIMESTAMP_EXTENSION)) {
                        isTimeStampExtension = true;
                    }
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

        } catch (IOException e) {
            throw new RuntimeException(e);
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

    private boolean isAsicsMimeType(ZipEntry entry, ZipEntryInputStream zipStream) {
        return entry.getName().equals(MIME_TYPE_FILE_NAME) && ASICS_MIME_TYPE.equals(new String(new InMemoryDocument(zipStream, entry.getName()).getBytes()));
    }

    @Autowired
    public void setRestProxyService(RESTProxyService restProxyService) {
        this.restProxyService = restProxyService;
    }
}
