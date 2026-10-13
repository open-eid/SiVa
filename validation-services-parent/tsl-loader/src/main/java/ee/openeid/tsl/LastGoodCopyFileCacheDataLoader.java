/*
 * Copyright 2017 - 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.tsl;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import lombok.extern.slf4j.Slf4j;

// Falls back to the copy on disk when a refresh fails. DSS's own loader throws once a cached file has expired
// and the download fails, so a fresh JVM (the snapshot fetcher, a restarted pod) would lose that trusted list
// although its last copy is right there; the validation job judges the copy's validity itself (next-update,
// signature), so a stale copy is never trusted longer than the list allows.
@Slf4j
public class LastGoodCopyFileCacheDataLoader extends FileCacheDataLoader {

    private static final long serialVersionUID = 1L;

    @Override
    public DSSDocument getDocument(String url, boolean refresh) {
        try {
            return super.getDocument(url, refresh);
        } catch (DSSException e) {
            DSSDocument lastGoodCopy = getDocumentFromCache(url);
            if (lastGoodCopy == null) {
                throw e;
            }
            String reason = e.getMessage();
            log.warn("Download of '{}' failed ({}); using the last copy on disk", url, reason);
            return lastGoodCopy;
        }
    }
}
