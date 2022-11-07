/*
 * Copyright 2021 - 2022 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.validator.container;

import ee.openeid.validation.service.generic.validator.report.DssSimpleReportWrapper;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.jaxb.XmlContainerInfo;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.validation.reports.Reports;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;

import static ee.openeid.validation.service.generic.validator.report.DssSimpleReportWrapper.createXmlMessage;

/**
 * An implementation of {@link ZipBasedContainerValidator.EntryValidator} for validating that datafiles in an ASiC
 * container are not empty.
 *
 * The validator requires an instance of {@link Reports} to work, where it extracts the list of datafiles names
 * ({@link Reports#getDiagnosticData()}, {@link DiagnosticData#getContainerInfo()}, {@link XmlContainerInfo#getContentFiles()})
 * to validate.
 * Each time {@link #validate(ZipEntry, InputStream)} is called, the entry is checked against the list of previously
 * obtained datafiles names.
 * In case the entry is a datafile, at least one byte is tried to read from its input stream. In case the entry is
 * empty (no bytes to read from its stream), a warning is added to each signature present in the instance of
 * {@link SimpleReport} obtained from the original {@link Reports} ({@link Reports#getSimpleReport()}).
 */
public class AsicContainerDataFileSizeValidator implements ZipBasedContainerValidator.EntryValidator {

    private final SimpleReport simpleReport;
    private final List<String> signatureIds;
    private final List<String> dataFiles;

    /**
     * Constructs an instance of {@link AsicContainerDataFileSizeValidator}.
     *
     * @param validationReports where to obtain the list of datafiles and the {@link SimpleReport} from
     */
    public AsicContainerDataFileSizeValidator(@NonNull Reports validationReports) {
        simpleReport = validationReports.getSimpleReport();
        signatureIds = Optional
                .ofNullable(simpleReport)
                .map(SimpleReport::getSignatureIdList)
                .orElseGet(Collections::emptyList);
        dataFiles = Optional
                .ofNullable(validationReports.getDiagnosticData())
                .map(DiagnosticData::getContainerInfo)
                .map(XmlContainerInfo::getContentFiles)
                .orElseGet(Collections::emptyList);
    }

    /**
     * In case the name of the specified entry is in the list of the names of datafiles of the container to validate,
     * tries to read at least one byte from the input stream of the specified entry. If the entry is empty, adds a warning
     * to each signature in the {@link SimpleReport} obtained from the original {@link Reports}.
     *
     * @param entry an instance of {@link ZipEntry} that represents the container entry to validate
     * @param entryInputStream an instance of {@link InputStream} where this entry's bytes can be read from
     * @throws IOException if reading this entry's input stream fails
     */
    @Override
    public void validate(@NonNull ZipEntry entry, @NonNull InputStream entryInputStream) throws IOException {
        if (simpleReport != null && dataFiles.contains(entry.getName()) && isEmpty(entryInputStream)) {
            final DssSimpleReportWrapper dssSimpleReportWrapper = new DssSimpleReportWrapper(simpleReport);
            final String emptyDataFileWarning = String.format("Data file '%s' is empty", entry.getName());

            signatureIds.forEach(signatureId -> Optional
                    .ofNullable(dssSimpleReportWrapper.getSignatureAdESValidationXmlDetails(signatureId))
                    .ifPresent(details -> details.getWarning().add(createXmlMessage(emptyDataFileWarning)))
            );
        }
    }

    private static boolean isEmpty(InputStream inputStream) throws IOException {
        // In case the stream returns -1 when trying to read the first byte of the entry,
        //  then the size of this entry is less than 1 byte, and thus the entry is empty.
        return (inputStream.read() < 0);
    }

}
