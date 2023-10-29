/*
 * Copyright 2022 - 2023 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.validation.security;

import eu.europa.esig.dss.asic.common.ZipContainerHandler;
import eu.europa.esig.dss.model.DSSDocument;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

/**
 * A thin wrapper for {@link ZipContainerHandler} that requests a new instance of a container handler
 * from a factory to delegate work to.
 *
 * This class can be used for delegating work to {@link eu.europa.esig.dss.asic.common.SecureContainerHandler}
 * In a thread-safe manner. <b>In DSS version 5.9, {@link eu.europa.esig.dss.asic.common.SecureContainerHandler}
 * is not thread-safe!</b>
 *
 * TODO: Remove this when SecureContainerHandler becomes thread-safe.
 */
@RequiredArgsConstructor
public class ThreadSafeSecureContainerHandler implements ZipContainerHandler {

    @NonNull
    private final Supplier<ZipContainerHandler> handlerFactory;

    @Override
    public List<DSSDocument> extractContainerContent(DSSDocument zipArchive) {
        return handlerFactory.get().extractContainerContent(zipArchive);
    }

    @Override
    public List<String> extractEntryNames(DSSDocument zipArchive) {
        return handlerFactory.get().extractEntryNames(zipArchive);
    }

    @Override
    public DSSDocument createZipArchive(List<DSSDocument> containerEntries, Date creationTime, String zipComment) {
        return handlerFactory.get().createZipArchive(containerEntries, creationTime, zipComment);
    }

}
